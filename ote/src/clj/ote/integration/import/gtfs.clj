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
    ))

(defn save-gtfs-to-db [db gtfs-file package-id]
  (try
    (let [file-list (read-zip (java.io.ByteArrayInputStream. gtfs-file))]
      (doseq [f file-list
              :let [file-data (gtfs-parse/parse-gtfs-file (gtfs-spec/name->keyword (:name f)) (:data f))
                    db-table-name (db-table-name (:name f))]]
        (log/debug "File: " (:name f) " PARSED.")

        (cond
          (= db-table-name :gtfs/shape) nil                 ;; save shape data to shape[] array
          (= db-table-name :gtfs/stop-time) nil
          :else                                             ;; Save all rows separately
          (doseq [fk file-data]
            (when (and db-table-name (seq fk))
              (specql/insert! db db-table-name (assoc fk :gtfs/package-id package-id))))))
      (log/debug "Save-gtfs-to-db - package-id: " package-id))
    (catch Exception e
      (.printStackTrace e)
      (log/warn "Error in save-gtfs-to-db" e))))


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
