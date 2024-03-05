(ns ote.integration.export.gtfs-flex
  "GTFS Flex v2 exporter.

  GTFS Flex is effectively GTFS combined with Geojson and some mapping between geojson shapes and stop times to support
  more dynamic forms of transit, such as dial-a-ride, route deviation and hail-a-ride services.

  See also https://github.com/MobilityData/gtfs-flex"
  (:require [cheshire.core :as cheshire]
            [clojure.string :as str]
            [com.stuartsierra.component :as component]
            [compojure.core :refer [GET]]
            [jeesql.core :refer [defqueries]]
            [ote.components.http :as http]
            [ote.gtfs.parse :as parse]
            [ote.gtfs.transform :as gtfs-transform]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]
            [ote.integration.export.geojson :as geojson]
            [ote.integration.export.gtfs :as gtfs]
            [ote.services.transport :as transport-service]
            [ote.util.transport-operator-util :as op-util]
            [ote.util.zip :as zip]
            [ring.util.io :as ring-io]
            [taoensso.timbre :as log]
            [specql.core :as specql])
  (:import [java.time LocalDateTime LocalDate]
           [java.time.format DateTimeFormatter]))

(defqueries "ote/integration/export/geojson.sql")

(defn- ->geojson-feature
  [area]
  (let [{:keys [geojson feature-id primary?]} area
        geometry                              (cheshire/decode geojson keyword)]
    {:type       "Feature"
     :id         feature-id
     :properties {}  ; TODO: figure out use for this
     :geometry   geometry
     :style      {:fill (if primary? "green" "orange")}}))

(defn- ->geojson-features
  [areas]
  (mapv ->geojson-feature areas))

(defn- ->geojson-feature-collection
  [areas]
  {:type     "FeatureCollection"
   :features (->geojson-features areas)})

(defn- mapv-indexed
  "Like `mapv`, but with indices. Uses one-based index as those are better for non-technical users"
  [f c]
  (into [] (map-indexed
             (fn [n i] (f (inc n) i))
             c)))

(defn export-geojson
  [db transport-service-id]
  (let [areas (seq (fetch-operation-area-for-service db {:transport-service-id transport-service-id}))]
    (when-not (empty? areas)
      (->geojson-feature-collection areas))))

(defn zip-content
  [zip-contents output-stream]
  (try
    (zip/write-zip zip-contents output-stream)
    (catch Exception e
      (log/warn "Exception while generating GTFS Flex zip" e))))

(defn- ->static-stop-times
  "Generate static 24 hour stop times for all areas with the same trip-id"
  [trip-id areas flex-booking-rules]
  (let [booking-id (:gtfs-flex/booking_rule_id flex-booking-rules)]
    (mapv-indexed
      (fn [n area]
        (let [{:keys [feature-id]} area]
          {:gtfs/trip-id                          trip-id
           :gtfs/stop-id                          feature-id
           :gtfs/pickup-type                      2
           :gtfs/drop-off-type                    2
           :gtfs/stop-sequence                    n
           :gtfs-flex/start_pickup_dropoff_window "0:00:00"
           :gtfs-flex/end_pickup_dropoff_window   "24:00:00"
           :gtfs-flex/pickup_booking_rule_id      booking-id
           :gtfs-flex/drop_off_booking_rule_id    booking-id}))
      areas)))

(defn join-routes-data
  "Splice together GTFS stop-times.txt with GTFS Flex compatible stop-times.txt by
    1. append GTFS Flex header and content
    2. drop GTFS header
    3. append GTFS content"
  [existing areas]
  (log/warn (str "existing routes " (count existing)))
  (log/warn (str "areas data " (count areas)))
  (str areas
       (str/replace-first existing #".*\n" "")))

(defn ->static-trips
  "Generate a trip reference for given route-id, trip-id, service-id triplet.

  This is a binding relation between the three, no actual scheduling metadata is involved in this file."
  [route-id trip-id service-id]
  {:gtfs/route-id   route-id
   :gtfs/trip-id    trip-id
   :gtfs/service-id service-id})

(defn ->static-routes
  "Generate a static route reference for given route-id to specify an eternally servicing route."
  [route-id route-type transport-operator-id operator-name service-name]
  {:gtfs/route-id         route-id
   :gtfs/route-short-name operator-name
   :gtfs/route-long-name  service-name
   :gtfs/route-type (case route-type
                      :light-rail "0"
                      :subway "1"
                      :rail "2"
                      :bus "3"
                      :ferry "4"
                      :cable-car "5"
                      :gondola "6"
                      :funicular "7")
   :gtfs/agency-id transport-operator-id})

(defn ->static-calendar
  [service-id transport-service]
  {:gtfs/service-id service-id
   :gtfs/monday     1
   :gtfs/tuesday    1
   :gtfs/wednesday  1
   :gtfs/thursday   1
   :gtfs/friday     1
   :gtfs/saturday   1
   :gtfs/sunday     1
   :gtfs/start-date (or (::t-service/available-from transport-service)
                        (LocalDate/now))
   :gtfs/end-date   (or (::t-service/available-to transport-service)
                        (-> (LocalDate/now) (.plusYears 5)))})

(defn ->static-location-groups
  [areas]
  ; TODO: should possibly generate stops locations as well?
  (mapv-indexed
    (fn [n area]
      (let [{:keys [feature-id]} area]
        {:gtfs-flex/location_group_id   (str feature-id "_a_" n)
         :gtfs-flex/location_id         feature-id
         :gtfs-flex/location_group_name feature-id}))
    areas))

(defn ->booking-rules
  [db service]
  (when-let [{:keys [application-link phone-countrycode phone-number]} (transport-service/get-rental-booking-info db (::t-service/id service))]
    {:gtfs-flex/booking_rule_id               1
     :gtfs-flex/booking_type                  2
     :gtfs-flex/booking_url                   application-link
     :gtfs-flex/booking_prior_notice_last_day 1
     :gtfs-flex/booking_phone_number          (str phone-countrycode phone-number)}))

(defn get-transport-service
  [db transport-service-id]
  (first (specql/fetch db ::t-service/transport-service
                       #{::t-service/id
                         ::t-service/name
                         ::t-service/available-from
                         ::t-service/available-to}
                       {::t-service/id transport-service-id})))

(defn export-gtfs-flex
  "This function is an adaptation of the GTFS generation in [[ote.gtfs.transform/sea-routes-gtfs]]"
  [db config transport-operator-id transport-service-id]

  ; load raw data and structures
  (let [transport-operator (gtfs/get-transport-operator db transport-operator-id)
        areas              (seq (fetch-operation-area-for-service db {:transport-service-id transport-service-id}))
        routes             (->> (gtfs/get-sea-routes db transport-operator-id)
                                (mapv #(assoc % :services (gtfs-transform/route-services %))))
        agency-txt         (gtfs-transform/agency-txt transport-operator)
        calendar-dates-txt (gtfs-transform/calendar-dates-txt routes)
        ; XXX: trips carries over metadata which is not part of any spec, but the existing functionality relies on it
        pseudo-trips       (gtfs-transform/sea-trips-txt routes)
        gtfs-trips         (->> pseudo-trips (map #(dissoc % :stoptimes)))
        gtfs-stop-times    (gtfs-transform/sea-stop-times-txt routes pseudo-trips)
        gtfs-routes        (gtfs-transform/sea-routes-txt (::t-operator/id transport-operator) routes)
        gtfs-stops (gtfs-transform/stops-txt (into {}
                                                   (comp (mapcat :ote.db.transit/stops)
                                                         (map (juxt :ote.db.transit/code identity)))
                                                   routes))
        gtfs-calendar      (gtfs-transform/calendar-txt routes)]
    ; complement GTFS content with GTFS Flex additions
    (let [transport-service (get-transport-service db transport-service-id)
          static-route-id   (str (::t-operator/name transport-operator) " route")
          static-service-id (str (::t-operator/name transport-operator) " schedule")
          static-trip-id    (str (::t-operator/name transport-operator) " transport service")
          flex-trips        (conj gtfs-trips
                                  (->static-trips static-route-id static-trip-id static-service-id))
          ; TODO: these are not all buses, but lets go with this one for now - areas and services need unique entries
          ; most likely to produce accurate data
          flex-routes       (conj gtfs-routes
                                  (->static-routes static-route-id :bus transport-operator-id (::t-operator/name transport-operator) (::t-service/name transport-service)))
          flex-booking-rule (->booking-rules db transport-service)  ; TODO: maybe apply to all stop times?
          flex-stop-times   (concat gtfs-stop-times
                                    (->static-stop-times static-trip-id areas flex-booking-rule))
          flex-calendar     [(->static-calendar static-service-id transport-service)]
          flex-locations    (when-not (empty? areas)
                              (-> (->geojson-feature-collection areas)
                                  (cheshire/encode {:key-fn name})))
          flex-location-groups (->static-location-groups areas)]
    {:status  200
     :headers {"Content-Type"        "application/zip"
               "Content-Disposition" (str "attachment; filename=" (op-util/gtfs-flex-file-name transport-operator))}
     :body    (ring-io/piped-input-stream
                (->> [{:name "agency.txt"
                       :data (parse/unparse-gtfs-file :gtfs/agency-txt agency-txt)}
                      {:name "routes.txt"
                       :data (parse/unparse-gtfs-file :gtfs/routes-txt flex-routes)}
                      {:name "stops.txt"
                       :data (parse/unparse-gtfs-file :gtfs/stops-txt gtfs-stops)}
                      {:name "trips.txt"
                       :data (parse/unparse-gtfs-file :gtfs/trips-txt flex-trips)}
                      {:name "calendar.txt"
                       :data (parse/unparse-gtfs-file :gtfs/calendar-txt flex-calendar)}
                      {:name "calendar_dates.txt"
                       :data (parse/unparse-gtfs-file :gtfs/calendar-dates-txt calendar-dates-txt)}
                      {:name "locations.geojson"
                       :data flex-locations}
                      {:name "location_groups.txt"
                       :data (parse/unparse-gtfs-file :gtfs-flex/location-groups-txt flex-location-groups)}
                      {:name "stop_times.txt"
                       :data (parse/unparse-gtfs-file :gtfs-flex/stop-times-txt flex-stop-times)}
                      {:name "booking_rules.txt"
                       :data (parse/unparse-gtfs-file :gtfs-flex/booking-rules-txt [flex-booking-rule])}]
                     (partial zip-content)))})))

(defrecord GTFSFlexExport [config]
  component/Lifecycle
  (start [{http :http
           db :db :as this}]
    (assoc this
      ::stop (http/publish! http {:authenticated? false}
                            (GET "/export/gtfs-flex/:transport-operator-id{[0-9]+}/:transport-service-id{[0-9]+}"
                                 [transport-operator-id transport-service-id]
                                 ; GTFS Export
                                 (export-gtfs-flex db config (Long/parseLong transport-operator-id) (Long/parseLong transport-service-id))))))
  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))