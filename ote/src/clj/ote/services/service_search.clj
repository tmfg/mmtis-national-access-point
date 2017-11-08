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



;; FIXME: define better result set
(def search-result-columns
  #{:ote.db.transport-service/contact-email
    :ote.db.transport-service/sub-type
    :ote.db.transport-service/parking
    :ote.db.modification/modified-by
    :ote.db.transport-service/ckan-resource-id
    :ote.db.transport-service/brokerage
    :ote.db.transport-service/id
    :ote.db.modification/created
    :ote.db.transport-service/contact-gsm
    :ote.db.transport-service/ckan-dataset-id
    :ote.db.transport-service/terminal
    :ote.db.transport-service/contact-address
    :ote.db.transport-service/rental
    :ote.db.modification/modified
    :ote.db.modification/created-by
    :ote.db.transport-service/homepage
    :ote.db.transport-service/name
    :ote.db.transport-service/type
    :ote.db.transport-service/transport-operator-id ;; FIXME: join operator
    :ote.db.transport-service/contact-phone
    :ote.db.transport-service/passenger-transportation})

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

(defn- search [db filters]
  (let [ids (apply set/intersection
                   (remove nil?
                           [(operation-area-ids db (:operation-area filters))
                            (text-search-ids db (:text params))]))]
    (specql/fetch db ::t-service/transport-service
                  search-result-columns
                  {::t-service/id (op/in ids)})))

(defn- service-search-routes [db]
  (routes
   (GET "/service-search/facets" []
        (http/transit-response (search-facets db)))

   (GET "/service-search" {params :query-params}
        (http/transit-response
         (search {:operation-area (some-> (params "operation_area")
                                                  (str/split #","))
                  :text (params "text")})))
   ))

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
