(ns ote.app.controller.parking
  "Parking service controls "
  (:require [tuck.core :as t]
            [ote.communication :as comm]
            [ote.ui.form :as form]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]
            [ote.app.controller.place-search :as place-search]))

(defrecord EditParkingState [data])
(defrecord SaveParkingToDb [])
(defrecord HandleParkingResponse [service])

(extend-protocol t/Event

  EditParkingState
  (process-event [{data :data} app]
    (update-in app [:transport-service ::t-service/rental] merge data))


  SaveParkingToDb
  (process-event [_ {service :transport-service :as app}]
    (let [service-data
          (-> service
              (assoc ::t-service/type :parking
                     ::t-service/transport-operator-id (get-in app [:transport-operator ::t-operator/id]))
              (update ::t-service/parking form/without-form-metadata)
              (update-in [::t-service/parking ::t-service/operation-area]
                         place-search/place-references))]
      (comm/post! "parking"
                  service-data
                  {:on-success (t/send-async! ->HandleParkingResponse)})
      app))

  HandleParkingResponse
  (process-event [{service :service} app]
    (assoc app
      :transport-service service
      :page :own-services)))
