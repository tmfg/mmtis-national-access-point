(ns ote.integration.import.gtfs
  "GTFS file import functionality."
  (:require [amazonica.aws.s3 :as s3]
            [ote.components.http :as http]
            [com.stuartsierra.component :as component]
            [compojure.core :refer [routes GET]]
            [clj-http.client :as http-client]
            [ote.util.zip :refer [read-zip]]
            [ote.gtfs.spec :as gtfs-spec]
            [ote.gtfs.parse :as gtfs-parse]
            [specql.core :as specql]
            [ote.db.gtfs :as gtfs]
            [clojure.java.io :as io]
            [digest]
            [specql.op :as op]
            [taoensso.timbre :as log]))

(defn load-zip-from-url [url]
  (with-open [in (:body (http-client/get url {:as :stream}))]
    (read-zip in)))

(defn load-file-from-url [url last-import-date]
  (let [query-headers (if (nil? last-import-date)
                        {:as :byte-array}
                        {:headers {"If-Modified-Since" last-import-date}
                         :as      :byte-array})
        response (http-client/get url query-headers)]
    (if (= 304 (:status response))
      ;; Not modified
      nil
      (:body response))))

(defn load-gtfs [url]
  (http/transit-response
   (into {}
         (keep (fn [{:keys [name data]}]
                 (when-let [gtfs-file-type (gtfs-spec/name->keyword name)]
                   [gtfs-file-type (gtfs-parse/parse-gtfs-file gtfs-file-type data)])))
         (load-zip-from-url url))))

(defn gtfs-file-name [operator-id ts-id]
  (let [new-date (java.util.Date.)
        date (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") new-date)]
    (str date "_" operator-id "_" ts-id "_gtfs.zip")))

(defn gtfs-hash [file]
  (digest/sha-256 file))

(defn upload-gtfs->s3 [gtfs-config db url operator-id ts-id last-import-date]
  ;; TODO: add transactions
  (let [filename (gtfs-file-name operator-id ts-id)
        gtfs-file (load-file-from-url url last-import-date)
        new-gtfs-hash (gtfs-hash gtfs-file)
        old-gtfs-hash (::gtfs/sha256 (first (specql/fetch db ::gtfs/package
                                                          (specql/columns ::gtfs/package)
                                                          {::gtfs/transport-operator-id operator-id})))]

    ;; IF hash doesn't match, save new and upload file to s3
    (when (or (nil? old-gtfs-hash) (not= old-gtfs-hash new-gtfs-hash))
      (s3/put-object (:bucket gtfs-config) filename (java.io.ByteArrayInputStream. gtfs-file) {:content-length (count gtfs-file)})
      (specql/upsert! db ::gtfs/package {::gtfs/sha256                new-gtfs-hash
                                         ::gtfs/transport-operator-id operator-id
                                         ::gtfs/created               (java.sql.Timestamp. (System/currentTimeMillis))})
      (log/debug "File " filename " was uploaded to S3"))))

(defrecord GTFSImport [gtfs-config]
  component/Lifecycle
  (start [{db :db http :http :as this}]
    (assoc this ::stop
      (http/publish! http {:authenticated? false}
                     (routes
                       (GET "/import/gtfs" {params :query-params}
                         (load-gtfs (get params "url")))
                       (GET "/import/gtfss3" {params :query-params}
                         (upload-gtfs->s3 gtfs-config db
                                          (get params "url")
                                          (get params "operator-id")
                                          (get params "ts-id")
                                          nil))))))
  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
