(ns ote.time
  "Common utilities for working with date and time information."
  (:require
   #?@(:clj [[specql.impl.composite :as specql-composite]]
       :cljs [[goog.string :as gstr]])
   [specql.data-types :as specql-data-types]
   [clojure.spec.alpha :as s]
   [clojure.string :as str]))

;; Record for wall clock time (hours, minutes and seconds)
(defrecord Time [hours minutes seconds])

(s/def ::specql-data-types/time #(instance? Time %))

(defn format-time-full [{:keys [hours minutes seconds]}]
  (#?(:clj format
      :cljs gstr/format)
   "%02d:%02d:%02d" hours minutes seconds))

(defn format-time [{:keys [hours minutes seconds] :as time}]
  (if seconds
    (format-time-full time)
    (#?(:clj format
        :cljs gstr/format)
     "%02d:%02d" hours minutes)))

(defn parse-time [string]
  (let [[h m s] (map #(#?(:clj Integer/parseInt
                          :cljs js/parseInt) %)
                     (str/split string #":"))]
    (->Time h m s)))

;; Hook into specql to allow us to read/write the time
#?(:clj
   (defmethod specql-composite/parse-value "time" [_ string]
     (parse-time string)))

#?(:clj
   (defmethod specql-composite/stringify-value "time" [_ time]
     (format-time time)))
