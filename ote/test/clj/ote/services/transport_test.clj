(ns ote.services.transport-test
  (:require [clojure.test :as t :refer [deftest testing is]]
            [ote.test :refer [system-fixture http-get http-post]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]
            [com.stuartsierra.component :as component]
            [ote.services.transport :as transport-service]
            [ote.db.transport-service :as t-service]
            [ote.db.common :as common]
            [ote.db.service-generators :as s-generators]
            [clojure.string :as str]
            [clojure.set :as set]
            [ote.time :as time]
            [clj-time.coerce :as time-coerce]
            [specql.core :as specql]
            [ote.integration.import.gtfs :as gtfs-import]
            [ote.services.admin :as admin]
            [ote.test-tools :as test-tools]))

(def enabled-features {:enabled-features #{:ote-login
                                           :sea-routes
                                           :gtfs-import
                                           :ote-register
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
        fetched (:transit fetch-response)
        ;; Rental booking info is added for service when service-type is :passenger-transportation without it having it. for UI purposes
        ;; Generator doesn't have this data, so we remove if from fetched data
        fetched (if (= (::t-service/type transport-service) :passenger-transportation)
                  (test-tools/dissoc-in fetched [::t-service/passenger-transportation :ote.db.rental-booking-service/rental-booking-info])
                  fetched)]

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

(defn- create-new-package [db operator-id ts-id interface-id]
  (let [new-gtfs-hash (gtfs-import/gtfs-hash "sdfsdf")]
    (specql/insert! db :gtfs/package
                    {:gtfs/sha256 new-gtfs-hash
                     :gtfs/first_package true               ;;

                     :gtfs/transport-operator-id operator-id
                     :gtfs/transport-service-id ts-id
                     :gtfs/created (time-coerce/to-sql-date (time/now))
                     :gtfs/etag (str "etag-" ts-id)
                     :gtfs/license "license"
                     :gtfs/external-interface-description-id interface-id})))

(defn- clean-service-for-printing [service]
  (-> service
      (dissoc :ote.db.transport-service/operation-area)
      (dissoc :ote.db.transport-service/description)
      (dissoc :ote.db.transport-service/contact-address)
      (dissoc :ote.db.transport-service/homepage)
      (dissoc :ote.db.transport-service/passenger-transportation)))

(defn- clean-interfaces-for-printing [interfaces]
  (map #(-> %
            (update ::t-service/external-interface dissoc ::t-service/description)
            (update ::t-service/external-interface dissoc ::t-service/format)
            (update ::t-service/external-interface dissoc ::t-service/license))
       interfaces))

(deftest update-external-interface-url
  (let [operator-id 2                                       ;; force operator id
        db (:db ote.test/*ote*)
        generated-service (gen/generate (s-generators/service-sub-type-generator :schedule))
        ;; Generated interfaces cannot be trusted so create one with correct data
        default-interface {::t-service/external-interface {::t-service/url "www.default.url"
                                                           ::t-service/description [{::t-service/lang "FI",
                                                                                     ::t-service/text "Default text"}]}
                           ::t-service/data-content [:route-and-schedule]
                           ::t-service/format ["GTFS"]
                           ::t-service/license "jnppWN61pC0u77PG4ha0"}
        generated-service (-> generated-service
                              (assoc ::t-service/transport-operator-id operator-id)
                              (dissoc ::t-service/external-interfaces)
                              (assoc-in [::t-service/external-interfaces] [default-interface])
                              ;; "move" to validation
                              (assoc ::t-service/validate? (time-coerce/to-sql-date (time/now))))
        saved-service (:transit (http-post (:user-id-normal @ote.test/user-db-ids-atom) "transport-service" generated-service))
        service-id (::t-service/id saved-service)
        saved-service (:transit (http-get "normaluser" (str "transport-service/" service-id)))
        ;; Ensure that service is in validate state (admin must accept the service)
        _ (is (not (nil? (::t-service/validate saved-service)))) ;; in validation
        _ (is (nil? (::t-service/published saved-service))) ;; not published
        _ (is (nil? (::t-service/re-edit saved-service)))   ;; not in re-edit

        ;; Generated services are not published, publish this one
        _ (http-post (:user-id-admin @ote.test/user-db-ids-atom) "admin/publish-service" {:id service-id})
        ;; And fetch it
        saved-service (:transit (http-get "normaluser" (str "transport-service/" service-id)))
        ;; Ensure that service is in published state
        _ (is (not (nil? (::t-service/published saved-service)))) ;; is published
        _ (is (nil? (::t-service/validate saved-service)))  ;; not in validation
        _ (is (nil? (::t-service/re-edit saved-service)))   ;; not in re-edit

        ;; Create package with received interface-id
        interfaces (::t-service/external-interfaces saved-service)
        packages (doall (mapv
                          (fn [interface]
                            (create-new-package db operator-id service-id (::t-service/id interface)))
                          interfaces))]
    ;; Saved ok
    (is (pos? service-id))

    ;; Fetch and update
    (let [;; Service must be changed to validate state to make changes to interfaces
          v-service (assoc saved-service ::t-service/validate? (time-coerce/to-sql-date (time/now)))
          ;; Edit interface -> if only url changes and id remains the same, everything should be as before
          ;; That is why we "delete" and "create new interface" by changing the interface id to nil
          v-service (-> v-service
                        (update-in [::t-service/external-interfaces 0] dissoc ::t-service/id)
                        (assoc-in [::t-service/external-interfaces 0 ::t-service/external-interface ::t-service/url] "first-change.com"))
          v-service (:transit (http-post (:user-id-normal @ote.test/user-db-ids-atom) "transport-service" v-service))
          v-service (:transit (http-get "normaluser" (str "transport-service/" (::t-service/id v-service))))
          ;; Ensure that service is not in published state to make additional changes to it
          _ (is (not (nil? (::t-service/validate v-service)))) ;;in validation
          _ (is (nil? (::t-service/published v-service)))   ;; not published
          _ (is (nil? (::t-service/re-edit v-service)))     ;;not in re-edit
          _ (is (not (nil? (::t-service/parent-id v-service)))) ;;has parent-id
          parent-id (::t-service/parent-id v-service)

          ;; publish service again
          _ (http-post (:user-id-admin @ote.test/user-db-ids-atom) "admin/publish-service" {:id (::t-service/id v-service)})
          ;; fetch original service using parent-id
          p-service (:transit (http-get "normaluser" (str "transport-service/" parent-id)))

          ;; Ensure that service is in published state
          _ (is (not (nil? (::t-service/published p-service))))
          _ (is (nil? (::t-service/validate p-service)))    ;; not in validate
          _ (is (nil? (::t-service/re-edit p-service)))     ;; not in re-edit
          _ (is (nil? (::t-service/parent-id p-service)))   ;; is not child

          ;; Get possibly changed packages
          possibly-changed-packages (:transit
                                      (http-get (:user-id-admin @ote.test/user-db-ids-atom) (str "admin/service-gtfs-packages/" parent-id)))]
      ;; interface-id should be changed
      (is (not= (:gtfs/external-interface-description-id (first packages)) (:gtfs/external-interface-description-id (first possibly-changed-packages))))
      (is (= true (:gtfs/interface-deleted? (first possibly-changed-packages)))))

    ;; Delete
    (let [delete-response (http-post (:user-id-normal @ote.test/user-db-ids-atom)
                                     "transport-service/delete"
                                     {:id service-id})]
      (is (= (:transit delete-response) service-id)))

    ;; Try to fetch now and it does not exist
    (is (thrown-with-msg?
          clojure.lang.ExceptionInfo #"status 404"
          (http-get "normaluser" (str "transport-service/" service-id))))))
