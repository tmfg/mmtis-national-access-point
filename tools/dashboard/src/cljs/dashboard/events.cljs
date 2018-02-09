(ns dashboard.events
  (:require [tuck.core :as tuck]
            [ajax.core :as ajax]))

(defrecord FetchDashboard [])
(defrecord FetchDashboardResponse [response])

(defn fetch [app]
  (ajax/GET "/dashboard"
            {:handler (tuck/send-async! ->FetchDashboardResponse)
             :response-format (ajax/transit-response-format)})
  app)

(def poll-interval 10000)

(extend-protocol tuck/Event
  FetchDashboard
  (process-event [_ app]
    (fetch app))

  FetchDashboardResponse
  (process-event [{response :response} app]
    (.setTimeout js/window (tuck/send-async! ->FetchDashboard) poll-interval)
    response))
