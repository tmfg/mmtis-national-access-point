(ns ote.gtfs.parse-gtfs-test
  (:require [clojure.test :refer :all]
            [ote.services.admin :as admin]
            [ote.integration.import.gtfs :as import-gtfs]
            [ote.gtfs.parse :as gtfs-parse]
            [clojure.java.io :as io]))


(deftest test-parse-trips-txt
  (let [trips-data (slurp (str "test/resources/gtfs/trips.txt"))
        file-type :gtfs/trips-txt
        trips-data (gtfs-parse/parse-gtfs-file file-type trips-data)

        result (import-gtfs/process-rows file-type trips-data)
        grouped-routes (group-by :gtfs/route-id result)]
    ;; trip count
    (is (= 61 (count result)))
    ;; Route count
    (is (= 37 (count grouped-routes)))))