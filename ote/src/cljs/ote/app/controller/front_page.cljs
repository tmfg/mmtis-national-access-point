(ns ote.app.controller.front-page
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.app.routes :as routes]))


;;Change page event. Give parameter in key format e.g: :front-page, :transport-operator, :transport-service
(defrecord ChangePage [given-page])
(defrecord GoToUrl [url])
(defrecord StayOnPage [])
(defrecord OpenUserMenu [])
(defrecord OpenHeader [])
(defrecord Logout [])
(defrecord SetLanguage [lang])

(defrecord GetTransportOperator [])
(defrecord TransportOperatorResponse [response])
(defrecord TransportOperatorFailed [response])
(defrecord EnsureTransportOperator [])

(defrecord GetTransportOperatorData [])
(defrecord TransportOperatorDataResponse [response])
(defrecord TransportOperatorDataFailed [error])

(defn navigate [event {:keys [before-unload-message navigation-prompt-open?] :as app} navigate-fn]
  (if (and before-unload-message (not navigation-prompt-open?))
    (assoc app
           :navigation-prompt-open? true
           :navigation-confirm event)
    (navigate-fn (dissoc app
                         :navigation-prompt-open?
                         :before-unload-message
                         :navigation-confirm))))

(extend-protocol tuck/Event

  ChangePage
  (process-event [{given-page :given-page :as e} app]
    (navigate e app (fn [app]
                      (do
                        (routes/navigate! given-page)
                        (assoc app :page given-page)))))

  GoToUrl
  (process-event [{url :url :as e} app]
    (navigate e app (fn [app]
                      #(set! (.-location js/window) url)
                      app)))

  StayOnPage
  (process-event [_ app]
    (dissoc app :navigation-prompt-open?))

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

  EnsureTransportOperator
  (process-event [_ app]
    (if (empty? (get app :transport-operator))
      (do
        (routes/navigate! :no-operator)
        (assoc app :page :no-operator)
        )
      app))

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
  ;; FIXME: this should be called something else, like SessionInit (the route as well)
  (process-event [_ app]
    (comm/post! "transport-operator/data" {}
                {:on-success (tuck/send-async! ->TransportOperatorDataResponse)
                 :on-failure (tuck/send-async! ->TransportOperatorDataFailed)})
    (assoc app :transport-operator-data-loaded? false))

  TransportOperatorDataFailed
  (process-event [{error :error} app]
    ;; 401 is ok (means user is not logged in
    (when (not= 401 (:status error))
      (.log js/console "Failed to fetch transport operator data: " (pr-str error)))
    (assoc app
           :transport-operator-data-loaded? true
           :user nil))

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
        :transport-service-vector (get (first response) :transport-service-vector)))))

  SetLanguage
  (process-event [{lang :lang} app]
    (set! (.-cookie js/document) (str "finap_lang=" lang ";path=/"))
    (.reload js/window.location)))
