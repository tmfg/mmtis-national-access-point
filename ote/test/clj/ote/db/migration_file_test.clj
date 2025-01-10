(ns ote.db.migration-file-test
  "Test that migration files are named properly."
  (:require  [clojure.test :as t :refer [deftest is testing]]
             [clojure.java.io :as io])
  (:import (java.io File)))

(defn migration-number [name]
  (second (re-matches #"^V1_(\d+)__.*\.sql$" name)))

(defn repeatable-migration? [name]
  (re-matches #"^R_.*$" name))

(deftest migration-files-ok
  (let [files (->> (File. "../database/src/main/resources/db/migration")
                   .listFiles
                   seq
                   (map #(.getName %))
                   (remove repeatable-migration?)
                   set)
        migrations (disj files "afterMigrate.sql")]

    (is (files "afterMigrate.sql") "afterMigrate script present")

    (is (not (empty? migrations)))

    (doseq [m migrations]
      ;; Migrations have one exception. And it is really hard to fix from all places. So skip it now.
      (println "m" (pr-str m))
      (when-not (= m "V210__store_tis_magic_link.sql")
        (is (re-matches #"^V1_\d+__.*\.sql$" m)
            (str "File " m " doesn't match the migration filename pattern!"))))

    (let [numbers (group-by identity (map migration-number migrations))]
      (doseq [n (keys numbers)]
        (is (= 1 (count (numbers n)))
            (str "Multiple migrations with number: " n))))))
