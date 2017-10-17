(ns ote.services.transport-test
  (:require  [clojure.test :as t :refer [deftest testing is]]
             [ote.test :refer [system-fixture http-get http-post]]
             [clojure.java.jdbc :as jdbc]
             [clojure.test.check :as tc]
             [clojure.test.check.generators :as gen]
             [clojure.test.check.clojure-test :refer [defspec]]
             [clojure.test.check.properties :as prop]
             [ote.components.db :as db]
             [com.stuartsierra.component :as component]
             [ote.components.http :as http]
             [ote.palvelut.transport :as transport-service]
             [ote.db.transport-operator :as t-operator]
             [ote.db.transport-service :as t-service]
             [ote.db.common :as common]
             [clojure.spec.gen.alpha :as sgen]
             [clojure.spec.test.alpha :as stest]
             [clojure.spec.alpha :as s]
             [ote.db.generators :as generators]))


(t/use-fixtures :once
  (system-fixture
   :transport (component/using
               (transport-service/->Transport)
               [:http :db])))

(def gen-passenger-transportation
  (gen/hash-map
   ::t-service/contact-email (s/gen ::t-service/contact-email)
   ::t-service/accessibility-tool (s/gen ::t-service/accessibility-tool)
   ::t-service/additional-services (s/gen ::t-service/additional-services)
   ::t-service/price-classes generators/gen-price-class-array
   ::t-service/booking-service generators/gen-service-link
   ::t-service/contact-gsm (s/gen ::t-service/contact-gsm)
   ::t-service/payment-methods (s/gen ::t-service/payment-methods)
   ::t-service/real-time-information generators/gen-service-link
   ::t-service/contact-address generators/gen-address
   ::t-service/accessibility-description generators/gen-localized-text-array
   ::t-service/homepage (s/gen ::t-service/homepage)
   ::t-service/contact-phone (s/gen ::t-service/contact-phone)
   ::t-service/service-hours generators/gen-service-hours-array
   ::t-service/luggage-restrictions generators/gen-localized-text-array))

(def gen-passenger-transportation-service
  (gen/hash-map
   ::t-service/transport-operator-id (gen/return 1)
   ::t-service/type (gen/return :passenger-transportation)
   ::t-service/passenger-transportation gen-passenger-transportation))

;; We have a single transport service inserted in the test data,
;; check that its information is fetched ok
(deftest fetch-test-data-transport-service
  (let [ts (http-get "admin" "transport-service/1")
        ps (get-in ts [:transit ::t-service/passenger-transportation])]
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


(defspec save-and-fetch-generated-passenger-transport-service
  200
  (prop/for-all
   [transport-service gen-passenger-transportation-service]

   (let [inserted (http-post "admin" "passenger-transportation-info"
                             transport-service)]
     (and (= (:status inserted) 200)
          (contains? (:transit inserted) ::t-service/id)))))
