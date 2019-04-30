(ns ote.transit-changes.detection-integration-test
  (:require [ote.transit-changes.detection :as detection]
            [ote.services.transport :as transport-service]
            [ote.db.transport-service :as t-service]
            [specql.core :as specql]
            [ote.integration.import.gtfs :as gtfs-import]
            [clojure.test :as t :refer [deftest testing is use-fixtures]]
            [clojure.spec.test.alpha :as spec-test]
            [ote.transit-changes :as transit-changes]
            [ote.transit-changes.detection-test-utilities :as tu]
            [ote.time :as time]
            [com.stuartsierra.component :as component]
            [ote.test]))

(defn slurp-bytes
  "Slurp the bytes from a slurpable thing"
  [x]
  (with-open [out (java.io.ByteArrayOutputStream.)]
    (clojure.java.io/copy (clojure.java.io/input-stream x) out)
    (.toByteArray out)))


(defn rewrite-calendar [calendar-data orig-date]
  ;; find out how far past orig-date is in history, and shift all calendar-data
  ;; dates forward by that amount.
  (let [day-diff (ote.time/day-difference (ote.time/native->date-time orig-date) (clj-time.core/now))
        
        ;; _ (println "diff" day-diff)
        
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
    (mapv #(update % :gtfs/date date-fwd-fn) calendar-data)))


;; [removed - comment with construction notes on this test, check git history if interested]

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


(use-fixtures :each
  (ote.test/system-fixture
   :transport (component/using
                (transport-service/->Transport
                  (:nap nil))
                [:http :db])))


(def test-operator-id 1)
(def test-service-id 2)


(defn java-localdate->inst [ld]
  (when (instance? java.time.LocalDate ld)
    (clojure.instant/read-instant-date (.toString ld))))

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
                                     :gtfs/first_package true ;; (nil? latest-package) ;; true -> will load past weeks
                                     ;; :gtfs/first_package (nil? latest-package) ;; true -> will load past weeks
                                     :gtfs/transport-operator-id operator-id
                                     :gtfs/transport-service-id ts-id
                                     :gtfs/created (java.sql.Timestamp. (System/currentTimeMillis))
                                     :gtfs/etag new-etag
                                     :gtfs/license license
                                     :gtfs/external-interface-description-id interface-id})]            
        ;; Parse gtfs package and save it to database.
        (gtfs-import/save-gtfs-to-db db gtfs-bytes (:gtfs/id package) interface-id ts-id intercept-fn)))))

;;  - todo:
;;  -  re-enable & check date rewriting
;;  - check detection-result format, can we eliminate one of :changes & :different-week keys
;;  - also check if we can omit maps without change info in why are there maps unde :route-changes
;;  - the current time travel doesn't solve holidays. so let the tests inject override-holidays behaviour somehow.

(deftest test-with-gtfs-package
  (let [db (:db ote.test/*ote*)
        ;; db (:db ote.main/ote)
        gtfs-zip-path "test/resources/2019-02-07_1149_1712_gtfs_anon.zip"
        gtfs-zip-bytes (slurp-bytes gtfs-zip-path)
        orig-date #inst "2019-02-02T00:00:00"
        my-intercept-fn (fn gtfs-data-intercept-fn [file-type file-data]
                          ;; (println "hello from intercept fn, type" file-type)
                          (if (= file-type :gtfs/calendar-dates-txt)
                            (rewrite-calendar file-data orig-date)
                            file-data)
                          file-data)
        store-result (store-gtfs-helper gtfs-zip-bytes db  test-operator-id test-service-id #inst "2012-12-12" "beerpl" 4242
                                        my-intercept-fn)
        route-query-params {:service-id test-service-id :start-date (joda-datetime->inst (time/days-from (time/now) -120)) :end-date (joda-datetime->inst (time/days-from (time/now) 1))}
        detection-result (detection/detect-route-changes-for-service-new db route-query-params)
        changes (->> detection-result
                    :route-changes
                    (filter :changes))
        changed-route-names (map :route-key changes)
        lohja-changes ( (filterv #(and (= "-Lohja - Nummela - Vihti-" (:route-key %)) (:changes %)) (:route-changes *nd)))]
    (println "found" changes "in the following routes:" changed-route-names)
    (def *nd detection-result)
    (println (:start-date route-query-params))
    (testing "got someting"
      (is (not= nil (first detection-result))))
    (println "lohja-change date is" (-> lohja-change :changes :different-week-date java-localdate->inst))
    (testing "got right date for lohja - nummela - vihti change"
      ;; wip: with rewrite-calendar call enabled, this should return a date somewhere 90+ days in the past
      ;; (difference between current time and 2019-02) but for some reason we still get 2019-02-25
      (is (= #inst "2019-02-04" (-> lohja-change :changes :different-week-date java-localdate->inst))))))
