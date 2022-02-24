(ns ote.integration.import.gtfs-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [clj-http.client :as http-client]
            [ote.integration.import.gtfs :as gtfs]
            [ote.test :refer [system-fixture http-post]]
            [ote.utils.ziptools :as ziptools]
            [ote.components.db :as db]
            [taoensso.timbre :as log])
  (:import [java.nio.file Files]))

(def db {:datasource (db/hikari-datasource ote.test/test-db-config)})

(defn test-db
  [f]
  (f)
  (.close (:datasource db)))

(use-fixtures :once test-db)

(def empty-package (ziptools/create "empty.zip" []))

(deftest test-package-creation
  (let []
    (testing "empty-package is created"
      (println "empty? " (.getPath empty-package))
      (is (= true (str/ends-with? (.getPath empty-package) "empty.zip")))
      (is (= true (.exists empty-package))))))

(def common-interface-params {:operator-id      123
                              :operator-name    "oppy op"
                              :ts-id            2
                              :last-import-date "2022-02-22"
                              :license          "anything"
                              :id               789
                              :data-content     nil})

(deftest required-files-are-present

  (let [required-files {"agency.txt"   "gtfs/import/agency_empty.txt"
                        "stops.txt"    "gtfs/import/stops_empty.txt"
                        "calendar.txt" "gtfs/import/calendar_empty.txt"
                        "trips.txt"    "gtfs/import/trips_empty.txt"}
        gtfs-files {::missing-files (ziptools/create "missing-files.zip" (random-sample required-files))}
        #_#_required-files2 ["agency.txt" "stops.txt" "calendar_dates.txt" "trips.txt"]]
    (with-redefs [http-client/get (fn [url & [req & r]]
                                    (if (= url ::missing-files)
                                      {:body (Files/readAllBytes (.toPath (gtfs-files url)))}
                                      (log/warn "Unknown call" url req r)))]

      (testing "missing files are reported"
        (is (= true (gtfs/download-and-store-transit-package :gtfs
                                                             {}
                                                             db
                                                             (merge common-interface-params
                                                                    {:url ::missing-files})
                                                             false
                                                             true)))))))
