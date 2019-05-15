(ns ote.services.admin-test
  (:require [ote.services.admin :as admin]
            [ote.db.transport-service :as t-service]
            [ote.db.auditlog :as auditlog]
            [specql.core :refer [fetch update! insert! upsert! delete!] :as specql]
            [clojure.test :as t :refer [deftest testing is]]
            [ote.test :refer [system-fixture http-get http-post]]
            [clojure.test.check.generators :as gen]
            [com.stuartsierra.component :as component]
            [ote.db.generators :as generators]
            [ote.db.service-generators :as s-generators]
            [ote.services.transport :as transport-service]))

(t/use-fixtures :each
                (system-fixture
                  :admin (component/using (admin/->Admin (:nap nil))
                                          [:http :db])
                  :transport (component/using
                               (transport-service/->Transport
                                 (:nap nil))
                               [:http :db])))

(deftest test-user-listing-admin
  (let [result (try (http-post "admin" "admin/users" "admin@napoteadmin123.com")
                    (catch clojure.lang.ExceptionInfo e     ;; sadly clj-http wants to communicate status as exceptions
                      (-> e ex-data)))]
    (is (= 200 (:status result))
        "Ensure admin user allowed to query users")
    (is (= 1 (count (:transit result)))
        "Ensure admin user gets a list of users")))

(deftest test-user-listing-normaluser
  (let [result (try (http-post "normaluser" "admin/users" "napoteadmin")
                    (catch clojure.lang.ExceptionInfo e     ;; sadly clj-http wants to communicate status as exceptions
                      (-> e ex-data)))]
    (is (= 403 (:status result))
        "Ensure normal user not allowed to query users")))

(deftest test-user-search-with-partial-name
  (let [users (:transit (http-post "admin" "admin/users" "Userso"))]
    (is (= 1 (count users)))
    (is (= "User Userson" (:name (first users))))))

(deftest test-user-search-with-partial-email
  (let [result (http-post "admin" "admin/users" "napoteadmin123")
        transit (:transit result)]
    (is (= 200 (:status result))
        "Ensure http status code is correct")
    (is (= 1 (count transit))
        "Ensure response result count is correct for user search using a partial email")
    (is (= "admin@napoteadmin123.com" (:email (first transit)))
        "Ensure response email is correct for user search using a partial email")))

(deftest test-user-not-found-name
  (let [result (try (http-post "admin" "admin/users" "xxxxxxxxyyyyyyyyzzzzzzzz")
                    (catch clojure.lang.ExceptionInfo e     ;; sadly clj-http wants to communicate status as exceptions
                      (-> e ex-data)))]
    (is (= 404 (:status result))
        "Ensure http status code is correct")
    (is (= 0 (count (:transit result))))))

(deftest test-delete-service-made-by-normaluser
  (let [generated-service (gen/generate s-generators/gen-transport-service)
        modified-service (assoc generated-service ::t-service/transport-operator-id 2)
        response (http-post "normaluser" "transport-service" modified-service)
        parsed-response (:transit response)
        id (::t-service/id parsed-response)
        deleted-response (http-post "admin" (str "admin/transport-service/delete") {:id id})
        auditlog (last (fetch
                         (:db ote.test/*ote*)
                         ::auditlog/auditlog
                         #{::auditlog/id ::auditlog/event-attributes ::auditlog/event-type}
                         {}))]
    (is (= true (not (nil? deleted-response))))
    (is (= 200 (:status deleted-response)))
    (is (= :delete-service (get auditlog ::auditlog/event-type)))))
