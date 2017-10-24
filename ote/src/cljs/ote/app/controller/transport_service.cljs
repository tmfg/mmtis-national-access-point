(ns ote.app.controller.transport-service
  "Transport operator controls "                            ;; FIXME: Move transport-service related stuff to other file
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.db.transport-service :as t-service]
            [ote.ui.form :as form]
            [ote.app.routes :as routes]
            [ote.time :as time]
            [clojure.string :as string]))

(def service-level-keys
  #{::t-service/contact-address
    ::t-service/contact-phone
    ::t-service/contact-email
    ::t-service/homepage
    ::t-service/name})

(defn move-service-level-keys
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
  "The form only sees the type specific level, move keys that are stored in the
  transport-service level back to service level (e.g. passenger-transportation)."
  [service to-key from]
  (reduce (fn [service key]
            (-> service
                (assoc-in [:transport-service to-key key] (get from key))
                ))
          service
          service-level-keys))

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
      (assoc :page (get response ::t-service/type)
             :transport-service response)
      (move-service-level-keys-to-form
        (keyword (str "ote.db.transport-service/" (string/replace (str (get response ::t-service/type)) ":" "")))
        response)
        )
  )

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

    ;; Remove deleted service from the app state
    ;; FIXME: todo
    ))