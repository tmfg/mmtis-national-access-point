(ns ote.komponentit.db
  "Tietokannan yhteyspool"
  (:import (com.zaxxer.hikari HikariConfig HikariDataSource))
  (:require [com.stuartsierra.component :as component]
            [clojure.java.jdbc :as jdbc]
            [clojure.string :as str]))

(defn- hikari-datasource [{:keys [url username password] :as asetukset}]
  (HikariDataSource.
   (doto (HikariConfig.)
     (.setJdbcUrl url)
     (.setUsername username)
     (.setPassword password)
     (.addDataSourceProperty "cachePrepStmts" "true")
     (.addDataSourceProperty "prepStmtCacheSize" "250")
     (.addDataSourceProperty "prepStmtCacheSqlLimit" "2048"))))

(defrecord Tietokanta [datasource asetukset]
  component/Lifecycle
  (start [this]
    (assoc this :datasource (hikari-datasource asetukset this)))
  (stop [{ds :datasource :as this}]
    (.close ds)
    (assoc this :datasource nil)))

(defn tietokanta
  "Luo tietokantakomponentin annetuilla asetuksilla"
  [asetukset]
  (->Database nil config))
