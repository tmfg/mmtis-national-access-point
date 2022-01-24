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
   [["/"                                         :taxi-ui/front-page]
    ["/login"                                    :taxi-ui/login]
    ["/stats"                                    :taxi-ui/stats]
    ["/pricing-details/:operator-id/:service-id" :taxi-ui/pricing-details]]))

;; Add pages that needs authenticating to this list
(def auth-required #{:taxi-ui/front-page :taxi-ui/pricing-details})

;; Add pages that needs :transit-authority? authenticating to this list
(def transit-authority-required #{:authority-pre-notices :transit-visualization :transit-changes :monitor})

(def admin-required #{:admin :admin-detected-changes :admin-route-id :admin-upload-gtfs :admin-commercial-services :admin-exception-days :user-edit})

(defmulti on-navigate-event
  "Determine event(s) to be run when user navigates to a given route.
  Returns a single Tuck event or a vector of Tuck events to be applied
  to the app state. Takes a map containing the navigation data as parameter.
  Route parameters are under the :params key."
  :taxi-ui/page)

(defmethod on-navigate-event :default [page-params] nil)

(defmulti on-leave-event
  "Determine event(s) to be run when user navigates away from the give route.
  Return values identical to on-navigate-event."
  :taxi-ui/page)

(tuck/define-event ClearPageData [page]
                   {}
                   (update app :taxi-ui dissoc (-> (name page) keyword)))

(defmethod on-leave-event :default [page-params]
  (->ClearPageData (:taxi-ui/page page-params)))

(defn requires-admin? [app]
  (and (contains? admin-required (:taxi-ui/page app))
       (not (get-in app [:user :admin?]))))

(defn requires-transit-authority? [app]
  (and (contains? transit-authority-required (:taxi-ui/page app))
       (not (get-in app [:user :transit-authority?]))))

(defn requires-authentication? [app]
  (and
       (not (user-logged-in? app))
       (contains? auth-required (:taxi-ui/page app))))

(defn- send-startup-events [event]
  (let [e!     (tuck/control state/app)
        events (->> (if (not (vector? event)) [event] (flatten event))
                    (filter some?))]
    (doseq [event events
            :when event]
      (e! event))))

(declare navigate!)

(defn- force-login!
  [orig-app navigation-data]
  (do
    (navigate! :taxi-ui/login)
    (assoc orig-app
      :login {:show? true
              :navigate-to navigation-data})))

(defn- not-authorized?
  [app]
  (or (requires-authentication? app)
      (requires-transit-authority? app)
      (requires-admin? app)))

(defn on-navigate [go-to-url-event route-name params query]
  (swap! state/app
         (fn [app]
           (let [navigation-data {:taxi-ui/page   route-name
                                  :params params
                                  :query  query
                                  :url    js/window.location.href}
                     orig-app    app
                     app         (merge app navigation-data)]
             (.setTimeout
               js/window
               (fn []
                 (send-startup-events
                   [(when-not (= route-name (:taxi-ui/page orig-app))
                      (on-leave-event    (select-keys orig-app [:taxi-ui/page :query :params])))
                    (on-navigate-event navigation-data)]))
               0)
             (if (not-authorized? app)
               (force-login! orig-app navigation-data)
               app)))))

(defn start! [go-to-url-event]
  (r/start! taxiui-router {:default     :taxi-ui/front-page
                           :on-navigate (partial on-navigate go-to-url-event)}))

(defn resolve
  "Return the URL Path matching to given parameters. Useful for link rendering."
  [page params]
  (r/resolve taxiui-router page params))

(defn navigate!
  "Navigate to given page with optional route and query parameters.
  The navigation is done by setting a timeout and can be called from
  tuck process-event."
  ([page] (navigate! page nil nil))
  ([page params] (navigate! page params nil))
  ([page params query]
   (.setTimeout js/window
                #(r/navigate! taxiui-router page params query) 0)))
