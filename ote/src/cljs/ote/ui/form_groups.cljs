(ns ote.ui.form-groups
  "Contains reusable form elements."
  (:require [ote.ui.form :as form]
            [ote.db.common :as common]
            [ote.db.transport-service :as t-service]
            [ote.ui.buttons :as buttons]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]
            [ote.localization :refer [tr tr-key]]))


(defn address
  "Creates a form group for address that creates three form elements street, post-office and postal-code."
  [label address-field]
  (form/group
   {:label label
    :layout :row
    :columns 3}
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
  (form/group
    {:label label
    :layout :row
    :columns 3}
    {:class "set-bottom"
    :name   ::t-service/url
    :type   :string
    :read   (comp ::t-service/url service-url-field)
    :write  (fn [data url]
             (assoc-in data [service-url-field ::t-service/url] url))}
    {:name ::t-service/description
    :type  :localized-text
    :rows  1 :rows-max 3
    :read  (comp ::t-service/description service-url-field)
    :write (fn [data desc]
             (assoc-in data [service-url-field ::t-service/description] desc))}))

(defn external-interfaces
  "Creates a form group for external services."
  []
  (form/group
   {:label  (tr [:field-labels :transport-service-common ::t-service/external-interfaces])
    :columns 3}

   (form/info (tr [:form-help :external-interfaces]))
   {:name ::t-service/external-interfaces
    :type :table
    :table-fields [{:name ::t-service/external-service-description :type :localized-text :width "40%"
                    :read (comp ::t-service/description ::t-service/external-interface)
                    :write #(assoc-in %1 [::t-service/external-interface ::t-service/description] %2)}
                   {:name ::t-service/external-service-url :type :string :width "40%"
                    :read (comp ::t-service/url ::t-service/external-interface)
                    :write #(assoc-in %1 [::t-service/external-interface ::t-service/url] %2)}
                   {:name ::t-service/format :type :string :width "20%"}]
    :delete? true
    :add-label (tr [:buttons :add-external-interface])}))
