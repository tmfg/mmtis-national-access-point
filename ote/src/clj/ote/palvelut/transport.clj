(ns ote.palvelut.transport
  "Services for getting transport data from database"
  (:require [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [specql.core :refer [fetch update! insert! upsert!] :as specql]
            [specql.op :as op]
            [ote.db.transport-operator :as transport-operator]
            [ote.db.transport-service :as transport-service]
            [ote.db.common :as common]
            [compojure.core :refer [routes GET POST]]
            [taoensso.timbre :as log]
            [clojure.java.jdbc :as jdbc]
            [specql.impl.composite :as specql-composite]
            [ote.services.places :as places]))

;; FIXME: monkey patch specql composite reading (For now)
(defmethod specql-composite/parse-value "bpchar" [_ string] string)

(def transport-operator-columns
  #{::transport-operator/id ::transport-operator/business-id ::transport-operator/email
    ::transport-operator/name})

(defn db-get-transport-operator [db where]
  (first (fetch db ::transport-operator/transport-operator
                transport-operator-columns
                where {::specql/limit 1}))
  )

(def transport-services-passenger-columns
  #{::transport-service/id ::transport-service/type :ote.db.transport-service/passenger-transportation})

(defn db-get-transport-services [db where]
  "Return Vector of transport-services"
  (fetch db ::transport-service/transport-service
                transport-services-passenger-columns
                where))



(defn- ensure-transport-operator-for-group [db {:keys [title id] :as ckan-group}]
  (jdbc/with-db-transaction [db db]
     (let [operator (db-get-transport-operator db {::transport-operator/ckan-group-id id})]
         (or operator
             ;; FIXME: what if name changed in CKAN, we should update?
             (insert! db ::transport-operator/transport-operator
                      {::transport-operator/name title
                       ::transport-operator/ckan-group-id id})))))


(defn- get-transport-operator-data [db {:keys [title id] :as ckan-group}]
  (println " get-transport-operator-data " ckan-group)
  (let [
        transport-operator (ensure-transport-operator-for-group db ckan-group)
        transport-services-vector (db-get-transport-services db {::transport-service/transport-operator-id (::transport-operator/id transport-operator)})
        ]
    {:transport-operator transport-operator
     :transport-service-vector transport-services-vector}))


(defn- save-transport-operator [db data]
  (upsert! db ::transport-operator/transport-operator data))


(defn- fix-price-classes
  "Frontend sends price classes prices as floating points. Convert them to bigdecimals before db insert."
  [price-classes-float]
  (try
    (mapv #(update % ::transport-service/price-per-unit bigdec) price-classes-float)
    (catch Exception e (println "price-per-unit is probably missing"))))


(defn- save-passenger-transportation-info
  "UPSERT! given data to database. And convert possible float point values to bigdecimal"
  [db data]
  (println "DATA: " (pr-str data))
  (let [places (get-in data [::transport-service/passenger-transportation ::transport-service/operation-area])
        value (-> data
                  (update ::transport-service/passenger-transportation dissoc ::transport-service/operation_area)
                  (update-in [::transport-service/passenger-transportation ::transport-service/price-classes] fix-price-classes))]
    (jdbc/with-db-transaction [db db]
      (let [transport-service
            (upsert! db ::transport-service/transport-service value)]
        (places/link-places-to-transport-service!
         db (::transport-service/id transport-service) places)))))


(defrecord Transport []
  component/Lifecycle
  (start [{:keys [db http] :as this}]
    (assoc
      this ::lopeta
           (http/publish! http (routes
                                  (POST "/transport-operator/group" {user :user}
                                    (ensure-transport-operator-for-group db (-> user :groups first)))

                                  (POST "/transport-operator/data" {user :user}
                                    (http/transit-response (get-transport-operator-data db (-> user :groups first))))

                                  (POST "/transport-operator" {form-data :body
                                                               user :user}
                                        (log/info "USER: " user)
                                        (http/transit-response (save-transport-operator db (http/transit-request form-data))))
                                  (POST "/passenger-transportation-info" {form-data :body}
                                        (http/transit-response (save-passenger-transportation-info db (http/transit-request form-data))))
                                  ))))
  (stop [{lopeta ::lopeta :as this}]
    (lopeta)
    (dissoc this ::lopeta)))
