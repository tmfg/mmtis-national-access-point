(ns ote.main
  "Finnish Transport Agency: OTE digitalization tool for transportation service information.
  Main entrypoint for the backend system."
  (:require [com.stuartsierra.component :as component]
            [ote.palvelut.transport :as transport-service]
            [ote.components.http :as http]
            [ote.components.db :as db]
            [ote.services.localization :as localization-service]
            [ote.services.places :as places]
            [ote.integration.export.geojson :as export-geojson])
  (:gen-class))

(defonce ^{:doc "Handle for OTE-system"}
  ote nil)

(defn ote-system [config]
  (component/system-map
   ;; Basic components
   :db (db/database (:db config))
   :http (component/using (http/http-server (:http config)) [:db])

   ;; Services for the frontend
   :transport (component/using (transport-service/->Transport) [:http :db])

   ;; Return localization information to frontend
   :localization (component/using
                  (localization-service/->Localization) [:http])

   ;; OpenStreetMap Overpass API queries
   :places (component/using (places/->Places (:places config)) [:http :db])


   ;; Integration: export GeoJSON
   :export-geojson (component/using (export-geojson/->GeoJSONExport) [:db :http])))

(defn start []
  (alter-var-root
   #'ote
   (constantly
    (-> "config.edn" slurp read-string
        ote-system ; luo järjestelmä
        component/start-system))))

(defn stop []
  (component/stop-system ote)
  (alter-var-root #'ote (constantly nil)))

(defn restart []
  (when ote
    (stop))
  (start))

(defn -main [& args]
  (start))
