(ns ote.db.utils
  "Utilities for handcrafted SQL queries"
  (:require [clojure.string :as str]))

(defn- to-path [key]
  (let [components (-> key name (str/split #"_"))]
    (into []
          (map keyword)
          components)))

(defn underscore->structure
  "Convert map with underscores in key names to a nested map structure.
  Example:
  (underscore->structure {:user_id 1 :user_name \"foo\" :other 42})
   => {:user {:id 1 :name \"foo\"}
       :other 42}"
  [m]
  (reduce (fn [acc [key val]]
            (assoc-in acc (to-path key) val))
          {}  m))
