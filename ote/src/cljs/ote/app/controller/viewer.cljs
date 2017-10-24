(ns ote.app.controller.viewer
  "Controller and events for viewer more (CKAN embedded resource view)."
  (:require [tuck.core :as tuck]))


(defrecord StartViewer [])

(extend-protocol tuck/Event

  StartViewer
  (process-event [_ app]
    ;;
    (assoc app
           :url (.getAttribute (.getElementById js/document "nap_viewer") "data-resource-url"))))
