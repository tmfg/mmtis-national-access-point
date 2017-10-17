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

;; Current db for tests
(defonce ^:dynamic *db* nil)

;; Current OTE system
(defonce ^:dynamic *ote* nil)

(def ote-db-url {:user "napote"
                 :dbtype "postgresql"
                 :dbname "napotetest"
                 :host "localhost"
                 :port 5432})

(defmacro with-test-db [& body]
  `(let [db# ote-db-url]
     (tx/with-transaction db#
       (jdbc/db-set-rollback-only! db#)
       (binding [*db* db#]
         ~@body))))

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
                              :db (db/->TestDatabase (:connection *db*))
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
