(ns ote.integration.export.gtfs
  "GTFS export of routes"
  (:require [specql.core :refer [fetch]]
            [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [compojure.core :refer [GET]]
            [ote.db.transit :as transit]
            [specql.op :as op]
            [ote.time :as time]
            [ring.util.io :as ring-io]
            [ote.util.zip :refer [write-zip]]
            [ote.db.transport-operator :as t-operator]
            [ote.gtfs.transform :as gtfs-transform]
            [taoensso.timbre :as log]))

(declare export-gtfs)

(defrecord GTFSExport []
  component/Lifecycle
  (start [{http :http
           db :db :as this}]
    (assoc this
           ::stop (http/publish! http {:authenticated? false}
                                 (GET "/export/gtfs/:transport-operator-id{[0-9]+}"
                                      [transport-operator-id]
                                      (export-gtfs db (Long/parseLong transport-operator-id))))))
  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))

(defn- current-routes
  "Return the currently available published routes of the given transport operator."
  [db transport-operator-id]
  (let [now (java.util.Date.)]
    (fetch db ::transit/route
           #{::transit/id ::transit/name ::transit/stops ::transit/trips
             ::transit/route-type ::transit/service-calendars}
           {::transit/transport-operator-id transport-operator-id
            ::transit/available-from (op/or op/null?
                                            (op/< now))
            ::transit/available-to (op/or op/null?
                                          (op/> now))})))

(defn routes-gtfs-zip
  [transport-operator routes output-stream]
  (try
    (write-zip (gtfs-transform/routes-gtfs transport-operator routes)
               output-stream)
    (catch Exception e
      (log/warn "Exception while generating GTFS zip" e))))

(def ^:private transport-operator-columns #{::t-operator/id ::t-operator/name
                                            ::t-operator/homepage ::t-operator/phone
                                            ::t-operator/email})

(defn export-gtfs [db transport-operator-id]
  (let [transport-operator (first (fetch db ::t-operator/transport-operator
                                         transport-operator-columns
                                         {::t-operator/id transport-operator-id}))
        routes (current-routes db transport-operator-id)]
    {:status 200
     :headers {"Content-Type" "application/zip"
               "Content-Disposition" "attachment; filename=gtfs.zip"}
     :body (ring-io/piped-input-stream
            (partial routes-gtfs-zip transport-operator routes))}))
