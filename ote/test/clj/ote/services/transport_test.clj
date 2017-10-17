(ns ote.services.transport-test
  (:require  [clojure.test :as t :refer [deftest testing is]]
             [ote.test :refer [system-fixture http-get]]
             [clojure.java.jdbc :as jdbc]
             [clojure.test.check :as tc]
             [clojure.test.check.generators :as gen]
             [ote.components.db :as db]
             [com.stuartsierra.component :as component]
             [ote.components.http :as http]
             [ote.palvelut.transport :as transport-service]
             [ote.db.transport-operator :as t-operator]
             [ote.db.transport-service :as t-service]
             [ote.db.common :as common]))


(t/use-fixtures :once
  (system-fixture
   :transport (component/using
               (transport-service/->Transport)
               [:http :db])))

;; We have a single transport service inserted in the test data,
;; check that its information is fetched ok
(deftest fetch-test-data-transport-service
  (let [ts (http-get "admin" "transport-service/1")
        ps (get-in ts [:transit ::t-service/passenger-transportation])]
    (println (clojure.pprint/pprint (:transit ts)))
    (is (= (::t-service/contact-address ps)
           #::common {:street "Street 1" :postal_code "90100" :post_office "Oulu"}))
    (is (= (::t-service/price-classes ps)
           [#:ote.db.transport-service{:name "starting",
                                       :price-per-unit 5.9M,
                                       :unit "trip",
                                       :currency "EUR"}
            #:ote.db.transport-service{:name "basic fare",
                                       :price-per-unit 4.9M,
                                       :unit "km",
                                       :currency "EUR"}]))

    (is (= (::t-service/homepage ps) "www.solita.fi"))
    (is (= (::t-service/contact-phone ps) "123456"))))
