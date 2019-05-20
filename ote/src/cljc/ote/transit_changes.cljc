(ns ote.transit-changes
  "Manipulate transit change times"
  (:require [ote.time :as time]
            [ote.util.collections :refer [index-of]]
            [taoensso.timbre :as log]
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [ote.db.gtfs :as gtfs])
  #?(:clj
     (:require [specql.core :as specql]
               [specql.op :as op])))

;; Define data type specs
(s/def ::day-hash (s/nilable string?))
(s/def ::week-hash (s/every ::day-hash :count 7))


(defn item-with-closest-time
  "Return a vector containing the given `item` and the item in `items`
  which has the closest time to the given `item`."
  [time-fn item items]
  (let [item-time (time-fn item)]
    [item (first (sort-by #(time/time-difference item-time (time-fn %)) items))]))

(defn- flip-vec [[l r]]
  [r l])

(defn merge-by-closest-time [time-fn left-items right-items]
  (let [left-items-with-closest (mapv #(item-with-closest-time time-fn % right-items) left-items)
        right-items-with-closest (mapv (comp flip-vec #(item-with-closest-time time-fn % left-items)) right-items)
        time-diff (fn [[l r]] (time/time-difference (time-fn l) (time-fn r)))
        sorted-pairs (remove
                      ;; Remove pairs whose time-difference is over 30 minutes
                      #(> (time-diff %) 30)

                      (sort-by time-diff
                               (concat left-items-with-closest
                                       right-items-with-closest)))]
    (loop [left-items-set (into #{} left-items)
           right-items-set (into #{} right-items)
           [p & pairs] sorted-pairs
           acc []]
      (if (not (and (seq left-items-set)
                    (seq right-items-set)
                    p))
        ;; No more items or or pairs: add any orphans (unpaired times)
        (sort-by
         ;; Sort by starting time
         (fn [[left right]]
           (time/minutes-from-midnight (time-fn (or left right))))

         (concat acc
                 (mapv (fn [left] [left nil]) left-items-set)
                 (mapv (fn [right] [nil right]) right-items-set)))

        ;; Take left and right items (if available) and add to acc
        (let [[left right] p]
          (if (and (contains? left-items-set left)
                   (contains? right-items-set right))
            ;; Bot items available, consume them from input sets
            (recur (disj left-items-set left)
                   (disj right-items-set right)
                   pairs
                   (conj acc p))

            ;; One or both items not available, ignore this pair
            (recur left-items-set right-items-set pairs acc)))))))

(defn stop-key
  "Use lat and lon values as stop-key. Stop-key is used to determine is the stop remain the same in different gtfs packages.
  Was earlier stop-name, now changed to lat lon pair."
  [stop]
  [(:gtfs/stop-fuzzy-lat stop) (:gtfs/stop-fuzzy-lon stop)])

(defn stop-key-for-stop-list
  "Use lat, lon and instance values as stop-key. The instance contains information on how many times the stop is in the route."
  [stop]
  [(:gtfs/stop-fuzzy-lat stop) (:gtfs/stop-fuzzy-lon stop) (:instance stop)])

(defn trip-stop-differences
  "Returns the amount of differences in stop times and stop sequence and stop names for the given trip pair."
  [left right]
  (let [left-stop-times (into {}
                              (map (juxt stop-key :gtfs/departure-time))
                              (:stoptimes left))
        right-stop-times (into {}
                               (map (juxt stop-key :gtfs/departure-time))
                               (:stoptimes right))
        left-stop-keys (into #{} (keys left-stop-times))
        right-stop-keys (into #{} (keys right-stop-times))
        all-stop-keys (into #{}
                             (set/union left-stop-keys right-stop-keys))
        left-different-stop-keys (set/difference left-stop-keys right-stop-keys)
        right-different-stop-keys (set/difference right-stop-keys left-stop-keys)]
    {:stop-time-changes (reduce (fn [chg stop-name]
                                  (let [left (left-stop-times stop-name)
                                        right (right-stop-times stop-name)]
                                  (if (and left right (not= left right))
                                    (inc chg)
                                    chg)))
                                0 all-stop-keys)
     :stop-seq-changes (+ (count left-different-stop-keys)
                          (count right-different-stop-keys))}))


(defn time-for-stop [stoptimes-display stop-name]
  (some #(when (= stop-name (:gtfs/stop-name %))
           (:gtfs/departure-time %))
        (:stoptimes stoptimes-display)))

(defn first-common-stop [stoptime-displays]
  (let [distinct-stops (into #{}
                             (mapcat (fn [stoptime-display]
                                       (map #(select-keys % [:gtfs/stop-name :gtfs/stop-sequence])
                                            (:stoptimes stoptime-display))))
                             stoptime-displays)]
    (some (fn [{:gtfs/keys [stop-name] :as common-stop-candidate}]
            (when (every? #(time-for-stop % stop-name) stoptime-displays)
              stop-name))
          (sort-by :gtfs/stop-sequence distinct-stops))))


(defn normalize-stop-sequence-numbers [stop-seq-of-zero stop-seq]
  (map (fn [stoptime]
         (update stoptime :gtfs/stop-sequence - stop-seq-of-zero))
       stop-seq))

(defn earliest-departure-time [stop]
  (let [minutes-from-midnight1 (some-> stop :gtfs/departure-time-date1 (time/minutes-from-midnight))
        minutes-from-midnight2 (some-> stop :gtfs/departure-time-date2 (time/minutes-from-midnight))]
    (cond
      (nil? minutes-from-midnight1) minutes-from-midnight2
      (nil? minutes-from-midnight2) minutes-from-midnight1
      :default (max minutes-from-midnight1 minutes-from-midnight2))))

(defn format-stop-info
  "recieves 2 vectors, first vector has coordinates, which are not used here, second vector is other stop-information"
  [[_ stop-times]]
  {:gtfs/stop-name (str/join "->"
                             (into #{} (map
                                         #(:gtfs/stop-name %)
                                         stop-times)))
   :gtfs/departure-time-date1 (:gtfs/departure-time
                                (first (filter #(= 1 (:trip %)) stop-times)))
   :gtfs/departure-time-date2 (:gtfs/departure-time
                                (first (filter #(= 2 (:trip %)) stop-times)))})


(defn normalize-trip-with-instance
  [trip stop-seq-of-fcs-trip trip-num]
  (let [normalized (map #(assoc % :trip trip-num)
                        (normalize-stop-sequence-numbers stop-seq-of-fcs-trip
                                                         (:stoptimes trip)))
        stops-by-coords (group-by stop-key normalized)]
    (apply concat (map (fn [[_ stop]]
                         (map-indexed (fn [idx single-stop]
                                        (assoc single-stop :instance (inc idx)))
                                      stop))
                       stops-by-coords))))

(defn combined-stop-sequence [first-common-stop [trip1 trip2]]
  (let [stop-seq-of-fcs-trip1 (some #(when (= first-common-stop (:gtfs/stop-name %))
                                       (:gtfs/stop-sequence %))
                                    (:stoptimes trip1))
        stop-seq-of-fcs-trip2 (some #(when (= first-common-stop (:gtfs/stop-name %))
                                       (:gtfs/stop-sequence %))
                                    (:stoptimes trip2))

        trip1-normalized (normalize-trip-with-instance trip1 stop-seq-of-fcs-trip1 1)

        trip2-normalized (normalize-trip-with-instance trip2 stop-seq-of-fcs-trip2 2)]
    ;; Combine the same stops!
    (sort-by
      earliest-departure-time
      (sort-by :gtfs/stop-name
               (mapv format-stop-info (group-by stop-key-for-stop-list
                               (sort-by :gtfs/stop-sequence (concat trip1-normalized trip2-normalized))))))))


(defn combine-trips [date1-trips date2-trips]
  (when (and date1-trips date2-trips (pos-int? (count date1-trips)) (pos-int? (count date2-trips)))
    (when-let [first-common-stop (first-common-stop (concat date1-trips date2-trips))]
      (let [first-common-stop
            #(assoc %
                    :first-common-stop first-common-stop
                    :first-common-stop-time (time-for-stop % first-common-stop))
            date1-trips (mapv first-common-stop date1-trips)
            date2-trips (mapv first-common-stop date2-trips)
            combined-trips (merge-by-closest-time :first-common-stop-time date1-trips date2-trips)]
        (mapv (fn [[l r]]
                [l r (trip-stop-differences l r)])
              combined-trips)))))


(defn week=
  "Compare week hashes. Returns true if they represent the same traffic
  (excluding no-traffic days and static-holidays).
  Both `w1` and `w2` are vectors of strings that must be the same length."
  [w1 w2]  
  (let [w1-empty? (every? nil? w1)
        w2-empty? (every? nil? w2)]
    ;; if one of the weekhashes is all nil's (no traffic),
    ;; they are equal if both of them are not-empty
    ;; or both of them are empty
    (if (or w1-empty? w2-empty?)
      (do
        (when (= w1 ))
        (println "week= comparing empties" w1 w2 "->" (= w1-empty? w2-empty?))
        ;; (clojure.stacktrace/print-stack-trace (Exception. "foo"))
        (= w1-empty? w2-empty?)
        )
      ;; otherwise, do day-by-day comparison ignoring no-traffic days and holidays
      (every? true?
              (map (fn [h1 h2]
                     ;; Only compare hashes where both days have traffic (not nil)
                     (or (nil? h1) ;; h1 is no-traffic day due to nil value
                         (nil? h2) ;; h2 is no-traffic day due to nil value
                         (keyword? h1) ;; h1 is static-holiday due to value is keyword
                         (keyword? h2) ;; h2 is static-holiday due to value is keyword
                         (= h1 h2)))
                   w1 w2)))))

(s/fdef week=
  :args (s/cat :w1 ::week-hash :w2 ::week-hash)
  :ret boolean?)

(defn- day-traffic-changes?
  "Takes hashes of a baseline day and a new day to compare, and similar pair for the previous detected change.
  Latter pair is used to resolve if this is a new change or an already reported change, which shouldn't be detected for
  a second time.
  Input: d1: baseline day hash, d2: hash of the day to analyze, d1p: baseline day hash of the previous changed day, d2p: hash of the previous changed day
  Output: True if d2 is considered a day when it's traffic has changed compared to baseline, false if not."
  [d1 d2 d1p d2p]
  (let [res (and (not= d1 d2)
                 (not (keyword? d1))
                 (not (keyword? d2))
                 ;; Not a change if change happened on previous day and it just continues on this day. E.g. AAAAAAA => AABBBBB
                 (or (not= d1p d1)
                     (not= d2p d2)))]
    res))

(defn changed-days-of-week
  "Takes collections of day hashes for a baseline week and a new week and detects which days change compared to baseline.
   Input:
   week-hash-2 collection of day hashes for the week used for comparing
   week-hash-2 collection of day hashes for the week which will be evaluated
   Output:
   Indices of the elements in week-hash-2 coll which change compared to week-hash-1."
  [week-hash-1 week-hash-2]
  (loop [result []
         ix 0
         d1 (nth week-hash-1 ix)                            ;; Day on "current week" which is the evaluation baseline
         d2 (nth week-hash-2 ix)                            ;; Day on future week, which is being evaluated
         d1p "."                                            ;; Day d1 hash of previous changed day, if available. Initial value for "not set" is here "." because logic uses nil and keyword.
         d2p "."]                                           ;; Day d2 hash of previous changed day. Initial value for "not set" is here "." because logic uses nil and keyword.
    (if (< ix (count week-hash-2))
      (let [day-changed? (day-traffic-changes? d1 d2 d1p d2p)
            ixn (inc ix)]
        (recur
          ;; Logic for detecting if day d2 is a new change or not.
          (if day-changed?
            (conj result ix)                                ;; Day is a change, so append its index to result coll
            result)                                         ;; No change, no changes added to result coll
          ixn
          (nth week-hash-1 ixn nil)
          (nth week-hash-2 ixn nil)
          (if day-changed? d1 d1p)
          (if day-changed? d2 d2p)))
      result)))

(def static-holidays
  "Static holidays e.g. Christmas that are skipped in. Key is a vector [day month] and value is the holiday id"
  {[1 1] :new-year
   [6 1] :epifania
   [1 5] :first-of-may
   [6 12] :finnish-independence-day
   [24 12] :xmas-eve
   [25 12] :xmas-day
   [26 12] :boxing-day})

#?(:clj
   (defn exception-holidays
    "returns a map like {[day month year] :holiday} based on detection-holidays table"
    [db]
    (let [dates (specql/fetch db
                              :gtfs/detection-holidays
                              #{:gtfs/date :gtfs/reason}
                              {:gtfs/date op/not-null?})]
      (reduce
        (fn [map holiday]
          (let [{::time/keys [month date year]} (time/date-fields (:gtfs/date holiday))]
            (assoc map
              [date month year] (keyword (str/lower-case (:gtfs/reason holiday))))))
        {}
        dates))))

#?(:clj
   (defn is-holiday?
     "Check if given date is a holiday to be skipped. Returns holiday id if it is or nil otherwise."
     [db date]
     (let [{::time/keys [month date year]} (time/date-fields date)
           exceptions (exception-holidays db)]
       (if-let [res (exceptions [date month year])]
         res
         (static-holidays [date month])))))
