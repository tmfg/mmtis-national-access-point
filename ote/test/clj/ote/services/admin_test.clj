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
   :admin (component/using
           (admin/->Admin (:nap nil))
           [:http :db])
   :transport (component/using
                (transport-service/->Transport
                  (:nap nil))
                [:http :db])))

(deftest user-listing-allowed-for-admin-only
  ;; Unauthorized is returned if non-admin tries to call service
  (is (thrown-with-msg?
       clojure.lang.ExceptionInfo #"status 403"
       (http-post "normaluser" "admin/users" "napoteadmin")))

  ;; 1 user is found with the admin email
  (is (= 1 (count (:transit (http-post "admin" "admin/users" "admin@napoteadmin123.com"))))))


(deftest user-is-found-with-partial-name
  (let [users (:transit (http-post "admin" "admin/users" "Userso"))]
    (is (= 1 (count users)))
    (is (= "User Userson" (:name (first users))))))

(deftest user-is-found-with-partial-email
  (let [users (:transit (http-post "admin" "admin/users" "napoteadmin123"))]
    (is (= 1 (count users)))
    (is (= "admin@napoteadmin123.com" (:email (first users))))))

(deftest delete-service-made-by-normaluser
  (let [generated-service (gen/generate s-generators/gen-transport-service)
        modified-service (assoc generated-service ::t-service/transport-operator-id 2)
        response (http-post "normaluser" "transport-service" modified-service)
        parsed-response (:transit response)
        id (::t-service/id parsed-response)
        deleted-response  (http-post "admin" (str "admin/transport-service/delete") {:id id})
        auditlog (last (fetch
                   (:db ote.test/*ote*)
                   ::auditlog/auditlog
                   #{::auditlog/id ::auditlog/event-attributes ::auditlog/event-type}
                   {}))]
    (is (= true (not (nil? deleted-response))))
    (is (= 200 (:status deleted-response)))
    (is (= :delete-service (get auditlog ::auditlog/event-type)))))
