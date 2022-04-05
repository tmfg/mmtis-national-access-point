(ns ote.netex.netex
  "Scheduled tasks to update gtfs file to s3 and later to database."
  (:require
    [taoensso.timbre :as log]
    [clojure.java.io :as io]
    [cheshire.core :as cheshire]
    [ote.environment :as env]
    [specql.op :as op]
    [specql.core :as specql]
    [clojure.java.shell :refer [sh with-sh-dir]]
    [clojure.string :as str]
    [amazonica.aws.s3 :as s3]
    [ote.config.netex-config :as config-nt-static]
    [ote.db.netex :as netex]
    [ote.gtfs.spec :as gtfs-spec]
    [ote.gtfs.parse :as gtfs-parse]
    [ote.util.zip :refer [write-zip read-zip-with]]
    [ote.util.zip :as zip-file]
    [ote.util.file :as file]
    [clojure.zip :as zip]
    [clojure.data.zip.xml :as z]
    [clojure.xml :as xml]
    [ote.gtfs.kalkati-to-gtfs :as kalkati-to-gtfs]
    [ote.integration.report :as report])
  (:import (java.util TimeZone)))

(def ^:private netex-validation-phases
  "Sourced from https://github.com/entur/chouette-projects-i18n/blob/master/locales/en.yml"
  {"1-GTFS-CSV-1" "Valid .zip Archive and files"
   "1-GTFS-CSV-2" "Valid UTF-8 CSV header line"
   "1-GTFS-CSV-3" "Presence of header"
   "1-GTFS-CSV-4" "Valid header definition"
   "1-GTFS-CSV-5" "Valid UTF-8 CSV data line"
   "1-GTFS-CSV-6" "No HTML tags"
   "1-GTFS-CSV-7" "No header/trailer spaces"
   "1-GTFS-Calendar-1" "At least one weekday"
   "1-GTFS-Calendar-2" "end_date after start_data"
   "1-GTFS-Common-1"  "Required files are present"
   "1-GTFS-Common-10" "Presence of agency_id"
   "1-GTFS-Common-11" "Unknown columns"
   "1-GTFS-Common-12" "Data in Required columns"
   "1-GTFS-Common-13" "Data in agency_id in case of several agencies"
   "1-GTFS-Common-14" "Data in agency_id"
   "1-GTFS-Common-15" "Conditional values for arrival and departure times, transfer_times"
   "1-GTFS-Common-16" "Valid data types"
   "1-GTFS-Common-2" "Presence of calendar_dates and/or calendars"
   "1-GTFS-Common-3" "Check for optional files"
   "1-GTFS-Common-4" "Presence of other files"
   "1-GTFS-Common-5" "Presence of data in files"
   "1-GTFS-Common-6" "Presence of data in calendar files"
   "1-GTFS-Common-7" "Presence of data in optional files"
   "1-GTFS-Common-8" "Unique Identifiers"
   "1-GTFS-Common-9" "Presence of Required columns"
   "1-GTFS-Route-1" "Presence of column route_short_name or else route_long_name"
   "1-GTFS-Route-2" "Presence of data for route_short_name or else route_long_name"
   "2-GTFS-Common-1" "Existence of external references"
   "2-GTFS-Common-2" "Usage of IDs"
   "2-GTFS-Common-3" "Uniqueness of objects"
   "2-GTFS-Common-4" "No repeated field values"
   "2-GTFS-Route-1" "route_short_name and route_long_name shall be different"
   "2-GTFS-Route-2" "Non inclusion de route_short_name dans route_long_name"
   "2-GTFS-Route-3" "Contrast between colors"
   "2-GTFS-Route-4" "No 2 routes have identical route_short_name and route_long_name values, but reversed"
   "2-GTFS-Stop-1" "location_type of parent_station stops"
   "2-GTFS-Stop-2" "Contrôle de l'usage de la colonne location_type"
   "2-GTFS-Stop-3" "stop_name et stop_desc non confondus"
   "2-GTFS-Stop-4" "Station without stations"})

(defn expand-netex-validation-report
  [json]
  (log/warn (str "Analysing " json))
  (->> (get-in (cheshire/parse-string json true) [:validation_report :errors])
       (map #(update % :test_id netex-validation-phases))))

(def compose-default-locale
  (let [country-code "fi"
        new-date (new java.util.Date)
        summer-date (new java.util.Date)
        _ (.setMonth summer-date 6)
        timezone (TimeZone/getTimeZone "Europe/Helsinki")
        current-offset (/ (.getOffset timezone (.getTime new-date)) 1000 60 60)
        summer-offset (/ (.getOffset timezone (.getTime summer-date)) 1000 60 60)]
    {:country-code country-code
     :current-offset current-offset
     :summer-offset summer-offset}))

(defn post-process-default-locale!
  "Change language-code fr to fi. Timezone offset to europe/helsinki (-2) and summer time timezone offset to europe/helsinki (-3)"
  [netext-xml-file]
  (let [_ (log/debug "processing - " (:name netext-xml-file))
        default-locale compose-default-locale
        zipper-file (kalkati-to-gtfs/kalkati-zipper (java.io.ByteArrayInputStream. (.getBytes (:data netext-xml-file) "UTF-8")))
        zipper-file (as-> (z/xml1-> zipper-file
                                    :PublicationDelivery :dataObjects :CompositeFrame :FrameDefaults :DefaultLocale :SummerTimeZoneOffset
                                    #(zip/edit % assoc :content
                                               (vector (str (:summer-offset default-locale)))) zip/root) zipper-file
                          (kalkati-to-gtfs/kalkati-zipper
                            (java.io.ByteArrayInputStream. (.getBytes (with-out-str (xml/emit zipper-file)) "UTF-8")))
                          (z/xml1-> zipper-file :PublicationDelivery :dataObjects :CompositeFrame :FrameDefaults :DefaultLocale :DefaultLanguage
                                    (fn [val]
                                      (zip/edit val assoc :content
                                                (vector (:country-code default-locale)))) zip/root)
                          (kalkati-to-gtfs/kalkati-zipper
                            (java.io.ByteArrayInputStream. (.getBytes (with-out-str (xml/emit zipper-file)) "UTF-8")))
                          (z/xml1-> zipper-file :PublicationDelivery :dataObjects :CompositeFrame :FrameDefaults :DefaultLocale :TimeZoneOffset
                                    (fn [val]
                                      (zip/edit val assoc :content
                                                (vector (str (:current-offset default-locale))))) zip/root))
        output-str (->
                     (with-out-str (xml/emit zipper-file))
                     (str/replace "\n\n\n" "\n")
                     (str/replace "\n\n" "\n")
                     (str/replace ">\n" ">")
                     (str/replace "\n<" "<")
                     (str/replace "><" ">\n<"))]
    output-str))

(defn- post-process-netex
  "Current chouette command line tool has some issues. It uses only fr as locale, sets timezone to france and adds too much log lines."
  [netex-filepath]
  (try
    (let [_ (log/debug "POST PROCESS - netex.zip")
          netex-zip (io/input-stream netex-filepath)
          netex-files (zip-file/read-zip netex-zip)
          processed-files (mapv
                            (fn [f]
                              (if (re-matches #"^(\d+).xml$" (:name f)) ; e.g. "1.xml" or "111.xml"
                                {:name (:name f)
                                 :data (post-process-default-locale! f)}
                                f))
                            netex-files)
          out (java.io.ByteArrayOutputStream.)]
      (write-zip processed-files out)
      (io/copy
        (.toByteArray out)
        (io/file netex-filepath)))
    (catch Exception e
      (log/error "POST PROCESS ERROR " (pr-str e)))))

(defn- stop->station
  "Netex doesn't add coordinates to the xml files if station location_type = 0. So convert location_type = 0 (stop) to 1 (station)."
  [stops]
  (map
    (fn [val]
      (assoc val :gtfs/location-type 1))
    stops))

(defn- pre-process-gtfsfile
  "Change stoptxt location_type to 1 and remove or add one empty row after all data rows."
  [zipped-gtfs-file]
  (let [csvfilemap (zip-file/read-zip (java.io.ByteArrayInputStream. zipped-gtfs-file))
        out (java.io.ByteArrayOutputStream.)]
    (try
      ;; Write zip
      (write-zip (mapv (fn [{:keys [name data]}]
                         (let [gtfs-file-type (gtfs-spec/name->keyword name)
                               parsed-data (gtfs-parse/parse-gtfs-file gtfs-file-type data)
                               parsed-data (filter #(not (empty? %)) parsed-data)
                               parsed-data (if (= "stops.txt" name)
                                             (stop->station parsed-data)
                                             parsed-data)
                               unparsed-data (gtfs-parse/unparse-gtfs-file gtfs-file-type parsed-data)]
                           {:name name
                            :data unparsed-data}))
                       csvfilemap)
                 out)
      ;; Stream it out
      (.toByteArray out)
      (catch Exception e
        (log/error "ERROR - preprosessing GTFS file: " (pr-str e))))))

(defn fetch-conversion [db file-id]
  (first
    (specql/fetch db
                  ::netex/netex-conversion
                  #{::netex/filename ::netex/id ::netex/data-content}
                  (op/and
                    {::netex/id file-id}
                    {::netex/status :ok}
                    {::netex/filename op/not-null?}))))

(defn- path-allowed?
  "Checks if path is in a system directory or similar not allowed place. Returns true if allowed, false if not."
  [^String path]
  (when-let [path-resolved (.getCanonicalPath (clojure.java.io/file path))]
    (or (str/starts-with? path-resolved "/tmp/")
        (str/starts-with? path-resolved "/private/tmp/")))) ; OSX resolves path under /private/

(defn delete-files-recursively! [f1]
  (when (.isDirectory (io/file f1))
    (doseq [f2 (.listFiles (io/file f1))]
      (delete-files-recursively! f2)))
  (io/delete-file f1 true))

(defn cleanup-dir-recursive!
  ;; TODO: replace using fileutils interop etc?
  "Takes an path argument and does a simple sanity check before removing it and child directories recursively"
  [path]
  (if (path-allowed? path)
    (delete-files-recursively! path)
    (log/warn "Directory cleanup skipped, bad path = " path)))

(defn- compose-chouette-import-gtfs-json [operator-name]
  (cheshire/generate-string {:gtfs-import
                             {:user_name "username-1"
                              :name "job 1"
                              :organisation_name operator-name
                              :referential_name "referential-name-1"
                              :object_id_prefix "GTFS"
                              :max_distance_for_connection_link 0
                              :max_distance_for_commercial 0
                              :ignore_end_chars 0
                              :ignoreLastWord false}}
                            {:pretty true}))

(defn- compose-chouette-export-netex-json [operator-name]
  (cheshire/generate-string {:netex-export
                             {:user_name "username-1"
                              :name "job 1"
                              :organisation_name operator-name
                              :referential_name "referential-name-1"
                              :add_metadata true
                              :projection_type "4326"       ; 4326 is WSG86 projection for chouette
                              :add_extension true}}
                            {:pretty true}))

(defn- chouette-report-ok? [chouette-report-filepath]
  (if (and (.exists (io/file chouette-report-filepath))
           (.isFile (io/file chouette-report-filepath)))
    (let [action_report (:action_report (cheshire/parse-string (slurp (str chouette-report-filepath)) keyword))
          result (:result action_report)
          error-files (->> action_report
                           :files
                           (filter #(= "NOK" (:status %)))
                           (mapv :name))]

      (if (and (= "OK" result) (empty? error-files))
        true
        (do (log/warn "NeTEx conversion chouette report NOK: result = " result
                      ", GTFS error files = '" error-files "'"
                      ", chouette-report-filepath = " chouette-report-filepath)
            false)))
    (do
      (log/warn "NeTEx conversion chouette report missing. filepath = " chouette-report-filepath)
      false)))

(defn- chouette-output-valid?
  "Takes chouette process exit info and output path and evaluates if conversion was a success or failure.
  Return: On success string defining filesystem path to output file, on failure nil"
  [{:keys [exit err] :as ex-info}                           ; Conversion command exit info
   {:keys [conversion-work-path]}                           ; Ote netex config
   {:keys [work-dir input-report-file output-report-file validation-report-file]}  ; Ote chouette config
   output-filepath
   chouette-cmd]
  (let [input-json (cheshire/generate-string (cheshire/parse-string (slurp (str conversion-work-path work-dir input-report-file)) keyword))
        validation-json (cheshire/generate-string (cheshire/parse-string (slurp (str conversion-work-path work-dir validation-report-file)) keyword))]

    (if (and (= 0 exit)
             ;(str/blank? err) Let conversion return something
             (chouette-report-ok? (str conversion-work-path work-dir input-report-file))
             (chouette-report-ok? (str conversion-work-path work-dir output-report-file))
             (.exists (io/file output-filepath)))
      (do
        (post-process-netex output-filepath)
        {:output-filepath output-filepath
         :input-report-file nil
         :validation-report-file nil})
      (do
        ;; Store input and validation files to db
        (log/warn "Netex conversion chouette error, command exit info = " ex-info ", tried = " chouette-cmd)
        {:output-filepath nil
         :input-report-file input-json
         :validation-report-file validation-json}))))

(defn gtfs->netex!
  "Return: On success string defining filesystem path to output netex archive, on failure nil"
  [{:keys [gtfs-file gtfs-filename gtfs-basename operator-name external-interface-description-id]}
   {:keys [chouette-path conversion-work-path] :as config-netex}]
  {:pre [(and (< 1 (count conversion-work-path))
              (not (clojure.string/blank? conversion-work-path)))
         (not (clojure.string/blank? gtfs-filename))
         (seq gtfs-file)]}
  (let [netex-config-static (config-nt-static/config)
        import-config-filepath (str conversion-work-path (get-in netex-config-static [:chouette :input-config-file]))
        export-config-filepath (str conversion-work-path (get-in netex-config-static [:chouette :export-config-file]))
        gtfs-filepath (str conversion-work-path gtfs-filename)
        gtfs-name-suffix "_gtfs"
        netex-filepath (str conversion-work-path
                            (if (str/ends-with? gtfs-basename gtfs-name-suffix)
                              (subs gtfs-basename 0 (- (count gtfs-basename) (count gtfs-name-suffix)))
                              gtfs-basename)
                            "_"
                            external-interface-description-id
                            "_netex.zip")
        netex-script (if (or (nil? (env/base-url))          ;; ci environment
                             (clojure.string/includes? (env/base-url) "localhost")) ;; localhost
                       "./chouette.sh"
                       "./ns-chouette.sh")
        chouette-cmd [netex-script                          ; Vector used to allow logging shell invocation on error
                      "-i " import-config-filepath
                      "-o " export-config-filepath
                      "-f " netex-filepath
                      ;; Set chouette's internal work dir under ote work dir so it gets deleted as part of task cleanup
                      "-d " (str conversion-work-path (get-in netex-config-static [:chouette :work-dir]))
                      gtfs-filepath]]

    (if (and (path-allowed? gtfs-filepath)
             (path-allowed? conversion-work-path))
      (do
        ;; Setup input files for chouette command line tool conversion call.
        ;; No return condition checks because calls throw and exception on failure
        ;(cleanup-dir-recursive! conversion-work-path)
        (.mkdirs (clojure.java.io/file conversion-work-path)) ; Returns true when created, false otherwise
        (spit import-config-filepath (compose-chouette-import-gtfs-json operator-name)) ; Returns nil on success or throws an exception
        (spit export-config-filepath (compose-chouette-export-netex-json operator-name))
        (io/copy                                            ; Returns nil on success or throws an exception
          (java.io.ByteArrayInputStream. gtfs-file)
          (io/file gtfs-filepath))
        ;; Do GTFS to NeTEx conversion, previous lines
        (log/info (str "GTFS->NeTEx invokes: " chouette-cmd))
        (->
          (with-sh-dir chouette-path
                       (apply sh chouette-cmd))
          (chouette-output-valid? config-netex
                                  (:chouette (config-nt-static/config))
                                  netex-filepath
                                  chouette-cmd)))
      (do
        (log/error (str "Bad path argument(s) " config-netex))
        nil))))

(defn- upload-s3
  "Takes path to file and map with bucket name and puts file into bucket.
  Returns: On success true,
  on failure amazonica throws an exception, on other failures like missing argument this returns false"
  [filepath {:keys [bucket]}]
  (when (and bucket filepath)
    (let [filename (.getName (io/file filepath))]
      (log/debug "Putting to S3 bucket = " bucket ", file = " filepath ", filename = " filename)
      ;; No need to set content-length?
      ;; Access denied or nonexistent bucket will throw an exception
      (s3/put-object bucket filename (io/file filepath))
      filename)))

(defmulti netex-validation-error :error_id)

(defmethod netex-validation-error "1_gtfs_csv_3"
  [error]
  (format "%s is missing the header row" (:error_value error)))

(defmethod netex-validation-error "1_gtfs_common_3"
  [error]
  (format "Optional file %s is not present." (:error_value error)))

(defmethod netex-validation-error "1_gtfs_common_4"
  [error]
  (format "Unexpected file %s present in GTFS package" (:error_value error)))

(defmethod netex-validation-error "1_gtfs_common_8"
  [error]
  (let [{:keys [source error_value reference_value]}   error
        {:keys [filename, line_number, column_number]} (:file source)]
    (format "%s contains the value %s for key %s multiple times, starting at line %s, column %s."
            filename error_value reference_value line_number column_number)))


(defmethod netex-validation-error "1_gtfs_common_11"
  [error]
  (let [{:keys [source error_value]} error
        {:keys [filename]}           (:file source)]
    (format "%s contains unknown column %s"
            filename error_value)))

(defmethod netex-validation-error "1_gtfs_common_12"
  [error]
  (let [{:keys [source error_value reference_value]}   error
        {:keys [filename, line_number, column_number]} (:file source)]
    (format "%s is missing required value for column %s on line %s, column %s"
            filename error_value line_number column_number)))

(defmethod netex-validation-error "2_gtfs_stop_4"
  [error]
  (let [{:keys [source error_value reference_value]}   error
        {:keys [filename, line_number, column_number]} (:file source)]
    (format "%s contains a station %s without stations at line %s, column %s"
            filename error_value line_number column_number)))

(defmethod netex-validation-error :default [x] (str x))

(defn set-conversion-status!
  "Resolves operation result based on input args and updates status to db.
  Return: On successful conversion true, on failure false"
  [{:keys [netex-filepath s3-filename input-report-file validation-report-file package-id]}
   db
   {:keys [service-id external-interface-description-id external-interface-data-content] :as conversion-meta}]
  (let [result (if (clojure.string/blank? netex-filepath)
                 :error
                 :ok)]
    (log/info (str "GTFS->NeTEx result to db: service-id = " service-id
                   " result = " result
                   ", s3-filename = " s3-filename
                   ", conversion-meta=" conversion-meta))
    (when (some? input-report-file)
      (report/gtfs-import-report! db "warning" package-id
                                  (str "NeTEx conversion input produced non-empty report")
                                  (.getBytes input-report-file)))
    (when (some? validation-report-file)
      (for [r (expand-netex-validation-report validation-report-file)]
        (report/gtfs-import-report! db "error" package-id
                                    (str "NeTEx conversion validation failed")
                                    (.getBytes (netex-validation-error r)))))
    (specql/upsert! db ::netex/netex-conversion
                    #{::netex/transport-service-id ::netex/external-interface-description-id}
                    {::netex/transport-service-id service-id
                     ::netex/external-interface-description-id external-interface-description-id
                     ::netex/filename (or s3-filename "")
                     ::netex/modified (java.util.Date.)
                     ::netex/status result
                     ::netex/data-content (set (mapv keyword external-interface-data-content))
                     ::netex/input-file-error input-report-file
                     ::netex/validation-file-error validation-report-file
                     ::netex/package_id package-id})
    (= :ok result)))

(defn gtfs->netex-and-set-status!
  "Deletes `conversion-work-path` and runs gtfs->netex conversion to gtfs file in `conversion-meta`
  Return: True on success, false on failure"
  [db {:keys [conversion-work-path] :as config-netex} conversion-meta]
  (cleanup-dir-recursive! conversion-work-path)
  ;; If s3 bucket is not defined conversion result is set to db as success in order to reflect conversion tool
  ;; invocation result. Even though created netex file is lost if s3 upload is skipped.
  (let [gtfs-file (pre-process-gtfsfile (:gtfs-file conversion-meta))
        conversion-meta (assoc conversion-meta :gtfs-file gtfs-file)
        conversion-result (gtfs->netex! conversion-meta config-netex)
        meta (try
               (as-> (assoc conversion-meta
                       :netex-filepath (:output-filepath conversion-result)
                       :input-report-file (:input-report-file conversion-result)
                       :validation-report-file (:validation-report-file conversion-result)) meta
                     (assoc meta
                       :s3-filename (upload-s3 (:netex-filepath meta) config-netex)))
               (catch Exception e
                 (log/warn (str "GTFS->NeTEx Conversion failed, service-id = " (:service-id conversion-meta)
                                ", external-interface-description-id = " (:external-interface-description-id conversion-meta
                                                                           )
                                ", filename = " (:filename conversion-meta)
                                ", Exception = \n" (pr-str e)))
                 conversion-meta))]
    (cleanup-dir-recursive! conversion-work-path)
    (set-conversion-status! meta db conversion-meta)))
