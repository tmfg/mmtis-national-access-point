(ns ote.app.controller.front-page
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.app.routes :as routes]))


;;Change page event. Give parameter in key format e.g: :front-page, :transport-operator, :transport-service
(defrecord ChangePage [given-page])
(defrecord GoToUrl [url])
(defrecord OpenUserMenu [])
(defrecord OpenHeader [])
(defrecord ToggleDebugState [])
(defrecord Logout [])

(defrecord GetTransportOperator [])
(defrecord TransportOperatorResponse [response])
(defrecord TransportOperatorFailed [response])

(defrecord GetTransportOperatorData [])
(defrecord TransportOperatorDataResponse [response])

(extend-protocol tuck/Event

  ChangePage
  (process-event [{given-page :given-page} app]
    (routes/navigate! given-page)
    (assoc app :page given-page))

  GoToUrl
  (process-event [{url :url} app]
    (set! (.-location js/window) url )
    app)

  ToggleDebugState
  (process-event [_ app]
    (cond
      (get-in app [:ote-service-flags :show-debug]) (assoc-in app [:ote-service-flags :show-debug] false)
      :default (assoc-in app [:ote-service-flags :show-debug] true)
      ))

  OpenUserMenu
  (process-event [_ app]
    (assoc-in app [:ote-service-flags :user-menu-open] true) app)

  OpenHeader
  (process-event [_ app]
    (assoc-in app [:ote-service-flags :header-open]
              (if (get-in app [:ote-service-flags :header-open]) false true)))

  Logout
  (process-event [_ app]
    (assoc-in app [:ote-service-flags :user-menu-open] true)
    app)

  GetTransportOperator
  (process-event [_ app]
      (comm/post! "transport-operator/group" {} {:on-success (tuck/send-async! ->TransportOperatorResponse)
                                                 :on-failure (tuck/send-async! ->TransportOperatorFailed)})
      app)

  TransportOperatorResponse
  (process-event [{response :response} app]
    (assoc app :transport-operator response))

  TransportOperatorFailed
  (process-event [{response :response} app]
    ;; FIXME: figure out what the error is and add it to app state
    ;; e.g. unauhtorized should shown unauthorized page and ask user to log in.
    (.log js/console " Error: " (clj->js response))
    app)

  GetTransportOperatorData
  (process-event [_ app]
    (comm/post! "transport-operator/data" {} {:on-success (tuck/send-async! ->TransportOperatorDataResponse)})
    (assoc app :transport-operator-data-loaded? false))

  TransportOperatorDataResponse
  (process-event [{response :response} app]
    (let [app (assoc app
                :transport-operator-data-loaded? true
                :user (:user (first response)))]
    ;; First time users don't have operators.
    ;; Ask them to add one
    (if (and (nil? (get (first response) :transport-operator)) (not= :services (get app :page)))
      (doall
        (routes/navigate! :no-operator)
        (assoc app :page :no-operator))

      (assoc app
        :transport-operators-with-services response
        :transport-operator  (get (first response) :transport-operator)
        :transport-service-vector (get (first response) :transport-service-vector))))))
