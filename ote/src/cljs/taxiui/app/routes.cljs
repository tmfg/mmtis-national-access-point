(ns taxiui.app.routes
  "Routes for the frontend app."
  (:require [bide.core :as r]
            [ote.app.state :as state]
            [ote.app.utils :refer [user-logged-in?]]
            [tuck.core :as tuck]))

(def dev-mode?
  (.getAttribute js/document.body "data-dev-mode?"))

;; when adding a new view: also add the keyword to the auth-required set below, and
;; add behaviour for it in the (case ..) form in ote.views.main/ote-application.

(def taxiui-router
  (r/router
    ; These first few routes are non-namespaced to work around application hardcoded default behaviors in several places
    ; which is significant as the Taxi UI reuses quite a few of the NAPOTE UI services and such.
    ; It would be possible to refactor all those bits to work dynamically, but it really isn't worth the effort at this
    ; point.
   [["/" :front-page]
    ["/login" :taxi-ui/login]
    ["/pricing-details" :taxi-ui/pricing-details]]))

;; Add pages that needs authenticating to this list
(def auth-required #{:taxi-ui/pricing-details})

;; Add pages that needs :transit-authority? authenticating to this list
(def transit-authority-required #{:authority-pre-notices :transit-visualization :transit-changes :monitor})

(def admin-required #{:admin :admin-detected-changes :admin-route-id :admin-upload-gtfs :admin-commercial-services :admin-exception-days :user-edit})

(defmulti on-navigate-event
  "Determine event(s) to be run when user navigates to a given route.
  Returns a single Tuck event or a vector of Tuck events to be applied
  to the app state. Takes a map containing the navigation data as parameter.
  Route parameters are under the :params key."
  :page)

(defmethod on-navigate-event :default [_] nil)

(defmulti on-leave-event
          "Determine event(s) to be run when user navigates away from the give route.
          Return values identical to on-navigate-event."
          :page)

(defmethod on-leave-event :default [_] nil)

(defn requires-admin? [app]
  (and (contains? admin-required (:page app))
       (not (get-in app [:user :admin?]))))

(defn requires-transit-authority? [app]
  (and (contains? transit-authority-required (:page app))
       (not (get-in app [:user :transit-authority?]))))

(defn requires-authentication? [app]
  (and
       (not (user-logged-in? app))
       (contains? auth-required (:page app))))

(defn- send-startup-events [event]
  (let [e! (tuck/control state/app)]
    (if (vector? event)
      ;; Received multiple events, apply them all
      (doseq [event event
              :when event]
        (e! event))
      ;; Apply single event
      (e! event))))

(declare navigate!)

(defn on-navigate [go-to-url-event route-name params query]
  (swap! state/app
         (fn [{:keys [before-unload-message navigation-prompt-open? url] :as app}]
           (if (and before-unload-message (not navigation-prompt-open?))
             (let [new-url js/window.location.href]
               ;; push previous URL to the history (the one we want to stay on)
               (.pushState js/window.history #js {} js/document.title
                           url)
               ;; Open confirmation dialog and only go to new page
               ;; if the user confirms navigation.
               (assoc app
                 :navigation-prompt-open? true
                 :navigation-confirm (go-to-url-event new-url)))

             (if (not= (:url app) js/window.location.href)
               (let [navigation-data {:page route-name
                                      :params params
                                      :query query
                                      :url js/window.location.href}
                     event-leave (on-leave-event {:page (:page app)})
                     event-leave (if (vector? event-leave) event-leave [event-leave])
                     event-to (on-navigate-event navigation-data)
                     event-to (if (vector? event-to) event-to [event-to])
                     orig-app app
                     app (merge app navigation-data)
                     win-location (subs (.. js/window -location -hash) 1)
                     ;; Remove potentially sensitive arguments from analytics script reporting
                     win-location (if-let [opt-out-page (#{:register :reset-password :confirm-email} (:page navigation-data))]
                                    (str "/" (name opt-out-page))
                                    win-location)
                     not-authorized? (or (requires-authentication? app)
                                       (requires-transit-authority? app)
                                       (requires-admin? app))]
                 (js/console.log (str "navigation data :: " navigation-data))
                 (if not-authorized?
                   (do
                     (js/console.log "loginia tarvitaan")
                     (navigate! :taxi-ui/login)
                     (assoc orig-app
                       :login {:show? true
                               :navigate-to navigation-data}))

                   ;; Send startup events (if any) immediately after returning from this swap
                   (if (or event-leave event-to)
                     (do
                       ; SPA page changes must be pushed to analytics script because url route might not change.
                       ;; Check tracker script in case script loading failed. A browser extension or other issue may block it.
                       (when (exists? js/_paq)
                         (.push js/_paq (clj->js ["setCustomUrl", win-location]))
                         ;; trackPageView signals to matomo piwik js api a single page visit.
                         (.push js/_paq (clj->js ["trackPageView"])))

                       (.setTimeout
                         js/window
                         (fn []
                           (send-startup-events (vec (concat event-leave event-to))))
                         0)
                       app)
                     app)))
               app)))))

(defn start! [go-to-url-event]
  (r/start! taxiui-router {:default     :front-page
                           :on-navigate (partial on-navigate go-to-url-event)}))

(defn navigate!
  "Navigate to given page with optional route and query parameters.
  The navigation is done by setting a timeout and can be called from
  tuck process-event."
  ([page] (navigate! page nil nil))
  ([page params] (navigate! page params nil))
  ([page params query]
   (.setTimeout js/window
                #(r/navigate! taxiui-router page params query) 0)))
