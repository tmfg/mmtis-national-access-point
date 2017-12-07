(ns ote.util.values
  "Common utilities for checking values.")

(defn effectively-empty?
  "Check if the given value is effectively empty.
  Effectively empty values are nil and blank (or just whitespace) strings
  and collections that contain only effectively empty values."
  [value]
  (or (nil? value)

      ;; This is a map that is empty or only has effectively empty values
      (and (map? value)
           (or (empty? value)
               (every? effectively-empty? (vals value))))

      ;; This is an empty collection or only has effectively empty values
      (and (coll? value)
           (or (empty? value)
               (every? effectively-empty? value)))

      ;; This is a blank (empty or just whitespace) string
      (and (string? value) (str/blank? value))))
