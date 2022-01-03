(ns ote.services.login
  "Login related services"
  (:require [buddy.hashers :as hashers]
            [hiccup.core :refer [html]]
            [ote.components.http :as http]
            [ote.middleware.throttler :as throttler]
            [compojure.core :refer [routes POST wrap-routes]]
            [ote.nap.cookie :as cookie]
            [ote.nap.users :as users]
            [jeesql.core :refer [defqueries]]
            [ote.util.encrypt :as encrypt]
            [ote.services.transport-operator :as transport-operator]
            [ote.components.service :refer [define-service-component]]
            [ote.db.tx :as tx :refer [with-transaction]]
            [specql.core :as specql]
            [ote.db.user :as user]
            [taoensso.timbre :as log]
            [ote.db.modification :as modification]
            [ote.localization :as localization :refer [tr]]
            [ote.email :as email]
            [ote.util.email-template :as email-template]
            [specql.op :as op]
            [clj-time.core :as t]
            [clj-time.coerce :as tc]
            [ote.util.throttle :refer [with-throttle-ms]]
            [clojure.string :as str]))

(defqueries "ote/services/login.sql")

(defn- with-auth-tkt [response auth-tkt-value domain]
  (update response :headers
          assoc "Set-Cookie" (if (nil? domain)
                               [(str "auth_tkt=" auth-tkt-value "; Path=/; HttpOnly")]
                               ;; Three cookies are required to match ckan cookie configuration
                               [(str "auth_tkt=" auth-tkt-value "; Path=/; HttpOnly; Secure")
                                (str "auth_tkt=" auth-tkt-value "; Path=/; HttpOnly"
                                     "; Domain=" domain "; Secure")
                                (str "auth_tkt=" auth-tkt-value "; Path=/; HttpOnly"
                                     "; Domain=." domain "; Secure")])))

(defn login [db auth-tkt-config
             {:keys [email password] :as credentials}]
  (if-let [login-info (first (fetch-login-info db {:email email}))]
    (if (hashers/check password
          (encrypt/passlib->buddy (:password login-info)))
      (if (:email-confirmed? login-info)                    ;;If email not confirmed send proper error
        ;; User was found and password is correct, return user info
        (with-auth-tkt
          (http/transit-response
            {:success? true
             :session-data
             (let [user (users/find-user db (:id login-info))]
               (transport-operator/get-user-transport-operators-with-services db (:groups user) (:user user)))}
            200)
          (cookie/unparse "0.0.0.0" (:shared-secret auth-tkt-config)
            {:digest-algorithm (:digest-algorithm auth-tkt-config)
             :timestamp (java.util.Date.)
             :user-id (:id login-info)
             :user-data ""})
          (:domain auth-tkt-config))

        (http/transit-response {:error :unconfirmed-email} 401)) ;; This could be 403 instead
      ;; Login shall indicate error in a general way and not reveal which credential was invalid
      (http/transit-response {:error :login-error} 400))
    (http/transit-response {:error :login-error} 400)))

(defn- logout [auth-tkt-config]
  (with-auth-tkt (http/transit-response :ok) "" (:domain auth-tkt-config)))

(defn- request-password-reset! [db {:keys [email]}]
  (tx/with-transaction db
    (when-let [user (first (fetch-login-info db {:email email}))]
      (log/info "User " (:id user) " requested password reset.")
      {:user user
       :password-reset-request (specql/insert! db ::user/password-reset-request
                                               {::user/reset-key (java.util.UUID/randomUUID)
                                                ::modification/created-by (:id user)})})))

(defn- send-password-reset-email [email {:keys [user password-reset-request]} language]
  (log/debug "Sending password reset email to " user)
  (localization/with-language language
                              (email/send! email
                                           {:to (:email user)
                                            :subject (tr [:email-templates :password-reset :subject])
                                            :body [{:type "text/html;charset=utf-8" :content (str email-template/html-header
                                                                                                  (html (email-template/reset-password
                                                                                                          (tr [:email-templates :password-reset :subject])
                                                                                                          (::user/reset-key password-reset-request)
                                                                                                          user)))}]})))

(defn request-password-reset [db email form-data]
  (with-throttle-ms 1000 ; always take 1sec to prevent spamming requests
    (try
      (when-let [reset-request (request-password-reset! db form-data)]
        (send-password-reset-email email reset-request (:language form-data)))
      (catch Exception e
        (log/error e "Unable to send password reset email")))
    ;; Always send :ok back, no matter what
    :ok))

(defn- reset-password! [db reset-request new-password]
  (log/info "Reset password:" reset-request)
  (specql/update! db ::user/user
                  {::user/password (encrypt/buddy->passlib (encrypt/encrypt new-password))}
                  {::user/id (::modification/created-by reset-request)})
  (specql/update! db ::user/password-reset-request
                  {::user/used (java.util.Date.)}
                  {::user/request-id (::user/request-id reset-request)}))

(defn- reset-password [db {:keys [id key new-password] :as form-data}]
  (with-throttle-ms 1000
    (if-not (user/password-valid? new-password)
      {:success? false :error :invalid-new-password}
      (with-transaction db
        (if-let [reset-request (first
                                (specql/fetch db ::user/password-reset-request
                                              (specql/columns ::user/password-reset-request)
                                              {::modification/created-by id
                                               ::user/reset-key (java.util.UUID/fromString key)
                                               ;; Only consider requests created within 1 hour
                                               ::modification/created (op/>= (tc/to-sql-date (t/minus (t/now)
                                                                                                      (t/hours 1))))
                                               ;; that have not been used yet
                                               ::user/used op/null?}))]
          ;; Found a valid password reset request
          (do (reset-password! db reset-request new-password)
              {:success? true})

          ;; No valid password reset request
          (do
            (log/warn "Invalid password reset request, user: " id ", key: " key)
            {:success? false :error :password-reset-request-not-found}))))))

(define-service-component LoginService
  {:fields [auth-tkt-config]
   :dependencies {email :email}}

  ^:unauthenticated
  (-> (POST "/login" {form-data :body}
        (#'login db auth-tkt-config
          (http/transit-request form-data)))
      (wrap-routes throttler/throttle))

  ^:unauthenticated
  (POST "/logout" []
        (logout auth-tkt-config))

  ^{:unauthenticated true :format :transit}
  (POST "/request-password-reset" {form-data :body}
        (request-password-reset db email (http/transit-request form-data)))

  ^{:unauthenticated true :format :transit}
  (POST "/reset-password" {form-data :body}
        (reset-password db (http/transit-request form-data))))
