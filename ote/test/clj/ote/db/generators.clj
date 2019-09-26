(ns ote.db.generators
  "Utilities for generating data in tests"
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as sgen]
            [clojure.spec.test.alpha :as stest]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transit :as transit]
            [ote.db.transport-service :as t-service]
            [ote.db.places :as places]
            [ote.db.common :as common]
            [ote.time :as time]
            [clojure.string :as str]))

(defn load-edn [path]
  (-> (slurp path)
      read-string))

(defn string-of-max-length [len generator]
  (gen/fmap #(if (> (count %) len)
               (subs % 0 len)
               %)
            generator))

(def gen-naughty-string
  (let [strings (load-edn "test/resources/naughty_strings.edn")]
    (gen/elements strings)))

(def gen-name gen/string-alphanumeric)

(def gen-text gen/string-alphanumeric)

(def gen-us-letter (gen/elements "ABCDEFGHIJKLMNOPQRSTUVWXYZ"))

(defn string-of-length [min-length max-length]
  (gen/let [len (gen/choose min-length max-length)]
    (apply str (take len (repeatedly #(gen/generate gen/char-alpha-numeric))))))

(defn word-of-length [min-length max-length]
  (gen/let [len (gen/choose min-length max-length)]
    (apply str (take len (repeatedly #(gen/generate gen-us-letter))))))

(def gen-language
  (gen/let [one gen-us-letter
            two gen-us-letter]
    (str one two)))

(def gen-localized-text
  (gen/hash-map
   ::t-service/lang gen-language
   ::t-service/text gen/string-alphanumeric))

(def gen-localized-text-array
  (gen/vector gen-localized-text 1 4))

(def gen-time
  (gen/let [hour (gen/scale #(* 10 %) (s/gen (s/int-in 0 23)))
            minute (gen/scale #(* 10 %) (s/gen (s/int-in 0 59)))]
    (time/->Time hour minute 0)))

(def gen-service-hours
  (gen/hash-map
   ::t-service/week-days (gen/vector (gen/elements (drop 1 t-service/days)) 1 7) ;; Drop :ALL at the beginning of the vector
   ::t-service/from gen-time
   ::t-service/to gen-time
   ::t-service/description gen-localized-text-array))

(def gen-service-hours-array
  (gen/vector gen-service-hours 0 4))

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

(def price-units ["km" "mile" "trip" "hour" "day" "week"])
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
  (gen/vector gen-price-class 0 5))

(def gen-postal-code
  (gen/let [digits (gen/vector (gen/elements "0123456789") 10)]
    (str/join digits)))

(def gen-address
  (gen/hash-map
   ::common/street (string-of-max-length 100 (s/gen ::common/street))
   ::common/postal_code gen-postal-code
   ::common/post_office (s/gen ::common/post_office)))

(def gen-payment-methods
  (gen/vector (s/gen ::t-service/payment_method) 1 7))

(def gen-interval
  (gen/let [amount (gen/choose 0 200)
            unit (gen/elements [:minutes :hours :days])]
    (time/interval amount unit)))

(def gen-accessibility-facility-array
  (gen/vector (s/gen ::t-service/accessibility_facility) 0 10))

(def gen-service-exception
  (gen/hash-map
   ::t-service/from-date (s/gen ::t-service/from-date)
   ::t-service/to-date (s/gen ::t-service/to-date)
   ::t-service/description gen-localized-text-array))

(def gen-service-exceptions-array
  (gen/vector gen-service-exception 0 10))

(def gen-parking-capacity
  (gen/hash-map
   ::t-service/parking-facility (s/gen ::t-service/parking_facility)
   ::t-service/capacity (gen/choose 0 10000)))

(def gen-parking-capacity-array
  (gen/vector gen-parking-capacity 0 10))

(def gen-description gen-localized-text-array)

(def gen-effective-date (s/gen ::transit/effective-date))

(def gen-available-from (s/gen ::t-service/available-from))

(def gen-available-to (s/gen ::t-service/available-to))

(def gen-rental-price-group
  (gen/vector (gen/hash-map
               ::t-service/unit (gen/elements price-units)
               ::t-service/price-per-unit (gen/double*
                                           {:min 0.0 :max 100000.0
                                            :infinite? false
                                            :NaN? false}))
              0 5))

(def gen-vehicle-class
  (gen/hash-map
   ::t-service/vehicle-type (string-of-max-length 20 gen/string-alphanumeric)
   ::t-service/license-required (string-of-max-length 10 gen/string-alphanumeric)
   ::t-service/minimum-age (gen/choose 0 200)
   ::t-service/price-classes gen-rental-price-group))

(def gen-vehicle-class-array
  (gen/vector gen-vehicle-class 0 5))

(def gen-email
  (gen/let [user (string-of-length 1 15)
            domain (string-of-length 1 15)
            domain-end (word-of-length 2 3)]
    (str user "@" domain "." domain-end)))

(def gen-external-interfaces-class
  (gen/hash-map
   ::t-service/data-content (gen/vector (gen/elements t-service/interface-data-contents) 1 11)
   ::t-service/external-interface (gen/hash-map
                                   ::t-service/url gen-url
                                   ::t-service/description gen-localized-text-array)
   ::t-service/format (gen/vector (string-of-length 1 20) 1 5)
   ::t-service/license (string-of-max-length 20 gen/string-alphanumeric) ))

(def gen-external-interfaces-array
  (gen/vector gen-external-interfaces-class 0 5))

(def gen-operation-area
  (gen/frequency
   [[2 (gen/return {::places/id "finnish-municipality-564"
                    ::places/namefin "Oulu"
                    ::places/type "finnish-municipality"
                    ::places/primary? true})]
    [1 (gen/return {::places/type "drawn"
                    ::places/namefin "Toriportti"
                    :geojson "{\"type\":\"Point\",\"coordinates\":[25.468116,65.012489]}"
                    ::places/primary? true})]]))

(def gen-additional-services
  (gen/hash-map
   ::t-service/additional-service-type (gen/elements t-service/additional-services)
   ::t-service/additional-service-price (gen/hash-map ::t-service/price-per-unit (gen/choose 1 200)
                                                      ::t-service/unit (string-of-length 1 10))))

(def gen-additional-services-array
  (gen/vector gen-additional-services 0 5))

(def gen-pick-up-locations
  (gen/hash-map
   ::t-service/pick-up-name (string-of-length 5 15)
   ::t-service/pick-up-type (gen/elements t-service/pick-up-types)
   ::t-service/pick-up-address gen-address
   ::t-service/service-hours (gen/vector gen-service-hours 0 5)
   ::t-service/service-exceptions (gen/vector gen-service-exception 0 5)))

(def gen-pick-up-locations-array
  (gen/vector gen-pick-up-locations 0 5))

(def gen-advance-reservation
  (gen/elements t-service/advance-reservation))
