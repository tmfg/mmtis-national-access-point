(ns ote.app.controller.route.gtfs
  "Save a planned route to GTFS"
  (:require [ote.util.zip :as zip]
            [ote.gtfs.spec :as gtfs-spec]
            [ote.gtfs.parse :as gtfs-parse]
            [ote.db.transport-operator :as t-operator]))

(defn- agency-txt [{:keys [transport-operator]}]
  [{:gtfs/agency-id 1 ;; always just one agency
    :gtfs/agency-name (::t-operator/name transport-operator)
    :gtfs/agency-url (::t-operator/homepage transport-operator)
    :gtfs/agency-timezone "Europe/Helsinki"
    :gtfs/agency-lang "FI"
    :gtfs/agency-phone (::t-operator/phone transport-operator)
    :gtfs/agency-email (::t-operator/email transport-operator)}])

(defn- stops-txt [{:keys [stop-sequence]}]
  (into []
        (map-indexed
         (fn [i {:keys [coordinates port-name port-id] :as stop}]
           ;; PENDING: need to add optional fields?
           {:gtfs/stop-id i
            :gtfs/stop-name port-name
            :gtfs/stop-lat (nth coordinates 0)
            :gtfs/stop-lon (nth coordinates 1)}))
        stop-sequence))

(defn- routes-txt [{:keys [name] :as route}]
  [{:gtfs/route-id 1
    :gtfs/route-short-name name
    :gtfs/route-long-name name
    :gtfs/route-type "4" ;; PENDING: hardcoded to 4 (ferry)
    :gtfs/agency-id 1}])

(defn- trips-txt [{:keys [times] :as route}]
  (into []
        (map-indexed
         (fn [i time]
           {:gtfs/route-id 1
            :gtfs/service-id 1 ;; only 1 calendar
            :gtfs/trip-id i}))
        times))

(defn- stop-times-txt [{:keys [times]}]
  (into []
        (comp
         (map-indexed
          (fn [i {:keys [stops] :as time}]
            (map-indexed
             (fn [stop-id {:keys [arrival-time departure-time]}]
               {:gtfs/trip-id i
                ;; First stop has no arrival time and last stop has no departure time
                :gtfs/arrival-time (or arrival-time departure-time)
                :gtfs/departure-time (or departure-time arrival-time)
                :gtfs/stop-id  stop-id
                :gtfs/stop-sequence stop-id})
             stops)))
         (mapcat identity))
        times))

(def files [["agency.txt" agency-txt gtfs-spec/agency-txt-fields]
            ["stops.txt" stops-txt gtfs-spec/stops-txt-fields]
            ["routes.txt" routes-txt gtfs-spec/routes-txt-fields]
            ["trips.txt" trips-txt gtfs-spec/trips-txt-fields]
            ["stop_times.txt" stop-times-txt gtfs-spec/stop-times-txt-fields]])

(defn save-gtfs [route file-name]
  (zip/write-zip (mapv (fn [[name generate-function fields]]
                         {:name name
                          :data (gtfs-parse/unparse-gtfs-file fields (generate-function route))})
                       files)
                 file-name))
