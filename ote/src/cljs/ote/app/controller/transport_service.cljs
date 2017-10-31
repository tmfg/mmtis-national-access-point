(ns ote.app.controller.transport-service
  "Transport operator controls "                            ;; FIXME: Move transport-service related stuff to other file
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.db.transport-service :as t-service]
            [ote.ui.form :as form]
            [ote.app.routes :as routes]
            [ote.time :as time]
            [taoensso.timbre :as log]))


(def service-level-keys
  #{::t-service/contact-address
    ::t-service/contact-phone
    ::t-service/contact-email
    ::t-service/homepage
    ::t-service/name
    ::t-service/sub-type})

(defn service-type-from-combined-service-type
  "Returns service type keyword from combined type-subtype key."
  [type]
  (case type
    :passenger-transportation-taxi :passenger-transportation
    :passenger-transportation-request :passenger-transportation
    :passenger-transportation-schedule :passenger-transportation
    :terminal :terminal
    :rentals :rentals
    :parking :parking
    :brokerage :brokerage))

(defn subtype-from-combined-service-type
  "Returns service subtype keyword from combined type-subtype key."
  [type]
  (case type
    :passenger-transportation-taxi :taxi
    :passenger-transportation-request :request
    :passenger-transportation-schedule :schedule
    :terminal :terminal ;; No subtype for terminals - but it is still saved to database
    :rentals :rentals
    :parking :parking
    :brokerage :brokerage))

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

(declare move-service-level-keys-from-form
         move-service-level-keys-to-form)

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
    (let [service-type-subtype (get data :transport-service-type-subtype)
          type (service-type-from-combined-service-type service-type-subtype)
          sub-type (subtype-from-combined-service-type service-type-subtype)]
            (routes/navigate! type)
            (-> app
                (assoc :transport-service {::t-service/type type})
                (assoc-in [:transport-service (t-service/service-key-by-type type) ] {::t-service/sub-type sub-type}))))

  ModifyTransportService
  (process-event [{id :id} app]
    (comm/get! (str "transport-service/" id)
               {:on-success (tuck/send-async! ->ModifyTransportServiceResponse)})
    app)

  ModifyTransportServiceResponse
  (process-event [{response :response} app]
    (let [type (::t-service/type response)]
      (routes/navigate! type)
      (assoc app
             :transport-service (move-service-level-keys-to-form
                                 response
                                 (t-service/service-key-by-type type)))))


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
    (let [filtered-map (filter #(not= (:ote.db.transport-service/id %) (int response)) (get app :transport-services))]
      (assoc app :transport-services filtered-map))))


(defn move-service-level-keys-from-form
  "The form only sees the type specific level, move keys that are stored in the
  transport-service level there."
  [service from]
  (reduce (fn [service key]
            (-> service
                (assoc key (get-in service [from key]))
                (update from dissoc key)))
          service
          service-level-keys))

(defn move-service-level-keys-to-form
  "Reverse of `move-service-level-keys-from-form`."
  [service to]
  (reduce (fn [service key]
            (-> service
                (assoc-in [to key] (get service key))
                (dissoc key)))
          service
          service-level-keys))
