(ns ote.views.transport-operator
  "Olennaisten tietojen lomakenäkymä"
  (:require [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.ui.buttons :as buttons]
            [ote.app.controller.transport-operator :as to]
            [ote.db.transport-operator :as to-definitions]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]))

(defn operator [e! status]
  [:span
   [:div
    [:h3 "Organisaation tiedot"]]  ;;FIXME: translate
   [form/form
    {:name->label (tr-key [:field-labels])
     :update! #(e! (to/->EditTransportOperatorState %))
     :name #(tr [:olennaiset-tiedot :otsikot %])
     :footer-fn (fn [data]
                  [buttons/save {:on-click #(e! (to/->SaveTransportOperator))
                                   :disabled (form/disable-save? data)}
                   (tr [:buttons :save])])}

    [(form/group
      {:label (tr [:common-texts :title-operator-basic-details])
       :columns 1}
      {:name ::to-definitions/name
       :type :string
       :validate [[:non-empty "Anna nimi"]]}  ;;FIXME: translate

      {:name ::to-definitions/business-id
       :type :string
       :validate [[:business-id]]}

      {:name ::common/street
       :type :string
       :read (comp ::common/street ::to-definitions/visiting-address)
       :write (fn [data street]
                (assoc-in data [::to-definitions/visiting-address ::common/street] street))}

      {:name ::common/postal_code
       :type :string
       :read (comp ::common/postal_code ::to-definitions/visiting-address)
       :write (fn [data postal-code]
                (assoc-in data [::to-definitions/visiting-address ::common/postal_code] postal-code))}

      {:name :ote.db.common/post_office
       :type :string
       :read (comp :ote.db.common/post_office :ote.db.transport-operator/visiting-address)
       :write (fn [data post-office]
                (assoc-in data [:ote.db.transport-operator/visiting-address :ote.db.common/post_office] post-office))}

      {:name ::to-definitions/homepage
       :type :string})

     (form/group
      {:label "Yhteystavat" ;;FIXME: translate
       :columns 1}

      {:name ::to-definitions/phone :type :string}
      {:name ::to-definitions/gsm :type :string}
      {:name ::to-definitions/email :type :string}
      {:name ::to-definitions/facebook :type :string}
      {:name ::to-definitions/twitter :type :string}
      {:name ::to-definitions/instant-message :type :string})]

    status]
   ])
