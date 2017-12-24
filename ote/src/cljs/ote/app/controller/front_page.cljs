(ns ote.app.controller.front-page
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.db.transport-operator :as t-operator]
            [ote.app.routes :as routes]))


;;Change page event. Give parameter in key format e.g: :front-page, :transport-operator, :transport-service
(defrecord ChangePage [given-page params])
(defrecord GoToUrl [url])
(defrecord OpenNewTab [url])
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

(defrecord ClearFlashMessage [])

(defrecord ShowLoginDialog [])
(defrecord UpdateLoginCredentials [credentials])
(defrecord Login [])
(defrecord LoginResponse [response])
(defrecord LoginFailed [response])
(defrecord LoginCancel [])

(defn navigate [event {:keys [before-unload-message navigation-prompt-open?] :as app} navigate-fn]
  (if (and before-unload-message (not navigation-prompt-open?))
    (assoc app
           :navigation-prompt-open? true
           :navigation-confirm event)
    (navigate-fn (dissoc app
                         :navigation-prompt-open?
                         :before-unload-message
                         :navigation-confirm))))

(defn get-transport-operator-data [app]
  (if (:transport-operator-data-loaded? app true)
     (do
       (comm/post! "transport-operator/data" {}
                   {:on-success (tuck/send-async! ->TransportOperatorDataResponse)
                    :on-failure (tuck/send-async! ->TransportOperatorDataFailed)})
       (assoc app
              :transport-operator-data-loaded? false
              :services-changed? false))
     app))

(extend-protocol tuck/Event

  ChangePage
  (process-event [{given-page :given-page params :params :as e} app]
    (navigate e app (fn [app]
                      (do
                        (routes/navigate! given-page params)
                        (assoc app
                          :page given-page
                          :params params)))))

  GoToUrl
  (process-event [{url :url :as e} app]
    (navigate e app (fn [app]
      (.setTimeout js/window #(set! (.-location js/window) url) 0)
      app)))

  OpenNewTab
  (process-event [{url :url :as e} app]
    (let [window-open (.open js/window)]
         (set! (.-opener window-open) nil)
         (set! (.-location window-open) url)
    app))

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
     (if (:services-changed? app)
      (get-transport-operator-data app)
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
    (get-transport-operator-data app))

  TransportOperatorDataFailed
  (process-event [{error :error} app]
    ;; 401 is ok (means user is not logged in
    (when (not= 401 (:status error))
      (.log js/console "Failed to fetch transport operator data: " (pr-str error)))
    (assoc app
           :transport-operator-data-loaded? true
           :user nil))

  TransportOperatorDataResponse
  (process-event [{response :response} {:keys [page ckan-organization-id transport-operator] :as app}]
    (let [app (assoc app
                     :transport-operator-data-loaded? true
                     :user (:user (first response)))]
      (if (and (nil? (:transport-operator (first response)))
               (not= :services page))
        ;; If page is :transport-operator and user has no operators, start creating a new one
        (do
          (if (= (:page app) :transport-operator)
            (assoc app
                   :transport-operator {:new? true}
                   :services-changed? true)
            app))

        ;; Get services from response.
        ;; Use selected operator if possible, if not, use the first one from the response.
        ;; Selected can either be previously selected or ckan-organization-id (CKAN edit view)
        (let [selected-operator (or
                                 (some #(when (or (= (::t-operator/id transport-operator)
                                                     (get-in % [:transport-operator ::t-operator/id]))
                                                  (= ckan-organization-id
                                                     (get-in % [:transport-operator ::t-operator/ckan-group-id])))
                                          %)
                                       response)
                                 (first response))]

          (assoc app
                 :transport-operators-with-services response
                 :transport-operator (:transport-operator selected-operator)
                 :transport-service-vector (:transport-service-vector selected-operator))))))

  SetLanguage
  (process-event [{lang :lang} app]
    (set! (.-cookie js/document) (str "finap_lang=" lang ";path=/"))
    (.reload js/window.location))

  ClearFlashMessage
  (process-event [_ app]
    (dissoc app :flash-message :flash-message-error))

  ShowLoginDialog
  (process-event [_ app]
    (assoc app :login {:show? true}))
  UpdateLoginCredentials
  (process-event [{credentials :credentials} app]
    (update-in app [:login :credentials] merge credentials))

  Login
  (process-event [_ app]
    (comm/post! "login"
                (select-keys (get-in app [:login :credentials]) #{:email :password})
                {:on-success (tuck/send-async! ->LoginResponse)
                 :on-failure (tuck/send-async! ->LoginFailed)})
    (update app :login
            #(-> %
                 (dissoc :failed? :error)
                 (assoc :in-progress? true))))

  LoginResponse
  (process-event [{response :response} app]
    (if (:success? response)
      (-> app
          (assoc :user (:user response))
          (dissoc :login))
      (assoc-in app [:login :failed?] true)))

  LoginFailed
  (process-event [{response :response} app]
    ;; The login request itself failed
    (assoc-in app [:login :error] response)
    )

  LoginCancel
  (process-event [_ app]
    (dissoc app :login))
  )
