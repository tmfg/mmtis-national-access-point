(ns ote.views.transport-operator
  "Form to edit transport operator information."
  (:require [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.ui.buttons :as buttons]
            [ote.ui.validation :as ui-validation]
            [ote.app.controller.transport-operator :as to]
            [ote.db.transport-operator :as t-operator]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]
            [ote.ui.form-fields :as form-fields]
            [reagent.core :as r]))

(defn- operator-form-groups []
  [(form/group
    {:label (tr [:common-texts :title-operator-basic-details])
     :columns 1}
    {:name ::t-operator/name
     :type :string
     :required? true}

    {:name ::t-operator/business-id
     :type :string
     :validate [[:business-id]]}

    {:name ::common/street
     :type :string
     :read (comp ::common/street ::t-operator/visiting-address)
     :write (fn [data street]
              (assoc-in data [::t-operator/visiting-address ::common/street] street))}

    {:name ::common/postal_code
     :type :string
     :read (comp ::common/postal_code ::t-operator/visiting-address)
     :write (fn [data postal-code]
              (assoc-in data [::t-operator/visiting-address ::common/postal_code] postal-code))}

    {:name :ote.db.common/post_office
     :type :string
     :read (comp :ote.db.common/post_office :ote.db.transport-operator/visiting-address)
     :write (fn [data post-office]
              (assoc-in data [:ote.db.transport-operator/visiting-address :ote.db.common/post_office] post-office))}

    {:name ::t-operator/homepage
       :type :string})

   (form/group
    {:label (tr [:organization-page :contact-types])
     :columns 1}

    {:name ::t-operator/phone :type :string :regex ui-validation/phone-number-regex}
    {:name ::t-operator/gsm :type :string}
    {:name ::t-operator/email :type :string})])

(defn- operator-form-options [e!]
  {:name->label (tr-key [:field-labels])
   :update! #(e! (to/->EditTransportOperatorState %))
   :name #(tr [:olennaiset-tiedot :otsikot %])
   :footer-fn (fn [data]
                [buttons/save {:on-click #(e! (to/->SaveTransportOperator))
                               :disabled (form/disable-save? data)}
                 (tr [:buttons :save])])})

(defn operator [e! state]
  (r/with-let [form-options (operator-form-options e!)
               form-groups (operator-form-groups)]
    [:div
     [:div.row
      [:div  {:class "col-xs-12 col-sm-4 col-md-4"}
       [:h1 (tr [:organization-page :organization-form-title])]]]
     (when (second (:transport-operators-with-services state))
       [:div.row
        [:div  {:class "col-xs-12 col-sm-4 col-md-4"}
         [form-fields/field
          {:label (tr [:field-labels :select-transport-operator])
           :name        :select-transport-operator
           :type        :selection
           :show-option ::t-operator/name
           :update!   #(e! (to/->SelectOperator %))
           :options     (map :transport-operator (:transport-operators-with-services state))
           :auto-width? true}
          (get state :transport-operator)]]])

     [:div.row

      [form/form
       form-options
       form-groups

       (:transport-operator state)]]]))
