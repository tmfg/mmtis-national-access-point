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
