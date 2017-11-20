(ns ote.time
  "Common utilities for working with date and time information."
  (:require
   #?@(:clj [[specql.impl.composite :as specql-composite]
             [clj-time.format :as format]
             [clj-time.coerce :as coerce]]
       :cljs [[goog.string :as gstr]
              [cljs-time.core :as cljs-time]
              [cljs-time.format :as format]
              [cljs-time.local :as local]
              [cljs-time.coerce :as coerce]])
   [specql.data-types :as specql-data-types]
   [clojure.spec.alpha :as s]
   [clojure.string :as str]))

;; Record for wall clock time (hours, minutes and seconds)
(defrecord Time [hours minutes seconds])

(s/def ::specql-data-types/time #(instance? Time %))

#?(:cljs
(defn format-timestamp-for-ui [time]
  (if  (nil? time)
    " " ;: if nil - print empty string
    (->> time
         cljs-time/to-default-time-zone
         (format/unparse (format/formatter "dd.MM.yyyy HH:mm"))))))

#?(:cljs
   (defn format-js-time [time]
     (if  (nil? time)
       "" ;: if nil - print empty string
       (->> time
            cljs-time/to-default-time-zone
            (format/unparse (format/formatter "HH:mm:ss"))))))

#?(:cljs
   (defn to-js-time [db-time]
     (let [hours (get db-time :hours)
           minutes (get db-time :minutes)
           seconds (get db-time :seconds)]
       (js/Date.
         (coerce/to-long (local/to-local-date-time
                           (cljs-time/today-at hours minutes seconds)))))))


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

(defprotocol DateFields
  (date-fields [this] "Return date fields as a map of data."))

(extend-protocol DateFields
   #?(:cljs js/Date :clj java.util.Date)
   (date-fields [this]
     {::date (.getDate this)
      ::month (inc (.getMonth this))
      ::year (+ 1900 (.getYear this))
      ::hours (.getHours this)
      ::minutes (.getMinutes this)
      ::seconds (.getSeconds this)}))

(defn format-date
  "Format given date in human readable format."
  [date]
  (let [{::keys [date month year]} (date-fields date)]
    (#?(:cljs gstr/format :clj format)
     "%02d.%02d.%d" date month year)))

(defn format-date-iso-8601
  "Format given date in ISO-8601 format."
  [date]
  (let [{::keys [date month year]} (date-fields date)]
    (#?(:cljs gstr/format :clj format)
     "%d-%02d-%02d" year month date)))


;; Hook into specql to allow us to read/write the time
#?(:clj
   (defmethod specql-composite/parse-value "time" [_ string]
     (parse-time string)))

#?(:clj
   (defmethod specql-composite/stringify-value "time" [_ time]
     (format-time time)))
