(ns ote.app.controller.ckan-org-viewer
  "Controller and events for org view mode (CKAN embedded view)."
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [taoensso.timbre :as log]
            [ote.app.routes :as routes]
            [ote.app.controller.transport-operator :as to]))


(defrecord StartViewer [])

(extend-protocol tuck/Event

  StartViewer
  (process-event [_ app]
    (let [ckan-group-id (.getAttribute (.getElementById js/document "nap_viewer") "data-group-id")]
      (to/get-transport-operator-by-ckan-group-id ckan-group-id))
    app))