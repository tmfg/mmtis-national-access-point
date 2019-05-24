(ns ote.services.service-search
  "Backend services for transport service search."
  (:require [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [specql.core :as specql]
            [taoensso.timbre :as log]
            [compojure.core :refer [routes GET]]
            [jeesql.core :refer [defqueries]]
            [ote.db.transport-service :as t-service]
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

(defn- map->set [key query-result]
  (into #{} (map key) query-result))

(defn- services-operating-in
  "Returns ids of services which operate in the given areas"
  [db operation-area]
  (when (seq operation-area)
    (map->set
     :id
     (service-ids-by-operation-areas db {:operation-area operation-area}))))

(defn match-quality
  "Returns match quality of a search result to the operation-area it was searched against. Greater return values are worse matches.
Negative return value is an invalid match"
  [intersection difference]
  (if (pos? intersection) (/ difference intersection) -1))

(defn- id-to-quality
  "Processes `spatial-diffs` into a mapping of id > match quality"
  [spatial-diffs]
  (into [] (map
             (fn [row]
               {:id (:id row)
                :difference (match-quality (:intersection row) (:difference row))
                :modified (:modified row)})
             spatial-diffs)))

(defn- service-search-match-qualities
  "Returns a mapping of service id -> match quality
  `db` database handle
  `service-ids` collection of ids for which qualities are returned
  `operation-area` collection of place names against which match quality is compared"
  [db service-ids operation-area]
  (let  [services (service-match-quality-to-operation-area db {:id service-ids :operation-area operation-area})
         services (id-to-quality services)]
    services))

(defn- text-search-ids [db text]
  (when-not (str/blank? text)
    (let [text (str/trim text)]
      (map->set ::t-service/id
           (specql/fetch db ::t-service/transport-service
                         #{::t-service/id}
                         {::t-service/published op/not-null?
                          ::t-service/name (op/ilike (str "%" text "%"))})))))

(defn- sub-type-ids
  "Fetch sub-types and brokerage service."
  [db types]
  (let [ids (cond
              (and (> (count types) 1) (contains? types :brokerage)) ;; Get sub types and brokerage
                (map->set ::t-service/id
                    (specql/fetch db ::t-service/transport-service
                                  #{::t-service/id}
                                  (op/and {::t-service/published op/not-null?}
                                          (op/or
                                           {::t-service/sub-type (op/in types)}
                                           {::t-service/brokerage? true}))))
              (and (seq types) (not (contains? types :brokerage))) ;; Only sub types
                (map->set ::t-service/id
                    (specql/fetch db ::t-service/transport-service
                                  #{::t-service/id}
                                  {::t-service/sub-type   (op/in types)
                                   ::t-service/published op/not-null?}))
              (and (= 1 (count types)) (contains? types :brokerage)) ;; Only brokerage
                (map->set ::t-service/id
                    (specql/fetch db ::t-service/transport-service
                                  #{::t-service/id}
                                  {::t-service/published op/not-null?
                                   ::t-service/brokerage? true})))]
    ids))

(defn- transport-type-ids [db transport-types]
  (when (seq transport-types)
    (map->set
      :id
      (service-ids-by-transport-type db {:tt (apply list transport-types)}))))

(defn- operator-ids [db operators]
  (when (seq operators)
    (map->set
      :id
      (service-ids-by-business-id db {:operators (apply list operators)}))))

(defn- data-content-ids [db data-content]
  (when (seq data-content)
    (map->set
      :id
      (service-ids-by-data-content db {:dc (apply list data-content)}))))

(defn operator-completions
  "Return a list of completions that match the given search term."
  [db term]
  (vec
    (service-search-by-operator db {:name (str "%" term "%")
                                    :businessid (str term )})))

(defn service-completions
  "Return a list of service completions that match the given name"
  [db name]
  (vec (service-search-by-service-name db {:name (str "%" name "%")})))

(def search-result-columns
  #{::t-service/contact-email
    ::t-service/sub-type
    ::t-service/id
    ::t-service/contact-gsm
    ::t-service/contact-address
    ::t-service/name
    ::t-service/type
    ::t-service/transport-operator-id
    ::t-service/contact-phone
    ::t-service/description
    ::t-service/published
    ::t-service/transport-type
    ::t-service/homepage
    ;; Information JOINed from other tables
    ::t-service/external-interface-links
    ::t-service/operator-name
    ::t-service/service-companies
    ::t-service/business-id})

(defn- hide-import-errors [ei-link]
  (if (or (::t-service/gtfs-import-error ei-link) (::t-service/gtfs-db-error ei-link))
    (-> ei-link
        (assoc :has-errors? true)
        (dissoc ::t-service/gtfs-import-error
                ::t-service/gtfs-db-error))
    ei-link))

(defn- transport-services
  "Queries database for transport services.
  `db` is the database handle
  `ids` is a collection of transport service ids."
  [db ids]
  (specql/fetch db ::t-service/transport-service-search-result
                search-result-columns
                {::t-service/id (op/in (map :id ids))}
                {:specql.core/order-by :ote.db.modification/modified
                 :specql.core/order-direction :desc}))

(defn- transport-services-page
  "Queries database for a page of transport services.
  `db` is the database handle
  `ids` is a collection of transport service ids.
  `offset` and `limit` make it possible to use paging instead of fetching all the results"
  [db ids offset limit]
  (let [options (if (and offset limit)
                  {:specql.core/offset offset
                   :specql.core/limit limit
                   :specql.core/order-by :ote.db.modification/created
                   :specql.core/order-direction :desc}
                  {})]
    (specql/fetch db ::t-service/transport-service-search-result
                              search-result-columns
                              {::t-service/id (op/in ids)}
                              options)))

(defn- page-of
  [seq offset limit]
  (if (and offset limit)
    (take limit (drop offset seq))
    seq))

(defn- transport-services-in-operation-area
  "Queries database for transport services ordered so that best matches to the operation area are first in the results.
  `db` is the database handle
  `operation-area` is a collection of operation area names
  `ids` is a collection of transport service ids.
  `offset` and `limit` make it possible to use paging instead of fetching all the results"
  [db ids operation-area offset limit]
  (let [match-qualities (service-search-match-qualities db ids operation-area)
        sorted-ids (sort-by (juxt :difference :modified) match-qualities)
        paged-result (page-of sorted-ids offset limit)
        results (transport-services db paged-result)
        results (map
                  (fn [result]
                    (let [maps-by-id (zipmap (map :id match-qualities) match-qualities)
                          service-id (::t-service/id result)
                          r (maps-by-id service-id)]
                      (-> result
                          (assoc :modified (:modified r))
                          (assoc :difference (:difference r)))))
                  results)
        results (sort-by (juxt :difference :modified) results)]
    results))

(defn- without-import-errors [search-result]
  (update-in search-result
             [::t-service/external-interface-links]
             #(mapv hide-import-errors %)))

(defn- without-personal-info
  "Removes contact information from the `search-result`"
  [search-result]
  (dissoc search-result ::t-service/contact-email ::t-service/contact-address ::t-service/contact-phone))

(defn- filtering-operators
  "Returns a function that removes not interesting companies from the `search-result`"
  [operators]
  (fn [search-result]
    (update search-result ::t-service/service-companies
            (fn [c]
              (filter (fn [company]
                        (when (and operators (::t-service/business-id company))
                          (.contains operators (::t-service/business-id company))))
                      c)))))
(defn- pare-results
  "Removes information not intended for the frontend from `search-results`
  Retains `searched-operators` in the search results"
  [search-results searched-operators]
  (->> search-results
       (mapv without-import-errors)
       (mapv (filtering-operators searched-operators))
       (mapv without-personal-info)))

(defn search [db {:keys [operation-area sub-type data-content transport-type text operators offset limit]
                   :as filters}]
  (let [;; Get service id's using different filters. If filter is not given no results will be returned.
        result-id-sets [(services-operating-in db operation-area)
                        (sub-type-ids db sub-type)
                        (transport-type-ids db transport-type)
                        (data-content-ids db data-content)
                        (text-search-ids db text)
                        (operator-ids db operators)]
        empty-filters? (every? nil? result-id-sets)         ;; filters are empty if results are nil
        ids (if empty-filters?
              ;; No filters specified, show latest services
              (latest-service-ids db)
              ;; Combine with intersection (AND)
              (apply set/intersection (remove nil? result-id-sets)))]
    (-> (if operation-area
                  (transport-services-in-operation-area db ids operation-area offset limit)
                  (transport-services-page db ids offset limit))
        (pare-results operators)
        (as-> results (merge
                       {:empty-filters? empty-filters?
                        :results results
                        :filter-service-count (count ids)}
                       (when empty-filters?
                         {:total-service-count (total-service-count db)
                          :total-company-count (total-company-count db)}))))))

(defn- service-search-parameters
  "Extract service search parameters from query parameters."
  [params]
  {:operation-area (some-> (params "operation_area")
                           (str/split #","))
   :text (params "text")
   :sub-type (when-let [st (some-> (params "sub_types")
                                   (str/split #","))]
               (into #{} (map keyword st)))
   :transport-type (some-> "transport_types" params
                           (str/split #","))
   :operators (some-> "operators" params
                      (str/split #","))
   :data-content (some-> "data_content" params
                      (str/split #","))
   :limit (some-> "limit" params (Integer/parseInt))
   :offset (some-> "offset" params (Integer/parseInt))})

(defn- service-search-routes [db]
  (routes
    (GET ["/operator-completions/:term", :term #".+"] {{term :term} :params :as req}
      (http/api-response req (operator-completions db term)))

    (GET ["/service-completions/:name", :name #".+"] {{name :name} :params :as req}
      (http/api-response req (service-completions db name)))

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
