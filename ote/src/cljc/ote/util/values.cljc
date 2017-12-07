(ns ote.util.values
  "Common utilities for checking values."
  (:require [clojure.string :as str]
            [ote.db.transport-service :as t-service]))

(def ignorable-key? #{::t-service/lang})

(defn effectively-empty?
  "Check if the given value is effectively empty.
  Effectively empty values are nil and blank (or just whitespace) strings
  and collections that contain only effectively empty values."
  [value]
  (or (nil? value)

      ;; This is a map that is empty or only has effectively empty values
      (and (map? value)
           (or (empty? value)
               (every? (fn [[key val]]
                         (or (ignorable-key? key)
                             (effectively-empty? val)))
                       value)))

      ;; This is an empty collection or only has effectively empty values
      (and (coll? value)
           (or (empty? value)
               (every? effectively-empty? value)))

      ;; This is a blank (empty or just whitespace) string
      (and (string? value) (str/blank? value))))

(defn without-empty-rows
  "Removes effectively empty rows from a vector."
  [vector]
  (into []
        (remove effectively-empty?)
        vector))
