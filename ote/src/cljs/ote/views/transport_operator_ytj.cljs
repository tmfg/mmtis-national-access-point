(ns ote.views.transport-operator-ytj
  "Form to edit transport operator information." ; TODO: this ytj replaces old solution when ready
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]

            [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.ui.buttons :as buttons]
            [ote.ui.validation :as ui-validation]
            [ote.ui.info :as info]
            [stylefy.core :as stylefy]
            [ote.style.form :as style-form]
            [ote.style.form-fields :as style-fields]
            [ote.ui.common :as ui-common]
            [ote.ui.form-fields :as form-fields]

            [ote.app.controller.transport-operator :as to]
            [ote.app.controller.front-page :as fp]

            [ote.db.transport-operator :as t-operator]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]
            [ote.style.base :as style-base]
            [ote.ui.common :as uicommon]))

(defn- delete-operator [e! operator]
  (when (:show-delete-dialog? operator)
    [ui/dialog
     {:id "delete-transport-operator-dialog"
      :open    true
      :title   (tr [:dialog :delete-transport-operator :title])
      :actions [(r/as-element
                  [ui/flat-button
                   {:label    (tr [:buttons :cancel])
                    :primary  true
                    :on-click #(e! (to/->ToggleTransportOperatorDeleteDialog))}])
                (r/as-element
                  [ui/raised-button
                   {:id "confirm-operator-delete"
                    :label     (tr [:buttons :delete])
                    :icon      (ic/action-delete-forever)
                    :secondary true
                    :primary   true
                    :on-click  #(e! (to/->DeleteTransportOperator (::t-operator/id operator)))}])]}
     (tr [:dialog :delete-transport-operator :confirm] {:name (::t-operator/name operator)})]))

(defn- business-id-selection [e! state]
  "Form group for querying business id from user and triggering data fetch for it from YTJ"
  (let [operator (:transport-operator state)
        status (get-in state [:ytj-response :status])
        ytj-loading? (fn [state] (:ytj-response-loading state))]
    (form/group
      {:card?           false}

      {:name      ::t-operator/business-id
       :type      :string
       :validate  [[:business-id]]
       :required? true
       :warning   (tr [:common-texts :required-field])
       :should-update-check form/always-update
       :on-blur #(e! (to/->EnsureUniqueBusinessId (-> % .-target .-value)))}

      ;disabled when business-id is taken or if business-id is not valid or if loading is ongoing
      {:name ::t-operator/btn-submit-business-id
       :type :external-button
       :label (tr [:organization-page :fetch-from-ytj])
       :primary true
       :secondary true
       :on-click #(e! (to/->FetchYtjOperator (::t-operator/business-id operator)))
       :disabled (or (not (nil? (get-in state [:transport-operator :ote.ui.form/errors ::t-operator/business-id])))
                     (get-in state [:transport-operator :business-id-exists])
                     (ytj-loading? state))}

      (when (:ytj-response state); label composition for error message
        (cond
          (= 200 status)
          (do )
          (= 404 status)
          {:name  :ytj-msg-results-not-found
           :type  :text-label
           :label (tr [:common-texts :data-not-found])}
          :else
          {:name  :ytj-msq-query-error
           :type  :text-label
           :label (tr [:common-texts :server-error-try-later])}))

      ; label composition for user instructions how to continue
      (when (and (:ytj-response state) (not= 200 status))
        {:name  :ytj-query-tip-whatnext
         :type  :text-label
         :label (str (tr [:common-texts :check-your-input]) " " (tr [:common-texts :optionally-fill-manually]))})

      ; label composition for existing business-id
      (when (get-in state [:transport-operator :business-id-exists])
        {:name :business-id-is-not-unique
         :type :text-label
         :label (tr :common-text :business-id-is-not-unique)}))))

(defn- operator-form-groups [e! state]
  "Creates a napote form and resolves data to fields. Assumes expired fields are already filtered from ytj-response."
  ;(.debug js/console "operator-form-groups: state=" (clj->js state))
  (let [response-ok? (= 200 (get-in state [:ytj-response :status]))
        disable-ytj-address-billing? (= (get-in state [:ytj-flags :use-ytj-addr-billing?]) true)
        disable-ytj-address-visiting? (= (get-in state [:ytj-flags :use-ytj-addr-visiting?]) true)
        ytj-company-names (:ytj-company-names state)
        ytj-company-names-found? (< 1 (count ytj-company-names))]
    (form/group
      {:label (tr [:common-texts :title-operator-basic-details])
       :columns 1
       :tooltip (tr [:organization-page :basic-info-tooltip])
       :tooltip-length "large"
       :card? false}

      {:name        :heading1
       :label       (str (tr [:organization-page :business-id-heading]) " " (get-in state [:transport-operator ::t-operator/business-id]))
       :type        :text-label
       :h-style     :h2
       :full-width? true}

      {:name        :heading1-divider
       :type        :divider}

      {:name       :heading2
       :label      (if ytj-company-names-found?
                     (tr [:organization-page :business-id-and-aux-names])
                     "Toiminimi")
       :type       :text-label
       :h-style    :h3
       :full-width? true}

      (when response-ok?
        {:name          :help-checkbox-group
         :type          :info-toggle
         :label         (tr [:common-texts :instructions])
         :body          [:div (tr [:organization-page :help-operator-edit-selection])]
         :default-state true})

      (if response-ok?                                      ; Input field if not YTJ results, checkbox-group otherwise
        {:name                :transport-operators-to-save
         :type                :checkbox-group
         :show-option         ::t-operator/name
         :option-enabled?     #(nil? (::t-operator/id %))
         :options             ytj-company-names
         :full-width?         true
         :should-update-check form/always-update
         :required?           true}
        {:name       ::t-operator/name
         :label      ""
         :type       :string
         :required?  true
         :style      style-fields/form-field})

      (when (and response-ok? (not ytj-company-names-found?))
        {:name :msg-no-aux-names-for-business-id
         :label (tr [:organization-page :no-aux-names-for-business-id])
         :type :text-label
         :max-length 128})

      {:name       :msg-business-id-contact-details
       :label      (if ytj-company-names-found?
                     (tr [:organization-page :contact-details-plural])
                     (tr [:organization-page :contact-details])
                     )
       :type       :text-label
       :max-length 128
       :h-style    :h3}

      (when response-ok?
        {:name          :help-operator-contact-details
         :type          :info-toggle
         :label         (tr [:common-texts :instructions])
         :body          [:div
                         (tr [:organization-page :help-operator-contact-entry])
                         [uicommon/extended-help-link (tr [:organization-page :help-ytj-contact-change-link]) (tr [:organization-page :help-ytj-contact-change-link-desc])]]
         :default-state true})

      {:name ::ote.db.transport-operator/billing-address
       :type :text-label
       :h-style :h4}

      {:name        ::common/billing-street
       :label       (tr [:field-labels :ote.db.common/street])
       :type        :string
       :disabled?   disable-ytj-address-billing?
       :style       style-fields/form-field
       :read        (comp ::common/street ::t-operator/billing-address)
       :write       (fn [data street]
                      (assoc-in data [::t-operator/billing-address ::common/street] street))}

      {:name ::common/billing-postal_code
       :label (tr [:field-labels :ote.db.common/postal_code])
       :type :string
       :disabled? disable-ytj-address-billing?
       :style style-fields/form-field
       :regex #"\d{0,5}"
       :read (comp ::common/postal_code ::t-operator/billing-address)
       :write (fn [data postal-code]
                (assoc-in data [::t-operator/billing-address ::common/postal_code] postal-code))}

      {:name ::common/billing-post_office
       :label (tr [:field-labels :ote.db.common/post_office])
       :type :string
       :disabled? disable-ytj-address-billing?
       :style style-fields/form-field
       :read (comp :ote.db.common/post_office :ote.db.transport-operator/billing-address)
       :write (fn [data post-office]
                (assoc-in data [:ote.db.transport-operator/billing-address :ote.db.common/post_office] post-office))}

      {:name ::ote.db.transport-operator/visiting-address
       :type :text-label
       :style style-fields/form-field
       :h-style :h4}

      {:name ::common/street
       :type :string
       :disabled? disable-ytj-address-visiting?
       :style style-fields/form-field
       :read (comp ::common/street ::t-operator/visiting-address)
       :write (fn [data street]
                (assoc-in data [::t-operator/visiting-address ::common/street] street))}

      {:name ::common/postal_code
       :type :string
       :disabled? disable-ytj-address-visiting?
       :style style-fields/form-field
       :regex #"\d{0,5}"
       :read (comp ::common/postal_code ::t-operator/visiting-address)
       :write (fn [data postal-code]
                (assoc-in data [::t-operator/visiting-address ::common/postal_code] postal-code))}

      {:name :ote.db.common/post_office
       :type :string
       :disabled? disable-ytj-address-visiting?
       :style style-fields/form-field
       :read (comp :ote.db.common/post_office :ote.db.transport-operator/visiting-address)
       :write (fn [data post-office]
                (assoc-in data [:ote.db.transport-operator/visiting-address :ote.db.common/post_office] post-office))}

      {:name ::t-operator/phone :type :string :disabled? (get-in state [:ytj-flags :use-ytj-phone?] false) :style style-fields/form-field :regex ui-validation/phone-number-regex}

      {:name ::t-operator/gsm :type :string :disabled? (get-in state [:ytj-flags :use-ytj-gsm?] false) :style style-fields/form-field :regex ui-validation/phone-number-regex}

      {:name ::t-operator/email :type :string :disabled? (get-in state [:ytj-flags :use-ytj-email?] false) :style style-fields/form-field}

      {:name ::t-operator/homepage :type :string :disabled? (get-in state [:ytj-flags :use-ytj-homepage?] false) :style style-fields/form-field})))

(defn- allow-manual-creation? [state] (some? (:ytj-response state)))

(defn- operator-form-options [e! state show-actions?]
  {:name->label     (tr-key [:field-labels])
   :update!         #(e! (to/->EditTransportOperatorState %))
   :footer-fn       (fn [data]
                      [:div
                       [:div
                        (when show-actions?
                          [buttons/save {:on-click #(e! (to/->SaveTransportOperator))
                                         :disabled (form/disable-save? data)}
                           (tr [:buttons :save])])

                        [buttons/save {:on-click #(e! (to/->CancelTransportOperator))}
                         (tr [:buttons :cancel])]]

                       (when show-actions?
                         (when (not (get-in state [:transport-operator :new?]))
                           [:div
                            [:br]
                            [ui/divider]
                            [:br]
                            [:div [:h3 (tr [:dialog :delete-transport-operator :title-base-view])]]
                            [info/info-toggle (tr [:common-texts :instructions]) (tr [:organization-page :help-operator-how-delete]) true]
                            [buttons/save {:on-click #(e! (to/->ToggleTransportOperatorDeleteDialog))
                                           :disabled (if (::t-operator/id data) false true)}
                             (tr [:buttons :delete-operator])]]))])})

(defn operator-ytj [e! {operator :transport-operator :as state}]
  (let [show-id-entry? (empty? (get-in state [:params :id]))
        show-details? (and (:transport-operator-loaded? state) (some? (:ytj-response state)))
        form-options (operator-form-options e! state show-details?)
        form-groups (cond-> []
                            show-id-entry? (conj (business-id-selection e! state))
                            show-details? (conj (operator-form-groups e! state)))]
    [:div
     [:div
      [:div
       [:h1 (tr [:organization-page
                 (if (:new? operator)
                   :organization-new-title
                   :organization-form-title)])]]]
     [:div
      [info/info-toggle (tr [:common-texts :instructions])
       [:div
        [:div (tr [:organization-page :help-ytj-integration-desc])]
        [:div (tr [:organization-page :help-desc-1])]
        [uicommon/extended-help-link (tr [:organization-page :help-about-ytj-link]) (tr [:organization-page :help-about-ytj-link-desc])]
        [uicommon/extended-help-link (tr [:organization-page :help-ytj-contact-change-link]) (tr [:organization-page :help-ytj-contact-change-link-desc])]]]]
     [delete-operator e! operator]
     [form/form
      form-options
      form-groups
      operator]]))
