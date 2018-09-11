(ns ote.app.controller.transit-visualization
  (:require [ote.communication :as comm]
            [tuck.core :as tuck :refer-macros [define-event]]
            [ote.app.routes :as routes]
            [cljs-time.core :as t]
            [cljs-time.format :as tf]
            [ote.time :as time]
            [ote.db.transport-operator :as t-operator]
            [taoensso.timbre :as log]
            [ote.transit-changes :as transit-changes]))

(def hash-colors
  ["#E1F4FD" "#DDF1D2" "#FFF7CE" "#E0B6F3" "#A4C9EB" "#FBDEC4"]
  #_["#52ef99" "#c82565" "#8fec2f" "#8033cb" "#5c922f" "#fe74fe" "#02531d"
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

(define-event LoadServiceChangesForDateResponse [response]
  {:path [:transit-visualization]}
  (assoc app
         :loading? false
         :service-info (:service-info response)
         :changes (:changes response)))

(define-event LoadServiceChangesForDate [service-id date]
  {}
  (comm/get! (str "transit-visualization/" service-id "/" date)
             {:on-success (tuck/send-async! ->LoadServiceChangesForDateResponse)})
  app)
(defmethod routes/on-navigate-event :transit-visualization [{params :params}]
  (->LoadServiceChangesForDate (:service-id params) (:date params)))

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

(defn combine-trips [{:keys [date1-trips date2-trips] :as compare}]
  (if (and date1-trips date2-trips)
    (when-let [first-common-stop (transit-changes/first-common-stop (concat date1-trips date2-trips))]
      (.log js/console "FIRST COMMON STOP: " first-common-stop)
      (let [first-common-stop
            #(assoc %
                    :first-common-stop first-common-stop
                    :first-common-stop-time (transit-changes/time-for-stop % first-common-stop))
            date1-trips (mapv first-common-stop date1-trips)
            date2-trips (mapv first-common-stop date2-trips)
            combined-trips (transit-changes/merge-by-closest-time
                            :first-common-stop-time
                            date1-trips date2-trips)]
        (assoc compare :combined-trips
               (mapv (fn [[l r]]
                       [l r (transit-changes/trip-stop-differences l r)])
                     combined-trips))))

    ;; Both dates not fetched, don't try to calculate
    (assoc compare :combined-trips nil)))

(define-event RouteTripsForDateResponse [trips date]
  {:path [:transit-visualization :compare]}
  (log/info (count trips) " for " date ", 1st trip:" (first trips))
  (combine-trips
   (cond
     (= date (:date1 app))
     (assoc app :date1-trips trips)

     (= date (:date2 app))
     (assoc app :date2-trips trips)

     :default
     app)))

(define-event RouteResponse [route-info]
  {:path [:transit-visualization]}
  (assoc app
         :date->hash (:calendar route-info)
         :hash->color (zipmap (distinct (vals (:calendar route-info)))
                              (cycle hash-colors
                                     ;; FIXME: after all colors are consumed, add some pattern style
                                     ))))

(defn fetch-routes-for-dates [compare service-id route date1 date2]
  (doseq [date [date1 date2]
          :let [params (merge {:date (time/format-date-iso-8601 date)}
                              (when-let [short (:gtfs/route-short-name route)]
                                {:short short})
                              (when-let [long (:gtfs/route-long-name route)]
                                {:long long})
                              (when-let [headsign (:gtfs/trip-headsign route)]
                                {:headsign headsign}))]
          :when date]
    (comm/get! (str "transit-visualization/" service-id "/route-lines-for-date")
               {:params params
                :on-success (tuck/send-async! ->RouteLinesForDateResponse date)})
    (comm/get! (str "transit-visualization/" service-id "/route-trips-for-date")
               {:params params
                :on-success (tuck/send-async! ->RouteTripsForDateResponse date)}))
  (assoc compare
         :date1 (:gtfs/current-week-date route)
         :date2 (:gtfs/different-week-date route)
         :date1-route-lines nil
         :date2-route-lines nil
         :date1-trips nil
         :date2-trips nil
         :show-stops? true))

(define-event SelectRouteForDisplay [route]
  {}
  (let [service-id (get-in app [:params :service-id])]
    (.log js/console "REITTI:" (pr-str route))
    (comm/get! (str "transit-visualization/" service-id "/route/"
                    (:gtfs/route-short-name route) "/"
                    (:gtfs/route-long-name route) "/"
                    (:gtfs/trip-headsign route))
               {:on-success (tuck/send-async! ->RouteResponse)})

    (-> app
        (assoc-in [:transit-visualization :selected-route] route)
        (update-in [:transit-visualization] dissoc :date->hash :hash->color)
        (update-in [:transit-visualization :compare] fetch-routes-for-dates
                   service-id route
                   (:gtfs/current-week-date route)
                   (:gtfs/different-week-date route))
        (assoc-in [:transit-visualization :compare :differences]
                  (select-keys route #{:gtfs/added-trips :gtfs/removed-trips
                                       :gtfs/trip-stop-sequence-changes
                                       :gtfs/trip-stop-time-changes})))))

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

(define-event SelectTripDescription [trip-description]
  {:path [:transit-visualization :compare :selected-trip-description]}
  trip-description)

(define-event ToggleShowPreviousYear []
  {:path [:transit-visualization :show-previous-year?]}
  (not app))

(define-event ToggleShowNextYear []
  {:path [:transit-visualization :show-next-year?]}
  (not app))

(define-event ToggleSection [section]
  {:path [:transit-visualization :open-sections]
   :app open-sections}
  (let [open? (get open-sections section true)]
    (assoc open-sections section (not open?))))

(define-event SelectTripPair [trip-pair]
  {:path [:transit-visualization :compare]}
  (assoc app
         :selected-trip-pair trip-pair
         :combined-stop-sequence (transit-changes/combined-stop-sequence
                                  (:first-common-stop (first trip-pair)) trip-pair)))
