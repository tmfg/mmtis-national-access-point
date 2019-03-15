(ns ote.app.controller.service-view
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [ote.communication :as comm]
            [ote.localization :as localization :refer [tr]]
            [ote.app.controller.common :refer [->ServerError]]
            [ote.util.url :as url-util]
            [ote.app.routes :as routes]))


(define-event ServiceSuccess [result]
              {}
              (assoc-in app [:service-view :transport-service] result))

(define-event OperatorSuccess [result]
              {}
              (assoc-in app [:service-view :transport-operator] result))

(define-event FetchServiceData [operator-id service-id]
              {}
              (do
                (comm/get! (str "t-service/" (url-util/encode-url-component service-id))
                           {:on-success (tuck/send-async! ->ServiceSuccess)}))
              app)

(defmethod routes/on-navigate-event :service-view [{params :params}]
  (when params
    (->FetchServiceData (:transport-operator-id params) (:transport-service-id params))))
