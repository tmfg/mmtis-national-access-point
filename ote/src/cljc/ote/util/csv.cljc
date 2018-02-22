(ns ote.util.csv
  "CSV related validators and util functions")

(defn valid-csv-header?
  "Ensure that there is at least 2 elements in header"
  [header]
  (>= (count header) 2))

(defn valid-business-id? [value]
  (let [pattern #"\d{7}-\d"]
    (boolean (re-matches pattern value))))