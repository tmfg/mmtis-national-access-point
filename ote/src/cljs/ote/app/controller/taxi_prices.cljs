(ns ote.app.controller.taxi-prices
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [tuck.effect :as tuck-effect]
            [ote.app.state :as state]
            [ote.localization :refer [tr tr-key]]
            [ote.communication :as comm]
            [ote.app.controller.common :refer [->ServerError]]))

(defrecord LoadTaxiPrices [])
(defrecord LoadTaxiPricesResponse [response])
(defrecord ApproveByIds [pricing-ids])
(defrecord ApproveByIdsResponse [response])

(def every-1min (* 1000 60 1))
(def is-validation-running? (atom false))
(defmethod tuck-effect/process-effect :adminTaxiPricesEvery1min [e! {:keys [on-success on-failure]}]
  (when (= false @is-validation-running?)
    (reset! is-validation-running? true)
    (.setInterval js/window (fn [_]
                              (when (= :admin (:page @state/app))
                                (comm/get! "taxiui/approvals"
                                           {:on-success on-success
                                            :on-failure on-failure})))
                  every-1min)))

(defn- load-prices-to-approve []
  (comm/get! "taxiui/approvals"
             {:on-success (tuck/send-async! ->LoadTaxiPricesResponse)
              :on-failure (tuck/send-async! ->ServerError)}))

(extend-protocol tuck/Event

  LoadTaxiPrices
  (process-event [_ app]
    (do
      ;; Load services immediately
      (load-prices-to-approve)
      ;; And start timer
      (tuck/fx app {:tuck.effect/type :adminTaxiPricesEvery1min
                    :on-success (tuck/send-async! ->LoadTaxiPricesResponse)
                    :on-failure (tuck/send-async! ->ServerError)})))

  LoadTaxiPricesResponse
  (process-event [{response :response} app]
    (update-in app [:admin :taxi-prices] assoc
               :loading? false
               :results response))

  ApproveByIds
  (process-event [{pricing-ids :pricing-ids} app]
    (comm/post!
      "taxiui/approvals"
      {:pricing-ids pricing-ids}
      {:on-success (tuck/send-async! ->ApproveByIdsResponse)
       :on-failure (tuck/send-async! ->ServerError)})
    (-> app
        (update-in [:admin :taxi-prices] dissoc :results)
        (update-in [:admin :taxi-prices] assoc :loading? true)))

  ApproveByIdsResponse
  (process-event [{response :response} app]
    (load-prices-to-approve)
    app))
