(ns ote.services.login-test
  (:require [ote.services.login :as sut]
            [clojure.test :as t]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]
            [clojure.string :as str])
  (:import (java.util Base64)))


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
