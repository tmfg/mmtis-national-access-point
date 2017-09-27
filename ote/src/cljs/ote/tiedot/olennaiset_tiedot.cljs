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
    (.log js/console "Tallennetaan tuli" (pr-str app))
    (let [operator-data (-> app
                            :transport-operator
                            form/without-form-metadata)]
      (comm/post! "/transport-operator" operator-data {:on-success (t/send-async! ->SaveTransportOperatorResponse)})
    app))

  SaveTransportOperatorResponse
  (process-event [{data :data} app]
    (.log js/console "TALLENNETTU " (pr-str data))
    (assoc app :transport-operator data
               :page :passenger-transportation))

  EditTransportService
  (process-event [{data :data} app]
    (.log js/console "EditTransportService" data)
    (update app :transport-service merge data))


  SavePassengerTransportData
  (process-event [_ {operator :transport-operator service :transport-service :as app}]
    (.log js/console "OPERATOR: " (pr-str operator))
    (let [service-data {:ote.domain.liikkumispalvelu/type :passenger-tranportation ;;FIXME: fix enum tranSportation
                        :ote.domain.liikkumispalvelu/transport-operator-id (:ote.domain.liikkumispalvelu/id operator)
                        :ote.domain.liikkumispalvelu/passenger-transportation
                        (form/without-form-metadata service)}]
      (.log js/console "POST data" (pr-str service-data)
      (comm/post! "/passenger-transportation-info" service-data {:on-success (t/send-async! SavePassengerTransportResponse)})
    app)))

  SavePassengerTransportResponse
  (process-event [{passenger-transportation-data :passenger-transportation-data} app]
    (.log js/console "SavePassengerTransportResponse data ->" passenger-transportation-data)
    (assoc app :service-provider passenger-transportation-data))

  )
