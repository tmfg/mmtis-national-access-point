(ns ote.app.controller.transit-changes
  "Controller for transit changes view"
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [ote.communication :as comm]
            [ote.app.controller.common :refer [->ServerError]]
            [ote.app.routes :as routes]))



(define-event TransitChangesResponse [changes]
  {:path [:transit-changes]}
  (assoc app
         :changes changes
         :loading? false))

(define-event LoadTransitChanges []
  {:path [:transit-changes]}
  (comm/get! "transit-changes/current"
             {:on-success (tuck/send-async! ->TransitChangesResponse)
              :on-failure (tuck/send-async! ->ServerError)})
  (assoc app :loading? true))

(defmethod routes/on-navigate-event :transit-changes [_]
  (->LoadTransitChanges))

(define-event ShowChangesForOperator [id]
  {}
  (routes/navigate! :transit-visualization {:operator-id id})
  app)
