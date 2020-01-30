(ns ote.services.pre-notices.attachments
  (:require [amazonica.aws.s3 :as s3]
            [taoensso.timbre :as log]
            [clojure.java.io :as io]
            [org.httpkit.client :as htclient]
            [ring.util.io :as ring-io]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [compojure.core :refer [routes POST GET]]
            [clj-time.core :as t]
            [specql.op :as op]
            [specql.core :as specql]
            [ote.authorization :as authorization]
            [ote.time :as time]
            [ote.util.file :as file]
            [ote.db.transit :as transit]
            [ote.db.modification :as modification]
            [ote.db.transport-service :as t-service]
            [ote.components.http :as http]
            [ote.nap.users :as users]
            [ote.services.admin :as admin]
            [ote.services.external :as external]
            [ote.services.transit-changes :as transit-changes]
            [ote.tasks.gtfs :as gtfs-tasks]
            [clojure.string :as str])
  (:import (java.nio.file Files)
           (java.net URLConnection)))

(def pre-notice-allowed-mime-types #{"application/pdf" "image/jpeg" "image/png"})
(def service-allowed-mime-types #{"csv"})

(defn- contentTypeFromFilename
  "This is not reliable but it is used in csv case. probeContentType and guessContentTypeFromName fn
  could not get content-type for csv files. So this is our own design and it relies only on file name."
  [filename]
  (second (clojure.string/split filename #"\.")))

(defn- generate-file-key [id filename]
  (str id "_" filename))

(defn validate-file-type [{:keys [tempfile filename]} allowed-mime-types]
  (let [path (.toPath tempfile)
        ;; In Mac os x mime types are not handled very well in java 1.8. So we try to probeContentType and if
        ;; it fails then we try to guessContentTypeFromName
        content-mime (or (Files/probeContentType path)
                         (URLConnection/guessContentTypeFromName filename)
                         (contentTypeFromFilename filename))]

    (when-not (allowed-mime-types content-mime)
      (throw (ex-info "Invalid file type" {:file-type content-mime})))))

(defn- laundry-convert-file->file! "java.io/file -> java.io/file | nil" [laundry-url input-file fn-suffix conv-name config]
  {:pre [(some? input-file)]}
  ;; conv-name is used for the subpath of converter service, eg "checksum/sha256" or "pdf/pdf2pdfa"
  (try
    (let [temp-file (java.io.File/createTempFile "laundry-tmp" fn-suffix)
          request-opts
          {:as :byte-array
           :basic-auth [(:laundry-apikey config) (:laundry-password config)]
           :multipart [{:name "file"
                        :content input-file
                        :filename (str "input" fn-suffix)
                        :mime-type "application/octet-stream"}]}
          resp (deref (htclient/post (str laundry-url conv-name) request-opts))
          _ (log/info "laundry - url " (pr-str str laundry-url conv-name))
          _ (log/info "laundry - response " (pr-str resp))]
      (if (= 200 (:status resp))
        (do (io/copy (:body resp) temp-file)
            (log/info "laundry conversion returning tempfile")
            temp-file)
        (do
          (log/error "Conversion failed, file name was: " (when input-file (.getName input-file)))
          (log/error "Error response from laundry service: " (pr-str resp))
          (.delete temp-file)
          nil)))
    (catch Exception ex
      (log/error "Error reading HTTP response from laundry: " ex)
      nil)))

(defn fn-suffix [fn]
  (str "." (last (clojure.string/split fn #"\."))))

(defn replace-suffix [fn old new]
  (let [new-without-dot (clojure.string/replace new "." "")
        old-without-dot (clojure.string/replace old "." "")
        dotted-parts (clojure.string/split fn #"\.")]
    (if (= old-without-dot (last dotted-parts))
      (clojure.string/join "." (conj (pop dotted-parts) new-without-dot))
      ;; else return changed
      fn)))

(defn upload-attachment [db {bucket :bucket :as config} {user :user :as req}]
  (try
    (let [uploaded-file (get-in req [:multipart-params "file"])
          _ (assert (and (:filename uploaded-file)
                         (:tempfile uploaded-file))
                    "No uploaded file")
          _ (validate-file-type uploaded-file pre-notice-allowed-mime-types)
          conversions {".pdf" "pdf/pdf2pdfa"
                       ;; ".docx" "docx/docx2pdf"
                       ".png" "image/png2png"
                       ".jpg" "image/jpeg2jpeg"
                       ".jpeg" "image/jpeg2jpeg"}
          orig-filename (:filename uploaded-file)
          converted-filename orig-filename                  ;; we don't convert between formats in our case so keep filename
          orig-suffix (fn-suffix orig-filename)
          laundry-url (:laundry-url config)
          converted-file (when laundry-url
                           (laundry-convert-file->file! laundry-url (:tempfile uploaded-file) orig-suffix (get conversions orig-suffix ".dat") config))
          ;; we save both the original and converted files but return only one id to the client (converted if successful, original otherwise)
          file (specql/insert! db ::transit/pre-notice-attachment
                               (modification/with-modification-timestamp-and-user
                                 {::transit/attachment-file-name orig-filename}
                                 ::transit/id user))
          file2 (when converted-file
                  (specql/insert! db ::transit/pre-notice-attachment
                                  (modification/with-modification-timestamp-and-user
                                    {::transit/attachment-file-name converted-filename}
                                    ::transit/id user)))]

      (s3/put-object bucket (generate-file-key (::transit/id file) orig-filename)
                     (:tempfile uploaded-file))
      (if (and converted-filename converted-file file2 (> (.length converted-file) 0))
        (do
          (s3/put-object bucket (generate-file-key (::transit/id file2) converted-filename)
                         converted-file)
          (log/debug "returning converted id to client")
          (http/transit-response file2))
        ;; else
        (do
          (if laundry-url
            (log/error "skipping laundry conversion because of nil filename / file object / conversion result")
            (log/info "skipping laundry conversion because no laundry-url configured"))
          (http/transit-response file))))
    (catch Exception e
      (let [msg (.getMessage e)]
        (log/error msg)
        (case msg
          "Invalid file type"
          {:status 422
           :body "Invalid file type."}
          {:status 500
           :body msg})))))

(defn- attachment-info [db user pre-notice-attachment-id]
  (let [can-view-all-attachments?
        (or (authorization/admin? user)
            (users/is-transit-authority-user? db {:user-id (authorization/user-id user)}))]
    (first
      (specql/fetch db ::transit/pre-notice-attachment
                    #{::transit/id ::transit/attachment-file-name}
                    (op/and
                      {::transit/id pre-notice-attachment-id}
                      (when-not can-view-all-attachments?
                        {::modification/created-by (authorization/user-id user)}))))))

(defn download-attachment [db {bucket :bucket :as config} {user :user :as req}]
  (let [attachment (attachment-info db user (Long/parseLong (get-in req [:params :id])))
        s3-file (when attachment
                  (s3/get-object bucket (generate-file-key
                                          (::transit/id attachment)
                                          (::transit/attachment-file-name attachment))))]
    (if-not s3-file
      {:status 404
       :body "No such attachment"}
      {:status 200
       :body (ring-io/piped-input-stream
               (fn [out]
                 (io/copy (:input-stream s3-file) out)))})))

(defn delete-from-s3 [db {bucket :bucket :as config} attachments]
  (doseq [{id ::transit/id file-name ::transit/attachment-file-name} attachments]
    (s3/delete-object bucket (generate-file-key id file-name))))

(defn upload-transport-service-csv
  "Company csv files are uploaded to s3 and stored to temp table at first. "
  [db {bucket :bucket :as config} service-id db-file-key {user :user :as req}]
  (try
    (let [uploaded-file (get-in req [:multipart-params "file"])
          _ (assert (and (:filename uploaded-file)
                         (:tempfile uploaded-file))
                    "No uploaded file")
          _ (validate-file-type uploaded-file service-allowed-mime-types)
          orig-filename (:filename uploaded-file)
          data (external/read-csv (slurp (:tempfile uploaded-file)))
          parsed-data (external/parse-response->csv data)
          validation-warning (str/join (external/validate-company-csv-file data))
          data (merge
                 {::t-service/csv-file-name orig-filename
                  ::t-service/validation-warning (when (not (empty? validation-warning))
                                                   validation-warning)
                  ::t-service/failed-companies-count (:failed-count parsed-data)
                  ::t-service/valid-companies-count (count (:result parsed-data))
                  ::modification/created-by (get-in user [:user :id])
                  ::modification/created (java.sql.Timestamp. (System/currentTimeMillis))}
                 (when service-id
                   {::t-service/transport-service-id service-id})
                 (when db-file-key
                   {::t-service/file-key db-file-key}))
          s3-file-key (file/generate-s3-csv-key orig-filename)
          db-row (specql/upsert! db
                                 ::t-service/transport-service-company-csv-temp
                                 (assoc data ::t-service/file-key s3-file-key))
          s3file-response (s3/put-object bucket s3-file-key
                                         (:tempfile uploaded-file))]

      ;; response to client application
      (http/transit-response
        {:status :success
         :count (count (:result parsed-data))
         :failed-count (:failed-count parsed-data)
         :companies (:result parsed-data)
         :filename orig-filename
         :db-file-key (::t-service/file-key db-row)}
        200))
    (catch Exception e
      (let [msg (.getMessage e)]
        (log/error msg)
        (case msg
          "Invalid file type"
          {:status 422
           :body "Invalid file type."}
          {:status 500
           :body msg})))))

(defn attachment-routes [db config]
  (if-not (or (:bucket (:pre-notices config)) (:bucket (:csv config)))
    (do (log/error "No S3 bucket configured, attachment upload/download disabled")
        nil)
    (wrap-multipart-params
      (routes
        (POST "/pre-notice/upload" req
          (upload-attachment db (:pre-notices config) req))
        (GET "/pre-notice/attachment/:id" req
          (download-attachment db (:pre-notices config) req))
        (POST "/transport-service/upload-company-csv/:service-id/:db-file-key" {{:keys [service-id db-file-key]} :params
                                                                                user :user :as req}
          (let [service-id (when (and service-id (> (Long/parseLong service-id) 0)) (Long/parseLong service-id))
                db-file-key (when (not= "x" db-file-key) db-file-key)]
            (upload-transport-service-csv db (:csv config) service-id db-file-key req)))
        (POST "/transit-changes/upload-gtfs/:service-id/:date"
              {{:keys [service-id date]} :params
               user :user
               :as req}
          (admin/require-admin-user "/transit-changes/upload-gtfs/:service-id/:date" (:user user))
          (do
            (transit-changes/upload-gtfs db (Long/parseLong service-id) date req)
            (gtfs-tasks/detect-new-changes-task db (time/date-string->date-time date) true [(Long/parseLong service-id)])
            (http/transit-response "OK")))))))
