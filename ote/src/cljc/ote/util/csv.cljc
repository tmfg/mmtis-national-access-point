(ns ote.util.csv
  "CSV related validators and util functions"
  (:require [clojure.string :as str]))

(defn valid-csv-header?
  "Ensure that there is at least 2 elements in header"
  [header]
  (>= (count header) 2))

(defn valid-business-id? [value]
  (let [pattern #"\d{7}-\d"]
    (boolean (re-matches pattern value))))

(defn csv-separator [input]
  (if (str/includes? (first (str/split-lines input)) ";")
    ;; First line contains a semicolon, use it as separator
    \;
    ;; Otherwise default to comma
    \,))
