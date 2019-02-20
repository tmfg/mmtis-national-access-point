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
            [ote.db.tx :as tx])
  (:import (java.time LocalDate DayOfWeek)))

(def ^:const no-traffic-detection-threshold
  "The amount of days after a no-traffic run is detected as a change."
  16)

(defqueries "ote/transit_changes/detection.sql")
(defqueries "ote/services/transit_changes.sql")

(defn get-gtfs-packages [db service-id package-count]
  (let [id-map (map :gtfs/id (specql/fetch db :gtfs/package
                                           #{:gtfs/id}
                                           {:gtfs/transport-service-id service-id
                                            :gtfs/deleted? false}
                                           {:specql.core/order-by        :gtfs/id
                                            :specql.core/order-direction :desc
                                            :specql.core/limit           package-count}))
        id-vector (vec (sort < id-map))]
    id-vector))

(defn calculate-package-hashes-for-service [db service-id package-count]
  (let [package-ids (get-gtfs-packages db service-id package-count)]
    (log/info "Found " (count package-ids) " For service " service-id)
    (doall
      (for [package-id package-ids]
        (do
          (log/info "Generating hashes for package " package-id "  (service " service-id ")")
          (generate-date-hashes db {:package-id package-id})
          (log/info "Generation ready! (package " package-id " service " service-id ")"))))))

(defn db-route-detection-type [db service-id]
  (let [type (first (specql/fetch db :gtfs/detection-service-route-type
                                  #{:gtfs/route-hash-id-type}
                                  {:gtfs/transport-service-id service-id}))]
    ;; If type is saved to database return it. If not, return default "short-long-headsign"
    (if type
      (:gtfs/route-hash-id-type type)
      "short-long-headsign")))

(defn calculate-route-hash-id-for-service
  "We support only few different hash calculation types. [short-long-headsign, short-long, route-id]"
  [db service-id package-count type]
  (let [package-ids (get-gtfs-packages db service-id package-count)]
    ;; Delete / insert type to db
    (specql/delete! db :gtfs/detection-service-route-type {:gtfs/transport-service-id service-id})
    (specql/insert! db :gtfs/detection-service-route-type
                    {:gtfs/transport-service-id service-id
                     :gtfs/route-hash-id-type type})
    (doall
      ;; Calculate route-hash-id:s again to detection-route table for given service.
      (for [package-id package-ids]
        (cond
          (= type "short-long-headsign")
          (calculate-routes-route-hashes-using-headsign db {:package-id package-id})
          (= type "short-long")
          (calculate-routes-route-hashes-using-short-and-long db {:package-id package-id})
          (= type "route-id")
          (calculate-routes-route-hashes-using-route-id db {:package-id package-id})
          (= type "long-headsign")
          (calculate-routes-route-hashes-using-long-headsign db {:package-id package-id})
          (= type "long")
          (calculate-routes-route-hashes-using-long db {:package-id package-id})
          :else
          (calculate-routes-route-hashes-using-headsign db {:package-id package-id}))))))

(defn routes-by-date [date-route-hashes all-routes type]
  ;; date-route-hashes contains all hashes for date range and is sorted
  ;; by date so we can partition by :date to get each date's hashes
  (for [hashes-for-date (partition-by :date date-route-hashes)
        :let [date (:date (first hashes-for-date))
              route-hashes (into (zipmap all-routes (repeat nil))
                                   (map (juxt :route-hash-id :hash))
                                   hashes-for-date)
              cleaned-route-hashes (dissoc route-hashes
                                           ;; If there is no traffic for the service on a given date, the
                                           ;; result set will contain a single row with nil values for route.
                                           ;; Remove the empty-route-key so we don't get an extra route.
                                           "" nil)]]
    {:date   (.toLocalDate date)
     :routes cleaned-route-hashes}))

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
  [{:keys [starting-week-hash] :as state} [prev curr next1 next2] route]
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
           (not (week= starting-week-hash next1))
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

      ;; Current week ends in no traffic, start new run
      (pos? end-run)
      (-> state
          (assoc :no-traffic-run end-run)
          (dissoc :no-traffic-start-date))

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

    :default
    state))

(defn- route-next-different-week
  [{diff :different-week no-traffic-end-date :no-traffic-end-date :as state} route weeks curr]
  (if (or diff no-traffic-end-date)
    ;; change already found, don't try again
    state

    ;; Change not yet found, try to find one
    (let [route-week-hashes (mapv (comp #(get % route) :routes)
                            weeks)
          result (-> state
                     ;; Detect no-traffic run
                     (detect-no-traffic-run route-week-hashes)
                     (add-no-traffic-run-dates curr)

                     ;; Detect other traffic changes
                     (detect-change-for-route route-week-hashes route)
                     (add-starting-week curr)
                     (add-different-week curr))]
      result)))


(defn first-week-difference
  "Detect the next different week in each route. Takes a list of weeks that have week hashes for each route.
  Returns map from route [short long headsign] to next different week info.
  The route-weeks maps have keys :beginning-of-week, :different-week and :routes, under :routes there is a map with route-name -> 7-vector with day hashes of the week"
  [route-weeks]
  ;; Take routes from the first week (they are the same in all weeks)
  (let [route-names (into #{}
                          (map first)
                          (:routes (first route-weeks)))
        result (reduce
                 (fn [route-detection-state [_ curr _ _ :as weeks]]
                   (reduce
                     (fn [route-detection-state route]
                       (update route-detection-state route
                               route-next-different-week route weeks curr))
                     route-detection-state route-names))
                 {}                                                    ; initial route detection state is empty
                 (partition 4 1 route-weeks))]
    result))

(defn week-difference-pairs [route-weeks]
  ;; should loop and start where previous iter end
  (loop [diff (first-week-difference route-weeks)
         results nil]
    (let [results nil
          new-results nil
          orig-end nil]
      (if (:start diff)
        (recur {:start (:end diff) :end (:end orig-end)} (conj results new-results))
        ;; else     
        ))))

(defn route-trips-for-date [db service-id route-hash-id date]
  (vec
    (for [trip-stops (partition-by (juxt :package-id :trip-id)
                                   (fetch-route-trips-for-date db {:service-id service-id
                                                                   :route-hash-id route-hash-id
                                                                   :date date}))
          :let [package-id (:package-id (first trip-stops))
                trip-id (:trip-id (first trip-stops))]]
      {:gtfs/package-id package-id
       :gtfs/trip-id    trip-id
       :stoptimes       (mapv (fn [{:keys [stop-id stop-name departure-time stop-sequence stop-lat stop-lon]}]
                                {:gtfs/stop-id        stop-id
                                 :gtfs/stop-name      stop-name
                                 :gtfs/stop-lat       stop-lat
                                 :gtfs/stop-lon       stop-lon
                                 :gtfs/stop-sequence  stop-sequence
                                 :gtfs/departure-time (time/pginterval->interval departure-time)})
                              trip-stops)})))


(defn compare-selected-trips [date1-trips date2-trips starting-week-date different-week-date]
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
                        changed)}))

(defn compare-route-days [db service-id route-hash-id
                          {:keys [starting-week starting-week-hash
                                  different-week different-week-hash] :as r}]
  (let [first-different-day (transit-changes/first-different-day starting-week-hash
                                                                 different-week-hash)
        starting-week-date (.plusDays (:beginning-of-week starting-week) first-different-day)
        different-week-date (.plusDays (:beginning-of-week different-week) first-different-day)]
    (log/debug "Found changes in trips for route: " route-hash-id ", comparing dates: " starting-week-date " and " different-week-date " route-hash-id " route-hash-id)
    (let [date1-trips (route-trips-for-date db service-id route-hash-id starting-week-date)
          date2-trips (route-trips-for-date db service-id route-hash-id different-week-date)]
      (compare-selected-trips date1-trips date2-trips starting-week-date different-week-date))))

(defn route-day-changes
  "Takes in routes with possible different weeks and adds day change comparison."
  [db service-id routes]
  (let [route-day-changes
        (into {}
              (map (fn [[route {diff :different-week :as detection-result}]]
                     (if diff
                       ;; If a different week was found, do detailed trip analysis
                       (do
                         ;(println "Print differences for route " (pr-str route) (pr-str diff) "\n detection-result: " (pr-str detection-result))
                         [route (assoc detection-result
                                  :changes (compare-route-days db service-id route detection-result))])

                       ;; Otherwise return as is
                       [route detection-result])))
              routes)]
    route-day-changes))

(defn- date-in-the-past? [^LocalDate date]
  (and date
       (.isBefore date (java.time.LocalDate/now))))

(defn- max-date-within-90-days? [{max-date :max-date}]
  (and max-date
       (.isBefore (.toLocalDate max-date) (.plusDays (java.time.LocalDate/now) 90))
       (.isAfter (.toLocalDate max-date) (.minusDays (java.time.LocalDate/now) 1)))) ; minus 1 day so we are sure the current day is still calculated

(defn- min-date-in-the-future? [{min-date :min-date}]
  (and min-date
       (.isAfter (.toLocalDate min-date) (java.time.LocalDate/now))))

(defn update-min-max-range [range val]
  (-> range
      (update :lower #(if (or (nil? %) (< val %)) val %))
      (update :upper #(if (or (nil? %) (> val %)) val %))))

(defn- week-day-in-week [date week-day]
  (.plusDays date (- (.getValue week-day)
                     (.getValue (.getDayOfWeek date)))))

(defn- sql-date [local-date]
  (when local-date
    (java.sql.Date/valueOf local-date)))

(defn discard-past-changes
  "Discard past changes by returning a :no-change"
  [{type :gtfs/change-type change-date :gtfs/change-date :as change}]
  (if (and
        change-date
        (.isBefore (.toLocalDate change-date) (java.time.LocalDate/now))
        (not= :removed type))
    {:gtfs/change-type :no-change
     :gtfs/change-date nil}
    change))

(defn transform-route-change
  "Transform a detected route change into a database 'gtfs-route-change-info' type."
  [all-routes route-key
   {:keys [no-traffic-start-date no-traffic-end-date
           starting-week different-week no-traffic-run
           changes]}]

  (let [route-map  (map #(second %) all-routes)
        route (first (filter #(= route-key (:route-hash-id %)) route-map))
        added? (min-date-in-the-future? route)
        removed? (max-date-within-90-days? route)
        no-traffic? (and no-traffic-start-date
                         (or no-traffic-end-date
                             (and (> no-traffic-run no-traffic-detection-threshold)
                                  (.isAfter no-traffic-start-date (.toLocalDate (:max-date route))))))
        max-date-in-past? (.isBefore (.toLocalDate (:max-date route)) (java.time.LocalDate/now))
        {:keys [starting-week-date different-week-date
                added-trips removed-trips trip-changes]} changes
        changed? (and starting-week-date different-week-date)
        trip-stop-seq-changes (reduce update-min-max-range
                                      {}
                                      (map :stop-seq-changes trip-changes))
        trip-stop-time-changes (reduce update-min-max-range
                                       {}
                                       (map :stop-time-changes trip-changes))]

    (merge
      {;; Route identification
       :gtfs/route-short-name (:route-short-name route)
       :gtfs/route-long-name (:route-long-name route)
       :gtfs/trip-headsign (:trip-headsign route)
       :gtfs/route-hash-id (:route-hash-id route)

       ;; Trip change counts
       :gtfs/added-trips added-trips
       :gtfs/removed-trips removed-trips
       :gtfs/trip-stop-sequence-changes-lower (:lower trip-stop-seq-changes)
       :gtfs/trip-stop-sequence-changes-upper (:upper trip-stop-seq-changes)
       :gtfs/trip-stop-time-changes-lower (:lower trip-stop-time-changes)
       :gtfs/trip-stop-time-changes-upper (:upper trip-stop-time-changes)}

     ;; Change type and type specific dates
     (discard-past-changes
       (cond

         max-date-in-past?                                  ; Done because some of the changes listed in transit changes pages are actually in the past
         {:gtfs/change-type         :no-change}

         added?
         {:gtfs/change-type         :added

         ;; For an added route, the change-date is the date when traffic starts
         :gtfs/different-week-date (:min-date route)
         :gtfs/change-date         (:min-date route)
         :gtfs/current-week-date   (sql-date (.plusDays (.toLocalDate (:min-date route)) -1))}

         removed?
         {:gtfs/change-type         :removed
          ;; For a removed route, the change-date is the day after traffic stops
          ;; BUT: If removed? is identified and route ends before current date, set change date as nil so we won't analyze this anymore.
         :gtfs/change-date (if (.isBefore (.toLocalDate (sql-date (.toLocalDate (:max-date route)))) (java.time.LocalDate/now))
                             nil
                             (sql-date (.plusDays (.toLocalDate (:max-date route)) 1)))

         :gtfs/different-week-date (sql-date (.plusDays (.toLocalDate (:max-date route)) 1))
         :gtfs/current-week-date   (:max-date route)}

         changed?
         {:gtfs/change-type         :changed
         :gtfs/current-week-date   (sql-date starting-week-date)
         :gtfs/different-week-date (sql-date different-week-date)
         :gtfs/change-date         (sql-date different-week-date)}

         no-traffic?
         {:gtfs/change-type :no-traffic

          ;; If the change is a no-traffic period, the different day is the first day that has no traffic
          :gtfs/current-week-date (sql-date
                                    (.plusDays no-traffic-start-date -1))
          ;; If no-traffic starts in future set different-week-day the day when no-traffic segment starts
          ;; if no-traffic segment is in the past (or currenlty on progress) set different-week-date when the traffic starts again
          :gtfs/different-week-date (if (.isBefore (.toLocalDate (sql-date no-traffic-start-date)) (.plusDays (java.time.LocalDate/now) 1))
                                      (sql-date no-traffic-end-date)
                                      (sql-date no-traffic-start-date))
          ;; If no-traffic is identified and no-traffic segment has begun before or at current date, set change date where the traffic starts again.
          :gtfs/change-date (if (.isBefore (.toLocalDate (sql-date no-traffic-start-date)) (.plusDays (java.time.LocalDate/now) 1))
                              (sql-date no-traffic-end-date) ;; Day when traffic starts again
                              (sql-date no-traffic-start-date))}

         :default
         {:gtfs/change-type :no-change})))))

(defn- debug-print-change-stats [all-routes route-changes type]
  (doseq [r all-routes
          :let [key (:route-hash-id r)
                {:keys [changes no-traffic-start-date no-traffic-end-date]
                 :as   route} (route-changes key)]]
    (println key " has traffic " (:min-date r) " - " (:max-date r)
             (when no-traffic-end-date
               (str " no traffic between: " no-traffic-start-date " - " no-traffic-end-date))
             (when changes
               (str " has changes")))))

(defn store-transit-changes! [db today service-id package-ids {:keys [all-routes route-changes]}]
  (let [type (db-route-detection-type db service-id)
        route-change-infos (map (fn [[route-key detection-result]]
                                  (transform-route-change all-routes route-key detection-result))
                                route-changes)
        change-count-by-type (fmap count (group-by :gtfs/change-type route-change-infos))
        earliest-route-change (first (drop-while (fn [{:gtfs/keys [change-date]}]
                                                   ;; Remove change-date from the route-changes-infos list if it is nil or it is in the past
                                                   (or (nil? change-date)
                                                       (date-in-the-past? (.toLocalDate change-date))))
                                                 (sort-by :gtfs/change-date route-change-infos)))
        ;; Set change date to future (1 week) if it is nil or it is too far in the future
        new-change-date (if (or
                              (nil? (:gtfs/change-date earliest-route-change))
                              (.isAfter (.toLocalDate (:gtfs/change-date earliest-route-change)) (.plusDays (java.time.LocalDate/now) 30)))
                          (sql-date (.plusDays (java.time.LocalDate/now) 7))
                          (:gtfs/change-date earliest-route-change))]
    #_(debug-print-change-stats all-routes route-changes type)
    (tx/with-transaction db
                         (specql/upsert! db :gtfs/transit-changes
                                         #{:gtfs/transport-service-id :gtfs/date}
                                         {:gtfs/transport-service-id service-id

                                          :gtfs/date today
                                          :gtfs/change-date new-change-date
                                          :gtfs/different-week-date (:gtfs/different-week-date earliest-route-change)
                                          :gtfs/current-week-date (:gtfs/current-week-date earliest-route-change)

                                          :gtfs/removed-routes (:removed change-count-by-type 0)
                                          :gtfs/added-routes (:added change-count-by-type 0)
                                          :gtfs/changed-routes (:changed change-count-by-type 0)
                                          :gtfs/no-traffic-routes (:no-traffic change-count-by-type 0)

                                          :gtfs/package-ids package-ids
                                          :gtfs/created (java.util.Date.)})
                         (doseq [r route-change-infos]
                           (let [update-count (specql/update! db :gtfs/detected-route-change
                                                              (merge {:gtfs/transit-change-date (sql-date today)
                                                                      :gtfs/transit-service-id service-id
                                                                      :gtfs/created-date (java.util.Date.)}
                                                                     r)
                                                              {:gtfs/transit-change-date (sql-date today)
                                                               :gtfs/transit-service-id service-id
                                                               :gtfs/route-hash-id (:gtfs/route-hash-id r)})]

                             (when (not= 1 update-count)
                               (specql/insert! db :gtfs/detected-route-change
                                               (merge {:gtfs/transit-change-date (sql-date today)
                                                       :gtfs/transit-service-id service-id
                                                       :gtfs/created-date (java.util.Date.)}
                                                      r))))))))

(defn override-static-holidays [date-route-hashes]
  (map (fn [{:keys [date] :as row}]
         (if-let [holiday-id (transit-changes/is-holiday? date)]
           (assoc row :hash holiday-id)
           row))
       date-route-hashes))

(defn- set-route-hash-id [_ route type]
  (let [short (:route-short-name route)
        long (:route-long-name route)
        headsign (:trip-headsign route)]
    (cond
      (= type "short-long-headsign")
        (str short "-" long "-" headsign)
      (= type "short-long")
        (str short "-" long)
      (= type "long-headsign")
        (str long "-" headsign)
      (= type "long")
        (str long)
      :else
        (str short "-" long "-" headsign))))

(defn add-route-hash-id-as-a-map-key
  "Add default route-hash-id (long-short-headsign) to routes"
  [routes type]
  (map
    (fn [x]
      (update x :route-hash-id #(set-route-hash-id % x type)))
    routes))

(defn map-by-route-key [service-routes type]
  (let [service-routes (if (empty? (:route-hash-id (first service-routes)))
                         (add-route-hash-id-as-a-map-key service-routes type)
                         service-routes)]
    (sort-by :route-hash-id (map-by :route-hash-id service-routes))))

(defn detect-route-changes-for-service [db {:keys [start-date service-id] :as route-query-params}]
  (let [type (db-route-detection-type db service-id)
        ;; Generate "key" for all routes. By default it will be a vector ["<route-short-name>" "<route-long-name" "trip-headsign"]
        service-routes (sort-by :route-hash-id (service-routes-with-date-range db {:service-id service-id}))
        all-routes (map-by-route-key service-routes type)
        all-route-keys (set (keys all-routes))
        ;; Get route hashes from database
        route-hashes (service-route-hashes-for-date-range db route-query-params)
        ;; Change hashes that are at static holiday to a keyword
        route-hashes-with-holidays (override-static-holidays route-hashes)
        routes-by-date (routes-by-date route-hashes-with-holidays all-route-keys type)]
    (try
      {:all-routes all-routes
       :route-changes
       (->> routes-by-date ;; Format of routes-by-date is: [{:date routes(=hashes)}]
            ;; Create week hashes so we can find out the differences between weeks
            (combine-weeks)
            ;; Search next week (for every route) that is different
            (first-week-difference)
            ;; Fetch detailed route comparison if a change was found
            (route-day-changes db service-id))}
      (catch Exception e
        (log/warn e "Error when detectin route changes using route-query-params: " route-query-params " service-id:" service-id)))))

(defn- update-hash [old x]
  (let [short (:gtfs/route-short-name x)
        long (:gtfs/route-long-name x)
        headsign (:gtfs/trip-headsign x)]
    (str short "-" long "-" headsign)))

(defn service-package-ids-for-date-range [db query-params]
  (mapv :id (service-packages-for-date-range db query-params)))

;; This is only for local development
;; Add route-hash-id for all routes in gtfs-transit-changes table in column route-hashes.
(defn update-date-hash-with-null-route-hash-id [db service-id]
  (let [transit-changes (specql/fetch db :gtfs/transit-changes
                                      (specql/columns :gtfs/transit-changes)
                                      {:gtfs/transport-service-id service-id})]
    (for [t transit-changes]
      (let [route-changes (specql/fetch db :gtfs/detected-route-change
                                        (specql/columns :gts/detected-route-change)
                                        {:gtfs/transit-change-date (:gtfs/date transit-changes)
                                         :gtfs/transit-service-id service-id})
            chg-routes (map (fn [x]
                              (update x :gtfs/route-hash-id #(update-hash % x))) route-changes)]
        (specql/update! db :gtfs/transit-changes
                        {:gtfs/transport-service-id service-id
                         :gtfs/date (:gtfs/date t)}
                        ;; where
                        {:gtfs/transport-service-id service-id
                         :gtfs/date (:gtfs/date t)})
        (doseq [r route-changes]
          (specql/update! db :gtfs/detected-route-change
                          r
                          {:gtfs/transport-service-id service-id
                           :gtfs/transit-change-date (:gtfs/date t)
                           :gtfs/route-short-name (:gtfs/route-short-name r)
                           :gtfs/route-long-name (:gtfs/route-long-name r)
                           :gtfs/trip-headsign (:gtfs/trip-headsign r)}))
        (println "service id " service-id "date " (:gtfs/date t))))))

;;Use only in local environment and for debugging purposes!
(defn update-route-hashes [db]
  (let [service-ids (fetch-distinct-services-from-transit-changes db)]
    (doall
      (for [id service-ids]
        (update-date-hash-with-null-route-hash-id db (:id id))))))


;; Do not use this if you don't need to.
;; This is helper function for local development. It will calculate route-hash-id to gtfs-date-hash table for the given
;; package-id. Running time of this function is quite long. (3-10minutes)
;; TODO: Create function that takes service-id as parameter and calls this function with package-id's that belongs to given service-id
(defn generate-gtfs-date-hash-for-package [db package-id]
  (let [hashes (specql/fetch db :gtfs/date-hash
                             (specql/columns :gtfs/date-hash)
                             {:gtfs/package-id package-id})]
    (doall
      (for [h hashes]
        (let [route-hashes (:gtfs/route-hashes h)
              chg-hashes (map (fn [x]
                                (update x :gtfs/route-hash-id #(update-hash % x))) route-hashes)]
            (when (and route-hashes (:gtfs/hash h))
              (specql/update! db :gtfs/date-hash
                              {:gtfs/date (:gtfs/date h)
                               :gtfs/package-id package-id
                               :gtfs/route-hashes chg-hashes
                               :gtfs/modified (java.util.Date.)}
                              ; where
                              {:gtfs/package-id package-id
                               :gtfs/date (:gtfs/date h)})
              (println "package-id " package-id "date " (:gtfs/date h))))))))
