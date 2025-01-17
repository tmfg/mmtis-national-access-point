(ns ote.tasks.gtfs
  "Scheduled tasks to update gtfs file to s3 and later to database."
  (:require [chime :refer [chime-at]]
            [clj-time.core :as t]
            [clj-time.periodic :refer [periodic-seq]]
            [com.stuartsierra.component :as component]
            [hiccup.core :as hiccup]
            [jeesql.core :refer [defqueries]]
            [ote.config.transit-changes-config :as config-tc]
            [ote.db.lock :as lock]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]
            [ote.db.tx :as tx]
            [ote.email :as email]
            [ote.integration.import.gtfs :as import-gtfs]
            [ote.integration.report :as report]
            [ote.localization :as localization]
            [ote.tasks.util :as tasks-util]
            [ote.time :as time]
            [ote.transit-changes.detection :as detection]
            [ote.util.db :as util-db]
            [ote.util.email-template :as email-template]
            [ote.util.feature :as feature]
            [specql.core :as specql]
            [taoensso.timbre :as log])
  (:import (org.joda.time DateTime DateTimeZone)
           (org.postgresql.util PSQLException)))

(defqueries "ote/tasks/gtfs.sql")
(defqueries "ote/services/transit_changes.sql")

(declare services-for-nightly-change-detection select-gtfs-urls-update select-gtfs-url-for-service
         select-gtfs-url-for-interface upcoming-changes valid-detected-route-changes)

(def daily-update-time (t/from-time-zone (t/today-at 0 5)
                                         (DateTimeZone/forID "Europe/Helsinki")))

(defn interface-type [format]
  (case format
    "GTFS" :gtfs
    "Kalkati" :kalkati
    "Kalkati.net" :kalkati  ; kept for legacy support
    nil))

(defn mark-gtfs-package-imported! [db interface]
  (specql/update! db ::t-service/external-interface-description
                  {::t-service/gtfs-imported (java.sql.Timestamp. (System/currentTimeMillis))}
                  {::t-service/id (:id interface)})) ;; external-interface-description.id, not service id.

(defn- get-blacklisted-operators [config]
  {:blacklist (if (empty? (:no-gtfs-update-for-operators config))
                #{-1}         ;; this is needed for postgres NOT IN conditional
                (:no-gtfs-update-for-operators config))})

(defn fetch-next-gtfs-interface! [db config]
  (tx/with-transaction
    db
    (let [blacklisted-operators (get-blacklisted-operators config)
          interface (first (select-gtfs-urls-update db blacklisted-operators))]
      (when interface
        (mark-gtfs-package-imported! db interface))
      interface)))

(defn fetch-given-gtfs-interface!
  "Get gtfs package data from database for given service."
  [db service-id interface-id]
  (let [interface (if (nil? interface-id)
                    (first (select-gtfs-url-for-service db {:service-id service-id}))
                    (first (select-gtfs-url-for-interface db {:service-id service-id
                                                              :interface-id interface-id})))]
    (when interface
      (mark-gtfs-package-imported! db interface))
    interface))

(defn email-validation-results
  [db testing-env? email service-id interface-id]
  (let [report (->> (report/latest-import-reports-for-service-interface db service-id interface-id)
                    (filter (fn [r] (= "error" (get-in r [:gtfs-import-report :severity])))))]
    (if (empty? report)
      (log/info (str "Empty report for service/interface " service-id "/" interface-id ", skipping email"))
      (let [service (some-> (specql/fetch db ::t-service/transport-service
                                          #{::t-service/contact-email
                                            ::t-service/transport-operator-id}
                                          {::t-service/id service-id})
                            first)
            operator (some-> (specql/fetch db ::t-operator/transport-operator
                                           #{::t-operator/id ::t-operator/email}
                                           {::t-operator/id (::t-service/transport-operator-id service)})
                             first)
            recipient (or (::t-service/contact-email service)
                          (::t-operator/email operator)
                          "nap@fintraffic.fi")]
        (when-not testing-env?
          (localization/with-language
            "fi"
            (email/send! email {:to      recipient
                                :subject (localization/tr [:email-templates :validation-report :title])
                                :body    [{:type    "text/html;charset=utf-8"
                                           :content (str email-template/html-header
                                                         (hiccup/html (email-template/validation-report
                                                                        [:email-templates :validation-report :title]
                                                                        operator
                                                                        service
                                                                        report)))}]})))

        (log/info (str "Sent " (if testing-env? "(simulated) " "") "email to "
                       recipient
                       " containing "
                       (count report)
                       " rows for operator-id/service-id/interface-id "
                       (::t-operator/id operator)
                       "/"
                       service-id
                       "/"
                       interface-id))))))

(defn- do-update-one-gtfs!
  "Core process logic extracted to its own function so that we can perform additional actions independently of whether
  this works or not."
  [config db interface upload-s3? force-download? used-service-id]
  (if interface
    (try
      (if-let [conversion-meta (import-gtfs/download-and-store-transit-package
                                 (interface-type (:format interface))
                                 (:gtfs config)
                                 db
                                 interface
                                 upload-s3?
                                 force-download?)]
        nil  ; SUCCESS. Explicit nil to make success branch more obvious
        (log/spy :warn "GTFS: Could not import GTFS file. service-id = " (:ts-id interface)))

      (catch Exception e
        (log/spy :warn "GTFS: Error importing, uploading or saving gtfs package to db! Exception=" e)))
    (log/spy :warn "GTFS: No gtfs files to upload. service-id = " used-service-id)))

(defn clean-old-entries!
  "Removes metadata from previous runs which are not needed anymore."
  [db service-id interface-id]
  (try
    (report/clean-old-reports! db service-id interface-id)
    (catch PSQLException pe
      (case (.getSQLState pe)
        "02000" nil  ; No results were returned by the query. -- ignored
        (log/warn pe "Failed to clean old reports")))
    (catch Exception e
      (log/warn e "Failed to clean old reports"))))

;; Return value could be reafactored to something else,
;; returned string used only for manually triggered operation result
(defn update-one-gtfs!
  "Selects the given service id, or if none given then selects the next service with external interface with new
  content, downloads and stores the content.
  Return: on success nil, on failure a string containing error details."
  ([config db email upload-s3?]
   (update-one-gtfs! config db email upload-s3? nil nil))
  ([config db email upload-s3? service-id interface-id]
   ;; Ensure that gtfs-import flag is enabled
   ;; upload-s3? should be false when using local environment
   (let [;; Load next gtfs package or package that is related to given service-id
         interface (if service-id
                     (fetch-given-gtfs-interface! db service-id interface-id)
                     (fetch-next-gtfs-interface! db config))
         interface (if (contains? interface :data-content)  ; Avoid creating a coll with empty key when coll doesn't exist
                     (update interface :data-content  util-db/PgArray->vec)
                     interface)
         force-download? (integer? service-id)
         ; ensure ids have values for sending emails
         service-id (or service-id (:ts-id interface))
         interface-id (or interface-id (:id interface))
         process-result (do-update-one-gtfs! config db interface upload-s3? force-download? service-id)]
     (if (and (some? email)
              (some? service-id))
       (email-validation-results db (:testing-env? config) email service-id interface-id)
       (log/warn (str "Could not send email due to internal state mismatch! (" email "/" service-id "/" interface-id ")")))
     (clean-old-entries! db service-id interface-id)
     process-result)))

(def night-hours #{0 1 2 3 4})

(defn night-time? [dt]
  (-> dt (t/to-time-zone tasks-util/timezone) time/date-fields ::time/hours night-hours boolean))

;; To run change detection for service(s) from REPL, call this with vector of service-ids: `(detect-new-changes-task (:db ote.main/ote) (time/now) true [1289])`
#_ (defn detect-new-changes-task
  ([config db ^DateTime detection-date force?]
   (detect-new-changes-task config db detection-date force? nil))
  ([config db ^DateTime detection-date force? service-ids]
   (let [lock-time-in-seconds (if force?
                                1
                                1800)
         ;; Today is the default but detection may be run "in the past" if admin wants to
         ;; detection-date is org joda datetime
         ;; Historic detections must use even deleted interfaces, so we need to identify if the detection date is in the past
         detection-date-in-the-past? (.isBefore (.toLocalDate detection-date) (.toLocalDate (time/now)))

         ;; Start from the beginning of last week
         start-date (time/days-from (time/beginning-of-week detection-date) -7)
         ;; Date in future up to where traffic should be analysed
         end-date (time/days-from start-date  (:detection-window-days (config-tc/config)))

         ;; Convert to LocalDate instances
         [start-date end-date today query-detection-date] (map (comp time/date-fields->date time/date-fields)
                                                               [start-date end-date detection-date detection-date])]
     (lock/try-with-lock
       db "gtfs-nightly-changes" lock-time-in-seconds
       (let [;; run detection only for given services or all
             service-ids (if service-ids
                           service-ids
                           (mapv :id (services-for-nightly-change-detection db
                                                                            (merge (get-blacklisted-operators config)
                                                                                   {:force force?}))))
             service-count (count service-ids)]
         (log/info "Detect transit changes for " (count service-ids) " services.")
         (dotimes [i (count service-ids)]
           (let [service-id (nth service-ids i)]
             (log/info "Detecting next transit changes for service (" (inc i) " / " service-count " ): " service-id)
             (try
               (let [query-params {:service-id service-id
                                   :start-date start-date
                                   :end-date end-date}
                     _ (log/info "Detecting :: query-params" (pr-str query-params))
                     packages-for-detection (detection/service-package-ids-for-date-range db query-params detection-date-in-the-past? query-detection-date)
                     _ (log/info "Detecting :: packages-for-detection " (pr-str packages-for-detection))]
                 (detection/update-transit-changes!
                   db detection-date service-id
                   packages-for-detection
                   (detection/detect-route-changes-for-service db query-params (tasks-util/joda-datetime-to-java-time-local-date detection-date))))
               (catch Exception e
                 (log/warn e "Change detection failed for service " service-id)))
             (log/info "Detection completed for service: " service-id))))))))

#_ (defn recalculate-detected-changes-count
  "Change detection (detect-new-changes-task) stores detected changes. These changes will expire after some period of time.
  All changes occur on a certain day and they will get old. Transit-changes page shows how many changes there are and
  if we don't recalculate them every night expired changes will be show also. So in this fn we recalculate change counts again using
  different-week-date value and skip all expired changes."
  [db]
  (let [upcoming-changes (upcoming-changes db)
        _ (log/info "Detected change count recalculation started!")]
    ;; Loop upcoming-changes
    (dotimes [i (count upcoming-changes)]
      (let [change-row (nth upcoming-changes i)
            changes (map #(assoc % :change-type (keyword (:change-type %)))
                         (valid-detected-route-changes db
                                                       {:date (:date change-row)
                                                        :service-id (:transport-service-id change-row)}))
            grouped-changes (group-by :change-type changes)]

        (log/info (inc i) "/" (count upcoming-changes) " Recalculating detected change amounts (by change type) for service " (:transport-service-id change-row) " detection date " (:date change-row))
        ;; Update sinle transit-change (change-row)
        (when (not (nil? (:date change-row)))
          (specql/update! db :gtfs/transit-changes
                          {:gtfs/current-removed-routes (count (group-by :route-hash-id (:removed grouped-changes)))
                           :gtfs/current-added-routes (count (group-by :route-hash-id (:added grouped-changes)))
                           :gtfs/current-changed-routes (count (group-by :route-hash-id (:changed grouped-changes)))
                           :gtfs/current-no-traffic-routes (count (group-by :route-hash-id (:no-traffic grouped-changes)))}
                          {:gtfs/date (:date change-row)
                           :gtfs/transport-service-id (:transport-service-id change-row)}))))
    (log/info "Detected change count recalculation ready!")))

(defrecord GtfsTasks [at config]
  component/Lifecycle
  (start [{db :db email :email :as this}]
    (assoc this
      ::stop-tasks
      (if (feature/feature-enabled? config :gtfs-import)
        [(chime-at
           (filter night-time?
                   (drop 1 (periodic-seq (t/now) (t/minutes 1))))
           (fn [_]
             (#'update-one-gtfs! config db email true)))
         ;; Change detection has been disabled.
         #_ (chime-at (tasks-util/daily-at 5 15)
                   (fn [_]
                     (detect-new-changes-task config db (time/now) false)))
         #_ (chime-at (tasks-util/daily-at 0 15)
                   (fn [_]
                     (recalculate-detected-changes-count db)))]
        (do
          (log/debug "GTFS IMPORT IS NOT ENABLED!")
          nil))))
  (stop [{stop-tasks ::stop-tasks :as this}]
    (for [stop-task stop-tasks]
      (stop-task))
    (dissoc this ::stop-tasks)))

(defn gtfs-tasks
  ([config] (gtfs-tasks daily-update-time config))
  ([at config]
   (->GtfsTasks at config)))
