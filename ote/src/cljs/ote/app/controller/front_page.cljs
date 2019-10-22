(ns ote.app.controller.front-page
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [ote.communication :as comm]
            [ote.app.routes :as routes]
            [ote.app.controller.login :as login]
            [ote.app.controller.flags :as flags]
            [ote.localization :as localization :refer [tr]]
            [reagent.core :as r]))


;;Change page event. Give parameter in key format e.g: :front-page, :transport-operator, :transport-service
(defrecord ChangePage [given-page params])
(defrecord GoToUrl [url])
(defrecord OpenNewTab [url])
(defrecord StayOnPage [])
(defrecord OpenUserMenu [])
(defrecord OpenHeader [])
(defrecord OpenLangMenu [])
(defrecord CloseHeaderMenus [])
(defrecord Logout [])
(defrecord SetLanguage [lang])
(defrecord ForceUpdateAll [app scroll-y])

(defrecord GetTransportOperator [])
(defrecord TransportOperatorResponse [response])
(defrecord TransportOperatorFailed [response])
(defrecord EnsureTransportOperator [])

(defrecord TransportOperatorDataResponse [response])
(defrecord TransportOperatorDataFailed [error])

(defrecord ClearFlashMessage [])



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
                      (do
                        (routes/navigate! given-page params)
                        app))))

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
    (-> app
      (assoc-in [:ote-service-flags :user-menu-open]
                (if (get-in app [:ote-service-flags :user-menu-open]) false true))
      (assoc-in [:ote-service-flags :header-open] false)
      (assoc-in [:ote-service-flags :lang-menu-open] false)))

  OpenHeader
  (process-event [_ app]
    (-> app
      (assoc-in [:ote-service-flags :header-open]
              (if (get-in app [:ote-service-flags :header-open]) false true))
      (assoc-in [:ote-service-flags :user-menu-open] false)
      (assoc-in [:ote-service-flags :lang-menu-open] false)))

  OpenLangMenu
  (process-event [_ app]
    (-> app
      (assoc-in [:ote-service-flags :lang-menu-open]
                (if (get-in app [:ote-service-flags :lang-menu-open]) false true))
      (assoc-in [:ote-service-flags :user-menu-open] false)
      (assoc-in [:ote-service-flags :header-open] false)))

  CloseHeaderMenus
  (process-event [_ app]
    (-> app
        (assoc-in [:ote-service-flags :lang-menu-open] false)
        (assoc-in [:ote-service-flags :user-menu-open] false)
        (assoc-in [:ote-service-flags :header-open] false)))

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

  SetLanguage
  (process-event [{lang :lang} app]
    (let [force-update-all (tuck/send-async! ->ForceUpdateAll app js/window.scrollY)]
      (set! (.-cookie js/document) (str "finap_lang=" lang ";path=/"))
      (r/after-render
       #(localization/load-language! lang
                                     (fn [lang _]
                                       (reset! localization/selected-language lang)
                                       ;; Reset app state to re-render everything
                                       (force-update-all)))))
    ;; Return empty app state, until new language has been fetched
    ;; Just calling (r/force-update-all) is not enough because some components
    ;; implement component should update.
    nil)

  ForceUpdateAll
  (process-event [{app :app scroll-y :scroll-y} _]
    (r/after-render #(.scrollTo js/window 0 scroll-y))
    app)

  ClearFlashMessage
  (process-event [_ app]
    (dissoc app :flash-message :flash-message-error)))

(define-event ToggleAddMemberDialog []
  {:path [:show-add-member-dialog?]
   :app show?}
  (not show?))

(define-event ToggleRegistrationDialog []
  {}
  (if (flags/enabled? :ote-register)
    (do
      (routes/navigate! :register)
      app)
    (-> app
        (update :show-register-dialog? not)
        (assoc-in [:login :navigate-to] {:page :own-services})
        get-transport-operator-data)))



(define-event UserResetRequested []
  {}
  (assoc app
         :show-reset-dialog? false
         :flash-message (tr [:login :check-email-for-link])))
