(ns ote.tasks.tis
  (:require [amazonica.aws.s3 :as s3]
            [chime :as chime]
            [clj-time.core :as t]
            [clj-time.periodic :as periodic]
            [clojure.string :as str]
            [com.stuartsierra.component :as component]
            [jeesql.core :refer [defqueries]]
            [ote.db.lock :as lock]
            [ote.integration.tis-vaco :as tis-vaco]
            [ote.netex.netex :as netex]
            [ote.tasks.util :as tasks-util]
            [ote.util.feature :as feature]
            [ote.util.tis-configs :as tis-configs]
            [specql.core :as specql]
            [taoensso.timbre :as log]
            [ote.services.operators :as operators-q]))

(defqueries "ote/tasks/tis.sql")

(declare fetch-external-interface-for-package select-packages-without-finished-results fetch-count-service-packages
         update-tis-results! list-all-external-interfaces)

(defn ^:private copy-to-s3
  [config link filename]
  (when-let [href (get link "href")]
    (with-open [in (tis-vaco/api-download-file (:tis-vaco config) href)]
      (let [bucket    (get-in config [:netex :bucket])
            available (.available in)]
        (log/info (str "Copying file to " bucket "/" filename " (" available " bytes available)"))
        ;; Try and catch put. S3PUT doesn't work in localhost by default (it can be enabled), so we need to catch the exception.
        (try
          (s3/put-object bucket filename in {:content-length available})
          (catch Exception e
            (log/error e "Failed to copy file to S3")))
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
  (when (feature/feature-enabled? config :tis-vaco-integration)
    (let [packages (select-packages-without-finished-results db)]
      (log/info (str (count packages) " TIS packages to update"))
      (mapv
        (fn [package]
          (let [{package-id :id entry-public-id :tis-entry-public-id} (select-keys package [:id :tis-entry-public-id])]
            (log/info (str "Polling package " package-id "/" entry-public-id " for results"))
            (let [entry      (tis-vaco/api-fetch-entry (:tis-vaco config) entry-public-id)
                  complete?  (let [error  (get-in entry ["error"])
                                   status (get-in entry ["data" "status"])]
                               (when (some? error)
                                 (log/info (str "API error when fetching entry " entry-public-id ": " error)))
                               (or (some? error)
                                   (not (or (nil? status)
                                            (= status "received")
                                            (= status "processing")))))
                  result     (get-in entry ["links" "gtfs2netex.fintraffic" "result"])
                  magic-link (get-in entry ["links" "refs" "magic" "href"])]
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
                    (log/info "Netex conversion status updated.")
                    (update-tis-results! db {:tis-entry-public-id entry-public-id
                                             :tis-complete        true
                                             :tis-success         true
                                             :tis-magic-link      magic-link})
                    (log/info "VACO integration status updated.")))
                (when complete?
                  (do
                    (log/info (str "No results found for package " package-id "/" entry-public-id " but the entry is complete -> no result available"))
                    (update-tis-results! db {:tis-entry-public-id entry-public-id
                                             :tis-complete        true
                                             :tis-success         false
                                             :tis-magic-link      nil}))
                  (log/info (str "Package " package-id "/" entry-public-id " processing is not yet complete on TIS side.")))))))
        packages))))

(defn ^:private interface-latest-package [db interface-id]
  (when interface-id
    (first
      (specql/fetch db :gtfs/package
                    #{:gtfs/id :gtfs/etag :gtfs/sha256}
                    {:gtfs/external-interface-description-id interface-id
                     :gtfs/deleted? false}
                    {::specql/order-by :gtfs/created
                     ::specql/order-direction :descending
                     ::specql/limit 1}))))

(defn ^:private create-package
  [db operator-id service-id external-interface-description-id license]
  (let [latest-package (interface-latest-package db external-interface-description-id)
        package-count (:package-count (first (fetch-count-service-packages db {:service-id service-id})))]
    (specql/insert! db :gtfs/package
                    {:gtfs/first_package (= 0 package-count)
                     :gtfs/transport-operator-id operator-id
                     :gtfs/transport-service-id service-id
                     :gtfs/created (java.sql.Timestamp. (System/currentTimeMillis))
                     :gtfs/license (or license "CC BY 4.0")
                     :gtfs/external-interface-description-id external-interface-description-id})))

(defn submit-single-package [config db package-id]
  (let [;; Get interface for package
        interface (first (fetch-external-interface-for-package db {:package-id package-id}))
        external-interface-description-id (:id interface)
        format (str/lower-case (:format interface))
        service-id (:transport-service-id interface)
        operator (first (operators-q/fetch-operator-by-service-id db {:service-id service-id}))
        operator-id (:id operator)
        package (create-package db operator-id service-id external-interface-description-id (:license interface))
        _ (log/info (str "Submit single package " (:gtfs/id package) " for " operator-id "/" service-id "/" external-interface-description-id " to TIS VACO for processing"))
        result (tis-vaco/queue-entry db (:tis-vaco config)
                                     {:url (:url interface)
                                      :operator-id operator-id
                                      :id external-interface-description-id}
                                     {:service-id service-id
                                      :package-id (:gtfs/id package)
                                      :external-interface-description-id external-interface-description-id
                                      :operator-name (:name operator)
                                      :contact-email (:email operator)}
                                     (merge {:format format}
                                            (tis-configs/vaco-create-payload format)))
        _ (log/info "Single package result:" result)]
    result
    ; return nil to allow early collection of intermediate results
    nil))

(defn submit-known-interfaces!
  [config db]
  (when (feature/feature-enabled? config :tis-vaco-integration)
    (lock/with-exclusive-lock
      db "tis-vaco-queue-entries" 1800  ; lock for 30 minutes
      (do
        (log/info "Submitting all known external interfaces as new entries to TIS/VACO API")
        (try
          ; execute sequentially for side effects only, discarding intermediate entries to help garbage collector do its thing
          (doseq [interface (list-all-external-interfaces db)]
            (let [{:keys [operator-id operator-name service-id external-interface-description-id url license format contact-email]} interface
                  package (create-package db operator-id service-id external-interface-description-id license)]
              (log/info (str "Submit package " (:gtfs/id package) " for " operator-id "/" service-id "/" external-interface-description-id " to TIS VACO for processing"))
              (tis-vaco/queue-entry db (:tis-vaco config)
                                    {:url         url
                                     :operator-id operator-id
                                     :id          external-interface-description-id}
                                    {:service-id                        service-id
                                     :package-id                        (:gtfs/id package)
                                     :external-interface-description-id external-interface-description-id
                                     :operator-name                     operator-name
                                     :contact-email                     contact-email}
                                    (merge {:format format}
                                           (tis-configs/vaco-create-payload format)))
              ; return nil to allow early collection of intermediate results
              nil))
          (catch Exception e
            (log/warn e "Failed to submit known interfaces")))))))

(defn submit-finap-feeds!
  [config db]
  (try
    (submit-known-interfaces! config db)
    (catch Exception e
      (log/warn e "Failure during polling!"))))

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
      ::tis-tasks [(chime/chime-at (tasks-util/daily-at
                                     ; run in testing in the morning so that nightly shutdown doesn't affect the API calls
                                     (if (:testing-env? config) 10 3) 15)
                                   (fn [_]
                                     (#'submit-finap-feeds! config db)))
                   (chime/chime-at (drop 1 (periodic/periodic-seq (t/now) (t/minutes 10)))
                                   (fn [_]
                                     (#'poll-tis-entries! config db)))]))
  (stop [{stop-tasks ::tis-tasks :as this}]
    (doseq [stop stop-tasks]
      (stop))
    (dissoc this ::tis-tasks)))

(defn tis-tasks [config]
  (->TisTasks config))