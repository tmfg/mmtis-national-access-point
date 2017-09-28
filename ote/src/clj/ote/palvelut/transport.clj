(ns ote.palvelut.transport
  "Services for getting transport data from database"
  (:require [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [specql.core :refer [fetch update! insert! upsert!]]
            [specql.op :as op]
            [ote.domain.liikkumispalvelu :as t]
            [compojure.core :refer [routes GET POST]]))

(defn db-get-transport-operator [db business-id]
  (fetch db ::t/transport-operator #{ ::t/id } {::t/business-id business-id})
  )

(defn- get-transport-operator [db business-id]
  (http/transit-response (db-get-transport-operator db business-id)))

(defn- save-transport-operator [db data]
  (upsert! db ::t/transport-operator data)
  )

(defn- save-passengert-transportation-info [db data]
  (upsert! db ::t/transport-service data)
  )

(defrecord Transport []
  component/Lifecycle
  (start [{:keys [db http] :as this}]
    (assoc
      this ::lopeta
           (http/publish! http (routes
                                  (GET "/transport-operator/:business-id" [business-id]
                                    (get-transport-operator db business-id))
                                  (POST "/transport-operator" {form-data :body}
                                    (http/transit-response (save-transport-operator db (http/transit-request form-data))))
                                  (POST "/passenger-transportation-info" {form-data :body}
                                    (http/transit-response (save-passengert-transportation-info db (http/transit-request form-data))))
                                  ))))
  (stop [{lopeta ::lopeta :as this}]
    (lopeta)
    (dissoc this ::lopeta)))
