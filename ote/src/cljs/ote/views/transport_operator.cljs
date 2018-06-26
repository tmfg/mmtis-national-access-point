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
            [ote.localization :refer [tr tr-key]]))

(defn- delete-operator [e! operator]
  (when (:show-delete-dialog? operator)
    [ui/dialog
     {:open    true
      :title   (tr [:dialog :delete-transport-operator :title])
      :actions [(r/as-element
                  [ui/flat-button
                   {:label    (tr [:buttons :cancel])
                    :primary  true
                    :on-click #(e! (to/->ToggleTransportOperatorDeleteDialog))}])
                (r/as-element
                  [ui/raised-button
                   {:label     (tr [:buttons :delete])
                    :icon      (ic/action-delete-forever)
                    :secondary true
                    :primary   true
                    :on-click  #(e! (to/->DeleteTransportOperator (::t-operator/id operator)))}])]}
     (tr [:dialog :delete-transport-operator :confirm] {:name (::t-operator/name operator)})]))

(defn transport-operator-selection [e! {operator :transport-operator
                                        operators :transport-operators-with-services
                                        show-add-member-dialog? :show-add-member-dialog?} & extended]
  [:span
   ;; Show operator selection if there are operators and we are not creating a new one
   (when (and (not (empty? operators))
              (not (:new? operator)))
     [:div.row
     [:div.col-sm-4.col-md-3
      [form-fields/field
       {:label (tr [:field-labels :select-transport-operator])
        :name        :select-transport-operator
        :type        :selection
        :show-option #(if (nil? %)
                        (tr [:buttons :add-new-transport-operator])
                        (::t-operator/name %))
        :update!   #(if (nil? %)
                      (e! (to/->CreateTransportOperator))
                      (e! (to/->SelectOperatorForService %)))
        :options     (into (mapv :transport-operator operators)
                           [:divider nil])
        :auto-width? true}
       operator]]

      (when extended
       [:div.col-xs-12.col-sm-3.col-md-2
       [ui/flat-button {:label (tr [:buttons :edit])
                        :style {:margin-top "1.5em"
                                :font-size "8pt"}
                        :icon (ic/content-create {:style {:width 16 :height 16}})
                        :on-click #(do
                                     (.preventDefault %)
                                     (e! (fp/->ChangePage :transport-operator {:id (::t-operator/id operator)})))}]])
      (when extended
       [:div.col-xs-12.col-sm-3.col-md-2
        [ui/flat-button {:label (tr [:buttons :add-new-member])
                         :style {:margin-top "1.5em"
                                 :font-size "8pt"}
                         :icon (ic/content-add {:style {:width 16 :height 16}})
                         :on-click #(do
                                      (.preventDefault %)
                                      (e! (fp/->ToggleAddMemberDialog)))}]
        (when show-add-member-dialog?
          [ui-common/ckan-iframe-dialog (::t-operator/name operator)
           (str "/organization/member_new/" (::t-operator/ckan-group-id operator))
           #(e! (fp/->ToggleAddMemberDialog))])])])])

(defn- operator-form-groups []
  [(form/group
    {:label (tr [:common-texts :title-operator-basic-details])
     :columns 1
     :tooltip (tr [:organization-page :basic-info-tooltip])
     :tooltip-length "large"}
    {:name ::t-operator/name
     :type :string
     :required? true}

    {:name ::t-operator/ckan-description
     :type :text-area
     :rows 2
     :tooltip (tr [:organization-page :ckan-description-tooltip])
     :tooltip-length "large"
    }

    {:name ::t-operator/business-id
     :type :string
     :validate [[:business-id]]
     :required? true}

    {:name ::common/street
     :type :string
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
     :read (comp :ote.db.common/post_office :ote.db.transport-operator/visiting-address)
     :write (fn [data post-office]
              (assoc-in data [:ote.db.transport-operator/visiting-address :ote.db.common/post_office] post-office))}

    {:name ::t-operator/homepage
       :type :string})

   (form/group
    {:label (tr [:organization-page :contact-types])
     :columns 1
     :tooltip (tr [:organization-page :contact-types-tooltip])
     :tooltip-length "large"}

    {:name ::t-operator/phone :type :string :regex ui-validation/phone-number-regex}
    {:name ::t-operator/gsm :type :string :regex ui-validation/phone-number-regex}
    {:name ::t-operator/email :type :string})])

(defn- operator-form-options [e!]
  {:name->label (tr-key [:field-labels])
   :update!     #(e! (to/->EditTransportOperatorState %))
   :name        #(tr [:olennaiset-tiedot :otsikot %])
   :footer-fn   (fn [data]
                  [:div
                   [buttons/save {:on-click #(e! (to/->SaveTransportOperator))
                                  :disabled (form/disable-save? data)}
                    (tr [:buttons :save])]
                   [buttons/delete {:on-click #(e! (to/->ToggleTransportOperatorDeleteDialog))
                                    :disabled (if (::t-operator/id data) false true)}
                    (tr [:buttons :delete])]])})

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
