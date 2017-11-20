(ns ote.app.controller.passenger-transportation
  "Transport operator controls "                            ;; FIXME: Move transport-service related stuff to other file
  (:require [tuck.core :as t]
            [ote.communication :as comm]
            [ote.ui.form :as form]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]
            [ote.app.controller.place-search :as place-search]
            [ote.app.controller.transport-service :as transport-service]
            [ote.app.routes :as routes]))

(defrecord EditPassengerTransportationState [data])

(defrecord HandlePassengerTransportationResponse [service])
(defrecord CancelPassengerTransportationForm [])


(extend-protocol t/Event

  EditPassengerTransportationState
  (process-event [{data :data} app]
    (update-in app [:transport-service ::t-service/passenger-transportation] merge data))

  HandlePassengerTransportationResponse
  (process-event [{service :service} app]
    (routes/navigate! :own-services)
    (assoc app
      :transport-service service
      :page :own-services))

  CancelPassengerTransportationForm
  (process-event [_ app]
    (routes/navigate! :own-services)
    app))
