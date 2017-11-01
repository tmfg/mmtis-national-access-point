(ns ote.app.controller.ckan-org-editor
  "Controller and events for org view mode (CKAN embedded view)."
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [taoensso.timbre :as log]
            [ote.app.routes :as routes]))


(defrecord StartEditor [])
(defrecord GetTransportOperatorData [ckan-group-id])
(defrecord TransportOperatorDataResponse [response])

(extend-protocol tuck/Event

  StartEditor
  (process-event [_ app]
    (let [ckan-group-id (.getAttribute (.getElementById js/document "nap_viewer") "data-group-id")]
      (tuck/action!
        (fn [e!]
          (e! (->GetTransportOperatorData ckan-group-id))))))

  GetTransportOperatorData
  (process-event [_ app]
    (comm/post! "transport-operator/data" {} {:on-success (tuck/send-async! ->TransportOperatorDataResponse)})
    app)

  TransportOperatorDataResponse
  (process-event [{response :response} app]
    ;(.log js/console " Transport operator data response" (clj->js response) (clj->js (get response :transport-operator)))
    (assoc app
      :transport-operator (get response :transport-operator)
      :transport-services (get response :transport-service-vector )
      :user (get response :user ))))
