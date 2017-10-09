(ns ote.app.controller.passenger-transportation
  "Transport operator controls "                            ;; FIXME: Move transport-service related stuff to other file
  (:require [tuck.core :as t]
            [ote.communication :as comm]
            [ote.ui.form :as form]))

(defrecord EditPassengerTransportationState [data])
(defrecord SavePassengerTransportationToDb [])
(defrecord HandlePassengerTransportationResponse [service])

(extend-protocol t/Event

  EditPassengerTransportationState
  (process-event [{data :data} app]
    (update-in app [:transport-service :ote.db.transport-service/passenger-transportation] merge data))


  SavePassengerTransportationToDb
  (process-event [_ {service :transport-service :as app}]
    (let [service-data (-> service
                           (assoc :ote.db.transport-service/type :passenger-transportation)
                           (assoc :ote.db.transport-service/transport-operator-id (get-in app [:transport-operator :ote.db.transport-operator/id]))
                           (update :ote.db.transport-service/passenger-transportation form/without-form-metadata))]
      (comm/post! "passenger-transportation-info" service-data {:on-success (t/send-async! ->HandlePassengerTransportationResponse)})
      app))

  HandlePassengerTransportationResponse
  (process-event [{service :service} app]
    (update-in app [:transport-service] merge service)))
