(ns ote.util-tests
  "Basic util tests"
  (:require  [clojure.test :as t :refer [deftest testing is]]
             [ote.util.transport-service-util :as tsu]
             [ote.util.transport-operator-util :as tou]))

(deftest test-week-day-order
  (let [day-set #{:SUN :SAT :FRI :MON}
        result (tsu/reorder-week-days day-set)]
    (is (= result '(:MON :FRI :SAT :SUN)))))

(deftest test-every-postal-code-validation
  (let [codes ["12345" "AU1234" "NNNNN-NNNN" "ANA NAN"]
        error-codes ["NNNNN-NNNNN" "    " "    a" "1,2,3,4,5"]]
    (is (= (tou/validate-every-postal-codes (first codes)) (first codes)))
    (is (= (tou/validate-every-postal-codes (second codes)) (second codes)))
    (is (= (tou/validate-every-postal-codes (nth codes 2)) (nth codes 2)))
    (is (= (tou/validate-every-postal-codes (nth codes 3)) (nth codes 3)))

    (is (= (tou/validate-every-postal-codes (first error-codes)) false))
    (is (= (tou/validate-every-postal-codes (second error-codes)) false))
    (is (= (tou/validate-every-postal-codes (nth error-codes 2)) false))
    (is (= (tou/validate-every-postal-codes (nth error-codes 3)) false))))