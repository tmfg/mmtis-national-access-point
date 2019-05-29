(ns ote.services.admin-test
  (:require [ote.services.admin :as admin]
            [ote.db.transport-service :as t-service]
            [ote.db.auditlog :as auditlog]
            [specql.core :refer [fetch update! insert! upsert! delete!] :as specql]
            [clojure.test :as t :refer [deftest testing is]]
            [ote.test :refer [system-fixture http-get http-post http-delete]]
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

(deftest test-user-list-normaluser
  (let [result (try (http-get "normaluser" "admin/user?type=any&search=napoteadmin")
                    (catch clojure.lang.ExceptionInfo e     ;; sadly clj-http wants to communicate status as exceptions
                      (-> e ex-data)))]
    (is (= 403 (:status result))
        "Ensure normal user not allowed to query users")))

(deftest test-user-list-all
  (let [users (:transit (http-get "admin" "admin/user"))]
    (is (= 2 (count users)))))

(deftest test-user-list-email
  (let [result (try (http-get "admin" "admin/user?type=any&search=admin@napoteadmin123.com")
                    (catch clojure.lang.ExceptionInfo e     ;; sadly clj-http wants to communicate status as exceptions
                      (-> e ex-data)))]
    (is (= 200 (:status result))
        "Ensure admin user allowed to query users")
    (is (= 1 (count (:transit result)))
        "Ensure admin user gets a list of users")))

(deftest test-user-list-partial-name
  (let [resp (try (http-get "admin" "admin/user?type=any&search=userso")
                    (catch clojure.lang.ExceptionInfo e
                      (-> e ex-data)))
        result (:transit resp)]
    (is (= 200 (:status resp))
        "Ensure user found by partial username")
    (is (= "User Userson" (:name (first result))))
    (is (= 1 (count result)))))

(deftest test-user-list-partial-email
  (let [result (http-get "admin" "admin/user?type=any&search=napoteadmin123")
        transit (:transit result)]
    (is (= 200 (:status result))
        "Ensure http status code is correct")
    (is (= 1 (count transit))
        "Ensure response result count is correct for user search using a partial email")
    (is (= "admin@napoteadmin123.com" (:email (first transit)))
        "Ensure response email is correct for user search using a partial email")))

(deftest test-user-not-found-name
  (let [result (try (http-get "admin" "admin/user?type=any&search=xxxxxxxxyyyyyyyyzzzzzzzzääääääääööööööööåååååååå")
                    (catch clojure.lang.ExceptionInfo e     ;; sadly clj-http wants to communicate status as exceptions
                      (-> e ex-data)))]
    (is (= 404 (:status result))
        "Ensure http status code is correct")
    (is (= 0 (count (:transit result)))
        "Ensure no data is returned")))

(deftest test-user-delete-not-found
  (let [result (try (http-delete "admin" "admin/user/000-000-000-000")
                    (catch clojure.lang.ExceptionInfo e     ;; sadly clj-http wants to communicate status as exceptions
                      (-> e ex-data)))]
    (is (= 404 (:status result))
        "Ensure http status code is correct")))

(deftest test-user-memberships-not-found-normaluser
  (let [result (try (http-get "normaluser" "admin/member?user=0")
                    (catch clojure.lang.ExceptionInfo e     ;; sadly clj-http wants to communicate status as exceptions
                      (-> e ex-data)))]
    (is (= 403 (:status result))
        "Ensure normal user not allowed to query user memberships")
    (is (= 0 (count (:transit result)))
        "Ensure there is no content")))

(deftest test-user-memberships-not-found-admin
  (let [result (try (http-get "admin" "admin/member?userid=123")
                    (catch clojure.lang.ExceptionInfo e     ;; sadly clj-http wants to communicate status as exceptions
                      (-> e ex-data)))]
    (is (= 200 (:status result))
        "Ensure admin allowed to query user memberships")
    (is (= 0 (count (:transit result)))
        "Ensure no results on bad userid")))

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
