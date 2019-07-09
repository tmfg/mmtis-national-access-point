(ns ote.services.register
  (:require [compojure.core :refer [routes GET POST DELETE]]
            [com.stuartsierra.component :as component]
            [ote.db.user :as user]
            [ote.localization :as localization]
            [ote.components.http :as http]
            [specql.core :as specql]
            [specql.op :as op]
            [ote.services.login :as login]
            [ote.db.tx :as tx :refer [with-transaction]]
            [jeesql.core :refer [defqueries]]
            [ote.util.feature :as feature]
            [clojure.string :as str]
            [ote.services.transport :as transport]
            [taoensso.timbre :as log]
            [ote.email :as email]
            [hiccup.core :as hiccup]
            [ote.util.email-template :as email-template]
            [ote.time :as time]
            [clj-time.core :as t]
            [clj-time.coerce :as tc]
            [ote.util.throttle :as throttle])
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
          (let [new-user (specql/insert! db ::user/user
                           {::user/id (str (UUID/randomUUID))
                            ::user/name username
                            ::user/fullname name
                            ::user/email email
                            ::user/password (login/buddy->passlib (login/encrypt password))
                            ::user/created (java.util.Date.)
                            ::user/state "active"
                            ::user/sysadmin false
                            ::user/email-confirmed? false
                            ::user/apikey (str (UUID/randomUUID))
                            ::user/activity_streams_email_notifications false})]
            (when (and token group-info)                ;; If the user doesn't have a token or group-info they can register, but aren't added to any group
              (transport/create-member! db (::user/id new-user) (:ckan-group-id operator-info))
              (specql/delete! db ::user/user-token
                {::user/token token})
              (log/info "New user (" email ") registered with token from " (:name operator-info))))
          {:success? true})))))

(defn- send-email-verification
  [email-config user-email language email-confirmation-uuid]
  (let [title "testi"]
    (try
      (localization/with-language
        language
        (email/send!
          email-config
          {:to user-email
           :subject title
           :body [{:type "text/html;charset=utf-8"
                   :content (str email-template/html-header
                              (hiccup/html (email-template/email-confirmation title email-confirmation-uuid)))}]}))
      (catch Exception e
        (log/warn (str "Error while sending verification to:  " user-email " ") e)))))

(defn create-confirmation-token!
  [db user-email token]
  (let [expiration-date (time/sql-date (.plusDays (java.time.LocalDate/now) 7))]
    (specql/insert! db ::user/email-confirmation-token
      {::user/user-email user-email ::user/token token ::user/expiration expiration-date})))

(defn register [db email auth-tkt-config form-data]
  (with-transaction db
    (feature/when-enabled :ote-register
      (let [result (register-user! db auth-tkt-config form-data)
            email-confirmation-token (str (UUID/randomUUID))
            user-email (:email form-data)
            language (:language form-data)]
        (if (:success? result)
          ;; User created, log in immediately with the user info
          (do (create-confirmation-token! db (:email form-data) email-confirmation-token)
              (send-email-verification email user-email language email-confirmation-token)
              (http/transit-response result 200))
          ;; registeration failed send errors
          (http/transit-response {:message :registeration-failure} 400))))))

(defn delete-users-old-token!
  [db user-email]
  (specql/delete! db ::user/email-confirmation-token
    {::user/user-email user-email}))

(defn manage-new-confirmation
  "We always want to send the same response so someone can't get all the user emails by spamming this endpoint"
  [db email-config form-data]
  (throttle/with-throttle-ms 1000
    (let [user-email (:email form-data)
          language (:language form-data)
          user-confirmed? (::user/email-confirmed?
                            (first (specql/fetch db ::user/user
                                     #{::user/email-confirmed?}
                                     {::user/email user-email})))
          confirmation-token (str (UUID/randomUUID))]
      (if user-confirmed?
        (http/transit-response :success true)
        (do
          (delete-users-old-token! db user-email)
          (create-confirmation-token! db user-email confirmation-token)
          (send-email-verification email-config user-email language confirmation-token)
          (http/transit-response :success true))))))

(defn mange-confirm-email-address
  [db form-data]
  (let [token (:token form-data)
        confirmation (first
                       (specql/fetch db ::user/email-confirmation-token
                         (specql/columns ::user/email-confirmation-token)
                         {::user/token token
                          ::user/expiration (op/>= (tc/to-sql-date (t/now)))}))]
    (if (not-empty confirmation)
      (do (specql/update! db ::user/user
            {::user/email-confirmed? true
             ::user/confirmation-time (tc/to-sql-date (t/now))}
            {::user/email (::user/user-email confirmation)})
          (specql/delete! db ::user/email-confirmation-token
            {::user/token token})
          (http/transit-response {:message :email-validation-success} 200))
      (http/transit-response {:message :email-validation-failure} 401))))

(defn- register-routes
  "Unauthenticated routes"
  [db email config]
  (let [auth-tkt-config (get-in config [:http :auth-tkt])]
    (routes
      (POST "/token/validate" {form-data :body
                               user :user}
        (let [token (:token (http/transit-request form-data))
              operator (first (fetch-operator-info db {:token token}))]
          (if (some? operator)
            (http/transit-response operator)
            (http/transit-response "Invalid token" 400))))

      (POST "/confirm-email" {form-data :body
                              user :user}
        (let [form-data (http/transit-request form-data)]
          (#'mange-confirm-email-address db form-data)))

      (POST "/send-email-confirmation" {form-data :body
                                        user :user}
        (let [form-data (http/transit-request form-data)]
          (#'manage-new-confirmation db email form-data)))

      (POST "/register" {form-data :body
                         user :user}
        (if user
          ;; Trying to register while logged in
          (http/transit-response {:success? false})
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
