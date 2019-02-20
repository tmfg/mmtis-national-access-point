(ns ote.util.clojure)

(defn remove-coll-key-ns [c]
  (into {} (map (fn [[k v]] [(keyword (name k)) v]) c)))
