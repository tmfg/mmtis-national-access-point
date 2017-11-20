(ns ote.app.controller.transport-operator
  "Transport operator controls "                            ;; FIXME: Move transport-service related stuff to other file
  (:require [tuck.core :as t]
            [ote.communication :as comm]
            [ote.ui.form :as form]
            [ote.app.routes :as routes]))

(defrecord EditTransportOperatorState [data])
(defrecord SaveTransportOperator [])
(defrecord SaveTransportOperatorResponse [data])

(defrecord TransportOperatorResponse [response])
(defrecord TransportOperatorDataResponse [response])

(defn transport-operator-by-ckan-group-id[id]
  (comm/get! (str "transport-operator/" id) {:on-success (t/send-async! ->TransportOperatorResponse)}))

(defn transport-operator-data []
  (comm/post! "transport-operator/data" {} {:on-success (t/send-async! ->TransportOperatorDataResponse)}))

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
    (routes/navigate! :own-services)
    (assoc app :transport-operator data
               :page :own-services))

  TransportOperatorResponse
  (process-event [{response :response} app]
    (assoc app
      :transport-operator (assoc response
                            :loading? false)))

  TransportOperatorDataResponse
  (process-event [{response :response} app]
    ;(.log js/console " Transport operator data response" (clj->js response) (clj->js (get response :transport-operator)))
    (assoc app
      :transport-operator (assoc (get response :transport-operator)
                            :loading? false)
      :transport-services (get response :transport-service-vector)
      :user (get response :user))))