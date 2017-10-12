(ns ote.app.controller.transport-service
  "Transport operator controls "                            ;; FIXME: Move transport-service related stuff to other file
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.db.transport-service :as t-service]
            [ote.ui.form :as form]
            [ote.app.controller.passenger-transportation :as pt]))

(defrecord AddPriceClassRow [])
(defrecord AddServiceHourRow [])
(defrecord RemovePriceClassRow [])
(defrecord SelectTransportServiceType [data])

(defrecord ModifyTransportService [id])
(defrecord ModifyTransportServiceResponse [response])

(defrecord PublishTransportService [transport-service-id])
(defrecord PublishTransportServiceResponse [success? transport-service-id])

(extend-protocol tuck/Event

  AddPriceClassRow
  (process-event [_ app]
    (update-in app [:transport-service ::t-service/passenger-transportation ::t-service/price-classes]
               #(conj (or % []) {::t-service/currency "EUR"})))

  AddServiceHourRow
  (process-event [_ app]
    (update-in app [:transport-service ::transport-service/passenger-transportation ::transport-service/service-hours]
               #(conj (or % []) {::transport-service/from "08:00"})))             

  RemovePriceClassRow
  (process-event [_ app]
    (assoc-in app [:transport-service :price-class-open] false))

  SelectTransportServiceType
  (process-event [{data :data} app]
    (assoc app :page (get data ::t-service/service-type)))

  ModifyTransportService
  (process-event [{id :id} app]
    (.log js/console " ModifyTransportService id " id (clj->js app))
    (comm/get! (str "transport-service/" id)
               {:on-success (tuck/send-async! ->ModifyTransportServiceResponse)})
    app)

  ModifyTransportServiceResponse
  (process-event [{response :response} app]
    (.log js/console " dada " (clj->js response) (clj->js app) (get response ::t-service/type))
     (assoc app
      :page :passenger-transportation
      :transport-service response))

  PublishTransportService
  (process-event [{:keys [transport-service-id]} app]
    (comm/post! (str "transport-service/publish")
                {:transport-service-id transport-service-id}
                {:on-success (tuck/send-async! ->PublishTransportServiceResponse transport-service-id)})
    app)

  PublishTransportServiceResponse
  (process-event [{success? :success? transport-service-id :transport-service-id :as e} app]

    (if success?
      (update app :transport-services
              (fn [services]
                (map (fn [{id ::t-service/id :as service}]
                       (if (= id transport-service-id)
                         (assoc service ::t-service/published? true)
                         service))
                     services)))
      app)))
