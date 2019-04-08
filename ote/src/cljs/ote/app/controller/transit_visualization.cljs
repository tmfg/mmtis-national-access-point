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
  (or (:route-hash-id route) (str (:route-short-name route) "-" (:route-long-name route) "-" (:trip-headsign route))))

;; Order is relevant - change order to create different kind of color scheme in calendar
(def hash-colors
  [;; 70% opacity
   "#b3d1f0" "#b3e6eb" "#b3ebdb" "#b3e6b3" "#e0f0b3" "#f5f0b3" "#ffdbb3" "#f5b3b3" "#ffb3cc" "#f5b3e6" "#e0b3f5" "#c2c2f5"
   ;; 80% opacity
   "#cce0f5" "#cceef1" "#ccf1e7" "#cceecc" "#ebf5cc" "#f8f5cc" "#ffe7cc" "#f8cccc" "#ffccdd" "#f8ccee" "#ebccf8" "#d6d6f8"
   ;; 90% opacity
   "#e6f0fa" "#e6f7f8" "#e6f8f3" "#e6f7e6" "#f5fae6" "#fcfae6" "#fff3e6" "#fce6e6" "#ffe6ee" "#fce6f7" "#f5e6fc" "#ebebfc"
   ;; 50% opacity
   "#80b3e6" "#80d5dd" "#80ddc4" "#80d580" "#cce680" "#eee680" "#ffc480" "#ee8080" "#ff80aa" "#ee80d5" "#cc80ee" "#9999ee"
   ;; 60% opacity
   "#99c2eb" "#99dde4" "#99e4cf" "#99dd99" "#d6eb99" "#f1eb99" "#ffcf99" "#f19999" "#ff99bb" "#f199dd" "#d699f1" "#adadf1"])

(defn route-filtering-available? [{:keys [changes-route-no-change] :as transit-visualization}]
  (seq changes-route-no-change))

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

(defn count-changes [key coll]
  (count
    (get coll key)))

(defn sorted-route-changes
  "Sort route changes according to change date and route-long-name: Earliest first and missing date last."
  [show-no-change changes]
  (let [;; Removed in past routes won't be displayed at the moment. They are ended routes and we do not need to list them.
        ;removed-in-past (sort-by (juxt :route-long-name :route-short-name) (filterv #(and (= :removed (:change-type %)) (nil? (:change-date %))) changes))
        no-changes (sort-by (juxt :route-long-name :route-short-name) (filterv #(= :no-change (:change-type %)) changes))
        only-changes (sort-by :different-week-date (filterv :change-date changes))

        ;; Group by only-changes by route-hash-id
        grouped-changes (group-by #(:route-hash-id %) only-changes)
        ;; Take first from every vector
        route-changes (map #(first (second %)) grouped-changes)
        route-changes (map (fn [x]
                             (assoc x :count (count-changes (:route-hash-id x) grouped-changes)))
                           route-changes)
        sorted-changes (sort-by (juxt :different-week-date :route-long-name :route-short-name) route-changes)
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
    :changes-all (sort-by :different-week-date < (:route-changes response))
    :changes-route-no-change (sorted-route-changes true (future-changes detection-date (:route-changes response)))
    :changes-route-filtered (sorted-route-changes false (future-changes detection-date (:route-changes response)))
    :gtfs-package-info (:gtfs-package-info response)
    :route-hash-id-type (:route-hash-id-type response)))

(defn- init-view-state [app]
  (let [initial-view-state {:all-route-changes-checkbox nil
                            :all-route-changes-display? false
                            :open-sections {:gtfs-package-info false}
                            :service-changes-for-date-loading? true}]
    (assoc app :transit-visualization initial-view-state)))

(define-event InitTransitVisualization [service-id detection-date]
  {}
  (comm/get! (str "transit-visualization/" service-id "/" detection-date)
             {:on-success (tuck/send-async! ->LoadServiceChangesForDateResponse detection-date)})
  (init-view-state app))

(defmethod routes/on-navigate-event :transit-visualization [{params :params}]
  (->InitTransitVisualization (:service-id params) (:date params)))

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

(defn compare-stop-differences [transit-visualization date1-trips date2-trips]
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
          (assoc-in transit-visualization [:compare :combined-trips] nil)))


(defn combine-trips [transit-visualization]
  (let [date1-trips (get-in transit-visualization [:compare :date1-trips])
        date2-trips (get-in transit-visualization [:compare :date2-trips])
        loading (or (:route-trips-for-date1-loading? transit-visualization) ;; both trips should be loaded
                    (:route-trips-for-date2-loading? transit-visualization))]
    (cond
      (and (not loading)
           (not-empty date1-trips)
           (not-empty date2-trips))
      (compare-stop-differences transit-visualization date1-trips date2-trips)

      (not loading)
      ;; Both dates not fetched, don't try to calculate - assume that route is a new one or ending
      (-> transit-visualization
          (compare-stop-differences date1-trips date2-trips)
          (assoc-in [:compare :differences]
                    (if (and date1-trips (empty? date2-trips))
                      ;ending - count trips from date1, because date2 is empty
                      {:removed-trips (count date1-trips)}
                      ;added - count trips from date2 because date1 is empty
                      {:added-trips (count date2-trips)})))

      :default
      transit-visualization)))

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
    ;; Wait until both trips are loaded
    ;; No need to render trips and stops while they are not ready
    (if (and (not (:route-trips-for-date1-loading? app))
             (not (:route-trips-for-date2-loading? app)))
      (select-first-trip app)
      app)))

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
        current-week-date (or (:current-week-date (:changes (:transit-visualization app)))
                              (t/now))
        ;; Use dates in route, or default to current week date and 7 days after that.
        date1 (or (:current-week-date route) current-week-date)
        date1 (cond
                ;; No-change route, and current date doesn't have traffic
                (and
                  (= :no-change (:change-type route))
                  (nil? (get (:calendar response) (str (time/date-to-str-date (time/now))))))
                (get-next-best-day-for-no-change date1 date1 :plus (into {} (sort-by key < (:calendar response))))

                ;; No-change route, current date has traffic
                (and
                  (= :no-change (:change-type route))
                  (not (nil? (get (:calendar response) (str (time/date-to-str-date (time/now)))))))
                (time/now)

                ;; No-traffic route, return current day always
                (= :no-traffic (:change-type route))
                (time/now)

                :else
                date1)
        date2 (if (and
                    (:different-week-date route)
                    (not= :no-traffic (:change-type route)))
                  (:different-week-date route)
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
                  (select-keys route #{:added-trips :removed-trips
                                       :trip-stop-sequence-changes-lower
                                       :trip-stop-sequence-changes-upper
                                       :trip-stop-time-changes-lower
                                       :trip-stop-time-changes-upper}))
        (assoc-in [:transit-visualization :route-calendar-hash-loading?] false)
        (assoc-in [:transit-visualization :date->hash] (:calendar response))
        (assoc-in [:transit-visualization :hash->color] (zipmap (distinct (vals (:calendar response)))
                                                                (cycle hash-colors
                                                                       ;; FIXME: after all colors are consumed, add some pattern style
                                                                       ))))))

(define-event RouteDifferencesResponse [response]
  {}
  (-> app
      (assoc :flash-message "Reitin muutokset ladattu.")
      (assoc-in [:transit-visualization :route-differences-loading?] false)
      (assoc-in [:transit-visualization :compare :differences] response)))

(define-event SelectDatesForComparison [date]
              {}
              (let [service-id (get-in app [:params :service-id])
                    compare (or (get-in app [:transit-visualization :compare]) {})
                    date1 (get-in app [:transit-visualization :compare :date1])
                    date2 (get-in app [:transit-visualization :compare :date2])
                    route (get-in app [:transit-visualization :selected-route])
                    goog-date1 (goog.date.DateTime. date1)
                    goog-date (goog.date.DateTime. date)
                    date-after-date1? (t/after? goog-date goog-date1)
                    earlier-date (if date-after-date1? date1 date)
                    later-date (if date-after-date1? date date1)]
                (cond
                  (or (and date1 date2) (t/equal? goog-date1 goog-date))
                  (-> app
                      (assoc-in [:transit-visualization :compare :date1] date)
                      (assoc-in [:transit-visualization :compare :date2] nil))

                  (nil? date2)
                  (do
                    (comm/get! (str "transit-visualization/" service-id "/route-differences")
                               {:params {:date1 (time/format-date-iso-8601 earlier-date)
                                         :date2 (time/format-date-iso-8601 later-date)
                                         :route-hash-id (ensure-route-hash-id route)}

                                :on-success (tuck/send-async! ->RouteDifferencesResponse)})
                    (-> app
                        (assoc-in [:transit-visualization :route-differences-loading?] true)
                        (assoc-in [:transit-visualization :compare :date1]
                                  earlier-date)
                        (assoc-in [:transit-visualization :compare :date2]
                                  later-date)
                        (assoc-in [:transit-visualization :compare]
                                  (fetch-trip-data-for-dates compare service-id
                                                             route
                                                             earlier-date
                                                             later-date)))))))

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

(define-event InitiateRouteModelUpdate []
  {}
  (update-in app [:transit-visualization :all-route-changes-display?] not))

(define-event ToggleShowNoChangeRoutes [e!]
  {:path [:transit-visualization]}
  ;; Timeout used because toggling key for route-changes table directly may cause delay in rendering the content with large data set.
  ;; Thus disabling of UI components must happen before table model change because otherwise table rendering delays those as well.
  (.setTimeout js/window #(e! (->InitiateRouteModelUpdate)) 0)
  (update app :all-route-changes-checkbox not))
