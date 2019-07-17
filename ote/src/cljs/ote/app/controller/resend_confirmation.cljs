(ns ote.app.controller.resend-confirmation
  (:require [ote.communication :as comm]
            [tuck.core :as tuck :refer-macros [define-event]]
            [ote.app.controller.common :refer [->ServerError]]))

(define-event EmailFieldOnChange [input]
  {}
  (update-in app [:send-confirmation] assoc :confirmation-email input))

(define-event SendConfirmationSuccess []
  {}
  (-> app
    (assoc-in [:send-confirmation :success?] true)))

(define-event SendConfirmation [email language]
  {}
  (comm/post!
    "send-email-confirmation"
    {:email email
     :language language}
    {:on-success (tuck/send-async! ->SendConfirmationSuccess)
     :on-failure (tuck/send-async! ->ServerError)})
  app)
