(ns ote.transit-changes
  "Manipulate transit change times"
  (:require [ote.time :as time]
            [ote.util.collections :refer [index-of]]
            [taoensso.timbre :as log]
            [clojure.set :as set]
            [clojure.spec.alpha :as s]))

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

(defn trip-stop-differences
  "Returns the amount of differences in stop times and stop sequence for the given trip pair."
  [left right]
  (let [left-stop-times (into {}
                              (map (juxt :gtfs/stop-name :gtfs/departure-time))
                              (:stoptimes left))
        right-stop-times (into {}
                               (map (juxt :gtfs/stop-name :gtfs/departure-time))
                               (:stoptimes right))
        left-stop-names (into #{} (keys left-stop-times))
        right-stop-names (into #{} (keys right-stop-times))
        all-stop-names (into #{}
                             (set/union left-stop-names right-stop-names))]
    {:stop-time-changes (reduce (fn [chg stop-name]
                                  (let [left (left-stop-times stop-name)
                                        right (right-stop-times stop-name)]
                                  (if (and left right (not= left right))
                                    (inc chg)
                                    chg)))
                                0 all-stop-names)
     :stop-seq-changes (+ (count (set/difference left-stop-names right-stop-names))
                          (count (set/difference right-stop-names left-stop-names)))}))


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

(defn combined-stop-sequence [first-common-stop [trip1 trip2]]
  (let [stop-seq-of-fcs-trip1 (some #(when (= first-common-stop (:gtfs/stop-name %))
                                       (:gtfs/stop-sequence %))
                                    (:stoptimes trip1))
        stop-seq-of-fcs-trip2 (some #(when (= first-common-stop (:gtfs/stop-name %))
                                       (:gtfs/stop-sequence %))
                                    (:stoptimes trip2))
        trip1-normalized-stop-seq (map #(assoc % :trip 1)
                                       (normalize-stop-sequence-numbers stop-seq-of-fcs-trip1
                                                                        (:stoptimes trip1)))
        trip2-normalized-stop-seq (map #(assoc % :trip 2)
                                       (normalize-stop-sequence-numbers stop-seq-of-fcs-trip2
                                                                        (:stoptimes trip2)))]
    ;; Combine the same stops!
    (mapv (fn [stop-times]
            {:gtfs/stop-name (:gtfs/stop-name (first stop-times))
             :gtfs/departure-time-date1 (:gtfs/departure-time
                                         (first (filter #(= 1 (:trip %)) stop-times)))
             :gtfs/departure-time-date2 (:gtfs/departure-time
                                         (first (filter #(= 2 (:trip %)) stop-times)))})

          (partition-by :gtfs/stop-name
                        (sort-by :gtfs/stop-sequence (concat trip1-normalized-stop-seq trip2-normalized-stop-seq))))))


(defn combine-trips [date1-trips date2-trips]
  (when (and date1-trips date2-trips)
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

(s/fdef week=
  :args (s/cat :w1 ::week-hash :w2 ::week-hash)
  :ret boolean?)

(defn first-different-day
  "Return the index of first different day in two week hash vectors.
  A day is considered different if both weeks have a hash (non nil)
  for that day and the hash is different."
  [week-hash-1 week-hash-2]
  (some identity
        (map (fn [i d1 d2]
               (and (some? d1)
                    (some? d2)
                    (not= d1 d2)
                    i))
             (iterate inc 0)
             week-hash-1
             week-hash-2)))



(s/fdef first-different-day
  :args (s/cat :week-hash-1 ::week-hash
               :week-hash-2 ::week-hash)
  :ret (s/nilable integer?))
