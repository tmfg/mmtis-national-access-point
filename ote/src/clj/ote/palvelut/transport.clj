(ns ote.palvelut.transport
  "Services for getting transport data from database"
  (:require [com.stuartsierra.component :as component]
            [ote.komponentit.http :as http]
            [specql.core :refer [fetch update! insert! upsert!]]
            [specql.op :as op]
            [ote.domain.liikkumispalvelu :as t]
            [compojure.core :refer [routes GET]]))

(defn db-get-transport-operator [db business-id]
  (println "Jee. tuli perille")
  (fetch db ::t/transport-operator #{ ::t/id } {::t/business-id business-id})
  )

(defn- get-transport-operator [db business-id]
  (println "tuleeko tänne")
  (http/transit-vastaus (db-get-transport-operator db business-id)))


(defrecord Transport []
  component/Lifecycle
  (start [{:keys [db http] :as this}]
    (assoc
      this ::lopeta
           (http/julkaise! http (routes
                                  (GET "/transport-operator/:business-id" [business-id]
                                    (get-transport-operator db business-id))))))
  (stop [{lopeta ::lopeta :as this}]
    (lopeta)
    (dissoc this ::lopeta)))
