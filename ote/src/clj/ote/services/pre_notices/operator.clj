(ns ote.services.pre-notices.operator
  "Transport operator's routes for managing 60 day pre notices"
  (:require [ote.components.http :as http]
            [ote.authorization :as authorization]
            [ote.db.transit :as transit]
            [ote.db.transport-operator :as t-operator]
            [specql.core :as specql]
            [compojure.core :refer [routes GET POST]]
            [specql.op :as op]
            [clojure.string :as str]
            [ote.db.tx :as tx]
            [ote.db.modification :as modification]
            [taoensso.timbre :as log]
            [ote.db.transport-operator :as t-operator]))

(defn list-operator-notices [db user]
  (http/no-cache-transit-response
   (specql/fetch db ::transit/pre-notice
                 (specql/columns ::transit/pre-notice)
                 {::t-operator/id (op/in (authorization/user-transport-operators db user))})))

(defn save-pre-notice [db user notice]
  (println "DAta " (pr-str notice))
  (authorization/with-transport-operator-check
    db user (::t-operator/id notice)
    (fn []
      (tx/with-transaction db
        (let [n (-> notice
                    (modification/with-modification-fields ::transit/id user))]
          (log/debug "Save notice: " n)
          (specql/upsert! db ::transit/pre-notice n))))))

(defn operator-pre-notices-routes
  "Routes for listing and creating pre notices for transport operators"
  [db]
  (routes
   (GET "/pre-notices/list" {user :user}
        (list-operator-notices db user))
   (POST "/pre-notice" {form-data :body
                        user      :user}
         (http/transit-response
          (save-pre-notice db user (http/transit-request form-data))))))
