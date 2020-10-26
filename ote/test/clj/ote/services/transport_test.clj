(ns ote.services.transport-test
  (:require [clojure.test :as t :refer [deftest testing is]]
            [ote.test :refer [system-fixture http-get http-post]]
            [clojure.java.jdbc :as jdbc]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]
            [cheshire.core :as chesire]
            [ote.components.db :as db]
            [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [ote.services.transport :as transport-service]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]
            [ote.db.common :as common]
            [clojure.spec.gen.alpha :as sgen]
            [clojure.spec.test.alpha :as stest]
            [clojure.spec.alpha :as s]
            [ote.db.generators :as generators]
            [ote.db.service-generators :as s-generators]
            [clojure.string :as str]
            [clojure.set :as set]
            [ote.time :as time]
            [clj-time.coerce :as time-coerce]
            [specql.core :as specql]
            [ote.integration.import.gtfs :as gtfs-import]
            [ote.services.admin :as admin]
            [cheshire.core :as cheshire]))

(def enabled-features {:enabled-features #{:ote-login
                                           :sea-routes
                                           :gtfs-import
                                           :ote-register
                                           :netex-conversion-automated
                                           :service-validation
                                           :terms-of-service
                                           :other-catalogs}})

(t/use-fixtures :each
                (system-fixture
                  :admin (component/using (admin/->Admin enabled-features)
                                          [:http :db])
                  :transport (component/using
                               (transport-service/->TransportService
                                 enabled-features)
                               [:http :db])))

;; We have a single transport service inserted in the test data,
;; check that its information is fetched ok
(deftest fetch-test-data-transport-service
  (let [ts (http-get (:user-id-admin @ote.test/user-db-ids-atom)
                     "transport-service/1")
        service (:transit ts)
        ps (::t-service/passenger-transportation service)]
    (is (= (::t-service/contact-address service)
           {::common/post_office "Oulu" ::common/postal_code "90100" ::common/street "Street 1" ::common/country_code "FI"}))
    (is (= (::t-service/price-classes ps)
           [#:ote.db.transport-service{:name "starting",
                                       :price-per-unit 5.9M,
                                       :unit "trip"}
            #:ote.db.transport-service{:name "basic fare",
                                       :price-per-unit 4.9M,
                                       :unit "km"}]))

    (is (= (::t-service/homepage service) "www.solita.fi"))
    (is (= (::t-service/contact-phone service) "123456"))))

(defn- non-nil-keys [m]
  (into #{}
        (keep (fn [[k v]]
                (when (and (not (nil? v))
                           (or (not (string? v))
                               (not (str/blank? v)))
                           (or (not (coll? v))
                               (not (empty? v))))
                  k)))
        m))

(defn date-fields [d]
  (if (nil? d)
    {}
    (select-keys (time/date-fields d) #{::time/year ::time/month ::time/date})))

(defn- compare-dates
  "Compare database DATE types (year, month and day)"
  [v1 v2]
  (= (date-fields v1) (date-fields v2)))


(defmulti compare-values (fn [comparison v1 v2] comparison))

(defmethod compare-values ::t-service/to-date [_ v1 v2] (compare-dates v1 v2))
(defmethod compare-values ::t-service/from-date [_ v1 v2] (compare-dates v1 v2))

(defn- normalize-interval [interval]
  (if (nil? interval)
    -1
    (long (+ (.getSeconds interval)
             (* 60 (.getMinutes interval))
             (* 60 60 (.getHours interval))
             (* 24 60 60 (.getDays interval))))))

(defmethod compare-values ::t-service/maximum-stay [_ v1 v2]
  (= (normalize-interval v1) (normalize-interval v2)))

(defmethod compare-values :default [_ v1 v2]
  (if-not (= v1 v2)
    (do (println "Values are not the same: " v1 " != " v2) false)
    true))

(defn effectively-same-deep
  "Compare that nested structures are effectively the same (insertion vs specql fetch).
  Maps and vectors are compared in a nested way.
  Ignores keys with nil values and empty strings (for composites)."
  ([v1 v2] (effectively-same-deep v1 v2 :default))
  ([v1 v2 comparison]

   (cond
     ;; Both values are maps, check that they have the same non-nil keys
     (and (map? v1) (map? v2))
     (let [keys-v1 (non-nil-keys v1)
           keys-v2 (non-nil-keys v2)]
       (if-not (= keys-v1 keys-v2)
         (do (println "Maps don't have the same non-empty keys"
                      (when-let [not-in-v1 (seq (set/difference keys-v2 keys-v1))]
                        (str ", not in left: " (str/join ", " not-in-v1)))
                      (when-let [not-in-v2 (seq (set/difference keys-v1 keys-v2))]
                        (str ", not in right: " (str/join ", " not-in-v2))))
             false)
         (and (= keys-v1 keys-v2)
              (every? #(let [result (effectively-same-deep (get v1 %)
                                                           (get v2 %)
                                                           %)]
                         (when-not result
                           (println "Values for key" % "are not the same!"
                                    "\nV1:" (pr-str v1)
                                    "\nV2:" (pr-str v2)))
                         result)
                      keys-v1))))

     ;; Both values are vectors, check that values at every index are the same
     (and (vector? v1) (vector? v2))
     (if-not (= (count v1) (count v2))
       (do
         (println "Vector lengths differ:" (count v1) "!=" (count v2)
                  "\nV1:" (pr-str v1)
                  "\nV2:" (pr-str v2))
         false)
       (every? true?
               (map (fn [left right]
                      (let [result (effectively-same-deep left right)]
                        (when-not result
                          (println "Vector values are not the same!"
                                   "\nV1:" (pr-str v1)
                                   "\nV2:" (pr-str v2)))
                        result))
                    v1 v2)))

     ;; Both are strings, trim and compare
     (and (string? v1) (string? v2))
     (= (str/trim v1) (str/trim v2))

     ;; Other values, just compare
     :default
     (compare-values comparison v1 v2))))

(defn- save-and-fetch-compare [transport-service compare-key]
  (let [response (http-post (:user-id-admin @ote.test/user-db-ids-atom)
                            "transport-service"
                            transport-service)
        service (:transit response)
        fetch-response (http-get "admin"
                                 (str "transport-service/" (::t-service/id service)))
        fetched (:transit fetch-response)]

    (and (= (:status response) (:status fetch-response) 200)
         (effectively-same-deep
           (compare-key service)
           (compare-key fetched)))))

(defspec save-and-fetch-generated-passenger-transport-service
         25
         (prop/for-all
           [transport-service (s-generators/service-type-generator :passenger-transportation)]
           (save-and-fetch-compare transport-service ::t-service/passenger-transportation)))

(defspec save-and-fetch-generated-parking-service
         25
         (prop/for-all
           [transport-service (s-generators/service-type-generator :parking)]
           (save-and-fetch-compare transport-service ::t-service/parking)))

(defspec save-and-fetch-generated-rental-service
         25
         (prop/for-all
           [transport-service (s-generators/service-type-generator :rentals)]
           (save-and-fetch-compare transport-service ::t-service/rentals)))

(deftest save-terminal-service-to-wrong-operator
  (let [generated-terminal-service (gen/generate s-generators/gen-terminal-service)
        modified-terminal-service (assoc generated-terminal-service ::t-service/transport-operator-id 2)
        response (http-post (:user-id-normal @ote.test/user-db-ids-atom)
                            "transport-service"
                            modified-terminal-service)
        service (:transit response)
        ;; GET generated service from server
        terminal-service (http-get "normaluser" (str "transport-service/" (::t-service/id service)))
        fetched (:transit terminal-service)
        ;; Change operator id from 2 -> 1 - which will cause error
        problematic-terminal-service (assoc fetched ::t-service/transport-operator-id 1)]

    ;; Gives error, because user doesn't have access rights to operator 1
    (is (thrown-with-msg?
          clojure.lang.ExceptionInfo #"status 403"
          (http-post (:user-id-normal @ote.test/user-db-ids-atom)
                     "transport-service"
                     problematic-terminal-service)))))

(deftest delete-transport-service
  (let [service (assoc (gen/generate s-generators/gen-transport-service)
                  ::t-service/transport-operator-id 2)
        save-response (http-post (:user-id-normal @ote.test/user-db-ids-atom)
                                 "transport-service"
                                 service)
        id (get-in save-response [:transit ::t-service/id])]
    ;; Saved ok
    (is (pos? id))

    ;; Fetch and it exists
    (let [fetch-response (http-get "normaluser" (str "transport-service/" id))]
      (is (= 200 (:status fetch-response)))
      (is (= id (get-in fetch-response [:transit ::t-service/id]))))

    ;; Delete
    (let [delete-response (http-post (:user-id-normal @ote.test/user-db-ids-atom)
                                     "transport-service/delete"
                                     {:id id})]
      (is (= (:transit delete-response) id)))

    ;; Try to fetch now and it does not exist
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo #"status 404"
         (http-get "normaluser" (str "transport-service/" id))))))
