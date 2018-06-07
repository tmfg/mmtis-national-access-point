(ns ote.tasks.gtfs
  "Scheduled tasks to update gtfs file to s3 and later to database."
  (:require [chime :refer [chime-at]]
            [clj-time.core :as t]
            [clj-time.periodic :refer [periodic-seq]]
            [ote.util.feature :as feature]
            [com.stuartsierra.component :as component]
            [jeesql.core :refer [defqueries]]
            [ote.db.tx :as tx]
            [ote.db.transport-service :as t-service]
            [ote.integration.import.gtfs :as import-gtfs]
            [taoensso.timbre :as log]
            [specql.core :as specql]
            [ote.time :as time]
            [ote.tasks.util :refer [timezone]])
  (:import (org.joda.time DateTimeZone)))

(defqueries "ote/tasks/gtfs.sql")

(def daily-update-time (t/from-time-zone (t/today-at 0 5)
                                         (DateTimeZone/forID "Europe/Helsinki")))

(defn interface-type [format]
  (case format
    "GTFS" :gtfs
    "Kalkati.net" :kalkati))

(defn fetch-and-mark-gtfs-interface! [db]
  (tx/with-transaction db
    (let [gtfs-data (first (select-gtfs-urls-update db))]
      (when gtfs-data
        (specql/update! db ::t-service/external-interface-description
                        {::t-service/gtfs-imported (java.sql.Timestamp. (System/currentTimeMillis))}
                        {::t-service/id (:id gtfs-data)}))
      gtfs-data)))

(defn update-one-gtfs! [config db]
  ;; Ensure that gtfs-import flag is enabled
  (let [{:keys [url operator-id ts-id last-import-date format] :as gtfs-data}
        (fetch-and-mark-gtfs-interface! db)]
    (if (or (nil? gtfs-data)
            (contains? (:no-gtfs-update-for-operators config) operator-id))
      (log/debug "No gtfs files to upload.")
      (try
        (log/debug "GTFS File found - Try to upload file to S3. - " (pr-str gtfs-data))
        (import-gtfs/download-and-store-transit-package (interface-type format)
                                                        (:gtfs config) db url operator-id ts-id
                                                        last-import-date
                                                        (:license gtfs-data))
        (catch Exception e
          (specql/update! db ::t-service/external-interface-description
                          {::t-service/gtfs-imported (java.sql.Timestamp. (System/currentTimeMillis))
                           ::t-service/gtfs-import-error (str (.getName (class e)) ": " (.getMessage e))}
                          {::t-service/id (:id gtfs-data)})
          (log/warn "Error in gtfs s3 upload!" e))))))

(def night-hours #{0 1 2 3 4})

(defn night-time? [dt]
  (-> dt (t/to-time-zone timezone) time/date-fields ::time/hours night-hours boolean))

(defrecord GtfsTasks [at config]
  component/Lifecycle
  (start [{db :db :as this}]
    (assoc this
           ::stop-task (if (feature/feature-enabled? config :gtfs-import)
                         (chime-at
                          (filter night-time?
                                  (drop 1 (periodic-seq (t/now) (t/minutes 1))))
                          (fn [_]
                            (#'update-one-gtfs! config db)))
                         (do
                           (log/debug "GTFS IMPORT IS NOT ENABLED!")
                           nil))))
  (stop [{stop-task ::stop-task :as this}]
    (when stop-task
      (stop-task))
    (dissoc this ::stop-task)))

(defn gtfs-tasks
  ([config] (gtfs-tasks daily-update-time config))
  ([at config]
   (->GtfsTasks at config)))
