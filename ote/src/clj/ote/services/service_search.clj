(ns ote.services.service-search
  "Backend services for transport service search."
  (:require [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [specql.core :as specql]
            [taoensso.timbre :as log]
            [compojure.core :refer [routes GET]]
            [jeesql.core :refer [defqueries]]
            [ote.db.transport-service :as t-service]
            [ote.db.transport-operator :as t-operator]
            [ote.db.service-search :as search]
            [ote.db.common :as common]
            [ote.db.modification :as modification]
            [specql.op :as op]
            [clojure.set :as set]
            [clojure.string :as str]))

(defqueries "ote/services/service_search.sql")

(defn search-facets
  "Return facet information for the search page (selections and their counts)
  using queries defined in `service_search.sql` file."
  [db]
  {::t-service/operation-area (vec (operation-area-facet db))
   ::t-service/sub-type
   (into []
         (map #(update % :sub-type keyword))
         (sub-type-facet db))})



(def search-result-columns
  #{::t-service/contact-email
    ::t-service/sub-type
    ::t-service/ckan-resource-id
    ::t-service/id
    ::t-service/contact-gsm
    ::t-service/ckan-dataset-id
    ::t-service/contact-address
    ::t-service/name
    ::t-service/type
    ::t-service/transport-operator-id
    ::t-service/contact-phone

    ;; Information JOINed from other tables
    ::t-service/external-interface-links
    ::t-service/operator-name})

(defn- ids [key query-result]
  (into #{} (map key) query-result))

(defn- operation-area-ids [db operation-area]
  (when-not (empty? operation-area)
    (ids ::search/transport-service-id
         (specql/fetch db ::search/operation-area-facet
                       #{::search/transport-service-id}
                       {::search/operation-area (op/in (map str/capitalize operation-area))}))))

(defn- text-search-ids [db text]
  (when-not (str/blank? text)
    (let [text (str/trim text)]
      (ids ::t-service/id
           (specql/fetch db ::t-service/transport-service
                         #{::t-service/id}
                         {::t-service/published? true
                          ::t-service/name (op/ilike (str "%" text "%"))})))))

(defn- sub-type-ids [db types]
  (when-not (empty? types)
    (ids ::t-service/id
         (specql/fetch db ::t-service/transport-service
                       #{::t-service/id}
                       {::t-service/published? true
                        ::t-service/sub-type (op/in types)}))))

(defn- operator-ids [db operators]
  (when-not (empty? operators)
    (ids ::t-service/id
         (specql/fetch db ::t-service/transport-service
                       #{::t-service/id}
                       {::t-service/published? true
                        ::t-service/transport-operator-id (op/in operators)}))))

(defn operator-completions
  "Return a list of completions that match the given search term."
  [db term]
  (into []
        (specql/fetch db ::t-operator/transport-operator
                      #{::t-operator/name ::t-operator/id}
                      {::t-operator/name (op/ilike (str "%" term "%"))
                       ::t-operator/deleted? false}
                      {::specql/order-by ::t-operator/name})))

(defn- search [db {:keys [operation-area sub-type text operators offset limit]
                   :as filters}]
  (let [result-id-sets [(operation-area-ids db operation-area)
                        (sub-type-ids db sub-type)
                        (text-search-ids db text)
                        (operator-ids db operators)]
        empty-filters? (every? nil? result-id-sets)
        ids (if empty-filters?
              ;; No filters specified, show latest services
              (latest-service-ids db)
              ;; Combine with intersection (AND)
              (apply set/intersection
                     (remove nil? result-id-sets)))
        options (if (and offset limit)
                  {:specql.core/offset offset
                   :specql.core/limit limit
                   :specql.core/order-by :ote.db.modification/created
                   :specql.core/order-direction :desc}
                  {})
        results (specql/fetch db ::t-service/transport-service-search-result
                              search-result-columns
                              {::t-service/id (op/in ids)}
                              options)]
    {:empty-filters? empty-filters?
     :total-service-count (total-service-count db)
     :results results
     :filter-service-count (count ids)}))

(defn- service-search-parameters
  "Extract service search parameters from query parameters."
  [params]
  {:operation-area (some-> (params "operation_area")
                           (str/split #","))
   :text (params "text")
   :sub-type (when-let [st (some-> (params "sub_types")
                                   (str/split #","))]
               (into #{} (map keyword st)))
   :operators (map #(Long/parseLong %)
                   (some-> "operators" params
                           (str/split #",")))
   :limit (some-> "limit" params (Integer/parseInt))
   :offset (some-> "offset" params (Integer/parseInt))})

(defn- service-search-routes [db]
  (routes
   (GET "/operator-completions/:term" {{term :term} :params :as req}
        (http/api-response req
                           (operator-completions db term)))

   (GET "/service-search/facets" []
        (http/no-cache-transit-response
         (search-facets db)))

   (GET "/service-search" {params :query-params :as req}
        (http/with-no-cache-headers
          (http/api-response
           req
           (search db (service-search-parameters params)))))))

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
