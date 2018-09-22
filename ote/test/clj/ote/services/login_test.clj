(ns ote.services.login-test
  (:require [ote.services.login :as sut]
            [clojure.test :as t :refer [deftest is testing]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]
            [clojure.string :as str]
            [ote.test :refer [system-fixture http-post]]
            [com.stuartsierra.component :as component])
  (:import (java.util Base64)))

(t/use-fixtures :each
  (system-fixture
   :login (component/using
           (sut/->LoginService {:shared-secret "test"
                                :digest-algorithm "MD5"})
           [:http :db :email])))

(defspec base64<->hex
  (prop/for-all
   [input-string gen/string]

   (let [base64 (str/replace (String. (.encode (Base64/getEncoder) (.getBytes input-string)))
                             #"\+" ".")]
     (and
      (= base64
         (sut/hex->base64 (sut/base64->hex base64)))))))

(defspec buddy<->passlib
  10
  (prop/for-all
   [input-password (gen/resize 30 (gen/frequency
                                   [[30 gen/string]
                                    [70 gen/string-alphanumeric]]))]
   (let [buddy (sut/encrypt input-password)]
     (= buddy
        (sut/passlib->buddy (sut/buddy->passlib buddy))))))

(deftest password-reset-request-sends-email
  (let [response (http-post "request-password-reset" {:email "admin@napoteadmin123.com"
                                                      :language "en"})
        outbox @ote.test/outbox]
    (is (= :ok (:transit response)))

    (is (= 1 (count outbox)))
    (testing "Email message"
      (let [{:keys [to subject body]} (first outbox)]
        (is (= to "admin@napoteadmin123.com"))
        (is (= subject "NAP - Reset your password"))
        (is (str/includes? body "admin"))
        (is (str/includes? body "#reset-password?key="))))))
