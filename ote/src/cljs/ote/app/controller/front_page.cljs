(ns ote.app.controller.front-page
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.app.routes :as routes]))


;;Change page event. Give parameter in key format e.g: :front-page, :transport-operator, :transport-service
(defrecord ChangePage [given-page])

(defrecord GetTransportOperator [])
(defrecord TransportOperatorResponse [response])

(defrecord GetTransportOperatorData [])
(defrecord TransportOperatorDataResponse [response])

(extend-protocol tuck/Event

  ChangePage
  (process-event [{given-page :given-page} app]
    (routes/navigate! given-page)
    app)

  GetTransportOperator
  (process-event [_ app]
      (comm/post! "transport-operator/group" {} {:on-success (tuck/send-async! ->TransportOperatorResponse)})
      app)

  TransportOperatorResponse
  (process-event [{response :response} app]
    (assoc app :transport-operator response))

  GetTransportOperatorData
  (process-event [_ app]
    (comm/post! "transport-operator/data" {} {:on-success (tuck/send-async! ->TransportOperatorDataResponse)})
    app)

  TransportOperatorDataResponse
  (process-event [{response :response} app]
    ;(.log js/console " Mitäkähän dataa serveriltä tulee " (clj->js response) (clj->js (get response :transport-operator)))
    (assoc app
      :transport-operator (get response :transport-operator)
      :transport-services (get response :transport-service-vector ))))
