(ns ote.services.register
  (:require [compojure.core :refer [routes GET POST DELETE]]
            [com.stuartsierra.component :as component]
            [ote.db.user :as user]
            [ote.components.http :as http]
            [specql.core :as specql]
            [ote.services.login :as login]
            [ote.db.tx :as tx :refer [with-transaction]]
            [jeesql.core :refer [defqueries]]
            [ote.util.feature :as feature]
            [clojure.string :as str]
            [ote.services.transport :as transport]
            [taoensso.timbre :as log])
  (:import (java.util UUID)))

(defqueries "ote/services/register.sql")

(defn valid-registration? [{:keys [username name email password]}]
  (and (user/password-valid? password)
    (user/email-valid? email)
    (user/username-valid? username)
    (string? name) (not (str/blank? name))))

(defn- register-user! [db auth-tkt-config {:keys [username name email password token] :as form-data}]
  (if-not (valid-registration? form-data)
    ;; Check errors that should have been checked on the form
    {:success? false}
    (with-transaction db
      (let [username-taken? (username-exists? db {:username username})
            email-taken? (email-exists? db {:email email})
            operator-info (when token
                            (first (fetch-operator-info db {:token token})))]
        (if (or username-taken? email-taken?)
          ;; Username or email taken, return errors to form
          {:success? false
           :username-taken (when username-taken? username)
           :email-taken (when email-taken? email)}
          ;; Registration data is valid and username/email is not taken
          (do
            (let [new-user (specql/insert! db ::user/user
                             {::user/id (str (UUID/randomUUID))
                              ::user/name username
                              ::user/fullname name
                              ::user/email email
                              ::user/password (login/buddy->passlib (login/encrypt password))
                              ::user/created (java.util.Date.)
                              ::user/state "active"
                              ::user/sysadmin false
                              ::user/apikey (str (UUID/randomUUID))
                              ::user/activity_streams_email_notifications false})]
              (when (and token operator-info)
                (transport/create-member! db (::user/id new-user) (:ckan-group-id operator-info))
                (specql/delete! db ::user/user-token
                  {::user/token token})
                (log/info "New user (" email ") registered with token from " (:name operator-info))))
            {:success? true}))))))

(defn register [db auth-tkt-config form-data]
  (feature/when-enabled :ote-register
    (let [result (register-user! db auth-tkt-config form-data)]
      (if (:success? result)
        ;; User created, log in immediately with the user info
        (login/login db auth-tkt-config form-data)

        ;; Registration failed, return errors
        (http/transit-response result)))))


(defn- register-routes
  "Unauthenticated routes"
  [db config]
  (let [auth-tkt-config (get-in config [:http :auth-tkt])]
    (routes
      (POST "/token/validate" {form-data :body
                               user :user}
        (let [token (:token (http/transit-request form-data))
              operator (first (fetch-operator-info db {:token token}))]
          (if (some? operator)
            (http/transit-response operator)
            (http/transit-response "Invalid token" 400))))

      (POST "/register" {form-data :body
                         user :user}
        (if user
          ;; Trying to register while logged in
          (http/transit-response {:success? false})
          (#'register db auth-tkt-config
            (http/transit-request form-data)))))))

(defrecord Register [config]
  component/Lifecycle
  (start [{:keys [db http] :as this}]
    (assoc
      this ::stop
           [(http/publish! http {:authenticated? false} (register-routes db config))]))
  (stop [{stop ::stop :as this}]
    (doseq [s stop]
      (s))
    (dissoc this ::stop)))
