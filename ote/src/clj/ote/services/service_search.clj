(ns ote.services.service-search
  "Backend services for transport service search."
  (:require [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [specql.core :as specql]
            [taoensso.timbre :as log]
            [compojure.core :refer [routes GET]]
            [jeesql.core :refer [defqueries]]
            [ote.db.transport-service :as t-service]
            [ote.db.service-search :as search]
            [specql.op :as op]
            [clojure.set :as set]
            [clojure.string :as str]))

(defqueries "ote/services/service_search.sql")

(defn search-facets [db]
  {::t-service/operation-area (vec (operation-area-facet db))
   ::t-service/sub-type
   (into []
         (map #(update % :sub-type keyword))
         (sub-type-facet db))})



(def search-result-columns
  #{::t-service/contact-email
    ::t-service/sub-type
    ::t-service/parking
    ::t-service/ckan-resource-id
    ::t-service/brokerage
    ::t-service/id
    ::t-service/contact-gsm
    ::t-service/ckan-dataset-id
    ::t-service/terminal
    ::t-service/contact-address
    ::t-service/rental
    ::t-service/homepage
    ::t-service/name
    ::t-service/type
    ::t-service/transport-operator-id
    ::t-service/contact-phone
    ::t-service/passenger-transportation

    ;; Information JOINed from other tables
    ::t-service/operation-area-description
    ::t-service/external-interface-links
    ::t-service/operator-name})

(defn- ids [key query-result]
  (into #{} (map key) query-result))

(defn- operation-area-ids [db operation-area]
  (when-not (empty? operation-area)
    (ids ::search/transport-service-id
         (specql/fetch db ::search/operation-area-facet
                       #{::search/transport-service-id}
                       {::search/operation-area (op/in operation-area)}))))

(defn- text-search-ids [db text]
  (when-not (str/blank? text)
    (let [text (str/trim text)]
      (ids ::t-service/id
           (specql/fetch db ::t-service/transport-service
                         #{::t-service/id}
                         {::t-service/name (op/ilike (str "%" text "%"))})))))

(defn- sub-type-ids [db types]
  (when-not (empty? types)
    (ids ::t-service/id
         (specql/fetch db ::t-service/transport-service
                       #{::t-service/id}
                       {::t-service/sub-type (op/in types)}))))

(defn- search [db filters]
  (let [ids (apply set/intersection
                   (remove nil?
                           ;; PENDING: we could (should) do this as a single query to INTERSECT all
                           ;; search facet ids
                           [(operation-area-ids db (:operation-area filters))
                            (sub-type-ids db (:sub-type filters))
                            (text-search-ids db (:text filters))]))]
    (specql/fetch db ::t-service/transport-service-search-result
                  search-result-columns
                  {::t-service/id (op/in ids)})))

(defn- service-search-routes [db]
  (routes
   (GET "/service-search/facets" []
        (http/transit-response (search-facets db)))

   (GET "/service-search" {params :query-params}
        (http/transit-response
         (search db
                 {:operation-area (some-> (params "operation_area")
                                          (str/split #","))
                  :text (params "text")
                  :sub-type (when-let [st (some-> (params "sub_types")
                                                  (str/split #","))]
                              (into #{} (map keyword st)))})))))

(defrecord ServiceSearch []
  component/Lifecycle
  (start [{db :db
           http :http
           :as this}]
    (assoc this ::stop
           (http/publish! http {:authenticated? false}
                          (service-search-routes db))))

  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
