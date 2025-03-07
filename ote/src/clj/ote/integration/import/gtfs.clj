(ns ote.integration.import.gtfs
  "GTFS file import functionality."
  (:require [amazonica.aws.s3 :as s3]
            [ote.components.http :as http]
            [com.stuartsierra.component :as component]
            [compojure.core :refer [routes GET]]
            [clj-http.client :as http-client]
            [ote.util.zip :refer [read-zip read-zip-with list-zip]]
            [ote.gtfs.spec :as gtfs-spec]
            [ote.gtfs.parse :as gtfs-parse]
            [specql.core :as specql]
            [ote.db.transport-service :as t-service]
            [clojure.java.io :as io]
            [digest]
            [taoensso.timbre :as log]
            [specql.impl.composite :as specql-composite]
            [specql.impl.registry :refer [table-info-registry]]
            [jeesql.core :refer [defqueries]]
            [ote.gtfs.kalkati-to-gtfs :as kalkati-to-gtfs]
            [ote.transit-changes.detection :as detection]
            [ote.integration.report :as report])
  (:import (java.io File)
           (org.apache.commons.io FilenameUtils)))

(defqueries "ote/integration/import/stop_times.sql")
(defqueries "ote/integration/import/import_gtfs.sql")
(defqueries "ote/transit_changes/detection.sql")


(defn load-zip-from-url [url]
  (with-open [in (:body (http-client/get url {:as :stream}))]
    (read-zip in)))

(defn load-file-from-url [_ interface-id url last-import-date etag force-download?]
  (let [query-headers {:headers (when (not force-download?)
                                  (merge
                                    (if (not (nil? etag))
                                      {"If-None-Match" etag}
                                      (when-not (nil? last-import-date)
                                        {"If-Modified-Since" last-import-date}))))
                       :as :byte-array}
        response (http-client/get url query-headers)]
    (log/info "Fetching transit interface" interface-id  ", URL:" url ", etag:" etag ", last-import-date:" last-import-date "=> status" (:status response))
    (if (= 304 (:status response))
      ;; Not modified - return nil
      nil
      response)))

(defn load-gtfs [url-or-response]
  (http/transit-response
    (into {}
          (keep (fn [{:keys [name data]}]
                  (when-let [gtfs-file-type (gtfs-spec/name->keyword name)]
                    [gtfs-file-type (gtfs-parse/parse-gtfs-file gtfs-file-type data)])))
          (if (and (map? url-or-response)
                   (contains? url-or-response :body))
            ;; This is an HTTP response, read body input stream
            (read-zip (:body url-or-response))

            ;; This is an URL, fetch and read it
            (load-zip-from-url url-or-response)))))

; This is also used by ote.tasks.tis to generate a compatible name
(defn gtfs-file-name [operator-id ts-id]
  (let [new-date (java.util.Date.)
        date (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") new-date)]
    (str date "_" operator-id "_" ts-id "_gtfs.zip")))

(defn gtfs-hash [file]
  (digest/sha-256 file))

(defn db-table-name [file-name]
  (case file-name
    "agency.txt" :gtfs/agency
    "stops.txt" :gtfs/stop
    "routes.txt" :gtfs/route
    "calendar.txt" :gtfs/calendar
    "calendar_dates.txt" :gtfs/calendar-date
    "shapes.txt" :gtfs/shape
    "stop_times.txt" :gtfs/stop-time
    "trips.txt" :gtfs/trip
    "transfers.txt" nil
    nil))

(defmulti process-rows (fn [file _] file))

;; Combine trips into an array by route and service ids
(defmethod process-rows :gtfs/trips-txt [_ trips]
  (for [[[route-id service-id] trips] (group-by (juxt :gtfs/route-id :gtfs/service-id) trips)]
    {:gtfs/route-id route-id
     :gtfs/service-id service-id
     :gtfs/trips (map #(dissoc % :gtfs/route-id :gtfs/service-id) trips)}))

;; Combine into an array by shape id
(defmethod process-rows :gtfs/shapes-txt [_ shapes]
  (for [[shape-id shapes] (group-by :gtfs/shape-id shapes)]
    {:gtfs/shape-id shape-id
     :gtfs/route-shape (map #(select-keys % #{:gtfs/shape-pt-lat :gtfs/shape-pt-lon
                                              :gtfs/shape-pt-sequence :gtfs/shape-dist-traveled})
                            shapes)}))

(defmethod process-rows :default [_ rows] rows)

(defn import-stop-times [db package-id stop-times-file]
  (log/debug "Importing stop times from " stop-times-file
             " (" (int (/ (.length stop-times-file) (* 1024 1024))) "mb)")
  (let [;; Read all trips into memory (mapping from trip id to the row and index for update)
        trip-id->update-info (into {}
                                   (map (juxt :trip-id identity))
                                   (gtfs-trip-id-and-index db {:package-id package-id}))
        all-stop-times       (gtfs-parse/parse-gtfs-file :gtfs/stop-times-txt (io/reader stop-times-file))]
    (when (empty? all-stop-times)
      ; This is almost a copy-paste from a bit lower in this same namespace, the description is chosen so that it
      ; matches with the other one.
      (report/gtfs-import-report! db "error" package-id
                                  (str "No data rows in file stop_times.txt of type " :gtfs/stop-times-txt)
                                  (.getBytes "")))
    (loop [i 0
           ;; Stop times file should have stops with the same trip id on consecutive lines
           ;; Partition returns a lazy sequence of groups of consecutive lines that have
           ;; the same trip id.
           [p & ps] (partition-by
                     :gtfs/trip-id
                     all-stop-times)]
      (when p
        (when (zero? (mod i 1000))
          (log/debug "Trip partitions stored: " i "/" (count ps)))

        (let [;; Use specql internal stringify to turn sequence of stop times
              ;; to a string in PostgreSQL composite array format
              stop-times (specql-composite/stringify @table-info-registry
                                                     {:category "A"
                                                      :element-type :gtfs/stop-time-info}
                                                     p true)
              {:keys [trip-row-id index] :as found} (trip-id->update-info (:gtfs/trip-id (first p)))]
          (when found
            (update-stop-times! db {:package-id package-id
                                    :trip-row-id trip-row-id
                                    :index index
                                    :stop-times stop-times}))
          (recur (inc i) ps))))))

(defn save-gtfs-to-db [db gtfs-file package-id interface-id service-id intercept-fn interface-url import-date]
  ;; intercept-fn is for tests, when we want to rewrite dates in incoming data
  (log/debug "Save-gtfs-to-db - package-id: " package-id " interface-id " interface-id)
  (let [stop-times-file (File/createTempFile (str "stop-times-" package-id "-") ".txt")]
    (try
      (read-zip-with
       (java.io.ByteArrayInputStream. gtfs-file)
       (fn [{:keys [name input]}]
         (if (= name "stop_times.txt")
           ;; Copy stop times to a temp file, we need to process it last
           (with-open [output (io/output-stream stop-times-file)]
             (io/copy input output))
           (if-let [db-table-name (db-table-name name)]
             (let [file-type (gtfs-spec/name->keyword name)
                   file-data (gtfs-parse/parse-gtfs-file db package-id file-type (io/reader input))
                   file-data (if intercept-fn
                               (intercept-fn file-type file-data)
                               file-data)]
               (log/debug file-type " file: " name " PARSED.")
               (let [rows (process-rows file-type file-data)]
                 (when (= 0 (count rows))
                   (report/gtfs-import-report! db "error" package-id
                                               (str "No data rows in file " name " of type " file-type)
                                               (.getBytes "")))
                 (doseq [fk rows]
                   (when (and db-table-name (seq fk))
                     (specql/insert! db db-table-name (assoc fk :gtfs/package-id package-id))))))))))

      ;; Handle stop times
      (import-stop-times db package-id stop-times-file)

      ;; Calculate stop-fuzzy-lat and stop-fuzzy-lon
      (log/info "Generating fuzzy location for stops in package " package-id)
      (calculate-fuzzy-location-for-stops! db {:package-id package-id})

      ;; Handle detection-routes
      ;; Calculate route-hash-id for the service using previous 100 packages
      (detection/calculate-route-hash-id-for-service db service-id 100 (detection/db-route-detection-type db service-id))

      (log/info "Generating date hashes for package " package-id " service: " service-id)
      (generate-date-hashes-for-future db {:package-id package-id :transport-service-id service-id :from-date import-date})

      (log/info "Generating finnish regions and envelope for package " package-id)
      (gtfs-set-package-geometry db {:package-id package-id})

      (catch Exception e
        (log/warn "Error in save-gtfs-to-db" e)
        (report/gtfs-import-report! db "warning" package-id
                                    (str "Cannot create new GTFS import")
                                    (.getBytes (.getMessage e)))
        (specql/insert! db ::t-service/external-interface-download-status
                        {::t-service/external-interface-description-id interface-id
                         ::t-service/transport-service-id service-id
                         ::t-service/download-status :failure
                         ::t-service/package-id package-id
                         ::t-service/url interface-url
                         ::t-service/db-error (str (.getName (class e)) ": " (.getMessage e))
                         ::t-service/created (java.sql.Timestamp. (System/currentTimeMillis))})
        (.printStackTrace e))

      (finally
        (.delete stop-times-file)))))

;; PENDING: this is for local testing, truncates *ALL* GTFS data from the database
;;          and reads in a local GTFS zip file

;; Create Transport Service with :sheduled sub_type
;; Add gtfs url for the service.
;; Keep this method commented away and when you want to manually use this, start REPL first, and then run this in REPL only.
;; Also! Change from gtfs-package table columns transport-service-id, transport-operator-id and external-interface-description-id to what they should be.
;; Also! Change from gtfs_package column created to be earlier than today.
;; And then run SELECT gtfs_upsert_service_transit_changes(<service_id>);
#_(defn test-hsl-gtfs []
  (let [db (:db ote.main/ote)]
    (clojure.java.jdbc/execute! db ["TRUNCATE TABLE gtfs_package RESTART IDENTITY CASCADE"])
    (clojure.java.jdbc/execute! db ["INSERT INTO gtfs_package (id) VALUES (1)"])
    (let [bytes (with-open [in (io/input-stream "/Users/markusva/Downloads/google_transit (1).zip" #_"hsl_gtfs.zip")]
                  (let [out (java.io.ByteArrayOutputStream.)]
                    (io/copy in out)
                    (.toByteArray out)))]
      (println "**************************** START test-hsl-gtfs *********************")
      (println "GTFS zip has " (int (/ (count bytes) (* 1024 1024))) " megabytes")
      (save-gtfs-to-db db bytes 1 1 1 nil nil (java.util.Date.))
      (println "******************* test-hsl-gtfs end *********************"))))

(defn- load-interface-url [db interface-id service-id url last-import-date saved-etag force-download?]
  (try
    (load-file-from-url db interface-id url last-import-date saved-etag force-download?)
    (catch Exception e
      (let [message (str "Error when loading gtfs package from url " url ": " (.getMessage e))]
        (log/warn message)
        (specql/insert! db ::t-service/external-interface-download-status
                        {::t-service/external-interface-description-id interface-id
                         ::t-service/transport-service-id service-id
                         ::t-service/download-status :failure
                         ::t-service/download-error message
                         ::t-service/url url
                         ::t-service/created (java.sql.Timestamp. (System/currentTimeMillis))})
      nil))))

(defmulti validate-interface-zip-package
          (fn [type _] type))

(defmethod validate-interface-zip-package :gtfs [_ byte-array-input]
  (let [expected-files #{"agency.txt" "stops.txt" "stop_times.txt" "calendar_dates.txt" "routes.txt" "trips.txt"}
        file-list      (list-zip byte-array-input)
        missing-files  (clojure.set/difference expected-files (set file-list))]
    (log/debug "Files in ZIP" file-list)
    (when-not (empty? missing-files)
      (throw (ex-info (str "Missing required files in GTFS zip file, missing " (seq missing-files))
                      {:expected-files expected-files
                       :file-list      file-list
                       :missing-files  missing-files})))))

(defmethod validate-interface-zip-package :kalkati [_ byte-array-input]
  (let [file-list (list-zip byte-array-input)]

    (when-not (contains? file-list "LVM.xml")
      (throw (ex-info "Missing required files in kalkati zip file" {:file-names file-list})))))

(defn check-interface-zip [type db package-id interface-id url byte-array-data service-id]
  (try
    (validate-interface-zip-package type byte-array-data)

    (catch Exception e
      (let [message (str "Error when opening interface zip package from url " url ":" (.getMessage e))
            error   (str "Invalid interface package: " (.getMessage e))]
        (log/warn message)
        (report/gtfs-import-report! db "warning" package-id message (.getBytes (or (str (ex-data e)) error)))
        (specql/insert! db ::t-service/external-interface-download-status
                        {::t-service/external-interface-description-id interface-id
                         ::t-service/transport-service-id service-id
                         ::t-service/download-status :failure
                         ::t-service/download-error (str "Invalid interface package: " (.getMessage e))
                         ::t-service/created (java.sql.Timestamp. (System/currentTimeMillis))})
        (throw (ex-info (str "Invalid interface package") {} e))))))

(defmulti load-transit-interface-url
          "Load transit interface from URL. Dispatches on type.
          Returns a response map or nil if it has not been modified."
          (fn [type _ _ _ _ _ _ _ _] type))

(defmethod load-transit-interface-url :gtfs [type db package-id interface-id service-id url last-import-date saved-etag force-download?]
  (let [response (load-interface-url db interface-id service-id url last-import-date saved-etag force-download?)]
    (if response
      (try
        (check-interface-zip type db package-id interface-id url (java.io.ByteArrayInputStream. (:body response)) service-id)
        response

        ;; Return nil response in case of error
        (catch Exception e
          (log/warn "Error while loading package from url url " url ": " (.getMessage e))
          nil))
      ;; Return nil response in case of error
      nil)))

(defmethod load-transit-interface-url :kalkati [type db package-id interface-id service-id url last-import-date saved-etag force-download?]
  (let [response (load-interface-url db interface-id service-id url last-import-date saved-etag force-download?)]
    (if response
      (try
        (check-interface-zip type db package-id interface-id url (java.io.ByteArrayInputStream. (:body response)) service-id)
        (update response :body kalkati-to-gtfs/convert-bytes)

        ;; Return nil response in case of error
        (catch Exception e
          (log/warn "Error while loading package from url url " url ": " (.getMessage e))
          nil))
      ;; Return nil response in case of error
      nil)))

(defn interface-latest-package [db interface-id]
  (when interface-id
     (first
      (specql/fetch db :gtfs/package
                    #{:gtfs/id :gtfs/etag :gtfs/sha256}
                    {:gtfs/external-interface-description-id interface-id
                     :gtfs/deleted? false}
                    {::specql/order-by :gtfs/created
                     ::specql/order-direction :descending
                     ::specql/limit 1}))))

(defn download-and-store-transit-package
  "Download GTFS or kalkati file, optionally upload to s3,
  (Since change detection is disabled, don't parse and store to database anymore.)
  Returns map containing an in-memory traffic gtfs package and related attributes or nil on failure "
  [interface-type
   gtfs-config
   db
   {:keys [url operator-id operator-name ts-id last-import-date license id data-content]}
   upload-s3?
   force-download?]
  (log/debug "GTFS: Proceeding to download, service-id = " ts-id ", file url = " (pr-str url))
  (let [filename (gtfs-file-name operator-id ts-id)
        latest-package (interface-latest-package db id)
        package-count (:package-count (first (fetch-count-service-packages db {:service-id ts-id})))
        _ (log/info "download-and-store-transit-package :: package-count" (pr-str package-count) "(= 0 package-count)" (= 0 package-count))
        package (specql/insert! db :gtfs/package
                                {:gtfs/first_package (= 0 package-count)
                                 :gtfs/transport-operator-id operator-id
                                 :gtfs/transport-service-id ts-id
                                 :gtfs/created (java.sql.Timestamp. (System/currentTimeMillis))
                                 :gtfs/license license
                                 :gtfs/external-interface-description-id id})
        response (load-transit-interface-url interface-type db (:gtfs/id package) id ts-id url last-import-date
                                             (:gtfs/etag package) force-download?)
        new-etag (get-in response [:headers :etag])
        gtfs-file (:body response)]

    (if (nil? gtfs-file)
      (log/warn "GTFS: service-id = " ts-id ", Got empty body as response when loading gtfs, URL = '" url "'")
      (let [new-gtfs-hash (gtfs-hash gtfs-file)
            old-gtfs-hash (:gtfs/sha256 latest-package)]
        ;; IF hash doesn't match, save new and upload file to s3
        (if (or force-download? (nil? old-gtfs-hash) (not= old-gtfs-hash new-gtfs-hash))
          (let [_ (specql/update! db :gtfs/package {:gtfs/sha256 new-gtfs-hash
                                                    :gtfs/etag new-etag} {:gtfs/id (:gtfs/id package)})]
            (when upload-s3?
              (s3/put-object (:bucket gtfs-config)
                             filename
                             (java.io.ByteArrayInputStream. gtfs-file)
                             {:content-length (count gtfs-file)})
              ;; Parse gtfs package and save it to database.
              ;; Change detection has been disabled.
              #_ (save-gtfs-to-db db gtfs-file (:gtfs/id package) id ts-id nil url (java.util.Date.))
              ;; Mark interface download a success
              (specql/insert! db ::t-service/external-interface-download-status
                              {::t-service/external-interface-description-id id
                               ::t-service/transport-service-id ts-id
                               ::t-service/download-status :success  ; TODO: status should be dependent on number of errors
                               ::t-service/package-id (:gtfs/id package)
                               ::t-service/url url
                               ::t-service/created (java.sql.Timestamp. (System/currentTimeMillis))})))

          (log/debug (str "GTFS: service-id=" ts-id ", File=" filename
                          " was found from db, no need to store or s3-upload. Thank you for trying.")))))

    ;; returning nil signals failure
    (when gtfs-file
      (log/debug (str "GTFS: service-id = " ts-id ", File imported and uploaded successfully, file = " filename))
      {:gtfs-file                         gtfs-file
       :gtfs-filename                     filename
       :gtfs-basename                     (FilenameUtils/getBaseName filename)
       :external-interface-description-id id
       :external-interface-data-content   data-content
       :service-id                        ts-id
       :package-id                        (:gtfs/id package)
       :operator-name                     operator-name})))

(defrecord GTFSImport [config]
  component/Lifecycle
  (start [{db :db http :http :as this}]
    (assoc this ::stop
      (http/publish! http {:authenticated? false}
                     (routes
                       (GET "/import/gtfs" {params :query-params}
                         (load-gtfs (get params "url")))))))
  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
