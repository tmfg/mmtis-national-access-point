(ns ote.app.controller.terminal
  "Port, Station and Terminal service controls "
  (:require [tuck.core :as t]
            [ote.communication :as comm]
            [ote.ui.form :as form]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]
            [ote.app.controller.place-search :as place-search]))

(defrecord EditTerminalState [data])
(defrecord SaveTerminalToDb [])
(defrecord HandleTerminalResponse [service])

(extend-protocol t/Event

  EditTerminalState
  (process-event [{data :data} app]
    (update-in app [:transport-service ::t-service/terminal] merge data))


  SaveTerminalToDb
  (process-event [_ {service :transport-service :as app}]
    (.log js/console " Lähetettään datoja serverille " (clj->js service))

    (let [service-data
          (-> service
              (dissoc ::t-service/passenger-transportation)
              (assoc ::t-service/type :terminal
                     ::t-service/transport-operator-id (get-in app [:transport-operator ::t-operator/id]))
              (update ::t-service/terminal form/without-form-metadata)
              (update-in [::t-service/terminal ::t-service/operation-area]
                         place-search/place-references))]
      (comm/post! "terminal"
                  service-data
                  {:on-success (t/send-async! ->HandleTerminalResponse)})
      app))

  HandleTerminalResponse
  (process-event [{service :service} app]
    (assoc app
      :transport-service service
      :page :front-page)))
