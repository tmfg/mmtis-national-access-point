(ns ote.db.transport-service
  "Database configurations for Transport Services"
  (:require [clojure.spec.alpha :as s]
            #?(:clj [ote.tietokanta.specql-db :refer [define-tables]])
            [specql.rel :as rel]
            [specql.transform :as xf]
            [specql.impl.registry]
            [ote.db.common]
            [specql.data-types]
            [ote.time]
            [ote.db.modification]
            #?(:clj [specql.impl.composite :as specql-composite]))
  #?(:cljs
     (:require-macros [ote.tietokanta.specql-db :refer [define-tables]])))


(define-tables
  ;; Define ENUMs
  ["week_day" ::day (specql.transform/transform (specql.transform/to-keyword))]

  ["payment_method" ::payment_method (specql.transform/transform (specql.transform/to-keyword))]
  ["transport_provider_type" ::transport_provider_type (specql.transform/transform (specql.transform/to-keyword))]
  ["transport_service_subtype" ::transport_service_subtype (specql.transform/transform (specql.transform/to-keyword))]
  ["accessibility_tool" ::accessibility_tool (specql.transform/transform (specql.transform/to-keyword))]
  ["accessibility_info_facility" ::accessibility_info_facility (specql.transform/transform (specql.transform/to-keyword))]
  ["accessibility_facility" ::accessibility_facility (specql.transform/transform (specql.transform/to-keyword))]
  ["passenger_information_facility" ::passenger_information_facility]
  ["safety_facility" ::safety_facility]
  ["parking_facility" ::parking_facility (specql.transform/transform (specql.transform/to-keyword))]
  ["additional_services" ::additional_services  (specql.transform/transform (specql.transform/to-keyword))]
  ["pick_up_type" ::pick_up_type (specql.transform/transform (specql.transform/to-keyword))]
  ["brokerage_service_type" ::brokerage_service_type]
  ["transportable_aid" ::transportable-aid (specql.transform/transform (specql.transform/to-keyword))]
  ["vehicle_accessibility" ::vehicle-accessibility (specql.transform/transform (specql.transform/to-keyword))]
  ["interface_data_content" ::interface-data-content (specql.transform/transform (specql.transform/to-keyword))]

  ;; UDT tyypit
  ["localized_text" ::localized_text]
  ["service_link" ::service_link]
  ["service_hours" ::service_hours]
  ["service_exception" ::service_exception]
  ["parking_capacity" ::parking_capacity]
  ["price_class" ::price_class]
  ["assistance_notification_requirement" ::assistance-notification-requirement]
  ["assistance_info" ::assistance-info]
  ["terminal_information" ::terminal_information]
  ["passenger_transportation_info" ::passenger_transportation_info]
  ["pick_up_location" ::pick_up_location]
  ["rental_additional_service" ::rental_additional_service]
  ["rental_vehicle" ::rental_vehicle]
  ["rental_provider_information" ::rental_provider_informaton]
  ["parking_provider_information" ::parking_provider_information]
  ["brokerage_service" ::brokerage_service]
  ["brokerage_provider_informaton" ::brokerage_provider_informaton]
  ["company" ::company]

  ;; Tables
  ["external-interface-description" ::external-interface-description]
  ["transport-service" ::transport-service
   ote.db.modification/modification-fields
   {::provider (specql.rel/has-one ::transport-operator-id ::transport-operator ::id)
    ::external-interfaces (specql.rel/has-many ::id
                                               ::external-interface-description
                                               ::transport-service-id)}]
  ["operation_area" ::operation_area]
  ["operation_area_geojson" ::operation_area_geojson]

  ["external_interface_search_result" ::external-interface-search-result]
  ["transport_service_search_result" ::transport-service-search-result
   ote.db.modification/modification-fields]
  ["service_company" ::service-company])

;; Create order for transport_type
(def transport-service-types [:terminal :passenger-transportation :rentals :parking])

;; Create order for transport_type
(def passenger-transportation-sub-types [:taxi :request :other :schedule])

(def interface-data-contents [:route-and-schedule :luggage-restrictions :realtime-interface :booking-interface
                             :accessibility-services :other-services :pricing :service-hours :disruptions
                             :payment-interface :other])

;; Create order for payment_method
(def payment-methods [:cash :debit-card :credit-card :mobilepay :contactless-payment :invoice :other])

(def parking-facilities [:unknown :car-park :park-and-ride-park :motorcycle-park :cycle-park :rental-car-park
                         :coach-park :disabled-park])

;; Create order for additional_services
(def additional-services [:child-seat :animal-transport :other])

;; Create order for accessibility_tool
(def accessibility-tool [:wheelchair :walkingstick :audio-navigator :visual-navigator :passenger-cart
                          :pushchair :umbrella :buggy :other])

(def days [:ALL :MON :TUE :WED :THU :FRI :SAT :SUN])


;; Create order for accessibility-facility
(def accessibility [:lift :escalator :travelator :ramp :stairs :shuttle :narrow-entrance :barrier
                    :pallet-access-low-floor :wheelchair-access-toilet
                    :step-free-access :suitable-for-wheelchairs :tactile-platform-edges :tactile-guiding-strips
                    :other])

;; Create order for accessibility-info-facility
(def information-service-accessibility [:audio-for-hearing-impaired :audio-information :visual-displays
                                        :displays-for-visually-impaired :large-print-timetables :other])

;; Create order for accessibility-facility for parking services
(def parking-accessibility [:lift :ramp :stairs :narrow-entrance :barrier :wheelchair-access-toilet
                            :step-free-access :suitable-for-wheelchairs :tactile-platform-edges :tactile-guiding-strips
                            :other])

(def parking-information-service-accessibility [:audio-for-hearing-impaired :audio-information :visual-displays
                                                :displays-for-visually-impaired :other])

(defn service-key-by-type
  "Returns the service column keyword for the given type enum value."
  [type]
  (case type
    :passenger-transportation ::passenger-transportation
    :terminal ::terminal
    :rentals ::rentals
    :parking ::parking
    :brokerage ::brokerage))

(defn localized-text-for [language localized-text]
  (some #(when (= (::lang %) language) (::text %)) localized-text))

(def transportable-aid
  [:wheelchair :walking-stick :crutches :walker])

(def pick-up-types
  [:pick-up :return :pick-up-return])

(def vehicle-accessibility
  [:low-floor :step-free-access :accessible-vehicle :suitable-for-wheelchairs :suitable-for-stretchers
   :boarding-assistance :assistance-dog-space])

(def rental-vehicle-accessibility
  [:disability-adapted-vehicle])

(def rental-transportable-aid
  [:wheelchair])

(def service-company-soure
  [:FILE :CSV :URL])
