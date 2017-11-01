(ns ote.app.controller.brokerage
  "Rental service controls "
  (:require [tuck.core :as t]
            [ote.communication :as comm]
            [ote.ui.form :as form]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]
            [ote.app.controller.place-search :as place-search]))

(defrecord EditBrokerageState [data])
(defrecord SaveBrokerageToDb [])
(defrecord HandleBrokerageResponse [service])

(extend-protocol t/Event

  EditBrokerageState
  (process-event [{data :data} app]
    (update-in app [:transport-service ::t-service/rental] merge data))


  SaveBrokerageToDb
  (process-event [_ {service :transport-service :as app}]
    (let [service-data
          (-> service
              (assoc ::t-service/type :brokerage
                     ::t-service/transport-operator-id (get-in app [:transport-operator ::t-operator/id]))
              (update ::t-service/brokerage form/without-form-metadata)
              (update-in [::t-service/brokerage ::t-service/operation-area]
                         place-search/place-references))]
      (comm/post! "rental"
                  service-data
                  {:on-success (t/send-async! ->HandleBrokerageResponse)})
      app))

  HandleBrokerageResponse
  (process-event [{service :service} app]
    (assoc app
      :transport-service service
      :page :own-services)))
