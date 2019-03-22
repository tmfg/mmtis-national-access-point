(ns ote.services.service-search-test
  (:require [ote.services.service-search :as sut]
            [clojure.test :as t :refer [deftest is testing use-fixtures]]
            [ote.test :refer [system-fixture *ote* http-post http-get sql-execute! sql-query]]
            [com.stuartsierra.component :as component]
            [clojure.test.check.generators :as gen]
            [ote.db.generators :as otegen]
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

(defn- publish-services! [saved-services]
  ;; Make all generated test services public (and the generated)
  ;; so that we can verify query results
  (sql-execute! "UPDATE \"transport-service\" SET published = NULL")
  (sql-execute!
   "UPDATE \"transport-service\" SET published = to_timestamp(0) WHERE id IN ("
   (str/join "," saved-services)
   ")"))

(deftest service-search-test

  (let [services (generate-services)
        saved-services (mapv (comp :transit
                                   (partial http-post "admin" "transport-service"))
                             services)]
    (publish-services! (map ::t-service/id saved-services))

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

  (testing "Spatial search returns services which operate in areas intersecting with search areas"
    (let [service (assoc (gen/generate service-generators/gen-transport-service)
                         ::t-service/operation-area [#:ote.db.places{:id "finnish-municipality-105",
                                                                     :namefin "Hyrynsalmi",
                                                                     :type "finnish-municipality",
                                                                     :primary? true}])
          saved-service (http-post "admin" "transport-service" service)]
      (publish-services! [(::t-service/id (:transit saved-service))])
      (let [services-in (fn [area] (get-in (http-get (str "service-search?operation_area=" area "&response_format=json"))
                                           [:json :results]))]
        ;; Matches with itself
        (is (= 1 (count (services-in "Hyrynsalmi"))))
        ;; Doesn't match with neighbouring areas
        (is (zero? (count (services-in "Suomussalmi"))))
        (is (zero? (count (services-in "Ristijärvi"))))
        (is (zero? (count (services-in "Kuhmo"))))
        (is (zero? (count (services-in "Puolanka"))))
        ;; Doesn't match with areas close by
        (is (zero? (count (services-in "Sotkamo"))))
        (is (zero? (count (services-in "Kajaani"))))
        ;; Matches with enveloping areas
        (is (= 1 (count (services-in "Kainuu"))))
        (is (= 1 (count (services-in "Suomi"))))
        (is (= 1 (count (services-in "Eurooppa")))))))


  (testing "Operator search does not return deleted companies"
    (sql-execute! "UPDATE \"transport-operator\" SET \"deleted?\" = TRUE")
    (let [result (http-get "operator-completions/Ajopalvelu?response_format=json")]
      (is (= 200 (:status result)))
      (is (zero? (count (:json result))))))

  (testing "Ranking search results with quality of match against operation-area"
    (let [match-qualities '({:id 817
                             :intersection 0.6384818473140419
                             :difference 66.3123620550177}
                            {:id 1448
                             :intersection 0.1
                             :difference 0.7630913680707156})
          initial-results  [{::t-service/id 817} {::t-service/id 1448}]
          results (sut/sort-by-match-quality initial-results match-qualities)]
      (is (= [1448 817] (map ::t-service/id results)))))

  (testing "Match quality counting"
    (is (= -1 (sut/match-quality 0 1))
        (= 2 (sut/match-quality 2 1)))))
