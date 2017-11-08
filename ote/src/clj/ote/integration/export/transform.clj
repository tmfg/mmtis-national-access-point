(ns ote.integration.export.transform
  "Transform specific data keys' values for export.
  Defines a multimethod to transform values by key, default being `identity`."
  (:require [ote.db.transport-service]
            [ote.db.transport-service :as t-service]
            [ote.time :as time]
            [clojure.walk :as walk]))

(defmulti transform
  "Transform value for the given key. Must return new value."
  (fn [key value] key))

(defmethod transform ::t-service/from-date [_ value]
  (when value
    (time/format-date-iso-8601 value)))

(defmethod transform ::t-service/to-date [_ value]
  (when value
    (time/format-date-iso-8601 value)))

(defmethod transform :default [_ value] value)

(defn transform-deep [data]
  (walk/postwalk
   (fn [data]
     (if (map? data)
       (reduce-kv
        (fn [m k v]
          (assoc m k (transform k v))) {} data)
       data))
   data))
