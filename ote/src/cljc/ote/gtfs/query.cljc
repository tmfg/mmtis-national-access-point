(ns ote.gtfs.query
  "Query data from GTFS EDN representation."
  (:require [ote.gtfs.spec :as gtfs-spec]
            [ote.util.functor :refer [fmap]]
            [ote.util.fn :refer [flip]]))

(defn route-trips [{:gtfs/keys [trips-txt]} route-id]
  (filter #(= (:gtfs/route-id %) route-id) trips-txt))

(defn stop-sequences-for-trips [{:gtfs/keys [trips-txt stops-txt stop-times-txt] :as gtfs}
                                trips]
  (let [stops-by-id (into {} (map (juxt :gtfs/stop-id identity)) stops-txt)
        trip-ids (into #{} (map :gtfs/trip-id) trips)
        trip-stop-sequence
        (->> stop-times-txt
             (reduce
              (fn [trip-stops {:gtfs/keys [trip-id stop-id stop-sequence]}]
                (if-not (contains? trip-stops trip-id)
                  ;; Not a trip we are interested in
                  trip-stops
                  (update trip-stops trip-id assoc stop-sequence
                          (stops-by-id stop-id))))
              (zipmap trip-ids (repeat {})))

             (fmap (comp
                    (partial map val)
                    (partial sort-by first))))]
    trip-stop-sequence))

(defn distinct-stop-sequences [stop-sequences]
  (distinct (vals stop-sequences)))

(defn distinct-trips-times
  "Group trips by name & headsign and extract all start times"
  [{:gtfs/keys [stop-times-txt] :as gtfs} trips]
  (doall
   (for [[[name headsign] trips] (group-by (juxt :gtfs/trip-short-name :gtfs/trip-headsign) trips)]
     {:name name
      :headsign headsign})))
