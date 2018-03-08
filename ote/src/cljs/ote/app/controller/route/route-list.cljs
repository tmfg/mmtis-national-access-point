(ns ote.app.controller.route.route-list
  "Route lsit controller"
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.app.routes :as routes]))

;; Load users own routes
(defrecord LoadRoutes [])
(defrecord LoadRoutesResponse [response])

;; Edit route
;(defrecord EditRoute [id]);

;; Create new route
(defrecord CreateNewRoute [])

(extend-protocol tuck/Event
  LoadRoutes
  (process-event [_ app]
    (let [on-success (tuck/send-async! ->LoadRoutesResponse)]
      (comm/post! "routes/routes" {}
                 {:on-success on-success})
      app))

  LoadRoutesResponse
  (process-event [{response :response} app]
    (.log js/console "Acting like we loaded something useful :) " (pr-str (get app :route)))
    (.log js/console "response " (pr-str response))
    (.log js/console "response " (clj->js response))

    (assoc app :route-list response
               :routes-vector (get (first response) :routes)))

  CreateNewRoute
  (process-event [_ app]
      (routes/navigate! :new-route)
      app))
