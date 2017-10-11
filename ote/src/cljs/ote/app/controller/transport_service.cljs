(ns ote.app.controller.transport-service
  "Transport operator controls "                            ;; FIXME: Move transport-service related stuff to other file
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.db.transport-service :as transport-service]
            [ote.ui.form :as form]
            [ote.app.controller.passenger-transportation :as pt]))

(defrecord AddPriceClassRow [])
(defrecord RemovePriceClassRow [])
(defrecord SelectTransportServiceType [data])

(defrecord ModifyTransportService [id])
(defrecord ModifyTransportServiceResponse [response])

(extend-protocol tuck/Event

  AddPriceClassRow
  (process-event [_ app]
    (update-in app [:transport-service ::transport-service/passenger-transportation ::transport-service/price-classes]
               #(conj (or % []) {::transport-service/currency "EUR"})))

  RemovePriceClassRow
  (process-event [_ app]
    (assoc-in app [:transport-service :price-class-open] false))

  SelectTransportServiceType
  (process-event [{data :data} app]
    (assoc app :page (get data ::transport-service/service-type)))

  ModifyTransportService
  (process-event [{id :id} app]
    (.log js/console " ModifyTransportService id " id (clj->js app))
    (comm/get! (str "transport-service/" id)
               {:on-success (tuck/send-async! ->ModifyTransportServiceResponse)})
    app)

  ModifyTransportServiceResponse
  (process-event [{response :response} app]
    (.log js/console " dada " (clj->js response) (clj->js app) (get response ::transport-service/type))
    (assoc app
      :page (get response ::transport-service/type)
      :transport-service response)

    )
)