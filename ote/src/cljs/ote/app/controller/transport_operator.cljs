(ns ote.app.controller.transport-operator
  "Transport operator controls "                            ;; FIXME: Move transport-service related stuff to other file
  (:require [tuck.core :as t]
            [ote.communication :as comm]
            [ote.ui.form :as form]))

(defrecord EditTransportOperatorState [data])
(defrecord SaveTransportOperatorToDb [])
(defrecord HandleTransportOperatorResponse [data])

(extend-protocol t/Event

  EditTransportOperatorState
  (process-event [{data :data} app]
    (update app :transport-operator merge data))

  SaveTransportOperatorToDb
  (process-event [_ app]
    (let [operator-data (-> app
                            :transport-operator
                            form/without-form-metadata)]
      (comm/post! "transport-operator" operator-data {:on-success (t/send-async! ->HandleTransportOperatorResponse)})
      app))

  HandleTransportOperatorResponse
  (process-event [{data :data} app]
    (assoc app :transport-operator data
               :page :passenger-transportation)))
