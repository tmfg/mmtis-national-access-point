(ns ote.tasks.tis
  (:require [amazonica.aws.s3 :as s3]
            [cheshire.core :as cheshire]
            [chime :as chime]
            [clj-http.client :as http-client]
            [clj-time.core :as t]
            [clj-time.periodic :as periodic]
            [com.stuartsierra.component :as component]
            [jeesql.core :refer [defqueries]]
            [ote.integration.tis-vaco :as tis-vaco]
            [ote.netex.netex :as netex]
            [ote.util.feature :as feature]
            [ote.integration.import.gtfs :as import-gtfs]
            [taoensso.timbre :as log]))

(defqueries "ote/tasks/tis.sql")

(defn ^:private copy-to-s3
  [config link filename]
  (when-let [href (get link "href")]
    (with-open [in (tis-vaco/download-package (:tis-vaco config) href)]
      (let [bucket    (get-in config [:netex :bucket])
            available (.available in)]
        (log/info (str "Copying file to " bucket "/" filename " (" available " bytes available)"))
        (s3/put-object bucket filename in {:content-length available})
        filename))))

(defn get-filename
  [package]
  (let [{operator-id  :transport-operator-id
         service-id   :transport-service-id
         interface-id :external-interface-description-id} package
        date (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") (java.util.Date.))]
    (str date "_" operator-id "_" service-id "_" interface-id "_netex.zip")))

(defn poll-incomplete-entry-results!
  "Polls incomplete TIS entries and complete them by storing the result links to database."
  [config db]
  (log/info "Polling for finished entries from TIS/VACO API")
  (when (and (feature/feature-enabled? config :tis-vaco-integration)
             (feature/feature-enabled? config :netex-conversion-automated))
    (let [packages (select-packages-without-finished-results db)]
      (log/info (str (count packages) " TIS packages to update"))
      (mapv
        (fn [package]
          (let [{package-id :id entry-public-id :tis-entry-public-id} (select-keys package [:id :tis-entry-public-id])]
            (log/info (str "Polling package " package-id "/" entry-public-id " for results"))
            (let [entry     (tis-vaco/api-fetch-entry (:tis-vaco config) entry-public-id)
                  complete? (every? (fn [t] (not (some? (get t "completed")))) (get-in entry ["data" "tasks"]))
                  result    (get-in entry ["links" tis-vaco/conversion-rule-name "result"])]
              (if result
                (do
                  (log/info (str "Result " result " found for package " package-id "/" entry-public-id ", copying blob to S3"))
                  (let [filename (copy-to-s3 config result (get-filename package))]
                    (netex/set-conversion-status!
                      {:netex-filepath filename
                       :s3-filename    filename
                       :package-id     package-id}
                      db
                      {:service-id                        (:transport-service-id package)
                       :external-interface-description-id (:external-interface-description-id package)
                       :external-interface-data-content   #{:route-and-schedule}})
                    (update-tis-results! db {:tis-entry-public-id entry-public-id
                                             :tis-complete        true
                                             :tis-success         true})))
                (if complete?
                  (do
                    (log/info (str "No results found for package " package-id "/" entry-public-id " but the entry is complete -> no result available"))
                    (update-tis-results! db {:tis-entry-public-id entry-public-id
                                             :tis-complete        true
                                             :tis-success         false}))
                  (log/info (str "Package " package-id "/" entry-public-id " processing is not yet complete on TIS side."))))))

          )
        packages))))

(defn poll-tis-entries!
  [config db]
  (try
    (poll-incomplete-entry-results! config db)
    (catch Exception e
      (log/warn e "Failure during polling!"))))

(defrecord TisTasks [config]
  component/Lifecycle
  (start [{db :db :as this}]
    (assoc this
      ::tis-tasks [(chime/chime-at (periodic/periodic-seq (t/now) (t/minutes 15))
                              (fn [_]
                                (#'poll-tis-entries! config db)))]))
  (stop [{stop-tasks ::tis-tasks :as this}]
    (doseq [stop stop-tasks]
      (stop))
    (dissoc this ::tis-tasks)))

(defn tis-tasks [config]
  (->TisTasks config))