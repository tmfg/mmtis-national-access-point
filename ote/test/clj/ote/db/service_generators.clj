(ns ote.db.service-generators
  "Helper functions to generate complerte transport-services"
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as sgen]
            [clojure.spec.test.alpha :as stest]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]
            [ote.db.common :as common]
            [ote.time :as time]
            [clojure.string :as str]
            [ote.db.generators :as generators]))

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
    ::t-service/service-hours-info generators/gen-localized-text-array))

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
   ::t-service/service-hours generators/gen-service-hours-array))

(def gen-transport-service-common
  (gen/hash-map
   ::t-service/name generators/gen-name
   ::t-service/transport-operator-id (gen/return 1)
   ::t-service/contact-address generators/gen-address
   ::t-service/contact-phone (s/gen ::t-service/contact-phone)
   ::t-service/brokerage? (s/gen boolean?)))

(defn service-type-generator [service-type]
  (gen/let [common gen-transport-service-common
            type-specific (case service-type
                            :passenger-transportation gen-passenger-transportation
                            :parking gen-parking)
            sub-type (if (= :passenger-transportation service-type)
                       (gen/elements t-service/passenger-transportation-sub-types)
                       (gen/return service-type))]
    (assoc common
           ::t-service/type service-type
           ::t-service/sub-type sub-type
           (case service-type
             :passenger-transportation ::t-service/passenger-transportation
             :parking ::t-service/parking) type-specific)))

(def gen-transport-service
  (gen/frequency
   [[50 (service-type-generator :passenger-transportation)]
    [50 (service-type-generator :parking)]]))
