(ns ote.test
  "Test utilities and fixtures for OTE backend tests."
  (:require  [clojure.test :as t]
             [ote.db.tx :as tx]
             [clojure.java.jdbc :as jdbc]
             [com.stuartsierra.component :as component]
             [ote.components.db :as db]
             [ote.components.http :as http]
             [clj-http.client :as http-client]
             [taoensso.timbre :as log]
             [ote.nap.cookie :as nap-cookie]
             [ote.transit :as transit])
  (:import (org.apache.http.client CookieStore)
           (org.apache.http.cookie Cookie)))

;; Don't log debugs to tests
(log/merge-config!
 {:appenders {:println {:min-level :info}}})

;; Current db for tests
(defonce ^:dynamic *db* nil)

;; Current OTE system
(defonce ^:dynamic *ote* nil)


(def db-url {:user "napote"
             :dbtype "postgresql"
             :dbname "napote"
             :host "localhost"
             :port 5432})

(def test-db-config {:url "jdbc:postgresql://localhost:5432/napotetest"
                     :username "napote"
                     :password ""})


(defn- kill-db-connections! [db database-name]
  (jdbc/query db (str "SELECT pg_terminate_backend(pg_stat_activity.pid)"
                      "  FROM pg_stat_activity"
                      " WHERE pg_stat_activity.datname = '" database-name "' AND pid <> pg_backend_pid()")))

(defn- create-test-db! []
  (kill-db-connections! db-url "napotetest")
  (kill-db-connections! db-url "napotetest_template")
  (with-open [c (jdbc/get-connection db-url)]
    (let [s (.createStatement c)]
      (.executeUpdate s "DROP DATABASE IF EXISTS napotetest")
      (.executeUpdate s "CREATE DATABASE napotetest TEMPLATE napotetest_template"))))

(defmacro with-test-db [& body]
  `(do
     (create-test-db!)
     ~@body))

(defn db-fixture []
  (fn [tests]
    (with-test-db (tests))))

(defn- port []
  (let [s (doto (java.net.ServerSocket. 0)
            (.setReuseAddress true))]
    (try
      (.getLocalPort s)
      (finally (.close s)))))

(def auth-tkt-config {:shared-secret "test" :max-age-in-seconds 60})

(defn system-fixture [& system-map-entries]
  (fn [tests]
    (with-test-db
      (binding [*ote* (component/start
                       (apply component/system-map
                              :db (db/database test-db-config)
                              :http (component/using
                                     (http/http-server {:port (port)
                                                        :auth-tkt auth-tkt-config})
                                     [:db])
                              system-map-entries))]
        (tests)
        (component/stop *ote*)))))

(defn- url-for-path [path]
  (str "http://localhost:" (get-in *ote* [:http :config :port]) "/" path))

(defn- cookie-store-for-user [user]
  (reify CookieStore
    (getCookies [_]
      [(reify Cookie
         (getName [_] "auth_tkt")
         (getValue [_]
           (nap-cookie/unparse "0.0.0.0" "test"
                               {:digest-algorithm "MD5"
                                :timestamp (java.util.Date.)
                                :user-id user
                                :user-data ""}))
         (getDomain [_] "localhost")
         (isExpired [_ _] false)
         (getPath [_] "/")
         (getPorts [_] (int-array [(get-in *ote* [:http :config :port])]))
         (getVersion [_] 2)
         (isPersistent [_] true)
         (isSecure [_] false)
         (getComment [_] nil)
         (getCommentURL [_] nil))])))

(defn- read-transit-response [res]
  (if (= (:status res) 200)
    (assoc res :transit (transit/transit->clj (:body res)))
    res))

(defn http-get [user path]
  (-> path
      url-for-path
      (http-client/get {:cookie-store (cookie-store-for-user user)})
      read-transit-response))

(defn http-post [user path payload]
  (-> path url-for-path
      (http-client/post {:body (transit/clj->transit payload)
                         :cookie-store (cookie-store-for-user user)})
      read-transit-response))
