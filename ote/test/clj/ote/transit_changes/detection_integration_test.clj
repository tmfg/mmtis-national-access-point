(ns ote.transit-changes.detection-integration-test
  (:require [ote.transit-changes.detection :as detection]
            [ote.services.transport :as transport-service]
            [ote.db.transport-service :as t-service]
            [specql.core :as specql]
            [ote.integration.import.gtfs :as gtfs-import]
            [clojure.test :as t :refer [deftest testing is use-fixtures]]
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

(defn round-up-to-nearest-multiple-of-7 [n]
  (if (pos? (rem n 7))
    (+ n (- 7 (rem n 7)))
    n))

(defn rewrite-calendar 
  "Find out how far past orig-date is in history, and shift all calendar-data
  dates forward by that amount."
  [calendar-data orig-date filter-fn]
  (let [day-diff (ote.time/day-difference (ote.time/native->date-time orig-date) (clj-time.core/now))
        day-diff-weekfixed (round-up-to-nearest-multiple-of-7 day-diff)
        ;; _ (println "rewrite-calendar: day difference (rounded to week):" day-diff-weekfixed)
        
        calendar-data (if filter-fn
                        (filterv filter-fn
                                calendar-data)
                        calendar-data)
        rewritten-calendar-data (mapv (fn [record] (update record :gtfs/date #(.plusDays % day-diff-weekfixed))) calendar-data)
        ]
       
    ;; (def *cd rewritten-calendar-data)
    rewritten-calendar-data))


;; [there was comment with construction notes on this test, check git history if interested]

(deftest rewrite-calendar-testutil-works
  (let [same-localdate-weekday? (fn [ld1 ld2]
                                 (= (.getValue (.getDayOfWeek ld1))
                                    (.getValue (.getDayOfWeek ld2))))
        localdate-days-between (fn [ld1 ld2]
                                 (print ld1 ld2)
                                 (.getDays  (java.time.Period/between ld1 ld2)))
        now-localdate (java.time.LocalDate/now)
        input-localdate (java.time.LocalDate/parse "2018-02-04")
        reinterpret-inst-date (clojure.instant/read-instant-date "2018-02-07T12:00:00")
        rewritten-date (->
                        (rewrite-calendar [#:gtfs{:service-id "1",
                                                  :date input-localdate,
                                                  :exception-type 1}] reinterpret-inst-date nil)
                        first
                        :gtfs/date)]
    (println "rewritten date" input-localdate "->" rewritten-date)
    (is (some? rewritten-date))

    (is (same-localdate-weekday? input-localdate rewritten-date))))


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

(deftest test-with-gtfs-package
  (let [db (:db ote.test/*ote*)
        ;; db (:db ote.main/ote)
        gtfs-zip-path "test/resources/2019-02-07_1149_1712_gtfs_anon.zip"
        gtfs-zip-bytes (slurp-bytes gtfs-zip-path)
        orig-date #inst "2019-02-15"
        my-intercept-fn (fn gtfs-data-intercept-fn [file-type file-data]
                          ;; (println "hello from intercept fn, type" file-type)
                          (if (= file-type :gtfs/calendar-dates-txt)
                            (rewrite-calendar file-data orig-date (fn calendar-filter-fn [row]
                                                                    (contains? #{"11" "22"} (:gtfs/service-id row))))
                            file-data))
        store-result (store-gtfs-helper gtfs-zip-bytes db  test-operator-id test-service-id #inst "2012-12-12" "beerpl" 4242
                                        my-intercept-fn
                                        )
        route-query-params {:service-id test-service-id
                            :start-date (joda-datetime->inst (time/days-from (time/now) -120))
                            :end-date (joda-datetime->inst (time/days-from (time/now) 30))
                            :ignore-holidays? true}
        detection-result (detection/detect-route-changes-for-service db route-query-params)
        changes (->> detection-result
                    :route-changes
                    (filter :changes))
        changed-route-names (map :route-key changes)
        lohja-changes (filterv #(= "-Lohja - Nummela - Vihti-" (:route-key %))
                               (:route-changes detection-result))]
    (testing "got sane change figures for lohja - nummela - vihti changes"
      (is (= 1 (count lohja-changes)))     
      (is (= 52 (-> lohja-changes first :changes :trip-changes first :stop-time-changes))))))
