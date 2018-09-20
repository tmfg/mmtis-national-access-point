(ns ote.services.settings
  "Backend services for settings."
  (:require [ote.components.http :as http]
            [com.stuartsierra.component :as component]
            [ote.components.service :refer [define-service-component]]
            [ote.db.user-notifications :as user-notifications]
            [specql.core :refer [fetch upsert!] :as specql]
            [compojure.core :refer [routes POST GET]]
            [specql.op :as op]
            [clojure.string :as str]
            [jeesql.core :refer [defqueries]]))

(defn save-notifications! [user db form-data]
  (let [data (merge form-data
                    {::user-notifications/created-by (get-in user [:user :id])
                     ::user-notifications/created (java.sql.Timestamp. (System/currentTimeMillis))
                     ::user-notifications/modified (java.sql.Timestamp. (System/currentTimeMillis))})]
    (upsert! db ::user-notifications/user-notifications data)))

(defn get-notifications [user db]
  (first (fetch db ::user-notifications/user-notifications
                (specql/columns ::user-notifications/user-notifications)
                {::user-notifications/created-by (get-in user [:user :id])})))

(define-service-component Settings {}
  ^{:format :transit}
  (POST "/settings/email-notifications" {form-data :body
                                         user      :user}
      (save-notifications! user db
                           (http/transit-request form-data)))
  ^{:format :transit}
  (GET "/settings/email-notifications" {user :user}
      (get-notifications user db)))
