(ns ote.integration.export.gtfs
  "GTFS export of routes"
  (:require [specql.core :refer [fetch]]
            [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [compojure.core :refer [GET]]
            [ote.db.transit :as transit]
            [specql.op :as op]
            [ring.util.io :as ring-io]
            [taoensso.timbre :as log]
            [ote.components.http :as http]
            [ote.db.transit :as transit]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]
            [ote.time :as time]
            [ote.gtfs.transform :as gtfs-transform]
            [ote.util.zip :refer [write-zip]]
            [ote.util.fn :refer [flip]]
            [ote.util.transport-operator-util :as op-util]
            [ote.localization :refer [*language*]]
            [ote.services.routes :refer [fetch-sea-trips]]))

(declare export-sea-gtfs)

(defrecord GTFSExport []
  component/Lifecycle
  (start [{http :http
           db :db :as this}]
    (assoc this
           ::stop (http/publish! http {:authenticated? false}
                                 (GET "/export/gtfs/:transport-operator-id{[0-9]+}"
                                      [transport-operator-id]
                                      (export-sea-gtfs db (Long/parseLong transport-operator-id))))))
  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))

(defn- fetch-sea-routes-published
  "Return the currently available published sea routes of the given transport operator."
  [db transport-operator-id]
  (let [routes (fetch db ::transit/route
               #{::transit/route-id ::transit/name ::transit/stops
                 ::transit/route-type ::transit/service-calendars}
               {::transit/transport-operator-id transport-operator-id
                ::transit/published? true})]
    (mapv #(assoc %
            ::transit/trips
            (fetch-sea-trips db (::transit/route-id %)))
          routes)))

(defn sea-routes-gtfs-zip
  [transport-operator routes output-stream]
  (try
    (write-zip (gtfs-transform/sea-routes-gtfs transport-operator routes)
               output-stream)
    (catch Exception e
      (log/warn "Exception while generating GTFS zip" e))))

(def ^:private transport-operator-columns #{::t-operator/id ::t-operator/name
                                            ::t-operator/homepage ::t-operator/phone
                                            ::t-operator/email})

(defn export-sea-gtfs [db transport-operator-id]
  (let [transport-operator (first (fetch db ::t-operator/transport-operator
                                         transport-operator-columns
                                         {::t-operator/id transport-operator-id}))
        routes (map #(update % ::transit/name (flip t-service/localized-text-with-fallback) *language*)
                    (fetch-sea-routes-published db transport-operator-id))]
    {:status 200
     :headers {"Content-Type" "application/zip"
               "Content-Disposition" (str "attachment; filename=" (op-util/gtfs-file-name transport-operator))}
     :body (ring-io/piped-input-stream
            (partial sea-routes-gtfs-zip transport-operator routes))}))
