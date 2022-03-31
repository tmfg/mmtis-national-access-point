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
            [ote.services.transport-operator :as t-operator]
            [ote.integration.export.geojson :as geojson]
            [ote.integration.export.gtfs :as gtfs]
            [ote.util.transport-operator-util :as op-util]
            [ote.util.zip :as zip]
            [ring.util.io :as ring-io]
            [taoensso.timbre :as log])
  (:import [java.time LocalDateTime]
           [java.time.format DateTimeFormatter]))

(defqueries "ote/integration/export/geojson.sql")

(defn- ->geojson-feature
  [feature]
  (let [{:keys [geojson feature-id primary?]} feature
        geometry                              (cheshire/decode geojson keyword)]
    {:type       "Feature"
     :id         feature-id
     :properties {}  ; TODO: figure out use for this
     :geometry   geometry
     :style      {:fill (if primary? "green" "orange")}}))

(defn- ->geojson-features
  [features]
  (mapv ->geojson-feature features))

(defn- ->geojson-feature-collection [features]
  {:type     "FeatureCollection"
   :features (->geojson-features features)})

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

(defn debug [m x] (log/warn (str m " " x)) x)

(defn- ->static-schedule
  "Generate static 24 hour schedule for all areas"
  [areas]
  (->> (mapv
         (fn [area]
           (let [{:keys [feature-id]} area]
             {:gtfs/stop-id                         feature-id
              :gtfs/pickup-type                      2
              :gtfs/drop-off-type                    0
              :gtfs-flex/start_pickup_dropoff_window "0:00:00"
              :gtfs-flex/end_pickup_dropoff_window   "24:00:00"}))
           areas)
       (debug "resulting gtfs content")))

(defn join-routes-data
  "Splice together GTFS stop-times.txt with GTFS Flex compatible stop-times.txt by
    1. append GTFS Flex header and content
    2. drop GTFS header
    3. append GTFS content"
  [existing areas]
  (log/warn (str "existing routes " (count existing)))
  (log/warn (str "areas data " (count areas)))
  (str areas
       (str/replace-first existing #".*\n" ""))
  )
(defn export-gtfs-flex
  [db config transport-operator-id transport-service-id]

  (let [; load raw data and structures
        transport-operator (gtfs/get-transport-operator db transport-operator-id)
        areas              (seq (fetch-operation-area-for-service db {:transport-service-id transport-service-id}))
        routes             (gtfs/get-sea-routes db transport-operator-id)
        ; generate/convert file specific data structures
        trips              (gtfs-transform/sea-trips-txt routes)
        agency-txt         (gtfs-transform/agency-txt transport-operator)
        routes-txt         (gtfs-transform/sea-routes-txt (::t-operator/id transport-operator) routes)
        calendar-txt       (gtfs-transform/calendar-txt routes)
        calendar-dates-txt (gtfs-transform/calendar-dates-txt routes)
        trips-txt          (map #(dissoc % :stoptimes) trips)
        locations-geojson  (when-not (empty? areas)
                             (-> (->geojson-feature-collection areas)
                                 (cheshire/encode {:key-fn name})))
        stop-times-txt     (concat (gtfs-transform/sea-stop-times-txt routes trips)
                                   (->static-schedule areas))]

    (log/warn "Areas: " areas)
    {:status  200
     :headers {"Content-Type"        "application/zip"
               "Content-Disposition" (str "attachment; filename=" (op-util/gtfs-file-name transport-operator))}
     :body    (ring-io/piped-input-stream
                (->> [{:name "agency.txt"
                       :data (parse/unparse-gtfs-file :gtfs/agency-txt agency-txt)}
                      {:name "routes.txt"
                       :data (parse/unparse-gtfs-file :gtfs/routes-txt routes-txt)}
                      {:name "trips.txt"
                       :data (parse/unparse-gtfs-file :gtfs/trips-txt trips-txt)}
                      {:name "calendar.txt"
                       :data (parse/unparse-gtfs-file :gtfs/calendar-txt calendar-txt)}
                      {:name "calendar_dates.txt"
                       :data (parse/unparse-gtfs-file :gtfs/calendar-dates-txt calendar-dates-txt)}
                      {:name "locations.geojson"
                       :data locations-geojson}
                      {:name "location_groups.txt"
                       :data ""}
                      {:name "stop_times.txt"
                       :data (parse/unparse-gtfs-file :gtfs-flex/stop-times-txt stop-times-txt)}]
                     (partial zip-content)))}
    ; TODO: Hardcode 24h eternal (+5 years) schedule
    ))

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