(ns ote.app.controller.passenger-transportation
  "Transport operator controls "                            ;; FIXME: Move transport-service related stuff to other file
  (:require [tuck.core :as t]
            [ote.communication :as comm]
            [ote.ui.form :as form]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]
            [ote.app.controller.place-search :as place-search]))

(defrecord EditPassengerTransportationState [data])
(defrecord SavePassengerTransportationToDb [])
(defrecord HandlePassengerTransportationResponse [service])

(extend-protocol t/Event

  EditPassengerTransportationState
  (process-event [{data :data} app]
    (update-in app [:transport-service ::t-service/passenger-transportation] merge data))


  SavePassengerTransportationToDb
  (process-event [_ {service :transport-service :as app}]
    (let [service-data
          (-> service
              (assoc ::t-service/type :passenger-transportation
                     ::t-service/transport-operator-id (get-in app [:transport-operator ::t-operator/id]))
              (update ::t-service/passenger-transportation form/without-form-metadata)
              (update-in [::t-service/passenger-transportation ::t-service/operation-area]
                         place-search/place-references))]
      (comm/post! "passenger-transportation-info"
                  service-data
                  {:on-success (t/send-async! ->HandlePassengerTransportationResponse)})
      app))

  HandlePassengerTransportationResponse
  (process-event [{service :service} app]
    (update-in app [:transport-service] merge service)))
