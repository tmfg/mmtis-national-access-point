(ns ote.db.generators
  "Utilities for generating data in tests"
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as sgen]
            [clojure.spec.test.alpha :as stest]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]
            [ote.db.common :as common]
            [ote.time :as time]
            [clojure.string :as str]))

(defn string-of-max-length [len generator]
  (gen/fmap #(if (> (count %) len)
               (subs % 0 len)
               %)
            generator))

(def gen-us-letter (gen/elements "ABCDEFGHIJKLMNOPQRSTUVWXYZ"))

(def gen-language
  (gen/let [one gen-us-letter
            two gen-us-letter]
    (str one two)))

(def gen-localized-text
  (gen/hash-map
   ::t-service/lang gen-language
   ::t-service/text gen/string-alphanumeric))

(def gen-localized-text-array
  (gen/vector gen-localized-text))

(def gen-time
  (gen/let [hour (gen/scale #(* 10 %) (s/gen (s/int-in 0 23)))
            minute (gen/scale #(* 10 %) (s/gen (s/int-in 0 59)))]
    (time/->Time hour minute nil)))

(def gen-service-hours
  (gen/hash-map
   ::t-service/week-days (gen/set (gen/elements t-service/days))
   ::t-service/from gen-time
   ::t-service/to gen-time))

(def gen-service-hours-array
  (gen/vector gen-service-hours))

(def host-parts ["service" "my" "www" "transport" "ride" "car" "share"
                 "taxi" "online" "fast" "courier"])

(def gen-url
  (gen/let [scheme (gen/elements ["http" "https" "ftp" "gopher"])
            host (gen/vector (gen/elements host-parts) 1 5)]
    (str scheme "://" (str/join "-" host) ".example.com")))

(def gen-service-link
  (gen/hash-map
   ::t-service/url gen-url
   ::t-service/description gen-localized-text-array))

(def price-units ["km" "mile" "trip" "day" "week"])
(def price-currency ["EUR" "SEK" "USD" "RMB"])

(def gen-price-class
  (gen/hash-map
   ::t-service/unit (gen/elements price-units)
   ::t-service/price-per-unit (gen/double* {:min 0.0 :max 100000.0
                                            :infinite? false
                                            :NaN? false})
   ::t-service/currency (gen/elements price-currency)
   ::t-service/name (string-of-max-length 100  gen/string-alphanumeric)))

(def gen-price-class-array
  (gen/vector gen-price-class))

(def gen-postal-code
  (gen/let [digits (gen/vector (gen/elements "0123456789") 5)]
    (str/join digits)))

(def gen-address
  (gen/hash-map
   ::common/street (string-of-max-length 100 (s/gen ::common/street))
   ::common/postal_code gen-postal-code
   ::common/post_office (s/gen ::common/post_office)))
