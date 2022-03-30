(ns ote.integration.export.gtfs-flex
  "GTFS Flex v2 exporter.

  GTFS Flex is effectively GTFS combined with Geojson and some mapping between geojson shapes and stop times to support
  more dynamic forms of transit, such as dial-a-ride, route deviation and hail-a-ride services.

  See also https://github.com/MobilityData/gtfs-flex"
  (:require [com.stuartsierra.component :as component]
            [compojure.core :refer [GET]]
            [ote.components.http :as http]
            [ote.gtfs.transform :as gtfs-transform]
            [ote.integration.export.geojson :as geojson]
            [ote.integration.export.gtfs :as gtfs]
            [ote.util.zip :as zip]
            [taoensso.timbre :as log]
            [ring.util.io :as ring-io]
            [ote.util.transport-operator-util :as op-util]))

(defn zip-content
  [zip-contents output-stream]
  (try
    (zip/write-zip zip-contents output-stream)
    (catch Exception e
      (log/warn "Exception while generating GTFS Flex zip" e))))

(defn export-gtfs-flex
  [db config transport-operator-id transport-service-id]

  (let [transport-operator (gtfs/get-transport-operator db transport-operator-id)
        routes             (gtfs/get-sea-routes db transport-operator-id)]

    {:status  200
     :headers {"Content-Type"        "application/zip"
               "Content-Disposition" (str "attachment; filename=" (op-util/gtfs-file-name transport-operator))}
     :body (ring-io/piped-input-stream
             (->> (conj (gtfs-transform/sea-routes-gtfs transport-operator routes {:gtfs/stop-times-txt #(concat % )})  ; returns ready-to-zip [{:name :data}, {...}, ...] structure
                        ; TODO: each polygon must have id Feature added
                        {:name "locations.geojson"
                         :data (:body (geojson/export-geojson db config transport-operator-id transport-service-id))}
                        {:name "location_groups.txt"
                         :data ""}
                        ; TODO: Hardcode 24h eternal (+5 years) schedule
                        )
               (partial zip-content)))}

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