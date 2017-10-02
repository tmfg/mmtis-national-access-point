(ns ote.ui.form-groups
  "Contains reusable form elements."
  (:require [ote.ui.form :as form]
            [ote.db.common :as common]
            [ote.db.transport-service :as transport-service]))


(defn address
  "Creates a form group for address that creates three form elements street, post-office and postal-code."
  [label address-field]
  (form/group label
              {:name ::common/street
               :type :string
               :read (comp ::common/street address-field)
               :write (fn [data street]
                        (assoc-in data [address-field ::common/street] street))}

              {:name ::common/post-office
               :type :string
               :read (comp ::common/postal_office address-field)
               :write (fn [data post-office]
                        (assoc-in data [address-field ::common/post-office] post-office))}

              {:name ::common/postal-code
               :type :string
               :read (comp ::common/postal_code address-field)
               :write (fn [data postal-code]
                        (assoc-in data [address-field ::common/postal-code] postal-code))}))


(defn service-url
  "Creates a form group for service url hat creates two form elements url and localized text area"
  [label service-url-field]
  (form/group label
              {:class "set-bottom"
               :name ::transport-service/url
               :type :string
               :read (comp ::transport-service/url service-url-field)
               :write (fn [data url]
                        (assoc-in data [service-url-field ::transport-service/url] url))}
              {
               :name ::transport-service/description
               :type  :localized-text
               :rows   3
               :read (comp ::transport-service/description service-url-field)
               :write (fn [data desc]
                        (assoc-in data [service-url-field ::transport-service/description] desc))}
              ))

(defn price-class
  "Creates a form group for price-class that creates four form elements name, price-per-unit, unit and currency."
  [label price-class]
  (form/group label
              {:name ::transport-service/name
               :type :string
               :read (comp ::transport-service/name price-class)
               :write (fn [data name]
                        (assoc-in data [price-class ::transport-service/name] name))}

              {:name ::transport-service/price-per-unit
              :type :string
              :read (comp ::transport-service/price-per-unit price-class)
              :write (fn [data price-per-unit]
                       (assoc-in data [price-class ::transport-service/price-per-unit] price-per-unit))}

              {:name ::transport-service/unit
               :type :string
               :read (comp ::transport-service/unit price-class)
               :write (fn [data unit]
                        (assoc-in data [price-class ::transport-service/unit] unit))}

              {:name ::transport-service/currency
               :type :string
               :read (comp ::transport-service/currency price-class)
               :write (fn [data currency]
                        (assoc-in data [price-class ::transport-service/currency] currency))}))