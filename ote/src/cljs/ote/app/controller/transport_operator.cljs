(ns ote.app.controller.transport-operator
  "Transport operator controls "                            ;; FIXME: Move transport-service related stuff to other file
  (:require [tuck.core :as t]
            [ote.communication :as comm]
            [ote.ui.form :as form]
            [ote.localization :refer [tr tr-key]]
            [ote.db.transport-operator :as t-operator]
            [ote.app.routes :as routes]))

(defrecord SelectOperator [data])
(defrecord EditTransportOperatorState [data])
(defrecord SaveTransportOperator [])
(defrecord SaveTransportOperatorResponse [data])
(defrecord FailedTransportOperatorResponse [response])

(defrecord TransportOperatorResponse [response])
(defrecord CreateTransportOperator [])


(defn transport-operator-by-ckan-group-id[id]
  (comm/get! (str "transport-operator/" id) {:on-success (t/send-async! ->TransportOperatorResponse)}))

(extend-protocol t/Event

  CreateTransportOperator
  (process-event [_ app]
    (routes/navigate! :transport-operator)
    (assoc app
           :transport-operator {:new? true}
           :services-changed? true))

  SelectOperator
  (process-event [{data :data} app]
    (let [id  (get data ::t-operator/id)
          selected (some #(when (= id (get-in % [:transport-operator ::t-operator/id]))
                            %)
                         (:transport-operators-with-services app))]
      (assoc app
             :transport-operator (:transport-operator selected)
             :transport-service-vector (:transport-service-vector selected))))

  EditTransportOperatorState
  (process-event [{data :data} app]
    (update app :transport-operator merge data))

  SaveTransportOperator
  (process-event [_ app]
    (let [operator-data (-> app
                            :transport-operator
                            form/without-form-metadata)]
      (comm/post! "transport-operator" operator-data {:on-success (t/send-async! ->SaveTransportOperatorResponse)
                                                      :on-failure (t/send-async! ->FailedTransportOperatorResponse)})
      app))

  FailedTransportOperatorResponse
  (process-event [{response :response} app]
    (assoc app :flash-message-error (tr [:common-texts :save-failed])))

  SaveTransportOperatorResponse
  (process-event [{data :data} app]
    (routes/navigate! :own-services)
    (assoc app
           :page :own-services
           :flash-message (tr [:common-texts :transport-operator-saved ])
           :transport-operator data
           :transport-operators-with-services (map (fn [{:keys [transport-operator] :as operator-with-services}]
                                                     (if (= (::t-operator/id data)
                                                            (::t-operator/id transport-operator))
                                                       (assoc operator-with-services
                                                              :transport-operator data)
                                                       operator-with-services))
                                                   (:transport-operators-with-services app))
           :services-changed? true))

  TransportOperatorResponse
  (process-event [{response :response} app]
    (assoc app
      :transport-operator (assoc response
                            :loading? false))))
