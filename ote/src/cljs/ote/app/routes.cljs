(ns ote.app.routes
  "Routes for the frontend app."
  (:require [bide.core :as r]
            [ote.app.state :as state]
            [tuck.core :as tuck]))

(def ga-tracking-code
  (.getAttribute js/document.body "data-ga-tracking-code"))

(def dev-mode?
  (.getAttribute js/document.body "data-dev-mode?"))


;; when adding a new view: also add the keyword to the auth-required set below, and
;; add behaviour for it in the (case ..) form in ote.views.main/ote-application.

(def ote-router
  (r/router
   [["/" :front-page]
    ["/login" :login]
    ["/reset-password" :reset-password]
    ["/register" :register]
    ["/register/:token" :register]
    ["/user" :user]
    ["/own-services" :own-services]
    ["/transport-operator/:ckan-group-id/users" :operator-users]
    ["/transport-operator" :transport-operator]
    ["/transport-operator/:id" :transport-operator]
    ["/passenger-transportation" :passenger-transportation]
    ["/terminal" :terminal]
    ["/rentals" :rentals]
    ["/parking" :parking]
    ["/new-service/:operator-id/:sub-type" :new-service]
    ["/new-service/:operator-id" :transport-service]
    ["/edit-service/:id" :edit-service]
    ["/services" :services]
    ["/services/:operator" :services]
    ["/service/:transport-operator-id/:transport-service-id" :service-view]

    ["/email-settings" :email-settings]

    ;; Route based traffic
    ["/routes" :routes]
    ["/route/new" :new-route]
    ["/edit-route/:id" :edit-route]

    ["/routes/view-gtfs" :view-gtfs]
    ["/transit-visualization/:service-id/:date/:scope" :transit-visualization]
    ["/transit-changes" :transit-changes]

    ;; 60 day pre-notice
    ["/pre-notices" :pre-notices]
    ["/pre-notice/new" :new-notice]
    ["/pre-notice/edit/:id" :edit-pre-notice]
    ["/authority-pre-notices" :authority-pre-notices]
    ["/authority-pre-notices/:id" :authority-pre-notices]

    ["/admin" :admin]
    ["/admin/detected-changes/detect-changes" :admin-detected-changes]
    ["/admin/detected-changes/route-id" :admin-route-id]
    ["/admin/detected-changes/upload" :admin-upload-gtfs]
    ["/admin/detected-changes/commercial" :admin-commercial-services]
    ["/admin/detected-changes/exception-days" :admin-exception-days]
    ["/admin/:admin-page" :admin]

    ["/monitor" :monitor]]))

;; Add pages that needs authenticating to this list
(def auth-required #{:own-services :transport-service :edit-service :new-service
                     :transport-operator :operator-users
                     :routes :new-route :edit-route
                     :new-notice :edit-pre-notice :pre-notices
                     :email-settings})

;; Add pages that needs :transit-authority? authenticating to this list
(def transit-authority-required #{:authority-pre-notices :transit-visualization :transit-changes})

(def admin-required #{:admin :admin-detected-changes :admin-route-id :admin-upload-gtfs :admin-commercial-services :admin-exception-days})

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
       (nil? (get-in app [:user :username]))
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
                     win-location (if-let [page (#{:register :reset-password}
                                                 (:page navigation-data))]
                                    (str "/" (name page))
                                    win-location)]

                 (if (or (requires-authentication? app)
                         (requires-transit-authority? app)
                         (requires-admin? app))
                   (do (navigate! :login)
                       (assoc orig-app
                         :login {:show? true
                                 :navigate-to navigation-data}))
                   (do
                     ;; Send startup events (if any) immediately after returning from this swap
                     (when (or event-leave event-to)
                       (do
                         ;; Due to spa (Single page app) we need to manually push page change to matomo
                         ;; But first check if _paq exists (may not exist in local machines)
                         (when (exists? js/_paq)
                           (.push js/_paq (clj->js ["setCustomUrl" win-location])))
                         (.setTimeout
                           js/window
                           (fn []
                             (send-startup-events (vec (concat event-leave event-to))))
                           0)))
                     app)))
               app)))))

(defn start! [go-to-url-event]
  (r/start! ote-router {:default :front-page
                        :on-navigate (partial on-navigate go-to-url-event)}))

(defn navigate!
  "Navigate to given page with optional route and query parameters.
  The navigation is done by setting a timeout and can be called from
  tuck process-event."
  ([page] (navigate! page nil nil))
  ([page params] (navigate! page params nil))
  ([page params query]
   (.setTimeout js/window
                #(r/navigate! ote-router page params query) 0)))
