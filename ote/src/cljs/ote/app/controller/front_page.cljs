(ns ote.app.controller.front-page
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.app.routes :as routes]))


;;Change page event. Give parameter in key format e.g: :front-page, :transport-operator, :transport-service
(defrecord ChangePage [given-page])
(defrecord OpenUserMenu [])
(defrecord ToggleDebugState [])
(defrecord Logout [])

(defrecord GetTransportOperator [])
(defrecord TransportOperatorResponse [response])

(defrecord GetTransportOperatorData [])
(defrecord TransportOperatorDataResponse [response])

(extend-protocol tuck/Event

  ChangePage
  (process-event [{given-page :given-page} app]
    (routes/navigate! given-page)
    (assoc app :page given-page))

  ToggleDebugState
  (process-event [_ app]
    (cond
      (get-in app [:ote-service-flags :show-debug]) (assoc-in app [:ote-service-flags :show-debug] false)
      :default (assoc-in app [:ote-service-flags :show-debug] true)
      ))

  OpenUserMenu
  (process-event [_ app]
    (assoc-in app [:ote-service-flags :user-menu-open] true) app)

  Logout
  (process-event [_ app]
    (assoc-in app [:ote-service-flags :user-menu-open] true)
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
    (assoc app
      :transport-operator (get response :transport-operator)
      :transport-services (get response :transport-service-vector )
      :user (get response :user ))))



