(ns ote.tasks.pre-notices-test
  (:require [ote.tasks.pre-notices :as sut]
            [clojure.test :as t :refer [deftest testing is]]
            [ote.test :refer :all]
            [ote.db.transit :as transit]
            [specql.core :as specql]
            [ote.db.modification :as modification]
            [ote.db.transport-operator :as t-operator]
            [clojure.string :as str]
            [ote.db.user-notifications :as user-notifications]
            [ote.services.settings :as settings]
            [com.stuartsierra.component :as component]
            [ote.db.gtfs :as gtfs]
            [ote.time :as time]
            [clj-time.coerce :as tc]))


(t/use-fixtures :each (system-fixture
                       :settings (component/using (settings/->Settings) [:db :http])))

(defn send! []
  (sut/send-notification! (:db *ote*) (:email *ote*) constantly))

(def test-pre-notice {::t-operator/id 1
                      ::transit/sent (java.util.Date.)
                      ::transit/pre-notice-state :sent
                      ::transit/route-description "this is a test route"
                      ::transit/description "this will change somehow"
                      ::transit/url "http://example.com/shape-of-things-to-come"
                      ::transit/pre-notice-type [:route-change]
                      ::transit/effective-dates [{::transit/effective-date (java.util.Date.)
                                                  ::transit/effective-date-description "this is happening right now"}]
                      ::modification/created (java.util.Date.)})

(defn- email-content []
  (get-in @outbox [0 :body 0 :content]))

(deftest no-notices-to-send
  (testing "Default empty database, there are no notices to send"
    (send!)
    (is (empty? @outbox))))

(deftest send-new-pre-notice
  (specql/insert! (:db *ote*) ::transit/pre-notice
                  test-pre-notice)
  (send!)
  (is (= 1 (count @outbox)))
  (is (str/includes? (email-content) "this is a test route")))

(deftest send-region-preferences
  (specql/insert! (:db *ote*) ::transit/pre-notice
                  (merge test-pre-notice
                         {::transit/regions ["01"]}))

  (testing "Nothing is sent if the regions don't match"
    (http-post "admin" "settings/email-notifications"
               {::user-notifications/finnish-regions ["02"]})
    (send!)
    (is (empty? @outbox)))

  (testing "Email is sent when regions match"
    (http-post "admin" "settings/email-notifications"
               {::user-notifications/finnish-regions ["01" "02"]})
    (send!)
    (is (= 1 (count @outbox)))))

(deftest email-includes-detected-changes

  (testing "nothing is sent before detected change"
    (send!)
    (is (empty? @outbox)))

  (testing "inserted change is found and sent"
    ;; Stupid way to clean up database. But package is hard coded to these test. So it must remain the same.
    (specql/delete! (:db *ote*) :gtfs/detection-route
                    {:gtfs/package-id 1}) ;; Clean detection-route to prevent foreign key problems
    (specql/delete! (:db *ote*) :gtfs/package
                    {:gtfs/id 1})
    (specql/delete! (:db *ote*) :gtfs/transit-changes
                    {:gtfs/date (tc/to-sql-date (time/now))})
    (specql/delete! (:db *ote*) :gtfs/detected-change-history
                    {:gtfs/transport-service-id 2})
    (specql/delete! (:db *ote*) :gtfs/detected-route-change
                    {:gtfs/transit-service-id 2})

    ;; Create package-id (email content is dependent on this id)
    (specql/insert! (:db *ote*) :gtfs/package
                    {:gtfs/id 1
                     :gtfs/transport-operator-id 1
                     :gtfs/transport-service-id  2
                     :gtfs/created               (tc/to-sql-date (time/now))})

    (specql/insert! (:db *ote*) :gtfs/transit-changes
                    {:gtfs/transport-service-id 2
                     :gtfs/date                 (tc/to-sql-date (time/now))
                     :gtfs/current-week-date    (tc/to-sql-date (time/now))
                     :gtfs/different-week-date  (tc/to-sql-date (time/days-from (time/now) 70))
                     :gtfs/change-date          (tc/to-sql-date (time/days-from (time/now) 67))
                     :gtfs/package-ids          [1]
                     :gtfs/removed-routes       1
                     :gtfs/added-routes         2
                     :gtfs/changed-routes       3})

    ;; Add one change to route-change table
    (specql/insert! (:db *ote*) :gtfs/detected-route-change
                    {:gtfs/transit-service-id 2
                     :gtfs/transit-change-date (tc/to-sql-date (time/now))

                     :gtfs/different-week-date  (tc/to-sql-date (time/days-from (time/now) 70))
                     :gtfs/current-week-date    (tc/to-sql-date (time/now))
                     :gtfs/created-date    (tc/to-sql-date (time/now))
                     :gtfs/route-hash-id "abcd"
                     :gtfs/route-long-name "long"
                     :gtfs/route-short-name "short"
                     :gtfs/change-str "abc"})

    ;; Add one change to history table
    (specql/insert! (:db *ote*) :gtfs/detected-change-history
                    {:gtfs/transport-service-id 2
                     :gtfs/different-week-date  (tc/to-sql-date (time/days-from (time/now) 70))
                     :gtfs/package-ids          [1]
                     :gtfs/route-hash-id "abcd"
                     :gtfs/change-str "abc"})

    (send!)
    (is (= 1 (count @outbox)))
    (is (str/includes? (email-content) "tunnistetut"))))
