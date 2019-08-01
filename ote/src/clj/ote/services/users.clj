(ns ote.services.users
  (:require [ote.components.http :as http]
            [compojure.core :refer [routes POST GET]]
            [ote.components.service :refer [define-service-component]]
            [jeesql.core :refer [defqueries]]
            [specql.op :as op]
            [ote.db.user :as user]
            [ote.services.login :as login]
            [ote.db.auditlog :as auditlog]
            [ote.db.tx :as tx :refer [with-transaction]]
            [ote.localization :as localization :refer [tr]]
            [ote.util.encrypt :as encrypt]
            [specql.core :as specql]
            [clojure.set :as set]
            [clojure.string :as str]
            [buddy.hashers :as hashers]
            [ote.localization :as localization]
            [ote.email :as email]
            [ote.util.email-template :as email-template]
            [hiccup.core :as hiccup]
            [taoensso.timbre :as log]
            [ote.time :as time]
            [clj-time.core :as t]
            [clj-time.coerce :as tc]
            [ote.util.throttle :as throttle]
            [clj-time.coerce :as tc])
  (:import (java.util UUID)))

(defqueries "ote/services/user_service.sql")

(defn delete-users-old-token!
  [db user-email]
  (specql/delete! db ::user/email-confirmation-token
    {::user/user-email user-email}))

(defn send-email-verification
  [email-config user-email language email-confirmation-uuid]
  (try
    (localization/with-language
      language
      (email/send!
        email-config
        {:to user-email
         :subject (tr [:email-templates :email-verification :verify-email])
         :body [{:type "text/html;charset=utf-8"
                 :content (str email-template/html-header
                            (hiccup/html (email-template/email-confirmation (tr [:email-templates :email-verification :verify-email]) email-confirmation-uuid)))}]}))
    (catch Exception e
      (log/warn (str "Error while sending verification to:  " user-email " ") e))))

(defn create-confirmation-token!
  [db user-email token]
  (let [expiration-date (time/sql-date (.plusDays (java.time.LocalDate/now) 7))]
    (specql/insert! db ::user/email-confirmation-token
      {::user/user-email user-email ::user/token token ::user/expiration expiration-date})))

(defn- valid-user-save? [{:keys [name email]}]
  (and (user/email-valid? email)
    (string? name)
    (not (str/blank? name))))

(defn save-user!
  [db email-config user form-data admin?]
  (if-not (valid-user-save? form-data)
    {:success? false}
    (with-transaction db
      (let [{new-email :email} form-data
            language (or (:language form-data) :fi)
            email-changed? (not= (:email user) new-email)
            email-taken? (and email-changed?
                           (email-exists? db {:email new-email}))
            login-info (first (login/fetch-login-info db {:email (:email user)}))
            password-incorrect? (if admin?
                                  false
                                  (or (str/blank? (:current-password form-data))
                                    (not (hashers/check (:current-password form-data)
                                           (encrypt/passlib->buddy (:password login-info))))))]
        (if
          ;; Password incorrect, username or email taken => return errors to form
          (or email-taken? password-incorrect?)
          {:success? false
           :email-taken (when email-taken? new-email)
           :password-incorrect? password-incorrect?}

          ;; Request is valid, do update
          (let [_ (specql/update! db ::user/user
                       (merge
                         {;; :user/name intentionally not set because it shall not be modified, previously it was possible all the way from UI
                          ::user/fullname (:name form-data)}
                         ;; If new password provided, change it
                         (when-not (str/blank? (:password form-data))
                           {::user/password (encrypt/buddy->passlib (encrypt/encrypt (:password form-data)))})
                         (when email-changed?
                           {::user/email new-email
                            ::user/email-confirmed? false
                            ::user/confirmation-time nil}))
                       {::user/id (:id user)})
                UUID (str (UUID/randomUUID))]
            ;; When email is changed also send new confirmation email
            (if email-changed?
              (do (delete-users-old-token! db (:email user)) ;; If the user changes email multiple times, delete old tokens
                  (create-confirmation-token! db new-email UUID)
                  (send-email-verification email-config new-email language UUID)
                  {:success? true
                   :email-changed? true
                   :new-email new-email})
              {:success? true})))))))


(defn save-user [db email auth-tkt-config user form-data]
  (let [result (save-user! db email user form-data false)]
    (if (:success? result)
      ;; User updated, re-login immediately with updated info
      (if-not (:email-changed? result)
        (login/login db auth-tkt-config
          {:email (:email form-data)
           :password (if (str/blank? (:password form-data))
                       (:current-password form-data)
                       (:password form-data))})
        (http/transit-response result 200))

      ;; Failed, return errors to form
      (http/transit-response result 400))))

(defn fetch-user-data
  [db id]
  (let [user (first (specql/fetch db
                      ::user/user
                      #{::user/id ::user/fullname ::user/email ::user/name ::user/email-confirmed?}
                      {::user/id id}))
        user (set/rename-keys user
               {::user/id :id
                ::user/fullname :name
                ::user/email :email
                ::user/name :username
                ::user/email-confirmed? :email-confirmed?})]
    user))

(defn handle-fetch-user-data
  [db id]
  (let [user (fetch-user-data db id)]
    (if (nil? user)
      (http/transit-response {} 404)
      (http/transit-response user 200))))

(defn edit-user-info
  [db email requester user-id form-data]
  (let [user (fetch-user-data db user-id)
        result (save-user! db email user form-data (get-in requester [:user :admin?]))
        auditlog {::auditlog/event-type :modify-user
                  ::auditlog/event-attributes
                  [{::auditlog/name "editing-user", ::auditlog/value (str (get-in requester [:user :id]))}
                   {::auditlog/name "edited-user", ::auditlog/value (str (:id user))}]
                  ::auditlog/event-timestamp (java.sql.Timestamp. (System/currentTimeMillis))
                  ::auditlog/created-by (get-in requester [:user :id])}
        _ (specql/insert! db ::auditlog/auditlog auditlog)]
    (log/info (get-in requester [:user :email]) " requested to edit user: " user-id)
    (if (:success? result)
      (http/transit-response result 200)
      (http/transit-response result 400))))

(defn manage-new-confirmation
  "We always want to send the same response so someone can't get all the user emails by spamming this endpoint"
  [db email-config form-data]
  (with-transaction db
    (throttle/with-throttle-ms 1000
      (let [user-email (:email form-data)
            language (:language form-data)
            user-confirmed? (::user/email-confirmed?
                              (first (specql/fetch db ::user/user
                                       #{::user/email-confirmed?}
                                       {::user/email user-email})))
            confirmation-token (str (UUID/randomUUID))]
        (if (or user-confirmed? (nil? user-confirmed?))
          (http/transit-response {:success true} 200)
          (do
            (delete-users-old-token! db user-email)
            (create-confirmation-token! db user-email confirmation-token)
            (send-email-verification email-config user-email language confirmation-token)
            (http/transit-response {:success true} 200)))))))

(defn manage-confirm-email-address
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
      (do
        (log/info "Email confirmation failed with token: " token)
        (http/transit-response {:message :email-validation-failure} 401)))))

(defn clean-old-email-confirmation-tokens
  [db]
  (specql/delete! db ::user/email-confirmation-token
    {::user/expiration (op/< (tc/to-sql-date (t/now)))}))

(define-service-component UsersService
  {:fields [auth-tkt-config]
   :dependencies {email :email}}

  ^:unauthenticated
  (POST "/confirm-email" {form-data :body
                          user :user}
    (let [form-data (http/transit-request form-data)]
      (#'clean-old-email-confirmation-tokens db)
      (#'manage-confirm-email-address db form-data)))

  ^:unauthenticated
  (POST "/token/validate" {form-data :body
                           user :user}
    ;; This ns validates user invite to operator
    (let [token (:token (http/transit-request form-data))
          operator (first (fetch-operator-info db {:token token}))]
      (if (some? operator)
        (http/transit-response operator)
        (http/transit-response "Invalid token" 400))))

  ^:unauthenticated
  (POST "/send-email-confirmation" {form-data :body
                                    user :user}
    (let [form-data (http/transit-request form-data)]
      (#'manage-new-confirmation db email form-data)))

  ^{:unauthenticated false}
  (POST "/save-user" {form-data :body
                      user :user}
    (#'save-user db email auth-tkt-config (:user user)
      (http/transit-request form-data)))

  ^{:unauthenticated false}
  (GET "/user/:id" {user :user
                    params :params :as request}
    (if (and (:admin? (:user user)) (:id params))
      (handle-fetch-user-data db (:id params))
      (http/transit-response {} 401)))

  ^{:unauthenticated false}
  (POST "/user/:id" {user :user
                     params :params
                     form-data :body :as request}
    (if (:admin? (:user user))
      (edit-user-info db email user (:id params) (http/transit-request form-data))
      (http/transit-response {} 401))))
