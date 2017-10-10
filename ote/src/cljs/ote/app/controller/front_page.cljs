(ns ote.app.controller.front-page
  (:require [tuck.core :as t]
            [ote.communication :as comm]))

(defrecord AddTransportService [])
(defrecord ModifyTransportOperator [])
(defrecord GetTransportOperator [])
(defrecord HandleTransportOperatorResponse [response])
(defrecord ChangePage [given-page])

(extend-protocol t/Event

  AddTransportService
  (process-event [_ app]
    (assoc app :page :transport-service))

  ModifyTransportOperator
  (process-event [_ app]
    (assoc app :page :transport-operator))

  GetTransportOperator
  (process-event [_ app]
      (comm/post! "transport-operator/group" {} {:on-success (t/send-async! ->HandleTransportOperatorResponse)})
      app)

  HandleTransportOperatorResponse
  (process-event [{response :response} app]
    (assoc app :transport-operator response))


  ChangePage
  (process-event [{given-page :given-page} app]
               (assoc app :page given-page)))

