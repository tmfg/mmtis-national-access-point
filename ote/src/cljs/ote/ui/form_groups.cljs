(ns ote.ui.form-groups
  "Contains reusable form elements."
  (:require [ote.ui.form :as form]
            [ote.domain.liikkumispalvelu :as t]))


(defn address
  "Creates a form group for address that creates three form elements street, post-office and post-code."
  [label address-field]
  (form/group label
              {:name ::t/street
               :type :string
               :read (comp ::t/street address-field)
               :write (fn [data street]
                        (assoc-in data [address-field ::t/street] street))}

              {:name ::t/post-office
               :type :string
               :read (comp ::t/post_office address-field)
               :write (fn [data post-office]
                        (assoc-in data [address-field ::t/post_office] post-office))}

              {:name ::t/post-code
               :type :string
               :read (comp ::t/post_code address-field)
               :write (fn [data post-code]
                        (assoc-in data [address-field ::t/post-code] post-code))}))