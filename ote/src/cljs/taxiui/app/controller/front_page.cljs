(ns taxiui.app.controller.front-page
  (:require [ote.app.controller.login :as login]
            [ote.app.controller.common :refer [->ServerError]]
            [ote.app.localstorage :as localstorage]
            [ote.communication :as comm]
            [ote.localization :as localization]
            [reagent.core :as r]
            [taxiui.app.routes :as routes]
            [tuck.core :as tuck]))

;;Change page event. Give parameter in key format e.g: :front-page, :transport-operator, :transport-service
(defrecord ChangePage [given-page params])
(defrecord GoToUrl [url])
(defrecord OpenNewTab [url])
(defrecord StayOnPage [])
(defrecord ToggleFintrafficMenu [])
(defrecord ToggleMobileBottomMenu [])
(defrecord ToggleUpdatesMenu [])
(defrecord ToggleServiceInfoMenu [])
(defrecord ToggleMyServicesMenu [])
(defrecord ToggleSupportMenu [])
(defrecord ToggleUserMenu [])
(defrecord ToggleLangMenu [])
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

(def ^:private all-menus [[:ote-service-flags :fintraffic-menu-open]
                          [:ote-service-flags :navigation-updates-menu]
                          [:ote-service-flags :service-info-menu-open]
                          [:ote-service-flags :my-services-menu-open]
                          [:ote-service-flags :support-menu-open]
                          [:ote-service-flags :user-menu-open]
                          [:ote-service-flags :lang-menu-open]])

(defn- switch-menus [app switch-fn]
  (reduce
    (fn [app path]
      (assoc-in app path (switch-fn app path)))
    app
    all-menus))

(defn- close-all-menus [app]
  (switch-menus app (constantly false)))

(defn- toggle-menu [app menu-flag-path]
  (switch-menus
    app
    (fn [app path]
      (if (= menu-flag-path path)
        (if (get-in app path) false true)
        false))))

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

  ToggleFintrafficMenu
  (process-event [_ app]
    (toggle-menu app [:ote-service-flags :fintraffic-menu-open]))

  ToggleMobileBottomMenu
  (process-event [_ app]
    ; mobile bottom menu is handled separately to support two-level nested menus
    (assoc-in
      app
      [:ote-service-flags :mobile-bottom-menu-open]
      (if (get-in app [:ote-service-flags :mobile-bottom-menu-open]) false true)))

  ToggleUpdatesMenu
  (process-event [_ app]
    (toggle-menu app [:ote-service-flags :navigation-updates-menu]))

  ToggleServiceInfoMenu
  (process-event [_ app]
    (toggle-menu app [:ote-service-flags :service-info-menu-open]))

  ToggleMyServicesMenu
  (process-event [_ app]
    (toggle-menu app [:ote-service-flags :my-services-menu-open]))

  ToggleSupportMenu
  (process-event [_ app]
    (toggle-menu app [:ote-service-flags :support-menu-open]))

  ToggleUserMenu
  (process-event [_ app]
    (toggle-menu app [:ote-service-flags :user-menu-open]))

  ToggleLangMenu
  (process-event [_ app]
    (toggle-menu app [:ote-service-flags :lang-menu-open]))

  CloseHeaderMenus
  (process-event [_ app]
    (-> app
        (close-all-menus)
        ; mobile bottom menu is handled separately to support two-level nested menus
        (assoc-in [:ote-service-flags :mobile-bottom-menu-open] false)))

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
    (dissoc app :flash-message :flash-message-error))

  CloseTermsAndPrivacyResponse
  (process-event [{response :response email :email} app]
    (localstorage/add-item! (keyword (str email "-tos-ok")) true)
    (routes/navigate! (:page app))
    (assoc-in app [:user :tos-ok] true))

  CloseTermsAndPrivacy
  (process-event [{user :user} app]
    (if (nil? user)
      (do
        (localstorage/add-item! :tos-ok true)
        (routes/navigate! (:page app)))
      (comm/post! "register/tos" {:user-email (:email user)}
                  {:on-success (tuck/send-async! ->CloseTermsAndPrivacyResponse (:email user))
                   :on-failure (tuck/send-async! ->ServerError)}))
    (assoc app :tos-ok true)))
