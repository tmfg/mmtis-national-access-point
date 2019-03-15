(ns ote.components.db
  "Database connection pool component"
  (:import (com.zaxxer.hikari HikariConfig HikariDataSource))
  (:require [com.stuartsierra.component :as component]
            [clojure.java.jdbc :as jdbc]
            [clojure.string :as str]))

(defn- hikari-datasource [{:keys [url username password] :as config}]
  (HikariDataSource.
   (doto (HikariConfig.)
     (.setJdbcUrl url)
     (.setUsername username)
     (.setPassword password)
     (.addDataSourceProperty "cachePrepStmts" "true")
     (.addDataSourceProperty "prepStmtCacheSize" "250")
     (.addDataSourceProperty "prepStmtCacheSqlLimit" "2048"))))

(defrecord Database [datasource config]
  component/Lifecycle
  (start [this]
    (assoc this :datasource (hikari-datasource config)))
  (stop [{ds :datasource :as this}]
    (.close ds)
    (assoc this :datasource nil)))

(defrecord TestDatabase [connection]
  component/Lifecycle
  (start [this] this)
  (stop [this] this))

(defn database
  "Create a database component with the given `config`."
  [config]
  (->Database nil config))
