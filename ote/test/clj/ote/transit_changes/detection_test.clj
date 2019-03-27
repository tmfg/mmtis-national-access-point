(ns ote.transit-changes.detection-test
  (:require [ote.transit-changes.detection :as detection]
            [clojure.test :as t :refer [deftest testing is]]
            [clojure.spec.test.alpha :as spec-test]
            [ote.transit-changes :as transit-changes]
            [ote.time :as time]))

(defn d [year month day]
  (java.time.LocalDate/of year month day))

(def select-keys-detect-changes-for-all-routes [:different-week
                                                :no-traffic-start-date
                                                :no-traffic-end-date
                                                :no-traffic-run
                                                :no-traffic-change
                                                :route-key
                                                :starting-week])
(def route-name "Raimola")
(def route-name-2 "Esala")

(defn weeks
  "Give first day of week (monday) as a starting-from."
  [starting-from & route-maps]
  (vec (map-indexed
        (fn [i routes]
          {:beginning-of-week (.plusDays starting-from (* i 7))
           :end-of-week (.plusDays starting-from (+ 6 (* i 7)))
           :routes routes})
        route-maps)))

;;;;;; TESTS for analysing specific weekx for changes in hash/traffic

(def data-no-changes
  (weeks (d 2019 5 13)
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}  ;; 2019 05 13
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}  ;; 2019 05 20
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}  ;; 2019 05 27
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}  ;; 2019 06 03
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}  ;; 2019 06 10
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}  ;; 2019 06 17
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}  ;; 2019 06 24
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}))

(deftest test-no-changes
  (let [result (-> data-no-changes
                   detection/changes-by-week->changes-by-route
                   detection/detect-changes-for-all-routes)]
    (is (= {:route-key route-name
            :starting-week {:beginning-of-week (d 2019 5 20) :end-of-week (d 2019 5 26)}}
           (-> result
               first
               (select-keys
                 [:no-traffic-start-date :no-traffic-end-date :route-key :no-traffic-change
                  :different-week :route-key :starting-week]))))
    (is (= 1 (count result)))))


(def data-change-on-2nd-to-last-wk
  (weeks (d 2019 5 13)
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}  ;; 2019 05 13
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}  ;; 2019 05 20
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}  ;; 2019 05 27
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}  ;; 2019 06 03
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}  ;; 2019 06 10
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}  ;; 2019 06 17
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}  ;; 2019 06 24
         {route-name ["h1" "h2" "h3" "h4" "h5" "!!" "h7"]}
         {route-name ["h1" "h2" "h3" "h4" "h5" "!!" "h7"]}))

(deftest test-change-on-2nd-to-last-wk
  (let [result (-> data-change-on-2nd-to-last-wk
                   detection/changes-by-week->changes-by-route
                   detection/detect-changes-for-all-routes)]
    (is (= {:route-key route-name
            :starting-week {:beginning-of-week (d 2019 5 20) :end-of-week (d 2019 5 26)}}
           (-> result
               first
               (select-keys ;; NOTE: Changes on 2nd to last and last week not detected currently by the analysis!
                   [:no-traffic-start-date :no-traffic-end-date :route-key :no-traffic-change
                    :different-week :route-key :starting-week]))))
    (is (= 1 (count result)))))

(def data-no-changes-weekend-nil
  (weeks (d 2019 5 13)
         {route-name ["h1" "h2" "h3" "h4" "h5" nil nil]}  ;; 2019 05 13
         {route-name ["h1" "h2" "h3" "h4" "h5" nil nil]}  ;; 2019 05 20
         {route-name ["h1" "h2" "h3" "h4" "h5" nil nil]}  ;; 2019 05 27
         {route-name ["h1" "h2" "h3" "h4" "h5" nil nil]}  ;; 2019 06 03
         {route-name ["h1" "h2" "h3" "h4" "h5" nil nil]}  ;; 2019 06 10
         {route-name ["h1" "h2" "h3" "h4" "h5" nil nil]}  ;; 2019 06 17
         {route-name ["h1" "h2" "h3" "h4" "h5" nil nil]}  ;; 2019 06 17
         {route-name ["h1" "h2" "h3" "h4" "h5" nil nil]}  ;; 2019 06 17
         {route-name ["h1" "h2" "h3" "h4" "h5" nil nil]}))

(deftest test-no-changes-weekend-nil
  (let [result (-> data-no-changes-weekend-nil
                   detection/changes-by-week->changes-by-route
                   detection/detect-changes-for-all-routes)]
    (testing "Weekend traffic nil, ensure :no-traffic is not reported"
      (is (= {:route-key route-name
              :starting-week {:beginning-of-week (d 2019 5 20) :end-of-week (d 2019 5 26)}}
             (-> result
                 first
                 (select-keys select-keys-detect-changes-for-all-routes))))
      (is (= 1 (count result))))))

(def data-test-no-traffic-run
  (weeks (d 2018 10 8)
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name ["h1" "h2" nil nil nil nil nil]} ; 4 day run
         {route-name [nil nil nil nil nil nil nil]} ; 7 days
         {route-name [nil nil nil nil nil "h6" "h7"]} ; 4 days => sum 17
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}))

(deftest no-traffic-run-is-detected
  (testing "no-traffic starts and ends middle of week, ensure not no-traffic reported"
    (is (= {:route-key route-name
            :starting-week {:beginning-of-week (d 2018 10 15) :end-of-week (d 2018 10 21)}
            :no-traffic-change 17
            :no-traffic-start-date (d 2018 10 17)
            :no-traffic-end-date (d 2018 11 3)}
           (-> #_(detection/route-weeks-with-first-difference-new data-test-no-traffic-run)
             data-test-no-traffic-run
             detection/changes-by-week->changes-by-route
             detection/detect-changes-for-all-routes
             first
             (select-keys select-keys-detect-changes-for-all-routes))))))

(def data-no-traffic-run-twice
  (weeks (d 2018 10 8)
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name [nil nil nil nil nil nil nil]}
         {route-name [nil nil nil nil nil nil nil]}
         {route-name [nil nil nil nil nil nil nil]}
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name [nil nil nil nil nil nil nil]}
         {route-name [nil nil nil nil nil nil nil]}
         {route-name [nil nil nil nil nil nil nil]}
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}))

(deftest test-no-traffic-run-twice-is-detected
  (let [test-result (-> data-no-traffic-run-twice
                        detection/changes-by-week->changes-by-route
                        detection/detect-changes-for-all-routes)]
    (testing "no-traffic run twice, ensure first run is reported"
      (is (= {:no-traffic-start-date (d 2018 10 15)
              :no-traffic-end-date (d 2018 11 5)
              :route-key "Raimola"
              :no-traffic-change 21}
             (-> (first test-result)
                 (select-keys [:no-traffic-start-date :no-traffic-end-date :route-key :no-traffic-change])))))
    (testing "no-traffic run twice, ensure second run is reported"
      (is (= {:no-traffic-start-date (d 2018 11 26)
              :no-traffic-end-date (d 2018 12 17)
              :route-key "Raimola"
              :no-traffic-change 21}
             (-> (second test-result)
                 (select-keys [:no-traffic-start-date :no-traffic-end-date :route-key :no-traffic-change])))))))

(def test-no-traffic-run-weekdays
  (weeks (d 2018 10 8)
         {route-name ["h1" "h2" "h3" "h4" "h5" nil nil]} ;; 8.10.
         {route-name ["h1" "h2" "h3" "h4" "h5" nil nil]} ;; 15.10.
         {route-name ["h1" "h2" "h3" "h4" "h5" nil nil]} ;; 22.10.
         {route-name ["h1" "h2" "h3" "h4" "h5" nil nil]} ;; 29.10.
         {route-name ["h1" "h2" "h3" "h4" "h5" nil nil]} ;; 5.11.
         {route-name ["h1" nil nil nil nil nil nil]}     ;; Starts 13.11, 6 day run
         {route-name [nil nil nil nil nil nil nil]}      ;;        7 days
         {route-name [nil nil nil nil "h5" nil nil]}     ;; Ends 30.11, 4 days => sum 17
         {route-name ["h1" "h2" "h3" "h4" "h5" nil nil]}
         {route-name ["h1" "h2" "h3" "h4" "h5" nil nil]}
         {route-name ["h1" "h2" "h3" "h4" "h5" nil nil]}))

(deftest no-traffic-run-weekdays-is-detected
  (testing "Ensure no-traffic run is reported and normal no-traffic weekends are not. "
    (is (= {:route-key route-name
            :starting-week {:beginning-of-week (d 2018 10 15) :end-of-week (d 2018 10 21)}
            :no-traffic-change 17
            :no-traffic-start-date (d 2018 11 13)
            :no-traffic-end-date (d 2018 11 30)}
           (-> (detection/route-weeks-with-first-difference-new test-no-traffic-run-weekdays)
               first
               (select-keys select-keys-detect-changes-for-all-routes))))))

(def no-traffic-run-full-detection-window
  (weeks (d 2018 10 8)
         {route-name ["h1" "h2" "h3" "h4" "h5" nil nil]}    ;; 8.10.
         {route-name ["h1" "h2" "h3" "h4" "h5" nil nil]}    ;; 15.10.
         {route-name ["h1" "h2" "h3" "h4" "h5" nil nil]}    ;; 22.10.
         {route-name ["h1" "h2" "h3" "h4" "h5" nil nil]}    ;; 29.10.
         {route-name ["h1" "h2" "h3" "h4" "h5" nil nil]}    ;; 5.11.
         {route-name ["h1" nil nil nil nil nil nil]}        ;; 12.11, Starting 13.11. 6 day run
         {route-name [nil nil nil nil nil nil nil]}
         {route-name [nil nil nil nil nil nil nil]}
         {route-name [nil nil nil nil nil nil nil]}
         {route-name [nil nil nil nil nil nil nil]}
         {route-name [nil nil nil nil nil nil nil]}
         {route-name [nil nil nil nil nil nil nil]}
         {route-name [nil nil nil nil nil nil nil]}
         {route-name [nil nil nil nil nil nil nil]}
         {route-name [nil nil nil nil nil nil nil]}
         {route-name [nil nil nil nil nil nil nil]}
         {route-name [nil nil nil nil nil nil nil]}
         {route-name [nil nil nil nil nil nil nil]}))

(deftest test-no-traffic-run-full-detection-window
  (testing "Ensure traffic with normal no-traffic days detects a no-traffic change correctly"
    (let [result (-> no-traffic-run-full-detection-window
                     detection/changes-by-week->changes-by-route
                     detection/detect-changes-for-all-routes)]
      (is (= {:route-key route-name
              :starting-week {:beginning-of-week (d 2018 10 15) :end-of-week (d 2018 10 21)}
              :no-traffic-run 76
              :no-traffic-start-date (d 2018 11 13)}
             (-> (first result)
                 (select-keys select-keys-detect-changes-for-all-routes))))

      (is (= 1 (count result))))))

(def test-traffic-2-different-weeks
  (weeks (d 2018 10 8)
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; starting point
         {route-name ["h1" "h2" "!!" "h4" "h5" "h6" "h7" ]} ; wednesday different
         {route-name ["h1" "h2" "h3" "!!" "h5" "h6" "h7" ]} ; thursday different
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}  ; back to normal
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}))

(deftest two-week-difference-is-skipped
  (is (nil?
       (get-in (detection/route-weeks-with-first-difference-new test-traffic-2-different-weeks)
               [route-name :different-week]))))

(def normal-to-1-different-to-1-normal-and-rest-are-changed
  (weeks (d 2019 1 28)
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; prev week
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; starting week
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; normal
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; normal
         {route-name ["!!" "!!" "!!" "!!" "!!" "h6" "h7"]} ; first different week - should be skipper
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; back to normal
         {route-name ["h1" "h2" "h3" "h4" "!!" "h6" "!!"]} ; new schedule - should be found as different week
         {route-name ["h1" "h2" "h3" "h4" "!!" "h6" "!!"]} ; new schedule
         {route-name ["h1" "h2" "h3" "h4" "!!" "h6" "!!"]} ; New schedule
         {route-name ["h1" "h2" "h3" "h4" "!!" "h6" "!!"]})); New schedule

(deftest one-week-difference-is-skipped
  (let [result (detection/route-weeks-with-first-difference-new normal-to-1-different-to-1-normal-and-rest-are-changed)]
    (is (= {:beginning-of-week (d 2019 3 11)
            :end-of-week (d 2019 3 17)}
           (:different-week (first result))
           ))))

(def test-traffic-normal-difference
  (weeks (d 2018 10 8)
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; 2018-10-08
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; 2018-10-15, starting point
         {route-name ["h1" "h2" "!!" "h4" "h5" "h6" "h7" ]} ; 2018-10-22, wednesday different
         {route-name ["h1" "h2" "h3" "!!" "h5" "h6" "h7" ]} ; 2018-10-29, thursday different
         {route-name ["h1" "h2" "h3" "h4" "!!" "h6" "h7"]} ; friday different
         {route-name ["h1" "h2" "h3" "!!" "!!" "h6" "h7"]})) ;; thu and fri different

(deftest normal-difference
  (is (= {:starting-week-hash ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]
          :starting-week {:beginning-of-week (d 2018 10 15)
                          :end-of-week (d 2018 10 21)}
          :different-week-hash  ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]
          :different-week {:beginning-of-week (d 2018 10 22)
                           :end-of-week (d 2018 10 28)}
          :route-key "Raimola"}
         (first (detection/route-weeks-with-first-difference-new test-traffic-normal-difference)))))

(def test-traffic-starting-point-anomalous
  (weeks (d 2018 10 8)
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; 2018-10-08
         {route-name ["h1!" "h2!" "h3!" "h4!" "h5!" "h5!" "h7!"]} ; 2018-10-15, starting week is an exception
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; 2018-10-22, next week same as previous
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; 2018-10-29
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}))

(deftest anomalous-starting-point-is-ignored
  (let [{:keys [starting-week different-week] :as res}
        (-> test-traffic-starting-point-anomalous
            detection/route-weeks-with-first-difference-new
            first)]
    (is (= (d 2018 10 22) (:beginning-of-week starting-week))); gets -15, should be -22
    (is (= "Raimola" (:route-key res)))
    (is (nil? different-week)))) ;; gets -22, should be nil

(def test-traffic-static-holidays
  (weeks (d 2018 12 3)
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 3.12
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 10.12
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 17.12
         {route-name [:xmas-eve :xmas-day "h3" "h4" "h5" "h6" "h7"]} ; Week having static-holidays - 24.12
         {route-name ["h1" :new-year "h3!" "h4!" "h5!" "h6!" "h7!"]} ; Week having static-holidays (1.1.) 31.12
         {route-name ["h1!" "h2!" "h3!" "h4!" "h5!" "h6!" "h7!"]} ;; 7.1
         {route-name ["h1!" "h2!" "h3!" "h4!" "h5!" "h6!" "h7!"]} ;; 14.1
         {route-name ["h1!" "h2!" "h3!" "h4!" "h5!" "h6!" "h7!"]})) ;; 21.1

(deftest static-holidays-are-skipped
  (let [{:keys [starting-week different-week] :as res}
        (first (detection/route-weeks-with-first-difference-new test-traffic-static-holidays))]

    (testing "detection skipped christmas week"
      (is (= (d 2018 12 31) (:beginning-of-week different-week))))

    (testing "first different day is wednesday because tuesday is new year"
      (is (= 2 (transit-changes/first-different-day (:starting-week-hash res) (:different-week-hash res)))))))

(def test-more-than-one-change  
  ; Produce change records about individual days -> first change week contains 2 days with differences
  ; In this test case we need to produce 3 rows in the database
  (weeks (d 2019 2 4)
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 4.2.
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; first current week (11.2.)
         {route-name ["h1" "!!" "!!" "h4" "h5" "h6" "h7"]} ;; 18.2. first change -> only one found currently | needs to detect change also on 13.2. -> this is set to current week
         {route-name ["h1" "!!" "!!" "h4" "h5" "h6" "h7"]} ;; no changes here (25.2.)
         {route-name ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]} ;; Only tuesday is found (4.3.)
         {route-name ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]} ;;
         {route-name ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]} ;;
         {route-name ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]}))

(def test-more-than-one-change-2-routes
  ; Produce change records about individual days -> first change week contains 2 days with differences
  ; In this test case we need to produce 3 rows in the database
  (weeks (d 2019 2 4)
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]
        route-name-2 ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 4.2.
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]
        route-name-2 ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; first current week (11.2.)
         {route-name ["h1" "!!" "!!" "h4" "h5" "h6" "h7"]
        route-name-2 ["h1" "!!" "!!" "h4" "h5" "h6" "h7"]} ;; 18.2. first change -> only one found currently | needs to detect change also on 13.2. -> this is set to current week
         {route-name ["h1" "!!" "!!" "h4" "h5" "h6" "h7"]
        route-name-2 ["h1" "!!" "!!" "h4" "h5" "h6" "h7"]} ;; no changes here (25.2.)
         {route-name ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]
        route-name-2 ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]} ;; Only tuesday is found (4.3.)
         {route-name ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]
        route-name-2 ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]} ;;
         {route-name ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]
        route-name-2 ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]} ;;
         {route-name ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]
        route-name-2 ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]}))

(def data-two-week-change
  (weeks (d 2019 2 4)
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 4.2.
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; first current week (11.2.)
         {route-name ["h1" "!!" "!!" "h4" "h5" "h6" "h7"]}
         {route-name ["h1" "!!" "!!" "h4" "h5" "h6" "h7"]}
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}))

(deftest more-than-one-change-found
  (spec-test/instrument `detection/route-weeks-with-first-difference-old)

  ;; first test that the test data and old change detection code agree
  (testing "single-change detection code agrees with test data"
    (is (= (d 2019 2 18) (-> test-more-than-one-change
                             detection/route-weeks-with-first-difference-new
                             first
                             :different-week
                             :beginning-of-week))))
  
  (let [diff-maps (-> test-more-than-one-change
                       detection/changes-by-week->changes-by-route
                       detection/detect-changes-for-all-routes)
        old-diff-maps (detection/route-weeks-with-first-difference-old test-more-than-one-change)]
    (testing "got two changes"
      (is (= 2 (count diff-maps))))
    (testing "first change is detected"
      (is (= (d 2019 2 18) (-> diff-maps
                               first
                               :different-week
                               :beginning-of-week))))

    (testing "second change is detected"
      (is (= (d 2019 3 4) (-> diff-maps
                              second
                              :different-week
                              :beginning-of-week))))))

(def data-two-week-two-route-change                         ;; This is the same format as the (combine-weeks) function
  (weeks (d 2019 2 4)
         {route-name   ["h1" "h2" "h3" "h4" "h5" "h6" "h7"] route-name-2 ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name   ["h1" "h2" "h3" "h4" "h5" "h6" "h7"] route-name-2 ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; 11.2. prev week start
         {route-name   ["h1" "##" "h3" "h4" "h5" "h6" "h7"] route-name-2 ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; 18.2. first change in route1
         {route-name   ["h1" "##" "h3" "h4" "h5" "h6" "h7"] route-name-2 ["h1" "h2" "##" "h4" "h5" "h6" "h7"]}
         {route-name   ["h1" "##" "h3" "h4" "h5" "h6" "h7"] route-name-2 ["h1" "h2" "##" "h4" "h5" "h6" "h7"]}
         {route-name   ["h1" "##" "h3" "h4" "h5" "h6" "h7"] route-name-2 ["h1" "h2" "##" "h4" "h5" "h6" "h7"]}
         {route-name   ["h1" "h2" "h3" "h4" "h5" "h6" "h7"] route-name-2 ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name   ["h1" "h2" "h3" "h4" "h5" "h6" "h7"] route-name-2 ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name   ["h1" "h2" "h3" "h4" "h5" "h6" "h7"] route-name-2 ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}))

(deftest more-than-one-change-found-w-2-routes
  (let [diff-maps (-> data-two-week-two-route-change
                       (detection/changes-by-week->changes-by-route)
                       (detection/detect-changes-for-all-routes))

        fwd-difference-old (detection/route-weeks-with-first-difference-old data-two-week-two-route-change)
        fwd-difference-new (detection/route-weeks-with-first-difference-new data-two-week-two-route-change)]
    (testing "first change matches first-week-difference return value"
      (is (= (-> fwd-difference-old (get "Raimola") :different-week) (-> diff-maps first :different-week)))
      (is (= (-> fwd-difference-new second :different-week) (-> diff-maps first :different-week))))

    (testing "second route's first change date is ok"
      (is (= (d 2019 2 25)
             (first
              (for [dp diff-maps
                    :let [d (-> dp :different-week :beginning-of-week)]
                    :when (= "Esala" (:route-key dp))]
                d)))))))


(deftest no-change-found
  (spec-test/instrument `detection/route-weeks-with-first-difference-new)

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
;  #(update % :routes select-keys [route-name])
;  *res)
; </snippet>

(def data-realworld-two-change-case
  [{:beginning-of-week (java.time.LocalDate/parse "2019-02-18"),
    :end-of-week (java.time.LocalDate/parse "2019-02-24"),
    :routes {route-name [nil nil nil nil nil nil "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-02-25"),
    :end-of-week (java.time.LocalDate/parse "2019-03-03"),
    :routes {route-name ["heka" "heka" "heka" "heka" "heka" "htoka" "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-03-04")
    :end-of-week (java.time.LocalDate/parse "2019-03-10"),
    :routes {route-name ["heka" "heka" "heka" "heka" "heka" "htoka" "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-03-11"),
    :end-of-week (java.time.LocalDate/parse "2019-03-17"),
    :routes {route-name ["heka" "heka" "heka" "heka" "heka" "htoka" "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-03-18"),
    :end-of-week (java.time.LocalDate/parse "2019-03-24"),
    :routes {route-name ["heka" "heka" "heka" "heka" "heka" "htoka" "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-03-25"),
    :end-of-week (java.time.LocalDate/parse "2019-03-31"),
    :routes {route-name ["heka" "heka" "heka" "heka" "heka" "htoka" "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-04-01"),
    :end-of-week (java.time.LocalDate/parse "2019-04-07"),
    :routes {route-name ["heka" "heka" "heka" "heka" "heka" "htoka" "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-04-08"),
    :end-of-week (java.time.LocalDate/parse "2019-04-14"),
    :routes {route-name ["heka" "heka" "heka" "heka" "heka" "htoka" "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-04-15"),
    :end-of-week (java.time.LocalDate/parse "2019-04-21"),
    :routes {route-name ["heka" "heka" "heka" "heka" "hkolmas" "htoka" "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-04-22"),
    :end-of-week (java.time.LocalDate/parse "2019-04-28"),
    :routes {route-name ["hkolmas" "heka" "heka" "heka" "heka" "htoka" "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-04-29"),
    :end-of-week (java.time.LocalDate/parse "2019-05-05"),
    :routes {route-name ["heka" "heka" "heka" "heka" "heka" "htoka" "hkolmas"]}} ;; In data third value was :first-of-may
   {:beginning-of-week (java.time.LocalDate/parse "2019-05-06"),
    :end-of-week (java.time.LocalDate/parse "2019-05-12"),
    :routes {route-name ["heka" "heka" "heka" "heka" "heka" "htoka" "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-05-13"),
    :end-of-week (java.time.LocalDate/parse "2019-05-19"),
    :routes {route-name ["heka" "heka" "heka" "heka" "heka" "htoka" "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-05-20"),
    :end-of-week (java.time.LocalDate/parse "2019-05-26"),
    :routes {route-name ["heka" "heka" "heka" "heka" "heka" "htoka" "hkolmas"]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-05-27"), ;; first change 
    :end-of-week (java.time.LocalDate/parse "2019-06-02"),
    :routes {route-name ["heka" "hkolmas" nil nil nil nil nil]}} 
   {:beginning-of-week (java.time.LocalDate/parse "2019-06-03"),
    :end-of-week (java.time.LocalDate/parse "2019-06-09"),
    :routes {route-name ["hneljas" "hneljas" "hneljas" "hneljas" "hneljas" nil nil]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-06-10"),
    :end-of-week (java.time.LocalDate/parse "2019-06-16"),
    :routes {route-name ["hneljas" "hneljas" "hneljas" "hneljas" "hneljas" nil nil]}}
   {:beginning-of-week (java.time.LocalDate/parse "2019-06-17"),
    :end-of-week (java.time.LocalDate/parse "2019-06-23"),
    :routes {route-name ["hneljas" "hneljas" "hneljas" "hneljas" nil nil nil]}}


   ;; padding added
   {:beginning-of-week (java.time.LocalDate/parse "2019-06-24"),
    :end-of-week (java.time.LocalDate/parse "2019-06-30"),
    :routes {route-name ["hneljas" "hneljas" "hneljas" "hneljas" nil nil nil]}}

   {:beginning-of-week (java.time.LocalDate/parse "2019-07-01"),
    :end-of-week (java.time.LocalDate/parse "2019-07-01"),
    :routes {route-name ["hneljas" "hneljas" "hneljas" "hneljas" nil nil nil]}}

   {:beginning-of-week (java.time.LocalDate/parse "2019-07-08"),
    :end-of-week (java.time.LocalDate/parse "2019-07-14"),
    :routes {route-name ["hneljas" "hneljas" "hneljas" "hneljas" nil nil nil]}}])

;; this doesn't compile or work in normal "lein test" run due to the ote.main/ote reference, uncomment for repl use
#_(deftest test-with-gtfs-package-of-a-service
  (let [db (:db ote.main/ote)
        route-query-params {:service-id 5 :start-date (time/parse-date-eu "18.02.2019") :end-date (time/parse-date-eu "06.07.2019")}
        new-diff (detection/detect-route-changes-for-service-new db route-query-params)
        old-diff (detection/detect-route-changes-for-service-old db route-query-params)]
    ;; new-diff structure:
    ;; :route-changes [ ( [ routeid {dada} ]) etc
    (println (:start-date route-query-params))
    (testing "differences between new and old"
      (is (= old-diff new-diff)))))

(deftest more-than-one-change-found-case-2
  (spec-test/instrument `detection/route-weeks-with-first-difference-new)

  ;; first test that the test data and old change detection code agree
  (testing "single-change detection code agrees with test data"
    (is (= (d 2019 5 27) (-> data-realworld-two-change-case
                             detection/route-weeks-with-first-difference-new
                             first
                             :different-week
                             :beginning-of-week))))

  (let [diff-maps (-> data-realworld-two-change-case
                       detection/changes-by-week->changes-by-route
                       detection/detect-changes-for-all-routes)
        old-diff-map (-> data-realworld-two-change-case
                          detection/route-weeks-with-first-difference-old)]
    (testing "got two changes"
      (is (= 2 (count diff-maps))))
    
    (testing "first change is detected"
      (is (= (d 2019 5 27) (-> diff-maps first :different-week :beginning-of-week))))

    (testing "second change date is correct"
      (is (= (d 2019 6 3) (-> diff-maps second :different-week :beginning-of-week))))))

(def data-change-to-summer-schedule-nil-weeks-with-midsummer
  (weeks (d 2019 5 13)
         {route-name ["1A" "1A" "1A" "1A" "1A" "6A" "7A"]} ;; 2019 05 13
         {route-name ["1A" "1A" "1A" "1A" "1A" "6A" "7A"]} ;; 2019 05 20
         {route-name ["1A" "1A" "1A" "7A" "5B" "6A" "7B"]} ;; 2019 05 27
         {route-name [nil nil nil nil nil "B6" nil]}       ;; 2019 06 03
         {route-name [nil nil nil nil nil "B6" nil]}       ;; 2019 06 10
         {route-name [nil nil nil nil "C5" nil nil]}       ;; 2019 06 17
         {route-name [nil nil nil nil nil "B6" nil]}       ;; 2019 06 24
         {route-name [nil nil nil nil nil "B6" nil]}
         {route-name [nil nil nil nil nil "B6" nil]}
         {route-name [nil nil nil nil nil "B6" nil]}
         {route-name [nil nil nil nil "C5" "B6" nil]}
         {route-name [nil nil nil nil nil "B6" nil]}
         {route-name [nil nil nil nil nil "B6" nil]}))

(def data-traffic-winter-to-summer-and-end-traffic
  (weeks (d 2019 5 13)
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}  ;; 2019 05 13
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}  ;; 2019 05 20
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}  ;; 2019 05 27
         {route-name [nil nil nil nil nil "h6" nil]}        ;; 2019 06 03
         {route-name [nil nil nil nil nil "h6" nil]}        ;; 2019 06 10
         {route-name [nil nil nil nil nil "h6" nil]}        ;; 2019 06 17
         {route-name [nil nil nil nil nil nil nil]}         ;; 2019 06 24
         {route-name [nil nil nil nil nil nil nil]}
         {route-name [nil nil nil nil nil nil nil]}
         {route-name [nil nil nil nil nil nil nil]}))

(deftest test-traffic-winter-to-summer-and-end-traffic
  (let [result (-> data-traffic-winter-to-summer-and-end-traffic
                   detection/changes-by-week->changes-by-route
                   detection/detect-changes-for-all-routes)]
    (is (= {:no-traffic-start-date (d 2019 6 23)
            :route-key route-name
            :starting-week {:beginning-of-week (d 2019 5 20) :end-of-week (d 2019 5 26)}}
           (select-keys (nth result 0)
                        [:no-traffic-start-date :no-traffic-end-date :route-key :no-traffic-change
                         :different-week :route-key :starting-week])))
    (is (= 1 (count result)))))

(deftest test-change-to-summer-schedule-nil-weeks-with-midsummer
  (let [result (-> data-change-to-summer-schedule-nil-weeks-with-midsummer
                   detection/changes-by-week->changes-by-route
                   detection/detect-changes-for-all-routes)]
    (is (= {:different-week {:beginning-of-week (d 2019 5 27) :end-of-week (d 2019 6 2)}
            :route-key route-name
            :starting-week {:beginning-of-week (d 2019 5 20) :end-of-week (d 2019 5 26)}}
           (-> (nth result 0)
               (select-keys [:no-traffic-start-date :no-traffic-end-date :route-key :no-traffic-change
                             :different-week :route-key :starting-week]))))

    (is (= {:different-week {:beginning-of-week (d 2019 6 3) :end-of-week (d 2019 6 9)}
            :route-key route-name
            :starting-week {:beginning-of-week (d 2019 5 27) :end-of-week (d 2019 6 2)}
            :no-traffic-start-date (d 2019 6 9)}
           (-> (nth result 1)
               (select-keys [:no-traffic-start-date :no-traffic-end-date :route-key :no-traffic-change
                             :different-week :route-key :starting-week]))))
    (is (= 2 (count result)))))

;;;;;; TESTS for analysing specific days for changes in hash/traffic

;; Day hash data for changes for a default week with ONE kind of day hashes
(def data-wk-hash-one-kind            ["A" "A" "A" "A" "A" "A" "A"])
(def data-wk-hash-one-kind-change-one  ["A" "A" "3" "3" "3" "3" "3"])
(def data-wk-hash-one-kind-change-two ["A" "A" "3" "3" "3" "3" "7"])
;; Day hash data for changes for a default week with TWO kind of day hashes
(def data-wk-hash-two-kind            ["A" "A" "A" "A" "A" "B" "B" ])
(def data-wk-hash-two-kind-change-one ["A" "A" "A" "A" "A" "5" "5" ])
(def data-wk-hash-two-kind-change-two ["1" "1" "1" "1" "1" "5" "5" ])
(def data-wk-hash-traffic-weekdays-nil-weekend-traffic [nil nil nil nil nil "C5" "C6"])
(def data-wk-hash-traffic-nil         [nil nil nil nil nil nil nil])

(def data-wk-hash-two-kind-on-weekend            ["A" "A" "A" "A" "A" "D" "E" ])
(def data-wk-hash-traffic-weekdays-nil-weekend-nil [nil nil nil nil nil "D2" "E2"])
;; Day hash data for changes for a default week with FIVE kind of day hashes
(def data-wk-hash-five-kind           ["A" "B" "B" "B" "F" "G" "H"])
(def data-wk-hash-five-kind-change-four  ["A" "2" "5" "5" "5" "6" "7"])
(def data-wk-hash-seven-kind             ["A" "C" "D" "E" "F" "G" "H"])
(def data-wk-hash-five-kind-change-seven ["1" "2" "3" "4" "5" "6" "7"])

(deftest test-changed-days-of-week
  (testing "One kind of traffic, changes: 0"
    (is (= [] (transit-changes/changed-days-of-week data-wk-hash-one-kind data-wk-hash-one-kind))))

  (testing "One kind of traffic, changes: 1"
    (is (= [2] (transit-changes/changed-days-of-week data-wk-hash-one-kind data-wk-hash-one-kind-change-one))))

  (testing "One kind of traffic, changes: 3"
    (is (= [2 6] (transit-changes/changed-days-of-week data-wk-hash-one-kind data-wk-hash-one-kind-change-two))))

  (testing "Two kinds of traffic, changes: 1 (weekend)"
    (is (= [5] (transit-changes/changed-days-of-week data-wk-hash-two-kind data-wk-hash-two-kind-change-one))))

  (testing "Two kinds of traffic, changes: 2 (weekend+week)"
    (is (= [0 5] (transit-changes/changed-days-of-week data-wk-hash-two-kind data-wk-hash-two-kind-change-two))))

  (testing "Two kinds of traffic, changes to nil on weekdays, different on weekend"
    (is (= [0 5 6] (transit-changes/changed-days-of-week data-wk-hash-two-kind data-wk-hash-traffic-weekdays-nil-weekend-traffic))))

  (testing "Two kinds of traffic, changes to nil"
    (is (= [0 5] (transit-changes/changed-days-of-week data-wk-hash-two-kind data-wk-hash-traffic-nil))))

  (testing "Five kinds of traffic, changes: 0"
    (is (= [] (transit-changes/changed-days-of-week data-wk-hash-five-kind data-wk-hash-five-kind))))

  (testing "Five kinds of traffic, changes: 5"
    (is (= [1 2 4 5 6] (transit-changes/changed-days-of-week data-wk-hash-five-kind data-wk-hash-five-kind-change-four))))

  (testing "Seven kinds of traffic, changes: 7"
    (is (= [0 1 2 3 4 5 6] (transit-changes/changed-days-of-week data-wk-hash-seven-kind data-wk-hash-five-kind-change-seven))))

  (testing "Two kinds of traffic, changes to nil on weekdays, different on weekend2"
    (is (= [0 5 6] (transit-changes/changed-days-of-week data-wk-hash-two-kind-on-weekend data-wk-hash-traffic-weekdays-nil-weekend-nil)))))
