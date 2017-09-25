(ns ote.domain.liikkumispalvelu
  "Liikkumispalvelun tietojen määritys"
  (:require [clojure.spec.alpha :as s]
            #?(:clj [ote.tietokanta.specql-db :refer [define-tables]])
            [specql.rel :as rel]
            [specql.transform :as xf])
  #?(:cljs
     (:require-macros [ote.tietokanta.specql-db :refer [define-tables]])))

(define-tables
  ;; Define ENUMs
  ["week_day" ::week_day (specql.transform/transform (specql.transform/to-keyword))]
  ["payment_method" ::payment_method (specql.transform/transform (specql.transform/to-keyword))]
  ["transport_provider_type" ::transport_provider_type (specql.transform/transform (specql.transform/to-keyword))]
  ["accessibility_tool" ::accessibility_tool (specql.transform/transform (specql.transform/to-keyword))]
  ["accessibility_info_facility" ::accessibility_info_facility]
  ["accessibility_facility" ::accessibility_facility]
  ["mobility_facility" ::mobility_facility]
  ["passenger_information_facility" ::passenger_information_facility]
  ["safety_facility" ::safety_facility]
  ["parking_facility" ::parking_facility (specql.transform/transform (specql.transform/to-keyword))]
  ["additional_rental_services" ::additional_rental_services]
  ["pick_up_type" ::pick_up_type]
  ["brokerage_service_type" ::brokerage_service_type]

  ;; UDT tyypit
  ["address" ::address]
  ["localized_text" ::localized_text]
  ["service_link" ::service_link]
  ["opening_hours" ::opening_hours]
  ["price_class" ::price_class]
  ["terminal_information" ::terminal_information]
  ["operation_area" ::operation_area]
  ["passenger_transportation_info" ::passenger_transportation_info]
  ["pick_up_location" ::pick_up_location]
  ["rental_provider_informaton" ::rental_provider_informaton]
  ["parking_area" ::parking_area]
  ["parking_provider_information" ::parking_provider_information]
  ["brokerage_service" ::brokerage_service]
  ["brokerage_provider_informaton" ::brokerage_provider_informaton]

  ;; Tables
  ["transport-operator" ::transport-operator]
  ["transport-service" ::transport-service
   {::provider (specql.rel/has-one ::transport-operator-id ::transport-operator ::id)}])

;; Create order for transport_type
(def transport-provider-types [:terminal :passenger-transportation :rentals :parking :brokerage])
