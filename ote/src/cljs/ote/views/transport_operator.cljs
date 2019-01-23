(ns ote.views.transport-operator
  "Form to edit transport operator information."
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]

            [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.ui.buttons :as buttons]
            [ote.ui.validation :as ui-validation]
            [stylefy.core :as stylefy]
            [ote.style.form :as style-form]
            [ote.ui.common :as ui-common]
            [ote.ui.form-fields :as form-fields]

            [ote.app.controller.transport-operator :as to]
            [ote.app.controller.front-page :as fp]

            [ote.db.transport-operator :as t-operator]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]
            [ote.style.dialog :as style-dialog]))

(defn- delete-operator [e! operator]
  (when (:show-delete-dialog? operator)
    [ui/dialog
     {:id "delete-transport-operator-dialog"
      :open true
      :actionsContainerStyle style-dialog/dialog-action-container
      :title (tr [:dialog :delete-transport-operator :title])
      :actions [(r/as-element
                  [ui/flat-button
                   {:label (tr [:buttons :cancel])
                    :primary true
                    :on-click #(e! (to/->ToggleTransportOperatorDeleteDialog))}])
                (r/as-element
                  [ui/raised-button
                   {:id "confirm-operator-delete"
                    :label (tr [:buttons :delete])
                    :icon (ic/action-delete-forever)
                    :secondary true
                    :primary true
                    :on-click #(e! (to/->DeleteTransportOperator (::t-operator/id operator)))}])]}
     (tr [:dialog :delete-transport-operator :confirm] {:name (::t-operator/name operator)})]))

(defn- operator-form-groups []
  [(form/group
    {:label (tr [:common-texts :title-operator-basic-details])
     :columns 1
     :tooltip (tr [:organization-page :basic-info-tooltip])
     :tooltip-length "large"}
    {:name ::t-operator/name
     :type :string
     :required? true
     :max-length 70}

    {:name ::t-operator/ckan-description
     :type :text-area
     :rows 2
     :tooltip (tr [:organization-page :ckan-description-tooltip])
     :tooltip-length "large"
    }

    {:name ::t-operator/business-id
     :type :string
     :validate [[:business-id]]
     :required? true
     :hint-text "1234567-8"}

    {:name ::common/street
     :type :string
     :max-length 128
     :read (comp ::common/street ::t-operator/visiting-address)
     :write (fn [data street]
              (assoc-in data [::t-operator/visiting-address ::common/street] street))}

    {:name ::common/postal_code
     :type :string
     :regex #"\d{0,5}"
     :read (comp ::common/postal_code ::t-operator/visiting-address)
     :write (fn [data postal-code]
              (assoc-in data [::t-operator/visiting-address ::common/postal_code] postal-code))}

    {:name :ote.db.common/post_office
     :type :string
     :max-length 64
     :read (comp :ote.db.common/post_office :ote.db.transport-operator/visiting-address)
     :write (fn [data post-office]
              (assoc-in data [:ote.db.transport-operator/visiting-address :ote.db.common/post_office] post-office))}

    {:name ::t-operator/homepage
     :type :string
     :max-length 200})

   (form/group
    {:label (tr [:organization-page :contact-types])
     :columns 1
     :tooltip (tr [:organization-page :contact-types-tooltip])
     :tooltip-length "large"}

    {:name ::t-operator/phone :type :string :regex ui-validation/phone-number-regex}
    {:name ::t-operator/gsm :type :string :regex ui-validation/phone-number-regex}
    {:name ::t-operator/email :type :string :max-length 200})])

(defn- operator-form-options [e!]
  {:name->label (tr-key [:field-labels])
   :update!     #(e! (to/->EditTransportOperatorState %))
   :footer-fn   (fn [data]
                  [:div
                   [buttons/save {:on-click #(e! (to/->SaveTransportOperator))
                                  :disabled (form/disable-save? data)}
                    (tr [:buttons :save])]
                   [buttons/delete {:on-click #(e! (to/->ToggleTransportOperatorDeleteDialog))
                                    :disabled (if (::t-operator/id data) false true)}
                    (tr [:buttons :delete-operator])]])})

(defn operator [e! {operator :transport-operator :as state}]
  (e! (to/->EditTransportOperator (get-in state [:params :id])))
  (fn [e! {operator :transport-operator :as state}]
    (r/with-let [form-options (operator-form-options e!)
                 form-groups (operator-form-groups)]
                [:div
                 [:div.row
                  [:div {:class "col-xs-12"}
                   [:h1 (tr [:organization-page
                             (if (:new? operator)
                               :organization-new-title
                               :organization-form-title)])]]]

                 [:div {:style {:white-space "pre-wrap"}}
                  [:p (tr [:organization-page :help-desc-1])]
                  [:p (tr [:organization-page :help-desc-2])]]
                 [delete-operator e! operator]
                 [:div.row.organization-info (stylefy/use-style style-form/organization-padding)
                  [form/form
                   form-options
                   form-groups
                   (:transport-operator state)]]])))
