(ns ote.services.operators
  "Backend services for operators listing."
  (:require [ote.components.http :as http]
            [com.stuartsierra.component :as component]
            [ote.db.transport-operator :as t-operator]
            [specql.core :as specql]
            [compojure.core :refer [routes POST]]
            [specql.op :as op]
            [clojure.string :as str]
            [jeesql.core :refer [defqueries]]))

(defqueries "ote/services/operators.sql")

(def operator-listing-columns
  #{::t-operator/id ::t-operator/name
    ::t-operator/homepage ::t-operator/email
    ::t-operator/phone ::t-operator/gsm
    ::t-operator/visiting-address})

(defn fetch-service-counts [db operators]
  (let [ids (into #{} (map ::t-operator/id) operators)
        services-by-id (into {}
                             (map (juxt :id :services))
                             (fetch-operator-service-counts db {:operators ids}))]
    (mapv (fn [{id ::t-operator/id :as operator}]
            (assoc operator ::t-operator/service-count
                   (services-by-id id 0)))
          operators)))

(defn list-operators [db {:keys [query limit offset]}]
  {:results
   (fetch-service-counts
    db
    (specql/fetch db ::t-operator/transport-operator
                  operator-listing-columns
                  (if (str/blank? query)
                    {}
                    {::t-operator/name (op/ilike (str "%" query "%"))})
                  {:specql.core/order-by ::t-operator/name
                   :specql.core/order-direction :asc
                   :specql.core/limit (or limit 25)
                   :specql.core/offset (or offset 0)}))
   :total-count
   (if (str/blank? query)
     (count-all-operators db)
     (count-matching-operators db {:name (str "%" query "%")}))})

(defn operator-routes [db]
  (routes
   (POST "/operators/list" {form-data :body}
         (http/transit-response
          (list-operators db
                          (http/transit-request form-data))))))

(defrecord Operators []
  component/Lifecycle
  (start [{db :db http :http :as this}]
    (assoc this ::stop
           (http/publish! http (operator-routes db))))

  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
