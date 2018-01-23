(ns ote.services.admin-test
  (:require [ote.services.admin :as admin]
            [clojure.test :as t :refer [deftest testing is]]
            [ote.test :refer [system-fixture http-get http-post]]
            [com.stuartsierra.component :as component]))

(t/use-fixtures :each
  (system-fixture
   :admin (component/using
           (admin/->Admin (:nap nil))
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
