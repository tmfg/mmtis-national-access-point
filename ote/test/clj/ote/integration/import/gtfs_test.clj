(ns ote.integration.import.gtfs-test
  (:require [amazonica.aws.s3 :as s3]
            [clojure.test :refer :all]
            [clojure.string :as str]
            [clj-http.client :as http-client]
            [ote.integration.import.gtfs :as gtfs]
            [ote.services.transit-changes :as transit-changes]
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

(deftest test-package-creation
  (let [empty-package (create-zip "empty" [])]
    (testing "empty package is created"
      (is (= true (str/ends-with? (.getPath empty-package) "empty.zip")))
      (is (= true (.exists empty-package)))

      (is (= true (empty? (zip/list-zip (io/input-stream empty-package))))))))

(defn- fetch-reports-for
  [result]
  (filter
    (fn [r]
      (if (some? result)
        (= (:package-id result)
           (get-in r [:gtfs-import/package_id :gtfs/id]))
        true))
    (transit-changes/load-gtfs-import-reports db)))

(defn- assert-report
  "Take the first report from reports, assert its contents, return rest of the reports for threading."
  [reports expected-description expected-severity]
  (let [{:gtfs-import/keys [description error severity]
         :gtfs-package/keys [transport-operator transport-service]} (first reports)]
    (is (= expected-severity severity))
    (is (str/starts-with? expected-description description)))
  (rest reports))

(defn gtfs-row
  [file-type values]
  (zipmap (get-in gtfs-parse/file-info [file-type :fields])
          values))

(defn gtfs-file
  [file-type values]
  (gtfs-parse/unparse-gtfs-file file-type (mapv #(gtfs-row file-type %) values)))

(deftest required-files-are-present
  (let [required-empty-1  [{:name "agency.txt"   :data (gtfs-file :gtfs/agency-txt [])}
                           {:name "stops.txt"    :data (gtfs-file :gtfs/stops-txt [])}
                           {:name "calendar.txt" :data (gtfs-file :gtfs/calendar-txt [])}
                           {:name "trips.txt"    :data (gtfs-file :gtfs/trips-txt [])}]
        missing-files-1   [{:name "stops.txt"    :data (gtfs-file :gtfs/stops-txt [])}]
        valid-content-1   [{:name "agency.txt"   :data (gtfs-file :gtfs/agency-txt [[100 "Pyörät Pyörii Oy" "example.fi/homepage" "Europe/Helsinki" "fi-FI" "0100100" "example.fi/fares" "email@example.fi"]])}
                           {:name "stops.txt"    :data (gtfs-file :gtfs/stops-txt [[200 "S200-FG" "Front Gate outbound" "Accessible unsheltered stop at front gate" 60.19 25.02 nil "example.fi/stops/S200-FG" 1 "" "Europe/Helsinki" 1]])}
                           {:name "calendar.txt" :data (gtfs-file :gtfs/calendar-txt [[300 1 1 1 1 1 1 1 #inst "2021-11-01" #inst "2022-04-01"]])}
                           {:name "trips.txt"    :data (gtfs-file :gtfs/trips-txt [[400 300 500 "To Centrum" "C16" 0 nil nil 1 1]])}]
                          ; keys are strings instead of keywords to get around unnecessary spec validation in http client
        gtfs-files        {"required-empty-1" (create-zip "required-empty-1" required-empty-1)
                           "missing-files-1"  (create-zip "missing-files-1" missing-files-1)
                           "valid-content-1"  (create-zip "valid-content-1" valid-content-1)}]

    (with-redefs [http-client/get (mock-handler gtfs-files)
                  s3/put-object (fn [_ _ _ _] (comment "no-op on purpose"))]

      (testing "valid package doesn't generate reports"
        (let [result  (process-transit-package "valid-content-1")
              reports (fetch-reports-for result)]
          (is (= 0 (count reports)))))

      (testing "missing files are reported"
        (let [result  (process-transit-package "missing-files-1")
              reports (fetch-reports-for result)]
          (is (= true (nil? result)))
          (assert-report [(last reports)] "Cannot create new GTFS import, missing-files-1 returned empty body as response when loading GTFS zip" "error")))

      (testing "empty content in GTFS files are reported"
        (let [result  (process-transit-package "required-empty-1")
              reports (fetch-reports-for result)]
          (is (= 4 (count reports)))
          (-> reports
              (assert-report "No data rows in file agency.txt of type :gtfs/agency-txt" "error")
              (assert-report "No data rows in file stops.txt of type :gtfs/stops-txt" "error")
              (assert-report "No data rows in file calendar.txt of type :gtfs/calendar-txt" "error")
              (assert-report "No data rows in file trips.txt of type :gtfs/trips-txt" "error")))))))
