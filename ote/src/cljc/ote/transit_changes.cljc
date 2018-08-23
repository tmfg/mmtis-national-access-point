(ns ote.transit-changes
  "Manipulate transit change times"
  (:require [ote.time :as time]))

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

(def test-times-left
  ["7:30"
   "9:00"
   "9:30"
   "9:45"
   "10:00"
   "10:20"])
(def test-times-right
  ["8:56"
   "9:15"
   "10:00"
   "12:20"
   "14:50"])

(println (merge-by-closest-time time/parse-time test-times-left test-times-right))
