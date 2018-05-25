(ns ote.services.login
  "Login related services"
  (:require [buddy.hashers :as hashers]
            [clojure.string :as str]
            [jeesql.core :refer [defqueries]]
            [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [compojure.core :refer [routes POST]]
            [ote.nap.cookie :as cookie]
            [ote.nap.users :as users]
            [ote.services.transport :as transport])
  (:import (java.util Base64 Base64$Decoder)))

(defqueries "ote/services/login.sql")

(defn base64->hex
  "Convert base64 encoded string to hex"
  [base64]
  (let [bytes (.decode (Base64/getDecoder)
                       ;; Passlib uses unconventional \. character instead of \+ in base64
                       (str/replace base64 #"\." "+"))]
    (str/join (map #(format "%02x" %) bytes))))

(defn passlib->buddy
  "Convert encrypted password from Python passlib format to buddy format."
  [hash]
  ;; Passlib format:
  ;; $pbkdf2-sha512$iterations$salt$hash
  ;;
  ;; buddy format:
  ;; pbkdf2+sha512$salt$iterations$hash
  ;;
  ;; passlib uses base64 encoding and buddy uses hex strings
  (let [[_ alg iterations salt password] (str/split hash #"\$")]
    (assert (= alg "pbkdf2-sha512"))
    (format "pbkdf2+sha512$%s$%s$%s"
            (base64->hex salt)
            iterations
            (base64->hex password))))

(defn with-auth-tkt [response auth-tkt-value domain]
  (update response :headers
          assoc "Set-Cookie" (if (nil? domain)
                               (str "auth_tkt=" auth-tkt-value "; Path=/; HttpOnly")
                               ;; Two cookies are required for wild card and exact domain
                               ;; similar to ckan cookie configuration
                               [(str "auth_tkt=" auth-tkt-value "; Path=/; HttpOnly"
                                     "; Domain=." domain "; Secure")
                                (str "auth_tkt=" auth-tkt-value "; Path=/; HttpOnly"
                                     "; Domain=" domain "; Secure")])))

(defn login [db auth-tkt-config
             {:keys [email password] :as credentials}]
  (let [login-info (first (fetch-login-info db {:email email}))]
    (if login-info
      (if (hashers/check password
                         (passlib->buddy (:password login-info)))
        ;; User was found and password is correct, return user info
        (with-auth-tkt
          (http/transit-response
           {:success? true
            :session-data
            (let [user (users/find-user db (:name login-info))]
              (transport/get-user-transport-operators-with-services db (:groups user) (:user user)))})
          (cookie/unparse "0.0.0.0" (:shared-secret auth-tkt-config)
                          {:digest-algorithm (:digest-algorithm auth-tkt-config)
                           :timestamp (java.util.Date.)
                           :user-id (:name login-info)
                           :user-data ""})
          (:domain auth-tkt-config))

        ;; No need to hide if the error was in the email or the password
        ;; the registration page can be used to check if an email has an account
        (http/transit-response {:error :incorrect-password}))
      (http/transit-response {:error :no-such-user}))))

(defn logout [auth-tkt-config]
  (with-auth-tkt (http/transit-response :ok) "" (:domain auth-tkt-config)))

(defn- login-routes [db auth-tkt-config]
  (routes
   (POST "/login" {form-data :body}
         (#'login db auth-tkt-config
                (http/transit-request form-data)))
   (POST "/logout" []
         (logout auth-tkt-config))))

(defrecord LoginService [auth-tkt-config]
  component/Lifecycle
  (start [{db :db
           http :http
           :as this}]
    (assoc this ::stop
           (http/publish! http {:authenticated? false}
                          (login-routes db auth-tkt-config))))
  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
