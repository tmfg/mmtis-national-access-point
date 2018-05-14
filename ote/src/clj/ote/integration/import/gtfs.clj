(ns ote.integration.import.gtfs
  "GTFS file import functionality."
  (:require [amazonica.aws.s3 :as s3]
            [ote.components.http :as http]
            [com.stuartsierra.component :as component]
            [compojure.core :refer [routes GET]]
            [clj-http.client :as http-client]
            [ote.util.zip :refer [read-zip read-zip-with]]
            [ote.gtfs.spec :as gtfs-spec]
            [ote.gtfs.parse :as gtfs-parse]
            [specql.core :as specql]
            [ote.db.gtfs :as gtfs]
            [clojure.java.io :as io]
            [digest]
            [specql.op :as op]
            [taoensso.timbre :as log]
            [specql.impl.composite :as specql-composite]
            [specql.impl.registry :refer [table-info-registry]]
            [jeesql.core :refer [defqueries]])
  (:import (java.io File)))

(defqueries "ote/integration/import/stop_times.sql")

(defn load-zip-from-url [url]
  (with-open [in (:body (http-client/get url {:as :stream}))]
    (read-zip in)))

(defn load-file-from-url [url last-import-date etag]
  (let [query-headers {:headers (merge
                                 (if (not (nil? etag))
                                   {"If-None-Match" etag}
                                   (when-not (nil? last-import-date)
                                     {"If-Modified-Since" last-import-date})))
                       :as :byte-array}
        response (http-client/get url query-headers)]
    (if (= 304 (:status response))
      ;; Not modified
      nil
      response)))

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

(defmulti process-rows (fn [file rows] file))

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
                                   (gtfs-trip-id-and-index db {:package-id package-id}))]
    (loop [i 0

           ;; Stop times file should have stops with the same trip id on consecutive lines
           ;; Partition returns a lazy sequence of groups of consecutive lines that have
           ;; the same trip id.
           [p & ps] (partition-by
                     :gtfs/trip-id
                     (gtfs-parse/parse-gtfs-file :gtfs/stop-times-txt
                                                 (io/reader stop-times-file)))]
      (when p
        (when (zero? (mod i 1000))
          (log/debug "Trip partitions stored: " i))

        (let [;; Use specql internal stringify to turn sequence of stop times
              ;; to a string in PostgreSQL composite array format
              stop-times (specql-composite/stringify @table-info-registry
                                                     {:category "A"
                                                      :element-type :gtfs/stop-time-info}
                                                     p true)
              {:keys [trip-row-id index] :as found} (trip-id->update-info (:gtfs/trip-id (first p)))]
          (when found
            (update-stop-times! db {:trip-row-id trip-row-id
                                    :index index
                                    :stop-times stop-times}))
          (recur (inc i) ps))))))

(defn save-gtfs-to-db [db gtfs-file package-id]
  (log/debug "save-gtfs-to-db - package-id: " package-id)
  (let [stop-times-file (File/createTempFile (str "stop-times-" package-id "-") ".txt")]
    (try
      (read-zip-with
       (java.io.ByteArrayInputStream. gtfs-file)
       (fn [{:keys [name input]}]
         (if (= name "stop_times.txt")
           ;; Copy stop times to a temp file, we need to process it last
           (with-open [output (io/output-stream stop-times-file)]
             (io/copy input output))
           (when-let [db-table-name (db-table-name name)]
             (let [file-type (gtfs-spec/name->keyword name)
                   file-data (gtfs-parse/parse-gtfs-file file-type (io/reader input))]
               (log/debug file-type " file: " name " PARSED.")
               (when (= file-type :gtfs/calendar-txt)
                 (def debug-calendar file-data))
               (doseq [fk (process-rows file-type file-data)]
                 (when (and db-table-name (seq fk))
                   (specql/insert! db db-table-name (assoc fk :gtfs/package-id package-id)))))))))

      ;; Handle stop times
      (import-stop-times db package-id stop-times-file)

      (log/info "Generating date hashes for package " package-id)
      (generate-date-hashes! db {:package-id package-id})

      (catch Exception e
        (.printStackTrace e)
        (log/warn "Error in save-gtfs-to-db" e))

      (finally
        (.delete stop-times-file)))))

;; PENDING: this is for local testing, truncates *ALL* GTFS data from the database
;;          and reads in a local GTFS zip file
#_(defn test-hsl-gtfs []
  (let [db (:db ote.main/ote)]
    (clojure.java.jdbc/execute! db ["TRUNCATE TABLE gtfs_package RESTART IDENTITY CASCADE"])
    (clojure.java.jdbc/execute! db ["INSERT INTO gtfs_package (id) VALUES (1)"])
    (let [bytes (with-open [in (io/input-stream "/Users/tatuta/Downloads/gtfs_tampere (2).zip" #_"hsl_gtfs.zip")]
                  (let [out (java.io.ByteArrayOutputStream.)]
                    (io/copy in out)
                    (.toByteArray out)))]
      (println "GTFS zip has " (int (/ (count bytes) (* 1024 1024))) " megabytes")
      (save-gtfs-to-db db bytes 1))))

(defn download-and-store-transit-package
  "Download gtfs (later kalkati files also) file, upload to s3, parse and store to database.
  Requires s3 bucket config, database settings, operator-id and transport-service-id."
  [gtfs-config db url operator-id ts-id last-import-date]
  (let [filename (gtfs-file-name operator-id ts-id)
        saved-etag (:gtfs/etag (last (specql/fetch db :gtfs/package
                                                   #{:gtfs/etag}
                                                   {:gtfs/transport-operator-id operator-id
                                                    :gtfs/transport-service-id  ts-id})))
        response (load-file-from-url url last-import-date saved-etag)
        new-etag (get-in response [:headers :etag])
        gtfs-file (:body response)]
    (if (nil? gtfs-file)
      (log/debug "Could not find new file version from given url " url)
      (let [new-gtfs-hash (gtfs-hash gtfs-file)
            old-gtfs-hash (specql/fetch db :gtfs/package
                                        #{:gtfs/sha256}
                                        {:gtfs/transport-operator-id operator-id
                                         :gtfs/transport-service-id  ts-id})]
        ;; IF hash doesn't match, save new and upload file to s3
        (if (or (nil? old-gtfs-hash) (not= old-gtfs-hash new-gtfs-hash))
          (do
            (let [package (specql/insert! db :gtfs/package {:gtfs/sha256                new-gtfs-hash
                                                            :gtfs/transport-operator-id operator-id
                                                            :gtfs/transport-service-id  ts-id
                                                            :gtfs/created               (java.sql.Timestamp. (System/currentTimeMillis))
                                                            :gtfs/etag new-etag})]
              (s3/put-object (:bucket gtfs-config) filename (java.io.ByteArrayInputStream. gtfs-file) {:content-length (count gtfs-file)})
              (log/debug "File: " filename " was uploaded to S3 successfully.")

              ;; Parse gtfs package and save it to database.
              (save-gtfs-to-db db gtfs-file (:gtfs/id package))))
          (log/debug "File " filename " was found from S3, no need to upload. Thank you for trying."))))))

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
