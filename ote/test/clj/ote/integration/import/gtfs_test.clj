(ns ote.integration.import.gtfs_test
  (:require [ote.integration.import.gtfs :as import-gtfs]
            [ote.tasks.gtfs :refer [upsert-service-transit-change]]
            [clojure.test :as t :refer [use-fixtures deftest is testing]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]
            [ote.test :refer [system-fixture *ote* http-post http-get sql-query sql-execute!]]
            [com.stuartsierra.component :as component]
            [clojure.test.check.generators :as gen]
            [ote.db.gtfs :as gtfs]
            [ote.db.places :as places]
            [ote.db.generators :as generators]
            [ote.integration.export.transform :as transform]
            [ote.time :as time]
            [webjure.json-schema.validator.macro :refer [make-validator]]
            [clojure.java.io :as io]
            [ote.util.zip :as zip-file]
            [ring.util.io :as ring-io]
            [specql.impl.registry :as specql-registry]
            [specql.impl.composite :as composite]
            [clojure.string :as string]))


(t/use-fixtures :each
                (system-fixture
                  :import-gtfs (component/using (import-gtfs/->GTFSImport
                                                  (:gtfs (read-string (slurp "config.edn")))) [:db :http])))

(defn convert-bytes
  [input-stream]
  (let [out (java.io.ByteArrayOutputStream.)]
    (io/copy input-stream out)
    (.toByteArray out)))

(defn date->gtfs-date-str [dt]
  (let [{::time/keys [year month date]} (time/date-fields dt)]
    (format "%04d%02d%02d" year month date)))


(def gtfs-files ["agency.txt" "routes.txt" "stops.txt" "trips.txt" "stop_times.txt" "calendar.txt" "calendar_dates.txt"])

(defn fetch-gtfs-package [id]
  (first (sql-query
           "SELECT * FROM gtfs_package WHERE id=" id "LIMIT 1")))


;; NOTE: We assume that ote-testdata contains a bus service (2) and a gtfs interface (999).
(defn upsert-gtfs-package! [id date]
  (set
    (sql-execute!
      "INSERT INTO gtfs_package (id, \"transport-operator-id\", \"transport-service-id\", \"external-interface-description-id\", created)
       VALUES (" id ", 1, 2, 999, '" date "'::DATE)")))

(defn truncate-gtfs-package-table! []
  (sql-execute!
    "TRUNCATE TABLE gtfs_package RESTART IDENTITY CASCADE"))

(defn fetch-latest-transit-change []
  (first (sql-query
           "SELECT * FROM \"gtfs-transit-changes\"
             WHERE \"transport-service-id\" = 2 AND date >= CURRENT_DATE
             ORDER BY \"transport-service-id\", date desc
             LIMIT 1")))

(defn calendar-txt [start-date end-date]
  (str
    "service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date\n"
    "2399,1,1,1,1,1,0,0," start-date "," end-date "\n"))

(defn calendar-dates-txt [exceptions]
  (str "service_id,date,exception_type\n"
       (string/join "\n" exceptions)))

(defn gtfs-zip [test-package-name & [custom-data]]
  (let [path (str "test/resources/gtfs/" test-package-name "/")
        files (mapv (fn [fname]
                      {:name fname :data (or (when custom-data
                                               (custom-data fname))
                                             (slurp (str path fname)))})
                    gtfs-files)]
    (ring-io/piped-input-stream #(zip-file/write-zip files %))))

(defn import-gtfs [test-package-name id date & [custom-data]]
  (let [gtfs-zip (gtfs-zip test-package-name custom-data)]
    (upsert-gtfs-package! id date)
    (import-gtfs/save-gtfs-to-db (:db *ote*) (convert-bytes gtfs-zip) id 999)))




(deftest save-gtfs-to-db
  (truncate-gtfs-package-table!)
  (import-gtfs "test1" 1000 (time/format-date-iso-8601 (time/now)))

  (let [gtfs-package (fetch-gtfs-package 1000)]
    (is (= 1000 (:id gtfs-package)))
    (is (not (nil? (:envelope gtfs-package))))
    (is (= 1 (:transport-operator-id gtfs-package)))
    (is (= 2 (:transport-service-id gtfs-package)))
    (is (= 999 (:external-interface-description-id gtfs-package)))
    (is (= (time/date-fields-only (time/now))
           (time/date-fields-only (time/native->date-time (:created gtfs-package)))))))


(deftest transit-changes-no-changes
  (truncate-gtfs-package-table!)
  (import-gtfs "test1" 1000 (time/format-date-iso-8601 (time/days-from (time/now) -7)))
  ;; Add a new package for the same service. Use the same test package -> no changes should be occurring.
  (import-gtfs "test1" 1001 (time/format-date-iso-8601 (time/now)))

  ;; Compute transit changes
  (upsert-service-transit-change (:db *ote*) {:service-id 2})

  (let [latest-change (fetch-latest-transit-change)
        route-changes (composite/parse @specql-registry/table-info-registry
                                       {:category "A"
                                        :element-type :gtfs/route-change-info}
                                       (str (:route-changes latest-change)))]

    (is (zero? (:removed-routes latest-change)))
    (is (zero? (:added-routes latest-change)))
    (is (zero? (:changed-routes latest-change)))
    (is (nil? (:different-week-date latest-change)))
    (is (= (set route-changes)
           (set [#:gtfs{:route-long-name "Stop29 MH - Stop32", :trip-headsign "Stop29 MH", :change-type :no-change}
                 #:gtfs{:route-long-name "Stop32 - Stop29 MH", :trip-headsign "Stop32", :change-type :no-change}
                 #:gtfs{:route-long-name "Stop32 - Stop32", :trip-headsign "Stop29 MH", :change-type :no-change}])))))


(deftest transit-changes-has-changes
  (truncate-gtfs-package-table!)
  (import-gtfs "test1" 1000 (time/format-date-iso-8601 (time/days-from (time/now) -14)))
  ;; Add a new package for the same service. Use the same test package -> no changes should be occurring.
  (import-gtfs "test1_changes" 1001 (time/format-date-iso-8601 (time/now))
               {"calendar_dates.txt"
                (calendar-dates-txt [(str "2399," (date->gtfs-date-str (time/days-from (time/now) 13)) ",2")])})

  ;; Compute transit changes
  (upsert-service-transit-change (:db *ote*) {:service-id 2})

  (let [latest-change (fetch-latest-transit-change)
        route-changes (composite/parse @specql-registry/table-info-registry
                                       {:category "A"
                                        :element-type :gtfs/route-change-info}
                                       (str (:route-changes latest-change)))]

    (is (= 1 latest-change))
    (is (= 1 route-changes))

    (is (= 1 (:removed-routes latest-change)))
    (is (= 1 (:added-routes latest-change)))
    (is (zero? (:changed-routes latest-change)))
    (is (not (nil? (:different-week-date latest-change))))
    #_(is (= (set route-changes)
             (set [#:gtfs{:route-long-name "Stop29 MH - Stop32", :trip-headsign "Stop29 MH", :change-type :no-change}
                   #:gtfs{:route-long-name "Stop32 - Stop29 MH", :trip-headsign "Stop32", :change-type :no-change}
                   #:gtfs{:route-long-name "Stop32 - Stop32", :trip-headsign "Stop29 MH", :change-type :no-change}])))))
