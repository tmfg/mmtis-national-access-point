(ns ote.transit-changes.change-history
  "Detect changes in transit traffic patterns.
  Interfaces with stored GTFS transit data."
  (:require
    [ote.time :as time]
    [jeesql.core :refer [defqueries]]
    [specql.core :as specql]
    [ote.util.collections :refer [map-by count-matching]]
    [ote.util.functor :refer [fmap]]
    [digest]))

(def history-columns #{:gtfs/change-str :gtfs/route-hash-id :gtfs/different-week-date})

(defn create-change-str-from-change-data [change-data]
  (let [relevant-keys #{:gtfs/removed-trips :gtfs/trip-stop-sequence-changes-lower
                        :gtfs/trip-stop-sequence-changes-upper :gtfs/route-hash-id
                        :gtfs/trip-stop-time-changes-lower :gtfs/trip-stop-time-changes-upper :gtfs/change-type
                        :gtfs/added-trips :gtfs/different-week-date}
				change-data (select-keys change-data relevant-keys)
				change-data (into (sorted-map) change-data)        ;; Sort map by keys
        value-string (digest/sha-256 (str change-data))]
    (merge change-data {:gtfs/change-str value-string})))

(defn update-change-history
  "To be able to tell when change is detected at the first time, we need to store change results to the db."
  [db analysis-date service-id package-ids route-change-infos]
  {:pre [(some? analysis-date)
         (pos-int? service-id)]}
	(let [service-change-history (into [] (specql/fetch db :gtfs/detected-change-history
                                                      ;; Columns
                                                      history-columns
                                                      ;; WHERE
                                                      {:gtfs/transport-service-id service-id}))
        service-change-strings (map
                                 #(create-change-str-from-change-data %)
                                 route-change-infos)
        unsaved-changes  (remove
                          (fn [new-change]
                            (some #(= (:gtfs/change-str new-change) (:gtfs/change-str %)) service-change-history))
                          service-change-strings)]
     (doseq [u unsaved-changes]
			 (specql/insert! db :gtfs/detected-change-history
											 {:gtfs/transport-service-id service-id
												:gtfs/change-str (:gtfs/change-str u)
												:gtfs/route-hash-id (:gtfs/route-hash-id u)
												:gtfs/change-detected (time/sql-date (java.time.LocalDate/now))
												:gtfs/different-week-date (:gtfs/different-week-date u)
												:gtfs/package-ids package-ids
												:gtfs/change-type (:gtfs/change-type u)}))))
