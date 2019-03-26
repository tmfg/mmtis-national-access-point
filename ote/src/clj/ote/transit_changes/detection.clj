(ns ote.transit-changes.detection
  "Detect changes in transit traffic patterns.
  Interfaces with stored GTFS transit data."
  (:require [ote.transit-changes :as transit-changes :refer [week=]]
            [ote.time :as time]
            [jeesql.core :refer [defqueries]]
            [taoensso.timbre :as log]
            [specql.core :as specql]
            [clojure.spec.alpha :as spec]
            [specql.op :as op]
            [ote.db.user :as user]
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
                                           {:specql.core/order-by :gtfs/id
                                            :specql.core/order-direction :desc
                                            :specql.core/limit package-count}))
        id-vector (vec (sort < id-map))]
    id-vector))

(defn hash-recalculations
  "List currently running hash-recalculations"
  [db]
  (let [calculations (specql/fetch db :gtfs/hash-recalculation
                                   (specql/columns :gtfs/hash-recalculation)
                                   {:gtfs/completed op/null?})]
    (when (pos-int? (count calculations))
       {:calculations calculations})))

(defn reset-last-hash-recalculations
  "Reset currently running hash-recalculations. Should only be used if caluclation is stuck."
  [db]
  (let [last-calculation (first (specql/fetch db :gtfs/hash-recalculation
                                              (specql/columns :gtfs/hash-recalculation)
                                              {:gtfs/completed              op/null?}
                                              {:specql.core/order-by        :gtfs/recalculation-id
                                               :specql.core/order-direction :desc
                                               :specql.core/limit           1}))]
    (when last-calculation
      (specql/delete! db :gtfs/hash-recalculation {:gtfs/recalculation-id (:gtfs/recalculation-id last-calculation)}))))

 (defn- start-hash-recalculation [db packets-total user]
  (let [id (specql/insert! db :gtfs/hash-recalculation
                  {:gtfs/started (java.sql.Timestamp. (System/currentTimeMillis))
                   :gtfs/packets-ready 0
                   :gtfs/packets-total packets-total
                   :gtfs/completed nil
                   :gtfs/created-by (:id user) })]
    id))

 (defn- update-hash-recalculation [db packages-ready id]
  (specql/update! db :gtfs/hash-recalculation
                  {:gtfs/packets-ready packages-ready}
                 {:gtfs/recalculation-id id}))

 (defn- stop-hash-recalculation [db id]
  (specql/update! db :gtfs/hash-recalculation
                 {:gtfs/completed (java.sql.Timestamp. (System/currentTimeMillis))}
                 {:gtfs/recalculation-id id}))

(defn calculate-package-hashes-for-service [db service-id package-count user]
  (let [package-ids (get-gtfs-packages db service-id package-count)
        ;; When given service has packages, mark calculation started
        recalculation-id (when package-ids
                           (:gtfs/recalculation-id (start-hash-recalculation db package-count user)))]
    (log/info "Found " (count package-ids) " For service " service-id)

    (dotimes [i (count package-ids)]
      (let [package-id (nth package-ids i)]
          (log/info "Generating hashes for package " package-id "  (service " service-id ")")
          (generate-date-hashes db {:package-id package-id})
          (update-hash-recalculation db (inc i) recalculation-id)
          (log/info "Generation ready! (package " package-id " service " service-id ")")))
    (stop-hash-recalculation db recalculation-id)))

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
    {:date (.toLocalDate date)
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

(defn- vnot [cond msg]
  #_(when cond
    (println "debug: not a change because" msg)
    )
  (not cond))

(defn detect-change-for-route
  "Reduces [prev curr next1 next2] weeks into a detection state change"
  [{:keys [starting-week-hash] :as state} [prev curr next1 next2] route]
  (cond
    ;; If this is the first call and the current week is "anomalous".
    ;; Then start at the next week.
    (and (nil? starting-week-hash)
         (not (week= curr next1))
         (week= prev next1))
    {} ;; Ignore this week

    ;; No starting week specified yet, use current week
    (nil? starting-week-hash)
    (assoc state :starting-week-hash curr)

    ;; If current week does not equal starting week...
    (and (vnot (week= starting-week-hash curr) (str "curr = start (1) sw:" starting-week-hash " curr:" curr))
         (vnot (week= starting-week-hash next1) "curr = next1 (2)")
         ;; ...and traffic does not revert back to previous in two weeks
         (vnot (week= starting-week-hash next2) "curr = next2 (3)"))
    ;; this is a change
    (assoc state :different-week-hash curr)

    ;; No change found, return state as is
    :default
    state))

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

(defn add-no-traffic-run-dates-old [{:keys [no-traffic-run no-traffic-change
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

(defn add-no-traffic-run-dates-new
  "
  state: map where analysis results per route and week are concatenated iteratively
  week: the week being analysed during this call,
  last-analysis-wk: week map of the last week where analysis will be carried out"
  [{:keys [no-traffic-run no-traffic-change
                                        no-traffic-start-date] :as state} week last-analysis-wk]

  (cond

    ;; Dissoc'd no-traffic keys because if normal week has traffic on weekdays and nil on last day,
    ;; analysis reports a started no-traffic run even if it actually ends on next day. This happens because analysis
    ;; doesn't run on the last weeks of the x week window.
    ;; Refer to test test-no-changes-weekend-nil
    (and no-traffic-run (nil? no-traffic-start-date) (= week last-analysis-wk))
    (dissoc state :no-traffic-start-date :no-traffic-run)

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

(defn- route-next-different-week-new
  [{diff :different-week no-traffic-end-date :no-traffic-end-date :as state} route weeks curr last-analysis-wk]
  ;; (println "route-next-different-week called with curr= " curr)
  (if (or diff no-traffic-end-date)
    ;; change already found, don't try again
    state

    ;; Change not yet found, try to find one
    (let [route-week-hashes (mapv (comp #(get % route) :routes)
                            weeks)
          result (-> state
                     (assoc :route-key route)
                     ;; Detect no-traffic run
                     (detect-no-traffic-run route-week-hashes)
                     (add-no-traffic-run-dates-new curr last-analysis-wk)

                     ;; Detect other traffic changes
                     (detect-change-for-route route-week-hashes route)
                     (add-starting-week curr)
                     (add-different-week curr))]
      #_(if-let [dw (:different-week result)]
        (println "route-next-different-week found something:" dw )
        (println "no changes found from:" (:beginning-of-week (first  weeks))))
      result)))

(defn- route-next-different-week-old
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
                     (add-no-traffic-run-dates-old curr)

                     ;; Detect other traffic changes
                     (detect-change-for-route route-week-hashes route)
                     (add-starting-week curr)
                     (add-different-week curr))]
      result)))

(spec/def
 ::routes
 (spec/map-of
  string?
  (spec/coll-of (spec/or :keyword keyword? :string (spec/nilable string?)))))
(spec/def ::local-date #(instance? java.time.LocalDate %))
(spec/def ::end-of-week ::local-date)
(spec/def ::beginning-of-week ::local-date)

(spec/def
 ::route-week
 (spec/keys :req-un [::beginning-of-week ::end-of-week ::routes]))
(spec/def
 ::route-weeks-vec
  (spec/coll-of ::route-week))

(spec/def ::bow-eow-map (spec/keys :req-un [::beginning-of-week ::end-of-week]))
(spec/def
  ::different-week
  ::bow-eow-map)
(spec/def
 ::week-hash-vec
  (spec/coll-of (spec/or :keyword keyword? :string string? :nil nil?)))
(spec/def
 ::different-week-hash
  ::week-hash-vec)
(spec/def
 ::starting-week
 ::bow-eow-map)
(spec/def ::starting-week-hash ::week-hash-vec)

(spec/def ::route-change-map
  (spec/keys
    :req-un
    [::different-week
     ::different-week-hash
     ::starting-week
     ::starting-week-hash]))

(spec/def ::route-key (spec/every string? :count 3))

(spec/def
  ::single-route-change
  (spec/coll-of (spec/tuple ::route-key ::route-change-map) :kind map?))

(spec/fdef route-weeks-with-first-difference-old
           :args (spec/cat :rw ::route-weeks-vec)
           :ret ::single-route-change)

(spec/def ::route-key string?)

(spec/def ::service-route-change-map
  (spec/keys
    :req-un
    [::route-key
     ::starting-week
     ::starting-week-hash]
    :opt-un
    [::changes
     ::different-week
     ::different-week-hash]))

(spec/def
  ::detected-route-changes-for-services-coll
  (spec/coll-of ::service-route-change-map :kind vector?))

(defn route-weeks-with-first-difference-old
  "Detect the next different week in each route.

  NOTE! starting from the seond week in the given route-weeks, the first given week is considered the \"prev\" week.
  
  Takes a list of weeks that have week hashes for each route.
  Returns map from route [short long headsign] to next different week info.
  The route-weeks maps have keys :beginning-of-week, :end-of-week and :routes, under :routes there is a map with route-name -> 7-vector with day hashes of the week"
  [route-weeks]
  #_(println "first-week-difference called, #weeks:" (count route-weeks))
  ;(if (= 7  (count route-weeks))
  ;   (def *r7 route-weeks))
  ;; (println "spec for route-weeks:")
  ;; (spec-provider.provider/pprint-specs (spec-provider.provider/infer-specs route-weeks ::route-weeks) 'ote.transit-changes.detection 'spec)
  ;; Take routes from the first week (they are the same in all weeks)
  (let [route-names (into #{}
                          (map first)
                          (:routes (first route-weeks)))
        result (reduce
                 (fn [route-detection-state [_ curr _ _ :as weeks]]
                   (reduce
                     (fn [route-detection-state route]
                       ;; value under route key in r-d-s map will be updated by
                       ;; (route-next-different-week *value* route weeks curr)
                       (update route-detection-state route
                               route-next-different-week-old route weeks curr))
                     route-detection-state route-names))
                 {}    ; initial route detection state is empty
                 (partition 4 1 route-weeks))]
    ;; (println "first-week-difference result: " (pr-str result))
    ;; (spec-provider.provider/pprint-specs (spec-provider.provider/infer-specs result ::route-differences-pair) 'ote.transit-changes.detection 'spec)
    result))


(defn route-weeks-with-first-difference-new
  "Detect the next different week in each route.
  NOTE! starting from the second week in the given route-weeks, the first given week is considered the \"prev\" week.
  Takes a list of weeks that have week hashes for each route.
  Returns map from route [short long headsign] to next different week info.
  The route-weeks maps have keys :beginning-of-week, :end-of-week and :routes, under :routes there is a map with route-name -> 7-vector with day hashes of the week"
  [route-weeks]
  ;(if (= 7  (count route-weeks))
  ;   (def *r7 route-weeks))
  ;; (println "spec for route-weeks:")
  ;; (spec-provider.provider/pprint-specs (spec-provider.provider/infer-specs route-weeks ::route-weeks) 'ote.transit-changes.detection 'spec)
  ;; Take routes from the first week (they are the same in all weeks)
  (let [route-names (into #{}
                          (map first)
                          (:routes (first route-weeks)))
        result (reduce
                 (fn [route-detection-state [_ curr _ _ :as weeks]]
                   (reduce
                     (fn [route-detection-state route]
                       ;; value under route key in r-d-s map will be updated by
                       ;; (route-next-different-week *value* route weeks curr)
                       (update route-detection-state route
                               route-next-different-week-new route weeks curr (first (take-last 3 route-weeks))))
                     route-detection-state
                     route-names))
                 {}    ; initial route detection state is empty
                 (partition 4 1 route-weeks))]
    ;; (println "first-week-difference result: " (pr-str result))
    ;; (spec-provider.provider/pprint-specs (spec-provider.provider/infer-specs result ::route-differences-pair) 'ote.transit-changes.detection 'spec)
    (vals result)))


(defn local-date-before? [d1 d2]
  (.isBefore d1 d2))

(defn local-date-after? [d1 d2]
  (clj-time.core/after? (time/native->date-time d1) (time/native->date-time d2)))

(defn route-starting-week-past-date? [rw date]
  #_(println "route-starting-week-past-date? (:beginning-of-week rw)=" (:beginning-of-week rw) " date=" date)
  (assert (some? date))
  (assert (some? rw))
  (assert (some? (:beginning-of-week rw)) rw)
  (local-date-after? (:beginning-of-week rw) date))

(defn route-starting-week-not-before?
  "rw: route week, date: localdate
  Returns true if `rw`s key is before `date`."
  [rw date]
  (assert (some? date))
  (assert (some? rw))
  (assert (some? (:beginning-of-week rw)) rw)
  (not (local-date-before? (:beginning-of-week rw) date)))

(defn filter-by-vals [pred map]
  (into {} (for [[k v] map
                 :when (pred v)]
             [k v])))

(def new-data-example [{:starting-week :foo
                        :different-week :foo }])

(defn route-differences
  "
  Takes a vector of weeks for one route and outputs vector of weeks where change or no traffic starts
  (Or if neither is found, returns the starting week of analysis)

  Input: [{:beginning-of-week #object[java.time.LocalDate 0x3f51d3c0 \"2019-02-11\"],
          :end-of-week #object[java.time.LocalDate 0x30b5f64f \"2019-02-17\"],
          :routes {\"routename\" [\"h1\" \"h2\" \"h3\" \"h4\" \"h5\" \"h6\" \"h7\"]}}
          {...}]

  Output: [{:different-week
            {:beginning-of-week [\"2019-02-25\"]
            :end-of-week #object[java.time.LocalDate 0x5a900751 \"2019-03-03\"]}
           :route-key \"routename\"
           :different-week-hash [\"h1\" \"!!\" \"h3\" \"h4\" \"h5\" \"h6\" \"h7\"]\n
           :starting-week {:beginning-of-week #object[java.time.LocalDate   \"2019-02-11\"]
                            :end-of-week #object[java.time.LocalDate \"2019-02-17\"]}
           :starting-week-hash [\"h1\" \"h2\" \"h3\" \"h4\" \"h5\" \"h6\" \"h7\"]}]
           {...}"
  [route-weeks]
  (loop [route-weeks route-weeks
         results []]
    (let [diff-data (route-weeks-with-first-difference-new route-weeks)
          filtered-diff-data (first (filter (fn [value]
                                              (or (:no-traffic-start-date value) (:different-week value)))
                                            diff-data))
          diff-week-beginnings (keep (comp :beginning-of-week :different-week) diff-data)
          no-traffic-end (:no-traffic-end-date (first diff-data))
          diff-week-date (first diff-week-beginnings)
          prev-week-date (when (or diff-week-date no-traffic-end)
                           (.minusWeeks (or diff-week-date no-traffic-end) 1))]
      ;(println "filtered-diff-data: " (pr-str filtered-diff-data))
      ;(println "diff-data: " (pr-str diff-data))
      ;(println "no-traffic-end: " (pr-str no-traffic-end))

      ;(println "diff-week-beginnings: " (pr-str diff-week-beginnings))
      ;(println "diff-week-date: " (pr-str diff-week-date) " typeof=" (type diff-week-date))
      ;(assert (= (count diff-week-beginnings) (count (set diff-week-beginnings))))
      (if (and (not-empty diff-data) prev-week-date)   ;; end condition: dates returned by f-w-d had nil different-week beginning
        (recur
          ;; Filter out different weeks before current week, because different week is starting week for next change.
          ;; Use the previous week date, because first-week-difference starts comparisons at the second given week
          (filter #(route-starting-week-not-before? % prev-week-date) route-weeks)
          (conj results filtered-diff-data))
        (if (empty? results)
          diff-data
          results))))) ;; Default week data expected when no changes are found

(defn combine-differences-with-routes
  [route-weeks differences]

  (let [changed-route-weeks
        (loop [route-weeks route-weeks
               result []]
          (let [first-week (first route-weeks)
                routes (:routes first-week)])
          (if (empty? route-weeks)
            result
            (recur (rest route-weeks) result)))]))

(defn route-trips-for-date [db service-id route-hash-id date]
  (vec
    (for [trip-stops (partition-by (juxt :package-id :trip-id)
                                   (fetch-route-trips-for-date db {:service-id service-id
                                                                   :route-hash-id route-hash-id
                                                                   :date date}))
          :let [package-id (:package-id (first trip-stops))
                trip-id (:trip-id (first trip-stops))]]
      {:gtfs/package-id package-id
       :gtfs/trip-id trip-id
       :stoptimes (mapv (fn [{:keys [stop-id stop-name departure-time stop-sequence stop-lat stop-lon stop-fuzzy-lat stop-fuzzy-lon]}]
                          {:gtfs/stop-id stop-id
                           :gtfs/stop-name stop-name
                           :gtfs/stop-lat stop-lat
                           :gtfs/stop-lon stop-lon
                           :gtfs/stop-fuzzy-lat stop-fuzzy-lat
                           :gtfs/stop-fuzzy-lon stop-fuzzy-lon
                           :gtfs/stop-sequence stop-sequence
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
                  combined-trips)
        ;; When dealing with new routes there aren't traffic at date1-trips because traffic is starting
        ;; So calculate only new trips, no other changes or stops
        added-trip-count (when (and (nil? combined-trips) (pos-int? (count date2-trips)))
                      (count date2-trips))
        ;; When traffic is ending there isn't traffic at date2-trips vector. So calculate only ending trips.
        removed-trip-count (when (and (nil? combined-trips) (pos-int? (count date1-trips)))
                           (count date1-trips))]
    {:starting-week-date starting-week-date
     :different-week-date different-week-date
     :added-trips (if combined-trips (count added) added-trip-count)
     :removed-trips (if combined-trips (count removed) removed-trip-count)
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
    ;(log/debug "Found changes in trips for route: " route-hash-id ", comparing dates: " starting-week-date " and " different-week-date " route-hash-id " route-hash-id)
    (let [date1-trips (route-trips-for-date db service-id route-hash-id starting-week-date)
          date2-trips (route-trips-for-date db service-id route-hash-id different-week-date)]
      (compare-selected-trips date1-trips date2-trips starting-week-date different-week-date))))

(defn compare-route-days-all-changes-for-week [db service-id route-hash-id
                          {:keys [starting-week starting-week-hash
                                  different-week different-week-hash] :as r}]
  (let [changed-days (transit-changes/changed-days-of-week starting-week-hash different-week-hash)]
    (for [ix changed-days
          :let [starting-week-date (.plusDays (:beginning-of-week starting-week) ix)
                different-week-date (.plusDays (:beginning-of-week different-week) ix)
                date1-trips (route-trips-for-date db service-id route-hash-id starting-week-date)
                date2-trips (route-trips-for-date db service-id route-hash-id different-week-date)]
          :when (number? ix)]
      (compare-selected-trips date1-trips date2-trips starting-week-date different-week-date))))

(defn route-day-changes
  "Takes in routes with possible different weeks and adds day change comparison."
  [db service-id routes]
  #_(println routes)
  (let [route-day-changes
        (into {}
              (map (fn [[route {diff :different-week :as detection-result}]]
                     (if diff ;; If a different week was found, do detailed trip analysis
                       [route (assoc detection-result
                                :changes (compare-route-days db service-id route detection-result))]
                       [route detection-result])))
              routes)]
    route-day-changes))

(defn- expand-day-changes
  "Input: coll of maps each describing a week with traffic changes.
  Takes :changes coll from each map, removes it, and creates a new map to contain each of the elements in :changes coll.
  Output: returns a coll of maps, each describing one single changed day on a week.
  There may be multiple maps per one week if there are multiple changed days on the week."
  [detection-results]
  (reduce (fn [result detection]
            (if-let [changes (:changes detection)]
              (concat result (for [chg changes]
                               (assoc detection :changes chg)))
              (conj result detection)))
          []
          detection-results))

(defn route-day-changes-new
  "Takes a collection of routes and adds day change comparison details for those weeks which have :different-week"
  [db service-id routes]
  (let [route-day-changes
        (mapv (fn [{diff :different-week route-key :route-key :as detection-result}]
                (if diff ;; If a different week was found, do detailed trip analysis
                  (assoc detection-result
                    :changes (compare-route-days-all-changes-for-week db service-id route-key detection-result))
                  detection-result))
              routes)
        res (into [] (expand-day-changes route-day-changes))]
    res))

(defn- date-in-the-past? [^LocalDate date]
  (and date
       (.isBefore date (java.time.LocalDate/now))))

(defn- max-date-within-evaluation-window? [{max-date :max-date}]
  (and max-date
       (.isBefore (.toLocalDate max-date) (.plusDays (java.time.LocalDate/now) 180))
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

(spec/fdef transform-route-change
           :args (spec/cat :all-routes vector? :route-change ::service-route-change-map :route-changes-all ::detected-route-changes-for-services-coll))
(defn transform-route-change
  "Transform a detected route change into a database 'gtfs-route-change-info' type."
  [all-routes
   {:keys [no-traffic-start-date no-traffic-end-date
           starting-week different-week route-key no-traffic-run
           changes] :as route-change} route-changes-all]
  (spec/assert ::detected-route-changes-for-services-coll route-changes-all)
  (let [route-map (map second all-routes)
        route (first (filter #(= route-key (:route-hash-id %)) route-map))
        added? (min-date-in-the-future? route)
        route-changes-for-key (filter #(= route-key (:route-key %)) route-changes-all)
        last-route-change? (= route-change (last route-changes-for-key))
        ;; When there are multiple route change detections for a route which is also ending, only the last detection should be marked :removed instead of all
        removed? (and last-route-change? (max-date-within-evaluation-window? route))
        no-traffic? (and no-traffic-start-date
                         (or no-traffic-end-date
                             (and (pos? no-traffic-run)
                                  (.isBefore no-traffic-start-date (.toLocalDate (:max-date route))))))
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
    #_(println key " has traffic " (:min-date r) " - " (:max-date r)
             (when no-traffic-end-date
               (str " no traffic between: " no-traffic-start-date " - " no-traffic-end-date))
             (when changes
               (str " has changes")))))

(defn- update-route-changes! [db analysis-date service-id route-change-infos]
  {:pre [(some? analysis-date)
         (pos-int? service-id)]}
  ;; Previous detected route change rows deleted because design is, there can be one analysis run per day per service
  ;; and count or details of route change rows per analysis round might differ.
  (specql/delete! db :gtfs/detected-route-change
                  {:gtfs/transit-change-date analysis-date
                   :gtfs/transit-service-id service-id})
  (doseq [r route-change-infos]
    (specql/insert! db :gtfs/detected-route-change
                    (merge {:gtfs/transit-change-date analysis-date
                            :gtfs/transit-service-id service-id
                            :gtfs/created-date (java.util.Date.)}
                           r))))

(defn update-transit-changes! [db analysis-date service-id package-ids {:keys [all-routes route-changes]}]
  {:pre [(some? analysis-date)
         (or (zero? service-id)
             (pos? service-id))]}
  (tx/with-transaction
    db
    (let [route-change-infos (map (fn [detection-result]
                                    (transform-route-change all-routes detection-result route-changes))
                                  route-changes)
          change-count-by-type (fmap count (group-by :gtfs/change-type route-change-infos))
          earliest-route-change (first (drop-while (fn [{:gtfs/keys [change-date]}]
                                                     ;; Remove change-date from the route-changes-infos list if it is nil or it is in the past
                                                     (or (nil? change-date)
                                                         (date-in-the-past? (.toLocalDate change-date))))
                                                   (sort-by :gtfs/change-date route-change-infos)))
          ;; Set change date to future (every 4 weeks at monday) - This is the day when changes are detected for next time
          new-change-date (sql-date (time/native->date (.plusDays (time/beginning-of-week (.toLocalDate (time/now))) 28)))
          transit-chg-res (specql/upsert! db :gtfs/transit-changes
                                          #{:gtfs/transport-service-id :gtfs/date}
                                          {:gtfs/transport-service-id service-id
                                           :gtfs/date analysis-date
                                           :gtfs/change-date new-change-date
                                           :gtfs/different-week-date (:gtfs/different-week-date earliest-route-change)
                                           :gtfs/current-week-date (:gtfs/current-week-date earliest-route-change)

                                           :gtfs/removed-routes (:removed change-count-by-type 0)
                                           :gtfs/added-routes (:added change-count-by-type 0)
                                           :gtfs/changed-routes (:changed change-count-by-type 0)
                                           :gtfs/no-traffic-routes (:no-traffic change-count-by-type 0)

                                           :gtfs/package-ids package-ids
                                           :gtfs/created (java.util.Date.)})]
      (update-route-changes! db (sql-date analysis-date) service-id route-change-infos))))

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

(defn changed-day-from-changed-week
  [db service-id route-list-with-changed-weeks]
  #_(println "route-list-with-changed-weeks: " route-list-with-changed-weeks)
  (mapv #(route-day-changes db service-id %) route-list-with-changed-weeks))


(defn changes-by-week->changes-by-route
  "Input: Takes collection of maps (weeks), each map contains all routes of the service for the week.
    Input format:
        {:beginning-of-week 1.1 :end-of-week 7.1 :routes { \"route1\" [\"h1\" \"h1\"}
                                                          \"route2\" [\"h1\" \"h1\"]
                                                          \"route3\" [\"h1\" \"h1\"]}}
        {:beginning-of-week 8.1 :end-of-week 15.1 :routes { \"route1\" [\"h1\" \"h1\"}
                                                          \"route2\" [\"h1\" \"h1\"]
                                                          \"route3\" [\"h1\" \"h1\"]}}
  Function splits the input maps so that routes are not grouped together, instead each route will be in its own collection.
  Output: A vector of 'route-vectors'. Each route-vector contains maps, each representing a week for the specific route.
    Output format:
       [[{:beginning-of-week 1.1, :end-of-week 7.1, :routes {\"route1\" [\"h1\" \"h1\"]}}
        {:beginning-of-week 8.1, :end-of-week 15.1, :routes {\"route1\" [\"h1\" \"h1\"]}}
        {:beginning-of-week 16.1, :end-of-week 23.1, :routes {\"route1\" [\"h1\" \"h1\"]}}]
        [{:beginning-of-week 1.1, :end-of-week 7.1, :routes {\"route2\" [\"h1\" \"h1\"]}}
         {:beginning-of-week 8.1, :end-of-week 15.1, :routes {\"route2\" [\"h1\" \"h1\"]}}
         {:beginning-of-week 16.1, :end-of-week 23.1, :routes {\"route2\" [\"h1\" \"h1\"]}}]]"
  [weeks]
  (vals (group-by (fn [d]
                    (keys (:routes d)))
                  (reduce
                    (fn [result week]
                      (let [routes (:routes week)
                            r-weeks
                            (map (fn [route]
                                   (assoc week :routes (conj {} route)))
                                 routes)]
                        (concat result r-weeks)))
                    []
                    weeks))))

(defn detect-changes-for-all-routes
  "Input: takes a vector or routes with their traffic weeks
  Invokes a function in a loop for each route to detect any changes for each route.
  Output: Vector of routes enriched by details if there are changes in traffic for a route on a week."
  [route-list-with-week-hashes]
  (vec (mapcat route-differences route-list-with-week-hashes)))

(spec/fdef detect-route-changes-for-service-new
           :ret ::detected-route-changes-for-services-coll)
(defn detect-route-changes-for-service-new [db {:keys [start-date service-id] :as route-query-params}]
  "Input: Takes service-id,
  fetches and analyzes packages for the service and produces a collection of structures, each of which describes
  if a route has traffic or changes/no-traffic/ending-traffic, during a time period defined in the analysis logic.
  Output: ::detected-route-changes-for-services-coll"
  (let [type (db-route-detection-type db service-id)
        ;; Generate "key" for all routes. By default it will be a vector ["<route-short-name>" "<route-long-name" "trip-headsign"]
        service-routes (sort-by :route-hash-id (service-routes-with-date-range db {:service-id service-id}))
        all-routes (map-by-route-key service-routes type)
        all-route-keys (set (keys all-routes))
        route-hashes (service-route-hashes-for-date-range db route-query-params)
        ;; Change hashes that are at static holiday to a keyword
        route-hashes-with-holidays (override-static-holidays route-hashes)
        routes-by-date (routes-by-date route-hashes-with-holidays all-route-keys type)] ;; Format: ({:date routes(=hashes)})
    (try
      {:all-routes all-routes
       :route-changes
       (let [new-data (->> routes-by-date
                           ;; Create week hashes so we can find out the differences between weeks
                           (combine-weeks)
                           (changes-by-week->changes-by-route)
                           (detect-changes-for-all-routes)
                           ;; Fetch detailed day details
                           (route-day-changes-new db service-id))]
         (spec/assert ::detected-route-changes-for-services-coll new-data)
         new-data)}
      (catch Exception e
        (log/warn e "Error when detecting route changes using route-query-params: " route-query-params " service-id:" service-id)))))


(defn detect-route-changes-for-service-old [db {:keys [start-date service-id] :as route-query-params}]
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
       (let [old-data (->> routes-by-date                   ;; Format of routes-by-date is: [{:date routes(=hashes)}]
                           ;; Create week hashes so we can find out the differences between weeks
                           (combine-weeks)
                           ;; Search next week (for every route) that is different
                           (route-weeks-with-first-difference-old)
                           ;; Fetch detailed route comparison if a change was found
                           (route-day-changes db service-id)               ;;remove this to run camparing tests of our changed function
                           )]
         old-data)}
      (catch Exception e
        (log/warn e "Error when detecting route changes using route-query-params: " route-query-params " service-id:" service-id)))))

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
        #_(println "service id " service-id "date " (:gtfs/date t))))))

;;Use only in local environment and for debugging purposes!
(defn update-route-hashes [db]
  (let [service-ids (fetch-distinct-services-from-transit-changes db)]
    (doall
      (for [id service-ids]
        (update-date-hash-with-null-route-hash-id db (:id id))))))

(defn- call-generate-date-hash [db packages user future]
  (let [package-count (count packages)
        recalculation-id (when packages
                           (:gtfs/recalculation-id (start-hash-recalculation db package-count user)))]
    (dotimes [i (count packages)]
      (let [package-id (nth packages i)]
          #_(println "Generating" (inc i) "/" package-count " - " package-id)
          (if future
            (generate-date-hashes-for-future db {:package-id (:package-id package-id)})
            (generate-date-hashes db {:package-id (:package-id package-id)}))
          (update-hash-recalculation db (inc i) recalculation-id))
      (log/info "Generation ready!"))
    (stop-hash-recalculation db recalculation-id)))

(defn calculate-monthly-date-hashes-for-packages [db user future]
  (let [monthly-packages (fetch-monthly-packages db)]
    (log/info "Generating monthly date hashes. Package count" (count monthly-packages))
    (call-generate-date-hash db monthly-packages user future)
    monthly-packages))

(defn calculate-date-hashes-for-all-packages [db user future]
  (let [all-packages (fetch-all-packages db)]
    (log/info "Generating all date hashes. Package count" (count all-packages))
    (call-generate-date-hash db all-packages user future)
    all-packages))

(defn calculate-date-hashes-for-contract-traffic [db user future]
  (let [all-packages (fetch-contract-packages db)]
    (log/info "Generating contract date hashes. Package count" (count all-packages))
    (call-generate-date-hash db all-packages user future)
    all-packages))


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


