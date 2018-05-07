(ns ote.util.feature
  "Util features")

(defn feature-enabled? [config feature]
  (contains? (:enabled-features config) feature))
