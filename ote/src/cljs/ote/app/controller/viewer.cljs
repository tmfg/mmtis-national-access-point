(ns ote.app.controller.viewer
  "Controller and events for viewer more (CKAN embedded resource view)."
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [taoensso.timbre :as log]))


(defrecord StartViewer [])

(defrecord ResourceFetched [response])

(extend-protocol tuck/Event

  StartViewer
  (process-event [_ app]
    (let [url (.getAttribute (.getElementById js/document "nap_viewer") "data-resource-url")]
      (comm/get! "viewer" {:params {:url url}
                           :on-success (tuck/send-async! ->ResourceFetched)
                           :response-format :json})
      (assoc app
             :url url
             :loading? true)))

  ResourceFetched
  (process-event [{response :response} app]
    (assoc app
           :resource response
           :geojson (clj->js response)
           :loading? false)))
