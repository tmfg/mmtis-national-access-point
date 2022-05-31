(ns ote.integration.report
  "Centralized location for report logging for integration import processes"
  (:require [specql.core :as specql]
            [jeesql.core :refer [defqueries]]
            [ote.db.utils :as db-utils]))

(defqueries "ote/integration/report.sql")

(defn gtfs-import-report!
  [db ^String severity package-id ^String description ^bytes error]
  (specql/insert! db :gtfs-import/report {:gtfs-import/package_id            package-id
                                          :gtfs-import/description           description
                                          :gtfs-import/error                 error
                                          :gtfs-import/severity              severity}))

(defn latest-import-reports-for-all-packages
  [db]
  (->> (fetch-import-reports-for-latest-packages db)
       (map (comp db-utils/underscore->structure
                  #(update % :gtfs-import-report_error (fn [v] (String. v)))))))

(defn latest-import-reports-for-service-interface
  [db service-id interface-id]
  (->> (fetch-latest-import-reports-for-service db {:transport-service-id              service-id
                                                    :external-interface-description-id interface-id})
       (map (comp db-utils/underscore->structure
                  #(update % :gtfs-import-report_error (fn [v] (String. v)))))))

(defn clean-old-reports!
  "Deletes all but latest report for given service-id + interface-id"
  [db service-id interface-id]
  (->> (delete-old-import-reports-for-service db {:transport-service-id              service-id
                                                  :external-interface-description-id interface-id})))