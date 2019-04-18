(ns ote.integration.import.exception-csv-test
  (:require [clojure.test :refer :all]
            [ote.services.admin :as admin]))


(deftest parsing-csv-to-exception-days
  (let [csv-data (slurp "../ote/resources/public/csv/poikkeavat_ajopaivat.csv")
        exceptions (admin/parse-exception-holidays-csv csv-data)]
    (testing "count of exceptions should be 73"
      (is (= 73 (count exceptions))))))