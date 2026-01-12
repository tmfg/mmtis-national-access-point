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

(defn merge-service-rdf-data
  "Merge multiple service RDF data structures into one.
   Concatenates datasets, catalog-records, and relationships.
   Keeps the first fintraffic-agent and ns-prefixes.
   Does NOT include catalog - that should be created separately after merging."
  [rdf-data-seq]
  (let [all-data (filter some? rdf-data-seq)]
    {:datasets (vec (mapcat :datasets all-data))
     :catalog-records (vec (mapcat :catalog-records all-data))
     :relationships (vec (mapcat :relationships all-data))
     :fintraffic-agent (:fintraffic-agent (first all-data))
     :operator-agents (vec (filter some? (map :operator-agent all-data)))
     :ns-prefixes (:ns-prefixes (first all-data))}))

(defn create-catalog-from-merged-data
  "Create a single catalog resource from merged service data."
  [merged-data base-url]
  (let [dataset-uris (map :uri (:datasets merged-data))
        catalog-records (:catalog-records merged-data)
        ;; Get latest publication date from catalog records
        latest-publication (->> catalog-records
                                (map #(get-in % [:properties :dct/modified :value]))
                                (filter some?)
                                (sort)
                                (last))
        fintraffic-uri "https://www.fintraffic.fi/en"]
    (rdf-model/domain->catalog catalog-records dataset-uris latest-publication fintraffic-uri base-url)))

(defn create-rdf
  ([config db output-stream]
   (let [base-url (get-in config [:environment :base-url])
         ;; Collect all service RDF data structures (without individual catalogs)
         service-rdf-data (->> (fetch-all-service-ids db)
                               (map (partial service-id->rdf-model config db))
                               (filter some?))
         ;; Merge at data structure level
         merged-data (merge-service-rdf-data service-rdf-data)
         ;; Create single catalog from merged data
         catalog (create-catalog-from-merged-data merged-data base-url)
         ;; Add catalog to merged data
         final-rdf-data (assoc merged-data :catalog catalog)]
     ;; Serialize the single merged RDF data structure
     (rdf-serialization/rdf-data->turtle output-stream final-rdf-data)))
  
  ([config db output-stream service-id]
   ;; Use data layer functions for fetching - single service also needs catalog
   (let [service-id (if (string? service-id)
                      (Long/parseLong service-id)
                      service-id)
         base-url (get-in config [:environment :base-url])
         service-rdf-data (service-id->rdf-model config db service-id)
         ;; Create catalog for this single service using helper function
         final-rdf-data (rdf-model/add-catalog-to-service-rdf service-rdf-data base-url)]
     (rdf-serialization/rdf-data->turtle output-stream final-rdf-data))))

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
                  (with-open [out (java.io.FileOutputStream. tmp-file)]
                    (create-rdf config db out))
                  tmp-file)))

      ;; Stream the file instead of loading it all into memory
      (-> (ring-io/piped-input-stream
           (fn [out]
             (io/copy (clojure.java.io/input-stream @dev-tmp-payload) out)))
          turtle-response))

    (-> (ring-io/piped-input-stream
         (fn [out]
           (let [{{:keys [bucket]} :rdf-export} config]
             (log/infof "Reading file \"rdf\" from bucket %s" (pr-str bucket))
             (io/copy (:input-stream (s3/get-object bucket "rdf")) out))))
        turtle-response)))

(defn- rds-routes [{:keys [dev-mode?] :as config} db]
  (routes
   (GET "/rdf" []
     (find-rdf-payload db config dev-mode?))
   
   #_(when dev-mode?
     ;; For testing smaller rdf payloads in dev-mode. Returns rdf for a single service
     (GET ["/rdf/:service-id", :service-id #".+"] {{service-id :service-id} :params}
       ;; create-rdf returns a complete response
       ;; and is probably a lot easier to redefine, as compojure's/ring's handlers are somewhat repl-hostile to redefine
       (turtle-response (ring-io/piped-input-stream (fn [out]
                                                      (create-rdf config db out service-id))))))))

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
