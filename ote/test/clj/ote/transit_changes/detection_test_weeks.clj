(ns ote.transit-changes.detection-test-weeks
  (:require [ote.transit-changes.detection :as detection]
            [clojure.test :as t :refer [deftest testing is]]
            [clojure.spec.test.alpha :as spec-test]
            [ote.transit-changes :as transit-changes]
            [ote.transit-changes.detection-test-utilities :as tu]
            [ote.time :as time]))

(def change-window detection/route-end-detection-threshold)

(def data-no-changes
  (tu/weeks (tu/to-local-date 2019 5 13) (tu/generate-traffic-week 9 ))) ;; Last week starts 2019 07 08

(deftest test-no-changes
  (let [result (-> data-no-changes
                   detection/changes-by-week->changes-by-route
                   detection/detect-changes-for-all-routes)]
    (is (= {:route-key tu/route-name
            :starting-week {:beginning-of-week (tu/to-local-date 2019 5 20) :end-of-week (tu/to-local-date 2019 5 26)}}
           (-> result
               first
               (select-keys tu/select-keys-detect-changes-for-all-routes))))
    (is (= 1 (count result)))))

(def data-change-on-2nd-to-last-wk
  (tu/weeks (tu/to-local-date 2019 5 13)
            (concat (tu/generate-traffic-week 5) ;; Last week starts 2019 06 17
                    [{tu/route-name ["h1" "h2" "h3" "h4" "h5" "!!" "h7"]}
                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "!!" "h7"]}])))

(deftest test-change-on-2nd-to-last-wk
  (let [result (-> data-change-on-2nd-to-last-wk
                   detection/changes-by-week->changes-by-route
                   detection/detect-changes-for-all-routes)]
    (is (= {:route-key tu/route-name
            :starting-week {:beginning-of-week (tu/to-local-date 2019 5 20) :end-of-week (tu/to-local-date 2019 5 26)}}
           (-> result
               first
               ;; NOTE: Changes on 2nd to last and last week not detected currently by the analysis!
               (select-keys tu/select-keys-detect-changes-for-all-routes))))
    (is (= 1 (count result)))))

(def data-no-changes-weekend-nil
  (tu/weeks (tu/to-local-date 2019 5 13)
            (tu/generate-traffic-week 9 ["h1" "h2" "h3" "h4" "h5" nil nil] tu/route-name))) ;; Last week starts 2019 06 24

(deftest test-no-changes-weekend-nil
  (let [result (-> data-no-changes-weekend-nil
                   detection/changes-by-week->changes-by-route
                   detection/detect-changes-for-all-routes)]
    (testing "Weekend traffic nil, ensure :no-traffic is not reported"
      (is (= {:route-key tu/route-name
              :starting-week {:beginning-of-week (tu/to-local-date 2019 5 20) :end-of-week (tu/to-local-date 2019 5 26)}}
             (-> result
                 first
                 (select-keys tu/select-keys-detect-changes-for-all-routes))))
      (is (= 1 (count result))))))

(def data-all-routes-2019
  [[tu/route-name-2
    {:route-short-name tu/route-name-2,
     :route-long-name tu/route-name-2,
     :trip-headsign "",
     :min-date nil,
     :max-date nil,
     :route-hash-id tu/route-name-2}]
   [tu/route-name
    {:route-short-name "",
     :route-long-name tu/route-name,
     :trip-headsign "",
     :min-date (time/sql-date (tu/to-local-date 2019 1 1)),
     :max-date (time/sql-date (tu/to-local-date 2019 12 31)),
     :route-hash-id tu/route-name}]
   [tu/route-name-3
    {:route-short-name "",
     :route-long-name tu/route-name-3,
     :trip-headsign "",
     :min-date nil,
     :max-date nil,
     :route-hash-id tu/route-name-3}]])

(def data-route-starts-no-end
  (tu/weeks (tu/to-local-date 2019 4 15)
            (concat [{tu/route-name [nil nil nil nil nil nil nil]}
                     {tu/route-name [:holiday :holiday nil nil nil nil nil]}
                     {tu/route-name [nil nil nil nil nil nil nil]}] ;; 29.4.
                    (tu/generate-traffic-week 11 [nil nil nil nil nil "h6" "h6"]))))

(deftest test-route-starts-no-end
  (let [result (->> data-route-starts-no-end
                    (detection/changes-by-week->changes-by-route)
                    (detection/detect-changes-for-all-routes)
                    (detection/add-ending-route-change (tu/to-local-date 2019 5 20) change-window data-all-routes-2019))]
    (testing "Ensure detection for starting route detects only one change and no route end. No further verification because currently transform-route-change converts it to change-type :added"
      (is (not (contains? result :route-end-date)))
      (is (= 1 (count result))))))

(def data-no-traffic-run
  (tu/weeks (tu/to-local-date 2018 10 8)
            (list {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
                  {tu/route-name ["h1" "h2" nil nil nil nil nil]} ; 4 day run
                  {tu/route-name [nil nil nil nil nil nil nil]} ; 7 days
                  {tu/route-name [nil nil nil nil nil "h6" "h7"]} ; 4 days => sum 17
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]})))

(deftest test-no-traffic-run
  (testing "no-traffic starts and ends middle of week, ensure not no-traffic reported"
    (is (= {:route-key tu/route-name
            :starting-week {:beginning-of-week (tu/to-local-date 2018 10 15) :end-of-week (tu/to-local-date 2018 10 21)}
            :no-traffic-change 17
            :no-traffic-start-date (tu/to-local-date 2018 10 17)
            :no-traffic-end-date (tu/to-local-date 2018 11 3)}
           (-> data-no-traffic-run
               detection/changes-by-week->changes-by-route
               detection/detect-changes-for-all-routes
               first
               (select-keys tu/select-keys-detect-changes-for-all-routes))))))

(def data-no-traffic-run-twice
  (tu/weeks (tu/to-local-date 2018 10 8)
            (concat [{tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
                     {tu/route-name [nil nil nil nil nil nil nil]}
                     {tu/route-name [nil nil nil nil nil nil nil]}
                     {tu/route-name [nil nil nil nil nil nil nil]}
                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
                     {tu/route-name [nil nil nil nil nil nil nil]}
                     {tu/route-name [nil nil nil nil nil nil nil]}
                     {tu/route-name [nil nil nil nil nil nil nil]}]
                    (tu/generate-traffic-week 5 ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]))))

(deftest test-no-traffic-run-twice-is-detected
  (let [test-result (-> data-no-traffic-run-twice
                        detection/changes-by-week->changes-by-route
                        detection/detect-changes-for-all-routes)]
    (testing "no-traffic run twice, ensure first run is reported"
      (is (= {:no-traffic-start-date (tu/to-local-date 2018 10 15)
              :no-traffic-end-date (tu/to-local-date 2018 11 5)
              :route-key "Raimola"
              :no-traffic-change 21
              :starting-week {:beginning-of-week (tu/to-local-date 2018 10 15)
                              :end-of-week (tu/to-local-date 2018 10 21)}}
             (-> (first test-result)
                 (select-keys tu/select-keys-detect-changes-for-all-routes)))))
    (testing "no-traffic run twice, ensure second run is reported"
      (is (= {:no-traffic-start-date (tu/to-local-date 2018 11 26)
              :no-traffic-end-date (tu/to-local-date 2018 12 17)
              :route-key "Raimola"
              :no-traffic-change 21
              :starting-week {:beginning-of-week (tu/to-local-date 2018 11 5)
                              :end-of-week (tu/to-local-date 2018 11 11)}}
             (-> (second test-result)
                 (select-keys tu/select-keys-detect-changes-for-all-routes)))))))

(def data-no-traffic-run-weekdays
  (tu/weeks (tu/to-local-date 2018 10 8)
            (list {tu/route-name ["h1" "h2" "h3" "h4" "h5" nil nil]} ;; 8.10.
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" nil nil]} ;; 15.10.
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" nil nil]} ;; 22.10.
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" nil nil]} ;; 29.10.
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" nil nil]} ;; 5.11.
                  {tu/route-name ["h1" nil nil nil nil nil nil]}     ;; Starts 13.11, 6 day run
                  {tu/route-name [nil nil nil nil nil nil nil]}      ;;        7 days
                  {tu/route-name [nil nil nil nil "h5" nil nil]}     ;; Ends 30.11, 4 days => sum 17
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" nil nil]}
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" nil nil]}
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" nil nil]})))

(deftest test-no-traffic-run-weekdays
  (testing "Ensure no-traffic run is reported and normal no-traffic weekends are not. "
    (is (= {:route-key tu/route-name
            :starting-week {:beginning-of-week (tu/to-local-date 2018 10 15) :end-of-week (tu/to-local-date 2018 10 21)}
            :no-traffic-change 17
            :no-traffic-start-date (tu/to-local-date 2018 11 13)
            :no-traffic-end-date (tu/to-local-date 2018 11 30)}
           (-> (detection/route-weeks-with-first-difference data-no-traffic-run-weekdays)
               first
               (select-keys tu/select-keys-detect-changes-for-all-routes))))))

(def no-traffic-run-full-detection-window
  (tu/weeks (tu/to-local-date 2018 10 8)
            (concat (tu/generate-traffic-week 5 ["h1" "h2" "h3" "h4" "h5" nil nil] tu/route-name)
                    [{tu/route-name ["h1" nil nil nil nil nil nil]}] ;; 12.11, Starting 13.11. 6 day run
                    (tu/generate-traffic-week 12 [nil nil nil nil nil nil nil] tu/route-name))))

(deftest test-no-traffic-run-full-detection-window
  (testing "Ensure traffic with normal no-traffic days detects a no-traffic change correctly"
    (let [result (-> no-traffic-run-full-detection-window
                     detection/changes-by-week->changes-by-route
                     detection/detect-changes-for-all-routes)]
      (is (= {:route-key tu/route-name
              :starting-week {:beginning-of-week (tu/to-local-date 2018 10 15) :end-of-week (tu/to-local-date 2018 10 21)}
              :no-traffic-run 76
              :no-traffic-start-date (tu/to-local-date 2018 11 13)}
             (-> (first result)
                 (select-keys tu/select-keys-detect-changes-for-all-routes))))

      (is (= 1 (count result))))))

(def test-traffic-2-different-weeks
  (tu/weeks (tu/to-local-date 2018 10 8)
            (list {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; starting point
                  {tu/route-name ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]} ; wednesday different
                  {tu/route-name ["h1" "h2" "h3" "!!" "h5" "h6" "h7"]} ; thursday different
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; back to normal
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]})))

(deftest two-week-difference-is-skipped
  (is (nil?
        (get-in (detection/route-weeks-with-first-difference test-traffic-2-different-weeks)
                [tu/route-name :different-week]))))

(def data-one-week-difference-is-skipped
  (tu/weeks (tu/to-local-date 2019 1 28)
            (list {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; prev week
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; starting week
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; normal
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; normal
                  {tu/route-name ["!!" "!!" "!!" "!!" "!!" "h6" "h7"]} ; first different week - should be skipper
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; back to normal
                  {tu/route-name ["h1" "h2" "h3" "h4" "!!" "h6" "!!"]} ; new schedule - should be found as different week
                  {tu/route-name ["h1" "h2" "h3" "h4" "!!" "h6" "!!"]} ; new schedule
                  {tu/route-name ["h1" "h2" "h3" "h4" "!!" "h6" "!!"]} ; New schedule
                  {tu/route-name ["h1" "h2" "h3" "h4" "!!" "h6" "!!"]}))) ; New schedule

(deftest test-one-week-difference-is-skipped
  (let [result (detection/route-weeks-with-first-difference data-one-week-difference-is-skipped)]
    (is (= {:beginning-of-week (tu/to-local-date 2019 3 11)
            :end-of-week (tu/to-local-date 2019 3 17)}
           (:different-week (first result))
           ))))

(def data-traffic-normal-difference
  (tu/weeks (tu/to-local-date 2018 10 8)
            (list {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; 2018-10-08
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; 2018-10-15, starting point
                  {tu/route-name ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]} ; 2018-10-22, wednesday different
                  {tu/route-name ["h1" "h2" "h3" "!!" "h5" "h6" "h7"]} ; 2018-10-29, thursday different
                  {tu/route-name ["h1" "h2" "h3" "h4" "!!" "h6" "h7"]} ; friday different
                  {tu/route-name ["h1" "h2" "h3" "!!" "!!" "h6" "h7"]}))) ;; thu and fri different

(deftest test-traffic-normal-difference
  (is (= {:starting-week-hash ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]
          :starting-week {:beginning-of-week (tu/to-local-date 2018 10 15)
                          :end-of-week (tu/to-local-date 2018 10 21)}
          :different-week-hash  ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]
          :different-week {:beginning-of-week (tu/to-local-date 2018 10 22)
                           :end-of-week (tu/to-local-date 2018 10 28)}
          :route-key "Raimola"}
         (first (detection/route-weeks-with-first-difference data-traffic-normal-difference)))))

(def test-traffic-starting-point-anomalous
(def data-traffic-starting-point-anomalous
  (tu/weeks (tu/to-local-date 2018 10 8)
            (concat [{tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; 2018-10-08
                     {tu/route-name ["h1!" "h2!" "h3!" "h4!" "h5!" "h5!" "h7!"]}] ; 2018-10-15, starting week is an exception
                    (tu/generate-traffic-week 4 ["h1" "h2" "h3" "h4" "h5" "h6" "h7"] tu/route-name))))

(deftest test-traffic-starting-point-anomalous
  (let [{:keys [starting-week different-week] :as res}
        (-> data-traffic-starting-point-anomalous
            detection/route-weeks-with-first-difference
            first)]
    (is (= (tu/to-local-date 2018 10 22) (:beginning-of-week starting-week))); gets -15, should be -22
    (is (= "Raimola" (:route-key res)))
    (is (nil? different-week)))) ;; gets -22, should be nil

(def test-traffic-static-holidays
  (tu/weeks (tu/to-local-date 2018 12 3)
            (list {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 3.12
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 10.12
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 17.12
                  {tu/route-name [:xmas-eve :xmas-day "h3" "h4" "h5" "h6" "h7"]} ; Week having static-holidays - 24.12
                  {tu/route-name ["h1" :new-year "h3!" "h4!" "h5!" "h6!" "h7!"]} ; Week having static-holidays (1.1.) 31.12
                  {tu/route-name ["h1!" "h2!" "h3!" "h4!" "h5!" "h6!" "h7!"]} ;; 7.1
                  {tu/route-name ["h1!" "h2!" "h3!" "h4!" "h5!" "h6!" "h7!"]} ;; 14.1
                  {tu/route-name ["h1!" "h2!" "h3!" "h4!" "h5!" "h6!" "h7!"]}))) ;; 21.1

(deftest static-holidays-are-skipped
  (let [{:keys [starting-week different-week] :as res}
        (first (detection/route-weeks-with-first-difference test-traffic-static-holidays))]

    (testing "detection skipped christmas week"
      (is (= (tu/to-local-date 2018 12 31) (:beginning-of-week different-week))))

    (testing "first different day is wednesday because tuesday is new year"
      (is (= 2 (transit-changes/first-different-day (:starting-week-hash res) (:different-week-hash res)))))))

(def test-more-than-one-change
  ; Produce change records about individual days -> first change week contains 2 days with differences
  ; In this test case we need to produce 3 rows in the database
  (tu/weeks (tu/to-local-date 2019 2 4)
            (list {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 4.2.
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; first current week (11.2.)
                  {tu/route-name ["h1" "!!" "!!" "h4" "h5" "h6" "h7"]} ;; 18.2. first change -> only one found currently | needs to detect change also on 13.2. -> this is set to current week
                  {tu/route-name ["h1" "!!" "!!" "h4" "h5" "h6" "h7"]} ;; no changes here (25.2.)
                  {tu/route-name ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]} ;; Only tuesday is found (4.3.)
                  {tu/route-name ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]} ;;
                  {tu/route-name ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]} ;;
                  {tu/route-name ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]})))

(def data-two-week-change
  (tu/weeks (tu/to-local-date 2019 2 4)
            (concat [{tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 4.2.
                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; first current week (11.2.)
                     {tu/route-name ["h1" "!!" "!!" "h4" "h5" "h6" "h7"]}
                     {tu/route-name ["h1" "!!" "!!" "h4" "h5" "h6" "h7"]}]
                    (tu/generate-traffic-week 4 ["h1" "h2" "h3" "h4" "h5" "h6" "h7"] tu/route-name))))

(deftest more-than-one-change-found
  (spec-test/instrument `detection/route-weeks-with-first-difference)

  ;; first test that the test data and old change detection code agree
  (testing "single-change detection code agrees with test data"
    (is (= (tu/to-local-date 2019 2 18) (-> test-more-than-one-change
                                            detection/route-weeks-with-first-difference
                                            first
                                            :different-week
                                            :beginning-of-week))))

  (let [diff-maps (-> test-more-than-one-change
                      detection/changes-by-week->changes-by-route
                      detection/detect-changes-for-all-routes)]
    (testing "got two changes"
      (is (= 2 (count diff-maps))))
    (testing "first change is detected"
      (is (= (tu/to-local-date 2019 2 18) (-> diff-maps
                                              first
                                              :different-week
                                              :beginning-of-week))))

    (testing "second change is detected"
      (is (= (tu/to-local-date 2019 3 4) (-> diff-maps
                                             second
                                             :different-week
                                             :beginning-of-week))))))

(def data-two-week-two-route-change                         ;; This is the same format as the (combine-weeks) function
  (tu/weeks (tu/to-local-date 2019 2 4)
            (list {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"] tu/route-name-2 ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"] tu/route-name-2 ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; 11.2. prev week start
                  {tu/route-name ["h1" "##" "h3" "h4" "h5" "h6" "h7"] tu/route-name-2 ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; 18.2. first change in route1
                  {tu/route-name ["h1" "##" "h3" "h4" "h5" "h6" "h7"] tu/route-name-2 ["h1" "h2" "##" "h4" "h5" "h6" "h7"]}
                  {tu/route-name ["h1" "##" "h3" "h4" "h5" "h6" "h7"] tu/route-name-2 ["h1" "h2" "##" "h4" "h5" "h6" "h7"]}
                  {tu/route-name ["h1" "##" "h3" "h4" "h5" "h6" "h7"] tu/route-name-2 ["h1" "h2" "##" "h4" "h5" "h6" "h7"]}
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"] tu/route-name-2 ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"] tu/route-name-2 ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"] tu/route-name-2 ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]})))

(deftest test-two-week-two-route-change
  (let [diff-maps (-> data-two-week-two-route-change
                      (detection/changes-by-week->changes-by-route)
                      (detection/detect-changes-for-all-routes))
        fwd-difference (detection/route-weeks-with-first-difference data-two-week-two-route-change)]
    (testing "first change matches first-week-difference return value"
      (is (= (-> fwd-difference second :different-week) (-> diff-maps first :different-week))))

    (testing "second route's first change date is ok"
      (is (= (tu/to-local-date 2019 2 25)
             (first
               (for [dp diff-maps
                     :let [d (-> dp :different-week :beginning-of-week)]
                     :when (= "Esala" (:route-key dp))]
                 d)))))))

(deftest no-change-found
  (spec-test/instrument `detection/route-weeks-with-first-difference)

  (let [diff-maps (-> data-two-week-change
                      detection/changes-by-week->changes-by-route
                      detection/detect-changes-for-all-routes)
        pairs-with-changes (filterv :different-week diff-maps)]
    (testing "got no changes"
      (is (= 0 (count pairs-with-changes))))))

; Dev tip: Put *symname in ns , evaluate, load, run (=define) and inspect in REPL
; Fetched from routes like below
; <snippet>
; (map
;  #(update % :routes select-keys [tu/route-name])
;  *res)
; </snippet>

(def data-realworld-two-change-case
  [{:beginning-of-week (java.time.LocalDate/parse "2019-02-18"),
    :end-of-week (java.time.LocalDate/parse "2019-02-24"),
    :routes {tu/route-name [nil nil nil nil nil nil "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-02-25"),
    :end-of-week (java.time.LocalDate/parse "2019-03-03"),
    :routes {tu/route-name ["heka" "heka" "heka" "heka" "heka" "htoka" "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-03-04")
    :end-of-week (java.time.LocalDate/parse "2019-03-10"),
    :routes {tu/route-name ["heka" "heka" "heka" "heka" "heka" "htoka" "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-03-11"),
    :end-of-week (java.time.LocalDate/parse "2019-03-17"),
    :routes {tu/route-name ["heka" "heka" "heka" "heka" "heka" "htoka" "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-03-18"),
    :end-of-week (java.time.LocalDate/parse "2019-03-24"),
    :routes {tu/route-name ["heka" "heka" "heka" "heka" "heka" "htoka" "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-03-25"),
    :end-of-week (java.time.LocalDate/parse "2019-03-31"),
    :routes {tu/route-name ["heka" "heka" "heka" "heka" "heka" "htoka" "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-04-01"),
    :end-of-week (java.time.LocalDate/parse "2019-04-07"),
    :routes {tu/route-name ["heka" "heka" "heka" "heka" "heka" "htoka" "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-04-08"),
    :end-of-week (java.time.LocalDate/parse "2019-04-14"),
    :routes {tu/route-name ["heka" "heka" "heka" "heka" "heka" "htoka" "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-04-15"),
    :end-of-week (java.time.LocalDate/parse "2019-04-21"),
    :routes {tu/route-name ["heka" "heka" "heka" "heka" "hkolmas" "htoka" "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-04-22"),
    :end-of-week (java.time.LocalDate/parse "2019-04-28"),
    :routes {tu/route-name ["hkolmas" "heka" "heka" "heka" "heka" "htoka" "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-04-29"),
    :end-of-week (java.time.LocalDate/parse "2019-05-05"),
    :routes {tu/route-name ["heka" "heka" "heka" "heka" "heka" "htoka" "hkolmas"]}} ;; In data third value was :first-of-may
   {:beginning-of-week (java.time.LocalDate/parse "2019-05-06"),
    :end-of-week (java.time.LocalDate/parse "2019-05-12"),
    :routes {tu/route-name ["heka" "heka" "heka" "heka" "heka" "htoka" "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-05-13"),
    :end-of-week (java.time.LocalDate/parse "2019-05-19"),
    :routes {tu/route-name ["heka" "heka" "heka" "heka" "heka" "htoka" "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-05-20"),
    :end-of-week (java.time.LocalDate/parse "2019-05-26"),
    :routes {tu/route-name ["heka" "heka" "heka" "heka" "heka" "htoka" "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-05-27"), ;; first change
    :end-of-week (java.time.LocalDate/parse "2019-06-02"),
    :routes {tu/route-name ["heka" "hkolmas" nil nil nil nil nil]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-06-03"),
    :end-of-week (java.time.LocalDate/parse "2019-06-09"),
    :routes {tu/route-name ["hneljas" "hneljas" "hneljas" "hneljas" "hneljas" nil nil]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-06-10"),
    :end-of-week (java.time.LocalDate/parse "2019-06-16"),
    :routes {tu/route-name ["hneljas" "hneljas" "hneljas" "hneljas" "hneljas" nil nil]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-06-17"),
    :end-of-week (java.time.LocalDate/parse "2019-06-23"),
    :routes {tu/route-name ["hneljas" "hneljas" "hneljas" "hneljas" nil nil nil]}}


   ;; padding added
   {:beginning-of-week (java.time.LocalDate/parse "2019-06-24"),
    :end-of-week (java.time.LocalDate/parse "2019-06-30"),
    :routes {tu/route-name ["hneljas" "hneljas" "hneljas" "hneljas" nil nil nil]}}

   {:beginning-of-week (java.time.LocalDate/parse "2019-07-01"),
    :end-of-week (java.time.LocalDate/parse "2019-07-01"),
    :routes {tu/route-name ["hneljas" "hneljas" "hneljas" "hneljas" nil nil nil]}}

   {:beginning-of-week (java.time.LocalDate/parse "2019-07-08"),
    :end-of-week (java.time.LocalDate/parse "2019-07-14"),
    :routes {tu/route-name ["hneljas" "hneljas" "hneljas" "hneljas" nil nil nil]}}])

(deftest more-than-one-change-found-case-2
  (spec-test/instrument `detection/route-weeks-with-first-difference)

  ;; first test that the test data and old change detection code agree
  (testing "single-change detection code agrees with test data"
    (is (= (tu/to-local-date 2019 5 27) (-> data-realworld-two-change-case
                                            detection/route-weeks-with-first-difference
                                            first
                                            :different-week
                                            :beginning-of-week))))

  (let [diff-maps (-> data-realworld-two-change-case
                      detection/changes-by-week->changes-by-route
                      detection/detect-changes-for-all-routes)]
    (testing "got two changes"
      (is (= 2 (count diff-maps))))

    (testing "first change is detected"
      (is (= (tu/to-local-date 2019 5 27) (-> diff-maps first :different-week :beginning-of-week))))

    (testing "second change date is correct"
      (is (= (tu/to-local-date 2019 6 3) (-> diff-maps second :different-week :beginning-of-week))))))

(def data-change-to-summer-schedule-nil-weeks-with-midsummer
  (tu/weeks (tu/to-local-date 2019 5 13)
            (list {tu/route-name ["1A" "1A" "1A" "1A" "1A" "6A" "7A"]} ;; 2019 05 13
                  {tu/route-name ["1A" "1A" "1A" "1A" "1A" "6A" "7A"]} ;; 2019 05 20
                  {tu/route-name ["1A" "1A" "1A" "7A" "5B" "6A" "7B"]} ;; 2019 05 27
                  {tu/route-name [nil nil nil nil nil "B6" nil]} ;; 2019 06 03
                  {tu/route-name [nil nil nil nil nil "B6" nil]} ;; 2019 06 10
                  {tu/route-name [nil nil nil nil "C5" nil nil]} ;; 2019 06 17
                  {tu/route-name [nil nil nil nil nil "B6" nil]} ;; 2019 06 24
                  {tu/route-name [nil nil nil nil nil "B6" nil]}
                  {tu/route-name [nil nil nil nil nil "B6" nil]}
                  {tu/route-name [nil nil nil nil nil "B6" nil]}
                  {tu/route-name [nil nil nil nil "C5" "B6" nil]}
                  {tu/route-name [nil nil nil nil nil "B6" nil]}
                  {tu/route-name [nil nil nil nil nil "B6" nil]})))

(def data-traffic-winter-to-summer-and-end-traffic
  (tu/weeks (tu/to-local-date 2019 5 13)
            (concat [{tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019 05 13
                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019 05 20
                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019 05 27
                     {tu/route-name [nil nil nil nil nil "h6" nil]} ;; 2019 06 03
                     {tu/route-name [nil nil nil nil nil "h6" nil]} ;; 2019 06 10
                     {tu/route-name [nil nil nil nil nil "h6" nil]} ;; 2019 06 17
                     (tu/generate-traffic-week 4 [nil nil nil nil nil nil nil] tu/route-name)])))

(deftest test-traffic-winter-to-summer-and-end-traffic
  (let [result (-> data-traffic-winter-to-summer-and-end-traffic
                   detection/changes-by-week->changes-by-route
                   detection/detect-changes-for-all-routes)]
    (is (= {:no-traffic-start-date (tu/to-local-date 2019 6 23)
            :route-key tu/route-name
            :starting-week {:beginning-of-week (tu/to-local-date 2019 5 20) :end-of-week (tu/to-local-date 2019 5 26)}}
           (select-keys (nth result 0)
                        [:no-traffic-start-date :no-traffic-end-date :route-key :no-traffic-change
                         :different-week :route-key :starting-week])))
    (is (= 1 (count result)))))

(deftest test-change-to-summer-schedule-nil-weeks-with-midsummer
  (let [result (-> data-change-to-summer-schedule-nil-weeks-with-midsummer
                   detection/changes-by-week->changes-by-route
                   detection/detect-changes-for-all-routes)]
    (is (= {:different-week {:beginning-of-week (tu/to-local-date 2019 5 27) :end-of-week (tu/to-local-date 2019 6 2)}
            :route-key tu/route-name
            :starting-week {:beginning-of-week (tu/to-local-date 2019 5 20) :end-of-week (tu/to-local-date 2019 5 26)}}
           (-> (nth result 0)
               (select-keys tu/select-keys-detect-changes-for-all-routes))))

    (is (= {:different-week {:beginning-of-week (tu/to-local-date 2019 6 3) :end-of-week (tu/to-local-date 2019 6 9)}
            :route-key tu/route-name
            :starting-week {:beginning-of-week (tu/to-local-date 2019 5 27) :end-of-week (tu/to-local-date 2019 6 2)}
            :no-traffic-run 1
            :no-traffic-start-date (tu/to-local-date 2019 6 9)}
           (-> (nth result 1)
               (select-keys tu/select-keys-detect-changes-for-all-routes))))
    (is (= 2 (count result)))))

;;;;;; TESTS for analysing route end reporting

(def data-all-routes [[tu/route-name-2
                       {:route-short-name tu/route-name-2,
                        :route-long-name tu/route-name-2,
                        :trip-headsign "",
                        :min-date nil,
                        :max-date nil,
                        :route-hash-id tu/route-name-2}]
                      [tu/route-name
                       {:route-short-name "",
                        :route-long-name tu/route-name,
                        :trip-headsign "",
                        :min-date (time/sql-date (tu/to-local-date 2019 1 1)),
                        :max-date (time/sql-date (tu/to-local-date 2019 7 14)),
                        :route-hash-id tu/route-name}]
                      [tu/route-name-3
                       {:route-short-name "",
                        :route-long-name tu/route-name-3,
                        :trip-headsign "",
                        :min-date nil,
                        :max-date nil,
                        :route-hash-id tu/route-name-3}]])

(def data-ending-route-change
  (tu/weeks (tu/to-local-date 2019 5 13)
            (tu/generate-traffic-week 9)));; 2019 07 08

(deftest test-ending-route-change
  (let [result (->> data-ending-route-change ;; Notice thread-last
                    (detection/changes-by-week->changes-by-route)
                    (detection/detect-changes-for-all-routes)
                    (detection/add-ending-route-change (tu/to-local-date 2019 5 20) change-window data-all-routes))]
    (is (= {:route-end-date (tu/to-local-date 2019 7 15)
            :route-key tu/route-name
            :starting-week {:beginning-of-week (tu/to-local-date 2019 5 20) :end-of-week (tu/to-local-date 2019 5 26)}}
           (-> result
               first
               (select-keys tu/select-keys-detect-changes-for-all-routes))))
    (is (= 1 (count result)))))

(def data-change-and-ending-route
  (tu/weeks (tu/to-local-date 2019 5 13)
            (concat (tu/generate-traffic-week 5  ["h1" "h2" "h3" "h4" "h5" "h6" "h7"])
                    ;; In below series: First wk 2019 06 17, last wk 2019 07 08
                    (tu/generate-traffic-week 4  ["h1" "h2" "h3" "h4" "h5" "!!" "!!"]))))

(deftest test-change-and-ending-route
  (let [result (->> data-change-and-ending-route ;; Notice thread-last
                    (detection/changes-by-week->changes-by-route)
                    (detection/detect-changes-for-all-routes)
                    (detection/add-ending-route-change (tu/to-local-date 2019 5 20) change-window data-all-routes))]
    (testing "Ensure a traffic change and route end within detection window are reported."
      (is (= {:different-week {:beginning-of-week (tu/to-local-date 2019 6 17)
                               :end-of-week (tu/to-local-date 2019 6 23)}
              :route-key tu/route-name
              :starting-week {:beginning-of-week (tu/to-local-date 2019 5 20) :end-of-week (tu/to-local-date 2019 5 26)}}
             (-> result
                 first
                 (select-keys tu/select-keys-detect-changes-for-all-routes))))

      (is (= {:route-end-date (tu/to-local-date 2019 7 15)
              :route-key tu/route-name
              :starting-week {:beginning-of-week (tu/to-local-date 2019 5 20) :end-of-week (tu/to-local-date 2019 5 26)}}
             (-> result
                 second
                 (select-keys tu/select-keys-detect-changes-for-all-routes))))
      (is (= 2 (count result))))))

(def data-change-nil-and-ending-route
  (tu/weeks (tu/to-local-date 2019 5 13)
            (concat [{tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019 05 13
                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019 05 20
                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019 05 27
                     {tu/route-name ["h1" "h2" nil nil nil nil nil]}] ;; 2019 06 03
                    (tu/generate-traffic-week 5 [nil nil nil nil nil nil nil]))))

(deftest test-change-nil-and-ending-route
  (let [result (->> data-change-nil-and-ending-route ;; Notice thread-last
                    (detection/changes-by-week->changes-by-route)
                    (detection/detect-changes-for-all-routes)
                    (detection/add-ending-route-change (tu/to-local-date 2019 5 20) change-window data-all-routes))]
    (testing "Ensure route end is reported when no-traffic starts, even if route max-date is later. If that is possible..."
      (is (= {:route-end-date (tu/to-local-date 2019 6 5)
              :route-key tu/route-name
              :starting-week {:beginning-of-week (tu/to-local-date 2019 5 20) :end-of-week (tu/to-local-date 2019 5 26)}}
             (-> result
                 first
                 (select-keys tu/select-keys-detect-changes-for-all-routes))))
      (is (= 1 (count result))))))
