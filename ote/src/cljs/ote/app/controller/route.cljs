(ns ote.app.controller.route
  "Route based traffic controller"
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.time :as time]
            [clojure.string :as str]))

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

(defrecord GoToStep [step])

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
    (update-in app [:route :dates]
               (fn [dates]
                 (let [date (time/date-fields date)
                       selected-dates (or dates #{})]
                   (if (selected-dates date)
                     (disj selected-dates date)
                     (conj selected-dates date))))))


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
      (update-in app [:route :times]
                 (fn [times]
                   (conj (or times [])
                         {:stops (mapv update-times-from-new-start
                                       (:stops time))})))))

  EditStopTime
  (process-event [{:keys [time-idx stop-idx form-data]} app]

    (update-in app [:route :times time-idx :stops stop-idx] merge form-data)))

(defn valid-stop-sequence?
  "Check if given route's stop sequence is valid. A stop sequence is valid
  if it is not empty and the first and last stops have a departure and arrival time respectively."
  [{:keys [stop-sequence] :as route}]
  (and (not (empty? stop-sequence))
       (:departure-time (first stop-sequence))
       (:arrival-time (last stop-sequence))))

(defn valid-basic-info?
  "Check if given route's has a name and an operator."
  [{:keys [name transport-operator]}]
  (and (not (str/blank? name))
       transport-operator))
