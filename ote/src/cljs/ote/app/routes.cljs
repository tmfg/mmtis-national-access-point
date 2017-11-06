(ns ote.app.routes
  "Routes for the frontend app."
  (:require [bide.core :as r]
            [ote.app.state :as state]))

(def ote-router
  (r/router
   [["/" :front-page]
    ["/own-services" :own-services]
    ["/transport-operator" :transport-operator]
    ["/passenger-transportation" :passenger-transportation]
    ["/terminal" :terminal]
    ["/rentals" :rentals]
    ["/brokerage" :brokerage]
    ["/parking" :parking]
    ["/new-service" :transport-service]
    ["/edit-service/:id" :edit-service]]))

(defn- on-navigate [name params query]
  (swap! state/app merge {:page name
                          :params params
                          :query query}))
(defn start! []
  (r/start! ote-router {:default :front-page
                        :on-navigate on-navigate}))

(defn navigate!
  "Navigate to given page with optional route and query parameters.
  The navigation is done by setting a timeout and can be called from
  tuck process-event."
  ([page] (navigate! page nil nil))
  ([page params] (navigate! page params nil))
  ([page params query]
   (.log js/console "NAVIGATE: " (pr-str page))
   (.setTimeout js/window
                #(r/navigate! ote-router page params query) 0)))
