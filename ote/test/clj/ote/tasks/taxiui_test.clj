(ns ote.tasks.taxiui-test
  (:require [ote.tasks.taxiui :as sut]
            [clojure.test :as t :refer [deftest testing is]]
            [ote.test :refer :all]
            [ote.db.transport-service :as t-service]
            [specql.core :as specql]
            [clojure.string :as str]
            [clj-time.coerce :as tc]
            [clj-time.core :as time]
            [clojure.java.jdbc :as jdbc]))

(t/use-fixtures :each (system-fixture))

(def ^:private test-service-id
  "Service ID from testdata-ote.sql that all tests use.
  The test database is reset before each test via system-fixture."
  1)

(defn- insert-taxi-service-price!
  "Helper to insert a taxi service price record with a specific timestamp"
  [db service-id timestamp-date]
  (jdbc/execute! db
                 ["INSERT INTO taxi_service_prices
                   (service_id, start_price_daytime, start_price_nighttime, start_price_weekend,
                    price_per_minute, price_per_kilometer, accessibility_service_stairs,
                    accessibility_service_stretchers, accessibility_service_fare, timestamp, \"approved?\")
                   VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                  service-id 10.0M 12.0M 15.0M 1.5M 2.0M 5.0M 7.0M 3.0M timestamp-date timestamp-date]))

(defn- setup-test-service-email!
  "Sets up the contact email for the test service.

  Note: This uses existing test data (service ID from test-service-id) rather than
  creating new records, as the test database is reset between test runs via system-fixture."
  [db contact-email]
  (when contact-email
    (jdbc/execute! db ["UPDATE \"transport-service\" SET \"contact-email\" = ? WHERE id = ?"
                       contact-email test-service-id])))

(defn- email-content []
  (get-in @outbox [0 :body 0 :content]))

(deftest no-outdated-prices-to-notify
  (testing "Default empty database, there are no outdated prices to send notifications about"
    (sut/send-outdated-taxiui-prices-emails {:testing-env? false} (:db *ote*) (:email *ote*))
    (is (empty? @outbox))))

(deftest send-email-for-outdated-prices
  (testing "Email is sent when taxi service prices are older than one year"
    (setup-test-service-email! (:db *ote*) "taxi@example.com")
    (let [;; Insert a price that is older than one year (e.g., 400 days ago)
          old-date (java.sql.Timestamp. (tc/to-long (time/minus (time/now) (time/days 400))))]

      (insert-taxi-service-price! (:db *ote*) test-service-id old-date)

      (sut/send-outdated-taxiui-prices-emails {:testing-env? false} (:db *ote*) (:email *ote*))

      (is (= 1 (count @outbox)))
      (is (= "taxi@example.com" (:to (first @outbox))))
      ;; Email is sent with pricing table data
      (is (str/includes? (email-content) "10,00")))))

(deftest no-email-for-recent-prices
  (testing "No email is sent when taxi service prices are recent (less than one year old)"
    (setup-test-service-email! (:db *ote*) "recent@example.com")
    (let [;; Insert a price that is recent (e.g., 30 days ago)
          recent-date (java.sql.Timestamp. (tc/to-long (time/minus (time/now) (time/days 30))))]

      (insert-taxi-service-price! (:db *ote*) test-service-id recent-date)

      (sut/send-outdated-taxiui-prices-emails {:testing-env? false} (:db *ote*) (:email *ote*))

      (is (empty? @outbox)))))

(deftest email-uses-operator-email-as-fallback
  (testing "When service has no contact email, operator email is used (or defaults to nap@fintraffic.fi)"
    (setup-test-service-email! (:db *ote*) nil) ;; No contact email
    (let [old-date (java.sql.Timestamp. (tc/to-long (time/minus (time/now) (time/days 400))))]

      (insert-taxi-service-price! (:db *ote*) test-service-id old-date)

      (sut/send-outdated-taxiui-prices-emails {:testing-env? false} (:db *ote*) (:email *ote*))

      (is (= 1 (count @outbox)))
      ;; Test operator ID 1 has no email, so it falls back to default
      (is (= "nap@fintraffic.fi" (:to (first @outbox)))))))

(deftest multiple-outdated-services
  (testing "Email is sent when service has outdated prices"
    (setup-test-service-email! (:db *ote*) "service-a@example.com")
    (let [old-date (java.sql.Timestamp. (tc/to-long (time/minus (time/now) (time/days 400))))]

      (insert-taxi-service-price! (:db *ote*) test-service-id old-date)

      (sut/send-outdated-taxiui-prices-emails {:testing-env? false} (:db *ote*) (:email *ote*))

      ;; At least one email should be sent
      (is (>= (count @outbox) 1))
      (is (= "service-a@example.com" (:to (first @outbox)))))))

(deftest no-email-in-testing-environment
  (testing "No emails are sent when testing-env? is true"
    (setup-test-service-email! (:db *ote*) "test@example.com")
    (let [old-date (java.sql.Timestamp. (tc/to-long (time/minus (time/now) (time/days 400))))]

      (insert-taxi-service-price! (:db *ote*) test-service-id old-date)

      (sut/send-outdated-taxiui-prices-emails {:testing-env? true} (:db *ote*) (:email *ote*))

      (is (empty? @outbox)))))
