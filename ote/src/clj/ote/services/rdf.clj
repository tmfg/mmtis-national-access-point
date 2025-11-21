(ns ote.services.rdf
  "RDF service endpoints for DCAT-AP transport service data."
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :as log]
            [ote.components.http :as http]
            [ring.util.response :as response]
            [specql.core :as specql]
            [compojure.core :refer [routes GET]]
            [ote.db.transport-service :as t-service]
            [ote.services.rdf.data :as rdf-data]
            [ote.services.rdf.model :as rdf-model]
            [ote.services.rdf.serialization :as rdf-serialization]))

(defn fetch-all-service-ids [db]
  (->> (specql/fetch db ::t-service/transport-service
                     #{:ote.db.transport-service/id}
                     {})
       (map :ote.db.transport-service/id)))

(defn service-id->rdf-model [db service-id]
  (let [rdf-data (rdf-data/fetch-service-data db service-id)]
    (rdf-model/service-data->rdf rdf-data)))

(defn turtle-response [turtle-data]
  (-> (response/response turtle-data)
      (response/content-type "text/turtle; charset=UTF-8")
      (response/header "Content-Disposition" "attachment;")))

(defn create-rdf
  ([db]
   (->> (fetch-all-service-ids db)
        (map (partial service-id->rdf-model db))
        rdf-serialization/rdf-data->turtle
        turtle-response))
  
  ([db service-id]
   ;; Use data layer functions for fetching
   (let [service-id (if (string? service-id)
                      (Long/parseLong service-id)
                      service-id)]
     (->> service-id
          (service-id->rdf-model db)
          rdf-serialization/rdf-data->turtle
          turtle-response))))

(defn- rds-routes [config db]
  (routes
   (GET "/rdf" {:as req}
     (create-rdf db))
   (GET ["/rdf/:service-id", :service-id #".+"] {{service-id :service-id} :params :as req}
     ;; create-rdf returns a complete response
     ;; and is probably a lot easier to redefine, as compojure's/ring's handlers are somewhat repl-hostile to redefine
     (create-rdf db service-id))))

(defrecord RDS [config]
  component/Lifecycle
  (start [{db :db
           http :http
           :as this}]
    (assoc this ::stop
                (http/publish! http {:authenticated? false}
                               (rds-routes config db))))

  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
