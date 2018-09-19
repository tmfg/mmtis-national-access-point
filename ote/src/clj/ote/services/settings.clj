(ns ote.services.settings
  "Backend services for settings."
  (:require [ote.components.http :as http]
            [com.stuartsierra.component :as component]
            [ote.db.user-notifications :as user-notifications]
            [specql.core :refer [fetch upsert!] :as specql]
            [compojure.core :refer [routes POST GET]]
            [specql.op :as op]
            [clojure.string :as str]
            [jeesql.core :refer [defqueries]]))

(defn save-notifications! [user db form-data]
  (let [data (-> form-data
                 (assoc ::user-notifications/created-by (get-in user [:user :id]))
                 (assoc ::user-notifications/created (java.sql.Timestamp. (System/currentTimeMillis)))
                 (assoc ::user-notifications/modified (java.sql.Timestamp. (System/currentTimeMillis))))]
    (upsert! db ::user-notifications/user-notifications data)))

(defn get-notifications [user db]
  (first (fetch db ::user-notifications/user-notifications
                (specql/columns ::user-notifications/user-notifications)
                {::user-notifications/created-by (get-in user [:user :id])})))

(defn setting-routes [db]
  (routes
    (POST "/settings/email-notifications" {form-data :body
                                           user      :user}
      (http/transit-response
        (save-notifications! user db
                             (http/transit-request form-data))))

    (GET "/settings/email-notifications" {user :user}
      (http/transit-response
        (get-notifications user db)))))

(defrecord Settings []
  component/Lifecycle
  (start [{db :db http :http :as this}]
    (assoc this ::stop
      (http/publish! http {:authenticated? false} (setting-routes db))))

  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
