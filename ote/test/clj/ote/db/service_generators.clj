(ns ote.db.service-generators
  "Helper functions to generate complerte transport-services"
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as sgen]
            [clojure.spec.test.alpha :as stest]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transit :as transit]
            [ote.db.transport-service :as t-service]
            [ote.db.common :as common]
            [ote.time :as time]
            [clojure.string :as str]
            [ote.db.generators :as generators]))

(def gen-effective-dates
  (gen/hash-map
    ::transit/effective-date generators/gen-effective-date
    ::transit/effective-date-description generators/gen-naughty-string))

(def gen-pre-notice
  (gen/hash-map
    ::t-operator/id (gen/return 1)
    ::transit/pre-notice-type (gen/return [:new])
    ::transit/effective-dates (gen/vector gen-effective-dates 5)
    ::transit/route-description generators/gen-naughty-string
    ::transit/url generators/gen-url
    ::transit/regions (gen/return ["01"])
    ::transit/pre-notice-state (gen/return :draft)
    ::transit/other-type-description generators/gen-naughty-string))

(def gen-terminal_information
  (gen/hash-map
    ::t-service/service-hours generators/gen-service-hours-array
    ::t-service/indoor-map generators/gen-service-link))

(def gen-terminal-service
  (gen/hash-map
    ::t-service/transport-operator-id (gen/return 1)
    ::t-service/type (gen/return :terminal)
    ::t-service/sub-type (gen/return :terminal)
    ::t-service/terminal gen-terminal_information
    ::t-service/contact-address generators/gen-address
    ::t-service/contact-phone (s/gen ::t-service/contact-phone)))

(def gen-passenger-transportation
  (gen/hash-map
    ::t-service/guaranteed-accessibility-tool (s/gen ::t-service/accessibility-tool)
    ::t-service/guaranteed-accessibility-description generators/gen-localized-text-array
    ::t-service/guaranteed-transportable-aid (s/gen ::t-service/guaranteed-transportable-aid)
    ::t-service/guaranteed-info-service-accessibility (s/gen ::t-service/guaranteed-info-service-accessibility)
    ::t-service/guaranteed-vehicle-accessibility (s/gen ::t-service/guaranteed-vehicle-accessibility)
    ::t-service/limited-accessibility-tool (s/gen ::t-service/accessibility-tool)
    ::t-service/limited-accessibility-description generators/gen-localized-text-array
    ::t-service/limited-transportable-aid (s/gen ::t-service/limited-transportable-aid)
    ::t-service/limited-info-service-accessibility (s/gen ::t-service/limited-info-service-accessibility)
    ::t-service/limited-vehicle-accessibility (s/gen ::t-service/limited-vehicle-accessibility)
    ::t-service/additional-services (s/gen ::t-service/additional-services)
    ::t-service/price-classes generators/gen-price-class-array
    ::t-service/booking-service generators/gen-service-link
    ::t-service/payment-methods (s/gen ::t-service/payment-methods)
    ::t-service/real-time-information generators/gen-service-link
    ::t-service/service-hours generators/gen-service-hours-array
    ::t-service/service-exceptions (gen/return []) ;; FIXME: generate these
    ::t-service/luggage-restrictions generators/gen-localized-text-array
    ::t-service/pricing generators/gen-service-link
    ::t-service/payment-method-description generators/gen-localized-text-array
    ::t-service/service-hours-info generators/gen-localized-text-array
    ::t-service/advance-reservation generators/gen-advance-reservation))

(def gen-parking
  (gen/hash-map
   ::t-service/information-service-accessibility (s/gen ::t-service/information-service-accessibility)
   ::t-service/charging-points generators/gen-localized-text-array
   ::t-service/price-classes generators/gen-price-class-array
   ::t-service/booking-service generators/gen-service-link
   ::t-service/office-hours-exceptions generators/gen-service-exceptions-array
   ::t-service/payment-methods generators/gen-payment-methods
   ::t-service/real-time-information generators/gen-service-link
   ::t-service/maximum-stay generators/gen-interval
   ::t-service/accessibility-description generators/gen-localized-text-array
   ::t-service/office-hours generators/gen-service-hours-array
   ::t-service/accessibility generators/gen-accessibility-facility-array
   ::t-service/additional-service-links (gen/vector generators/gen-service-link 0 7)
   ::t-service/payment-method-description generators/gen-localized-text-array
   ::t-service/service-exceptions generators/gen-service-exceptions-array
   ::t-service/accessibility-info-url generators/gen-url
   ::t-service/parking-capacities generators/gen-parking-capacity-array
   ::t-service/service-hours generators/gen-service-hours-array
   ::t-service/pricing generators/gen-service-link
   ::t-service/advance-reservation generators/gen-advance-reservation))

(def gen-rentals
  (gen/hash-map
   ::t-service/vehicle-classes generators/gen-vehicle-class-array
   ::t-service/vehicle-price-url generators/gen-url
   ::t-service/booking-service generators/gen-service-link
   ::t-service/luggage-restrictions generators/gen-localized-text-array
   ::t-service/payment-methods generators/gen-payment-methods
   ::t-service/guaranteed-vehicle-accessibility (s/gen ::t-service/guaranteed-vehicle-accessibility)
   ::t-service/limited-vehicle-accessibility (s/gen ::t-service/limited-vehicle-accessibility)
   ::t-service/guaranteed-transportable-aid (s/gen ::t-service/guaranteed-transportable-aid)
   ::t-service/limited-transportable-aid (s/gen ::t-service/limited-transportable-aid)
   ::t-service/guaranteed-accessibility-description generators/gen-localized-text-array
   ::t-service/limited-accessibility-description generators/gen-localized-text-array
   ::t-service/accessibility-info-url generators/gen-url
   ::t-service/rental-additional-services generators/gen-additional-services-array
   ::t-service/usage-area (generators/word-of-length 5 50)
   ::t-service/advance-reservation generators/gen-advance-reservation
   ::t-service/pick-up-locations generators/gen-pick-up-locations-array
   ::t-service/real-time-information generators/gen-service-link))

(def gen-transport-service-common
  (gen/hash-map
   ::t-service/name generators/gen-name
   ::t-service/description generators/gen-description
   ::t-service/available-from generators/gen-available-from
   ::t-service/available-to generators/gen-available-to
   ::t-service/transport-operator-id (gen/return 1)
   ::t-service/contact-address generators/gen-address
   ::t-service/contact-email generators/gen-email
   ::t-service/contact-phone (s/gen ::t-service/contact-phone)
   ::t-service/homepage generators/gen-url
   ::t-service/brokerage? (s/gen boolean?)
   ::t-service/operation-area (gen/vector generators/gen-operation-area 0 2)
   ::t-service/external-interfaces generators/gen-external-interfaces-array
   ::t-service/notice-external-interfaces? (s/gen boolean?)))

(defn service-type-generator [service-type]
  (gen/let [common gen-transport-service-common
            type-specific (case service-type
                            :passenger-transportation gen-passenger-transportation
                            :parking gen-parking
                            :rentals gen-rentals)
            sub-type (if (= :passenger-transportation service-type)
                       (gen/elements t-service/passenger-transportation-sub-types)
                       (gen/return service-type))]
    (assoc common
           ::t-service/type service-type
           ::t-service/sub-type sub-type
           (case service-type
             :passenger-transportation ::t-service/passenger-transportation
             :parking ::t-service/parking
             :rentals ::t-service/rentals) type-specific)))

(defn service-sub-type-generator [service-sub-type]
  (gen/let [common gen-transport-service-common
            type (gen/return :passenger-transportation)]
     (-> common
           (assoc ::t-service/type type)
           (assoc ::t-service/sub-type service-sub-type)
           (assoc ::t-service/passenger-transportation (gen/generate gen-passenger-transportation)))))

(def gen-transport-service
  (gen/frequency
   [[3 (service-type-generator :passenger-transportation)]
    [3 (service-type-generator :parking)]
    [3 (service-type-generator :rentals)]]))
