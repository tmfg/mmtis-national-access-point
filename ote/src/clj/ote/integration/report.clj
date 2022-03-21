(ns ote.integration.report
  "Centralized location for report logging for integration import processes"
  (:require [specql.core :as specql]))

(defn gtfs-import-report!
  [db ^String severity package-id ^String description ^bytes error]
  (specql/insert! db :gtfs-import/report {:gtfs-import/package_id            package-id
                                          :gtfs-import/description           description
                                          :gtfs-import/error                 error
                                          :gtfs-import/severity              severity}))
