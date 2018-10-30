(ns ote.transit-changes.detection
  "Detect changes in transit traffic patterns.
  Interfaces with stored GTFS transit data."
  (:require [ote.transit-changes :as transit-changes :refer [week=]]
            [ote.time :as time]
            [jeesql.core :refer [defqueries]]
            [taoensso.timbre :as log]
            [specql.core :as specql]
            [ote.util.collections :refer [map-by count-matching]]
            [ote.util.functor :refer [fmap]]
            [ote.db.gtfs :as gtfs])
  (:import (java.time LocalDate DayOfWeek)))

(def ^:const no-traffic-detection-threshold
  "The amount of days after a no-traffic run is detected as a change."
  16)

(defqueries "ote/transit_changes/detection.sql")

(def route-key
  "Route key is a vector of [short-name long-name headsign]"
  (juxt :route-short-name :route-long-name :trip-headsign))

(defn routes-by-date [date-route-hashes all-routes]
  ;; date-route-hashes contains all hashes for date range and is sorted
  ;; by date so we can partition by :date to get each date's hashes
  (for [hashes-for-date (partition-by :date date-route-hashes)
        :let [date (:date (first hashes-for-date))]]
    {:date (.toLocalDate date)
     :routes (into (zipmap all-routes (repeat nil))
                   (map (juxt route-key :hash))
                   hashes-for-date)}))

(defn merge-week-hash
  "Merges multiple maps containing route day hashes.
  Returns a single map with each routes hashes in a vector."
  [route-day-hashes]
  (apply merge-with (fn [v1 v2]
                      (if (vector? v1)
                        (conj v1 v2)
                        [v1 v2]))
         route-day-hashes))

(defn combine-weeks
  "Combine list of date based hashes into weeks."
  [routes-by-date]
  (for [days (partition 7 routes-by-date)
        :let [bow (:date (first days))
              eow (:date (last days))]]
    {:beginning-of-week bow
     :end-of-week eow
     :routes (merge-week-hash (map :routes days))}))


(defn detect-change-for-route
  "Reduces [prev curr next1 next2] weeks into a detection state change"
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
         (not (week= starting-week-hash next2)))
    ;; this is a change
    (assoc state :different-week-hash curr)

    ;; No change found, return state as is
    :default state))

(defn week-hash-no-traffic-run
  "Return the continuous amount of days that have no traffic at the beginning or end of the weekhash."
  [beginning? weekhash]
  (->> (if beginning?
         weekhash
         (reverse weekhash))
       (take-while nil?)
       count))

(defn add-current-week-hash [to-key if-key state week]
  (if (and (nil? (get state to-key))
           (some? (get state if-key)))
    (assoc state to-key (dissoc week :routes))
    state))

(def add-starting-week
  (partial add-current-week-hash :starting-week :starting-week-hash))

(def add-different-week
  (partial add-current-week-hash :different-week :different-week-hash))

(defn detect-no-traffic-run [{no-traffic-run :no-traffic-run :as state} [_ curr _ _]]
  (let [;; How many continuous days have no traffic at the start of the week
        beginning-run (week-hash-no-traffic-run true curr)

        ;; How many continuous days have no traffic at the end of the week
        end-run (week-hash-no-traffic-run false curr)]

    (cond

      ;; If a no traffic run is in progress and this week has no traffic
      (and no-traffic-run (= 7 beginning-run))
      (assoc state :no-traffic-run (+ no-traffic-run 7))

      ;; If current run + beginning run is above threshold, mark this as a change
      (and no-traffic-run
           (> (+ no-traffic-run beginning-run) no-traffic-detection-threshold))
      (-> state
          (dissoc :no-traffic-run)
          (assoc :no-traffic-change (+ no-traffic-run beginning-run)))

      ;; If no run is in progress but current week ends in no traffic, start new run
      (and (nil? no-traffic-run)
           (pos? end-run))
      (assoc state :no-traffic-run end-run)

      ;; No condition matched, remove any partial run
      :default
      (dissoc state :no-traffic-start-date :no-traffic-run))))

(defn add-no-traffic-run-dates [{:keys [no-traffic-run no-traffic-change
                                        no-traffic-start-date] :as state} week]

  (cond

    ;; A new no traffic run has started, set date from current week
    (and no-traffic-run (nil? no-traffic-start-date))
    (assoc state :no-traffic-start-date (.plusDays (:beginning-of-week week) (- 7 no-traffic-run)))

    ;; No traffic change was detected, add end date
    no-traffic-change
    (assoc state :no-traffic-end-date (.plusDays no-traffic-start-date no-traffic-change))

    ;; Run ended without reaching threshold, remove start date
    (nil? no-traffic-run)
    (dissoc state :no-traffic-start)

    :default state))

(defn- route-next-different-week
  [{diff :different-week no-traffic-end-date :no-traffic-end-date
    :as state} route weeks curr]
  (if (or diff no-traffic-end-date)
    ;; change already found, don't try again
    state

    ;; Change not yet found, try to find one
    (let [week-hashes (mapv (comp #(get % route) :routes)
                            weeks)]
      (-> state
          ;; Detect no-traffic run
          (detect-no-traffic-run week-hashes)
          (add-no-traffic-run-dates curr)

          ;; Detect other traffic changes
          (detect-change-for-route week-hashes)
          (add-starting-week curr)
          (add-different-week curr)))))

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
                  route-next-different-week route weeks curr))
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
  (let [first-different-day (transit-changes/first-different-day starting-week-hash
                                                                 different-week-hash)
        starting-week-date (.plusDays (:beginning-of-week starting-week) first-different-day)
        different-week-date (.plusDays (:beginning-of-week different-week) first-different-day)]
    (log/debug "Route: " route ", comparing dates: " starting-week-date " and " different-week-date)
    (let [date1-trips (route-trips-for-date db service-id route starting-week-date)
          date2-trips (route-trips-for-date db service-id route different-week-date)]

      (log/debug "trips " (count date1-trips) " vs " (count date2-trips))
      (let [combined-trips (transit-changes/combine-trips date1-trips date2-trips)
            {:keys [added removed changed]}
            (group-by (fn [[l r]]
                        (cond
                          (nil? l) :added
                          (nil? r) :removed
                          :default :changed))
                      combined-trips)]
        {:starting-week-date starting-week-date
         :different-week-date different-week-date
         :added-trips (count added)
         :removed-trips (count removed)
         :trip-changes (map (fn [[l r]]
                              (transit-changes/trip-stop-differences l r))
                             changed)}))))

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

(defn- max-date-in-the-past? [{max-date :max-date}]
  (.isBefore (.toLocalDate max-date) (java.time.LocalDate/now)))

(defn- max-date-within-90-days? [{max-date :max-date}]
  (.isBefore (.toLocalDate max-date) (.plusDays (java.time.LocalDate/now) 90)))

(defn- min-date-in-the-future? [{min-date :min-date}]
  (.isAfter (.toLocalDate min-date) (java.time.LocalDate/now)))

(defn- update-min-max-range [range val]
  (merge {:lower-inclusive? true
          :upper-inclusive? true}
         (-> range
             (update :lower #(if (or (nil? %) (< val %)) val %))
             (update :upper #(if (or (nil? %) (> val %)) val %)))))

(defn- week-day-in-week [date week-day]
  (.plusDays date (- (.getValue week-day)
                     (.getValue (.getDayOfWeek date)))))

(defn- sql-date [local-date]
  (when local-date
    (java.sql.Date/valueOf local-date)))

(defn transform-route-change
  "Transform a detected route change into a database 'gtfs-route-change-info' type."
  [all-routes route-key
   {:keys [no-traffic-start-date no-traffic-end-date
           starting-week different-week
           changes]}]
  (let [route (all-routes route-key)
        added? (min-date-in-the-future? route)
        removed? (max-date-within-90-days? route)
        no-traffic? (and no-traffic-start-date no-traffic-end-date)
        {:keys [starting-week-date different-week-date
                added-trips removed-trips trip-changes]} changes
        trip-stop-seq-changes (reduce update-min-max-range nil
                                      (map :stop-seq-changes trip-changes))
        trip-stop-time-changes (reduce update-min-max-range nil
                                       (map :stop-time-changes trip-changes))]

    (merge
     {;; Route identification
      :gtfs/route-short-name (:route-short-name route)
      :gtfs/route-long-name (:route-long-name route)
      :gtfs/trip-headsign (:trip-headsign route)

      ;; Change classification
      :gtfs/change-type (cond
                          no-traffic? :no-traffic
                          added? :added
                          removed? :removed
                          changes :changed
                          :default :no-change)

      ;; Trip change counts
      :gtfs/added-trips added-trips
      :gtfs/removed-trips removed-trips
      :gtfs/trip-stop-sequence-changes trip-stop-seq-changes
      :gtfs/trip-stop-time-changes trip-stop-time-changes}

     ;; Dates
     (if no-traffic?
       ;; If the change is a no-traffic period, the different day is the first day that has no traffic
       {:gtfs/current-week-date (sql-date
                                 (.plusDays no-traffic-start-date -1)
                                 #_(week-day-in-week (:beginning-of-week starting-week) (.getDayOfWeek no-traffic-start-date)))
        :gtfs/different-week-date (sql-date no-traffic-start-date)
        :gtfs/change-date (sql-date no-traffic-start-date)}

       ;; If this is a change
       {:gtfs/current-week-date (sql-date starting-week-date)
        :gtfs/different-week-date (sql-date different-week-date)
        :gtfs/change-date (sql-date different-week-date)}))))

(defn- debug-print-change-stats [all-routes route-changes]
  (doseq [r all-routes
          :let [key (route-key r)
                {:keys [changes no-traffic-start-date no-traffic-end-date]
                 :as route} (route-changes key)]]
    (println key " has traffic " (:min-date r) " - " (:max-date r)
             (when no-traffic-end-date
               (str " no traffic between: " no-traffic-start-date " - " no-traffic-end-date))
             (when changes
               (str " has changes")))))

(defn store-transit-changes! [db service-id package-ids {:keys [all-routes route-changes]}]
  (let [today (java.time.LocalDate/now)
        route-change-infos (map (fn [[route-key detection-result]]
                                  (transform-route-change all-routes route-key detection-result))
                                route-changes)
        change-count-by-type (fmap count (group-by :gtfs/change-type route-change-infos))
        earliest-route-change (first (drop-while (complement :gtfs/change-date)
                                                 (sort-by :gtfs/change-date route-change-infos)))]
    #_(debug-print-change-stats all-routes route-changes)
    (specql/upsert! db :gtfs/transit-changes
                    #{:gtfs/transport-service-id :gtfs/date}
                    {:gtfs/transport-service-id service-id

                     :gtfs/date today
                     :gtfs/change-date (:gtfs/change-date earliest-route-change)
                     :gtfs/different-week-date (:gtfs/different-week-date earliest-route-change)
                     :gtfs/current-week-date (:gtfs/current-week-date earliest-route-change)

                     :gtfs/removed-routes (:removed change-count-by-type 0)
                     :gtfs/added-routes (:added change-count-by-type 0)
                     :gtfs/changed-routes (:changed change-count-by-type 0)
                     :gtfs/no-traffic-routes (:no-traffic change-count-by-type 0)
                     :gtfs/route-changes route-change-infos

                     :gtfs/package-ids package-ids})))

(defn route-changes [db {:keys [start-date service-id] :as route-query-params}]
  (let [all-routes (map-by (juxt :route-short-name :route-long-name :trip-headsign)
                           (service-routes-with-date-range db {:service-id service-id}))
        all-route-keys (set (keys all-routes))]
    {:all-routes all-routes
     :route-changes
     (-> db
         (service-route-hashes-for-date-range route-query-params)
         (routes-by-date all-route-keys)
         combine-weeks
         (next-different-weeks)
         (as-> routes
             ;; Fetch detailed route comparison if a change was found
             (route-day-changes db service-id routes)))}))


(def db (:db ote.main/ote))
(def query-params {:start-date (LocalDate/of 2018 10 29)
                            :end-date (LocalDate/of 2019 2 17)
                            :service-id 1454})
(def chg (route-changes db query-params))
(store-transit-changes! db 1454 (mapv :id (service-packages-for-date-range db query-params)) chg)
