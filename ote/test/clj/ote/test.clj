(ns ote.test
  "Test utilities and fixtures for OTE backend tests."
  (:require  [clojure.test :as t]
             [ote.db.tx :as tx]
             [clojure.java.jdbc :as jdbc]
             [com.stuartsierra.component :as component]
             [ote.components.db :as db]
             [ote.components.http :as http]
             [org.httpkit.client :as http-client]
             [taoensso.timbre :as log]))

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
        (println "*OTE*: " (keys  *ote*))
        (tests)
        (component/stop *ote*)))))

(defn http-get [path payload]
  (let [url (str "http://localhost:" (get-in *ote* [:http :config :port]) "/" path)]
    (log/info "Fetching URL: " url)
    @(http-client/get url)))
