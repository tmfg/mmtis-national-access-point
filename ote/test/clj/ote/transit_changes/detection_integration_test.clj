(ns ote.transit-changes.detection-integration-test
  (:require [ote.transit-changes.detection :as detection]
            [ote.db.transport-service :as t-service]
            [specql.core :as specql]
            [ote.integration.import.gtfs :as gtfs-import]
            [clojure.test :as t :refer [deftest testing is]]
            [clojure.spec.test.alpha :as spec-test]
            [ote.transit-changes :as transit-changes]
            [ote.transit-changes.detection-test-utilities :as tu]
            [ote.time :as time]))

;; this doesn't compile or work in normal "lein test" run due to the ote.main/ote reference, uncomment for repl use

#_(deftest test-with-gtfs-package-of-a-service-repl
  (let [db (:db ote.main/ote)
        route-query-params {:service-id 5 :start-date (time/parse-date-eu "18.02.2019") :end-date (time/parse-date-eu "06.07.2019")}
        new-diff (detection/detect-route-changes-for-service-new db route-query-params)
        old-diff (detection/detect-route-changes-for-service-old db route-query-params)]
    ;; new-diff structure:
    ;; :route-changes [ ( [ routeid {dada} ]) etc
    (println (:start-date route-query-params))
    (testing "differences between new and old"
      (is (= old-diff new-diff)))))

(defn slurp-bytes
  "Slurp the bytes from a slurpable thing"
  [x]
  (with-open [out (java.io.ByteArrayOutputStream.)]
    (clojure.java.io/copy (clojure.java.io/input-stream x) out)
    (.toByteArray out)))


#_(defn java-localdate->inst [ld]
  (ote.time/date-fields->native
   (merge {:ote.time/hours 0 :ote.time/minutes 0 :ote.time/seconds 0}
          (time/date-fields ld))))

(defn rewrite-calendar [calendar-data orig-date]
  ;; find out how far past orig-date is in history, and shift all calendar-data
  ;; dates forward by that amount.
  ;; xxx should make sure the weeks align? is is enough to just divmod 7 the interval-in-days?
  (let [day-diff (ote.time/day-difference (ote.time/native->date-time orig-date) (clj-time.core/now))
        
        _ (println "diff" day-diff)
        
        date-fwd-fn (fn [java-local-date]                     
                      (let [joda-orig-datetime (ote.time/java-localdate->joda-date-time
                                                java-local-date)
                            orig-dow (clj-time.core/day-of-week joda-orig-datetime)
                            joda-future-datetime (ote.time/days-from joda-orig-datetime day-diff)
                            future-dow (clj-time.core/day-of-week joda-future-datetime)
                            dow-diff (- orig-dow future-dow)
                            ;; _ (println "dow diff" orig-dow future-dow "->" dow-diff)
                            dow-corrected (ote.time/days-from joda-future-datetime dow-diff)
                            ;;_ (println "fd/cfd" joda-future-datetime dow-corrected)
                            str-future-datetime (ote.time/format-date-iso-8601 dow-corrected)
                            java-future-datetime (java.time.LocalDate/parse str-future-datetime)]
                        
                        #_(println "weekday adjusted:" (ote.time/day-of-week joda-future-datetime))
                        java-future-datetime))]
       
    (def *cd calendar-data)
    (println "cd type" (type calendar-data))
    (mapv #(update % :gtfs/date date-fwd-fn) calendar-data)))


#_(deftest rewrite-calendar-testutil-works
  (is (= [#:gtfs{:service-id "1",
                 :date (java.time.LocalDate/now),
                 :exception-type 1}]
         (rewrite-calendar [#:gtfs{:service-id "1",
                                   :date (java.time.LocalDate/parse "2013-01-01"),
                                   :exception-type 1}] #inst "2013-01-01") ))

  (is (= [#:gtfs{:service-id "1",
                 :date (.plusDays (java.time.LocalDate/now) 5),
                 :exception-type 1}]
         (rewrite-calendar [#:gtfs{:service-id "1",
                                   :date (java.time.LocalDate/parse "2013-01-06"),
                                   :exception-type 1}] #inst "2013-01-01") )))

;; tbd - check some actual changes read from this package (detected-route-change table)
;; and cross ref with the bug

;; for case Lohja - Nummela - Vihti we should find
;; detected-route-change rows containing changes for these
;; prob: vihti routes missing from detection results (even under :all-routes key)
;; repl wip:
;; (detection/service-routes-with-date-range (:db ote.main/ote) {:service-id 2})
;; or ..
;; (->> (detection/service-routes-with-date-range (:db ote.main/ote) {:service-id 2})
;;      (filter #(-> %
;;                   :route-long-name
;;                   (= "Lohja - Nummela - Vihti")))
;;      first)
;; -> sql query -> sproc -> gtfs_service_routes_with_daterange
;; checking routes: select "id", "route-id", "route-long-name" from "gtfs-route" where "route-long-name" like '%ummela%';
;; ->  142 | r_26     | Lohja - Nummela - Vihti
;; ok. after redoing import with updated code from master, the original symptom changed:
;;  - now we get :all routes containing the route:
;; (filter #(= (:route-long-name %) "Lohja - Nummela - Vihti")  (vals  (:all-routes *nd)))

;; test setup:
;; 1. a transit info zip must be about some operator ("transit-operator" table),
;; and have a record about it in "gtfs_package" table, and be associated with
;; some operator ("transport-operator" table).
;; normally in the test data, we have "Ajopalvelu Testinen Oy" with id 1,
;; and their bus service with service-id 2, so we can use those.

(def test-operator-id 1)
(def test-service-id 2)

(defn joda-datetime->inst [ld]
  (ote.time/date-fields->native
   (merge {:ote.time/hours 0 :ote.time/minutes 0 :ote.time/seconds 0}
          (time/date-fields ld))))

(defn store-gtfs-helper
  [gtfs-bytes db operator-id ts-id last-import-date license interface-id intercept-fn]
  (let [filename (gtfs-import/gtfs-file-name operator-id ts-id)
        latest-package (gtfs-import/interface-latest-package db interface-id)
        new-etag nil]

    (let [new-gtfs-hash (gtfs-import/gtfs-hash gtfs-bytes)
          old-gtfs-hash (:gtfs/sha256 latest-package)]

      ;; No gtfs import errors caught. Remove old import errors.
      (specql/update! db ::t-service/external-interface-description
                      {::t-service/gtfs-import-error nil}
                      {::t-service/id interface-id})

      (let [package (specql/insert! db :gtfs/package
                                    {:gtfs/sha256 new-gtfs-hash
                                     :gtfs/first_package (nil? latest-package)
                                     :gtfs/transport-operator-id operator-id
                                     :gtfs/transport-service-id ts-id
                                     :gtfs/created (java.sql.Timestamp. (System/currentTimeMillis))
                                     :gtfs/etag new-etag
                                     :gtfs/license license
                                     :gtfs/external-interface-description-id interface-id})]            
        ;; Parse gtfs package and save it to database.
        (gtfs-import/save-gtfs-to-db db gtfs-bytes (:gtfs/id package) interface-id ts-id intercept-fn)))))


(deftest test-with-kalkati-package
  (let [;; db (:db ote.test/*ote*)
        db (:db ote.main/ote)
        kalkati-zip-path "../issue163446864_kalkati_anonco.zip"
        gtfs-zip-bytes (ote.gtfs.kalkati-to-gtfs/convert-bytes (slurp-bytes kalkati-zip-path))
        orig-date #inst "2019-03-24T00:00:00"
        my-intercept-fn (fn gtfs-data-intercept-fn [file-type file-data]
                          ;; (println "hello from intercept fn, type" file-type)
                          (if (= file-type :gtfs/calendar-dates-txt)
                            (rewrite-calendar file-data orig-date)
                            file-data)
                          file-data)
        store-result (store-gtfs-helper gtfs-zip-bytes  (:db ote.main/ote)  test-operator-id test-service-id #inst "2012-12-12" "beerpl" 4242 my-intercept-fn)
        route-query-params {:service-id test-service-id :start-date (joda-datetime->inst (time/days-from (time/now) -120)) :end-date (joda-datetime->inst (time/days-from (time/now) 1))}
        new-diff (detection/detect-route-changes-for-service-new db route-query-params)]
    (def *nd new-diff)
    (println (:start-date route-query-params))
    (testing "got someting"
      (is (not= nil (first new-diff))))))


;; tbd - check some actual changes read from this package (detected-route-change table)
#_(deftest test-with-gtfs-package-of-a-service
  (let [db (:db ote.test/*ote*)
        service-id test-service-id
        gtfs-zipfile-orig-path "/home/ernoku/Downloads/2019-02-24_310_1290_gtfs.zip"
        gtfs-zipfile-orig-date #inst "2019-02-24T00:00:00"
        my-intercept-fn (fn gtfs-data-intercept-fn [file-type file-data]
                          ;; (println "hello from intercept fn, type" file-type)
                          (if (= file-type :gtfs/calendar-dates-txt)
                            (rewrite-calendar file-data gtfs-zipfile-orig-date)
                            file-data)
                          file-data)
        package-id 1
        interface-id 1
        _ (ote.integration.import.gtfs/save-gtfs-to-db db (slurp-bytes gtfs-zipfile-orig-path) package-id interface-id service-id my-intercept-fn)
        route-query-params {:service-id service-id :start-date (time/days-from (time/now) -265) :end-date (time/days-from (time/now) 1)}
        new-diff (detection/detect-route-changes-for-service-new db route-query-params)]
    (def *nd new-diff)
    (println (:start-date route-query-params))
    (testing "got something"
      (is (not= nil (first new-diff))))))
