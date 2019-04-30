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
;; repl:
;; (detection/service-routes-with-date-range (:db ote.main/ote) {:service-id 2})
;; or ..
#_(->> (detection/service-routes-with-date-range (:db ote.main/ote) {:service-id 2})
     (filter #(-> %
                  :route-long-name
                  (= "Lohja - Nummela - Vihti")))
     first)
;; this returns one record
;; -> sql query -> sproc -> gtfs_service_routes_with_daterange
;; checking routes: select "id", "route-id", "route-long-name" from "gtfs-route" where "route-long-name" like '%ummela%';
;; ->  142 | r_26     | Lohja - Nummela - Vihti
;; ok. after redoing import with updated code from master, the original symptom changed:
;;  - now we get :all routes containing the route:
;; (filter #(= (:route-long-name %) "Lohja - Nummela - Vihti")  (vals  (:all-routes *nd)))
;; but it's not under :route-changes
;; -> try to work backwards, what would need to happen for route-changes to get filledin
;;   - detection/detect-route-changes-for-service-new fn calls series of fns on routes-by-date
;;   - saving routes-by-date in the above fn and looking for our route of interest:
;;    (filter #(= "-Lohja - Nummela - Vihti-" (first %) ) (mapcat :routes *rd))
;;    yields records with only a single constant hash (and some nil hashes)
;;  - track down to this call:
;;   - (service-route-hashes-for-date-range (:db ote.main/ote) {:route-hash-id "-Lohja - Nummela - Vihti-" :service-id 2, :start-date #inst "2018-12-30T08:45:43.000-00:00", :end-date #inst "2019-04-30T07:45:43.000-00:00"})
;;      -> get only single hash + nils , evenif start-date is changed way back to 2010
;;        -> is it possible that no-traffic weeks are nils so this is actually the right data?
;;        -> need to catch the phase where the week hash is formed and see what the data is in human readable form
;;           also, find where in the db this is stored? -> it's our old pal gtfs-date-hash table joined variously
;;         -> transport-service-id is null on thr gtfs-date-hash rows of our packages. maybe it's unused? because we still get some data
;;         -> checking import path. in integration.import.gtfs there's import-stop-times fn that reads packages. it's sql for updating "gtfs-trip".
;;         ->  select id, "route-id", "package-id" from "gtfs-trip" where "package-id" = 22 and "route-id" in (select "route-id" from "gtfs-route" where "route-long-name" = 'Lohja - Nummela - Vihti')
;;           -> ok, looks to be the same sparse info
;;           -> look at the kalkati data manually
;;           -> ok, looks like this xml may indeed be missing what we need..
;;           -> go digging into the napote s3 bucket.
;;           -> grab https://s3.eu-central-1.amazonaws.com/napote-gtfs/2019-02-07_1149_1712_gtfs.zip
;;               - this is not kalkati format any more, let's nevertheless try to load it
;;           -> actually, that is too new also, previous found is 2019-01-10_1149_1712_gtfs.zip,  use that
;;           -> ok, now we actually get 2 different hashes. but still empty :route-changes in detect-route-changes return map
;;           -> seems that route-weeks-with-first-difference-new fn only gets weeks with c8 hash
;;           -> looking at callers to look for where they disappear
;;             observation: only 2 weeks caught in debug *var in detect-changes-for-all-routes
;;             using (map :beginning-of-week (mapcat identity *dcrl)),
;;             also verified that it's only called once so no data overwritten...
;;           -> found out that first-package must be set to false, otherwise dates in past will be dropped.
;;           -> for a while got 2 different hashes coming out of
;;             (filter #(= "-Lohja - Nummela - Vihti-" (first %) ) (mapcat :routes *rd)) - possibly before the first-package change the imports had left in old data in the db? (when running against local napotedb database, not the test fixture one)
;;              but it somehow stopped after few changes trying to figure out why change detection for that route wasn't reached
;;           -> learn to look for the week hashes in the db. trying ... select hash, unnest("route-hashes") from "gtfs-date-hash" where "package-id" = 44; - no
;;           -> right diretion? select gtfs_service_route_date_hash(2, '2019-04-29', '', 'Lohja - Nummela - Vihti', ''); -> same hash as seen in debug
;;           -> select gtfs_service_route_date_hash(2, ser.d ::date, '', 'Lohja - Nummela - Vihti', '') from (select generate_series('2019-02-01'::date,'2019-07-02'::date,'1 day') d) as ser ;
;;           -> starting to suspect that the rewrite-calendar functionality might be somehow broken and mess things up? for example, we only adjust the calendar-dates data. are there other dates in the data that are not rewritten?
;;           -> trying some test runs without the rewrite-calendar call.
;;           -> meanwhile went back to the gtfs zip and eyeballed the data, and the prod front view. there are 2 hash colors and a blank week.
;;           -> next: try the sproc call for the date now that rewrite-calendar is disabled. nope, still the same ...b94a hash.
;;           -> two things to check: 1) see how hash is actually calculated, what are the inputs of the b94a hash.
;;              2) go back to the gtfs zip data and look for the scheduling change, not the blank trafficless week
;;           -> ok 2) seems to have been the winning ticket. actually had to use the next later package which had the 2 different schedules for the route. now the test shows changes for me.
;;  
;; test setup notes:
;; a transit info zip must be about some operator ("transit-operator" table),
;; and have a record about it in "gtfs_package" table, and be associated with
;; some operator ("transport-operator" table).
;; normally in the test data, we have "Ajopalvelu Testinen Oy" with id 1,
;; and their bus service with service-id 2, so we can use those.

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

;;  - todo: check detection-result format, can we eliminate one of :changes & :different-week keys
;;  - also check if we can omit maps without change info in why are there maps unde :route-changes

(deftest test-with-gtfs-package
  (let [db (:db ote.test/*ote*)
        ;; db (:db ote.main/ote)
        gtfs-zip-path "test/resources/2019-02-07_1149_1712_gtfs_anon.zip"
        gtfs-zip-bytes (slurp-bytes gtfs-zip-path)
        orig-date #inst "2019-02-02T00:00:00"
        ;; my-intercept-fn (fn gtfs-data-intercept-fn [file-type file-data]
        ;;                   ;; (println "hello from intercept fn, type" file-type)
        ;;                   (if (= file-type :gtfs/calendar-dates-txt)
        ;;                     (rewrite-calendar file->data orig-date)
        ;;                     file-data)
        ;;                   file-data)
        store-result (store-gtfs-helper gtfs-zip-bytes db  test-operator-id test-service-id #inst "2012-12-12" "beerpl" 4242
                                        ;; my-intercept-fn
                                        nil
                                        )
        route-query-params {:service-id test-service-id :start-date (joda-datetime->inst (time/days-from (time/now) -120)) :end-date (joda-datetime->inst (time/days-from (time/now) 1))}
        detection-result (detection/detect-route-changes-for-service-new db route-query-params)
        changes (->> detection-result
                    :route-changes
                    (filter :changes))
        changed-route-names (map :route-key changes)
        lohja-change (first (filterv #(and (= "-Lohja - Nummela - Vihti-" (:route-key %)) (:changes %)) (:route-changes *nd)))]
    (println "found" changes "in the following routes:" changed-route-names)
    (def *nd detection-result)
    (println (:start-date route-query-params))
    (testing "got someting"
      (is (not= nil (first detection-result))))
    (testing "got right date for lohja - nummela - vihti change"
      (is (= #inst "2019-02-04" (-> lohja-change :changes :different-week-date java-localdate->inst))))))
