(ns ote.services.external-test
  (:require [clojure.test :as t :refer [deftest testing is]]
            [ote.test :refer [system-fixture http-get http-post]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [ote.services.external :as external-service]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]))

(t/use-fixtures :each
  (system-fixture
    :external (component/using
                 (external-service/->External
                   (:nap nil))
                 [:http :db])))

(deftest ensure-url-test
  (let [test-url "www.solita.fi"
        changed-url (external-service/ensure-url test-url)]
    (is (= changed-url "http://www.solita.fi"))))

(deftest parse-csv->map-test
  (let [csv-file (io/reader "test/resources/testcsv.csv")
        data (csv/read-csv csv-file)
        csv-map (external-service/parse-response->csv data)
        company-count (count (:result csv-map))]
    (is (= company-count 4))))