(ns ote.app.routes
  "Routes for the frontend app."
  (:require [bide.core :as r]
            [ote.app.state :as state]
            [tuck.core :as tuck]
            [ote.util.fn :refer [flip]]))

(def ote-router
  (r/router
   [["/" :own-services]
    ["/own-services" :own-services]
    ["/transport-operator" :transport-operator]
    ["/passenger-transportation" :passenger-transportation]
    ["/terminal" :terminal]
    ["/rentals" :rentals]
    ["/parking" :parking]
    ["/new-service/:sub-type" :new-service]
    ["/new-service" :transport-service]
    ["/edit-service/:id" :edit-service]
    ["/services" :services]
    ["/services/:operator" :services]
    ["/operators" :operators]

    ;; Route based traffic
    ["/routes" :routes]
    ["/route/new" :new-route]
    ["/edit-route/:id" :edit-route]

    ;; 60 days pre notices
    ["/notice/new" :new-notice]

    ["/routes/view-gtfs" :view-gtfs]

    ["/admin" :admin]
    ["/admin/:admin-page" :admin]]))

(defmulti on-navigate-event :page)

(defmethod on-navigate-event :default [_] nil)

(defn- on-navigate [go-to-url-event name params query]
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

             (let [navigation-data {:page name
                                    :params params
                                    :query query
                                    :url js/window.location.href}
                   event (on-navigate-event navigation-data)
                   app (merge app navigation-data)]
               (if event
                 (binding [tuck/*current-send-function*
                           #(swap! state/app (flip tuck/process-event) %)]
                   (tuck/process-event event app))
                 app))))))

(defn start! [go-to-url-event]
  (r/start! ote-router {:default :own-services
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
