(ns ote.services.rdf
  "RDF service endpoints for DCAT-AP transport service data."
  (:require [amazonica.aws.s3 :as s3]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as log]
            [ote.components.http :as http]
            [ring.util.response :as response]
            [specql.core :as specql]
            [compojure.core :refer [routes GET]]
            [ote.db.transport-service :as t-service]
            [ote.services.rdf.data :as rdf-data]
            [ote.services.rdf.model :as rdf-model]
            [ote.services.rdf.serialization :as rdf-serialization]

            [ring.util.io :as ring-io]
            [clojure.java.io :as io])

  (:import [java.io File]))

(defn fetch-all-service-ids [db]
  (->> (specql/fetch db ::t-service/transport-service
                     #{:ote.db.transport-service/id}
                     {})
       (map :ote.db.transport-service/id)))

(defn service-id->rdf-model [config db service-id]
  (let [rdf-data (rdf-data/fetch-service-data db service-id)
        base-url (get-in config [:environment :base-url])]
    (rdf-model/service-data->rdf rdf-data base-url)))

(defn turtle-response [turtle-data]
  (-> (response/response turtle-data)
      (response/content-type "text/turtle; charset=UTF-8")
      (response/header "Content-Disposition" "attachment;")))

(defn create-rdf
  ([config db]
   (->> (fetch-all-service-ids db)
        (map (partial service-id->rdf-model config db))
        rdf-serialization/rdf-data->turtle))
  
  ([config db service-id]
   ;; Use data layer functions for fetching
   (let [service-id (if (string? service-id)
                      (Long/parseLong service-id)
                      service-id)]
     (->> service-id
          (service-id->rdf-model db config)
          rdf-serialization/rdf-data->turtle))))

(def dev-tmp-payload
  "An atom that - in dev - contains a tmp-file handle that we can spit into and slurp from the rdf-data that would be in s3 in a real world"
  (atom nil))

(defn find-rdf-payload [db config dev-mode?]
  (if dev-mode?
    (do
      (log/info "Finding rdf-payload in dev-mode")
      (when-not @dev-tmp-payload
        (log/info "Creating the tmp payload")
        (reset! dev-tmp-payload
                (let [tmp-file (File/createTempFile "napote" ".tmp")]
                  (.deleteOnExit tmp-file)
                  (spit tmp-file (create-rdf config db))
                  tmp-file)))

      ;; Stream the file instead of loading it all into memory
      (-> (ring-io/piped-input-stream
           (fn [out]
             (io/copy @dev-tmp-payload out)))
          turtle-response))
    
    ;; TODO how are response headers handled here? (turtle-response ...) ?
    (ring-io/piped-input-stream
     (fn [out]
       (io/copy (:input-stream (s3/get-object "finap-rdf-cache" "rdf")) out)))))

(defn- rds-routes [{:keys [dev-mode?] :as config} db]
  (routes
   (GET "/rdf" []
     (find-rdf-payload db config dev-mode?))
   
   (when dev-mode?
     ;; For testing smaller rdf payloads in dev-mode. Returns rdf for a single service
     (GET ["/rdf/:service-id", :service-id #".+"] {{service-id :service-id} :params}
       ;; create-rdf returns a complete response
       ;; and is probably a lot easier to redefine, as compojure's/ring's handlers are somewhat repl-hostile to redefine
       (create-rdf db config service-id)))))

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
