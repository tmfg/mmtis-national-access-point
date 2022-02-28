(ns ote.integration.import.gtfs-test
  (:require [amazonica.aws.s3 :as s3]
            [clojure.test :refer :all]
            [clojure.string :as str]
            [clj-http.client :as http-client]
            [ote.integration.import.gtfs :as gtfs]
            [ote.test :refer [system-fixture http-post]]
            [ote.components.db :as db]
            [taoensso.timbre :as log]
            [ote.gtfs.parse :as gtfs-parse]
            [ote.util.zip :as zip]
            [clojure.java.io :as io]
            [specql.core :as specql])
  (:import [java.nio.file Files]
           (java.io File)))

(def db {:datasource (db/hikari-datasource ote.test/test-db-config)})

(defn test-db
  [f]
  (f)
  (.close (:datasource db)))

(use-fixtures :once test-db)

(defn- create-zip
  "Creates a ZIP file for use in testing."
  [file-name content]
  (let [target (File/createTempFile "ote_gtfs_" (str file-name ".zip"))
        _      (.deleteOnExit target)
        _      (zip/write-zip content (io/output-stream target))]
    target))

(defn- mock-handler
  "clj-http mock handler which uses map of url->File as its source"
  [known-files]
  (fn [url & [req & r]]
    (if (known-files url)
      {:body (Files/readAllBytes (.toPath (known-files url)))}
      (log/warn "Unexpected HTTP client GET with params" url req r))))

(def ^:private common-interface-params
  {:operator-id      1
   :operator-name    "Ajopalvelu Testinen Oy"
   :ts-id            2
   :last-import-date "2022-02-22"
   :license          "anything"
   :id               789
   :data-content     nil})

(defn- process-transit-package [source-zip]
  (gtfs/download-and-store-transit-package
    :gtfs
    {}
    db
    (merge common-interface-params
           {:url source-zip})
    true
    true))

#_(deftest test-package-creation
  (let [empty-package (create-zip "empty-package")]
    (testing "empty-package is created"
      (println "empty? " (.getPath empty-package))
      (is (= true (str/ends-with? (.getPath empty-package) "empty.zip")))
      (is (= true (.exists empty-package))))))

(deftest required-files-are-present
  (let [required-empty-1 [{:name "agency.txt"   :data (gtfs-parse/unparse-gtfs-file :gtfs/agency-txt [])}
                          {:name "stops.txt"    :data (gtfs-parse/unparse-gtfs-file :gtfs/stops-txt [])}
                          {:name "calendar.txt" :data (gtfs-parse/unparse-gtfs-file :gtfs/calendar-txt [])}
                          {:name "trips.txt"    :data (gtfs-parse/unparse-gtfs-file :gtfs/trips-txt [])}]
        gtfs-files       {"required-empty-1" (create-zip "required-empty-1" required-empty-1)}]

    (with-redefs [http-client/get (mock-handler gtfs-files)
                  s3/put-object (fn [_ _ _ _] (comment "no-op on purpose"))]

      (testing "missing files are reported"
        ;(is (= true (process-transit-package ::missing-files-1)))
        ;(is (= true (process-transit-package ::missing-files-2)))
        )

      (testing "empty content in GTFS files are reported"
        (let [result (process-transit-package "required-empty-1")
              _ (println "result="result)
              reports (specql/fetch
                        db
                        :gtfs-import/report
                        #{:gtfs-import/id
                          :gtfs-import/package_id
                          :gtfs-import/severity
                          :gtfs-import/description
                          :gtfs-import/error}
                        {:gtfs-import/package_id (:package-id result)})]
          (println "reports=" reports)
          (is (= true result)))))))
