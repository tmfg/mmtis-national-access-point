(ns ote.services.service-search-test
  (:require [ote.services.service-search :as sut]
            [clojure.test :as t :refer [deftest is testing use-fixtures]]
            [ote.test :refer [system-fixture *ote* http-post http-get sql-execute! sql-query]]
            [com.stuartsierra.component :as component]
            [clojure.test.check.generators :as gen]
            [ote.db.service-generators :as service-generators]
            [ote.db.transport-service :as t-service]
            [ote.db.transport-operator :as t-operator]
            [ote.services.transport :as transport-service]
            [clojure.string :as str]))

(use-fixtures :each
  (system-fixture
   :transport-service
   (component/using (transport-service/->Transport nil) [:http :db])

   :service-search
   (component/using (sut/->ServiceSearch) [:http :db])))

(defn- generate-services []
   (take 10 (repeatedly
             #(gen/generate
               service-generators/gen-transport-service))))

(deftest service-search-test

  (let [services (generate-services)
        saved-services (mapv (comp :transit
                                   (partial http-post "admin" "transport-service"))
                             services)]

    ;; Make all generated test services public (and the generated)
    ;; so that we can verify query results
    (sql-execute! "UPDATE \"transport-service\" SET published = NULL")
    (sql-execute!
     "UPDATE \"transport-service\" SET published = to_timestamp(0) WHERE id IN ("
     (str/join "," (map ::t-service/id saved-services))
     ")")

    (testing "Searching by exact name returns it"
      (let [{name ::t-service/name :as s} (first services)
            result (http-get (str "service-search?text="
                                  name
                                  "&response_format=json"))]
        (is (= 200 (:status result)))
        (is (pos? (count (:results (:json result)))))
        (is (some #(= name (:name %))
                  (:results (:json result))))))

    (testing "Search by subtype"
      (testing "Searching by subtype find correct amount"
        (let [types (group-by ::t-service/sub-type services)]
          (doseq [[t services] types
                  :let [result (http-get (str "service-search?sub_types=" (name t) "&response_format=json"))]]
            (is (= (count services)
                   (count (:results (:json result))))))))

      (testing "Search by all sub-types returns all results"
        (is (= (count services)
               (count
                (:results
                 (:json
                  (http-get (str "service-search?sub-types="
                                 (str/join "," (into #{} (map ::t-service/sub-type) services))
                                 "&response_format=json")))))))))

    (testing "Search operators by name"
      (let [operator-names (sql-query
                             "SELECT * FROM \"transport-operator\" WHERE id IN ("
                             (str/join "," (map ::t-service/transport-operator-id saved-services))
                             ")")
            name (subs (:name (first operator-names)) 0 5)
            get-url (str "operator-completions/" name)
            result (http-get get-url)
            transit-result (:transit result)]

        (is (= 200 (:status result)))
        (is (pos? (count transit-result)))
        (is (some #(= (:name (first operator-names)) (:operator %))
                  transit-result))))

    (testing "Search by operator"
      (let [{op-id ::t-service/transport-operator-id :as s} (first services)
            operator (sql-query  "SELECT * FROM \"transport-operator\" WHERE id = " op-id)
            op-bi (:business-id (first operator))
            result (http-get (str "service-search?operators=" op-bi "&response_format=json"))]
        (is (= 200 (:status result)))
        (is (pos? (count (:results (:json result)))))
        (is (some #(= op-bi (:business-id %))
                  (:results (:json result)))))))

  (testing "Operator search returns matching company"
    (let [result (http-get "operator-completions/Ajopalvelu?response_format=json")]
      (is (= 200 (:status result)))
      (is (pos? (count (:json result))))
      (is (some #(= "Ajopalvelu Testinen Oy" (:operator %))
                (:json result)))))

  (testing "Operator search does not return deleted companies"
    (sql-execute! "UPDATE \"transport-operator\" SET \"deleted?\" = TRUE")
    (let [result (http-get "operator-completions/Ajopalvelu?response_format=json")]
      (is (= 200 (:status result)))
      (is (zero? (count (:json result)))))))
