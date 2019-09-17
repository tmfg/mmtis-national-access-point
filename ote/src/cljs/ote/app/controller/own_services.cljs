(ns ote.app.controller.own-services
  "Own services controller"
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [ote.communication :as comm]
            [ote.localization :as localization :refer [tr]]
            [ote.app.controller.common :refer [->ServerError]]
            [ote.util.url :as url-util]
            [ote.db.transport-operator :as t-operator]
            [ote.app.routes :as routes]))

(define-event SearchSuccess [result]
              {}
              (assoc-in app [:service-search :suggestions] result))

(define-event SearchInvolved [val]
              {}
              (if (not= (count val) 0)
                (do
                  (comm/get! (str "service-completions/" (url-util/encode-url-component val))
                             {:on-success (tuck/send-async! ->SearchSuccess)})
                  app)
                (assoc-in app [:service-search :suggestions] [])))

(define-event AddSelectionSuccess [result service operator-id]
              {}
              (-> app
                  (update-in [:transport-operator ::t-operator/own-associations]
                             #(conj (or % []) service))
                  (dissoc :association-failed)
                  (update :transport-operators-with-services
                          (fn [operators]
                            (map
                              (fn [o]
                                (if (= (get-in o [:transport-operator ::t-operator/id]) operator-id)
                                  (update-in o [:transport-operator ::t-operator/own-associations] merge service)
                                  o))
                              operators)))))

(define-event AddSelectionFailure [result]
              {}
              (assoc app :association-failed true))

(define-event AddSelection [service-name service-id operator-name operator-business-id operator-id service-operator]
              {}
              (let [service {:service-name service-name
                             :service-id service-id
                             :operator-name operator-name
                             :operator-business-id operator-business-id
                             :operator-id operator-id
                             :service-operator service-operator}]
                (comm/post! (str "transport-service/" (url-util/encode-url-component service-id) "/associated-operators")
                            service
                            {:on-success (tuck/send-async! ->AddSelectionSuccess service operator-id)
                             :on-failure (tuck/send-async! ->AddSelectionFailure)})
                app))

(define-event RemoveSelectionSuccess [result operator-id service-id]
              {}
              (-> app
                  (update-in [:transport-operator ::t-operator/own-associations]
                             #(filter
                                (fn [s] (not= service-id (:service-id s)))
                                %))
                  (update :transport-operators-with-services
                          (fn [operators]
                            (map
                              (fn [o]
                                (if (= (get-in o [:transport-operator ::t-operator/id]) operator-id)
                                  (update-in o [:transport-operator ::t-operator/own-associations]
                                             #(filter
                                                (fn [as]
                                                  (not= (:service-id as) service-id))
                                                %))
                                  o))
                              operators)))))

(define-event RemoveSelection [service-id]
              {}
              (let [transport-operator-id (::t-operator/id (:transport-operator app))]
                (comm/delete!
                  (str "transport-service/"
                       (url-util/encode-url-component service-id)
                       "/associated-operators/"
                       (url-util/encode-url-component transport-operator-id))
                  {}
                  {:on-success (tuck/send-async! ->RemoveSelectionSuccess transport-operator-id service-id)})
                app))

; Ensure that :transport-operator is not nil
; When user refreshes page our app-state removes data from :transport-operator key. That data is used
; everywhere to determine which operator is selected for usage.
(define-event InitOwnServices []
  {}
  (if (nil? (:transport-operator app))
    (assoc app :transport-operator (:transport-operator (first (get app :transport-operators-with-services))))
    app))

(defmethod routes/on-navigate-event :own-services [_]
  (->InitOwnServices))
