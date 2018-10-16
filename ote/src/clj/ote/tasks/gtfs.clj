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
            [ote.tasks.util :refer [daily-at timezone]]
            [ote.db.lock :as lock]
            [clojure.string :as str]
            [ote.util.functor :refer [fmap]])
  (:import (org.joda.time DateTimeZone)))

(defqueries "ote/tasks/gtfs.sql")

(def daily-update-time (t/from-time-zone (t/today-at 0 5)
                                         (DateTimeZone/forID "Europe/Helsinki")))

(defn interface-type [format]
  (case format
    "GTFS" :gtfs
    "Kalkati.net" :kalkati))

(defn fetch-and-mark-gtfs-interface! [config db]
  (tx/with-transaction db
    (let [blacklisted-operators {:blacklist (if (empty? (:no-gtfs-update-for-operators config))
                                              #{-1} ;; this is needed for postgres NOT IN conditional
                                              (:no-gtfs-update-for-operators config))}
          gtfs-data (first (select-gtfs-urls-update db blacklisted-operators))]
      (when gtfs-data
        (specql/update! db ::t-service/external-interface-description
                        {::t-service/gtfs-imported (java.sql.Timestamp. (System/currentTimeMillis))}
                        {::t-service/id (:id gtfs-data)}))
      gtfs-data)))

(defn update-one-gtfs! [config db upload-s3?]
  ;; Ensure that gtfs-import flag is enabled
  ;; upload-s3? should be false when using local environment
  (let [{:keys [id url operator-id ts-id last-import-date format license] :as gtfs-data}
        (fetch-and-mark-gtfs-interface! config db)]
    (if (nil? gtfs-data)
      (log/debug "No gtfs files to upload.")
      (try
        (log/debug "GTFS File url found. " (pr-str gtfs-data))
        (import-gtfs/download-and-store-transit-package
          (interface-type format) (:gtfs config) db url operator-id ts-id last-import-date license id upload-s3?)
        (catch Exception e
          (log/warn e "Error when importing, uploading or saving gtfs package to db!"))
        (finally
          (log/debug "GTFS file imported and uploaded successfully!"))))))

(def night-hours #{0 1 2 3 4})

(defn night-time? [dt]
  (-> dt (t/to-time-zone timezone) time/date-fields ::time/hours night-hours boolean))


;;; FIXME: move this to its own ns... like ote.gtfs.change-detection

(defn extract-first-hash [{s :hashes}]
  (let [pos (.indexOf s (int \,))]
    (if (neg? pos)
      s
      (subs s 0 pos))))

(defn skip-first-hash [{s :hashes :as route}]
  (let [pos (.indexOf s (int \,))]
    (assoc route :hashes (if (neg? pos)
                           ""
                           (subs s (inc pos))))))

(defn routes-by-date [routes-with-hashes start-date]
  (loop [date start-date
         dates []
         routes routes-with-hashes]
    (if (str/blank? (:hashes (first routes)))
      ;; No more hashes, return all dates
      dates

      ;; More hashes, extract first from each route
      (recur (.plusDays date 1)
             (conj dates
                   {:date date
                    :routes (into {}
                                  (map (juxt (juxt :route-short-name :route-long-name :trip-headsign)
                                             #(let [h (extract-first-hash %)]
                                                ;; If hash is "N/A" (no hash could be calculated, use nil
                                                (when (not= "N/A" h)
                                                  h))))
                                  routes)})
             (map skip-first-hash routes)))))

(defn combine-weeks
  "Combine list of date based hashes into weeks."
  [routes-by-date]
  (for [days (partition 7 routes-by-date)
        :let [bow (:date (first days))
              eow (:date (last days))]]
    {:beginning-of-week bow
     :end-of-week eow
     :routes (apply merge-with (fn [v1 v2]
                                 (if (vector? v1)
                                   (conj v1 v2)
                                   [v1 v2]))
                    (map :routes days))}))

(defn next-different-weeks
  "Detect the next different week in each route. Takes a list of weeks that have week hashes for each route.
  Returns map from route [short long headsign] to next different week info."
  [route-weeks]
  (reduce
   (fn [route-detection-state [prev curr next]]

     ;; do detection per route

     )
   (partition 3 1 route-weeks))
  )

(defn route-changes [db {start-date :start-date :as route-query-params}]
  (combine-weeks (routes-by-date (service-routes-with-hashes db route-query-params)
                                 start-date)))

(defn detect-new-changes-task
  ([db]
   (detect-new-changes-task db false))
  ([db force?]
   (let [start-date (time/beginning-of-week (time/now))
         end-date (time/days-from start-date (dec (* 7 15)))]
     (lock/try-with-lock
      db "gtfs-nightly-changes" 1800
      (let [service-ids (map :id (services-for-nightly-change-detection db {:force force?}))]
        (log/info "Detect transit changes for " (count service-ids) " services.")
        (doseq [service-id service-ids]
          (log/info "Detecting next transit changes for service: " service-id)
          (let [changes (route-changes )])
          (upsert-service-transit-change db {:service-id service-id})))))))

(defrecord GtfsTasks [at config]
  component/Lifecycle
  (start [{db :db :as this}]
    (assoc this
           ::stop-tasks
           (if (feature/feature-enabled? config :gtfs-import)
             [(chime-at
               (filter night-time?
                       (drop 1 (periodic-seq (t/now) (t/minutes 1))))
               (fn [_]
                 (#'update-one-gtfs! config db true)))
              (chime-at (daily-at 5 15)
                        (fn [_]
                          (detect-new-changes-task db)))]
             (do
               (log/debug "GTFS IMPORT IS NOT ENABLED!")
               nil))))
  (stop [{stop-tasks ::stop-tasks :as this}]
    (for [stop-task stop-tasks]
      (stop-task))
    (dissoc this ::stop-tasks)))

(defn gtfs-tasks
  ([config] (gtfs-tasks daily-update-time config))
  ([at config]
   (->GtfsTasks at config)))
