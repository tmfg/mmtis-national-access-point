(ns ote.app.controller.ckan-org-viewer
  "Controller and events for org edit mode (CKAN embedded view)."
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [taoensso.timbre :as log]
            [ote.app.controller.front-page :as fp-ctrl]))



(defrecord StartViewer [])

(extend-protocol tuck/Event

  StartViewer
  (process-event [_ app]
    ; Trigger transport operator data fetch action
    (tuck/action!
      (fn [e!]
        (e! (fp-ctrl/->GetTransportOperatorData))))))
