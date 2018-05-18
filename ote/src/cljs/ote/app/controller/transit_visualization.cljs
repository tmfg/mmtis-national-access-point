(ns ote.app.controller.transit-visualization
  (:require [ote.communication :as comm]
            [tuck.core :as tuck :refer-macros [define-event]]
            [ote.app.routes :as routes]
            [cljs-time.core :as t]
            [cljs-time.format :as tf]
            [ote.time :as time]
            [taoensso.timbre :as log]))

(def hash-colors
  ["#52ef99" "#c82565" "#8fec2f" "#8033cb" "#5c922f" "#fe74fe" "#02531d"
   "#ec8fb5" "#23dbe1" "#a4515b" "#169294" "#fd5925" "#3d4e92" "#f4d403"
   "#66a1e5" "#d07d09" "#9382e9" "#b9cf84" "#544437" "#f2cdb9"])

(defn parse-date [date-str]
  (tf/parse (tf/formatter "dd.MM.yyyy") date-str))

(defn days-to-first-diff [start-date date->hash]
  (let [hash (date->hash start-date)
        dt (parse-date start-date)
        first-diff (first
                     (sort #(t/before? (first %1) (first %2))
                           (filter #(and (not (= hash (second %)))
                                         (t/after? (first %) dt)
                                         (= (time/day-of-week (first %))
                                            (time/day-of-week dt)))
                                   (map (juxt (comp parse-date first)
                                              second) date->hash))))]
    (t/in-days (t/interval dt (first first-diff)))))

(define-event LoadOperatorDatesResponse [dates]
  {:path [:transit-visualization]}
  (-> app
      (assoc :hash->color (zipmap (distinct (keep :hash dates))
                            (cycle hash-colors
                                   ;; FIXME: after all colors are consumed, add some pattern style
                                   ))
             :date->hash (into {}
                               (map (juxt (comp time/format-date :date)
                                          :hash))
                               dates)
             :years (if (empty? dates)
                      []
                      (vec
                       (range (reduce min (map (comp time/year :date) dates))
                              (inc (reduce max (map (comp time/year :date) dates))))))
             :highlight {:mode :diff})
      (dissoc :loading?)))

(define-event LoadOperatorDates [operator-id]
  {:path [:transit-visualization :loading?]}
  (comm/get! (str "transit-visualization/dates/" operator-id)
             {:on-success (tuck/send-async! ->LoadOperatorDatesResponse)})
  true)

(define-event SetHighlightMode [mode]
  {:path [:transit-visualization :highlight]}
  (-> app
      (assoc :mode mode)))

(define-event DaysToFirstDiff [start-date date->hash]
  {:path [:transit-visualization :days-to-diff]}
  (days-to-first-diff start-date date->hash))

(defmethod routes/on-navigate-event :transit-visualization [_]
  (->LoadOperatorDates 1))

(define-event HighlightHash [hash day]
  {:path [:transit-visualization :highlight]}
  (-> app
      (merge {:hash hash :day day})))

(define-event RoutesForDatesResponse [date1 date2 routes]
  {:path [:transit-visualization :compare]}
  (when (and (= date1 (:date1 app))
             (= date2 (:date2 app)))
    (-> app
        (assoc :routes routes)
        (dissoc :loading?))))

(define-event SelectDateForComparison [date]
  {:path [:transit-visualization :compare]}
  (let [app (or app {})
        last-selected (:last-selected app)
        date (time/format-date date)
        app (merge app
                   (if (not= 1 last-selected)
                     {:date1 date
                      :last-selected 1}
                     {:date2 date
                      :last-selected 2}))]
    (if (and (:date1 app) (:date2 app))
      (do
        (comm/get! (str "transit-visualization/routes-for-dates")
                   {:params (select-keys app [:date1 :date2])
                    ;:on-success (tuck/send-async! ->RoutesForDatesResponse date1 date2)
                    })
        (assoc app :loading? true))
      app)))
