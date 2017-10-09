(ns ote.app.controller.transport-service
  "Transport operator controls "                            ;; FIXME: Move transport-service related stuff to other file
  (:require [tuck.core :as t]
            [ote.communication :as comm]
            [ote.ui.form :as form]))

(defrecord EditTransportServiceState [data])
(defrecord SaveTransportServiceToDb [])
(defrecord HandleTransportServiceResponse [passenger-transportation-data])

(extend-protocol t/Event

  EditTransportServiceState
  (process-event [{data :data} app]
    (update-in app :transport-service merge data))


  SaveTransportServiceToDb
  (process-event [_ {operator :transport-operator service :transport-service :as app}]
    (let [service-data {:ote.db.transport-service/type                  :passenger-transportation
                        :ote.db.transport-service/transport-operator-id (:ote.db.transport-operator/id operator)
                        :ote.db.transport-service/passenger-transportation
                                                                        (form/without-form-metadata service)}]
      (comm/post! "passenger-transportation-info" service-data {:on-success (t/send-async! ->HandleTransportServiceResponse)})
      app))

  HandleTransportServiceResponse
  (process-event [{passenger-transportation-data :passenger-transportation-data} app]
    (.log js/console " Ja sitte käsitellään reponse täällä pitäis olla jo id " (clj->js app))
    (assoc-in app [:transport-service] passenger-transportation-data)))