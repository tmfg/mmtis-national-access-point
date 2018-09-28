(ns ote.integration.import.gtfs_test
  (:require [ote.integration.import.gtfs :as import-gtfs]
            [clojure.test :as t :refer [use-fixtures deftest is testing]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]
            [ote.test :refer [system-fixture *ote* http-post http-get sql-query sql-execute!]]
            [com.stuartsierra.component :as component]
            [ote.services.transport :as transport-service]
            [ote.db.service-generators :as service-generators]
            [clojure.test.check.generators :as gen]
            [ote.db.transport-service :as t-service]
            [ote.db.places :as places]
            [ote.db.generators :as generators]
            [ote.integration.export.transform :as transform]
            [ote.time :as time]
            [webjure.json-schema.validator.macro :refer [make-validator]]
            [cheshire.core :as cheshire]
            [taoensso.timbre :as log]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [ote.util.zip :as zip-file]
            [ring.util.io :as ring-io]))


(t/use-fixtures :each
                (system-fixture
                  :import-gtfs (component/using (import-gtfs/->GTFSImport
                                                  (:gtfs (read-string (slurp "config.edn")))) [:db :http])))

(defn convert-bytes
  [input-stream]
  (let [out (java.io.ByteArrayOutputStream.)]
    (io/copy input-stream out)
    (.toByteArray out)))


(def gtfs-files ["agency.txt" "routes.txt" "stops.txt" "trips.txt" "stop_times.txt" "calendar_dates.txt"])

(defn fetch-gtfs-package [id]
  (first (sql-query
           "SELECT * FROM gtfs_package WHERE id=" id "LIMIT 1")))

(defn upsert-gtfs-package [id]
  (set
    (sql-execute!
      "DELETE FROM gtfs_package WHERE id =" id ";"
      "INSERT INTO gtfs_package (id, \"transport-operator-id\", \"transport-service-id\", \"external-interface-description-id\", created)
        VALUES (" id ", 1, 1, 1, '2017-01-01'::DATE)")))



(defn gtfs-zip [test-package-id]
  (let [path (str "test/resources/gtfs/gtfs_package_" test-package-id "/")
        files (mapv (fn [fname]
                      {:name fname :data (slurp (str path fname))})
                    gtfs-files)]
    (ring-io/piped-input-stream #(zip-file/write-zip files %))))


(deftest save-gtfs-to-db
  (let [gtfs-zip (gtfs-zip 1)]
    (upsert-gtfs-package 999)
    (import-gtfs/save-gtfs-to-db (:db *ote*) (convert-bytes gtfs-zip) 999 1)

    (println (fetch-gtfs-package 999))

    (is (= 999 (:id (fetch-gtfs-package 999))))))
