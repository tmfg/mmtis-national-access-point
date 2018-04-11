(ns ote.services.pre-notices.operator
  "Transport operator's routes for managing 60 day pre notices"
  (:require [ote.components.http :as http]
            [ote.authorization :as authorization]
            [ote.db.transit :as transit]
            [specql.core :as specql]
            [compojure.core :refer [routes GET POST]]
            [specql.op :as op]
            [clojure.string :as str]))

(defn list-operator-notices [db user]
  (http/no-cache-transit-response
   (specql/fetch db ::transit/pre-notice
                 (specql/columns ::transit/pre-notice)
                 {::transit/transport-operator-id (op/in (authorization/user-transport-operators db user))})))

(defn operator-pre-notices-routes
  "Routes for listing and creating pre notices for transport operators"
  [db]
  (routes
   (GET "/pre-notices/list" {user :user}
        (#'list-operator-notices db user))))
