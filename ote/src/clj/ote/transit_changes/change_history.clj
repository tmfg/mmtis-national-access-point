(ns ote.transit-changes.change-history
  "Store detected changes once to db."
  (:require
    [ote.time :as time]
    [jeesql.core :refer [defqueries]]
    [specql.core :as specql]
    [ote.util.collections :refer [map-by count-matching]]
    [ote.util.functor :refer [fmap]]
    [digest]))

(def history-columns #{:gtfs/change-key :gtfs/route-hash-id :gtfs/different-week-date})

(defn create-change-key-from-change-data
  "Create change-key for changes which is kind of same what route-hash-id is for routes. It creates unique key to detect that changes
  are stored only once to db."
  [change-data]
  (let [relevant-keys #{:gtfs/removed-trips :gtfs/trip-stop-sequence-changes-lower
                        :gtfs/trip-stop-sequence-changes-upper :gtfs/route-hash-id
                        :gtfs/trip-stop-time-changes-lower :gtfs/trip-stop-time-changes-upper :gtfs/change-type
                        :gtfs/added-trips :gtfs/different-week-date}
        change-data (select-keys change-data relevant-keys)
        change-data (into (sorted-map) change-data)         ;; Sort map by keys
        change-key (digest/sha-256 (str change-data))]
    (merge change-data {:gtfs/change-key change-key})))

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
        service-changes (map
                          #(create-change-key-from-change-data %)
                          route-change-infos)
        unsaved-changes (remove
                          (fn [new-change]
                            (some #(= (:gtfs/change-key new-change) (:gtfs/change-key %)) service-change-history))
                          service-changes)]
    (doseq [u unsaved-changes]
      (when u
        (specql/insert! db :gtfs/detected-change-history
                        {:gtfs/transport-service-id service-id
                         :gtfs/change-key (:gtfs/change-key u)
                         :gtfs/route-hash-id (:gtfs/route-hash-id u)
                         :gtfs/change-detected (time/sql-date (java.time.LocalDate/now))
                         :gtfs/different-week-date (:gtfs/different-week-date u)
                         :gtfs/package-ids package-ids
                         :gtfs/change-type (:gtfs/change-type u)})))))
