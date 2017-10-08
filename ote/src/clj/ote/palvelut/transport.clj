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
            [taoensso.timbre :as log]))

(defn db-get-transport-operator [db business-id]
  (fetch db ::transport-operator/transport-operator #{ ::transport-operator/id } {::transport-operator/business-id business-id})
  )

(defn- get-transport-operator [db business-id]
  (http/transit-response (db-get-transport-operator db business-id)))

(defn- save-transport-operator [db data]
  (println "save-transport-operator data " data)
  (upsert! db ::transport-operator/transport-operator data)
  )

(defn- save-passenger-transportation-info [db data]
  (println "save-passenger-transportation-info " (pr-str data))
  (upsert! db ::transport-service/transport-service data)
  )

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
