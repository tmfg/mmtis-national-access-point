(ns ote.app.controller.transport-service
  "Transport operator controls "                            ;; FIXME: Move transport-service related stuff to other file
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.db.transport-service :as t-service]
            [ote.ui.form :as form]
            [ote.app.controller.passenger-transportation :as pt]
            [ote.app.routes :as routes]
            [ote.time :as time]))

(defrecord AddPriceClassRow [])
(defrecord AddServiceHourRow [])
(defrecord RemovePriceClassRow [])
(defrecord SelectTransportServiceType [data])

(defrecord ModifyTransportService [id])
(defrecord ModifyTransportServiceResponse [response])

(defrecord DeleteTransportService [id])
(defrecord DeleteTransportServiceResponse [response])

(defrecord PublishTransportService [transport-service-id])
(defrecord PublishTransportServiceResponse [success? transport-service-id])

(extend-protocol tuck/Event

  AddPriceClassRow
  (process-event [_ app]
    (update-in app [:transport-service ::t-service/passenger-transportation ::t-service/price-classes]
               #(conj (or % []) {::t-service/currency "EUR"})))

  AddServiceHourRow
  (process-event [_ app]
    (update-in app [:transport-service ::t-service/passenger-transportation ::t-service/service-hours]
               #(conj (or % []) {::t-service/from (time/parse-time "08:00")})))

  RemovePriceClassRow
  (process-event [_ app]
    (assoc-in app [:t-service :price-class-open] false))

  SelectTransportServiceType
  (process-event [{data :data} app]
    ;; Clear selected transport type section from app state
    ;; Navigate to selected transport type form
    (routes/navigate! (get data ::t-service/type))
    (assoc app :transport-service {::t-service/type (::t-service/type data)}))

  ModifyTransportService
  (process-event [{id :id} app]
    (comm/get! (str "transport-service/" id)
               {:on-success (tuck/send-async! ->ModifyTransportServiceResponse)})
    app)

  ModifyTransportServiceResponse
  (process-event [{response :response} app]
    (-> app
      (assoc-in [(keyword ::t-service/type) ::t-service/contact-address] (get response ::t-service/contact-address))
      (assoc-in [(keyword ::t-service/type) ::t-service/contact-phone] (get response  ::t-service/contact-phone))
      (assoc-in [(keyword ::t-service/type) ::t-service/contact-email] (get response  ::t-service/contact-email))
      (assoc-in [(keyword ::t-service/type) ::t-service/homepage] (get response  ::t-service/homepage))
      (assoc :page (get response ::t-service/type)
             :transport-service response)))


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
      app))

  DeleteTransportService
  (process-event [{id :id} app]
    (comm/get! (str "transport-service/delete/" id)
               {:on-success (tuck/send-async! ->DeleteTransportServiceResponse)})
    app)

  DeleteTransportServiceResponse
  (process-event [{response :response} app]
    (.log js/console " deletoitiin ")
    )


  )
