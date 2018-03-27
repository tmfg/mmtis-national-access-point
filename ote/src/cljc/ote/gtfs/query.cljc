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

(defn shapes-for-ids [{shapes-txt :gtfs/shapes-txt} shape-ids]
  (fmap (partial sort-by :gtfs/shape-pt-sequence)
        (reduce
         (fn [lines {:gtfs/keys [shape-id] :as shape}]
           (if (shape-ids shape-id)
             (update lines shape-id (fnil conj []) shape)
             lines))
         {} shapes-txt)))


;;;;;;;;;;;;;;;,
;; lat/lon calculation utils
;; see: https://www.movable-type.co.uk/scripts/latlong.html
;; PENDING: eventually move these to better namespace
;; or even a separate library.

(def ^:const pi Math/PI)
(def ^:const R 6371e3)

(defn- to-rad [deg]
  (/ (* deg pi) 180))

(defn- to-deg [rad]
  (/ (* rad 180) pi))

(defn haversine-dist [[lat1 lon1] [lat2 lon2]]
  (let [o1 (to-rad lat1)
        o2 (to-rad lat2)
        dlat (to-rad (- lat2 lat1))
        dlon (to-rad (- lon2 lon1))
        a (+ (Math/pow (Math/sin (/ dlat 2)) 2)
             (* (Math/cos o1) (Math/cos o2)
                (Math/pow (Math/sin (/ dlon 2)) 2)))
        c (* 2 (Math/atan2 (Math/sqrt a)
                           (Math/sqrt (- 1 a))))]
    (* R c)))

(defn bearing
  "Initial bearing (forward azimuth)."
  [[lat1 lon1] [lat2 lon2]]
  (let [o1 (to-rad lat1)
        o2 (to-rad lat2)
        dlon (to-rad (- lon2 lon1))
        y (* (Math/sin dlon) (Math/cos o2))
        x (- (* (Math/cos o1) (Math/sin o2))
             (* (Math/sin o1) (Math/cos o2) (Math/cos dlon)))]
    (to-deg (Math/atan2 y x))))


(defn bearing-markers
  "Create bearing markers for shape. Shape is an ordered sequence
  of GTFS shape maps."
  [shape min-distance]
  (let [shape (filter :gtfs/shape-dist-traveled shape)
        pos (juxt :gtfs/shape-pt-lat :gtfs/shape-pt-lon)]
    (loop [markers []
           last-position (pos (first shape))
           last-marker-distance 0
           [{dist :gtfs/shape-dist-traveled
             :as point} & points] (rest shape)]
      (if-not point
        markers
        (let [cur-pos (pos point)
              add? (>= dist (+ last-marker-distance min-distance))]

          (recur (if add?
                   (conj markers {:position last-position
                                  :bearing (bearing last-position cur-pos)})
                   markers)
                 cur-pos
                 (if add? dist last-marker-distance)
                 points))))))
