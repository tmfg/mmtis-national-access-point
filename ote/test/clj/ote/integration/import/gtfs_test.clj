(ns ote.integration.import.gtfs-test
  (:require [amazonica.aws.s3 :as s3]
            [clojure.test :refer :all]
            [clojure.string :as str]
            [clj-http.client :as http-client]
            [ote.integration.import.gtfs :as gtfs]
            [ote.services.transit-changes :as transit-changes]
            [ote.test :refer [sql-query]]
            [ote.components.db :as db]
            [taoensso.timbre :as log]
            [ote.gtfs.parse :as gtfs-parse]
            [ote.util.zip :as zip]
            [clojure.java.io :as io]
            [specql.core :as specql]
            [clojure.java.jdbc :as jdbc])
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

(defn- process-transit-package
  [interface-id source-zip]
  (gtfs/download-and-store-transit-package
    :gtfs
    {}
    db
    (merge common-interface-params
           {:id interface-id
            :url source-zip})
    true
    true))

(defn- fetch-reports-for
  [interface-id result]
  (filter
    (fn [r]
      (if (some? result)
        (= (:package-id result)
           (get-in r [:gtfs-package :id]))
        (if (some? interface-id)
          (= interface-id (get-in r [:gtfs-package :external-interface-description-id]))
          true)))
    (transit-changes/load-gtfs-import-reports db)))

(defn- assert-report
  "Take the first report from reports, assert its contents, return rest of the reports for threading."
  [reports expected-description expected-severity]
  (let [{:keys [gtfs-import-report gtfs-package transport-operator transport-service]} (first reports)
        {:keys [description error severity]} gtfs-import-report]
    (is (= expected-severity severity))
    (is (str/starts-with? description expected-description)))
  (rest reports))

(defn gtfs-row
  [file-type values]
  (zipmap (get-in gtfs-parse/file-info [file-type :fields])
          values))

(defn gtfs-file
  [file-type values]
  (gtfs-parse/unparse-gtfs-file file-type (mapv #(gtfs-row file-type %) values)))

(deftest test-package-creation
  (let [empty-package (create-zip "empty" [])]
    (testing "empty package is created"
      (is (= true (str/ends-with? (.getPath empty-package) "empty.zip")))
      (is (= true (.exists empty-package)))
      (is (= true (empty? (zip/list-zip (io/input-stream empty-package))))))))

(deftest gtfs-packages-tests
  (let [required-empty-1  [{:name "agency.txt"         :data (gtfs-file :gtfs/agency-txt [])}
                           {:name "stops.txt"          :data (gtfs-file :gtfs/stops-txt [])}
                           {:name "stop_times.txt"     :data (gtfs-file :gtfs/stop-times-txt [])}
                           {:name "calendar_dates.txt" :data (gtfs-file :gtfs/calendar-dates-txt [])}
                           {:name "routes.txt"         :data (gtfs-file :gtfs/routes-txt [])}
                           {:name "trips.txt"          :data (gtfs-file :gtfs/trips-txt [])}]
        missing-files-1   [{:name "stops.txt"          :data (gtfs-file :gtfs/stops-txt [])}]
        valid-content-1   [{:name "agency.txt"         :data (gtfs-file :gtfs/agency-txt [[100 "Pyörät Pyörii Oy" "example.fi/homepage" "Europe/Helsinki" "fi-FI" "0100100" "example.fi/fares" "email@example.fi"]])}
                           {:name "stops.txt"          :data (gtfs-file :gtfs/stops-txt [[200 "S200-FG" "Front Gate outbound" "Accessible unsheltered stop at front gate" 60.19 25.02 nil "example.fi/stops/S200-FG" 1 "" "Europe/Helsinki" 1]])}
                           {:name "trips.txt"          :data (gtfs-file :gtfs/trips-txt [[400 300 500 "To Centrum" "C16" 0 nil nil 1 1]])}
                           {:name "routes.txt"         :data (gtfs-file :gtfs/routes-txt [[400 100 "FAST" "Fast-CX" "Fast connection to city centrum" 3 "example.fi/routes/3" "0DFAA0" "FFFFFF"]])}
                           {:name "stop_times.txt"     :data (gtfs-file :gtfs/stop-times-txt [[500 "17:40:00" "17:28:00" 200 1 "To Centrum" 0 0 14.3 1]])}
                           {:name "calendar_dates.txt" :data (gtfs-file :gtfs/calendar-dates-txt [[600 #inst "2022-03-07" 1]])}]
                          ; keys are strings instead of keywords to get around unnecessary spec validation in http client
        gtfs-files        {"required-empty-1" (create-zip "required-empty-1" required-empty-1)
                           "missing-files-1"  (create-zip "missing-files-1" missing-files-1)
                           "valid-content-1"  (create-zip "valid-content-1" valid-content-1)}]

    (with-redefs [http-client/get (mock-handler gtfs-files)
                  s3/put-object (fn [_ _ _ _] (comment "no-op on purpose"))]

      (testing "valid package doesn't generate reports"
        (let [result  (process-transit-package 6001 "valid-content-1")
              reports (fetch-reports-for 6001 result)]
          (is (= true (some? result)))
          (is (= 0 (count reports)))))

      (testing "missing files are reported"
        (let [result  (process-transit-package 6101 "missing-files-1")
              reports (fetch-reports-for 6101 result)]
          ; `nil` is returned as result because missing files is considered a fatal error
          ; I'd like to change this but because of legacy creep I don't have enough domain knowledge to change this.
          ; So please heed my warning, if you see this test break, rollback and let it be.
          (is (= true (nil? result)))
          ;  `reports` is all reports produced by all tests so far, last is the latest and the one we're interested of
          (assert-report [(last reports)] "Error when opening interface zip package from url missing-files-1:Missing required files in GTFS zip file" "warning")))

      (testing "empty content in GTFS files are reported"
        (let [result  (process-transit-package 6201 "required-empty-1")
              reports (fetch-reports-for 6201 result)]
          (is (= 5 (count reports)))
          (-> reports
              (assert-report "No data rows in file agency.txt of type :gtfs/agency-txt" "error")
              (assert-report "No data rows in file stops.txt of type :gtfs/stops-txt" "error")
              (assert-report "No data rows in file calendar_dates.txt of type :gtfs/calendar-dates-txt" "error")
              (assert-report "No data rows in file routes.txt of type :gtfs/routes-txt" "error")
              (assert-report "No data rows in file trips.txt of type :gtfs/trips-txt" "error")
              ; stop times is a special file and is handled last because of trip id lookups
              ;; Due to disabling change detection, stop_times.txt is not required anymore
              #_ (assert-report "No data rows in file stop_times.txt of type :gtfs/stop-times-txt" "error"))))

      (testing "uploading invalid package multiple times adds only one failure report to latest package"
        (let [reports-before (fetch-reports-for 6301 nil)
              results  (->> (range 5)
                            (mapv (fn [_] (process-transit-package 6301 "missing-files-1"))))
              reports-after (fetch-reports-for 6301 nil)]
          (for [r results]
            (is (= true (nil? r))))
          ;  what's happening here:
          ;   - at first, there's a set of reports already in database
          ;   - adding five failures followed by one success
          (is (= 1 (- (count reports-after) (count reports-before))))
          (assert-report [(last reports-after)] "Error when opening interface zip package from url missing-files-1:Missing required files in GTFS zip file" "warning")))

      (testing "uploading valid package after few failures results in empty report"
        (let [results  (->> (range 3)
                            (mapv (fn [_] (process-transit-package 6401 "missing-files-1"))))
              result  (process-transit-package 6402 "valid-content-1")
              reports (fetch-reports-for 6402 result)]
          (is (= true (some? result)))
          (is (= 0 (count reports))))))))
