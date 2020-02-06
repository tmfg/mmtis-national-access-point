(ns ote.app.controller.own-services
  "Own services controller"
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [tuck.effect :as tuck-effect]
            [ote.app.state :as state]
            [ote.communication :as comm]
            [ote.localization :as localization :refer [tr]]
            [ote.app.controller.common :refer [->ServerError]]
            [ote.util.url :as url-util]
            [ote.db.transport-operator :as t-operator]
            [ote.app.routes :as routes]
            [ote.app.controller.login :as login]))

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

(define-event LoadOperatorDataResponse [response]
  {}
  (login/update-transport-operator-data app response))

(def every-5min (* 1000 60 1))
(def is-service-running? (atom false))

;; Load transport operator data (and services) every five minutes to ensure that possibly published service is shown correctly.
;; Services are published by admin
(defmethod tuck-effect/process-effect :serviceRunEvery5min [e! {:keys [on-success on-failure]}]
  (when (= false @is-service-running?)
    (reset! is-service-running? true)
    (.setInterval js/window (fn [_]
                              (when (= :own-services (:page @state/app))
                                (comm/post! "/transport-operator/data" {}
                                            {:on-success on-success
                                             :on-failure on-failure})))
                  every-5min)))


; Ensure that :transport-operator is not nil
; When user refreshes page our app-state removes data from :transport-operator key. That data is used
; everywhere to determine which operator is selected for usage.
(define-event InitOwnServices []
  {}
  (let [
        ;; Admin and user might be same account so clear admin settings when opening own-service page
        app (assoc-in app [:admin :in-validation :validating] nil)
        app (if (nil? (:transport-operator app))
              (assoc app :transport-operator (:transport-operator (first (get app :transport-operators-with-services))))
              app)]
    (tuck/fx app
             {:tuck.effect/type :serviceRunEvery5min
              :on-success (tuck/send-async! ->LoadOperatorDataResponse)
              :on-failure (tuck/send-async! ->ServerError)})))

(defmethod routes/on-navigate-event :own-services [_]
  (->InitOwnServices))
