(ns ote.app.controller.transport-operator
  "Transport operator controls "                            ;; FIXME: Move transport-service related stuff to other file
  (:require [tuck.core :as t]
            [ote.communication :as comm]
            [ote.ui.form :as form]))

(defrecord EditTransportOperatorState [data])
(defrecord SaveTransportOperator   [])
(defrecord SaveTransportOperatorResponse [data])

(extend-protocol t/Event

  EditTransportOperatorState
  (process-event [{data :data} app]
    (update app :transport-operator merge data))

  SaveTransportOperator
  (process-event [_ app]
    (let [operator-data (-> app
                            :transport-operator
                            form/without-form-metadata)]
      (comm/post! "transport-operator" operator-data {:on-success (t/send-async! ->SaveTransportOperatorResponse)})
      app))

  SaveTransportOperatorResponse
  (process-event [{data :data} app]
    (assoc app :transport-operator data
               :page :front-page)))
