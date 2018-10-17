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
            [ote.util.functor :refer [fmap]]
            [ote.util.collections :refer [map-by]]
            [ote.transit-changes :as transit-changes])
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

(defn routes-by-date [date-route-hashes all-routes]
  (for [hashes-for-date (partition-by :date date-route-hashes)
        :let [date (:date (first hashes-for-date))]]
    {:date (.toLocalDate date)
     :routes (into (zipmap all-routes (repeat nil))
                   (map (juxt (juxt :route-short-name :route-long-name :trip-headsign) :hash))
                   hashes-for-date)}))

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

(defn week=
  "Compare week hashes. Returns true if they represent the same traffic
  (excluding no-traffic days).

  Both `w1` and `w2` are vectors of strings that must be the same length."
  [w1 w2]
  (every? true?
          (map (fn [h1 h2]
                 ;; Only compare hashes where both days have traffic (not nil)
                 (or (nil? h1)
                     (nil? h2)
                     (= h1 h2)))
               w1 w2)))

(defn detect-change-for-route
  "Reduces [prev curr next] weeks into a detection state change"
  [{:keys [starting-week-hash] :as state} [prev curr next1 next2]]
  (cond

    ;; If this is the first call and the current week is "anomalous".
    ;; Then start at the next week.
    (and (nil? state)
         (not (week= curr next1))
         (week= prev next1))
    ;; Ignore this week
    {}

    ;; No starting week specified yet, use current week
    (nil? starting-week-hash)
    (assoc state :starting-week-hash curr)

    ;; If current week does not equal starting week...
    (and (not (week= starting-week-hash curr))
         ;; ...and traffic does not revert back to previous in two weeks
         (not (week= prev next2)))
    ;; this is a change
    (assoc state :different-week-hash curr)

    ;; No change found, return state as is
    :default state))

(defn add-current-week-hash [to-key if-key state week]
  (if (and (nil? (get state to-key))
           (some? (get state if-key)))
    (assoc state to-key (dissoc week :routes))
    state))

(def add-starting-week
  (partial add-current-week-hash :starting-week :starting-week-hash))

(def add-different-week
  (partial add-current-week-hash :different-week :different-week-hash))

(defn next-different-weeks
  "Detect the next different week in each route. Takes a list of weeks that have week hashes for each route.
  Returns map from route [short long headsign] to next different week info."
  [route-weeks]
  ;; Take routes from the first week (they are the same in all weeks)
  (let [routes (into #{}
                     (map first)
                     (:routes (first route-weeks)))]

    (reduce
     (fn [route-detection-state [_ curr _ _ :as weeks]]
       (reduce
        (fn [route-detection-state route]
          (update route-detection-state route
                  (fn [{diff :different-week :as state}]
                    (if diff
                      ;; change already found, don't try again
                      state

                      ;; Change not yet found, try to find one
                      (-> state
                          (detect-change-for-route (mapv (comp #(get % route) :routes)
                                                         weeks))
                          (add-starting-week curr)
                          (add-different-week curr))))))
        route-detection-state routes))
     {} ; initial route detection state is empty
     (partition 4 1 route-weeks))))

(defn route-trips-for-date [db service-id [short long headsign] date]
  (vec
   (for [trip-stops (partition-by (juxt :package-id :trip-id)
                                  (fetch-route-trips-for-date db {:service-id service-id
                                                                  :route-short-name short
                                                                  :route-long-name long
                                                                  :trip-headsign headsign
                                                                  :date date}))
         :let [package-id (:package-id (first trip-stops))
               trip-id (:trip-id (first trip-stops))]]
     {:gtfs/package-id package-id
      :gtfs/trip-id trip-id
      :stoptimes (mapv (fn [{:keys [stop-id departure-time stop-sequence]}]
                         {:gtfs/stop-id stop-id
                          :gtfs/stop-name stop-id ; we don't fetch name, use id
                          :gtfs/stop-sequence stop-sequence
                          :gtfs/departure-time (time/pginterval->interval departure-time)})
                       trip-stops)})))

(defn compare-route-days [db service-id [short long headsign :as route]
                          {:keys [starting-week starting-week-hash
                                  different-week different-week-hash] :as r}]
  (let [first-different-day (some identity
                                  (map (fn [i d1 d2]
                                         (and (some? d1)
                                              (some? d2)
                                              (not= d1 d2)
                                              i))
                                       (iterate inc 0)
                                       starting-week-hash
                                       different-week-hash))
        starting-week-date (.plusDays (:beginning-of-week starting-week) first-different-day)
        different-week-date (.plusDays (:beginning-of-week different-week) first-different-day)]
    (log/debug "Route: " route ", comparing dates: " starting-week-date " and " different-week-date)
    (let [params {:service-id service-id
                  :route-short-name short
                  :route-long-name long
                  :trip-headsign headsign}
          date1-trips (route-trips-for-date db service-id route starting-week-date)
          date2-trips (route-trips-for-date db service-id route different-week-date)]

      (log/debug "trips " (count date1-trips) " vs " (count date2-trips))
      (let [combined-trips (transit-changes/combine-trips date1-trips date2-trips)
            added-trips (count
                         (filter (fn [[l r]]
                                   (and (nil? l)
                                        (some? r))) combined-trips))
            removed-trips (count
                           (filter (fn [[l r]]
                                     (and (some? l)
                                          (nil? r))) combined-trips))]
        {:added-trips added-trips
         :removed-trips removed-trips}))))

(defn route-day-changes
  "Takes in routes with possible different weeks and adds day change comparison."
  [db service-id routes]
  (into {}
        (map (fn [[route {diff :different-week :as detection-result}]]
               (if diff
                 ;; If a different week was found, do detailed trip analysis
                 [route (assoc detection-result
                               :changes (compare-route-days db service-id route detection-result))]

                 ;; Otherwise return as is
                 [route detection-result])))
        routes))

(defn route-changes [db {:keys [start-date service-id] :as route-query-params}]
  (let [all-routes (service-routes-with-date-range db {:service-id service-id})
        all-route-keys (into #{}
                             (map (juxt :route-short-name :route-long-name :trip-headsign))
                             all-routes)]
    (-> db
        (service-route-hashes-for-date-range route-query-params)
        (routes-by-date all-route-keys)
        combine-weeks
        (next-different-weeks)
        (as-> routes
            (route-day-changes db service-id routes)))))

(defn detect-new-changes-task
  ([db]
   (detect-new-changes-task db false))
  ([db force?]
   (let [;; Start from the beginning of last week
         start-date (time/days-from (time/beginning-of-week (time/now)) -7)

         ;; Continue 15 weeks from the current week
         end-date (time/days-from start-date (dec (* 7 16)))]
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
