(ns ote.transit-changes.detection-test-weeks
  (:require [ote.transit-changes.detection :as detection]
            [clojure.test :as t :refer [deftest testing is]]
            [clojure.spec.test.alpha :as spec-test]
            [ote.transit-changes :as transit-changes]
            [ote.transit-changes.detection-test-utilities :as tu]
            [ote.time :as time]))

;; Dev tip: Put *symname in ns , evaluate, load, run (=define) and inspect in REPL
;; Fetched from routes like below
;; <snippet>
;; (map
;;  #(update % :routes select-keys [tu/route-name])
;;  *res)
;; </snippet>

(def change-window detection/route-end-detection-threshold)

;;;;;;;; Test Ongoing traffic no changes

(def data-no-changes
  (tu/weeks (tu/to-local-date 2019 5 13) (tu/generate-traffic-week 9))) ;; Last week starts 2019 07 08

(deftest test-no-changes
  (let [result (-> data-no-changes
                   detection/changes-by-week->changes-by-route
                   detection/detect-changes-for-all-routes)]
    (testing "Ensure ongoing traffic without changes is reported as ongoing traffic."
      (is (=
            {:route-key tu/route-name
             :starting-week {:beginning-of-week (tu/to-local-date 2019 5 20) :end-of-week (tu/to-local-date 2019 5 26)}}
            (-> result
                first
                (select-keys tu/select-keys-detect-changes-for-all-routes)))))

    (testing "Ensure that the right count of changes are found and there are no extra changes."
      (is (= 1 (count result))))))

;;;;;;;; Test change on 2nd to last week

(def data-change-on-2nd-to-last-wk
  (tu/weeks (tu/to-local-date 2019 5 13)
            (concat (tu/generate-traffic-week 5)            ;; Last week starts 2019 06 17
                    [{tu/route-name ["h1" "h2" "h3" "h4" "h5" "!!" "h7"]}
                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "!!" "h7"]}])))

(deftest test-change-on-2nd-to-last-wk
  (let [result (-> data-change-on-2nd-to-last-wk
                   detection/changes-by-week->changes-by-route
                   detection/detect-changes-for-all-routes)]

    (testing "Ensure no change is reported, ongoing traffic is reported"
      (is (= {:route-key tu/route-name
              :starting-week {:beginning-of-week (tu/to-local-date 2019 5 20)
                              :end-of-week (tu/to-local-date 2019 5 26)}}
             (-> result
                 first
                 ;; NOTE: Changes on 2nd to last and last week not detected currently by the analysis!
                 (select-keys tu/select-keys-detect-changes-for-all-routes)))))

    (testing "Ensure that a right amount of changes are found and there are no extra changes."
      (is (= 1 (count result))))))

;;;;;;;; Test traffic with nil weekend and no changes

(def data-no-changes-weekend-nil
  (tu/weeks (tu/to-local-date 2019 5 13)
            (tu/generate-traffic-week 9 ["h1" "h2" "h3" "h4" "h5" nil nil] tu/route-name))) ;; Last week starts 2019 06 24

(deftest test-no-changes-weekend-nil
  (let [result (-> data-no-changes-weekend-nil
                   detection/changes-by-week->changes-by-route
                   detection/detect-changes-for-all-routes)]
    (testing "Test traffic with nil weekend and no changes, ensure :no-traffic is not reported"
      (is (= {:route-key tu/route-name
              :starting-week {:beginning-of-week (tu/to-local-date 2019 5 20)
                              :end-of-week (tu/to-local-date 2019 5 26)}}
             (-> result
                 first
                 (select-keys tu/select-keys-detect-changes-for-all-routes)))))

    (testing "Ensure that a right amount of changes are found and there are no extra changes."
      (is (= 1 (count result))))))

;;;;;;;; Test new route

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
      (is (not (contains? result :route-end-date))))

    (testing "Ensure that a right amount of changes are found and there are no extra changes."
      (is (= 1 (count result))))))

;;;;;;;; Test no traffic i.e. pause in traffic which is just above threshold

(def data-no-traffic-run
  (tu/weeks (tu/to-local-date 2018 10 8)
            (list {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
                  {tu/route-name ["h1" "h2" nil nil nil nil nil]} ;; 4 day run
                  {tu/route-name [nil nil nil nil nil nil nil]} ;; 7 days
                  {tu/route-name [nil nil nil nil nil "h6" "h7"]} ;; 4 days => sum 17
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]})))

(deftest test-no-traffic-run
  (testing "Ensure no-traffic starts and ends middle of week and lasts just above threshold, ensure no-traffic is reported"
    (is (= {:route-key tu/route-name
            :starting-week {:beginning-of-week (tu/to-local-date 2018 10 15)
                            :end-of-week (tu/to-local-date 2018 10 21)}
            :no-traffic-change 17
            :no-traffic-start-date (tu/to-local-date 2018 10 17)
            :no-traffic-end-date (tu/to-local-date 2018 11 3)}
           (-> data-no-traffic-run
               detection/changes-by-week->changes-by-route
               detection/detect-changes-for-all-routes
               first
               (select-keys tu/select-keys-detect-changes-for-all-routes))))))

;;;;;;;; Test two separate no traffic runs i.e. pauses in traffic

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
    (testing "Ensure first no-traffic run is reported"
      (is (= {:no-traffic-start-date (tu/to-local-date 2018 10 15)
              :no-traffic-end-date (tu/to-local-date 2018 11 5)
              :route-key tu/route-name
              :no-traffic-change 21
              :starting-week {:beginning-of-week (tu/to-local-date 2018 10 15)
                              :end-of-week (tu/to-local-date 2018 10 21)}}
             (-> (first test-result)
                 (select-keys tu/select-keys-detect-changes-for-all-routes)))))

    (testing "Ensure second no-traffic run is reported"
      (is (= {:no-traffic-start-date (tu/to-local-date 2018 11 26)
              :no-traffic-end-date (tu/to-local-date 2018 12 17)
              :route-key tu/route-name
              :no-traffic-change 21
              :starting-week {:beginning-of-week (tu/to-local-date 2018 11 5)
                              :end-of-week (tu/to-local-date 2018 11 11)}}
             (-> (second test-result)
                 (select-keys tu/select-keys-detect-changes-for-all-routes)))))))

;;;;;;;; Test traffic change and two patterns of no-traffic which do NOT exceed reporting threshold

;; Data adapted from a real-world use-case
(def data-no-traffic-two-patterns-consecutive
  (tu/weeks (tu/to-local-date 2019 5 13)
            (list {tu/route-name ["AA" "AA" "AA" "AA" "AA" "AA" "AA"]} ;; Week 13.5.
                  {tu/route-name ["AA" "AA" "AA" "AA" "AA" "AA" "AA"]} ;; Week 20.5.
                  {tu/route-name ["AA" "AA" "AA" "AA" "AA" "AA" "AA"]} ;; Week 27.5.
                  {tu/route-name ["BB" "CC" nil nil nil nil nil]} ;; Week 3.6.
                  {tu/route-name ["BB" "CC" nil nil nil nil nil]} ;; Week 10.6.
                  {tu/route-name ["DD" "DD" "DD" "DD" "DD" nil nil]} ;; Week 17.6.
                  {tu/route-name ["DD" "DD" "DD" "DD" "DD" nil nil]}
                  {tu/route-name ["DD" "DD" "DD" "DD" "DD" nil nil]}
                  {tu/route-name ["DD" "DD" "DD" "DD" "DD" nil nil]})))

(deftest test-no-traffic-two-patterns-consecutive
  (let [result (-> data-no-traffic-two-patterns-consecutive
                   detection/changes-by-week->changes-by-route
                   detection/detect-changes-for-all-routes)]
    (testing "Ensure first change is reported"
      (is (=
            {:route-key tu/route-name
             :starting-week {:beginning-of-week (tu/to-local-date 2019 5 20)
                             :end-of-week (tu/to-local-date 2019 5 26)}
             :different-week {:beginning-of-week (tu/to-local-date 2019 6 3)
                              :end-of-week (tu/to-local-date 2019 6 9)}}
            (-> result
                first
                (select-keys tu/select-keys-detect-changes-for-all-routes)
                ;; no-traffic keys ignored in analysis because :different-week takes priority over them so let's just not examine them. Update test when logic changes.
                (dissoc :no-traffic-run :no-traffic-start-date)))))

    (testing "Ensure second change is reported"
      (is (=
            {:route-key tu/route-name
             :starting-week {:beginning-of-week (tu/to-local-date 2019 6 3)
                             :end-of-week (tu/to-local-date 2019 6 9)}
             :different-week {:beginning-of-week (tu/to-local-date 2019 6 17)
                              :end-of-week (tu/to-local-date 2019 6 23)}}
            (-> result
                second
                (select-keys tu/select-keys-detect-changes-for-all-routes)
                ;; no-traffic keys ignored in analysis because :different-week takes priority over them so let's just not examine them. Update test when logic changes.
                (dissoc :no-traffic-run :no-traffic-start-date)))))

    (testing "Ensure that a right amount of changes are found and there are no extra changes."
      (is (= 2 (count result))))))

;;;;;;;; Test no-traffic run start and end when normal traffic pattern includes also no-traffic days

(def data-no-traffic-run-weekdays
  (tu/weeks (tu/to-local-date 2018 10 8)
            (list {tu/route-name ["h1" "h2" "h3" "h4" "h5" nil nil]} ;; 8.10.
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" nil nil]} ;; 15.10.
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" nil nil]} ;; 22.10.
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" nil nil]} ;; 29.10.
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" nil nil]} ;; 5.11.
                  {tu/route-name ["h1" nil nil nil nil nil nil]} ;; Starts 13.11, 6 day run
                  {tu/route-name [nil nil nil nil nil nil nil]} ;; 7 days
                  {tu/route-name [nil nil nil nil "h5" nil nil]} ;; Ends 30.11, 4 days => sum 17
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" nil nil]}
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" nil nil]}
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" nil nil]})))

(deftest test-no-traffic-run-weekdays
  (testing "Ensure no-traffic run is reported and normal no-traffic weekends are not. "
    (is (= {:route-key tu/route-name
            :starting-week {:beginning-of-week (tu/to-local-date 2018 10 15)
                            :end-of-week (tu/to-local-date 2018 10 21)}
            :no-traffic-change 17
            :no-traffic-start-date (tu/to-local-date 2018 11 13)
            :no-traffic-end-date (tu/to-local-date 2018 11 30)}
           (-> (detection/route-weeks-with-first-difference data-no-traffic-run-weekdays)
               first
               (select-keys tu/select-keys-detect-changes-for-all-routes))))))

;;;;;;;; Test no-traffic run start and end when normal week pattern includes also no-traffic days

(def no-traffic-run-full-detection-window
  (tu/weeks (tu/to-local-date 2018 10 8)
            (concat (tu/generate-traffic-week 5 ["h1" "h2" "h3" "h4" "h5" nil nil] tu/route-name)
                    [{tu/route-name ["h1" nil nil nil nil nil nil]}] ;; 12.11, Starting 13.11. 6 day run
                    (tu/generate-traffic-week 12 [nil nil nil nil nil nil nil] tu/route-name)
                    (tu/generate-traffic-week 5 ["h1" "h2" "h3" "h4" "h5" nil nil] tu/route-name))))

(deftest test-no-traffic-run-full-detection-window
  (let [result (-> no-traffic-run-full-detection-window
                   detection/changes-by-week->changes-by-route
                   detection/detect-changes-for-all-routes
                   detection/trafficless-differences->no-traffic-changes)]
      ;; (def *r result)
      ;; debug note after week= & first-different-day change:
      ;; - change is caused by week= change, reverting f-d-d made no diff
      ;; - judging from debug prints the traffic-run 13 is found first and a second one goes up to 70-something
      ;; - at some point the first one is dropped before its returned here, where?
      ;; - the week-hash-no-traffic-run fn is the workhorse, seems to detect a break in trafficless run right after the first all-nil week when called week by week
      ;; - stack traces from week= tell us the 3 invocations come from detection.clj:200 & :201 & :203 ("If current week does not equal starting week...") 
      ;; - thinking about the length of the no-traffic run, 13, it's 1 day short of 2 weeks, matching 2 weeks with the first one being the tue-sun trafficless period
      ;; - ok, looks like due to old nil handling the trafficless period was never considered a change in traffic, but now it is. is this ok?
      ;; - not ok if we want to keep detecting longer no-traffic runs?
      ;; - no-traffic-run & no-traffic-change keys seem to be used only in detection ns. lets see how no-traffic info is propagated upwards, does our data model support no-traffic runs overlapping traffic changes?
      ;; - even before the week= change how did the test get to 76 length no-traffic run if no-traffic-detection-threshold is 16 days and the no-traffic-run is reset after that? investigate what the current no-traffic-change behaviour is
      ;;     - in add-no-traffic-run-dates fn, is used to set :no-traffic-end-date
      ;;     - no immediate other uses of no-traffic-change. let's see if no-traffic-end-date and
      ;; fix idea: postprocess traffic-changes to no-traffic in route-change-type or in the othe threading spot
      ;; - test should be changed to include return of traffic at the end because this is actually as-is an ending route, with just no-traffic weeks at the end.
    (testing "Ensure traffic with normal no-traffic days detects a no-traffic change correctly"
      (is (= {:route-key tu/route-name
              :starting-week {:beginning-of-week (tu/to-local-date 2018 10 15)
                              :end-of-week (tu/to-local-date 2018 10 21)}
              :no-traffic-run 76
              :no-traffic-start-date (tu/to-local-date 2018 11 13)}
             (select-keys
               (first result)
               tu/select-keys-detect-changes-for-all-routes))))

    (testing "Ensure that a right amount of changes are found and there are no extra changes."
      (is (= 1 (count result))))))

;;;;;;;; Test traffic change window when change does not reach change threshold

(def data-traffic-2-different-weeks
  (tu/weeks (tu/to-local-date 2018 10 8)
            (list {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; starting point
                  {tu/route-name ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]} ; wednesday different
                  {tu/route-name ["h1" "h2" "h3" "!!" "h5" "h6" "h7"]} ; thursday different
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; back to normal
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]})))

(deftest test-traffic-2-different-weeks
  (let [result (detection/route-weeks-with-first-difference data-traffic-2-different-weeks)
        expect-first {:route-key tu/route-name
                      :starting-week {:beginning-of-week (tu/to-local-date 2018 10 15)
                                      :end-of-week (tu/to-local-date 2018 10 21)}}]
    (testing "Ensure change week is reported only if there are enough consecutive change weeks, in this case no change"
      (is (= expect-first
             (select-keys
               (first result)
               tu/select-keys-detect-changes-for-all-routes))))

    (testing "Ensure that a right amount of changes are found and there are no extra changes."
      (is (= 1 (count result))))))

;;;;;;;; Test 1-week traffic change is skipped and a real change of one type is detected

(def data-traffic-1wk-difference-and-manywk-difference
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

(deftest test-traffic-1-different-week-and-new-change
  (let [result (detection/route-weeks-with-first-difference data-traffic-1wk-difference-and-manywk-difference)
        expect-first {:route-key tu/route-name
                      :starting-week {:beginning-of-week (tu/to-local-date 2019 2 4)
                                      :end-of-week (tu/to-local-date 2019 2 10)}
                      :different-week {:beginning-of-week (tu/to-local-date 2019 3 11)
                                       :end-of-week (tu/to-local-date 2019 3 17)}}]
    (testing "Ensure 1-week traffic change is skipped and a real change is detected"
      (is (= expect-first
             (select-keys (first result) tu/select-keys-detect-changes-for-all-routes))))

    (testing "Ensure that a right amount of changes are found and there are no extra changes."
      (is (= 1 (count result))))))

;;;;;;;; Test route-weeks-with-first-difference finds the first difference in different traffic patterns

(def data-consecutive-different-traffic-patterns
  (tu/weeks (tu/to-local-date 2018 10 8)
            (list {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; 2018-10-08
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; 2018-10-15, starting point
                  {tu/route-name ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]} ; 2018-10-22, wednesday different
                  {tu/route-name ["h1" "h2" "h3" "!!" "h5" "h6" "h7"]} ; 2018-10-29, thursday different
                  {tu/route-name ["h1" "h2" "h3" "h4" "!!" "h6" "h7"]} ; friday different
                  {tu/route-name ["h1" "h2" "h3" "!!" "!!" "h6" "h7"]}))) ;; thu and fri different

(deftest test-consecutive-different-traffic-patterns
  (let [result (detection/route-weeks-with-first-difference data-consecutive-different-traffic-patterns)
        expect-first {:starting-week {:beginning-of-week (tu/to-local-date 2018 10 15)
                                      :end-of-week (tu/to-local-date 2018 10 21)}
                      :different-week {:beginning-of-week (tu/to-local-date 2018 10 22)
                                       :end-of-week (tu/to-local-date 2018 10 28)}
                      :route-key tu/route-name}]
    (testing "Test a consecutive change of different traffic patterns is detected"
      (is (= expect-first
             (select-keys
               (first result)
               tu/select-keys-detect-changes-for-all-routes))))

    (testing "Ensure that a right amount of changes are found and there are no extra changes."
      (is (= 1 (count result))))))

;;;;;;;; Test more than one change found, in many weeks when no holidays or no-traffic

(def data-more-than-one-change
  ; Produce change records about individual days -> first change week contains 2 days with differences
  ; In this test case we need to produce 3 rows in the database
  (tu/weeks (tu/to-local-date 2019 2 4)
            (list {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; Week 4.2.
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; Week 11.2.
                  {tu/route-name ["h1" "!!" "!!" "h4" "h5" "h6" "h7"]} ;; Week 18.2.
                  {tu/route-name ["h1" "!!" "!!" "h4" "h5" "h6" "h7"]} ;; Week 25.2.
                  {tu/route-name ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]} ;; Week 4.3.
                  {tu/route-name ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]} ;;
                  {tu/route-name ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]} ;;
                  {tu/route-name ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]})))

(deftest test-more-than-one-change-found
  (spec-test/instrument `detection/route-weeks-with-first-difference)

  ;; first test that the test data and old change detection code agree
  (testing "Ensure single-change detection code agrees with test data"
    (is (= (tu/to-local-date 2019 2 18) (-> data-more-than-one-change
                                            detection/route-weeks-with-first-difference
                                            first
                                            :different-week
                                            :beginning-of-week))))

  (let [result (-> data-more-than-one-change
                   detection/changes-by-week->changes-by-route
                   detection/detect-changes-for-all-routes)
        expect-first {:route-key tu/route-name
                      :starting-week {:beginning-of-week (tu/to-local-date 2019 2 11)
                                      :end-of-week (tu/to-local-date 2019 2 17)}
                      :different-week {:beginning-of-week (tu/to-local-date 2019 2 18)
                                       :end-of-week (tu/to-local-date 2019 2 24)}}
        expect-second {:route-key tu/route-name
                       :starting-week {:beginning-of-week (tu/to-local-date 2019 2 18)
                                       :end-of-week (tu/to-local-date 2019 2 24)}
                       :different-week {:beginning-of-week (tu/to-local-date 2019 3 4)
                                        :end-of-week (tu/to-local-date 2019 3 10)}}
        ]

    (testing "Ensure first change "
      (is (= expect-first
             (select-keys (first result) tu/select-keys-detect-changes-for-all-routes))))

    (testing "Ensure second change i"
      (is (= expect-second
             (select-keys (second result) tu/select-keys-detect-changes-for-all-routes))))

    (testing "Ensure that right number of changes are found and there are no extra changes."
      (is (= 2 (count result))))))

;;;;;;;; Test traffic change middle of week

(def data-traffic-change-middle-of-week
  (tu/weeks (tu/to-local-date 2019 4 15)
            (list
              {tu/route-name ["AA" "AA" "AA" "AA" "AA" "AA" "AA"]} ;; Week 2019-04-15
              {tu/route-name ["AA" "AA" "AA" "AA" "AA" "AA" "AA"]} ;; Week 2019-04-22
              {tu/route-name ["AA" "AA" "AA" "AA" "AA" "AA" "AA"]} ;; Week 2019-04-29
              {tu/route-name ["AA" "AA" "AA" "BB" "BB" "BB" "BB"]} ;; Week 2019-05-06
              {tu/route-name ["BB" "BB" "BB" "BB" "BB" "BB" "BB"]}
              {tu/route-name ["BB" "BB" "BB" "BB" "BB" "BB" "BB"]}
              {tu/route-name ["BB" "BB" "BB" "BB" "BB" "BB" "BB"]}
              {tu/route-name ["BB" "BB" "BB" "BB" "BB" "BB" "BB"]})))

(deftest test-traffic-change-middle-of-week
  (let [result (-> data-traffic-change-middle-of-week
                   (detection/changes-by-week->changes-by-route)
                   (detection/detect-changes-for-all-routes))
        expect-first {:route-key tu/route-name
                      :starting-week {:beginning-of-week (tu/to-local-date 2019 4 22)
                                      :end-of-week (tu/to-local-date 2019 4 28)}
                      :different-week {:beginning-of-week (tu/to-local-date 2019 5 6)
                                       :end-of-week (tu/to-local-date 2019 5 12)}}
        expect-second {:route-key tu/route-name
                       :starting-week {:beginning-of-week (tu/to-local-date 2019 5 6)
                                       :end-of-week (tu/to-local-date 2019 5 12)}
                       :different-week {:beginning-of-week (tu/to-local-date 2019 5 13)
                                        :end-of-week (tu/to-local-date 2019 5 19)}}]

    (testing "Ensure first change middle of week is found, when end of week traffic changes (AAAAAAA=>AAABBBB)"
      (is (=
            expect-first
            (select-keys
              (first result)
              tu/select-keys-detect-changes-for-all-routes))))

    (testing "Ensure second change is found next week, when also the beginning part of the week changes (AAABBBB=>BBBBBBB)"
      (is (= expect-second
             (select-keys
               (second result)
               tu/select-keys-detect-changes-for-all-routes))))

    (testing "Ensure that right number of changes are found and there are no extra changes."
      (is (= 2 (count result))))))

;;;;;;;; Test traffic starting point anomalous

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
    (is (= (tu/to-local-date 2018 10 22) (:beginning-of-week starting-week))) ;; gets -15, should be -22
    (is (= tu/route-name (:route-key res)))
    (is (nil? different-week))))                            ;; gets -22, should be nil

;;;;;;;; Test holiday/keyword day handling and day detection

(def data-traffic-static-holidays
  (tu/weeks (tu/to-local-date 2018 12 3)
            (list {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; Week 3.12
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; Week 10.12
                  {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; Week 17.12
                  {tu/route-name [:xmas-eve :xmas-day "h3" "h4" "h5" "h6" "h7"]} ;; Week 24.12, having static-holidays
                  {tu/route-name ["h1" :new-year "h3!" "h4!" "h5!" "h6!" "h7!"]} ;; Week 31.12, having static-holidays (1.1.)
                  {tu/route-name ["h1!" "h2!" "h3!" "h4!" "h5!" "h6!" "h7!"]} ;; Week 7.1
                  {tu/route-name ["h1!" "h2!" "h3!" "h4!" "h5!" "h6!" "h7!"]} ;; Week 14.1
                  {tu/route-name ["h1!" "h2!" "h3!" "h4!" "h5!" "h6!" "h7!"]})))

(deftest test-traffic-static-holidays2
  (let [{:keys [starting-week different-week] :as res}
        (first (detection/route-weeks-with-first-difference data-traffic-static-holidays))]

    (testing "Ensure detection skipped christmas week"
      (is (= (tu/to-local-date 2018 12 31) (:beginning-of-week different-week))))

    (testing "Ensure first different day is wednesday because tuesday is new year"
      (is (= [true false] (take 2 (mapv = (:starting-week-hash res) (:different-week-hash res))) )))))

;;;;;;;; Test holiday/keyword day handling for week detection

(deftest test-traffic-static-holidays
  (let [result (-> data-traffic-static-holidays
                   (detection/changes-by-week->changes-by-route)
                   (detection/detect-changes-for-all-routes))
        expect-first {:route-key tu/route-name
                      :starting-week {:beginning-of-week (tu/to-local-date 2018 12 10)
                                      :end-of-week (tu/to-local-date 2018 12 16)}
                      :different-week {:beginning-of-week (tu/to-local-date 2018 12 31)
                                       :end-of-week (tu/to-local-date 2019 1 6)}}
        expect-second {:route-key tu/route-name
                       :starting-week {:beginning-of-week (tu/to-local-date 2018 12 31)
                                       :end-of-week (tu/to-local-date 2019 1 6)}
                       :different-week {:beginning-of-week (tu/to-local-date 2019 1 7)
                                        :end-of-week (tu/to-local-date 2019 1 13)}}]

    (testing "Expect first different week be the one where traffic changes"
      (is (= expect-first
             (select-keys
               (first result)
               tu/select-keys-detect-changes-for-all-routes))))

    (testing "Expect second different week be the one when there's no holidays and remaining days detected as changed"
      (is (= expect-second
             (select-keys
               (second result)
               tu/select-keys-detect-changes-for-all-routes))))))

;;;;;;;; Test two routes with changes

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
      (is (= (-> fwd-difference second :different-week)
             (-> diff-maps first :different-week))))

    (testing "second route's first change date is ok"
      (is (= (tu/to-local-date 2019 2 25)
             (first
               (for [dp diff-maps
                     :let [d (-> dp :different-week :beginning-of-week)]
                     :when (= tu/route-name-2 (:route-key dp))]
                 d)))))))

;;;;;;;;; Test traffic change to pattern with nils. Adapted from production case.

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

(deftest test-change-to-summer-schedule-nil-weeks-with-midsummer
  (let [result (-> data-change-to-summer-schedule-nil-weeks-with-midsummer
                   detection/changes-by-week->changes-by-route
                   detection/detect-changes-for-all-routes)]

    (is (= {:different-week {:beginning-of-week (tu/to-local-date 2019 5 27) :end-of-week (tu/to-local-date 2019 6 2)}
            :route-key tu/route-name
            :starting-week {:beginning-of-week (tu/to-local-date 2019 5 20) :end-of-week (tu/to-local-date 2019 5 26)}}
           (select-keys
             (first result)
             tu/select-keys-detect-changes-for-all-routes)))

    (is (= {:different-week {:beginning-of-week (tu/to-local-date 2019 6 3) :end-of-week (tu/to-local-date 2019 6 9)}
            :route-key tu/route-name
            :starting-week {:beginning-of-week (tu/to-local-date 2019 5 27) :end-of-week (tu/to-local-date 2019 6 2)}
            :no-traffic-run 1
            :no-traffic-start-date (tu/to-local-date 2019 6 9)}
           (select-keys
             (second result)
             tu/select-keys-detect-changes-for-all-routes)))

    (is (= 2 (count result)))))

;;;;;;;;; Data for all routes, needed in tests where have to resolve for how long route has traffic declared

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

;;;;;;;;; Test route END reporting when traffic change to pattern with nils and then only nil. Adapted from production case.

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
  (let [result (->> data-traffic-winter-to-summer-and-end-traffic
                   detection/changes-by-week->changes-by-route
                   detection/detect-changes-for-all-routes
                   (detection/add-ending-route-change (tu/to-local-date 2019 5 20) change-window data-all-routes))]

    (testing "Expect route end instead of no-traffic, when no-traffic starts within route end threshold"
      (is (= {:route-key tu/route-name
              :route-end-date (tu/to-local-date 2019 6 23)
              :starting-week {:beginning-of-week (tu/to-local-date 2019 5 20)
                              :end-of-week (tu/to-local-date 2019 5 26)}}
             (select-keys
               (first result)
               tu/select-keys-detect-changes-for-all-routes))))

    (testing "Ensure that a right amount of changes are found and there are no extra changes."
      (is (= 1 (count result))))))

;;;;;;;;; Test route END reporting when no-traffic starts middle of week

(def data-change-nil-and-ending-route
  (tu/weeks (tu/to-local-date 2019 5 13)
            (concat [{tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019 05 13
                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019 05 20
                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019 05 27
                     {tu/route-name ["h1" "h2" nil nil nil nil nil]}] ;; 2019 06 03
                    (tu/generate-traffic-week 5 [nil nil nil nil nil nil nil]))))

(deftest test-change-nil-and-ending-route
  (let [result (->> data-change-nil-and-ending-route        ;; Notice thread-last
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
      (testing "Ensure that a right amount of changes are found and there are no extra changes."
        (is (= 1 (count result)))))))

;;;;;; Test route END reporting when last traffic date below route end detection threshold

(def data-ending-route-change
  (tu/weeks (tu/to-local-date 2019 5 13)
            (tu/generate-traffic-week 9)))                  ;; 2019 07 08

(deftest test-ending-route-change
  (let [result (->> data-ending-route-change                ;; Notice thread-last
                    (detection/changes-by-week->changes-by-route)
                    (detection/detect-changes-for-all-routes)
                    (detection/add-ending-route-change (tu/to-local-date 2019 5 20) change-window data-all-routes))]
    (is (= {:route-end-date (tu/to-local-date 2019 7 15)
            :route-key tu/route-name
            :starting-week {:beginning-of-week (tu/to-local-date 2019 5 20) :end-of-week (tu/to-local-date 2019 5 26)}}
           (-> result
               first
               (select-keys tu/select-keys-detect-changes-for-all-routes))))
    (testing "Ensure that a right amount of changes are found and there are no extra changes."
      (is (= 1 (count result))))))

;;;;;;;;; Test route END and traffic change detection

(def data-change-and-ending-route
  (tu/weeks (tu/to-local-date 2019 5 13)
            (concat (tu/generate-traffic-week 5 ["h1" "h2" "h3" "h4" "h5" "h6" "h7"])
                    ;; In below series: First wk 2019 06 17, last wk 2019 07 08
                    (tu/generate-traffic-week 4 ["h1" "h2" "h3" "h4" "h5" "!!" "!!"]))))

(deftest test-change-and-ending-route
  (let [result (->> data-change-and-ending-route            ;; Notice thread-last
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


(def change->no-traffic-data
  [{:route-key "Raimola",
    :no-traffic-run 13,
    :starting-week-hash ["h1" "h2" "h3" "h4" "h5" nil nil],
    :starting-week
    {:beginning-of-week
     (java.time.LocalDate/parse "2018-10-15"),
     :end-of-week
     (java.time.LocalDate/parse "2018-10-21")},
    :no-traffic-start-date
    (java.time.LocalDate/parse "2018-11-13"),
    :different-week-hash [nil nil nil nil nil nil nil],
    :different-week
    {:beginning-of-week
     (java.time.LocalDate/parse "2018-11-19"),
     :end-of-week
     (java.time.LocalDate/parse "2018-11-25")}}
   {:route-key "Raimola",
    :no-traffic-start-date
    (java.time.LocalDate/parse "2018-11-19"),
    :starting-week-hash [nil nil nil nil nil nil nil],
    :starting-week
    {:beginning-of-week
     (java.time.LocalDate/parse "2018-11-19"),
     :end-of-week
     (java.time.LocalDate/parse "2018-11-25")},
    :no-traffic-change 84,
    :no-traffic-end-date
    (java.time.LocalDate/parse "2019-02-11"),
    :different-week-hash ["h1" "h2" "h3" "h4" "h5" nil nil],
    :different-week
    {:beginning-of-week
     (java.time.LocalDate/parse "2019-02-11"),
     :end-of-week
     (java.time.LocalDate/parse "2019-02-17")}}])

(deftest test-change->no-traffic
  (let [[diff-a diff-b] change->no-traffic-data]
    (is (= true (detection/changes-straddle-trafficless-period? diff-a diff-b)))
    (is (= {:route-key "Raimola",
            :combined true,
            :no-traffic-start-date
            (java.time.LocalDate/parse "2018-11-13"),
            :starting-week-hash [nil nil nil nil nil nil nil],
            :starting-week
            {:beginning-of-week
             (java.time.LocalDate/parse "2018-11-19"),
             :end-of-week (java.time.LocalDate/parse "2018-11-25")},
            :no-traffic-change 97,
            :no-traffic-end-date
            (java.time.LocalDate/parse "2019-02-11"),
            :different-week-hash ["h1" "h2" "h3" "h4" "h5" nil nil],
            :different-week
            {:beginning-of-week
             (java.time.LocalDate/parse "2019-02-11"),
             :end-of-week (java.time.LocalDate/parse "2019-02-17")}}
           (detection/change-pair->no-traffic diff-a diff-b)))
    (let [merge-result (detection/trafficless-differences->no-traffic-changes [(assoc diff-a :n 1)
                                                                               (assoc diff-a :n 2)
                                                                               (assoc diff-b :n 3)])]
      (is (= 2 (count merge-result)))
      (is (:combined (second merge-result))))))
