(ns ote.app.controller.transit-changes
  "Controller for transit changes view"
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [ote.communication :as comm]
            [ote.app.controller.common :refer [->ServerError]]
            [ote.app.routes :as routes]
            [ote.time :as time]
            [cljs-time.core :as t]
            [ote.db.places :as places]))

(defn first-diff-date [diff-days week-start-date]
  (t/plus (time/native->date-time week-start-date)
          (t/days (time/week-day-order
                    (first diff-days)))))

(define-event TransitChangesResponse [response]
  {:path [:transit-changes]}
  (assoc app
         :changes-contains-errors (filter
                                    (fn [change]
                                      (or
                                        (:interfaces-has-errors? change)
                                        (:no-interfaces-imported? change)))
                                    (:changes response))
         :changes-contract-traffic (filter
                                     (fn [change]
                                       (= false (:commercial? change)))
                                     (:changes response))
         :changes (remove
                    (fn [change]
                      (or
                          (:interfaces-has-errors? change)
                          (:no-interfaces-imported? change)
                          (= false (:commercial? change))))
                    (:changes response))
         :finnish-regions (:finnish-regions response)
         :loading? false))

(define-event LoadTransitChanges []
  {:path [:transit-changes]}
  (comm/get! "transit-changes/current"
             {:on-success (tuck/send-async! ->TransitChangesResponse)
              :on-failure (tuck/send-async! ->ServerError)})
  (-> app
      (assoc :loading? true)
      (assoc :show-errors true)
      (assoc :show-contract-traffic false)))

(defmethod routes/on-navigate-event :transit-changes [_]
  (->LoadTransitChanges))

(define-event ShowChangesForService [id date]
  {}
  (routes/navigate! :transit-visualization {:service-id id
                                            :date (time/format-date-iso-8601 date)})
  app)

(define-event SetRegionFilter [regions]
  {:path [:transit-changes :selected-finnish-regions]}
  regions)

(define-event ToggleShowAllChanges []
  {:path [:transit-changes :show-errors]
   :app show?}
  (not show?))

(define-event ToggleShowContractTraffic []
  {:path [:transit-changes :show-contract-traffic]
   :app show?}
  (not show?))

(define-event ChangeTab [tab-value]
  {:path [:transit-changes :selected-tab]}
  (routes/navigate! (keyword tab-value))
  tab-value)