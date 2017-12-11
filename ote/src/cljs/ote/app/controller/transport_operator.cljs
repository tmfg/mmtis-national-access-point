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

(defrecord TransportOperatorResponse [response])
(defrecord TransportOperatorDataResponse [response])

(defn transport-operator-by-ckan-group-id[id]
  (comm/get! (str "transport-operator/" id) {:on-success (t/send-async! ->TransportOperatorResponse)}))

(defn transport-operator-data []
  (comm/post! "transport-operator/data" {} {:on-success (t/send-async! ->TransportOperatorDataResponse)}))

(extend-protocol t/Event

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
      (comm/post! "transport-operator" operator-data {:on-success (t/send-async! ->SaveTransportOperatorResponse)})
      app))

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
                                              (:transport-operators-with-services app))))

  TransportOperatorResponse
  (process-event [{response :response} app]
    (assoc app
      :transport-operator (assoc response
                            :loading? false)))

  TransportOperatorDataResponse
  (process-event [{response :response} app]
    (let [ckan-organization-id (get app :ckan-organization-id)
          selected-operator (if (not (nil? ckan-organization-id))
                                  (some #(when (= ckan-organization-id (get-in % [:transport-operator ::t-operator/ckan-group-id]))
                                    %)
                                   response)
                                  nil)]
    (assoc app
      :loading? false
      :transport-operators-with-services response
      ;; If operator is selected (ckan-organizatin-id is set) use it. Else take the first
      :transport-operator (if (nil? selected-operator) (get (first response) :transport-operator) (:transport-operator selected-operator))
      :transport-service-vector (get (first response) :transport-service-vector)
      :user (get response :user)))))