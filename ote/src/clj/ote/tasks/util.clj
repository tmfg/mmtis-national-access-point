(ns ote.tasks.util
  (:require [clj-time.core :as t]
            [clj-time.periodic :refer [periodic-seq]])
  (:import (org.joda.time DateTimeZone)))

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
