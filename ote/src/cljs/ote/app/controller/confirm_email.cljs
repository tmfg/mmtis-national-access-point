(ns ote.app.controller.confirm-email
  (:require [ote.app.routes :as routes]
            [ote.communication :as comm]
            [tuck.core :as tuck :refer-macros [define-event]]))

(define-event ConfirmSuccess []
  {}
  (-> app
    (assoc-in [:confirm-email :loaded?] true)
    (assoc-in [:confirm-email :success?] true)))

(define-event ConfirmFailure []
  {}
  (-> app
    (assoc-in [:confirm-email :loaded?] true)
    (assoc-in [:confirm-email :success?] false)))

(define-event InitConfirmEmailView []
  {}
  (comm/post!
    "confirm-email"
    {:token (get-in app [:params :token])}
    {:on-success (tuck/send-async! ->ConfirmSuccess)
     :on-failure (tuck/send-async! ->ConfirmFailure)})
  (assoc-in app [:confirm-email :loaded?] false))

(defmethod routes/on-navigate-event :confirm-email [_]
  (->InitConfirmEmailView))
