(ns ote.app.controller.route
  "Route based traffic controller"
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.time :as time]
            [clojure.string :as str]
            [ote.app.controller.route.gtfs :as route-gtfs]
            [ote.db.transit :as transit]))

;; Load available stops from server (GeoJSON)
(defrecord LoadStops [])
(defrecord LoadStopsResponse [response])

;; Edit route basic info
(defrecord EditRoute [form-data])

;; Events to edit the route's stop sequence
(defrecord AddStop [feature])
(defrecord UpdateStop [idx stop])
(defrecord DeleteStop [idx])

;; Edit times
(defrecord InitRouteTimes []) ; initialize route times based on stop sequence
(defrecord NewStartTime [time])
(defrecord AddRouteTime [])
(defrecord EditStopTime [time-idx stop-idx form-data])

;; Event to set service calendar
(defrecord ToggleDate [date])
(defrecord EditServiceCalendarRules [rules])
(defrecord ClearServiceCalendar [])

;; Save route as GTFS
(defrecord SaveAsGTFS [])

(defrecord GoToStep [step])

;; Save route to database
(defrecord SaveToDb [])

(defn rule-dates
  "Evaluate a recurring schedule rule. Returns a sequence of dates."
  [{:keys [from to] :as rule}]
  (let [week-days (into #{}
                        (keep #(when (get rule %) %))
                        time/week-days)]
    (when (and from to (not (empty? week-days)))
      (for [d (time/date-range (time/js->date-time from)
                               (time/js->date-time to))
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

  EditRoute
  (process-event [{form-data :form-data} app]
    (update app :route merge form-data))

  AddStop
  (process-event [{feature :feature} app]
    ;; Add stop to current stop sequence
    (-> app
        (update-in [:route :stop-sequence]
                   (fn [stop-sequence]
                     (conj (or stop-sequence [])
                           (merge (into {}
                                        (map #(update % 0 keyword))
                                        (js->clj (aget feature "properties")))
                                  {:coordinates (vec (aget feature "geometry" "coordinates"))}))))
        (update-in [:route :selected-port-ids]
                   (fn [port-ids]
                     (conj (or port-ids #{})
                           (aget feature "properties" "port-id"))))))

  UpdateStop
  (process-event [{idx :idx stop :stop :as e} app]
    (update-in app [:route :stop-sequence idx]
               (fn [{old-arrival :arrival-time
                     old-departure :departure-time
                     :as old-stop}]
                 (let [new-stop (merge old-stop stop)]
                   ;; If old departure time is same as arrival and arrival
                   ;; was changed, also change departure time.
                   (if (and (= old-departure old-arrival)
                            (contains? stop :arrival-time))
                     (assoc new-stop
                            :departure-time (:arrival-time new-stop))
                     new-stop)))))

  DeleteStop
  (process-event [{idx :idx} app]
    (update-in app [:route :stop-sequence]
               (fn [stops]
                 (into (subvec stops 0 idx)
                       (subvec stops (inc idx))))))

  ToggleDate
  (process-event [{date :date} app]
    (update-in app [:route :service-calendar]
               (fn [{:keys [added-dates removed-dates rules] :as service-calendar}]
                 (let [added-dates (or added-dates #{})
                       removed-dates (or removed-dates #{})
                       date (time/date-fields date)]
                   (cond
                     ;; This date is in added dates, remove it
                     (added-dates date)
                     (assoc service-calendar :added-dates (disj added-dates date))

                     ;; This date is in removed dates, remove it
                     (removed-dates date)
                     (assoc service-calendar :removed-dates (disj removed-dates date))

                     ;; This date matches a rule, add it to removed dates
                     (some #(some (partial = date) (rule-dates %)) (:rules rules))
                     (assoc service-calendar :removed-dates (conj removed-dates date))

                     ;; Otherwise add this to added dates
                     :default
                     (assoc service-calendar :added-dates (conj added-dates date)))))))

  EditServiceCalendarRules
  (process-event [{rules :rules} app]
    (let [rule-dates (into #{}
                           (mapcat rule-dates)
                           (:rules rules))]
      (-> app
          (assoc-in [:route :service-calendar :rules] rules)
          (assoc-in [:route :service-calendar :rule-dates] rule-dates))))

  ClearServiceCalendar
  (process-event [_ app]
    (assoc-in app [:route :service-calendar] {}))

  GoToStep
  (process-event [{step :step} app]
    (assoc-in app [:route :step] step))


  InitRouteTimes
  (process-event [_ app]
    (assoc-in app [:route :times]
              [{:stops (get-in app [:route :stop-sequence])}]))

  NewStartTime
  (process-event [{time :time} app]
    (assoc-in app [:route :new-start-time] time))

  AddRouteTime
  (process-event [_ {route :route :as app}]
    (let [time (last (:times route))
          start-time (time/minutes-from-midnight (:departure-time (first (:stops time))))
          new-start-time (time/minutes-from-midnight (:new-start-time route))
          time-from-new-start #(when %
                                 (-> %
                                     time/minutes-from-midnight
                                     (- start-time)
                                     (+ new-start-time)
                                     time/minutes-from-midnight->time))
          update-times-from-new-start
          #(-> %
               (update :arrival-time time-from-new-start)
               (update :departure-time time-from-new-start))]
      (-> app
          (assoc-in [:route :new-start-time] nil)
          (update-in [:route :times]
                     (fn [times]
                       (conj (or times [])
                             {:stops (mapv update-times-from-new-start
                                           (:stops time))}))))))

  EditStopTime
  (process-event [{:keys [time-idx stop-idx form-data]} app]

    (update-in app [:route :times time-idx :stops stop-idx] merge form-data))

  SaveAsGTFS
  (process-event [_ {route :route :as app}]
    (route-gtfs/save-gtfs route (str (:name route) ".zip"))
    app))

(defn valid-stop-sequence?
  "Check if given route's stop sequence is valid. A stop sequence is valid
  if it is not empty and the first and last stops have a departure and arrival time respectively."
  [{:keys [stop-sequence] :as route}]
  (and (not (empty? stop-sequence))
       (:departure-time (first stop-sequence))
       (:arrival-time (last stop-sequence))))

(defn valid-basic-info?
  "Check if given route has a name and an operator."
  [{::transit/keys [name transport-operator-id]}]
  (and (not (str/blank? name))
       transport-operator-id))

(defn valid-stop-times?
  "Check if given route's stop times are valid.
  The first stop must have a departure time and the last stop must
  have an arrival time. All other stops must have both the arrival
  and the departure time."
  [{:keys [times]}]
  (every? (fn [{:keys [stops]}]
            (let [first-stop (first stops)
                  last-stop (last stops)
                  other-stops (rest (butlast stops))]
              (and (time/valid-time? (:departure-time first-stop))
                   (time/valid-time? (:arrival-time last-stop))
                   (every? #(and (time/valid-time? (:departure-time %))
                                 (time/valid-time? (:arrival-time %))) other-stops))))
          times))
