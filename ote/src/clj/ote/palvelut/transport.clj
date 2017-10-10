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
            [clojure.java.jdbc :as jdbc]))

(def transport-operator-columns
  #{::transport-operator/id ::transport-operator/business-id ::transport-operator/email
    ::transport-operator/name})

(defn db-get-transport-operator [db where]
  (first (fetch db ::transport-operator/transport-operator
                transport-operator-columns
                where {::specql/limit 1}))
  )
;;{::transport-operator/business-id business-id}

(defn- ensure-transport-operator-for-group [db {:keys [title id] :as ckan-group}]
  (jdbc/with-db-transaction [db db]
     (let [operator (db-get-transport-operator db {::transport-operator/ckan-group-id id})]
       (http/transit-response
         (or operator
             ;; FIXME: what if name changed in CKAN, we should update?
             (insert! db ::transport-operator/transport-operator
                      {::transport-operator/name title
                       ::transport-operator/ckan-group-id id}))))))


(defn- save-transport-operator [db data]
  (println "save-transport-operator data " data)
  (upsert! db ::transport-operator/transport-operator data)
  )


(defn- fix-price-classes
  "Frontend sends price classes prices as floating points. Convert them to bigdecimals before db insert."
  [price-classes-float]
  (try
    (mapv #(update % ::transport-service/price-per-unit bigdec) price-classes-float)
    (catch Exception e (println "price-per-unit is probably missing"))))

(defn- save-passenger-transportation-info [db data]
  "UPSERT! given data to database. And convert possible float point values to bigdecimal"
  (let [value (update-in data [::transport-service/passenger-transportation ::transport-service/price-classes] fix-price-classes)]
    (upsert! db ::transport-service/transport-service value)))


(defrecord Transport []
  component/Lifecycle
  (start [{:keys [db http] :as this}]
    (assoc
      this ::lopeta
           (http/publish! http (routes                                  
                                  (POST "/transport-operator/group" {user :user}
                                    (ensure-transport-operator-for-group db (-> user :groups first)))
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
