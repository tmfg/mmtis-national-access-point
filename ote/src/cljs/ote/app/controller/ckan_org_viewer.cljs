(ns ote.app.controller.ckan-org-viewer
  "Controller and events for org edit mode (CKAN embedded view)."
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [taoensso.timbre :as log]
            [ote.app.routes :as routes]))


(defrecord StartViewer [])
(defrecord GetTransportOperatorData [ckan-group-id])
(defrecord TransportOperatorResponse [response])

(extend-protocol tuck/Event

  StartViewer
  (process-event [_ app]
    (let [ckan-group-id (.getAttribute (.getElementById js/document "nap_viewer") "data-group-id")]
      (tuck/action!
        (fn [e!]
          (e! (->GetTransportOperatorData ckan-group-id))))))

  GetTransportOperatorData
  (process-event [{id :ckan-group-id} app]
    (comm/get! (str "transport-operator/" id) {:on-success (tuck/send-async! ->TransportOperatorResponse)})
    app)

  TransportOperatorResponse
  (process-event [{response :response} app]
    ;(.log js/console " Transport operator response" (clj->js response) (clj->js (get response :transport-operator)))
    (assoc app
      :transport-operator response)))
