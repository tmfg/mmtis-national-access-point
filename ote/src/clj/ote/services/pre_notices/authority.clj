(ns ote.services.pre-notices.authority
  "Pre notice services for transport authority users"
  (:require [ote.components.http :as http]
            [compojure.core :refer [routes POST GET]]
            [specql.core :as specql]
            [ote.db.transit :as transit]
            [ote.db.modification :as modification]
            [ote.authorization :as authorization]))


(defn list-published-notices [db]
  (specql/fetch db ::transit/pre-notice
                (specql/columns ::transit/pre-notice)
                {}
                #_{:specql.core/order-by ::modification/created}))

(defn authority-pre-notices-routes [db]
  (routes
   (GET "/pre-notices/authority-list" {user :user :as req}
        (authorization/with-transit-authority-check
          db user
          #(http/no-cache-transit-response (list-published-notices db))))))
