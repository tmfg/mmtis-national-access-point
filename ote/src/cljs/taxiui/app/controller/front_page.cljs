(ns taxiui.app.controller.front-page
  (:require [ote.app.controller.login :as login]
            [ote.app.controller.common :refer [->ServerError]]
            [ote.app.localstorage :as localstorage]
            [ote.communication :as comm]
            [ote.localization :as localization]
            [reagent.core :as r]
            [taxiui.app.controller.loader :as loader]
            [taxiui.app.routes :as routes]
            [tuck.core :as tuck]))

;;Change page event. Give parameter in key format e.g: :front-page, :transport-operator, :transport-service
(defrecord ChangePage [given-page params])
(defrecord GoToUrl [url])
(defrecord OpenNewTab [url])
(defrecord StayOnPage [])
(defrecord ForceUpdateAll [app scroll-y])

(defrecord GetTransportOperator [])
(defrecord TransportOperatorResponse [response])
(defrecord TransportOperatorFailed [response])
(defrecord EnsureTransportOperator [])

(defrecord TransportOperatorDataResponse [response])
(defrecord TransportOperatorDataFailed [error])

(defrecord ClearFlashMessage [])

(defrecord CloseTermsAndPrivacy [user])
(defrecord CloseTermsAndPrivacyResponse [response email])


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
      (-> app
          (assoc
            :transport-operator-data-loaded? false
            :services-changed? false)
          (dissoc :transport-operators-with-services)))
    app))

(extend-protocol tuck/Event

  ChangePage
  (process-event [{given-page :given-page params :params :as e} app]
    (navigate e app (fn [app]
                      (routes/navigate! given-page params)
                      app)))

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
    (let [{:keys [page params]} (get-in app [:login :navigate-to])]
      (when page
        (routes/navigate! page params)))
    (login/update-transport-operator-data (dissoc app :login) response))

  ForceUpdateAll
  (process-event [{app :app scroll-y :scroll-y} _]
    (r/after-render #(.scrollTo js/window 0 scroll-y))
    app)

  ClearFlashMessage
  (process-event [_ app]
    (dissoc app :flash-message :flash-message-error))

  CloseTermsAndPrivacyResponse
  (process-event [{response :response email :email} app]
    (localstorage/add-item! (keyword (str email "-tos-ok")) true)
    (routes/navigate! (:taxi-ui/page app))
    (assoc-in app [:user :tos-ok] true))

  CloseTermsAndPrivacy
  (process-event [{user :user} app]
    (if (nil? user)
      (do
        (localstorage/add-item! :tos-ok true)
        (routes/navigate! (:taxi-ui/page app)))
      (comm/post! "register/tos" {:user-email (:email user)}
                  {:on-success (tuck/send-async! ->CloseTermsAndPrivacyResponse (:email user))
                   :on-failure (tuck/send-async! ->ServerError)}))
    (assoc app :tos-ok true)))

(defmethod routes/on-navigate-event :taxi-ui/front-page [{params :params}]
  (loader/->RemoveHit :page-loading))