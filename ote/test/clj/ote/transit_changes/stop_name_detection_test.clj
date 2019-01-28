(ns ote.transit-changes.stop-name-detection-test
  (:require [ote.transit-changes.detection :as detection]
            [clojure.test :as t :refer [deftest testing is]]
            [ote.transit-changes :as transit-changes]))

(defn d [year month date]
  (java.time.LocalDate/of year month date))

(def p1  [{:gtfs/package-id 1, :gtfs/trip-id "1_0", :stoptimes
                            [#:gtfs{:stop-id "FIOUL-VS", :stop-name "OULUN SATAMA: Vihreäsaari", :stop-lat 65.00462784311766M, :stop-lon 25.398150223816184M, :stop-sequence 0, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 8, :minutes 0, :seconds 0.0}}
                             #:gtfs{:stop-id "OTE1", :stop-name "Tukholma", :stop-lat 59.310768M, :stop-lon 18.539429M, :stop-sequence 1, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 0, :seconds 0.0}}]}
          {:gtfs/package-id 1, :gtfs/trip-id "1_1", :stoptimes
                            [#:gtfs{:stop-id "FIOUL-VS", :stop-name "OULUN SATAMA: Vihreäsaari", :stop-lat 65.00462784311766M, :stop-lon 25.398150223816184M, :stop-sequence 0, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 16, :minutes 0, :seconds 0.0}}
                             #:gtfs{:stop-id "OTE1", :stop-name "Tukholma", :stop-lat 59.310768M, :stop-lon 18.539429M, :stop-sequence 1, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 23, :minutes 0, :seconds 0.0}}]}])
(def p2-no-change [{:gtfs/package-id 2, :gtfs/trip-id "1_0", :stoptimes
                                     [#:gtfs{:stop-id "FIOUL-VS", :stop-name "OULUN SATAMA: Vihreäsaari", :stop-lat 65.00462784311766M, :stop-lon 25.398150223816184M, :stop-sequence 0, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 8, :minutes 0, :seconds 0.0}}
                                      #:gtfs{:stop-id "OTE1", :stop-name "Tukholma", :stop-lat 59.310768M, :stop-lon 18.539429M, :stop-sequence 1, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 0, :seconds 0.0}}]}
                   {:gtfs/package-id 2, :gtfs/trip-id "1_1", :stoptimes
                                     [#:gtfs{:stop-id "FIOUL-VS", :stop-name "OULUN SATAMA: Vihreäsaari", :stop-lat 65.00462784311766M, :stop-lon 25.398150223816184M, :stop-sequence 0, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 16, :minutes 0, :seconds 0.0}}
                                      #:gtfs{:stop-id "OTE1", :stop-name "Tukholma", :stop-lat 59.310768M, :stop-lon 18.539429M, :stop-sequence 1, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 23, :minutes 0, :seconds 0.0}}]}])


(deftest test-compare-selected-trips-no-changes
  (let [result (detection/compare-selected-trips
                 p1 p2-no-change
                 (d 2018 11 7) (d 2019 01 02))]
    (is (= (:added-trips result) 0))
    (is (= (:removed-trips result) 0))
    (is (= (first (:trip-changes result)) {:stop-time-changes 0, :stop-seq-changes 0}))))

(def p2-add-trip [{:gtfs/package-id 2, :gtfs/trip-id "1_0", :stoptimes
                                    [#:gtfs{:stop-id "FIOUL-VS", :stop-name "OULUN SATAMA: Vihreäsaari", :stop-lat 65.00462784311766M, :stop-lon 25.398150223816184M, :stop-sequence 0, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 8, :minutes 0, :seconds 0.0}}
                                     #:gtfs{:stop-id "OTE1", :stop-name "Tukholma", :stop-lat 59.310768M, :stop-lon 18.539429M, :stop-sequence 1, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 0, :seconds 0.0}}]}
                  {:gtfs/package-id 2, :gtfs/trip-id "1_1", :stoptimes
                                    [#:gtfs{:stop-id "FIOUL-VS", :stop-name "OULUN SATAMA: Vihreäsaari", :stop-lat 65.00462784311766M, :stop-lon 25.398150223816184M, :stop-sequence 0, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 16, :minutes 0, :seconds 0.0}}
                                     #:gtfs{:stop-id "OTE1", :stop-name "Tukholma", :stop-lat 59.310768M, :stop-lon 18.539429M, :stop-sequence 1, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 23, :minutes 0, :seconds 0.0}}]}
                  {:gtfs/package-id 2, :gtfs/trip-id "1_2", :stoptimes
                                    [#:gtfs{:stop-id "FIOUL-VS", :stop-name "OULUN SATAMA: Vihreäsaari", :stop-lat 65.00462784311766M, :stop-lon 25.398150223816184M, :stop-sequence 0, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 21, :minutes 0, :seconds 0.0}}
                                     #:gtfs{:stop-id "OTE1", :stop-name "Tukholma", :stop-lat 59.310768M, :stop-lon 18.539429M, :stop-sequence 1, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 28, :minutes 0, :seconds 0.0}}]}])


(deftest test-compare-selected-trips-add-one-trip
  (let [result (detection/compare-selected-trips
                 p1 p2-add-trip
                 (d 2018 11 7) (d 2019 01 02))]
    (is (= (:added-trips result) 1))
    (is (= (:removed-trips result) 0))
    (is (= (first (:trip-changes result)) {:stop-time-changes 0, :stop-seq-changes 0}))))

(def p2-change-stop-name [{:gtfs/package-id 2, :gtfs/trip-id "1_0", :stoptimes
                                            [#:gtfs{:stop-id "FIOUL-VS", :stop-name "OULUN SATAMA: Vihreäsaari - change name", :stop-lat 65.00462784311766M, :stop-lon 25.398150223816184M, :stop-sequence 0, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 8, :minutes 0, :seconds 0.0}}
                                             #:gtfs{:stop-id "OTE1", :stop-name "Tukholma", :stop-lat 59.310768M, :stop-lon 18.539429M, :stop-sequence 1, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 0, :seconds 0.0}}]}
                          {:gtfs/package-id 2, :gtfs/trip-id "1_1", :stoptimes
                                            [#:gtfs{:stop-id "FIOUL-VS", :stop-name "OULUN SATAMA: Vihreäsaari - change name", :stop-lat 65.00462784311766M, :stop-lon 25.398150223816184M, :stop-sequence 0, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 16, :minutes 0, :seconds 0.0}}
                                             #:gtfs{:stop-id "OTE1", :stop-name "Tukholma", :stop-lat 59.310768M, :stop-lon 18.539429M, :stop-sequence 1, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 23, :minutes 0, :seconds 0.0}}]}])

(deftest test-one-stop-name-changed
  (let [result (detection/compare-selected-trips
                 p1 p2-change-stop-name
                 (d 2018 11 7) (d 2019 01 02))]
    (println "tulo \n " (pr-str result))
    (is (= (:added-trips result) 0))
    (is (= (:removed-trips result) 0))
    (is (= (:stop-seq-changes (first (:trip-changes result))) 0))
    (is (= (:stop-seq-changes (second (:trip-changes result))) 0))))

(def p2-change-stop-name-2 [{:gtfs/package-id 2, :gtfs/trip-id "1_0", :stoptimes
                                              [#:gtfs{:stop-id "FIOUL-VS", :stop-name "OULUN SATAMA: Vihreäsaari", :stop-lat 65.00462784311766M, :stop-lon 25.398150223816184M, :stop-sequence 0, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 8, :minutes 0, :seconds 0.0}}
                                               #:gtfs{:stop-id "OTE1", :stop-name "Tukholma1", :stop-lat 59.310768M, :stop-lon 18.539429M, :stop-sequence 1, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 0, :seconds 0.0}}]}
                            {:gtfs/package-id 2, :gtfs/trip-id "1_1", :stoptimes
                                              [#:gtfs{:stop-id "FIOUL-VS", :stop-name "OULUN SATAMA: Vihreäsaari", :stop-lat 65.00462784311766M, :stop-lon 25.398150223816184M, :stop-sequence 0, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 16, :minutes 0, :seconds 0.0}}
                                               #:gtfs{:stop-id "OTE1", :stop-name "Tukholma1", :stop-lat 59.310768M, :stop-lon 18.539429M, :stop-sequence 1, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 23, :minutes 0, :seconds 0.0}}]}])

(deftest test-stop-name-changed-with-different-data
  (let [result (detection/compare-selected-trips
                 p1 p2-change-stop-name-2
                 (d 2018 11 7) (d 2019 01 02))]
    (is (= (:added-trips result) 0))
    (is (= (:removed-trips result) 0))
    (is (= (:stop-seq-changes (first (:trip-changes result))) 0))
    (is (= (:stop-seq-changes (second (:trip-changes result))) 0))))

(def p1_1 [{:gtfs/package-id 1, :gtfs/trip-id "1_0", :stoptimes
                             [#:gtfs{:stop-id "s1", :stop-name "n1", :stop-lat 1, :stop-lon 1, :stop-fuzzy-lat 1, :stop-fuzzy-lon 1, :stop-sequence 0, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 8, :minutes 0, :seconds 0.0}}
                              #:gtfs{:stop-id "s2", :stop-name "n2", :stop-lat 2, :stop-lon 2, :stop-fuzzy-lat 2, :stop-fuzzy-lon 2, :stop-sequence 1, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 12, :minutes 0, :seconds 0.0}}
                              #:gtfs{:stop-id "s3", :stop-name "n3", :stop-lat 3, :stop-lon 3, :stop-fuzzy-lat 3, :stop-fuzzy-lon 3, :stop-sequence 2, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 0, :seconds 0.0}}]}
           {:gtfs/package-id 1, :gtfs/trip-id "1_1", :stoptimes
                             [#:gtfs{:stop-id "s1", :stop-name "n1", :stop-lat 1, :stop-lon 1, :stop-fuzzy-lat 1, :stop-fuzzy-lon 1, :stop-sequence 0, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 16, :minutes 0, :seconds 0.0}}
                              #:gtfs{:stop-id "s2", :stop-name "n2", :stop-lat 2, :stop-lon 2, :stop-fuzzy-lat 2, :stop-fuzzy-lon 2, :stop-sequence 1, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 17, :minutes 0, :seconds 0.0}}
                              #:gtfs{:stop-id "s3", :stop-name "n3", :stop-lat 3, :stop-lon 3, :stop-fuzzy-lat 3, :stop-fuzzy-lon 3, :stop-sequence 2, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 0, :seconds 0.0}}]}])
(def p2_1 [{:gtfs/package-id 2, :gtfs/trip-id "1_0", :stoptimes
                             [#:gtfs{:stop-id "s1", :stop-name "n1", :stop-lat 1, :stop-lon 1, :stop-fuzzy-lat 1, :stop-fuzzy-lon 1, :stop-sequence 0, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 8, :minutes 0, :seconds 0.0}}
                              #:gtfs{:stop-id "s2", :stop-name "n2_2", :stop-lat 2, :stop-lon 2, :stop-fuzzy-lat 2, :stop-fuzzy-lon 2, :stop-sequence 1, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 12, :minutes 0, :seconds 0.0}}
                              #:gtfs{:stop-id "s3", :stop-name "n3_3", :stop-lat 3, :stop-lon 3, :stop-fuzzy-lat 3, :stop-fuzzy-lon 3, :stop-sequence 2, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 0, :seconds 0.0}}]}
           {:gtfs/package-id 2, :gtfs/trip-id "1_1", :stoptimes
                             [#:gtfs{:stop-id "s1", :stop-name "n1", :stop-lat 1, :stop-lon 1, :stop-fuzzy-lat 1, :stop-fuzzy-lon 1, :stop-sequence 0, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 16, :minutes 0, :seconds 0.0}}
                              #:gtfs{:stop-id "s2", :stop-name "n2_2", :stop-lat 2, :stop-lon 2, :stop-fuzzy-lat 2, :stop-fuzzy-lon 2, :stop-sequence 1, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 17, :minutes 0, :seconds 0.0}}
                              #:gtfs{:stop-id "s3", :stop-name "n3_3", :stop-lat 3, :stop-lon 3, :stop-fuzzy-lat 3, :stop-fuzzy-lon 3, :stop-sequence 2, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 0, :seconds 0.0}}]}])

(deftest test-two-stop-name-changed-location-is-same
  (let [result (detection/compare-selected-trips
                 p1_1 p2_1
                 (d 2018 11 7) (d 2019 01 02))]
    (is (= (:added-trips result) 0))
    (is (= (:removed-trips result) 0))
    (is (= (:stop-seq-changes (first (:trip-changes result))) 0))
    (is (= (:stop-seq-changes (second (:trip-changes result))) 0))))

(def p2_2 [{:gtfs/package-id 2, :gtfs/trip-id "1_0", :stoptimes
                             [#:gtfs{:stop-id "s1", :stop-name "n1", :stop-lat 1, :stop-lon 1, :stop-fuzzy-lat 1, :stop-fuzzy-lon 1, :stop-sequence 0, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 8, :minutes 0, :seconds 0.0}}
                              #:gtfs{:stop-id "s2", :stop-name "n2_2", :stop-lat 2, :stop-lon 2, :stop-fuzzy-lat 2, :stop-fuzzy-lon 2, :stop-sequence 1, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 12, :minutes 0, :seconds 0.0}}
                              #:gtfs{:stop-id "s3", :stop-name "n3_3", :stop-lat 4, :stop-lon 4, :stop-fuzzy-lat 4, :stop-fuzzy-lon 4, :stop-sequence 2, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 0, :seconds 0.0}}]}
           {:gtfs/package-id 2, :gtfs/trip-id "1_1", :stoptimes
                             [#:gtfs{:stop-id "s1", :stop-name "n1", :stop-lat 1, :stop-lon 1, :stop-fuzzy-lat 1, :stop-fuzzy-lon 1, :stop-sequence 0, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 16, :minutes 0, :seconds 0.0}}
                              #:gtfs{:stop-id "s2", :stop-name "n2_2", :stop-lat 2, :stop-lon 2, :stop-fuzzy-lat 2, :stop-fuzzy-lon 2, :stop-sequence 1, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 17, :minutes 0, :seconds 0.0}}
                              #:gtfs{:stop-id "s3", :stop-name "n3_3", :stop-lat 4, :stop-lon 4, :stop-fuzzy-lat 4, :stop-fuzzy-lon 4, :stop-sequence 2, :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 0, :seconds 0.0}}]}])

(deftest test-two-stop-name-and-location-changed
  (let [result (detection/compare-selected-trips
                 p1_1 p2_2
                 (d 2018 11 7) (d 2019 01 02))]
    (is (= (:added-trips result) 0))
    (is (= (:removed-trips result) 0))
    (is (= (:stop-seq-changes (first (:trip-changes result))) 2))
    (is (= (:stop-seq-changes (second (:trip-changes result))) 2))))