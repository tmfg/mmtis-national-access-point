(ns ote.util.feature
  "Util features"
  (:require [taoensso.timbre :as log]))

(defonce enabled-features (atom #{}))

(defn set-enabled-features! [ef]
  (reset! enabled-features ef))

(defn feature-enabled?
  ([feature]
   (feature-enabled? @enabled-features feature))
  ([config feature]
   (contains? (:enabled-features config) feature)))

(defmacro when-enabled
  "Run body if the given feature is enabled."
  [feature & body]
  `(let [feature# ~feature]
     (if (feature-enabled? feature#)
       (do ~@body)
       (log/info "Feature is not enabled: " feature#))))
