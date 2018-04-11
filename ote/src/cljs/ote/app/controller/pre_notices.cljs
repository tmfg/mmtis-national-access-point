(ns ote.app.controller.pre-notices
  "Controller for 60 day pre notices"
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [ote.communication :as comm]
            [ote.app.routes :as routes]
            [ote.localization :refer [tr]]))


(declare ->OrganizationPreNoticesResponse ->OrganizationPreNoticesFailure)

;; Load the pre-notices that are available
(define-event LoadOrganizationPreNotices []
  {:path [:pre-notices]}
  (comm/get! "pre-notices/list"
             {:on-success (tuck/send-async! ->OrganizationPreNoticesResponse)
              :on-failure (tuck/send-async! ->OrganizationPreNoticesFailure)})
  :loading)

(defmethod routes/on-navigate-event :pre-notices [_]
  (->LoadOrganizationPreNotices))

(define-event OrganizationPreNoticesResponse [response]
  {:path [:pre-notices]}
  response)

(define-event OrganizationPreNoticesFailure [response]
  {}
  (assoc app :flash-message-error (tr [:common-texts :server-error])))
