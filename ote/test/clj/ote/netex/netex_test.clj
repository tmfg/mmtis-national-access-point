(ns ote.netex.netex-test
  (:require [clojure.test :refer :all]
            [ote.netex.netex :as netex]))

(deftest validation-report-conversion-and-reports
  (testing "1_gtfs_csv_3 / Presence of header"
    (is (= "transfers.txt is missing the header row"
           (netex/netex-validation-error {:test_id "Presence of header",
                                          :error_id "1_gtfs_csv_3",
                                          :source {:file {:filename "transfers.txt", :line_number 1, :column_number 0}, :objectid "", :label ""},
                                          :error_value "transfers.txt"}))))
  (testing "1_gtfs_common_3 / Check for optional files"
    (is (= "Optional file shapes.txt is not present."
           (netex/netex-validation-error {:test_id "Check for optional files",
                                          :error_id "1_gtfs_common_3",
                                          :source {:file {:filename "shapes.txt"}, :objectid "", :label ""},
                                          :error_value "shapes.txt"}))))
  (testing "1_gtfs_common_4 / Presence of other files"
    (is (= "Unexpected file contracts.txt present in GTFS package"
           (netex/netex-validation-error {:test_id "Presence of other files",
                                          :error_id "1_gtfs_common_4",
                                          :source {:file {:filename "contracts.txt"}, :objectid "", :label ""},
                                          :error_value "contracts.txt"}))))
  (testing "1_gtfs_common_8 / Unique Identifiers"
    (is (= "agency.txt contains the value 6791 for key agency_id multiple times, starting at line 6, column 1."
           (netex/netex-validation-error {:test_id "Unique Identifiers",
                                          :error_id "1_gtfs_common_8",
                                          :source {:file {:filename "agency.txt", :line_number 6, :column_number 1}, :objectid "", :label ""},
                                          :error_value "6791",
                                          :reference_value "agency_id"}))))
  (testing "1_gtfs_common_11 / Unknown columns"
    (is (= "routes.txt contains unknown column route_sort_order"
           (netex/netex-validation-error {:test_id "Unknown columns",
                                          :error_id "1_gtfs_common_11",
                                          :source {:file {:filename "routes.txt", :line_number 1, :column_number 10}, :objectid "", :label ""},
                                          :error_value "route_sort_order"}))))
  (testing "1_gtfs_common_12 / Data in Required columns"
    (is (= "stop_times.txt is missing required value for column stop_id on line 9, column 4"
           (netex/netex-validation-error {:test_id "Data in Required columns",
                                          :error_id "1_gtfs_common_12",
                                          :source {:file {:filename "stop_times.txt", :line_number 9, :column_number 4}, :objectid "", :label ""},
                                          :error_value "stop_id"}))))
  (testing "2_gtfs_stop_4 / Station without stations"
    (is (= "stops.txt contains a station s3846 without stations at line 18, column 10"
           (netex/netex-validation-error {:test_id "Station without stations",
                                          :error_id "2_gtfs_stop_4",
                                          :source {:file {:filename "stops.txt", :line_number 18, :column_number 10}, :objectid "71745", :label ""},
                                          :error_value "s3846"}))))
  (testing "Unknown error type will return raw data as plain string"
    (let [error {:test_id "Teleporter endpoint temporality",
                 :error_id "9_matter_transportation",
                 :source {:file {:filename "quantum_gates.txt", :line_number 27, :column_number 9}, :objectid "", :label ""},
                 :error_value "P2X-555"}]
      (is (= (str error) (netex/netex-validation-error error))))))

