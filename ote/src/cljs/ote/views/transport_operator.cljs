(ns ote.views.transport-operator
  "Form to edit transport operator information."
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]

            [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.ui.buttons :as buttons]
            [ote.ui.validation :as ui-validation]
            [ote.ui.info :as info]
            [ote.ui.select_field :as sf]
            [ote.ui.warning_msg :as msg-warn]
            [ote.ui.success_msg :as msg-succ]
            [ote.ui.circular_progress :as prog]
            [stylefy.core :as stylefy]
            [ote.style.form :as style-form]
            [ote.style.form-fields :as style-fields]
            [ote.ui.common :as ui-common]
            [ote.ui.form-fields :as form-fields]

            [ote.app.controller.flags :as flags]
            [ote.app.controller.transport-operator :as to]
            [ote.app.controller.front-page :as fp]

            [ote.db.transport-operator :as t-operator]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]
            [ote.style.base :as style-base]
            [ote.ui.common :as uicommon]
            [ote.style.dialog :as style-dialog])
  )

;; Returns boolean about if there are any orphan nap operators which need renaming to ytj-company-names
(defn- unmerged-ytj-nap-ops? [orphans]
  (some? (some #(if (:merge-handled? (:transport-operator %)) false true)
               orphans)))

(defn- delete-operator [e! operator service-vector]
  ;; When operator is passed from :transport-operators-to-save list they do not have business-id
  (let [toggle-dialog (if (nil? (::t-operator/business-id operator))
                        (to/->ToggleListTransportOperatorDeleteDialog operator)
                        (to/->ToggleSingleTransportOperatorDeleteDialog))
        operator-services (:transport-service-vector (some #(when (= (::t-operator/id operator) (get-in % [:transport-operator ::t-operator/id]))
                                                                %)
                                                           service-vector))]
  (when (:show-delete-dialog? operator)
    [:div
     [ui/dialog
      {:id (str "delete-transport-operator-dialog-" (::t-operator/id operator))
       :open true
       :actionsContainerStyle style-dialog/dialog-action-container
       :title (tr [:dialog :delete-transport-operator :title])
       :actions [(r/as-element
                   [buttons/delete
                    {:id "confirm-operator-delete"
                     :disabled (if (empty? operator-services)
                                 false
                                 true)
                     :on-click #(e! (to/->DeleteTransportOperator (::t-operator/id operator)))}
                    (tr [:buttons :delete])])
                 (r/as-element
                   [buttons/cancel
                    {:on-click #(e! toggle-dialog)}
                    (tr [:buttons :cancel])])]}
      [:div
       (if (empty? operator-services)
         (tr [:dialog :delete-transport-operator :confirm] {:name (::t-operator/name operator)})
         (tr [:organization-page :help-operator-how-delete]))]]])))

(defn- business-id-selection [e! state ytj-supported?]
  "Form group for querying business id from user and triggering data fetch for it from YTJ"
  (let [operator (:transport-operator state)
        status (get-in state [:ytj-response :status])
        ytj-loading? (fn [state] (:ytj-response-loading state)) ;; Declared as function because a form field needs a fn
        ytj-response? (:ytj-response state)
        ytj-success? (= 200 status)]
    (form/group
      (merge {:card? false}
             (when ytj-supported?                           ;; Sets input and button on same row
               {:layout :row}))

      (when-not ytj-supported?
        {:type :string
         :element-id "input-operator-name"
         :name ::t-operator/name
         :label (tr [:organization-page :business-or-aux-name])
         :required? true
         :style style-fields/form-field})

      {:type :string
       :name ::t-operator/business-id
       :label (tr [:organization-page :business-id-heading])
       :element-id "input-business-id"
       :validate [[:business-id]]
       :required? true
       :warning (tr [:common-texts :required-field])
       :should-update-check form/always-update
       :disabled? (ytj-loading? state)
       :style (if ytj-supported?
                (when (ytj-loading? state) style-base/disabled-control)
                style-fields/form-field)
       :on-change #(e! (to/->EnsureUniqueBusinessId %))}

      (when ytj-supported?
        ;; Disabled when business-id is taken or if business-id is not valid or if loading is ongoing
        {:type :external-button
         :element-id "btn-submit-business-id"
         :name ::t-operator/btn-submit-business-id
         :label (tr [:organization-page :fetch-from-ytj])
         :primary true
         :secondary true
         :on-click #(e! (to/->FetchYtjOperator (::t-operator/business-id operator)))
         :disabled (or
                     (empty? (::t-operator/business-id operator))
                     (not (nil? (get-in state [:transport-operator :ote.ui.form/errors ::t-operator/business-id])))
                     (:business-id-exists? operator)
                     (ytj-loading? state))})

      (when ytj-supported?
        {:type :loading-spinner
         :name :loading-spinner-ytj
         :display? (ytj-loading? state)})

      (when (and ytj-supported? ytj-response?)
        (if ytj-success?
          {:type :result-msg-success
           :name :ytj-result-msg
           :content (tr [:organization-page :fetch-from-ytj-success])}
          {:type :result-msg-warning
           :name :ytj-result-msg
           :content (if (= 404 status)
                      (str (tr [:common-texts :data-not-found])
                           " " (tr [:common-texts :optionally-fill-manually]))
                      (str (tr [:common-texts :server-error])
                           " " (tr [:common-texts :server-error-try-later])
                           " " (tr [:common-texts :optionally-fill-manually])))}))

      ; label composition for existing business-id
      (when (get-in state [:transport-operator :business-id-exists?])
        {:type :result-msg-warning
         :element-id "label-business-id-is-not-unique"
         :name :business-id-is-not-unique
         :content (tr [:common-texts :business-id-is-not-unique])}))))

(defn- operator-form-groups [e! {operator :transport-operator :as state} creating? ytj-supported?]
  "Creates a napote form and resolves data to fields. Assumes expired fields are already filtered from ytj-response."
  (let [ytj-response-ok? (= 200 (get-in state [:ytj-response :status]))
        disable-ytj-address-billing? (= (get-in state [:ytj-flags :use-ytj-addr-billing?]) true)
        disable-ytj-address-visiting? (= (get-in state [:ytj-flags :use-ytj-addr-visiting?]) true)
        ytj-company-names (:ytj-company-names state)
        ytj-company-names-found? (pos-int? (count ytj-company-names))
        required-public-contact-missing? (fn [operator] (and (empty? (::t-operator/phone operator))
                                                             (empty? (::t-operator/gsm operator))
                                                             (empty? (::t-operator/email operator))
                                                             (empty? (::t-operator/homepage operator))))]
    (form/group
      {:label (tr [:common-texts :title-operator-basic-details])
       :columns 1
       :tooltip (tr [:organization-page :basic-info-tooltip])
       :tooltip-length "large"
       :card? false}

      (when ytj-supported?
        {:type :divider
         :name :heading1-divider})

      (when-not (or creating? ytj-supported?)
        {:type :string
         :element-id "input-operator-name"
         :name ::t-operator/name
         :label (tr [:organization-page :business-or-aux-name])
         :required? true
         :style style-fields/form-field})

      ;; Because user not allowed to edit business id himself,
      ;; business-id field is input in business-id-selection when creating, a read-only label here when only modifying.
      (when-not creating?
        {:type :string
         :name ::t-operator/business-id
         :label (tr [:organization-page :business-id-heading])
         :element-id "field-business-id-editmode"
         :disabled? true
         :style  style-fields/form-field})

      (when ytj-response-ok?
        {:type :text-label
         :name :heading2
         :label (tr [:organization-page (if ytj-company-names-found?
                                          :business-id-and-aux-names
                                          :business-or-aux-name)])
         :h-style :h3
         :full-width? true})

      (when ytj-response-ok?
        {:type          :info-toggle
         :name          :help-checkbox-group
         :label         (tr [:common-texts :instructions])
         :body          [:div (tr [:organization-page :help-operator-edit-selection])]
         :default-state true})

      (when ytj-response-ok? ;; Input field if no YTJ results, checkbox-group otherwise
        {:type                :checkbox-group-with-delete
         :name                :transport-operators-to-save
         :show-option         ::t-operator/name
         :option-enabled?     #(nil? (::t-operator/id %))
         :options             ytj-company-names
         :should-update-check form/always-update
         :required?           true
         :on-delete (fn [data]
                      (do
                        (e! (to/->ToggleListTransportOperatorDeleteDialog data))
                        (delete-operator e! data (:transport-operators-with-services state))))})

      (when (and ytj-response-ok? (not ytj-company-names-found?))
        {:type :text-label
         :name :msg-no-aux-names-for-business-id
         :label (tr [:organization-page :no-aux-names-for-business-id])

         :max-length 128})

      (when ytj-response-ok?
        {:type          :info-toggle
         :name          :help-operator-contact-details
         :label         (tr [:common-texts :instructions])
         :body          [:div
                         (tr [:organization-page :help-operator-contact-entry])
                         [uicommon/extended-help-link
                          (tr [:organization-page :help-ytj-contact-change-link])
                          (tr [:organization-page :help-ytj-contact-change-link-desc])]]
         :default-state true})

      ;; Contact details for service developers

      {:type :text-label
       :name :heading-contact-details-other
       :label (tr [:organization-page :contact-details-service-developers])
       :h-style :h2}

      {:type :info-toggle
       :name :help-operator-contact-details-service-developers
       :label (tr [:common-texts :instructions])
       :body [:div
              [:p (tr [:organization-page :help-contact-details])]
              [:p (tr [:organization-page :help-contact-details-privacy])]
              [:p (tr [:common-texts :nap-data-license]) "\"" (tr [:common-texts :nap-data-license-url-label]) "\"."]]
       :default-state true}

      (if (required-public-contact-missing? operator)
        {:type :result-msg-warning
         :element-id "label-public-contact-missing"
         :name :public-contact-missing
         :content (tr [:common-texts :required-one-of-many])}
        {:type :text-label
         :name :warning-public-contact-missing-placeholder
         :label ""
         :h-style :h4})

      {:type :string
       :element-id "input-operator-telephone"
       :name ::t-operator/phone
       :label (tr [:organization-page :field-phone-telephone] )
       :disabled? (get-in state [:ytj-flags :use-ytj-phone?] false)
       :required? (required-public-contact-missing? operator)
       :style style-fields/form-field
       :regex ui-validation/phone-number-regex}

      {:type :string
       :element-id "input-operator-mobilePhone"
       :name ::t-operator/gsm
       :label (tr [:organization-page :field-phone-mobile] )
       :disabled? (get-in state [:ytj-flags :use-ytj-gsm?] false)
       :required? (required-public-contact-missing? operator)
       :style style-fields/form-field
       :regex ui-validation/phone-number-regex}

      {:type :string
       :element-id "input-operator-email"
       :name ::t-operator/email
       :disabled? (get-in state [:ytj-flags :use-ytj-email?] false)
       :required? (required-public-contact-missing? operator)
       :style style-fields/form-field}

      {:type :string
       :element-id "input-operator-web"
       :name ::t-operator/homepage
       :disabled? (get-in state [:ytj-flags :use-ytj-homepage?] false)
       :required? (required-public-contact-missing? operator)
       :style style-fields/form-field}

      ;; Contact details for authorities

      {:type :text-label
       :name :heading-contact-details-authorities
       :label (tr [:organization-page :contact-details-authorities])
       :h-style :h2}

      {:type :info-toggle
       :name :help-operator-contact-details-auth
       :label (tr [:common-texts :instructions])
       :body [:p (tr [:organization-page :help-contact-details-auth])]
       :default-state true}

      {:type :string
       :element-id "input-operator-telephone-auth"
       :name ::t-operator/authority-phone
       :required? true
       :label (tr [:organization-page :field-phone-telephone])
       :style style-fields/form-field
       :regex ui-validation/phone-number-regex}

      {:type :string
       :element-id "input-operator-email-auth"
       :name ::t-operator/authority-email
       :required? true
       :label ::t-operator/email
       :style style-fields/form-field
       ;:validate [[:email]];;TODO implement email regex
       })))

(defn- operator-merge-section [e! {nap-orphans :ytj-orphan-nap-operators :as operator} ytj-company-names]
  [:div {:style style-base/wizard-container}
   [:div [:h3 (tr [:organization-page :heading-operator-edit])]]
   [info/info-toggle (tr [:common-texts :instructions]) (tr [:organization-page :help-merge-company-names]) true]
   (doall
     (for [n nap-orphans
           :let [nap-op (:transport-operator n)
                 control-disabled? false]
           :when n]
       ^{:key (str "operator-merge-section-item-" (::t-operator/name nap-op))}
       [:div {:style (merge
                       (style-base/flex-container "row")
                       (style-base/align-items "center")
                       (style-base/justify-content "flex-start")
                       (when control-disabled? style-base/disabled-control))}
        [:div "\"" [:strong (::t-operator/name nap-op)] "\" " (tr [:organization-page :merge-operator-to-ytj])]
        [:div {:style style-base/item-list-row-margin}]
        [sf/select-field
         {:options
          (vec (concat
                 [{::t-operator/name "" :placeholder true}] ;; menuitem for "no selection" goes first
                 ytj-company-names))
          :show-option #(::t-operator/name %)
          :style style-base/item-list-row-margin
          :update! #(e! (to/->OperatorRename nap-op %))}]

        (when (= false (:save-success? nap-op))
          [msg-warn/warning-msg (str (tr [:common-texts :save-failure])
                                     " "
                                     (tr [:common-texts :server-error-try-later]))])

        (when (= true (:save-success? nap-op))
          [msg-succ/success-msg (tr [:common-texts :save-success])])]))

   [buttons/save {:on-click #(e! (to/->UserCloseMergeSection nil))
                  :disabled (unmerged-ytj-nap-ops? nap-orphans)
                  :style style-form/action-control-section-margin}
    (tr [:buttons :next])]])

(defn- operator-form-options [e! state show-actions?]
  {:name->label (tr-key [:field-labels])
   :update! #(e! (to/->EditTransportOperatorState %))
   :footer-fn (fn [data]
                [:div {:style style-form/action-control-section-margin}
                 [:div
                  (when show-actions?
                    [buttons/save {:id "btn-operator-save"
                                   :on-click #(e! (to/->SaveTransportOperator))
                                   :disabled (or (get-in state [:transport-operator :business-id-exists?])
                                                 (form/disable-save? data))}
                     (tr [:buttons :save])])

                  [buttons/cancel {:on-click #(e! (to/->CancelTransportOperator))}
                   (tr [:buttons :cancel])]]

                 (when (and show-actions? (empty? (:ytj-company-names state)))
                   (when (not (get-in state [:transport-operator :new?]))
                     [:div
                      [:br]
                      [ui/divider]
                      [:br]
                      [:div [:h2 (tr [:dialog :delete-transport-operator :title-base-view])]]
                      [info/info-toggle (tr [:common-texts :instructions]) (tr [:organization-page :help-operator-how-delete]) true]
                      [buttons/save {:on-click #(e! (to/->ToggleSingleTransportOperatorDeleteDialog))
                                     :disabled (if (and
                                                     (empty? (:transport-service-vector state))
                                                     (::t-operator/id data))
                                                 false
                                                 true)}
                       (tr [:buttons :delete-operator])]]))])})

(defn operator [e! {operator :transport-operator :as state}]
  (let [creating? (nil? (get-in state [:params :id]))
        ytj-supported? (flags/enabled? :open-ytj-integration)
        show-details? (or (not ytj-supported?)
                          (and (:transport-operator-loaded? state)
                               (some? (:ytj-response state))))
        show-merge-companies? (and (pos-int? (count (get-in state [:transport-operator :ytj-orphan-nap-operators])))
                                   (pos-int? (count (:ytj-company-names state))))
        form-groups (cond-> []
                            creating? (conj (business-id-selection e! state ytj-supported?))
                            show-details? (conj (operator-form-groups e! state creating? ytj-supported?)))]
    [:div
     [:div
      [:div
       [:h1 (tr [:organization-page
                 (if (:new? operator)
                   :organization-new-title
                   :organization-form-title)])]]]
     [:h2 (tr [:organization-page :basic-details])]
     [:div
      [info/info-toggle (tr [:common-texts :instructions] true)
       (if ytj-supported?
         [:div
          [:div (tr [:organization-page :help-ytj-integration-desc])]
          [:div (tr [:organization-page :help-desc-1])]
          [uicommon/extended-help-link (tr [:organization-page :help-about-ytj-link]) (tr [:organization-page :help-about-ytj-link-desc])]
          [uicommon/extended-help-link (tr [:organization-page :help-ytj-contact-change-link]) (tr [:organization-page :help-ytj-contact-change-link-desc])]]
         [:div
          [:div (tr [:organization-page :basic-info-tooltip])]])]]

     ;; When business-id has multiple companies create list of delete-operator dialogs. Otherwise add only one
     (if (empty? (:ytj-company-names state))
       [delete-operator e! operator (:transport-operators-with-services state)]
       (for [o (get-in state [:transport-operator :transport-operators-to-save])]
         ^{:key (str "operator-delete-control-" (::t-operator/name o) "-" (::t-operator/id o) )}
         [delete-operator e! o (:transport-operators-with-services state)]))

     (if show-merge-companies?
       (operator-merge-section e! operator (:ytj-company-names state))
       [form/form
        (operator-form-options e! state show-details?)
        form-groups
        operator])]))
