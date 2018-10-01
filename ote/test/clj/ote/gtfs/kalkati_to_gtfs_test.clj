(ns ote.gtfs.kalkati-to-gtfs-test
  (:require [ote.gtfs.kalkati-to-gtfs :as sut]
            [clojure.test :as t :refer [deftest is testing]]
            [clojure.java.io :as io]
            [ote.util.zip :as zip-file]
            [ote.gtfs.parse :as gtfs-parse]
            [ote.gtfs.spec :as gtfs-spec]))

(defn- kalkati-resource-to-zip [resource-path]
  (let [out (java.io.ByteArrayOutputStream.)]
    (zip-file/write-zip [{:name "LVM.xml"
                          :data (slurp resource-path)}] out)
    (.toByteArray out)))

;; A trivial smoke test to check basic functionality
(deftest kalkati-to-gtfs-smoke-test
  (let [kalkati-zip (kalkati-resource-to-zip "test/resources/kalkati/test1.xml")
        gtfs-zip (sut/convert-bytes kalkati-zip)
        gtfs-files (zip-file/read-zip (java.io.ByteArrayInputStream. gtfs-zip))]

    (is (= #{"agency.txt" "routes.txt" "stop_times.txt" "stops.txt" "trips.txt" "calendar_dates.txt"}
           (into #{} (map :name gtfs-files)))
        "All expected GTFS files are present")

    (let [{:gtfs/keys [agency-txt routes-txt stop-times-txt
                       stops-txt trips-txt calendar-dates-txt]
           :as parsed} (into {}
                             (map (fn [{:keys [name data]}]
                                    (let [gtfs-file-type (gtfs-spec/name->keyword name)]
                                      (is (some? gtfs-file-type) "Zip contains no unrecognized files")
                                      [gtfs-file-type (gtfs-parse/parse-gtfs-file gtfs-file-type data)])))
                             gtfs-files)]

      (testing
          "GTFS contents are parsed correctly"

        (testing "agency.txt"
          (is (= 1 (count agency-txt)))
          (is (= "Test Company Ltd" (:gtfs/agency-name (first agency-txt)))))

        (testing "routes.txt"
          (is (= 1 (count routes-txt)))
          (is (= "Test service" (:gtfs/route-long-name (first routes-txt)))))

        (testing "stops.txt"
          (is (= 3 (count stops-txt)))
          (is (= #{"Test station 1" "Test station 2" "Test station 3"}
                 (into #{} (map :gtfs/stop-name) stops-txt))))

        (testing "trips.txt"
          (is (= 1 (count trips-txt))))

        (testing "stop_times.txt"
          (is (= 3 (count stop-times-txt))))

        (testing "calendar_dates.txt"
          (is (= 5 (count calendar-dates-txt)))
          (is (every? #(= 1 (:gtfs/exception-type %)) calendar-dates-txt))
          (is (= #{"2018-09-28" "2018-09-29" "2018-09-30" "2018-10-04" "2018-10-05"}
                 (into #{} (map (comp str :gtfs/date) calendar-dates-txt)))))))))
