(ns ote.app.controller.ckan-service-viewer
  "Controller and events for viewer more (CKAN embedded resource view)."
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [taoensso.timbre :as log]))


(defrecord StartViewer [])

(defrecord ResourceFetched [response])
(defrecord TransportOperatorResponse [response])

(extend-protocol tuck/Event

  StartViewer
  (process-event [_ app]
    (if-let [elt (.getElementById js/document "nap_viewer")]
      ;; If we are in an embedded CKAN viewer, fetch the resource
      (let [url (.getAttribute elt "data-resource-url")
            element (aget (.getElementsByClassName js/document "authed") 0)
            logged-in? (not (nil? element))] ;; Is user is logged in

        (comm/get! "viewer" {:params {:url url}
                             :on-success (tuck/send-async! ->ResourceFetched)
                             :response-format :json})

        (comm/post! "transport-operator/group" {} {:on-success (tuck/send-async! ->TransportOperatorResponse)})

        (assoc app
               :url url
               :logged-in? logged-in?
               :loading? true))
      ;; If not in embedded CKAN viewer page, do nothing
      app))

  ResourceFetched
  (process-event [{response :response} app]
    (assoc app
           :resource response
           :geojson (clj->js response)
           :loading? false))

  TransportOperatorResponse
  (process-event [{response :response} app]
    (let [org-id (str (nth (clojure.string/split (get app :url) "/") 6))
          service-org-id (str (get response :ote.db.transport-operator/id))
          authorized? (= org-id service-org-id)]
      (assoc app :authorized? authorized?))))
