(ns ote.app.controller.transit-visualization
  (:require [ote.communication :as comm]
            [tuck.core :as tuck :refer-macros [define-event]]
            [ote.app.routes :as routes]
            [cljs-time.core :as t]
            [cljs-time.format :as tf]
            [cljs-time.coerce :as tc]
            [ote.time :as time]
            [ote.db.transport-operator :as t-operator]
            [ote.transit-changes :as tcu]
            [clojure.string :as str]
            [ote.app.controller.common :refer [->ServerError]]
            [taoensso.timbre :as log]))

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

(defn route-filtering-available? [{:keys [changes-route-no-change]}]
  (seq changes-route-no-change))

(defn loading-trips? [{:keys [route-lines-for-date-loading? route-trips-for-date1-loading?
                              route-trips-for-date2-loading? route-calendar-hash-loading?
                              route-differences-loading? routes-for-dates-loading?
                              service-changes-for-dates-loading?]}]
  (or route-lines-for-date-loading?
      route-trips-for-date1-loading?
      route-trips-for-date2-loading?
      route-calendar-hash-loading?
      route-differences-loading?
      routes-for-dates-loading?
      service-changes-for-dates-loading?))

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
  "When route is selected first trip needs to be selected as well from trip list. Set selected-trip-pair and combined-stop-sequence."
  [transit-visualization]
  (let [trip-pair (first (get-in transit-visualization [:compare :combined-trips]))]
    (-> transit-visualization
        (assoc-in [:compare :selected-trip-pair] trip-pair)
        (assoc-in [:compare :combined-stop-sequence]
                  (tcu/combined-stop-sequence (:first-common-stop (first trip-pair)) trip-pair))
        (assoc-in [:open-sections :trip-stop-sequence] true))))

(defn future-changes
  "Filter routes changes that are in the future. (or no changes)"
  [detection-date changes]
  (let [detection-date (time/parse-date-iso-8601 detection-date)]
    (filter
      (fn [{:keys [different-week-date]}]
        (or (nil? different-week-date)
            (not (t/before?
                   (time/native->date-time different-week-date)
                   detection-date))))
      changes)))

(defn count-changes [key coll]
  (count
    (get coll key)))

(defn- combine-change-types
  "Convert change collection types to one string"
  [coll]
  (str/join "/" (map :change-type coll)))

(defn- combine-change-values
  "Combine grouped route changes to the first route row (shown in routes list)"
  [single-route grouped-routes]
  (-> single-route
      (assoc :added-trips (apply + (map :added-trips grouped-routes)))
      (assoc :removed-trips (apply + (map :removed-trips grouped-routes)))
      (assoc :trip-stop-sequence-changes-lower (apply + (map :trip-stop-sequence-changes-lower grouped-routes)))
      (assoc :trip-stop-sequence-changes-upper (apply + (map :trip-stop-sequence-changes-upper grouped-routes)))
      (assoc :trip-stop-time-changes-lower (apply + (map :trip-stop-time-changes-lower grouped-routes)))
      (assoc :trip-stop-time-changes-upper (apply + (map :trip-stop-time-changes-upper grouped-routes)))))

(defn sorted-route-changes
  "Sort route changes according to change date and route-long-name: Earliest first and missing date last."
  [show-no-change changes]
  (let [;; Removed in past routes won't be displayed at the moment. They are ended routes and we do not need to list them.
        ;removed-in-past (sort-by (juxt :route-long-name :route-short-name) (filterv #(and (= :removed (:change-type %)) (nil? (:change-date %))) changes))
        no-changes (sort-by (juxt :route-long-name :route-short-name) (filterv #(= :no-change (:change-type %)) changes))
        only-changes (sort-by :different-week-date (filterv :different-week-date changes))
        ;; Group by only-changes by route-hash-id
        grouped-changes (group-by :route-hash-id only-changes)
        group-recent? (map
                        (fn [[_ changes]]
                          (some? (some :recent-change? changes)))
                        grouped-changes)
        ;; Take first from every vector
        route-changes (map #(first (second %)) grouped-changes)
        route-changes-with-recent (map
                                    (fn [change recent?]
                                      (assoc change :recent-change? recent?))
                                    route-changes
                                    group-recent?)
        route-changes (map (fn [x]
                             (-> x
                                 (assoc :count (count-changes (:route-hash-id x) grouped-changes))
                                 (assoc :combined-change-types (combine-change-types (get grouped-changes (:route-hash-id x))))
                                 (combine-change-values (get grouped-changes (:route-hash-id x)))))
                           route-changes-with-recent)
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

        :else
        (assoc app :route-lines-for-date-loading? false))

      ;; Add all received routes to shown map
      [:compare :show-route-lines] merge (zipmap route-line-names (repeat true)))))

(defn combined-trips-and-stop-differences
  "Combine trips from date1 and date2 vectors. When trips are combined calculate stop difference counts between those
  two vectors."
  [transit-visualization date1-trips date2-trips]
  (if-let [first-common-stop (tcu/first-common-stop (concat date1-trips date2-trips))]
    (let [first-common-stop
          #(assoc %
             :first-common-stop first-common-stop
             :first-common-stop-time (tcu/time-for-stop % first-common-stop))
          date1-trips (mapv first-common-stop date1-trips)
          date2-trips (mapv first-common-stop date2-trips)
          combined-trips (tcu/merge-trips-by-closest-time
                           :first-common-stop-time
                           date1-trips date2-trips)]

      ;; Calculate stop differences => {:stop-time-changes :stop-seq-changes}
      (assoc-in transit-visualization [:compare :combined-trips]
                (mapv (fn [[l r]]
                        [l r (tcu/trip-stop-differences l r)])
                      combined-trips)))

    ;; Can't find common stop
    (assoc-in transit-visualization [:compare :combined-trips] nil)))

(defn combine-trips [transit-visualization]
  (let [date1-trips (get-in transit-visualization [:compare :date1-trips])
        date2-trips (get-in transit-visualization [:compare :date2-trips])]
    (if (and (seq date1-trips) (seq date2-trips))
      (combined-trips-and-stop-differences transit-visualization date1-trips date2-trips)
      ;; Both dates not fetched, don't try to calculate - assume that route is a new one or ending
      (-> transit-visualization
          (combined-trips-and-stop-differences date1-trips date2-trips)
          (assoc-in [:compare :differences]
                    (if (and date1-trips (empty? date2-trips))
                      ;ending - count trips from date1, because date2 is empty
                      {:removed-trips (count date1-trips)}
                      ;added - count trips from date2 because date1 is empty
                      {:added-trips (count date2-trips)}))))))

;; Routes trip data
(define-event RouteTripsForDateResponse [trips date]
  {:path [:transit-visualization]}
  (let [app (cond
              (= date (get-in app [:compare :date1]))
              (-> app
                  (assoc :route-trips-for-date1-loading? false)
                  (assoc-in [:compare :date1-trips] trips))

              (= date (get-in app [:compare :date2]))
              (-> app
                  (assoc :route-trips-for-date2-loading? false)
                  (assoc-in [:compare :date2-trips] trips))

              :else app)
        app (if (or (:route-trips-for-date1-loading? app) (:route-trips-for-date2-loading? app))
              app
              ;; Combine only after all data available to avoid rendering incorrect numbers
              (combine-trips app))]
    ;; Wait until both trips are loaded
    ;; No need to render trips and stops while they are not ready
    (if (and (not (:route-trips-for-date1-loading? app))
             (not (:route-trips-for-date2-loading? app)))
      ;; Select first trip from trip list
      (select-first-trip app)
      app)))

(define-event RouteDifferencesResponse [response]
  {}
  (-> app
      (assoc :flash-message "Reitin muutokset ladattu.")
      (assoc-in [:transit-visualization :route-differences-loading?] false)
      (assoc-in [:transit-visualization :compare :differences] response)))

(defn- remove-date2-keys [coll]
  (assoc coll :date2 nil
              :date2-trips nil
              :date2-route-lines nil))

(defn fetch-trip-data-for-dates [{:keys [compare] :as t-vis} service-id route date1 date2]
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
                :on-success (tuck/send-async! ->RouteLinesForDateResponse date)
                :on-failure (tuck/send-async! ->ServerError)})
    (comm/get! (str "transit-visualization/" service-id "/route-trips-for-date")
               {:params params
                :on-success (tuck/send-async! ->RouteTripsForDateResponse date)
                :on-failure (tuck/send-async! ->ServerError)}))

  ;; Get differences for change
  (comm/get! (str "transit-visualization/" service-id "/route-differences")
             {:params {:date1 (time/format-date-iso-8601 date1)
                       :date2 (time/format-date-iso-8601 date2)
                       :route-hash-id (ensure-route-hash-id route)}

              :on-success (tuck/send-async! ->RouteDifferencesResponse)
              :on-failure (tuck/send-async! ->ServerError)})

  (assoc t-vis :compare
               (assoc compare
                 :show-route-lines {}
                 :date1 date1
                 :date2 date2
                 :date1-route-lines nil
                 :date2-route-lines nil
                 :date1-trips nil
                 :date2-trips nil
                 :show-stops? true
                 :differences nil)
               :route-trips-for-date1-loading? true
               :route-trips-for-date2-loading? true))

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
                            start-date                      ;; Return start-date because we did't find any better day.
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

                ;; No-traffic route, return current day when the no-traffic start is in the past
                (and
                  (= :no-traffic (:change-type route))
                  (t/after? (time/now) date1))
                (time/now)

                :else
                date1)
        date2 (if-let [date2 (:different-week-date route)]
                date2
                (time/days-from (tc/from-date (time/native->date-time date1)) 7))]
    (-> app
        ;; Clear flag here only after data is available for rendering, earlier clearing would render first old data to view
        (assoc-in [:transit-visualization :route-dates-selected-from-calendar?] false)
        (assoc-in [:transit-visualization :route-lines-for-date-loading?] true)
        (assoc-in [:transit-visualization :selected-route] route)
        (update-in [:transit-visualization :compare] dissoc
                   :selected-trip-pair
                   :combined-trips
                   :combined-stop-sequence)
        (update-in [:transit-visualization] dissoc :date->hash :hash->color)
        (update-in [:transit-visualization] fetch-trip-data-for-dates service-id route date1 date2)
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

(define-event SelectDatesForComparison [date]
  {}
  (let [service-id (get-in app [:params :service-id])
        date1 (get-in app [:transit-visualization :compare :date1])
        date2 (get-in app [:transit-visualization :compare :date2])
        route (get-in app [:transit-visualization :selected-route])
        goog-date1 (goog.date.DateTime. date1)
        goog-date (goog.date.DateTime. date)
        date-after-date1? (t/after? goog-date goog-date1)
        earlier-date (if date-after-date1? date1 date)
        later-date (if date-after-date1? date date1)]
    (cond
      (or (and date1 date2) (t/equal? goog-date1 goog-date)) ;; Re-selection of day pair after to replace previous day pair selection
      (-> app
          (assoc-in [:transit-visualization :compare :date1] date)
          (update-in [:transit-visualization :compare] remove-date2-keys)
          (assoc-in [:transit-visualization :route-dates-selected-from-calendar?] true))

      (nil? date2)
      (do
        (comm/get! (str "transit-visualization/" service-id "/route-differences")
                   {:params {:date1 (time/format-date-iso-8601 earlier-date)
                             :date2 (time/format-date-iso-8601 later-date)
                             :route-hash-id (ensure-route-hash-id route)}

                    :on-success (tuck/send-async! ->RouteDifferencesResponse)
                    :on-failure (tuck/send-async! ->ServerError)})
        (-> app
            (assoc-in [:transit-visualization :route-differences-loading?] true)
            (assoc-in [:transit-visualization :compare :date1] earlier-date)
            (assoc-in [:transit-visualization :compare :date2] later-date)
            (assoc-in [:transit-visualization]
                      (fetch-trip-data-for-dates (:transit-visualization app)
                                                 service-id
                                                 route
                                                 earlier-date
                                                 later-date)))))))


(defn- fetch-change-details
  "Takes `params` and initiates request for data for service id and date from service.
  Clears route selection from app state if fetch could not be initiated.
  Return: Updated app state"
  [app {:keys [date service-id] :as params} change]
  (if (and change service-id date)
    (do
      (comm/get! (str "transit-visualization/" service-id "/route")
                 {:params {:route-hash-id (ensure-route-hash-id change) ; ensure-route-hash-id might not be needed anymore...
                           :detection-date date}
                  :on-success (tuck/send-async! ->RouteCalendarDatesResponse change)
                  :on-failure (tuck/send-async! ->ServerError)})
      (-> app
          (assoc-in [:transit-visualization :params-previous] params)
          (assoc-in [:transit-visualization :route-calendar-hash-loading?] true)
          (assoc-in [:transit-visualization :compare :differences] nil)))
    (assoc-in app [:transit-visualization :selected-route] nil)))

(defn- url-route-hash-id->change [changes route-hash-id-url-format]
  (when-let [route-hash-id (js/decodeURIComponent route-hash-id-url-format)]
    (some #(when (= (:route-hash-id %) route-hash-id)
             %)
          changes)))

(defn- url-change-id->change [url-change-id url-route-hash-id changes-all]
  (when (and url-change-id url-route-hash-id changes-all)
    (let [route-hash-id-decoded (js/decodeURIComponent url-route-hash-id)
          diff-wk-date-decoded (js/decodeURIComponent url-change-id)]
      (some (fn [change]
              (when (and (= (:route-hash-id change) route-hash-id-decoded)
                         (= (str (:different-week-date change)) diff-wk-date-decoded))
                change))
            changes-all))))

(defn- url-params->change [{:keys [route-hash-id change-id]} changes-all]
  (or (url-change-id->change change-id route-hash-id changes-all)
      (url-route-hash-id->change changes-all route-hash-id)))

;; Detection date and selected route are taken as arg just to ensure url route is changed to what initiated the fetch
(define-event LoadChangedRoutesListResponse [response router-params]
  {}
  (let [{:keys [date scope] :as router-params} router-params
        detection-date date
        date-filter (if (= (name :now) scope)
                      (time/now-iso-date-str)
                      detection-date)
        changes (future-changes date-filter (:route-changes response))
        changes-all (sort-by :different-week-date < changes)
        route (url-params->change router-params changes-all)]

    (-> (fetch-change-details app router-params route)
        (assoc :transit-visualization
               (assoc (:transit-visualization app)
                 :service-changes-for-date-loading? false
                 :service-info (:service-info response)
                 :changes-all changes-all
                 :changes-route-no-change (sorted-route-changes true changes)
                 :changes-route-filtered (sorted-route-changes false changes)
                 :gtfs-package-info (:gtfs-package-info response)
                 :route-hash-id-type (:route-hash-id-type response)
                 :selected-route route
                 :detection-date detection-date)))))

(defn- fetch-changed-routes-list [app {:keys [service-id date] :as url-router-params}]
  (comm/get! (str "transit-visualization/" service-id "/" date)
             {:on-success (tuck/send-async! ->LoadChangedRoutesListResponse url-router-params)
              :on-failure (tuck/send-async! ->ServerError)})
  (assoc app
    :transit-visualization
    {:all-route-changes-display? false
     :all-route-changes-chenckbox false
     :open-sections {:gtfs-package-info false}
     :service-changes-for-date-loading? true
     :show-next-year? (or
                        (t/after?
                          (goog.date.DateTime. (js/Date.))
                          ;; Next years calendar will be shown by default if date is past 1.9.<current-year>
                          (goog.date.DateTime. (js/Date.
                                                 (.getFullYear (js/Date.)) 8 1)))
                        false)}))

(define-event UrlNavigation [url-router-params]
  {}
  (let [{:keys [service-id date route-hash-id change-id] :as params-new} (:params url-router-params)
        params-previous (:params-previous app)]
    (assoc
      ;; cond resolves new app state based on url changes. Move into a function if assocs get complicated in future.
      ;; Each case MUST RETURN AN APP STATE, please
      (cond
        (or (not= date (:date params-previous))
            (not= service-id (:service-id params-previous)))
        (fetch-changed-routes-list app params-new)

        ;; Use-case: selection of route-hash-id cleared. E.g. manually or via page history navigation
        (str/blank? route-hash-id)
        (assoc-in app [:transit-visualization :selected-route] nil)

        ;; Use-case: selection of route-hash-id changes. E.g. via user selection or browser history navigation
        (or (not= route-hash-id (:route-hash-id params-previous))
            (not= change-id (:change-id params-previous)))
        (fetch-change-details app
                              params-new
                              (url-params->change params-new
                                                  (get-in app [:transit-visualization :changes-all])))
        :else
        (do
          (log/error "Unexpected url navigation failed. Clearing selected route")
          (assoc-in app [:transit-visualization :selected-route] nil)))
      :params-previous params-new)))

(defmethod routes/on-navigate-event :transit-visualization [router-params]
  (->UrlNavigation router-params))

(define-event ToggleRouteDisplayDate [date]
  {:path [:transit-visualization :compare]}
  (cond
    (= date (:date1 app))
    (update app :date1-show? not)

    (= date (:date2 app))
    (update app :date2-show? not)

    :else
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
  {}
  (-> app
      (assoc-in [:transit-visualization :compare :selected-trip-pair] trip-pair)
      (assoc-in [:transit-visualization :compare :combined-stop-sequence]
                (tcu/combined-stop-sequence (:first-common-stop (first trip-pair)) trip-pair))
      (assoc-in [:transit-visualization :open-sections :trip-stop-sequence] true)))

(define-event ToggleShowRouteLine [routename]
  {:path [:transit-visualization :compare :show-route-lines]}
  (update app routename not))

(define-event InitiateRouteModelUpdate []
  {}
  (update-in app [:transit-visualization :all-route-changes-display?] not))

(define-event ToggleShowNoChangeRoutes [e!]
  {:path [:transit-visualization]}
  ;; Timeout used because toggling key for route-changes table directly may cause delay in rendering the content with large data set.
  ;; Thus disabling of UI components must happen before table model change because otherwise table rendering delays those as well.
  (.setTimeout js/window #(e! (->InitiateRouteModelUpdate)) 0)
  (update app :all-route-changes-checkbox not))
