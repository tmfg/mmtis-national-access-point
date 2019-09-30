(ns ote.netex.netex
  "Scheduled tasks to update gtfs file to s3 and later to database."
  (:require
    [taoensso.timbre :as log]
    [clojure.java.io :as io]
    [clojure.test :refer [is]]
    [cheshire.core :as cheshire]
    [ote.db.netex :as netex]
    [specql.core :as specql]))

(defn set-conversion-status
  "Resolves operation result based on input args and updates status to db.
  Return: True on success, false on failure"
  [filepath db {:keys [service-id external-interface-description-id]}]
  (log/debug "set-netex-conversion-status: service-id = " service-id ", path = " filepath
             ", external-interface-description-id = " external-interface-description-id)
  (let [result (if (clojure.string/blank? filepath)
                 :error
                 :ok)]
    (specql/upsert! db ::netex/netex-conversion
                    #{::netex/transport-service-id ::netex/external-interface-description-id}
                    {
                     ::netex/transport-service-id service-id
                     ::netex/external-interface-description-id external-interface-description-id
                     ::netex/url (or filepath "")
                     ::netex/modified (ote.time/sql-date (java.time.LocalDate/now)) ; TODO: db created and modfied use different timezone like this
                     ::netex/status result})
    (= :ok result)))

(defn delete-files-recursively! [f1]
  (when (.isDirectory (io/file f1))
    (doseq [f2 (.listFiles (io/file f1))]
      (delete-files-recursively! f2)))
  (io/delete-file f1 true))

(defn cleanup-dir-recursive!
  ;; TODO: replace using fileutils interop etc
  ;; TODO; first create an in-mem file object and verity it's path instead of the path string arg
  "Takes an path argument and does a simple sanity check before removing it and child directories recursively"
  [path]
  (when (< 1 (count path))
    (delete-files-recursively! path)))

(defn- compose-chouette-import-gtfs-json [operator-name]
  (cheshire/generate-string
    {:gtfs-import
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
  (cheshire/generate-string
    {:netex-export
     {:user_name "username-1"
      :name "job 1"
      :organisation_name operator-name
      :referential_name "referential-name-1"
      :add_metadata true
      :projection_type "4326"                               ; 4326 is WSG86 projection for chouette
      :add_extension true}}
    {:pretty true}))

(defn- upload-s3 [filepath]
  (log/debug "upload-s3: path = " filepath)
  (when-let [s3-path nil]))

(defn- convert! [chouette-import-config-filename chouette-export-config-filename gtfs-filepath]
  (let [netex-filepath nil]
    netex-filepath))

(defn- gtfs->netex!
  "Return: String defining filesystem path to output file, or nil on failure"
  [{:keys [conversion-work-path]} {:keys [gtfs-file gtfs-filename]}]
  {:pre [(is (and (< 1 (count conversion-work-path))
                  (not (clojure.string/blank? conversion-work-path))))
         (is (not (clojure.string/blank? gtfs-filename)))
         (is (seq gtfs-file))]}                             ;`is` used to print the value of a failed precondition
  (log/debug "gtfs->netex!: path = " conversion-work-path)
  (let [chouette-import-config-filename (str conversion-work-path "importGtfs.json")
        chouette-export-config-filename (str conversion-work-path "exportNetexjson")
        gtfs-filepath (str conversion-work-path gtfs-filename)]
    (cleanup-dir-recursive! conversion-work-path)
    (.mkdirs (java.io.File. conversion-work-path))
    (spit chouette-import-config-filename (compose-chouette-import-gtfs-json nil))
    (spit chouette-export-config-filename (compose-chouette-export-netex-json nil))
    (spit gtfs-filepath gtfs-file)
    gtfs-filepath

    (convert! chouette-import-config-filename chouette-export-config-filename gtfs-filepath)))

;(defn convert-to-netex-and-store
;  "Return: String defining s3 path to netex file, or nil on failure"
;  [config data]
;  (->
;    (gtfs->netex! config data)
;    (upload-s3)))

(defn gtfs->netex-and-set-status
  "Return: True on success, false on failure"
  [db config-netex gtfs-and-meta]
  (->
    (try
      (-> (gtfs->netex! config-netex gtfs-and-meta)
          (upload-s3))
      (catch Exception e
        (log/warn (str "GTFS: Conversion failed, service-id = " (:service-id gtfs-and-meta)
                       ", external-interface-description-id = " (:external-interface-description-id gtfs-and-meta
                                                                  )
                       ", file url = " (:filename gtfs-and-meta)
                       ", Exception = \n" (pr-str e)))
        (cleanup-dir-recursive! (:conversion-work-path config-netex))
        false))
    (set-conversion-status db gtfs-and-meta)))
