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

(defn process-change [{next-different-week :next-different-week :as change}]
  (let [{:keys [current-week-traffic different-week-traffic change-date]} next-different-week
        current-week-start (t/minus (t/now) (t/days (dec (t/day-of-week (t/now)))))
        diff-days (keep #(when (not= ((:day->hash current-week-traffic) %)
                                     ((:day->hash different-week-traffic) %))
                           %)
                        time/week-days)]

    (assoc change :first-diff-dates {:date1 (first-diff-date diff-days current-week-start)
                                     :date2 (first-diff-date diff-days change-date)}
                  :diff-days diff-days
                  :week-start-date current-week-start
                  :diff-week-start-date change-date)))


(define-event TransitChangesResponse [response]
  {:path [:transit-changes]}
  (assoc app
         :changes (mapv process-change (:changes response))
         :finnish-regions (:finnish-regions response)
         :loading? false))

(define-event LoadTransitChanges []
  {:path [:transit-changes]}
  (comm/get! "transit-changes/current"
             {:on-success (tuck/send-async! ->TransitChangesResponse)
              :on-failure (tuck/send-async! ->ServerError)})
  (assoc app :loading? true))

(defmethod routes/on-navigate-event :transit-changes [_]
  (->LoadTransitChanges))

(define-event ShowChangesForOperator [id date1 date2]
  {}
  (routes/navigate! :transit-visualization {:operator-id id} {:compare-date1 date1
                                                              :compare-date2 date2})
  app)

(define-event SetRegionFilter [regions]
  {:path [:transit-changes :selected-finnish-regions]}
  regions)
