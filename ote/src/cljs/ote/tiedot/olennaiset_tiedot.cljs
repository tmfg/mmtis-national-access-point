(ns ote.tiedot.palvelu
  "Liikkumispalvelun tietojen käsittely"
  (:require [tuck.core :as t]
            [ote.communication :as comm]
            [ote.ui.form :as form]))

(defrecord EditTransportOperator [data])
(defrecord SaveTransportOperator [])
(defrecord SaveTransportOperatorResponse [data])

(defrecord EditTransportService [data])
(defrecord SavePassengerTransportData [])
(defrecord SavePassengerTransportResponse [passenger-transportation-data])

(extend-protocol t/Event

  EditTransportOperator
  (process-event [{data :data} app]
    (update app :transport-operator merge data))

  SaveTransportOperator
  (process-event [_ app]
    (let [operator-data (-> app
                            :transport-operator
                            form/without-form-metadata)]
      (comm/post! "/transport-operator" operator-data {:on-success (t/send-async! ->SaveTransportOperatorResponse)})
    app))

  SaveTransportOperatorResponse
  (process-event [{data :data} app]
    (assoc app :transport-operator data
               :page :passenger-transportation))

  EditTransportService
  (process-event [{data :data} app]
    (update app :transport-service merge data))


  SavePassengerTransportData
  (process-event [_ {operator :transport-operator service :transport-service :as app}]
    (let [service-data {:ote.db.transport-service/type :passenger-transportation
                        :ote.db.transport-service/transport-operator-id (:ote.db.transport-operator/id operator)
                        :ote.db.transport-service/passenger-transportation
                        (form/without-form-metadata service)}]
      (comm/post! "/passenger-transportation-info" service-data {:on-success (t/send-async! ->SavePassengerTransportResponse)})
    app))

  SavePassengerTransportResponse
  (process-event [{passenger-transportation-data :passenger-transportation-data} app]
    (assoc app :service-provider passenger-transportation-data))
  )
