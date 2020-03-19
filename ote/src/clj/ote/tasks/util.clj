(ns ote.tasks.util
  (:require [clj-time.core :as t]
            [clj-time.periodic :refer [periodic-seq]]
            [java-time :as java-time]
            [ote.time :as time])
  (:import (org.joda.time DateTimeZone)
           (java.time LocalDate)))

(defonce timezone (DateTimeZone/forID "Europe/Helsinki"))

(defn daily-at
  "Returns a periodic sequence of times at the given hour/minute of day.
  If the todays time is already in the past, it is skipped."
  [hours minutes]
  (let [first-time (t/from-time-zone (t/today-at hours minutes) timezone)]
    (periodic-seq (if (t/before? first-time (t/now))
                    (t/plus first-time (t/days 1))
                    first-time)
                  (t/days 1))))

(defn joda-local-date-to-str [^org.joda.time.LocalDate joda-local-date]
  (let [joda-date-dayOfMonth (.getDayOfMonth joda-local-date)
        joda-date-dayOfMonth (if (= 1 (count (str joda-date-dayOfMonth)))
                          (str "0" joda-date-dayOfMonth)
                          joda-date-dayOfMonth)
        joda-date-month (.getMonthOfYear joda-local-date)
        joda-date-month (if (= 1 (count (str joda-date-month)))
                          (str "0" joda-date-month)
                          joda-date-month)
        joda-date-year (.getYear joda-local-date)]
    (str joda-date-year "-" joda-date-month "-" joda-date-dayOfMonth)))

(defn joda-datetime-to-java-time-local-date
  "Format joda datetime to java.time.localdate"
  [^org.joda.time.DateTime joda-datetime]
  (java-time.local/local-date (joda-local-date-to-str (.toLocalDate joda-datetime))))

(defn joda-local-date-to-java-time-local-date
  "Format joda local date to java.time.localdate"
  [^org.joda.time.LocalDate joda-local-date]
  (java-time.local/local-date (joda-local-date-to-str joda-local-date)))

(defn joda-local-date-to-inst
  "Format joda-localdate to java datetime inst"
  [^org.joda.time.LocalDate joda-local-date]
  (time/date-string->inst-date (joda-local-date-to-str joda-local-date)))