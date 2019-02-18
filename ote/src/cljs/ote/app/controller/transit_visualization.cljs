(ns ote.app.controller.transit-visualization
  (:require [ote.communication :as comm]
            [tuck.core :as tuck :refer-macros [define-event]]
            [ote.app.routes :as routes]
            [ote.util.fn :refer [flip]]
            [cljs-time.core :as t]
            [cljs-time.format :as tf]
            [cljs-time.coerce :as tc]
            [ote.time :as time]
            [ote.util.url :as url-util]
            [ote.db.transport-operator :as t-operator]
            [taoensso.timbre :as log]
            [ote.transit-changes :as transit-changes]
            [clojure.set :as set]))

(defn ensure-route-hash-id
  "Some older detected route changes might not contain route-hash-id key, so ensure that one is found."
  [route]
  (or (:gtfs/route-hash-id route)
    (str (:gtfs/route-short-name route) "-" (:gtfs/route-long-name route) "-" (:gtfs/trip-headsign route))))

(def hash-colors
  ["#E1F4FD" "#DDF1D2" "#FFF7CE" "#E0B6F3" "#A4C9EB" "#FBDEC4"]
  #_["#52ef99" "#c82565" "#8fec2f" "#8033cb" "#5c922f" "#fe74fe" "#02531d"
   "#ec8fb5" "#23dbe1" "#a4515b" "#169294" "#fd5925" "#3d4e92" "#f4d403"
   "#66a1e5" "#d07d09" "#9382e9" "#b9cf84" "#544437" "#f2cdb9"])

(defn route-filtering-available? [{:keys [changes-filtered changes-no-change route-changes-loading?] :as transit-visualization}]
  (and (not route-changes-loading?)
       (seq (:gtfs/route-changes changes-no-change))))

(defn loaded-from-server? [{:keys [route-lines-for-date-loading? route-trips-for-date1-loading?
                                   route-trips-for-date2-loading? route-calendar-hash-loading?
                                   route-differences-loading? routes-for-dates-loading?
                                   service-changes-for-dates-loading?]
                            :as   transit-visualization}]
  (and (not route-lines-for-date-loading?)
       (not route-trips-for-date1-loading?)
       (not route-trips-for-date2-loading?)
       (not route-calendar-hash-loading?)
       (not route-differences-loading?)
       (not routes-for-dates-loading?)
       (not service-changes-for-dates-loading?)))

(defn parse-date [date-str]
  (tf/parse (tf/formatter "dd.MM.yyyy") date-str))

(defn days-to-first-diff [start-date date->hash]
  (let [hash (date->hash start-date)
        dt (parse-date start-date)
        first-diff (first
                     (sort #(t/before? (first %1) (first %2))
                           (filter #(and (not= hash (second %))
                                         (t/after? (first %) dt)
                                         (= (time/day-of-week (first %))
                                            (time/day-of-week dt)))
                                   (map (juxt (comp parse-date first)
                                              second) date->hash))))
        diff-date (first first-diff)]
    (when diff-date
      {:days (t/in-days (t/interval dt diff-date))
       :date diff-date})))

(defn select-first-trip
  "When route is selected first trip needs to be selected as well. Set selected-trip-pair and combined-stop-sequence."
  [transit-visualization]
  (let [trip-pair (first (get-in transit-visualization [:compare :combined-trips]))]
    (-> transit-visualization
        (assoc-in [:compare :selected-trip-pair] trip-pair)
        (assoc-in [:compare :combined-stop-sequence]
                  (transit-changes/combined-stop-sequence (:first-common-stop (first trip-pair)) trip-pair))
        (assoc-in [:open-sections :trip-stop-sequence] true))))

(defn future-changes
  "Filter routes changes that are in the future. (or no changes)"
  [detection-date changes]
  (let [detection-date (time/parse-date-iso-8601 detection-date)]
    (filter
      (fn [{:gtfs/keys [change-date]}]
          (or (nil? change-date)
              (not (t/before?
                     (time/native->date-time change-date)
                     detection-date))))
      changes)))


(defn sorted-route-changes
  "Sort route changes according to change date and route-long-name: Earliest first and missing date last."
  [show-no-change changes]
  (let [;; Removed in past routes won't be displayed at the moment. They are ended routes and we do not need to list them.
        removed-in-past (sort-by (juxt :gtfs/route-long-name :gtfs/route-short-name) (filterv #(and (= :removed (:gtfs/change-type %)) (nil? (:gtfs/change-date %))) changes))
        no-changes (sort-by (juxt :gtfs/route-long-name :gtfs/route-short-name) (filterv #(= :no-change (:gtfs/change-type %)) changes))
        only-changes (filterv :gtfs/change-date changes)
        sorted-changes (sort-by (juxt :gtfs/different-week-date :gtfs/route-long-name :gtfs/route-short-name) only-changes)
        all-sorted-changes (if show-no-change
                             (concat sorted-changes no-changes)
                             sorted-changes)]
    all-sorted-changes))

(define-event RoutesForDatesResponse [routes dates]
  {:path [:transit-visualization :compare]}
  (if (= dates (select-keys app [:date1 :date2]))
    (-> app
        (assoc :routes routes)
        (dissoc :routes-for-dates-loading?))
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
                  (comm/get! (str "transit-visualization/routes-for-dates/" (:operator-id app))
                             {:params (select-keys compare [:date1 :date2])
                              :on-success (tuck/send-async! ->RoutesForDatesResponse
                                                            (select-keys compare [:date1 :date2]))})
                  (assoc compare :routes-for-dates-loading? true))))

      (dissoc :routes-for-dates-loading?)))

(define-event LoadOperatorDates [operator-id compare-date1 compare-date2]
  {:path [:transit-visualization]}
  (comm/get! (str "transit-visualization/dates/" operator-id)
             {:on-success (tuck/send-async! ->LoadOperatorDatesResponse)})
  (assoc app
         :operator-dates-loading? true
         :operator-id operator-id
         :compare {:date1 compare-date1
                   :date2 compare-date2}))

(define-event LoadInfoResponse [info]
  {:path [:transit-visualization]}
  (assoc app :operator-name (::t-operator/name info)))

(define-event LoadInfo [operator-id]
  {:path [:transit-visualization]}
  (comm/get! (str "transit-visualization/info/" operator-id)
             {:on-success (tuck/send-async! ->LoadInfoResponse)})
  app)

(define-event SetHighlightMode [mode]
  {:path [:transit-visualization :highlight]}
  (assoc app :mode mode))

(define-event SetCalendarMode [mode]
  {:path [:transit-visualization :calendar-mode]}
  mode)

(define-event DaysToFirstDiff [start-date date->hash]
  {:path [:transit-visualization :days-to-diff]}
  (days-to-first-diff start-date date->hash))

(define-event LoadServiceChangesForDateResponse [response detection-date]
  {:path [:transit-visualization]}
  (assoc app
         :service-changes-for-date-loading? false
         :service-info (:service-info response)
         :changes-all (:changes response)
         :changes-no-change (update (:changes response) :gtfs/route-changes (comp (partial sorted-route-changes true) (partial future-changes detection-date)))
         :changes-filtered (update (:changes response) :gtfs/route-changes (comp (partial sorted-route-changes false) (partial future-changes detection-date)))
         :gtfs-package-info (:gtfs-package-info response)
         :route-hash-id-type (:route-hash-id-type response)))

(define-event LoadServiceChangesForDate [service-id detection-date]
  {}
  (comm/get! (str "transit-visualization/" service-id "/" detection-date)
             {:on-success (tuck/send-async! ->LoadServiceChangesForDateResponse detection-date)})
  (-> app
      (assoc-in [:transit-visualization :route-changes-loading?] true)
      (assoc-in [:transit-visualization :changes-no-change] nil)
      (assoc-in [:transit-visualization :changes-filtered] nil)
      (assoc-in [:transit-visualization :service-changes-for-date-loading?] true)
      (assoc-in [:transit-visualization :show-no-change-routes?] false)
      (assoc-in [:transit-visualization :open-sections :gtfs-package-info] false)))

(defmethod routes/on-navigate-event :transit-visualization [{params :params}]
  (->LoadServiceChangesForDate (:service-id params) (:date params)))

(define-event HighlightHash [hash day]
  {:path [:transit-visualization :highlight]}
  (merge app {:hash hash :day day}))

(define-event RouteLinesForDateResponse [geojson date]
  {:path [:transit-visualization]}
  (let [route-line-names (into #{}
                               (keep #(get-in % ["route-line" "properties" "routename"]))
                               (get geojson "features"))]
    (update-in
     (cond
       (= date (get-in app [:compare :date1]))
       (-> app
           (assoc :route-lines-for-date-loading? false)
           (assoc-in [:compare :date1-route-lines] geojson)
           (assoc-in [:compare :date1-show?] true))

       (= date (get-in app [:compare :date2]))
       (-> app
           (assoc :route-lines-for-date-loading? false)
           (assoc-in [:compare :date2-route-lines] geojson)
           (assoc-in [:compare :date2-show?] true))

       :default
       (assoc app :route-lines-for-date-loading? false))

     ;; Add all received routes to shown map
     [:compare :show-route-lines] merge (zipmap route-line-names (repeat true)))))

(defn combine-trips [transit-visualization]
  (let [date1-trips (get-in transit-visualization [:compare :date1-trips])
        date2-trips (get-in transit-visualization [:compare :date2-trips])]

  (if (and date1-trips date2-trips)
    (if-let [first-common-stop (transit-changes/first-common-stop (concat date1-trips date2-trips))]
      (let [first-common-stop
            #(assoc %
                    :first-common-stop first-common-stop
                    :first-common-stop-time (transit-changes/time-for-stop % first-common-stop))
            date1-trips (mapv first-common-stop date1-trips)
            date2-trips (mapv first-common-stop date2-trips)
            combined-trips (transit-changes/merge-by-closest-time
                            :first-common-stop-time
                            date1-trips date2-trips)]
        (assoc-in transit-visualization [:compare :combined-trips]
               (mapv (fn [[l r]]
                       [l r (transit-changes/trip-stop-differences l r)])
                     combined-trips)))

      ;; Can't find common stop
      (assoc-in transit-visualization [:compare :combined-trips] nil))

    ;; Both dates not fetched, don't try to calculate
    (assoc-in transit-visualization [:compare :combined-trips] nil))))

;; Routes trip data
(define-event RouteTripsForDateResponse [trips date]
  {:path [:transit-visualization]}
  (let [app (combine-trips
              (cond
                (= date (get-in app [:compare :date1]))
                (-> app
                    (assoc :route-trips-for-date1-loading? false)
                    (assoc-in [:compare :date1-trips] trips))

                (= date (get-in app [:compare :date2] app))
                (-> app
                    (assoc :route-trips-for-date2-loading? false)
                    (assoc-in [:compare :date2-trips] trips))

                :default
                app))]
    (select-first-trip app)))

(defn fetch-trip-data-for-dates [compare service-id route date1 date2]
  (doseq [date [date1 date2]
          :let [params (merge {:date (time/format-date-iso-8601 date)
                               :route-hash-id (ensure-route-hash-id route)}
                              (when-let [short (:gtfs/route-short-name route)]
                                {:short-name short})
                              (when-let [long (:gtfs/route-long-name route)]
                                {:long-name long})
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
    :show-route-lines {}
    :date1 date1
    :date2 date2
    :date1-route-lines nil
    :date2-route-lines nil
    :date1-trips nil
    :date2-trips nil
    :show-stops? true))

;; When route type is :no-change we need to find date that has traffic
(defn- get-next-best-day-for-no-change [start-date current-date direction calendar-days]
  (let [list-size (count calendar-days)
        day-difference (time/day-difference (time/to-local-js-date start-date) (time/to-local-js-date current-date))
        new-direction (if (and
                            (> day-difference list-size)
                            (= :plus direction))
                        :minus
                        direction)
        new-direction (if (and
                            (> (* -1 day-difference) list-size)
                            (= :minus direction))
                        :problem
                        new-direction)
        days-to-change (if (= :plus new-direction) 1 -1)
        new-date (time/days-from (tc/from-date current-date) days-to-change)
        first-not-nil-day (if (nil? (get calendar-days (str (time/date-to-str-date new-date))))
                            (get-next-best-day-for-no-change start-date new-date new-direction calendar-days)
                            new-date)
        first-not-nil-day (if (= :problem new-direction)
                            start-date ;; Return start-date because we did't find any better day.
                            first-not-nil-day)]
    first-not-nil-day))

;; Gets routes dates and hashes for dates. Passes data to calendar component and starts fetching trip and stop data
;; based on selected dates
(define-event RouteCalendarDatesResponse [response route]
  {}
  (let [service-id (get-in app [:params :service-id])
        current-week-date (or (get-in app [:transit-visualization :changes :gtfs/current-week-date])
                              (t/now))
        ;; Use dates in route, or default to current week date and 7 days after that.
        date1 (or (:gtfs/current-week-date route) current-week-date)
        date1 (cond
                ;; No-change route, and current date doesn't have traffic
                (and
                  (= :no-change (:gtfs/change-type route))
                  (nil? (get (:calendar response) (str (time/date-to-str-date (time/now))))))
                (get-next-best-day-for-no-change date1 date1 :plus (into {} (sort-by key < (:calendar response))))

                ;; No-change route, current date has traffic
                (and
                  (= :no-change (:gtfs/change-type route))
                  (not (nil? (get (:calendar response) (str (time/date-to-str-date (time/now)))))))
                (time/now)

                ;; No-traffic route, return current day always
                (= :no-traffic (:gtfs/change-type route))
                (time/now)

                :else
                date1)
        date2 (if (and
                    (:gtfs/different-week-date route)
                    (not= :no-traffic (:gtfs/change-type route)))
                  (:gtfs/different-week-date route)
                  (time/days-from (tc/from-date (time/native->date-time date1)) 7))]
    (-> app
        (assoc-in [:transit-visualization :route-lines-for-date-loading?] true)
        (assoc-in [:transit-visualization :route-trips-for-date1-loading?] true)
        (assoc-in [:transit-visualization :route-trips-for-date2-loading?] true)
        (assoc-in [:transit-visualization :selected-route] route)
        (update-in [:transit-visualization :compare] dissoc
                   :selected-trip-pair
                   :combined-trips
                   :combined-stop-sequence)
        (update-in [:transit-visualization] dissoc :date->hash :hash->color)
        (update-in [:transit-visualization :compare] fetch-trip-data-for-dates
                   service-id route
                   date1 date2)
        (assoc-in [:transit-visualization :compare :differences]
                  (select-keys route #{:gtfs/added-trips :gtfs/removed-trips
                                       :gtfs/trip-stop-sequence-changes
                                       :gtfs/trip-stop-time-changes}))
        (assoc-in [:transit-visualization :route-calendar-hash-loading?] false)
        (assoc-in [:transit-visualization :date->hash] (:calendar response))
        (assoc-in [:transit-visualization :hash->color] (zipmap (distinct (vals (:calendar response)))
                                                                (cycle hash-colors
                                                                       ;; FIXME: after all colors are consumed, add some pattern style
                                                                       ))))))

(define-event RouteDifferencesResponse [response]
  {:path [:transit-visualization]}
  (-> app
      (assoc :route-differences-loading? false)
      (assoc-in [:compare :differences] response)))

(define-event SelectDateForComparison [date]
  {}
  (let [service-id (get-in app [:params :service-id])
        compare (or (get-in app [:transit-visualization :compare]) {})
        route (get-in app [:transit-visualization :selected-route])
        date (goog.date.DateTime. date)
        date1 (goog.date.DateTime. (get-in app [:transit-visualization :compare :date1]))
        date2 (goog.date.DateTime. (get-in app [:transit-visualization :compare :date2]))
        last-selected-date (:last-selected-date compare 2)
        compare (merge compare
                       (cond (or (t/after? date1 date)
                                 (t/equal? date1 date))
                             {:date1 date
                              :last-selected-date 1}
                             (or (t/after? date date2)
                                 (t/equal? date date2))
                             {:date2 date
                              :last-selected-date 2}
                             :else
                             (if (= 2 last-selected-date)
                               {:date1 date
                                :last-selected-date 1}
                               {:date2 date
                                :last-selected-date 2})))]
    (comm/get! (str "transit-visualization/" service-id "/route-differences")
               {:params {:date1 (time/format-date-iso-8601 (:date1 compare))
                         :date2 (time/format-date-iso-8601 (:date2 compare))
                         :route-hash-id (ensure-route-hash-id route)}

                :on-success (tuck/send-async! ->RouteDifferencesResponse)})
    (-> app
        (assoc-in [:transit-visualization :route-differences-loading?] true)
        (assoc-in [:transit-visualization :compare]
                  (fetch-trip-data-for-dates compare service-id
                                          route
                                          (:date1 compare)
                                          (:date2 compare))))))

(define-event SelectRouteForDisplay [route]
  {}
  (comm/get! (str "transit-visualization/" (get-in app [:params :service-id]) "/route")
             {:params  {:route-hash-id (ensure-route-hash-id route)}
              :on-success (tuck/send-async! ->RouteCalendarDatesResponse route)})
  (assoc-in app [:transit-visualization :route-calendar-hash-loading?] true))

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

(define-event ToggleSection [section]
  {:path [:transit-visualization :open-sections]
   :app open-sections}
  (let [open? (get open-sections section true)]
    (assoc open-sections section (not open?))))

(define-event SelectTripPair [trip-pair]
  {}
  (-> app
      (assoc-in [:transit-visualization :compare :selected-trip-pair] trip-pair)
      (assoc-in [:transit-visualization :compare :combined-stop-sequence]
                (transit-changes/combined-stop-sequence (:first-common-stop (first trip-pair)) trip-pair))
      (assoc-in [:transit-visualization :open-sections :trip-stop-sequence] true)))

(define-event ToggleShowRouteLine [routename]
  {:path [:transit-visualization :compare :show-route-lines]}
  (update app routename not))

(define-event LoadingRoutesResponse []
  {:path [:transit-visualization]}
  (assoc app :route-changes-loading? false))

(define-event ToggleShowNoChangeRoutesDelayed []
  {}
  (update-in app [:transit-visualization :show-no-change-routes?] not))

(define-event ToggleShowNoChangeRoutes [e!]
  {:path [:transit-visualization]}
  ;; Timeout used because toggling route-changes table may cause delay in rendering the content with large data model.
  ;; Disabling of UI components must happen before table model change because otherwise table rendering
  ;; delays those as well.
  (.setTimeout js/window #(e! (->ToggleShowNoChangeRoutesDelayed)) 0)
  (-> app
      (update :show-no-change-routes-checkbox? not)
      (assoc :route-changes-loading? true)))
