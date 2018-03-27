(ns ote.app.controller.route
  "Route based traffic controller"
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.time :as time]
            [clojure.string :as str]
            [ote.app.controller.route.gtfs :as route-gtfs]
            [ote.db.transit :as transit]
            [ote.db.transport-operator :as t-operator]
            [ote.ui.form :as form]
            [ote.app.routes :as routes]
            [ote.util.fn :refer [flip]]
            [clojure.set :as set]
            [ote.localization :refer [tr tr-key]]))

;; Load available stops from server (GeoJSON)
(defrecord LoadStops [])
(defrecord LoadStopsResponse [response])

;; Initialize editing a new route
(defrecord InitRoute [])

;; Load existing route
(defrecord LoadRoute [id])
(defrecord LoadRouteResponse [response])

;; Edit route basic info
(defrecord EditRoute [form-data])

;; Events to edit the route's stop sequence
(defrecord AddStop [feature])
(defrecord UpdateStop [idx stop])
(defrecord DeleteStop [idx])

;; create, edit and remove custom stops
(defrecord CreateCustomStop [id geojson])
(defrecord UpdateCustomStop [stop])
(defrecord CloseCustomStopDialog [])
(defrecord UpdateCustomStopGeometry [id geojson])
(defrecord RemoveCustomStop [id])

;; add custom stop to stop sequence
(defrecord AddCustomStop [id])

;; Edit times
(defrecord InitRouteTimes []) ; initialize route times based on stop sequence
(defrecord NewStartTime [time])
(defrecord AddTrip [])
(defrecord EditStopTime [trip-idx stop-idx form-data])
(defrecord ShowStopException [stop-type stop-idx icon-type trip-idx])

;; Event to set service calendar
(defrecord EditServiceCalendar [trip-idx])
(defrecord CloseServiceCalendar [])
(defrecord ToggleDate [date trip-idx])
(defrecord EditServiceCalendarRules [rules trip-idx])
(defrecord ClearServiceCalendar [trip-idx])

;; Save route as GTFS
(defrecord SaveAsGTFS [])

(defrecord GoToStep [step])

;; Save route to database
(defrecord SaveToDb [exit-wizard?])
(defrecord CancelRoute [])
(defrecord SaveRouteResponse [response])
(defrecord SaveRouteFailure [response])

(defn- update-stop-by-idx [route stop-idx trip-idx update-fn & args]
  (update (get-in route [::transit/trips trip-idx]) ::transit/stop-times
          (fn [stops]
            (mapv #(if (= (::transit/stop-idx %) stop-idx)
                     (apply update-fn % args)
                     %)
                  stops))))

(defn- update-stop-times
  "Copy departure and arrival time for stops from first trip. Stops can't have departure time in db, but in
  ui they can."
  [stops trips]
    (map-indexed
      (fn [idx item]
        (assoc item ::transit/departure-time
                    (get-in (first trips) [::transit/stop-times idx ::transit/departure-time])
                    ::transit/arrival-time
                    (get-in (first trips) [::transit/stop-times idx ::transit/arrival-time])))
      stops))

(defn update-trips-calendar
  "In database one service-calendar can be linked to all trips, but in front-end we need to copy or multiply
  service-calendars according to trips service-calendar-idx"
  [trips service-calendars]
  (let [new-calendars
        (mapv
          (fn [trip]
            (if (empty? service-calendars)
              {::transit/service-added-dates   #{}
               ::transit/service-removed-dates #{}
               ::transit/service-rules         []
               :rule-dates                     #{}}

              (let [cal-idx (::transit/service-calendar-idx trip)
                    cal (nth service-calendars cal-idx)
                    rule-dates (into #{}
                                     (mapcat transit/rule-dates)
                                     (::transit/service-rules cal))]
                (-> cal
                    (assoc :rule-dates rule-dates)
                    (assoc ::transit/service-added-dates (into #{}
                                                               (map #(time/date-fields-from-timestamp %)
                                                                    (::transit/service-added-dates cal))))
                    (assoc ::transit/service-removed-dates (into #{}
                                                                 (map #(time/date-fields-from-timestamp %)
                                                                      (::transit/service-removed-dates cal))))))))
          trips)]
    new-calendars))

(defn- set-saved-transfer-operator
  [app route]
  (assoc app :transport-operator
         (:transport-operator
          (some #(when (= (::transit/transport-operator-id route)
                          (get-in % [:transport-operator ::t-operator/id]))
                   %)
                (:transport-operators-with-services app)))))

(extend-protocol tuck/Event
  LoadStops
  (process-event [_ app]
    (let [on-success (tuck/send-async! ->LoadStopsResponse)]
      (comm/get! "transit/stops.json"
                 {:on-success on-success
                  :response-format :json})
      app))

  LoadStopsResponse
  (process-event [{response :response} app]
    (assoc-in app [:route :stops] response))

  LoadRoute
  (process-event [{id :id} app]
    (let [on-success (tuck/send-async! ->LoadRouteResponse)]
      (comm/get! (str "routes/" id)
                 {:on-success on-success})
      app))

  LoadRouteResponse
  (process-event [{response :response} app]
    (let [trips (::transit/trips response)
          service-calendars (update-trips-calendar trips (::transit/service-calendars response))
          stop-coordinates (mapv #(update % ::transit/location (fn [stop] (:coordinates stop)) ) (::transit/stops response))
          stops (if (empty? trips)
                  stop-coordinates
                  (update-stop-times stop-coordinates trips))
          trips (vec (map-indexed (fn [i trip] (assoc trip ::transit/service-calendar-idx i)) trips))]

      (-> app
        (assoc :route response)
        (assoc-in [:route ::transit/stops] stops)
        (assoc-in [:route ::transit/trips] trips)
        (assoc-in [:route ::transit/service-calendars] service-calendars))))

  InitRoute
  (process-event [_ app]
    (update app :route assoc
            ::transit/stops []
            ::transit/trips []
            ::transit/service-calendars []
            ::transit/route-type :ferry
            ::transit/transport-operator-id (get-in app [:transport-operator ::t-operator/id])))

  EditRoute
  (process-event [{form-data :form-data} app]
    (update app :route merge form-data))

  AddStop
  (process-event [{feature :feature} app]
    ;; Add stop to current stop sequence
    (let [properties (js->clj (aget feature "properties"))
          stop-sequence (into [] (get-in app [:route ::transit/stops])) ;; Ensure, that we use vector and not list
          new-stop (dissoc (merge (into {}
                                        (map #(update % 0 (partial keyword "ote.db.transit")))
                                        properties)
                                  {::transit/location (vec (aget feature "geometry" "coordinates"))})
                           ::transit/country
                           ::transit/country-code
                           ::transit/unlocode
                           ::transit/port-type
                           ::transit/port-type-name
                           ::transit/type)
          new-stop-sequence (if (= (::transit/code (last stop-sequence)) (get  properties "code"))
                              stop-sequence
                              (conj stop-sequence new-stop))]
      (-> app
        (assoc-in [:route ::transit/stops] new-stop-sequence)
        (assoc-in [:route ::transit/trips] [])
        (assoc-in [:route ::transit/service-calendars] []))))

  AddCustomStop
  (process-event [{id :id} {route :route :as app}]
    ;; Add stop to current route stop sequence (:route ::transit/stops)
    ;; And add stop to current map marker stop sequence (:route :stops "feature")
    (let [stop-sequence (into [] (get-in app [:route ::transit/stops])) ;; Ensure, that we use vector and not list
          {feature :geojson :as custom-stop}
          (first (keep #(when (= (:id %) id) %) (:custom-stops route)))
          geometry (vec (aget feature "geometry" "coordinates"))
          properties (js->clj (aget feature "properties"))
          new-custom-stop (merge (into {}
                                       (map #(update % 0 (partial keyword "ote.db.transit")))
                                       properties)
                                 {::transit/location geometry})
          new-stop-sequence (if (= (::transit/code (last stop-sequence)) (get properties "code"))
                              stop-sequence
                              (conj stop-sequence new-custom-stop))
          new-stop {"geometry"   {"type" "Point", "coordinates" geometry}
                    "properties" properties
                    "type"       "Feature"}
          stops (get-in app [:route :stops "features"])]
      (-> app
          (assoc-in [:route :stops "features"] (conj stops new-stop))
          (assoc-in [:route ::transit/stops] new-stop-sequence))))

  CreateCustomStop
  (process-event [{id :id geojson :geojson} app]
    (-> app
        (update-in [:route :custom-stops]
                   (fnil conj [])
                   {:id id
                    :geojson geojson})
        (assoc-in [:route :custom-stop-dialog] true)))

  UpdateCustomStop
  (process-event [{stop :stop} app]
    (let [idx (dec (count (:custom-stops (:route app))))]
      (update-in app [:route :custom-stops idx] merge stop)))

  UpdateCustomStopGeometry
  (process-event [{id :id geojson :geojson} app]
    (-> app
        (update-in [:route :custom-stops] (flip mapv)
                   (fn [{stop-id :id :as stop}]
                     (if (= id stop-id)
                       (update stop :geojson
                               #(-> %
                                    js->clj
                                    (assoc :geometry (get (js->clj geojson) "geometry"))
                                    clj->js))
                       stop)))
        (update-in [:route ::transit/stops] (flip mapv)
                   (fn [{::transit/keys [custom code] :as stop}]
                     (if (and custom (= code id))
                       (assoc stop ::transit/location
                              (get-in (js->clj geojson) ["geometry" "coordinates"]))
                       stop)))))

  CloseCustomStopDialog
  (process-event [_ {route :route :as app}]
    (-> app
        (update-in [:route :custom-stops (dec (count (:custom-stops route)))]
                   (fn [stop]
                     (-> stop
                         (update :geojson
                                 #(-> %
                                      js->clj
                                      (assoc :properties
                                             {:name (:name stop)
                                              :code (:id stop)
                                              :custom true})
                                      clj->js)))))
        (update :route dissoc :custom-stop-dialog)))

  RemoveCustomStop
  (process-event [{id :id} app]
    (-> app
        (update-in [:route :custom-stops]
                   (flip filterv) #(not= (:id %) id))
        (update-in [:route ::transit/stops]
                   (flip filterv)
                   (fn [{::transit/keys [code custom]}]
                     (not (and custom (= code id)))))))

  UpdateStop
  (process-event [{idx :idx stop :stop :as e} app]
    (-> app
        (update-in [:route ::transit/stops idx]
                   (fn [{old-arrival ::transit/arrival-time
                         old-departure ::transit/departure-time
                         :as old-stop}]
                     (let [new-stop (merge old-stop stop)]
                       ;; If old departure time is same as arrival and arrival
                       ;; was changed, also change departure time.
                       (if (and (= old-departure old-arrival)
                                (contains? stop ::transit/arrival-time))
                         (assoc new-stop
                           ::transit/departure-time (::transit/arrival-time new-stop))
                         new-stop))))
        (assoc-in [:route ::transit/trips] [])
        (assoc-in [:route ::transit/service-calendars] [])))

  DeleteStop
  (process-event [{idx :idx} app]
    (-> app
        (update-in [:route ::transit/stops]
                   (fn [stops]
                     (into (subvec stops 0 idx)
                           (subvec stops (inc idx)))))
        (assoc-in [:route ::transit/trips] [])
        (assoc-in [:route ::transit/service-calendars] [])))


  EditServiceCalendar
  (process-event [{trip-idx :trip-idx} app]
    (assoc-in app [:route :edit-service-calendar] trip-idx))

  CloseServiceCalendar
  (process-event [_ app]
    (update-in app [:route] dissoc :edit-service-calendar))

  ToggleDate
  (process-event [{date :date trip-idx :trip-idx} app]
    (update-in app [:route ::transit/service-calendars trip-idx]
               (fn [{::transit/keys [service-added-dates service-removed-dates service-rules]
                     :as service-calendar}]
                 (let [service-added-dates (or service-added-dates #{})
                       service-removed-dates (or service-removed-dates #{})
                       date (time/date-fields date)]
                   (cond
                     ;; This date is in added dates, remove it
                     (service-added-dates date)
                     (assoc service-calendar ::transit/service-added-dates
                            (disj service-added-dates date))

                     ;; This date is in removed dates, remove it
                     (service-removed-dates date)
                     (assoc service-calendar ::transit/service-removed-dates
                            (disj service-removed-dates date))

                     ;; This date matches a rule, add it to removed dates
                     (some #(some (partial = date) (transit/rule-dates %)) service-rules)
                     (assoc service-calendar ::transit/service-removed-dates
                            (conj service-removed-dates date))

                     ;; Otherwise add this to added dates
                     :default
                     (assoc service-calendar ::transit/service-added-dates
                            (conj service-added-dates date)))))))

  EditServiceCalendarRules
  (process-event [{rules :rules trip-idx :trip-idx} app]
    (let [rule-dates (into #{}
                           (mapcat transit/rule-dates)
                           (::transit/service-rules rules))]
      (-> app
          (update-in [:route ::transit/service-calendars trip-idx] merge rules)
          (assoc-in [:route ::transit/service-calendars trip-idx :rule-dates] rule-dates))))

  ClearServiceCalendar
  (process-event [{trip-idx :trip-idx} app]
    (assoc-in app [:route ::transit/service-calendars trip-idx] {}))

  GoToStep
  (process-event [{step :step} app]
    (assoc-in app [:route :step] step))


  InitRouteTimes
  (process-event [_ app]
    (-> app
        (assoc-in [:route ::transit/trips]
                  [{::transit/stop-times (vec (map-indexed
                                                (fn [stop-idx {::transit/keys [arrival-time departure-time]}]
                                                  {::transit/stop-idx stop-idx
                                                   ::transit/arrival-time arrival-time
                                                   ::transit/departure-time departure-time})
                                                (get-in app [:route ::transit/stops])))
                    ::transit/service-calendar-idx 0}])
        ;; Make sure that we have an empty associated calendar for the trip
        (assoc-in [:route ::transit/service-calendars] [{}])))

  NewStartTime
  (process-event [{time :time} app]
    (assoc-in app [:route :new-start-time] time))

  AddTrip
  (process-event [_ {route :route :as app}]
    (let [trip (last (::transit/trips route))
          start-time (time/minutes-from-midnight (::transit/departure-time
                                                  (first (::transit/stop-times trip))))
          new-start-time (time/minutes-from-midnight (:new-start-time route))
          time-from-new-start #(when %
                                 (-> %
                                     time/minutes-from-midnight
                                     (- start-time)
                                     (+ new-start-time)
                                     time/minutes-from-midnight->time))
          update-times-from-new-start
          #(-> %
               (update ::transit/arrival-time time-from-new-start)
               (update ::transit/departure-time time-from-new-start))]
      (-> app
          (assoc-in [:route :new-start-time] nil)
          (update-in [:route ::transit/trips]
                     (fn [times]
                       (conj (or times [])
                             {::transit/stop-times (mapv update-times-from-new-start
                                                         (::transit/stop-times trip))
                              ::transit/service-calendar-idx (count (::transit/trips route))})))
          (update-in [:route ::transit/service-calendars]
                     (fn [calendars]
                       (let [trip-idx (count (::transit/trips route))
                             prev-calendar (get-in calendars [(dec trip-idx)] nil)
                             calendar (get-in calendars [trip-idx] nil)]
                         (cond
                           (and (not calendar) prev-calendar) (assoc calendars trip-idx prev-calendar)
                           (not calendar) (assoc calendars trip-idx {})
                           :else calendars)))))))

  EditStopTime
  (process-event [{:keys [trip-idx stop-idx form-data]} app]
    (update-in app [:route ::transit/trips trip-idx ::transit/stop-times stop-idx] merge form-data))

  ShowStopException
  (process-event [{stop-type :stop-type stop-idx :stop-idx icon-type :icon-type trip-idx :trip-idx} app]
    (let [icon-key (if (= "arrival" stop-type)
                     (keyword "ote.db.transit/pickup-type")
                     (keyword "ote.db.transit/drop-off-type"))]
    (assoc-in app [:route ::transit/trips trip-idx]
              (update-stop-by-idx
                (get app :route) stop-idx trip-idx
                assoc icon-key icon-type))))

  SaveAsGTFS
  (process-event [_ {route :route :as app}]
    (route-gtfs/save-gtfs route (str (:name route) ".zip"))
    app)

  SaveToDb
  (process-event [{exit-wizard? :exit-wizard?} app]
    (let [calendars (mapv form/without-form-metadata (get-in app [:route ::transit/service-calendars]))
          deduped-cals (into [] (distinct calendars))
          cals-indices (mapv #(first (keep-indexed
                                       (fn [i cal] (when (= cal %1) i))
                                       deduped-cals)) calendars)
          route (-> app :route form/without-form-metadata
                    (assoc ::transit/service-calendars deduped-cals)
                    (update ::transit/stops (fn [stop]
                                              (map
                                                #(dissoc % ::transit/departure-time ::transit/arrival-time)
                                                stop)))
                    (update ::transit/trips
                            (fn [trips]
                              ;; Update service-calendar indexes
                              (mapv
                                #(assoc % ::transit/service-calendar-idx
                                          (nth cals-indices (::transit/service-calendar-idx %)))
                                trips)))
                    (dissoc :step :stops :new-start-time :edit-service-calendar :exit-wizard?))]
      (comm/post! "routes/new" route
                  {:on-success (tuck/send-async! ->SaveRouteResponse)
                   :on-failure (tuck/send-async! ->SaveRouteFailure)})
      (-> app
        (set-saved-transfer-operator route)
        (assoc-in [:route :exit-wizard?] exit-wizard?))))

  SaveRouteResponse
  (process-event [{response :response} app]
    (let [exit-wizard? (get-in app [:route :exit-wizard?])
          app (assoc app :flash-message (tr [:route-wizard-page :save-success]))
          app (if exit-wizard?
               (dissoc app :route)
               app)]
      (if exit-wizard?
        (do
          (routes/navigate! :routes)
          (assoc app :page :routes))
        app)))

  SaveRouteFailure
  (process-event [{response :response} app]
    (.error js/console "Save route failed:" (pr-str response))
    (assoc app
      :flash-message-error (tr [:route-wizard-page :save-failure])))

  CancelRoute
  (process-event [_ app]
    (routes/navigate! :routes)
    (dissoc app :route)))

(defn validate-stop-times [first-stop last-stop other-stops]
  (and (time/valid-time? (::transit/departure-time first-stop))
       (time/valid-time? (::transit/arrival-time last-stop))
       (every? #(and (time/valid-time? (::transit/departure-time %))
                     (time/valid-time? (::transit/arrival-time %))) other-stops)))

(defn valid-stop-sequence?
  "Check if given route's stop sequence is valid. A stop sequence is valid
  if it is not empty and the first and last stops have a departure and arrival time respectively. And all other
  stops have valid arrival and departure times."
  [{::transit/keys [stops] :as route}]
  (let [first-stop (first stops)
        last-stop (last stops)
        other-stops (rest (butlast stops))]
    (validate-stop-times first-stop last-stop other-stops)))

(defn valid-basic-info?
  "Check if given route has a name and an operator."
  [{::transit/keys [name transport-operator-id]}]
  (and (not (str/blank? name))
       transport-operator-id))

(defn valid-trips?
  "Check if given route's trip stop times are valid.
  The first stop must have a departure time and the last stop must
  have an arrival time. All other stops must have both the arrival
  and the departure time."
  [{::transit/keys [trips]}]
  (every? (fn [{stops ::transit/stop-times}]
            (let [first-stop (first stops)
                  last-stop (last stops)
                  other-stops (rest (butlast stops))]
              (validate-stop-times first-stop last-stop other-stops)))
          trips))

(defn validate-previous-steps
  "To be able to select a step in wizard that is valid, we call all previous validate functions."
  [route step-name wizard-steps]
  (every? (fn [{validate :validate}]
            (if validate
              (validate route)
              true))
            (take-while #(not= step-name (:name %)) wizard-steps)))
