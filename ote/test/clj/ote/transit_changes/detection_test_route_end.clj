(ns ote.transit-changes.detection-test-route-end
  (:require [ote.transit-changes.detection :as detection]
            [clojure.test :as t :refer [deftest testing is]]
            [clojure.spec.test.alpha :as spec-test]
            [ote.transit-changes :as transit-changes]
            [ote.transit-changes.detection-test-utilities :as tu]
            [ote.time :as time]
            [ote.config.transit-changes-config :as config-tc]))

;;;;;;;;; Test route END reporting

(deftest test-traffic-winter-to-summer-and-end
  (let [analysis-date (tu/to-local-date 2019 5 20)
        all-routes (tu/create-data-all-routes (list '(2019 5 13) '(2019 6 22)))
        test-data (tu/weeks (tu/to-local-date 2019 5 13)    ;; Test data adapted from production case
                            (concat [{tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019-05-13
                                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019-05-20
                                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019-05-27
                                     {tu/route-name [nil nil nil nil nil "h6" nil]} ;; 2019-06-10
                                     {tu/route-name [nil nil nil nil nil "h6" nil]} ;; 2019-06-03
                                     {tu/route-name [nil nil nil nil nil "h6" nil]} ;; 2019-06-17, traffic on 22nd
                                     {tu/route-name [nil nil nil nil nil nil nil]}]))
        result (detection/traffic-week-maps->change-maps analysis-date all-routes test-data)]

    (testing "Test route END reporting when traffic change to pattern with nils and then only nil."
      (is (= {:change-type :changed
              :route-key tu/route-name
              :different-week {:beginning-of-week (tu/to-local-date 2019 6 3)
                               :end-of-week (tu/to-local-date 2019 6 9)}
              :starting-week {:beginning-of-week (tu/to-local-date 2019 5 20)
                              :end-of-week (tu/to-local-date 2019 5 26)}}
             (select-keys (first result) tu/select-keys-detect-changes-for-all-routes))
          "Expect change to a week with nils")

      (is (= {:change-type :removed
              :route-key tu/route-name
              :change-date (tu/to-local-date 2019 6 23)}
             (select-keys (second result) tu/select-keys-detect-changes-for-all-routes))
          "Expect route end instead of no-traffic, when no-traffic starts within route end threshold"))

    (is (= 2 (count result)) "Ensure that a right amount of changes are found and there are no extra changes.")))

(deftest test-change-nil-and-end
  (let [analysis-date (tu/to-local-date 2019 5 20)
        all-routes (tu/create-data-all-routes (list '(2019 5 13) '(2019 7 22)))
        test-data (tu/weeks (tu/to-local-date 2019 5 13)
                            (concat [{tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019 05 13
                                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019 05 20
                                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019 05 27
                                     {tu/route-name ["h1" "h2" nil nil nil nil nil]}] ;; 2019 06 03
                                    (tu/generate-traffic-week 5 [nil nil nil nil nil nil nil])))
        result (detection/traffic-week-maps->change-maps analysis-date all-routes test-data)]

    (testing "Test route END reporting when no-traffic starts middle of week"
      (is (= {:change-type :removed
              :change-date (tu/to-local-date 2019 6 5)
              :route-key tu/route-name}
             (select-keys (first result) tu/select-keys-detect-changes-for-all-routes))
          "Ensure route end is reported when no-traffic starts, even if route max-date is later. If that is possible...")

      (is (= 1 (count result)) "Ensure that a right amount of changes are found and there are no extra changes."))))

(deftest test-change-route-end
  (let [analysis-date (tu/to-local-date 2019 5 20)
        all-routes (tu/create-data-all-routes (list '(2019 5 13) '(2019 7 22)))
        test-data (tu/weeks (tu/to-local-date 2019 5 13)
                            (concat [{tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019 05 13
                                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019 05 20
                                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019 05 27
                                     {tu/route-name [nil nil nil nil nil nil nil]}] ;; 2019 06 03
                                    (tu/generate-traffic-week 5 [nil nil nil nil nil nil nil])))
        result (detection/traffic-week-maps->change-maps analysis-date all-routes test-data)]

    (testing "Test route END reporting when no-traffic starts beginning of week"
      (is (= {:change-type :removed
              :change-date (tu/to-local-date 2019 6 3)
              :route-key tu/route-name}
             (select-keys (first result) tu/select-keys-detect-changes-for-all-routes))
          "Ensure route end is reported when no-traffic starts")

      (is (= 1 (count result)) "Ensure that a right amount of changes are found and there are no extra changes."))))

(deftest test-change-and-ending-route
  (let [analysis-date (tu/to-local-date 2019 5 20)
        all-routes (tu/create-data-all-routes (list '(2019 5 13) '(2019 7 7)))
        test-data (tu/weeks (tu/to-local-date 2019 5 13)
                            (concat [{tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019-05-13
                                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019-05-20
                                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019-05-27
                                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019-06-03
                                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "--" "--"]} ;; 2019-06-10
                                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "--" "--"]} ;; 2019-06-17
                                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "--" "--"]} ;; 2019-06-24
                                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "--" "--"]}])) ;; 2019-07-01, last day 7th
        result (detection/traffic-week-maps->change-maps analysis-date all-routes test-data)]
    (testing "Test route END and traffic change detection"
      (is (= {:change-type :changed
              :different-week {:beginning-of-week (tu/to-local-date 2019 6 10)
                               :end-of-week (tu/to-local-date 2019 6 16)}
              :route-key tu/route-name
              :starting-week {:beginning-of-week (tu/to-local-date 2019 5 20) :end-of-week (tu/to-local-date 2019 5 26)}}
             (select-keys (first result) tu/select-keys-detect-changes-for-all-routes)))

      (is (= {:change-type :removed
              :change-date (tu/to-local-date 2019 7 8)
              :route-key tu/route-name}
             (select-keys (second result) tu/select-keys-detect-changes-for-all-routes)))
      (is (= 2 (count result)) "Ensure that a right amount of changes are found and there are no extra changes."))))

(deftest test-paused-traffic-with-end
  (let [analysis-date (tu/to-local-date 2019 5 20)
        all-routes (tu/create-data-all-routes (list '(2019 5 13) '(2019 8 4)))
        test-data (tu/weeks (tu/to-local-date 2019 5 13)
                            (concat [{tu/route-name ["A" "A" "A" "A" "A" "A" "A"]} ;; 2019 05 13
                                     {tu/route-name ["A" "A" "A" "A" "A" "A" "A"]} ;; 2019 05 20
                                     {tu/route-name ["A" "A" "A" "A" "A" "A" "A"]} ;; 2019 05 27
                                     {tu/route-name ["A" "A" "A" "A" "A" "A" "A"]} ;; 2019 06 03
                                     {tu/route-name [nil nil nil nil nil nil nil]} ;; 2019 06 10
                                     {tu/route-name [nil nil nil nil nil nil nil]} ;; 2019 06 17
                                     {tu/route-name [nil nil nil nil nil nil nil]} ;; 2019 06 24
                                     {tu/route-name [nil nil nil nil nil nil nil]} ;; 2019 07 01
                                     {tu/route-name [nil nil nil "A" "A" "A" "A"]} ;; 2019 07 08
                                     {tu/route-name ["A" "A" "A" "A" "A" "A" "A"]} ;; 2019 07 15
                                     {tu/route-name ["A" "A" "A" "A" "A" "A" "A"]} ;; 2019 07 22
                                     {tu/route-name ["A" "A" "A" "A" "A" "A" "A"]} ;; 2019 07 29
                                     {tu/route-name [nil nil nil nil nil nil nil]} ;; 2019 08 05
                                     {tu/route-name [nil nil nil nil nil nil nil]} ;; 2019 08 12
                                     {tu/route-name [nil nil nil nil nil nil nil]} ;; 2019 08 19
                                     {tu/route-name [nil nil nil nil nil nil nil]}]))
        result (detection/traffic-week-maps->change-maps analysis-date all-routes test-data)]

    (is (= {:change-type :no-traffic
            :change-date (tu/to-local-date 2019 6 10)
            :route-key tu/route-name
            :no-traffic-start-date (tu/to-local-date 2019 6 10)
            :no-traffic-end-date (tu/to-local-date 2019 7 11)}
           (select-keys (first result) tu/select-keys-detect-changes-for-all-routes))
        "Ensure that the pause is detected properly")

    (is (= {:route-key tu/route-name
            :change-type :removed
            :change-date (tu/to-local-date 2019 8 5)}
           (select-keys (second result) tu/select-keys-detect-changes-for-all-routes))
        "Ensure that the route end is detected properly")

    (is (= 2 (count result)) "Ensure that a right amount of changes are found and there are no extra changes.")))


(deftest test-2-changes-with-nil-days-with-route-end
  (let [analysis-date (tu/to-local-date 2019 5 13)
        all-routes (tu/create-data-all-routes (list '(2019 5 6) '(2019 7 14)))
        test-data (tu/weeks (tu/to-local-date 2019 5 6)
                            (concat [{tu/route-name ["A" "A" "A" "A" "A" "A" "A"]} ;; 2019 05 6
                                     {tu/route-name ["A" "A" "A" "A" "A" "A" "A"]} ;; 2019 05 13
                                     {tu/route-name ["A" "A" "A" "A" "A" "A" "A"]} ;; 2019 05 20
                                     {tu/route-name ["A" "A" "A" "A" "A" "A" "A"]} ;; 2019 05 27
                                     {tu/route-name [nil nil nil nil nil "X" nil]} ;; 2019 06 03
                                     {tu/route-name [nil nil nil nil nil "X" nil]} ;; 2019 06 10
                                     {tu/route-name [nil nil nil nil nil "X" nil]} ;; 2019 06 17
                                     {tu/route-name ["B" "B" "B" "B" "B" "B" "B"]} ;; 2019 06 24
                                     {tu/route-name ["B" "B" "B" "B" "B" "B" "B"]} ;; 2019 07 01
                                     {tu/route-name ["B" "B" "B" "B" "B" "B" "B"]}])) ;; 2019 07 08, last day 14th
        result (detection/traffic-week-maps->change-maps analysis-date all-routes test-data)]

    (is (= {:change-type :changed
            :route-key tu/route-name
            :different-week {:beginning-of-week (tu/to-local-date 2019 6 3)
                             :end-of-week (tu/to-local-date 2019 6 9)}
            :starting-week {:beginning-of-week (tu/to-local-date 2019 5 13)
                            :end-of-week (tu/to-local-date 2019 5 19)}}

           (select-keys (first result) tu/select-keys-detect-changes-for-all-routes))
        "Ensure a traffic change is reported.")

    (is (= {:change-type :changed
            :route-key tu/route-name
            :different-week {:beginning-of-week (tu/to-local-date 2019 6 24)
                             :end-of-week (tu/to-local-date 2019 6 30)}
            :starting-week {:beginning-of-week (tu/to-local-date 2019 6 3)
                            :end-of-week (tu/to-local-date 2019 6 9)}}
           (select-keys (second result) tu/select-keys-detect-changes-for-all-routes))
        "Ensure a traffic change is reported.")

    (is (= {:route-key tu/route-name
            :change-type :removed
            :change-date (tu/to-local-date 2019 7 15)}
           (select-keys (nth result 2) tu/select-keys-detect-changes-for-all-routes))
        "Ensure route end is reported.")

    (is (= 3 (count result)) "Ensure that a right amount of changes are found and there are no extra changes.")))

(deftest test-change-after-no-traffic-and-route-end
  (let [analysis-date (tu/to-local-date 2019 5 13)
        all-routes (tu/create-data-all-routes (list '(2019 5 6) '(2019 7 14)))
        test-data (tu/weeks (tu/to-local-date 2019 5 6)
                            (concat [{tu/route-name ["A" "A" "A" "A" "A" "A" "A"]} ;; 2019 05 6
                                     {tu/route-name ["A" "A" "A" "A" "A" "A" "A"]} ;; 2019 05 13
                                     {tu/route-name ["A" "A" "A" "A" "A" "A" "A"]} ;; 2019 05 20
                                     {tu/route-name ["A" "A" "A" "A" "A" "A" "A"]} ;; 2019 05 27
                                     {tu/route-name [nil nil nil nil nil nil nil]} ;; 2019 06 03
                                     {tu/route-name [nil nil nil nil nil nil nil]} ;; 2019 06 10
                                     {tu/route-name [nil nil nil nil nil nil nil]} ;; 2019 06 17
                                     {tu/route-name ["B" "B" "B" "B" "B" "B" "B"]} ;; 2019 06 24
                                     {tu/route-name ["B" "B" "B" "B" "B" "B" "B"]} ;; 2019 07 01
                                     {tu/route-name ["B" "B" "B" "B" "B" "B" "B"]}])) ;; 2019 07 08, last day 14th
        result (detection/traffic-week-maps->change-maps analysis-date all-routes test-data)]

    (is (= {:change-type :no-traffic
            :change-date (tu/to-local-date 2019 6 3)
            :route-key tu/route-name
            :no-traffic-start-date (tu/to-local-date 2019 6 3)
            :no-traffic-end-date (tu/to-local-date 2019 6 24)}
           (select-keys (first result) tu/select-keys-detect-changes-for-all-routes))
        "Ensure a traffic change is reported.")

    (is (= {:change-type :changed
            :route-key tu/route-name
            :different-week {:beginning-of-week (tu/to-local-date 2019 6 24)
                             :end-of-week (tu/to-local-date 2019 6 30)}
            :starting-week {:beginning-of-week (tu/to-local-date 2019 5 13)
                            :end-of-week (tu/to-local-date 2019 5 19)}}
           (select-keys (second result) tu/select-keys-detect-changes-for-all-routes))
        "Ensure a traffic change is reported.")

    (is (= {:route-key tu/route-name
            :change-type :removed
            :change-date (tu/to-local-date 2019 7 15)}
           (select-keys (nth result 2) tu/select-keys-detect-changes-for-all-routes))
        "Ensure route end is reported.")

    (is (= 3 (count result)) "Ensure that a right amount of changes are found and there are no extra changes.")))
