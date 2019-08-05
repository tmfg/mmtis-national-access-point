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
                                   (partial http-post (:user-id-admin @ote.test/user-db-ids-atom) "transport-service"))
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
          saved-service (http-post (:user-id-admin @ote.test/user-db-ids-atom)
                                   "transport-service"
                                   service)]
      (publish-services! [(::t-service/id (:transit saved-service))])
      (let [services-in (fn [area] (get-in
                                     (http-get (str "service-search?operation_area=" area "&response_format=json"))
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

  (testing "Spatial search returns services which operate in areas intersecting with search areas 2nd sample set"
    (let [service (assoc (gen/generate service-generators/gen-transport-service)
                         ::t-service/operation-area [#:ote.db.places{:id "finnish-postal-90900",
                                                                     :namefin "90900 Kiiminki Keskus",
                                                                     :type "finnish-postal",
                                                                     :primary? true}])
          saved-service (http-post (:user-id-admin @ote.test/user-db-ids-atom)
                                   "transport-service"
                                   service)]
      (publish-services! [(::t-service/id (:transit saved-service))])
      (let [services-in (fn [area]
                          (get-in
                            (http-get (str "service-search?operation_area=" area "&response_format=json"))
                            [:json :results]))]
        ;; Matches with itself
        (is (= 1 (count (services-in "90900 Kiiminki Keskus"))))
        ;; Doesn't match with neighbouring areas
        (is (zero? (count (services-in "90630 Korvensuora"))))
        (is (zero? (count (services-in "90660 Sanginsuu"))))
        (is (zero? (count (services-in "91200 Yli-Ii Keskus"))))
        (is (zero? (count (services-in "91210 Jakkukylä"))))
        (is (zero? (count (services-in "91260 Pahkakoski-Räinä"))))
        (is (zero? (count (services-in "90910 Kontio"))))
        (is (zero? (count (services-in "90940 Jääli"))))
        (is (zero? (count (services-in "91300 Ylikiiminki Keskus"))))
        (is (zero? (count (services-in "91310 Arkala"))))
        ;; Doesn't match with areas close by
        (is (zero? (count (services-in "Sotkamo"))))
        (is (zero? (count (services-in "Kajaani"))))
        ;; Matches with enveloping areas
        (is (= 1 (count (services-in "Oulu"))))
        (is (= 1 (count (services-in "Pohjois-Pohjanmaa"))))
        (is (= 1 (count (services-in "Suomi"))))
        (is (= 1 (count (services-in "Eurooppa")))))))

  (testing "Spatial search returns services which operate in areas intersecting with search areas 3rd sample set"
    (let [service (assoc (gen/generate service-generators/gen-transport-service)
                         ::t-service/operation-area [#:ote.db.places{:id "finnish-municipality-249",
                                                                     :namefin "Keuruu",
                                                                     :type "finnish-municipality",
                                                                     :primary? true}])
          saved-service (http-post (:user-id-admin @ote.test/user-db-ids-atom)
                                   "transport-service"
                                   service)]
      (publish-services! [(::t-service/id (:transit saved-service))])
      (let [services-in (fn [area]
                          (get-in
                            (http-get (str "service-search?operation_area=" area "&response_format=json"))
                            [:json :results]))]
        ;; Matches with itself
        (is (= 1 (count (services-in "Keuruu"))))
        ;; Doesn't match with neighbouring areas
        (is (zero? (count (services-in "Petäjävesi"))))
        (is (zero? (count (services-in "Virrat"))))
        (is (zero? (count (services-in "Mänttä-Vilppula"))))
        (is (zero? (count (services-in "Multia"))))
        (is (zero? (count (services-in "Jämsä"))))
        (is (zero? (count (services-in "Ähtäri"))))
        (is (zero? (count (services-in "Pirkanmaa"))))
        (is (zero? (count (services-in "Etelä-Pohjanmaa"))))
        ;; Doesn't match with exterior areas
        (is (zero? (count (services-in "Sotkamo"))))
        (is (zero? (count (services-in "Kajaani"))))
        ;; Matches with overlapping areas
        (is (= 1 (count (services-in "Keski-Suomi"))))
        (is (= 1 (count (services-in "41970 Huttula"))))
        (is (= 1 (count (services-in "42520 Asunta"))))

        ;; Matches with enveloping areas
        (is (= 1 (count (services-in "Suomi"))))
        (is (= 1 (count (services-in "Eurooppa")))))))

  (testing "Spatial search returns services which operate in areas intersecting with search areas 4th sample set"
    (let [service (assoc (gen/generate service-generators/gen-transport-service)
                         ::t-service/operation-area [#:ote.db.places{:id "finnish-postal-33200",
                                                                     :namefin "33200 Tampere Keskus Läntinen",
                                                                     :type "finnish-postal",
                                                                     :primary? true}])
          saved-service (http-post (:user-id-admin @ote.test/user-db-ids-atom)
                                   "transport-service"
                                   service)]
      (publish-services! [(::t-service/id (:transit saved-service))])
      (let [services-in (fn [area]
                          (get-in
                            (http-get (str "service-search?operation_area=" area "&response_format=json"))
                            [:json :results]))]
        ;; Matches with itself
        (is (= 1 (count (services-in "33200 Tampere Keskus Läntinen"))))
        ;; Doesn't match with neighbouring areas
        (is (zero? (count (services-in "33210 Itä-Amuri-Tammerkoski"))))
        (is (zero? (count (services-in "33230 Länsi-Amuri"))))
        (is (zero? (count (services-in "33100 Tampere Keskus"))))
        (is (zero? (count (services-in "33900 Härmälä-Rantaperkiö"))))
        ;; Doesn't match with exterior areas
        (is (zero? (count (services-in "Sotkamo"))))
        (is (zero? (count (services-in "Kajaani"))))

        ;; Matches with enveloping areas
        (is (= 1 (count (services-in "Tampere"))))
        (is (= 1 (count (services-in "Pirkanmaa"))))
        (is (= 1 (count (services-in "Suomi"))))
        (is (= 1 (count (services-in "Eurooppa")))))))

  (testing "Spatial search returns services with operation area defined as a single point when searching with the surrounding area"
    (let [service (assoc (gen/generate service-generators/gen-transport-service)
                         ::t-service/operation-area [{:ote.db.places/type "drawn"
                                                      :ote.db.places/namefin "Kannus rautatieasema"
                                                      :ote.db.places/primary? true
                                                      :geojson "{\"type\":\"Point\",\"coordinates\":[23.914974,63.898401]}"}])
          saved-service (http-post (:user-id-admin @ote.test/user-db-ids-atom)
                                   "transport-service"
                                   service)]
      (publish-services! [(::t-service/id (:transit saved-service))])
      (let [services-in (fn [area]
                          (get-in
                            (http-get (str "service-search?operation_area=" area "&response_format=json"))
                            [:json :results]))]
        ;; Doesn't match with neighbouring areas
        (is (zero? (count (services-in "Petäjävesi"))))
        (is (zero? (count (services-in "Virrat"))))
        (is (zero? (count (services-in "Mänttä-Vilppula"))))
        (is (zero? (count (services-in "Multia"))))
        (is (zero? (count (services-in "Jämsä"))))
        (is (zero? (count (services-in "Ähtäri"))))
        (is (zero? (count (services-in "Pirkanmaa"))))
        (is (zero? (count (services-in "Etelä-Pohjanmaa"))))
        ;; Doesn't match with exterior areas
        (is (zero? (count (services-in "Sotkamo"))))
        (is (zero? (count (services-in "Kajaani"))))
        ;; Matches with enveloping areas
        (is (= 1 (count (services-in "Keski-Pohjanmaa"))))
        (is (= 1 (count (services-in "69100 Kannus Keskus"))))
        (is (= 1 (count (services-in "Kannus"))))
        (is (= 1 (count (services-in "Suomi"))))
        (is (= 1 (count (services-in "Eurooppa")))))))

  (testing "Spatial search results are ranked so services with operating areas matching closest to the search term are returned first"
    (let [tampere-service (assoc (gen/generate service-generators/gen-transport-service)
                                 ::t-service/operation-area [#:ote.db.places{:id "finnish-municipality-837",
                                                                             :namefin "Tampere",
                                                                             :type "finnish-municipality",
                                                                             :primary? true}])
          marked-service (assoc tampere-service ::t-service/name "Tampereen keskustan palvelu")
          services (cons marked-service
                         (take 30 (repeat (assoc (gen/generate service-generators/gen-transport-service)
                                                 ::t-service/operation-area [#:ote.db.places{:id "finnish-postal-33200",
                                                                                             :namefin "33200 Tampere Keskus Läntinen",
                                                                                             :type "finnish-postal",
                                                                                             :primary? true}]))))
          saved-services (map (partial
                                http-post (:user-id-admin @ote.test/user-db-ids-atom)
                                "transport-service")
                              (shuffle services))]
      (publish-services! (map #(::t-service/id (:transit %1)) saved-services))
      (let [services-in (fn [area]
                          (get-in
                            (http-get (str "service-search?operation_area=" area "&response_format=json"))
                            [:json :results]))]
        ;; All services are in Tampere
        (is (= 31 (count (services-in "Tampere"))))
        ;; Services are found with postal code as well as it intersects with Tampere 
        (is (= 31 (count (services-in "33200 Tampere Keskus Läntinen"))))

        ;; Matches with enveloping areas
        (is (= 31 (count (services-in "Suomi"))))
        (is (= 31 (count (services-in "Eurooppa"))))

        ;; Best matching result is returned first
        (is (= "Tampereen keskustan palvelu" (:name (get (services-in "Tampere") 0)))))))

  (testing "Spatial search results are ranked so services with operating areas matching closest to the search term are returned first, even when limited"
    (let [tampere-service (assoc (gen/generate service-generators/gen-transport-service)
                                 ::t-service/operation-area [#:ote.db.places{:id "finnish-municipality-837",
                                                                             :namefin "Tampere",
                                                                             :type "finnish-municipality",
                                                                             :primary? true}])
          marked-service (assoc tampere-service ::t-service/name "Tampereen keskustan palvelu")
          services (cons marked-service
                         (take 30 (repeat (assoc (gen/generate service-generators/gen-transport-service)
                                                 ::t-service/operation-area [#:ote.db.places{:id "finnish-postal-33200",
                                                                                             :namefin "33200 Tampere Keskus Läntinen",
                                                                                             :type "finnish-postal",
                                                                                             :primary? true}]))))
          saved-services (map (partial
                                http-post (:user-id-admin @ote.test/user-db-ids-atom)
                                "transport-service")
                              (shuffle services))]
      (publish-services! (map #(::t-service/id (:transit %1)) saved-services))
      (let [services-in (fn [area] (get-in (http-get (str "service-search?operation_area=" area "&response_format=json&limit=25&offset=0"))
                                           [:json :results]))]
        ;; All services are in Tampere
        (is (= 25 (count (services-in "Tampere"))))
        ;; Services are found with postal code as well as it intersects with Tampere 
        (is (= 25 (count (services-in "33200 Tampere Keskus Läntinen"))))

        ;; Matches with enveloping areas
        (is (= 25 (count (services-in "Suomi"))))
        (is (= 25 (count (services-in "Eurooppa"))))

        ;; Best matching result is returned first
        (is (= "Tampereen keskustan palvelu" (:name (get (services-in "Tampere") 0)))))))

  (testing "Operator search does not return deleted companies"
    (sql-execute! "UPDATE \"transport-operator\" SET \"deleted?\" = TRUE")
    (let [result (http-get "operator-completions/Ajopalvelu?response_format=json")]
      (is (= 200 (:status result)))
      (is (zero? (count (:json result)))))))
