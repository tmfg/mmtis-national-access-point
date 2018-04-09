(ns ote.util.collections)

(defn remove-by-index
  "Remove element from vector by index."
  [vector idx]
  (into (subvec vector 0 idx)
        (subvec vector (inc idx))))
