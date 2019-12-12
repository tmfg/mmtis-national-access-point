(ns ote.app.controller.admin-validation
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [ote.localization :refer [tr tr-key]]
            [ote.communication :as comm]
            [ote.app.routes :as routes]
            [ote.app.controller.common :refer [->ServerError]]))

;; Validation
(defrecord OpenConfirmPublishModal [id])
(defrecord CloseConfirmPublishModal [])
(defrecord LoadValidationServices [])
(defrecord LoadValidationServicesResponse [response])
(defrecord PublishService [id])
(defrecord PublishResponse [response])

(defn- load-validation-services []
  (comm/get! "admin/validation-services"
             {:on-success (tuck/send-async! ->LoadValidationServicesResponse)
              :on-failure (tuck/send-async! ->ServerError)}))

(extend-protocol tuck/Event

  OpenConfirmPublishModal
  (process-event [{id :id} app]
    (assoc-in app [:admin :in-validation :modal] id))

  CloseConfirmPublishModal
  (process-event [_ app]
    (assoc-in app [:admin :in-validation :modal] nil))

  LoadValidationServices
  (process-event [_ app]
    (load-validation-services)
    app)

  LoadValidationServicesResponse
  (process-event [{response :response} app]
    (update-in app [:admin :in-validation] assoc
               :loading? false
               :results response))

  PublishService
  (process-event [{id :id} app]
    ;; post id to publish route
    (comm/post! "admin/publish-service" {:id id}
                {:on-success (tuck/send-async! ->PublishResponse)
                 :on-failure (tuck/send-async! ->ServerError)})
    app)

  PublishResponse
  (process-event [{response :response} app]
    ;; close modal and show success flash message
    (load-validation-services)
    (-> app
        (assoc :flash-message "Palvelu julkaistu onnistuneesti.")
        (assoc-in [:admin :in-validation :modal] nil))))
