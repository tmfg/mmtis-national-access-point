(ns ote.util-tests
  "Basic util tests"
  (:require  [clojure.test :as t :refer [deftest testing is]]
             [ote.util.transport-service-util :as tsu]))

(deftest test-week-day-order
  (let [day-set #{:SUN :SAT :FRI :MON}
        result (tsu/reorder-week-days day-set)]
    (is (= result '(:MON :FRI :SAT :SUN)))))

