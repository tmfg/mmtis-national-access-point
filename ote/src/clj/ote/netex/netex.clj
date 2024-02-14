(ns ote.netex.netex
  "Thin wrapper around storing NeTEx conversion result metadata to FINAP database.

  This namespace used to host Chouette based local GTFS to NeTEx conversion functionality. In Q1/2024 this was replaced
  with Fintraffic's TIS VACO service, and majority of this namespace was deleted as result of code cleanup. What remains
  is old style metadata tracking which was left alone to avoid too invasive changes to the entire data model. If you are
  interested to dig more, explore the changes in the commit which adds this docstring as starting point."
  (:require
    [clojure.string]
    [ote.db.netex :as netex]
    [specql.core :as specql]
    [specql.op :as op]
    [taoensso.timbre :as log])
  (:import (java.util Date)))

(defn fetch-conversion [db file-id]
  (first
    (specql/fetch db
                  ::netex/netex-conversion
                  #{::netex/filename ::netex/id ::netex/data-content}
                  (op/and
                    {::netex/id file-id}
                    {::netex/status :ok}
                    {::netex/filename op/not-null?}))))

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
    (specql/upsert! db ::netex/netex-conversion
                    #{::netex/transport-service-id ::netex/external-interface-description-id}
                    {::netex/transport-service-id service-id
                     ::netex/external-interface-description-id external-interface-description-id
                     ::netex/filename (or s3-filename "")
                     ::netex/modified (Date.)
                     ::netex/status result
                     ::netex/data-content (set (mapv keyword external-interface-data-content))
                     ::netex/input-file-error input-report-file
                     ::netex/validation-file-error validation-report-file
                     ::netex/package_id package-id})
    (= :ok result)))


