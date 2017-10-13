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
               :read (comp ::common/post_office address-field)
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