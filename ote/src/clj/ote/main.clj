(ns ote.main
  "Finnish Transport Agency: OTE digitalization tool for transportation service information.
  Main entrypoint for the backend system."
  (:require [com.stuartsierra.component :as component]
            [ote.services.transport :as transport-service]
            [ote.components.http :as http]
            [ote.components.db :as db]

            [ote.services.index :as index]
            [ote.services.localization :as localization-service]
            [ote.services.places :as places]
            [ote.services.viewer :as viewer]
            [ote.services.external :as external]
            [ote.services.routes :as routes]
            [ote.services.service-search :as service-search]
            [ote.services.login :as login-service]
            [ote.services.admin :as admin-service]
            [ote.services.operators :as operators-service]
            [ote.services.pre-notices :as pre-notices]

            [ote.integration.export.geojson :as export-geojson]
            [ote.integration.export.gtfs :as export-gtfs]
            [ote.integration.import.gtfs :as import-gtfs]
            [ote.integration.import.kalkati :as import-kalkati]

            [ote.tasks.company :as tasks-company]
            [ote.tasks.pre-notices :as tasks-pre-notices]

            [taoensso.timbre :as log]
            [taoensso.timbre.appenders.3rd-party.rolling :as timbre-rolling]
            [jeesql.autoreload :as autoreload])
  (:gen-class))

(defonce ^{:doc "Handle for OTE-system"}
  ote nil)

(defn ote-system [config]
  (component/system-map
   ;; Basic components
   :db (db/database (:db config))
   :http (component/using (http/http-server (:http config)) [:db])

   ;; Index page
   :index (component/using (index/->Index config)
                           [:http])

   ;; Services for the frontend
   :transport (component/using (transport-service/->Transport (:nap config)) [:http :db])
   :viewer (component/using (viewer/->Viewer) [:http])
   :external (component/using (external/->External (:nap config)) [:http :db])
   :routes (component/using (routes/->Routes (:nap config)) [:http :db])
   :pre-notices (component/using (pre-notices/->PreNotices (:pre-notices config)) [:http :db])

   ;; Return localization information to frontend
   :localization (component/using
                  (localization-service/->Localization) [:http])

   ;; OpenStreetMap Overpass API queries
   :places (component/using (places/->Places (:places config)) [:http :db])

   ;; Service search
   :service-search (component/using
                    (service-search/->ServiceSearch)
                    [:http :db])

   ;; Integration: export GeoJSON and GTFS
   :export-geojson (component/using (export-geojson/->GeoJSONExport) [:db :http])
   :export-gtfs (component/using (export-gtfs/->GTFSExport) [:db :http])
   :import-gtfs (component/using (import-gtfs/->GTFSImport) [:http])
   :import-kalkati (component/using (import-kalkati/->KalkatiImport) [:http])

   :login (component/using
           (login-service/->LoginService (get-in config [:http :auth-tkt]))
           [:db :http])

   :admin (component/using
           (admin-service/->Admin (:nap config))
           [:db :http])

   :operators (component/using (operators-service/->Operators) [:db :http])

   ;; Scheduled tasks
   :tasks-company (component/using (tasks-company/company-tasks) [:db])
   :tasks-pre-notices (component/using (tasks-pre-notices/pre-notices-tasks
                                         (:email config)) [:db])))

(defn configure-logging [{:keys [level] :as log-config}]
  (log/merge-config!
   {:level (or level :debug)
    :appenders
    {:rolling
     (timbre-rolling/rolling-appender {:path "logs/ote.log" :pattern :daily})}}))

(defn start []
  (alter-var-root
   #'ote
   (fn [_]
     (let [config (read-string (slurp "config.edn"))]
       (configure-logging (:log config))
       (when (:dev-mode? config)
         (autoreload/start-autoreload))
       (component/start-system (ote-system config))))))

(defn stop []
  (component/stop-system ote)
  (alter-var-root #'ote (constantly nil)))

(defn restart []
  (when ote
    (stop))
  (start))

(defn -main [& args]
  (start))

(defn log-level-info! []
  (log/merge-config!
    {:appenders {:println {:min-level :info}}}))
