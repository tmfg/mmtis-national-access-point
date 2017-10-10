(ns ote.palvelut.transport
  "Services for getting transport data from database"
  (:require [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [specql.core :refer [fetch update! insert! upsert!]]
            [specql.op :as op]
            [ote.db.transport-operator :as transport-operator]
            [ote.db.transport-service :as transport-service]
            [ote.db.common :as common]
            [compojure.core :refer [routes GET POST]]
            [taoensso.timbre :as log]
            [clojure.java.jdbc :as jdbc]
            [ote.services.places :as places]))

(defn db-get-transport-operator [db business-id]
  (fetch db ::transport-operator/transport-operator #{ ::transport-operator/id } {::transport-operator/business-id business-id})
  )

(defn- get-transport-operator [db business-id]
  (http/transit-response (db-get-transport-operator db business-id)))

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
                                  (GET "/transport-operator/:business-id" [business-id]
                                    (get-transport-operator db business-id))
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
