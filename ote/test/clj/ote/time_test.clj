(ns ote.time-test
  "Basic time/date tests"
  (:require  [clojure.test :as t :refer [deftest testing is]]
             [ote.time :as time]))

(deftest time-parse-and-format-roundtrip
  (is (= "23:44" (time/format-time (time/parse-time "23:44"))))
  (is (= "08:00" (time/format-time (time/parse-time "8:0")))))

(deftest format-with-seconds
  (testing "non-zero seconds are shown"
    (is (= "05:06:07" (time/format-time (time/->Time 5 6 7)))))
  (testing "zero or nil seconds are omitted"
    (is (= "16:30" (time/format-time (time/->Time 16 30 0))))
    (is (= "16:30" (time/format-time (time/->Time 16 30 nil)))))
  (testing "zero seconds are shown with format-time-full"
    (is (= "04:20:00" (time/format-time-full (time/->Time 4 20 0))))))

(deftest date-parse-and-format-roundtrip
  (is (= "08.04.1981" (time/format-date (time/parse-date-eu "8.4.1981"))))
  (is (= "1997-08-04" (time/format-date-iso-8601 (time/parse-date-iso-8601 "1997-08-04")))))
