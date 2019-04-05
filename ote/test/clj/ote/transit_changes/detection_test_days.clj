(ns ote.transit-changes.detection-test-days
  (:require [ote.transit-changes.detection :as detection]
            [clojure.test :as t :refer [deftest testing is]]
            [clojure.spec.test.alpha :as spec-test]
            [ote.transit-changes :as transit-changes]
            [ote.transit-changes.detection-test-utilities :as tu]
            [ote.time :as time]))

;;;;;; TESTS for analysing changes in traffic between specific days

;; Day hash data for changes for a default week with ONE kind of day hashes
(def data-wk-hash-one-kind            ["A" "A" "A" "A" "A" "A" "A"])
(def data-wk-hash-one-kind-change-one  ["A" "A" "3" "3" "3" "3" "3"])
(def data-wk-hash-one-kind-change-two ["A" "A" "3" "3" "3" "3" "7"])
;; Day hash data for changes for a default week with TWO kind of day hashes
(def data-wk-hash-two-kind            ["A" "A" "A" "A" "A" "B" "B" ])
(def data-wk-hash-two-kind-one-nil    ["A" "A" "A" nil "A" "B" "B" ])
(def data-wk-hash-two-kind-change-one ["A" "A" "A" "A" "A" "5" "5" ])
(def data-wk-hash-two-kind-change-two ["1" "1" "1" "1" "1" "5" "5" ])
(def data-wk-hash-two-kind-holiday    [:holiday1 "A" "A" "A" "A" "B" "B" ])
(def data-wk-hash-traffic-weekdays-nil-weekend-traffic [nil nil nil nil nil "C5" "C6"])
(def data-wk-hash-traffic-nil         [nil nil nil nil nil nil nil])

(def data-wk-hash-two-kind-on-weekend            ["A" "A" "A" "A" "A" "D" "E" ])
(def data-wk-hash-traffic-weekdays-nil-weekend-nil [nil nil nil nil nil "D2" "E2"])
;; Day hash data for changes for a default week with FIVE kind of day hashes
(def data-wk-hash-five-kind           ["A" "B" "B" "B" "F" "G" "H"])
(def data-wk-hash-five-kind-change-four  ["A" "2" "5" "5" "5" "6" "7"])
(def data-wk-hash-seven-kind             ["A" "C" "D" "E" "F" "G" "H"])
(def data-wk-hash-five-kind-change-seven ["1" "2" "3" "4" "5" "6" "7"])

(def data-wk-hash-two-kind-nil                     ["A" "A" "A" "A" nil "B" "B" ])
(def data-wk-hash-two-kind-nil-and-holiday         ["A" "A" "A" "A" :some-holiday nil "B" ])
(def data-wk-hash-two-kind-nil-and-holiday-changed ["A" "A" "A" "A" "4" "5" "6" ])
(def data-wk-hash-two-kind-nil-changed-and-holiday ["B" "B" :some-holiday "B" "B" "B" "B" ])


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

  (testing "Two kinds of traffic, changes to all nil"
    (is (= [0 5] (transit-changes/changed-days-of-week data-wk-hash-two-kind data-wk-hash-traffic-nil))))

  (testing "Two kinds of traffic, changes to one nil"
    (is (= [3] (transit-changes/changed-days-of-week data-wk-hash-two-kind data-wk-hash-two-kind-one-nil))))

  (testing "Five kinds of traffic, changes: 0"
    (is (= [] (transit-changes/changed-days-of-week data-wk-hash-five-kind data-wk-hash-five-kind))))

  (testing "Five kinds of traffic, changes: 5"
    (is (= [1 2 4 5 6] (transit-changes/changed-days-of-week data-wk-hash-five-kind data-wk-hash-five-kind-change-four))))

  (testing "Seven kinds of traffic, changes: 7"
    (is (= [0 1 2 3 4 5 6] (transit-changes/changed-days-of-week data-wk-hash-seven-kind data-wk-hash-five-kind-change-seven))))

  (testing "Two kinds of traffic, changes to nil on weekdays, different on weekend2"
    (is (= [0 5 6] (transit-changes/changed-days-of-week data-wk-hash-two-kind-on-weekend data-wk-hash-traffic-weekdays-nil-weekend-nil)))))

(deftest test-changed-days-holidays
  (testing "Two kinds of traffic with nil, changes to one kind with holiday"
    (is (= [0 4] (transit-changes/changed-days-of-week data-wk-hash-two-kind-nil data-wk-hash-two-kind-nil-changed-and-holiday))))

  (testing "Two kinds of traffic with holiday on baseline, ensure no change is detected"
    (is (= [] (transit-changes/changed-days-of-week data-wk-hash-two-kind-holiday data-wk-hash-two-kind-holiday))))

  (testing "Two kinds of traffic with holiday on new week, ensure no change is detected"
    (is (= [] (transit-changes/changed-days-of-week data-wk-hash-two-kind-holiday data-wk-hash-two-kind-holiday))))

  (testing "Two kinds of traffic with holiday on baseline, ensure change is detected"
    (is (= [1 5] (transit-changes/changed-days-of-week data-wk-hash-two-kind-holiday data-wk-hash-two-kind-change-two))))

  (testing "Two kinds of traffic with holiday and nil on baseline, ensure change is detected"
    (is (= [5 6] (transit-changes/changed-days-of-week data-wk-hash-two-kind-nil-and-holiday data-wk-hash-two-kind-nil-and-holiday-changed)))))
