(ns ote.app.controller.transit-visualization
  (:require [ote.communication :as comm]
            [tuck.core :as tuck :refer-macros [define-event]]
            [ote.app.routes :as routes]
            [cljs-time.core :as t]
            [cljs-time.format :as tf]
            [ote.time :as time]
            [ote.db.transport-operator :as t-operator]
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
                                              second) date->hash))))
        diff-date (first first-diff)]
    (when diff-date
      {:days (t/in-days (t/interval dt diff-date))
       :date diff-date})))

(define-event RoutesForDatesResponse [routes dates]
  {:path [:transit-visualization :compare]}
  (if (= dates (select-keys app [:date1 :date2]))
    (-> app
        (assoc :routes routes)
        (dissoc :loading?))
    app))

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
             :highlight {:mode nil}
             :calendar-mode :compact)
      (update :compare
              (fn [{:keys [date1 date2] :as compare}]
                (when (and date1 date2)
                  (do
                    (comm/get! (str "transit-visualization/routes-for-dates/" (:operator-id app))
                               {:params (select-keys compare [:date1 :date2])
                                :on-success (tuck/send-async! ->RoutesForDatesResponse
                                                              (select-keys compare [:date1 :date2]))})
                    (assoc compare :loading? true)))))

      (dissoc :loading?)))

(define-event LoadOperatorDates [operator-id compare-date1 compare-date2]
  {:path [:transit-visualization]}
  (comm/get! (str "transit-visualization/dates/" operator-id)
             {:on-success (tuck/send-async! ->LoadOperatorDatesResponse)})
  (assoc app
         :loading? true
         :operator-id operator-id
         :compare {:date1 compare-date1
                   :date2 compare-date2}))

(define-event LoadInfoResponse [info]
  {:path [:transit-visualization]}
  (-> app
      (assoc :operator-name (::t-operator/name info))))

(define-event LoadInfo [operator-id]
  {:path [:transit-visualization]}
  (comm/get! (str "transit-visualization/info/" operator-id)
             {:on-success (tuck/send-async! ->LoadInfoResponse)})
  app)

(define-event SetHighlightMode [mode]
  {:path [:transit-visualization :highlight]}
  (-> app
      (assoc :mode mode)))

(define-event SetCalendarMode [mode]
  {:path [:transit-visualization :calendar-mode]}
  mode)

(define-event DaysToFirstDiff [start-date date->hash]
  {:path [:transit-visualization :days-to-diff]}
  (days-to-first-diff start-date date->hash))

(defmethod routes/on-navigate-event :transit-visualization [{params :params query :query}]
  [(->LoadInfo (:operator-id params))
   (->LoadOperatorDates (:operator-id params) (:compare-date1 query) (:compare-date2 query))])

(define-event HighlightHash [hash day]
  {:path [:transit-visualization :highlight]}
  (-> app
      (merge {:hash hash :day day})))

(define-event SelectDateForComparison [date]
  {:path [:transit-visualization]}
  (let [operator-id (:operator-id app)]
    (update
     app :compare
     (fn [app]
       (let [app (or app {})
             last-selected (:last-selected app)
             date (time/format-date date)
             app (merge app
                        (if (not= 1 last-selected)
                          {:date1 date
                           ;; date1 = date2 by default. Date2 will be changed on the second click of day.
                           :date2 date
                           :last-selected 1}
                          {:date2 date
                           :last-selected 2}))]
         (if (and (:date1 app) (:date2 app))
           (do
             (comm/get! (str "transit-visualization/routes-for-dates/" operator-id)
                        {:params (select-keys app [:date1 :date2])
                         :on-success (tuck/send-async! ->RoutesForDatesResponse
                                                       (select-keys app [:date1 :date2]))})
             (assoc app :loading? true))
           app))))))

(define-event RouteLinesForDateResponse [geojson date]
  {:path [:transit-visualization :compare]}
  (cond
    (= date (:date1 app))
    (assoc app
           :date1-route-lines geojson
           :date1-show? true)

    (= date (:date2 app))
    (assoc app
           :date2-route-lines geojson
           :date2-show? true)

    :default
    app))

(define-event RouteTripsForDateResponse [trips date]
  {:path [:transit-visualization :compare]}
  (cond
    (= date (:date1 app))
    (assoc app :date1-trips trips)

    (= date (:date2 app))
    (assoc app :date2-trips trips)

    :default
    app))

(define-event SelectRouteForDisplay [route-short-name route-long-name trip-headsign]
  {:path [:transit-visualization]}
  (let [operator-id (:operator-id app)]
    (update
     app :compare
     (fn [app]
       (doseq [date [(:date1 app) (:date2 app)]
               :let [params (merge {:date date}
                                   (when route-short-name
                                     {:short route-short-name})
                                   (when route-long-name
                                     {:long route-long-name})
                                   (when trip-headsign
                                     {:headsign trip-headsign}))]]
         (comm/get! (str "transit-visualization/route-lines-for-date/" operator-id)
                    {:params params
                     :on-success (tuck/send-async! ->RouteLinesForDateResponse date)})
         (comm/get! (str "transit-visualization/route-trips-for-date/" operator-id)
                    {:params params
                     :on-success (tuck/send-async! ->RouteTripsForDateResponse date)}))
       (assoc app
              :date1-route-lines nil
              :date2-route-lines nil
              :date1-trips nil
              :date2-trips nil
              :route-short-name route-short-name
              :route-long-name route-long-name
              :trip-headsign trip-headsign)))))

(define-event ToggleRouteDisplayDate [date]
  {:path [:transit-visualization :compare]}
  (cond
    (= date (:date1 app))
    (update app :date1-show? not)

    (= date (:date2 app))
    (update app :date2-show? not)

    :default
    app))


(define-event ToggleDifferent []
  {:path [:transit-visualization :compare :different?]}
  (not app))

(define-event ToggleRouteDisplayStops []
  {:path [:transit-visualization :compare :show-stops?]}
  (not app))
