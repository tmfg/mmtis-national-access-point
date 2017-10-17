(ns ote.palvelut.transport
  "Services for getting transport data from database"
  (:require [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [specql.core :refer [fetch update! insert! upsert!] :as specql]
            [specql.op :as op]
            [ote.db.transport-operator :as transport-operator]
            [ote.db.transport-service :as t-service]
            [ote.db.common :as common]
            [compojure.core :refer [routes GET POST]]
            [taoensso.timbre :as log]
            [clojure.java.jdbc :as jdbc]
            [specql.impl.composite :as specql-composite]
            [ote.services.places :as places]
            [ote.authorization :as authorization]
            [ote.db.tx :as tx])
  (:import (java.time LocalTime)))

;; FIXME: monkey patch specql composite reading (For now)
(defmethod specql-composite/parse-value "bpchar" [_ string] string)

(def transport-operator-columns
  #{::transport-operator/id ::transport-operator/business-id ::transport-operator/email
    ::transport-operator/name})

(defn get-transport-operator [db where]
  (first (fetch db ::transport-operator/transport-operator
                (specql/columns ::transport-operator/transport-operator)
                where {::specql/limit 1})))

(def transport-services-passenger-columns
  #{::t-service/id ::t-service/type :ote.db.transport-service/passenger-transportation
    ::t-service/published?})

(defn get-transport-services [db where]
  "Return Vector of transport-services"
  (fetch db ::t-service/transport-service
                transport-services-passenger-columns
                where))

(defn- get-transport-service
  "Get single transport service by id"
  [db id]
  (first (fetch db
                ::t-service/transport-service
                (specql/columns ::t-service/transport-service)
                {::t-service/id id}
                {::specql/limit 1})))


(defn- ensure-transport-operator-for-group [db {:keys [title id] :as ckan-group}]
  (tx/with-transaction db
    (let [operator (get-transport-operator db {::transport-operator/ckan-group-id id})]
      (or operator
          ;; FIXME: what if name changed in CKAN, we should update?
          (insert! db ::transport-operator/transport-operator
                   {::transport-operator/name title
                    ::transport-operator/ckan-group-id id})))))


(defn- get-transport-operator-data [db {:keys [title id] :as ckan-group} user]
  (let [
        transport-operator (ensure-transport-operator-for-group db ckan-group)
        transport-services-vector (get-transport-services db {::t-service/transport-operator-id (::transport-operator/id transport-operator)})
        ]
    {:transport-operator transport-operator
     :transport-service-vector transport-services-vector
     :user user}))


(defn- save-transport-operator [db data]
  (upsert! db ::transport-operator/transport-operator data))


(defn- fix-price-classes
  "Frontend sends price classes prices as floating points. Convert them to bigdecimals before db insert."
  [price-classes-float]
  (try
    (mapv #(update % ::t-service/price-per-unit bigdec) price-classes-float)
    (catch Exception e (println "price-per-unit is probably missing"))))

(defn- save-passenger-transportation-info
  "UPSERT! given data to database. And convert possible float point values to bigdecimal"
  [db data]
  (println "DATA: " (pr-str data))
  (let [places (get-in data [::t-service/passenger-transportation ::t-service/operation-area])
        value (-> data
                  (update ::t-service/passenger-transportation dissoc ::t-service/operation_area)
                  (update-in [::t-service/passenger-transportation ::t-service/price-classes] fix-price-classes))]
    (jdbc/with-db-transaction [db db]
      (let [transport-service
            (upsert! db ::t-service/transport-service value)]
        (places/link-places-to-transport-service!
         db (::t-service/id transport-service) places)))))


(defn- publish-transport-service [db user {:keys [transport-service-id]}]
  (let [transport-operator-ids (authorization/user-transport-operators db user)]
    (= 1
       (specql/update! db ::t-service/transport-service
                       {::t-service/published? true}

                       {::t-service/transport-operator-id (op/in transport-operator-ids)
                        ::t-service/id transport-service-id}))))


(defn- transport-routes
  [db]
  (routes

   (GET "/transport-service/:id" [id]
     (http/transit-response (get-transport-service db (Long/parseLong id))))

   (POST "/transport-operator/group" {user :user}
         (ensure-transport-operator-for-group db (-> user :groups first)))

   (POST "/transport-operator/data" {user :user}
        (http/transit-response (get-transport-operator-data db (-> user :groups first) (:user user))))

   (POST "/transport-operator" {form-data :body
                                user :user}
         (log/info "USER: " user)
         (http/transit-response (save-transport-operator db (http/transit-request form-data))))
   (POST "/passenger-transportation-info" {form-data :body}
         (http/transit-response (save-passenger-transportation-info db (http/transit-request form-data))))

   (POST "/transport-service/publish" {payload :body
                                       user :user}
         (->> payload
              http/transit-request
              (publish-transport-service db user)
              http/transit-response))))

(defrecord Transport []
  component/Lifecycle
  (start [{:keys [db http] :as this}]
    (assoc
      this ::lopeta
           (http/publish! http (transport-routes db))))
  (stop [{lopeta ::lopeta :as this}]
    (lopeta)
    (dissoc this ::lopeta)))
