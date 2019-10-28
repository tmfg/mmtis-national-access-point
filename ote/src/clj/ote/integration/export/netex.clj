(ns ote.integration.export.netex
  "Exporting service for traffic data in NeTEx format"
  (:require [ote.util.feature :as feature]
            [amazonica.aws.s3 :as s3]
            [ote.netex.netex :as netex]
            [ring.util.io :as ring-io]
            [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [compojure.core :refer [GET]]
            [clojure.java.io :as io]
            [specql.core :as specql]
            [ote.db.common :as common]))

(defn file-download-url [{{base-url :base-url} :environment} transport-service-id file-id]
  (format "%sexport/netex/%d/%d" base-url transport-service-id file-id))

(defn fetch-netex-response
  "Return netex file as reponse.
  `origin` parameter is not mandatody. If it is missing, it is handled as api call."
  [db {:keys [bucket]} file-id service-id origin]
  (let [filename (:ote.db.netex/filename (netex/fetch-conversion db file-id))
        file (when (and bucket
                        (not (clojure.string/blank? filename)))
               (s3/get-object bucket filename))
        stats-type (if (or (nil? origin) (= "api" origin))
                     :download-route-netex-api
                     :download-route-netex-ui)]
    (do
      ;; Store download statistics to database
      (specql/insert! db ::common/stats-service
                      {::common/transport-service-id service-id
                       ::common/type stats-type
                       ::common/created (java.sql.Timestamp. (System/currentTimeMillis))})
      ;; Return file
      (if file
        {:status 200
         :headers {"Content-Type" "application/zip"
                   "Content-Disposition" (str "attachment; filename=" filename)}
         :body (ring-io/piped-input-stream (fn [out]
                                             (io/copy (:input-stream file) out)))}
        {:status 404
         :body "File not found"}))))

(defrecord NeTExExport [config]
  component/Lifecycle
  (start [{http :http
           db :db :as this}]
    (assoc this
      ::stop
      (when (feature/feature-enabled? config :netex-conversion-automated)
        (http/publish! http
                       {:authenticated? false}
                       (GET "/export/netex/:transport-service-id{[0-9]+}/:file-id{[0-9]+}"
                            {{:keys [file-id transport-service-id]} :params
                             {:strs [origin]} :query-params}
                         (fetch-netex-response db
                                               (:netex config)
                                               (Long/parseLong file-id)
                                               (Long/parseLong transport-service-id)
                                               origin))))))
  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
