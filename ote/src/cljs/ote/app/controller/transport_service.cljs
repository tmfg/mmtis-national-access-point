(ns ote.app.controller.transport-service
  "Transport operator controls "                            ;; FIXME: Move transport-service related stuff to other file
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.db.transport-service :as t-service]
            [ote.ui.form :as form]
            [ote.app.routes :as routes]
            [ote.time :as time]
            [taoensso.timbre :as log]
            [ote.db.transport-operator :as t-operator]
            [ote.localization :refer [tr tr-key]]
            [ote.app.controller.place-search :as place-search]))


(def service-level-keys
  #{::t-service/contact-address
    ::t-service/contact-phone
    ::t-service/contact-email
    ::t-service/homepage
    ::t-service/name
    ::t-service/sub-type
    ::t-service/external-interfaces
    ::t-service/operation-area
    ::t-service/companies})

(defn service-type-from-combined-service-type
  "Returns service type keyword from combined type-subtype key."
  [type]
  (case type
    :passenger-transportation-taxi :passenger-transportation
    :passenger-transportation-request :passenger-transportation
    :passenger-transportation-other :passenger-transportation
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
    :passenger-transportation-other :other
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
(defrecord OpenTransportServicePage [id])


(defrecord DeleteTransportService [id])
(defrecord ConfirmDeleteTransportService [id])
(defrecord CancelDeleteTransportService [id])
(defrecord DeleteTransportServiceResponse [response])

(defrecord PublishTransportService [transport-service-id])
(defrecord PublishTransportServiceResponse [success? transport-service-id])

(defrecord SaveTransportService [publish?])
(defrecord SaveTransportServiceResponse [response])
(defrecord CancelTransportServiceForm [])

(declare move-service-level-keys-from-form
         move-service-level-keys-to-form)

(defn- update-service-by-id [app id update-fn & args]
  (update app :transport-services
          (fn [services]
            (map #(if (= (::t-service/id %) id)
                    (apply update-fn % args)
                    %)
                 services))))

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
     (assoc app :transport-service-loaded? false))

  ModifyTransportServiceResponse
  (process-event [{response :response} app]
    (let [type (::t-service/type response)]
      (assoc app
        :transport-service-loaded? true
        :transport-service
             (-> response
                 (update ::t-service/operation-area place-search/operation-area-to-places)
                 (move-service-level-keys-to-form (t-service/service-key-by-type type))))))

  ;; Use this when navigating outside of OTE. Above methods won't work from NAP.
  OpenTransportServicePage
  (process-event [{id :id} app]
    (set! (.-location js/window) (str "/ote/index.html#/edit-service/" id))
    app)

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
    (update-service-by-id
     app id
     assoc :show-delete-modal? true))

  CancelDeleteTransportService
  (process-event [{id :id} app]
    (update-service-by-id
     app id
     dissoc :show-delete-modal?))

  ConfirmDeleteTransportService
  (process-event [{id :id} app]
    (comm/get! (str "transport-service/delete/" id)
               {:on-success (tuck/send-async! ->DeleteTransportServiceResponse)})
    app)

  DeleteTransportServiceResponse
  (process-event [{response :response} app]
    (let [filtered-map (filter #(not= (:ote.db.transport-service/id %) (int response)) (get app :transport-services))]
      (assoc app :transport-services filtered-map)))

  SaveTransportService
  (process-event [{publish? :publish?} {service :transport-service
                                        operator :transport-operator :as app}]
    (let [key (t-service/service-key-by-type (::t-service/type service))
          service-data
          (-> service
              (assoc ::t-service/published? publish?
                     ::t-service/transport-operator-id (::t-operator/id operator))
              (update key form/without-form-metadata)
              (move-service-level-keys-from-form key)
              (update ::t-service/operation-area place-search/place-references))]
      (comm/post! "transport-service" service-data
                  {:on-success (tuck/send-async! ->SaveTransportServiceResponse)})
      app))

  SaveTransportServiceResponse
  (process-event [{response :response} app]
    (routes/navigate! :own-services)
    (assoc app :flash-message (tr [:common-texts :transport-service-saved])))

  CancelTransportServiceForm
  (process-event [_ app]
    (routes/navigate! :own-services)
    (dissoc app :transport-service)))


(defn move-service-level-keys-from-form
  "The form only sees the type specific level, move keys that are stored in the
  transport-service level there."
  [service from]
  (reduce (fn [service key]
            (as-> service s
              (if (contains? (get service from) key)
                (assoc s key (get-in service [from key]))
                s)
              (update s from dissoc key)))
          service
          service-level-keys))

(defn move-service-level-keys-to-form
  "Reverse of `move-service-level-keys-from-form`."
  [service to]
  (reduce (fn [service key]
            (as-> service s
              (if (contains? service key)
                (assoc-in s [to key] (get service key))
                s)
              (dissoc s key)))
          service
          service-level-keys))
