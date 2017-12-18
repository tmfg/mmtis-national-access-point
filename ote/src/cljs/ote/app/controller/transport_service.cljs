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

(defn new-transport-service [app]
      (update app :transport-service select-keys #{::t-service/type}))

(def service-level-keys
  #{::t-service/contact-address
    ::t-service/contact-phone
    ::t-service/contact-email
    ::t-service/homepage
    ::t-service/name
    ::t-service/sub-type
    ::t-service/external-interfaces
    ::t-service/operation-area
    ::t-service/companies
    ::t-service/published?
    ::t-service/brokerage?
    ::t-service/description
    ::t-service/available-from
    ::t-service/available-to
    ::t-service/notice-external-interfaces?})

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
(defrecord SelectTransportServiceType [])

(defrecord ModifyTransportService [id])
(defrecord ModifyTransportServiceResponse [response])
(defrecord OpenTransportServicePage [id])
(defrecord OpenTransportServiceTypePage [])


(defrecord DeleteTransportService [id])
(defrecord ConfirmDeleteTransportService [id])
(defrecord CancelDeleteTransportService [id])
(defrecord DeleteTransportServiceResponse [response])
(defrecord FailedDeleteTransportServiceResponse [response])

(defrecord PublishTransportService [transport-service-id])
(defrecord PublishTransportServiceResponse [success? transport-service-id])

(defrecord EditTransportService [form-data])
(defrecord SaveTransportService [schemas publish?])
(defrecord SaveTransportServiceResponse [response])
(defrecord FailedTransportServiceResponse [response])
(defrecord CancelTransportServiceForm [])

(defrecord SelectServiceType [data])
(defrecord SelectOnlyServiceType [data])
(defrecord SetNewServiceType [type])

(declare move-service-level-keys-from-form
         move-service-level-keys-to-form)

(defn- update-service-by-id [app id update-fn & args]
  (update app :transport-service-vector
          (fn [services]
            (map #(if (= (::t-service/id %) id)
                    (apply update-fn % args)
                    %)
                 services))))

(defmulti transform-save-by-type
  "Transform transport service before sending it to the server.
  Dispatches on the type. By default, returns service as is."
  ::t-service/type)

(defmethod transform-save-by-type :rentals [service]
  (-> service
      (update-in [::t-service/rentals ::t-service/pick-up-locations]
                 (fn [pick-up-locations]
                   (map (fn [{hours-and-exceptions ::t-service/service-hours-and-exceptions :as pick-up-location}]
                          (as-> pick-up-location loc
                            (if-let [hours (::t-service/service-hours hours-and-exceptions)]
                              (assoc loc ::t-service/service-hours hours)
                              loc)
                            (if-let [exceptions (::t-service/service-hours hours-and-exceptions)]
                              (assoc loc ::t-service/service-exceptions exceptions)
                              loc)
                            (dissoc loc ::t-service/service-hours-and-exceptions)))
                        pick-up-locations)
                   ))
      (update-in [::t-service/rentals ::t-service/vehicle-classes]
                 (fn [vehicle-classes]               
                   (mapv (fn [{prices-and-units :price-group :as price-group}]
                          (as-> price-group price
                            (if-let [prices (::t-service/price-classes prices-and-units)]
                              (assoc price ::t-service/price-classes prices)
                              price)
                            (dissoc price :price-group)))
                        vehicle-classes)))))

(defmethod transform-save-by-type :default [service] service)

(defmulti transform-edit-by-type
  "Transform transport service for editing after receiving it from the server.
  Dispatches on the type. By default, returns service as is."
  ::t-service/type)

(defmethod transform-edit-by-type :rentals [service]
  (-> service
      (update-in [::t-service/rentals ::t-service/pick-up-locations]
                 (fn [pick-up-locations]
                   (mapv (fn [{hours ::t-service/service-hours
                               exceptions ::t-service/service-exceptions
                               :as pick-up-location}]
                           (-> pick-up-location
                               (assoc ::t-service/service-hours-and-exceptions
                                      {::t-service/service-hours hours
                                       ::t-service/service-exceptions exceptions})
                               (dissoc ::t-service/service-hours
                                       ::t-service/service-exceptions)))
                         pick-up-locations)))
      (update-in [::t-service/rentals ::t-service/vehicle-classes]
                 (fn [vehicle-classes]
                   (mapv (fn [{price-classes ::t-service/price-classes
                               :as vehicle-class}]
                           (-> vehicle-class
                               (assoc :price-group 
                                      {::t-service/price-classes price-classes})
                               (dissoc ::t-service/price-classes)))
                         vehicle-classes)))))

(defmethod transform-edit-by-type :default [service] service)

(defn- add-service-for-operator [app service]
  ;; Add service for currently selected transport operator and transport-operator-vector
  (as-> app app
      (update app :transport-operators-with-services
              (fn [operators-with-services]
                (map (fn [operator-with-services]
                       (if (= (get-in operator-with-services [:transport-operator ::t-operator/id])
                              (::t-service/transport-operator-id service))
                         (update operator-with-services :transport-service-vector
                                 (fn [services]
                                   (let [service-idx (first (keep-indexed (fn [i s]
                                                                            (when (= (::t-service/id s)
                                                                                     (::t-service/id service))
                                                                              i)) services))]
                                     (if service-idx
                                       (assoc (vec services) service-idx service)
                                       (conj (vec services) service)))))
                         operator-with-services))
                     operators-with-services)))
      (assoc app :transport-service-vector
                 (some #(when (= (get-in % [:transport-operator ::t-operator/id])
                                 (get-in app [:transport-operator ::t-operator/id]))
                          (:transport-service-vector %))
                       (:transport-operators-with-services app)))))

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

  SelectServiceType
  ;; Set only service type and sub-type
  (process-event [{data :data} app]
   (let [service-type-subtype data
          type (service-type-from-combined-service-type data)
          sub-type (subtype-from-combined-service-type service-type-subtype)
          subtype-key (t-service/service-key-by-type type)
          app (assoc-in app [:transport-service :transport-service-type-subtype ] data)
          app (assoc-in app [:transport-service subtype-key ] {::t-service/sub-type sub-type})
          app (assoc-in app [:transport-service ::t-service/type] type)
          ]
      app))

  SelectTransportServiceType
  ;; Redirect to add service page
  (process-event [_ app]
    (let [app (new-transport-service app)]
      (routes/navigate! :new-service {:type (name (get-in app [:transport-service ::t-service/type]))})
      app))

  SelectOnlyServiceType
  ;; Set service type, sub-type and
  (process-event [{data :data} app]
    (let [service-type-subtype data
          type (service-type-from-combined-service-type data)
          sub-type (subtype-from-combined-service-type service-type-subtype)
          subtype-key (t-service/service-key-by-type type)
          app (assoc-in app [:transport-service :transport-service-type-subtype ] data)
          app (assoc-in app [:transport-service subtype-key ] {::t-service/sub-type sub-type})
          app (assoc-in app [:transport-service ::t-service/type] type)
          ]
      (routes/navigate! :new-service {:type (name type)})
      app))

  OpenTransportServiceTypePage
  ;; :transport-service :<transport-service-type> needs to be cleaned up before creating a new one
  (process-event [_ app]
    (let [app (new-transport-service app)]
      (routes/navigate! :transport-service)
      app))

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
            (move-service-level-keys-to-form (t-service/service-key-by-type type))
            transform-edit-by-type))))

  ;; Use this when navigating outside of OTE. Above methods won't work from NAP.
  OpenTransportServicePage
  (process-event [{id :id} app]
    (set! (.-location js/window) (str "/ote/#/edit-service/" id))
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
               {:on-success (tuck/send-async! ->DeleteTransportServiceResponse)
                :on-failure (tuck/send-async! ->FailedDeleteTransportServiceResponse)})
    app)

  DeleteTransportServiceResponse
  (process-event [{response :response} app]
    (let [filtered-map (filter #(not= (:ote.db.transport-service/id %) (int response)) (get app :transport-service-vector))]
      (assoc app :transport-service-vector filtered-map
                 :page :own-services
                 :flash-message (tr [:common-texts :delete-service-success])
                 :services-changed? true)))

  FailedDeleteTransportServiceResponse
  (process-event [{response :response} app]
    (assoc app :flash-message-error (tr [:common-texts :delete-service-error])))

  SaveTransportService
  (process-event [{:keys [schemas publish?]} {service :transport-service
                                              operator :transport-operator :as app}]
    (let [key (t-service/service-key-by-type (::t-service/type service))
          service-data
          (-> service
              (update key (comp (partial form/prepare-for-save schemas)
                                form/without-form-metadata))
              (dissoc :transport-service-type-subtype
                      :select-transport-operator)
              (move-service-level-keys-from-form key)
              (assoc ::t-service/published? publish?
                     ::t-service/transport-operator-id (::t-operator/id operator))
              (update ::t-service/operation-area place-search/place-references)
              transform-save-by-type)]
      (comm/post! "transport-service" service-data
                  {:on-success (tuck/send-async! ->SaveTransportServiceResponse)
                   :on-failure (tuck/send-async! ->FailedTransportServiceResponse)})
      (dissoc app :before-unload-message)))

  SaveTransportServiceResponse
  (process-event [{response :response} app]
    (let [app (add-service-for-operator
                (assoc app :flash-message (tr [:common-texts :transport-service-saved]))
                response)]
    (routes/navigate! :own-services)
    (-> app
        (assoc :services-changed? true)
        (dissoc :transport-service))))

  FailedTransportServiceResponse
  (process-event [{response :response} app]
    (assoc app :flash-message-error (tr [:common-texts :save-failed])))

  EditTransportService
  (process-event [{form-data :form-data} {ts :transport-service :as app}]
    (let [key (t-service/service-key-by-type (::t-service/type ts))]
      (-> app
          (update-in [:transport-service key] merge form-data)
          (assoc :before-unload-message (tr [:dialog :navigation-prompt :unsaved-data])))))

  CancelTransportServiceForm
  (process-event [_ app]
    (routes/navigate! :own-services)
    (dissoc app :transport-service :before-unload-message))

  SetNewServiceType
  (process-event [{type :type} app]
    ;; This is needed when directly loading a new service URL to set the type
    (assoc-in app [:transport-service ::t-service/type] type)))

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
