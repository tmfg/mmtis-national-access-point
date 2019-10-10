(ns ote.tasks.company-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [ote.tasks.company :refer [update-one-csv! store-daily-company-stats]]
            [ote.test :refer [system-fixture with-http-resource http-post sql-query sql-execute! *ote*]]
            [ote.services.transport :as transport-service]
            [com.stuartsierra.component :as component]
            [ote.services.external :as external]
            [clojure.string :as str]
            [clojure.test.check.generators :as gen]
            [ote.db.service-generators :as s-generators]
            [ote.db.transport-service :as t-service]))

(use-fixtures :each
  (system-fixture
   :transport (component/using
                (transport-service/->TransportService
                  (:nap nil))
                [:http :db])))

(defn company-csv [companies]
  (str "business id,name\n"
       (str/join "\n"
                 (map #(str (:business-id %) "," (:name %)) companies))))

(def test-companies #{{:business-id "1234567-8" :name "company 1"}
                      {:business-id "2345678-9" :name "company 2"}})

(defn fetch-companies [id]
  (set
   (sql-query
    "SELECT (x.c).\"business-id\", (x.c).name"
    " FROM ("
    "SELECT unnest(companies) c"
    "  FROM service_company "
    " WHERE \"transport-service-id\" = " id
    ") x")))

(deftest csv-is-updated
  (with-http-resource
    "companies" ".csv"
    (fn [file url]
      (spit file (company-csv test-companies))
      (let [ts (->  (s-generators/service-type-generator :passenger-transportation)
                    gen/generate
                    (dissoc ::t-service/companies)
                    (assoc ::t-service/companies-csv-url url
                           ::t-service/company-source :csv-url))
            response (http-post (:user-id-admin @ote.test/user-db-ids-atom)
                                "transport-service"
                                ts)
            id (get-in response [:transit ::t-service/id])]
        (is (= 200 (:status response)))

        ;; check that companies are stored
        (is (= test-companies (fetch-companies id)))

        ;; write new CSV data
        (let [test-companies (conj test-companies
                                   {:business-id "6666667-8" :name "devilish inc"})]
          (spit file (company-csv test-companies))
          (sql-execute! "UPDATE service_company"
                        "   SET updated = updated - '2 days'::interval"
                        " WHERE \"transport-service-id\" = " id)
          (update-one-csv! (:db *ote*))
          (is (= test-companies (fetch-companies id))))))))
