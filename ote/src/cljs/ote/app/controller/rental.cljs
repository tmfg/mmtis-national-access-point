(ns ote.app.controller.rental
  "Rental service controls "
  (:require [tuck.core :as t]
            [ote.communication :as comm]
            [ote.ui.form :as form]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]
            [ote.app.controller.place-search :as place-search]))

(defrecord EditRentalState [data])
(defrecord SaveRentalToDb [])
(defrecord HandleRentalResponse [service])

(extend-protocol t/Event

  EditRentalState
  (process-event [{data :data} app]
    (update-in app [:transport-service ::t-service/rentals] merge data))


  SaveRentalToDb
  (process-event [_ {service :transport-service :as app}]
    (let [service-data
          (-> service
              (assoc ::t-service/type :rentals
                     ::t-service/transport-operator-id (get-in app [:transport-operator ::t-operator/id]))
              (update ::t-service/rentals form/without-form-metadata)
              (update-in [::t-service/rentals ::t-service/operation-area]
                         place-search/place-references))]
      (comm/post! "transport-service"
                  service-data
                  {:on-success (t/send-async! ->HandleRentalResponse)})
      app))

  HandleRentalResponse
  (process-event [{service :service} app]
    (assoc app
      :transport-service service
      :page :own-services)))
