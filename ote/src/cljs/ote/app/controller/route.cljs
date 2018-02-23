(ns ote.app.controller.route
  "Route based traffic controller"
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]))

(defrecord LoadStops [])
(defrecord LoadStopsResponse [response])

(defrecord AddStop [feature])
(defrecord UpdateStop [idx stop])

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
    (.log js/console "GOT:" response)
    (assoc-in app [:route :stops] response))

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
                     new-stop))))))
