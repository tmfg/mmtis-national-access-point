(ns ote.app.controller.admin-validation
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [tuck.effect :as tuck-effect]
            [ote.localization :refer [tr tr-key]]
            [ote.communication :as comm]
            [ote.app.routes :as routes]
            [ote.app.controller.common :refer [->ServerError]]
            [ote.app.controller.front-page :as fp-controller]))

;; Validation
(defrecord OpenConfirmPublishModal [id])
(defrecord CloseConfirmPublishModal [])
(defrecord LoadValidationServices [])
(defrecord LoadValidationServicesResponse [response])
(defrecord PublishService [id])
(defrecord PublishResponse [response])
(defrecord EditService [id])
(defrecord ShowDiffModal [service])
(defrecord CloseDiffModal [])

(def every-5min (* 1000 60 1))
(defmethod tuck-effect/process-effect :adminevery5min [e! {:keys [on-success on-failure]}]
  (.setInterval js/window #(comm/get! "admin/validation-services"
                                      {:on-success on-success
                                       :on-failure on-failure})
                every-5min))

(defn- load-validation-services []
  (comm/get! "admin/validation-services"
             {:on-success (tuck/send-async! ->LoadValidationServicesResponse)
              :on-failure (tuck/send-async! ->ServerError)}))

(extend-protocol tuck/Event

  LoadValidationServices
  (process-event [_ app]
    (do
      ;; Load services immediately
      (load-validation-services)
      ;; And start timer
      (tuck/fx app {:tuck.effect/type :adminevery5min
                    :on-success (tuck/send-async! ->LoadValidationServicesResponse)
                    :on-failure (tuck/send-async! ->ServerError)})))

  OpenConfirmPublishModal
  (process-event [{id :id} app]
    (assoc-in app [:admin :in-validation :modal] id))

  CloseConfirmPublishModal
  (process-event [_ app]
    (assoc-in app [:admin :in-validation :modal] nil))

  LoadValidationServicesResponse
  (process-event [{response :response} app]
    (update-in app [:admin :in-validation] assoc
               :loading? false
               :results response))

  EditService
  (process-event [{id :id} app]
    (routes/navigate! :edit-service {:id id})
    (assoc-in app [:admin :in-validation :validating] id))

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
    (routes/navigate! :admin)
    (load-validation-services)
    (-> app
        (assoc :flash-message "Palvelu julkaistu onnistuneesti.")
        (assoc-in [:admin :in-validation :modal] nil)))

  ShowDiffModal
  (process-event [{service :service} app]
    (-> app
        (assoc-in [:admin :in-validation :show-diff-modal?] true)
        (assoc-in [:admin :in-validation :diff-service] service)))

  CloseDiffModal
  (process-event [_ app]
    (-> app
        (assoc-in [:admin :in-validation :show-diff-modal?] false)
        (assoc-in [:admin :in-validation :diff-service] nil))))
