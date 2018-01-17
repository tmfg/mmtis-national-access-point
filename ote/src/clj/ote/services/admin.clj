(ns ote.services.admin
  "Backend services for admin functionality."
  (:require [ote.components.http :as http]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as log]
            [compojure.core :refer [POST routes]]
            [ote.nap.users :as nap-users]))

(defn- require-admin-user [route user]
  (when (not (:admin? user))
    (log/warn "Non admin user tried to call " route ", user: " user)
    (throw (SecurityException. "admin only"))))

(defn- admin-service [route {user :user
                             form-data :body :as req} db handler]
  (require-admin-user route user)
  (http/transit-response
   (handler db user (http/transit-request form-data))))

(defn- list-users [db user query]
  (nap-users/list-users db ))

(defn- admin-routes [db http]
  (routes
   (POST "/admin/users" req (admin-service "users" req db #'list-users))))

(defrecord Admin []
  component/Lifecycle
  (start [{db :db http :http :as this}]
    (assoc this ::stop
           (http/publish! http (admin-routes db http))))

  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
