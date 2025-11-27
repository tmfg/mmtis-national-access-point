(ns ote.main
  "Finnish Transport Agency: OTE digitalization tool for transportation service information.
  Main entrypoint for the backend system."
  (:require [com.stuartsierra.component :as component]
            [ote.services.transport :as transport-service]
            [ote.services.transport-operator :as transport-operator]
            [ote.services.register :as register-services]
            [ote.components.http :as http]
            [ote.components.db :as db]
            [ote.email :as email]
            [ote.environment :as env]

            [ote.services.index :as index]
            [ote.services.localization :as localization-service]
            [ote.services.places :as places]
            [ote.services.external :as external]
            [ote.services.users :as users]
            [ote.services.routes :as routes]
            [ote.services.service-search :as service-search]
            [ote.services.login :as login-service]
            [ote.services.admin :as admin-service]
            [ote.services.settings :as settings-service]
            [ote.services.pre-notices :as pre-notices]
            [ote.services.transit-visualization :as transit-visualization]
            [ote.services.transit-changes :as transit-changes]
            [ote.services.taxiui-service :as taxiui-services]
            [ote.services.robots :as robots]
            [ote.services.health :as health]

            [ote.services.rdf :as rdf]

            [ote.integration.export.geojson :as export-geojson]
            [ote.integration.export.gtfs :as export-gtfs]
            [ote.integration.export.csv :as export-csv]
            [ote.integration.export.netex :as export-netex]
            [ote.integration.export.gtfs-flex :as export-gtfsflex]

            [ote.integration.import.gtfs :as import-gtfs]
            [ote.integration.import.kalkati :as import-kalkati]
            [ote.integration.import.ytj :as fetch-ytj]

            [ote.tasks.company :as tasks-company]
            [ote.tasks.pre-notices :as tasks-pre-notices]
            [ote.tasks.gtfs :as tasks-gtfs]
            [ote.tasks.taxiui :as tasks-taxiui]
            [ote.tasks.tis :as tasks-tis]

            [ote.util.feature :as feature]

            [ote.services.taxi-index :as taxi-index]

            [taoensso.timbre :as log]
            [taoensso.timbre.appenders.3rd-party.rolling :as timbre-rolling]
            [ote.tasks.rdf :as rdf-task]
            [jeesql.autoreload :as autoreload])
  (:gen-class))

(defonce ^{:doc "Handle for OTE-system"}
  ote nil)

(defn ote-system [config]
  (component/system-map
   ;; Basic components
   :db (db/database (:db config))
   :http (component/using (http/http-server (:http config)) [:db])
   :ssl-upgrade (http/map->SslUpgrade (get-in config [:http :ssl-upgrade]))
   :email (email/->Email (:email config))

   :rdf (component/using (rdf/->RDS config) [:http :db])

   ;; Index pages for frontends
   :ote/index (component/using (index/->Index config) [:http :db])
   :taxi/index (component/using (taxi-index/->Index config) [:http :db])

   :robots (component/using (robots/->RobotsTxt (get-in config [:http :allow-robots?])) [:http])
   :users (component/using (users/->UsersService (get-in config [:http :auth-tkt]))  [:http :db :email])
   ;; Services for the frontend
   :register (component/using (register-services/->Register config) [:http :db :email])
   :transport (component/using (transport-service/->TransportService config) [:http :db :email])
   :transport-operator (component/using (transport-operator/->TransportOperator config) [:http :db :email])
   :external (component/using (external/->External (:nap config)) [:http :db])
   :routes (component/using (routes/->Routes (:nap config)) [:http :db])
   :pre-notices (component/using (pre-notices/->PreNotices config) [:http :db])
   :transit-visualization (component/using (transit-visualization/->TransitVisualization) [:http :db])
   :transit-changes (component/using (transit-changes/->TransitChanges config) [:http :db :email])
   ;; Return localization information to frontend
   :localization (component/using
                  (localization-service/->Localization) [:http])

   :taxiui-service (component/using (taxiui-services/->TaxiUIService) [:http :db])

   ;; OpenStreetMap Overpass API queries
   :places (component/using (places/->Places (:places config)) [:http :db])

   ;; Service search
   :service-search (component/using
                    (service-search/->ServiceSearch config)
                    [:http :db])

   ;; Integration: export GeoJSON, GTFS and CSV
   :export-geojson (component/using (export-geojson/->GeoJSONExport config) [:db :http])
   :export-gtfs (component/using (export-gtfs/->GTFSExport) [:db :http])
   :export-csv (component/using (export-csv/->CSVExport) [:db :http])
   :export-netex (component/using (export-netex/->NeTExExport config) [:db :http])
   :import-gtfs (component/using (import-gtfs/->GTFSImport (:gtfs config)) [:db :http])
   :import-kalkati (component/using (import-kalkati/->KalkatiImport) [:http])
   :export-gtfsflex (component/using (export-gtfsflex/->GTFSFlexExport config) [:db :http])

   ;; rdf -> s3 
   :rdf->s3 (component/using (rdf-task/rdf-tasks (:dev-mode? config)
                                                 (:rdf-export config)) [:db])

   ;; Integration: Fetch company data from YTJ
   :fetch-ytj (component/using (fetch-ytj/->YTJFetch config) [:db :http])

   :login (component/using
           (login-service/->LoginService (get-in config [:http :auth-tkt]))
           [:db :http :email])

   :admin (component/using
           (admin-service/->Admin config)
           [:db :http :email])

   :admin-reports (component/using
                    (admin-service/->CSVAdminReports)
                    [:db :http])

   :monitor (component/using
                    (admin-service/->MonitorReport)
                    [:db :http])

   :monitor-csv (component/using
                    (admin-service/->MonitorReportCSV)
                    [:db :http])

   :settings (component/using (settings-service/->Settings) [:db :http])

   ;; Scheduled tasks
   :tasks-company (component/using (tasks-company/company-tasks) [:db])
   :tasks-gtfs (component/using (tasks-gtfs/gtfs-tasks config) [:db :email])
   :tasks-pre-notices (component/using (tasks-pre-notices/pre-notices-tasks (:pre-notices config))
                                       [:db :email])
   :tasks-tis (component/using (tasks-tis/tis-tasks config) [:db])
   :tasks-taxiui (component/using (tasks-taxiui/taxiui-tasks config) [:db])

   :health (component/using (health/->Health config) [:db :http])))

(defn configure-logging [dev-mode? {:keys [level] :as log-config}]
  (log/merge-config!
   {:level (or level :debug)
    :middleware [(fn drop-hikari-stats-middlware [data]
                   (if (and
                        (= :debug (:level data))
                        (= "com.zaxxer.hikari.pool.HikariPool" (:?ns-str data)))
                     nil
                     ;; else
                     data))]
    :appenders
    (if dev-mode?
      ;; In dev-mode only do println logging
      {:println {:enabled? true}}

      ;; In production only do file logging (so that logs don't end up in /var/log/messages)
      {:println {:enabled? false}
       :rolling
       (timbre-rolling/rolling-appender {:path "logs/ote.log" :pattern :daily})})}))

(defn start []
  (alter-var-root
   #'ote
   (fn [_]
     (let [config (read-string (slurp "config.edn"))]
       (configure-logging (:dev-mode? config) (:log config))
       (env/merge-environment! (:environment config))
       (feature/set-enabled-features! (or (:enabled-features config) #{}))
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
