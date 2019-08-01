(ns ote.services.register
  (:require [compojure.core :refer [routes GET POST DELETE]]
            [com.stuartsierra.component :as component]
            [ote.db.user :as user]
            [ote.components.http :as http]
            [specql.core :as specql]
            [ote.util.encrypt :as encrypt]
            [ote.services.users :as user-service]
            [ote.db.tx :as tx :refer [with-transaction]]
            [jeesql.core :refer [defqueries]]
            [ote.util.feature :as feature]
            [clojure.string :as str]
            [ote.services.transport :as transport]
            [taoensso.timbre :as log])
  (:import (java.util UUID)))

(defqueries "ote/services/register.sql")
(defqueries "ote/services/user_service.sql")

(defn valid-registration? [{:keys [username name email password]}]
  (and (user/password-valid? password)
    (user/email-valid? email)
    (user/username-valid? username)
    (string? name) (not (str/blank? name))))

(defn- register-user! [db auth-tkt-config {:keys [username name email password token] :as form-data}]
  (if-not (valid-registration? form-data)
    ;; Check errors that should have been checked on the form
    {:success? false}
    (let [username-taken? (username-exists? db {:username username})
          email-taken? (email-exists? db {:email email})
          group-info (when token
                       (first (fetch-operator-info db {:token token})))]
      (if (or username-taken? email-taken?)
        ;; Username or email taken, return errors to form
        {:success? false
         :username-taken (when username-taken? username)
         :email-taken (when email-taken? email)}
        ;; Registration data is valid and username/email is not taken
        (do
          (let [user-id (str (UUID/randomUUID))
                new-user (specql/insert! db ::user/user
                           {::user/id user-id
                            ::user/name user-id ;; Username not used anymore, use internally row id
                            ::user/fullname name
                            ::user/email email
                            ::user/password (encrypt/buddy->passlib (encrypt/encrypt password))
                            ::user/created (java.util.Date.)
                            ::user/state "active"
                            ::user/sysadmin false
                            ::user/email-confirmed? false
                            ::user/apikey (str (UUID/randomUUID))
                            ::user/activity_streams_email_notifications false})]
            (when (and token group-info)                    ;; If the user doesn't have a token or group-info they can register, but aren't added to any group
              (transport/create-member! db (::user/id new-user) (:ckan-group-id group-info))
              (specql/delete! db ::user/user-token
                {::user/token token})
              (log/info "New user (" email ") registered with token from " (:name group-info))))
          {:success? true})))))

(defn register [db email auth-tkt-config form-data]
  (with-transaction db
    (feature/when-enabled :ote-register
      (let [result (register-user! db auth-tkt-config form-data)
            email-confirmation-token (str (UUID/randomUUID))
            user-email (:email form-data)
            language (:language form-data)]
        (if (:success? result)
          ;; User created, log in immediately with the user info
          (do (user-service/create-confirmation-token! db (:email form-data) email-confirmation-token)
              (user-service/send-email-verification email user-email language email-confirmation-token)
              (http/transit-response result 201))
          ;; registeration failed send errors
          (http/transit-response result 400))))))

(defn- register-routes
  "Unauthenticated routes"
  [db email config]
  (let [auth-tkt-config (get-in config [:http :auth-tkt])]
    (routes

      (POST "/register" {form-data :body
                         user :user}
        (if user
          ;; Trying to register while logged in
          (http/transit-response {:success? false
                                  :message :already-logged-in} 400)
          (#'register db email auth-tkt-config
            (http/transit-request form-data)))))))

(defrecord Register [config]
  component/Lifecycle
  (start [{:keys [db email http] :as this}]
    (assoc
      this ::stop
           [(http/publish! http {:authenticated? false} (register-routes db email config))]))
  (stop [{stop ::stop :as this}]
    (doseq [s stop]
      (s))
    (dissoc this ::stop)))
