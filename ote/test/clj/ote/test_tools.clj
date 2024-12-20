(ns ote.test-tools
  (:require [ote.test :refer :all]))

(defn get-transport-opertor-by-name [operator-name]
  (first (sql-query
           (format "SELECT id, name, \"business-id\", email FROM \"transport-operator\" WHERE name = '%s'" operator-name))))

(defn dissoc-in
  "Like assoc-in but for dissoc"
  [m [k & ks]]
  (if ks
    (let [sub-map (dissoc-in (get m k) ks)]
      (if (empty? sub-map)
        (dissoc m k)
        (assoc m k sub-map)))
    (dissoc m k)))
