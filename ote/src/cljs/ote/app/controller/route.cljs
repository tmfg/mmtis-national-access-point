(ns ote.app.controller.route
  "Route based traffic controller"
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.time :as time]
            [clojure.string :as str]
            [ote.app.controller.route.gtfs :as route-gtfs]
            [ote.db.transit :as transit]
            [ote.ui.form :as form]
            [ote.app.routes :as routes]))

;; Load available stops from server (GeoJSON)
(defrecord LoadStops [])
(defrecord LoadStopsResponse [response])

;; Initialize editing a new route
(defrecord InitRoute [])

;; Edit route basic info
(defrecord EditRoute [form-data])

;; Events to edit the route's stop sequence
(defrecord AddStop [feature])
(defrecord UpdateStop [idx stop])
(defrecord DeleteStop [idx])

;; Edit times
(defrecord InitRouteTimes []) ; initialize route times based on stop sequence
(defrecord NewStartTime [time])
(defrecord AddTrip [])
(defrecord EditStopTime [trip-idx stop-idx form-data])

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
(defrecord SaveToDb [])
(defrecord CancelRoute [])
(defrecord SaveRouteResponse [response])
(defrecord SaveRouteFailure [response])

(defn rule-dates
  "Evaluate a recurring schedule rule. Returns a sequence of dates."
  [{::transit/keys [from-date to-date] :as rule}]
  (let [week-days (into #{}
                        (keep #(when (get rule (keyword "ote.db.transit" (name %))) %))
                        time/week-days)]
    (when (and from-date to-date (not (empty? week-days)))
      (for [d (time/date-range (time/js->date-time from-date)
                               (time/js->date-time to-date))
            :when (week-days (time/day-of-week d))]
        (time/date-fields d)))))

(extend-protocol tuck/Event
  LoadStops
  (process-event [_ app]
    (let [on-success (tuck/send-async! ->LoadStopsResponse)]
      (comm/get! "finnish-ports.geojson"
                 {:on-success on-success
                  :response-format :json})
      app))

  LoadStopsResponse
  (process-event [{response :response} app]
    (assoc-in app [:route :stops] response))

  InitRoute
  (process-event [_ app]
    (update app :route assoc
            ::transit/stops []
            ::transit/trips []
            ::transit/service-calendars []
            ::transit/route-type :ferry))

  EditRoute
  (process-event [{form-data :form-data} app]
    (update app :route merge form-data))

  AddStop
  (process-event [{feature :feature} app]
    ;; Add stop to current stop sequence
    (-> app
        (update-in [:route ::transit/stops]
                   (fn [stop-sequence]
                     (conj (or stop-sequence [])
                           (merge (into {}
                                        (map #(update % 0 (partial keyword "ote.db.transit")))
                                        (js->clj (aget feature "properties")))
                                  {::transit/location (vec (aget feature "geometry" "coordinates"))}))))))

  UpdateStop
  (process-event [{idx :idx stop :stop :as e} app]
    (update-in app [:route ::transit/stops idx]
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
                     new-stop)))))

  DeleteStop
  (process-event [{idx :idx} app]
    (update-in app [:route ::transit/stops]
               (fn [stops]
                 (into (subvec stops 0 idx)
                       (subvec stops (inc idx))))))


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
                     (some #(some (partial = date) (rule-dates %)) service-rules)
                     (assoc service-calendar ::transit/service-removed-dates
                            (conj service-removed-dates date))

                     ;; Otherwise add this to added dates
                     :default
                     (assoc service-calendar ::transit/service-added-dates
                            (conj service-added-dates date)))))))

  EditServiceCalendarRules
  (process-event [{rules :rules trip-idx :trip-idx} app]
    (let [rule-dates (into #{}
                           (mapcat rule-dates)
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
    (assoc-in app [:route ::transit/trips]
              [{::transit/stop-times (vec (map-indexed
                                           (fn [stop-idx {::transit/keys [arrival-time departure-time]}]
                                             {::transit/stop-idx stop-idx
                                              ::transit/arrival-time arrival-time
                                              ::transit/departure-time departure-time})
                                           (get-in app [:route ::transit/stops])))}]))

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
                                                         (::transit/stop-times trip))})))
          (update-in [:route ::transit/service-calendars]
                     (fn [calendars]
                       (let [trip-idx (count (::transit/trips route))
                             prev-calendar (get-in calendars [(dec trip-idx)] nil)
                             calendar (get-in calendars [trip-idx] nil)]
                         (if (and (not calendar) prev-calendar)
                           (assoc calendars trip-idx prev-calendar)
                           calendars)))))))

  EditStopTime
  (process-event [{:keys [trip-idx stop-idx form-data]} app]
    (update-in app [:route ::transit/trips trip-idx ::transit/stop-times stop-idx] merge form-data))

  SaveAsGTFS
  (process-event [_ {route :route :as app}]
    (route-gtfs/save-gtfs route (str (:name route) ".zip"))
    app)

  SaveToDb
  (process-event [_ app]
    (let [route (-> app :route form/without-form-metadata
                    (update ::transit/service-calendars #(mapv form/without-form-metadata %))
                    (dissoc :step :stops :new-start-time))]
      (comm/post! "routes/new" route
                  {:on-success (tuck/send-async! ->SaveRouteResponse)
                   :on-failure (tuck/send-async! ->SaveRouteFailure)}))
    app)

  SaveRouteResponse
  (process-event [{response :response} app]
    (routes/navigate! :routes)
    (dissoc app :route))

  SaveRouteFailure
  (process-event [{response :response} app]
    (.error js/console "Save route failed:" (pr-str response))
    (assoc app
      :flash-message-error "Reitin tallennus epäonnistui"))

  CancelRoute
  (process-event [_ app]
    (routes/navigate! :routes)
    (dissoc app :route)))

(defn valid-stop-sequence?
  "Check if given route's stop sequence is valid. A stop sequence is valid
  if it is not empty and the first and last stops have a departure and arrival time respectively."
  [{::transit/keys [stops] :as route}]
  (and (not (empty? stops))
       (::transit/departure-time (first stops))
       (::transit/arrival-time (last stops))))

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
              (and (time/valid-time? (::transit/departure-time first-stop))
                   (time/valid-time? (::transit/arrival-time last-stop))
                   (every? #(and (time/valid-time? (::transit/departure-time %))
                                 (time/valid-time? (::transit/arrival-time %))) other-stops))))
          trips))

(defn validate-previous-steps
  "To be able to select a step in wizard that is valid, we call all previous validate functions."
  [route step-name wizard-steps]
  (every? (fn [{validate :validate}]
            (if validate
              (validate route)
              true))
            (take-while #(not= step-name (:name %)) wizard-steps)))