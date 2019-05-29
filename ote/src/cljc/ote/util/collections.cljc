(ns ote.util.collections)

(defn index-of
  "Return the index of the first element in `coll` where `pred` returns a truthy value.
  If no elements match `pred`, return nil."
  [pred coll]
  (first (keep-indexed
          (fn [i item]
            (when (pred item)
              i))
          coll)))

(defn remove-by-index
  "Remove element from vector by index."
  [vector idx]
  (into (subvec vector 0 idx)
        (subvec vector (inc idx))))

(defn map-by
  "Return a map of items in `coll` keyed by calling `key-fn` on each element.
  If multiple items have the same key, only the last value for that key
  will be in the output map."
  [key-fn coll]
  (into {}
        (map (juxt key-fn identity))
        coll))

(defn count-matching
  "Count the number of elements in `coll` where `pred` returns truthy.
  Returns the count."
  [pred coll]
  (loop [c 0
         coll coll]
    (if (empty? coll)
      c
      (recur (+ c (if (pred (first coll)) 1 0))
             (rest coll)))))

(defn remove-coll-key-ns [c]
  (into {} (map (fn [[k v]] [(keyword (name k)) v]) c)))

;; Input: coll=map
;; Output: coll, where keys are converted to keywords
(defn map->keyed [coll]
  (into {} (map
             (fn [[key val]]
               [(keyword key) val])
             coll)))
