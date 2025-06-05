(ns ote.views.transport-service.transport-service-common
  "View parts that are common to all transport service forms."
  (:require [clojure.string :as str]
            [tuck.core :as tuck]
            [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [stylefy.core :as stylefy]
            [ote.time :as time]
            [ote.localization :refer [tr tr-key tr-tree]]
            [ote.util.values :as values]
            [ote.db.transport-service :as t-service]
            [ote.db.common :as common]
            [ote.theme.colors :as colors]
            [ote.style.form :as style-form]
            [ote.style.dialog :as style-dialog]
            [ote.style.base :as style-base]
            [ote.ui.buttons :as buttons]
            [ote.ui.common :refer [linkify dialog tooltip-wrapper]]
            [ote.ui.form :as form]
            [ote.ui.form-fields :as form-fields]
            [ote.ui.info :as info]
            [ote.ui.validation :as validation]
            [ote.app.controller.common :as common-c]
            [ote.app.controller.transport-service :as ts-controller]
            [ote.app.controller.flags :as flags]
            [ote.views.place-search :as place-search]
            [ote.views.place-search :as place-search]
            [ote.app.controller.admin-validation :as admin-validation]
            [taxiui.app.routes :as taxiui-router]))

(defn advance-reservation-group
  "Creates a form group for in advance reservation.
   Form displays header text and selection list by radio button group."
  [in-validation?]
  (form/group
    {:label (tr [:field-labels :transport-service-common ::t-service/advance-reservation])
     :columns 3
     :layout :row
     :card? false
     :top-border true}

    {:type :info-toggle
     :name :advance-reservation-group-info
     :label (tr [:common-texts :filling-info])
     :body [:div (tr [:form-help :advance-reservation-info])]
     :default-state false
     :full-width? true
     :container-class "col-xs-12 col-sm-12 col-md-12"}

    {:name ::t-service/advance-reservation
     :type :selection
     :disabled? in-validation?
     :show-option (tr-key [:enums ::t-service/advance-reservation])
     :options t-service/advance-reservation
     :radio? true
     :required? true
     :container-class "col-md-12"}))

(defn service-url
  "Creates a form group for service url that creates two form elements url and localized text area"
  [element-id label service-url-field info-message in-validation?]
  (apply
    form/group
    {:label label
     :layout :row
     :columns 2
     :card? false
     :top-border true}

    (concat
      (when info-message
        [{:type :info-toggle
          :name :service-url-info
          :label (tr [:common-texts :filling-info])
          :body [:div info-message]
          :default-state false
          :full-width? true
          :container-class "col-xs-12 col-sm-12 col-md-12"}])

      [{:class "set-bottom"
        :element-id element-id
        :name ::t-service/url
        :type :string
        :disabled? in-validation?
        :read (comp ::t-service/url service-url-field)
        :write (fn [data url]
                 (assoc-in data [service-url-field ::t-service/url] url))
        :full-width? true
        :container-class "col-xs-12 col-sm-12 col-md-6 col-lg-6"
        :max-length 200}

       {:name ::t-service/description
        :type :localized-text
        :disabled? in-validation?
        :rows 1
        :read (comp ::t-service/description service-url-field)
        :write (fn [data desc]
                 (assoc-in data [service-url-field ::t-service/description] desc))
        :container-class "col-xs-12 col-sm-12 col-md-6 col-lg-6"
        :full-width? true}])))

(defn service-urls
  "Creates a table for additional service urls."
  [label service-url-field in-validation?]
  (form/group
    {:label label
     :columns 3
     :card? false
     :top-border true}

    (merge
      {:name service-url-field
       :type :div-table
       :container-class "col-xs-12"
       :prepare-for-save values/without-empty-rows
       :table-fields [{:name ::t-service/url
                       :label (tr [:field-labels :transport-service ::t-service/url])
                       :type :string
                       :disabled? in-validation?
                       :full-width? true
                       :field-class "col-xs-12 col-sm-3 col-md-3"}
                      {:name ::t-service/description
                       :label (tr [:field-labels :transport-service ::t-service/description])
                       :type :localized-text
                       :disabled? in-validation?
                       :full-width? true
                       :field-class "col-xs-12 col-sm-7 col-md-7"}]}
      (when-not in-validation?
        {:inner-delete? true
         :add-label (tr [:buttons :add-new-service-link])
         :inner-delete-class "col-xs-12 col-sm-2 col-md-2"
         :inner-delete-label (tr [:buttons :delete])}))))

(defn- gtfs-viewer-link [{interface ::t-service/external-interface format ::t-service/format}]
  (when (seq format)
    (let [format (str/lower-case (first format))]
      (when (or (= "gtfs" format) (= "kalkati.net" format))
        (linkify
          (str "#/routes/view-gtfs?url=" (.encodeURIComponent js/window
                                                              (::t-service/url interface))
               (when (= "kalkati.net" format)
                 "&type=kalkati"))
          [ui/icon-button
           [(tooltip-wrapper ic/action-visibility) {:style style-base/icon-small}
            {:text (tr [:form-help :external-interfaces-tooltips :view-routes])}]]
          {:target "_blank"})))))

(defn external-interfaces
  "Creates a form group for external services. Displays help texts conditionally by transport operator type."
  [& [e! type sub-type transport-type in-validation?]]
  (let [type (or type :other)
        transport-type (set transport-type)
        field-help (fn [label help-text]
                     [:div {:style {:padding-top "1rem"}}
                      [:strong {:style {:padding-right "1rem"}}
                       (str label ": ")]
                      [:span help-text]])]

    (form/group
      {:label (tr [:field-labels :transport-service-common ::t-service/external-interfaces])
       :columns 3
       :card? false
       :top-border true}

      {:name :external-interface-instructions
       :type :component
       :full-width? true
       :component (fn [_]
                    [info/info-toggle
                     (tr [:own-services-page :filling-info])
                     [:div
                      [:div
                       [:b (if (= :schedule sub-type)
                             [:span (str (tr [:form-help :external-interfaces-intro-1]) " ")
                              (when (:road transport-type)
                                [:span (str (tr [:form-help :external-interfaces-intro-rae-text]) " ")
                                 [linkify "https://www.traficom.fi/fi/asioi-kanssamme/saannollisen-henkiloliikenteen-reitti-ja-aikataulutiedon-digitoiminen"
                                  (str (tr [:form-help :RAE-link-text]) ". ")
                                  {:target "_blank"}]])
                              (when (and (flags/enabled? :sea-routes) (:sea transport-type))
                                [:span
                                 (str (tr [:form-help :external-interfaces-intro-rae-text]) " ")
                                 [linkify "/ote/#/routes" (tr [:form-help :SEA-ROUTE-link-text])
                                  {:target "_blank"}]])]
                             (tr [:form-help :external-interfaces-intro]))]]
                      [:div (tr [:form-help :external-interfaces])]
                      [dialog
                       (tr [:form-help :external-interfaces-read-more :link])
                       (tr [:form-help :external-interfaces-read-more :dialog-title])
                       [:div
                        (tr [:form-help :external-interfaces-read-more :dialog-text])]]
                      (when (= :passenger-transportation type)
                        [:div {:style {:margin-top "1rem"}}
                         [:b (tr [:form-help :external-interfaces-payment-systems])]])

                      [:div
                       [field-help (tr [:field-labels :transport-service-common ::t-service/data-content]) (tr [:form-help :external-interfaces-tooltips :data-content])]
                       [field-help (tr [:field-labels :transport-service-common ::t-service/format]) (tr [:form-help :external-interfaces-tooltips :format])]
                       [field-help (tr [:field-labels :transport-service-common ::t-service/external-service-url]) (tr [:form-help :external-interfaces-tooltips :external-service-url])]
                       [field-help (tr [:field-labels :transport-service-common ::t-service/external-service-description]) (tr [:form-help :external-interfaces-tooltips :external-service-description])]
                       [field-help (tr [:field-labels :transport-service-common ::t-service/license]) (tr [:form-help :external-interfaces-tooltips :license])]]]
                     {:default-open? false}])}
      (merge
        {:name ::t-service/external-interfaces
         :type :div-table
         :prepare-for-save values/without-empty-rows
         :table-fields [{:name ::t-service/data-content
                         :type :multiselect-selection
                         :disabled? in-validation?
                         :label (tr [:field-labels :transport-service-common ::t-service/data-content])
                         :field-class "col-xs-12 col-sm-3 col-md-3"
                         :full-width? true
                         :options t-service/interface-data-contents
                         :show-option (tr-key [:enums ::t-service/interface-data-content])
                         :required? true
                         :is-empty? validation/empty-enum-dropdown?}

                        {:name ::t-service/format
                         :type :selection
                         :disabled? in-validation?
                         :label (tr [:field-labels :transport-service-common ::t-service/format])
                         :field-class "col-xs-12 col-sm-3 col-md-3"
                         :full-width? true
                         ; XXX: This used to be a multiselect which means the produced values were wrapped in a vector,
                         ;      the database type for this column is array etc. etc. Easiest way to get around this
                         ;      without breaking existing data is to just return one-item vectors :)
                         :options (if (flags/enabled? :new-transit-data-formats)
                                    [["GTFS"] ["GTFS-RT"] ["GBFS"] ["Kalkati"] ["NeTEx"] ["GeoJSON"] ["JSON"]
                                     ["CSV"] ["Datex II"] ["SIRI"] ["SIRI-ET"] ["SIRI-SX"] ["SIRI-VM"]]
                                    [["GTFS"] ["Kalkati.net"] ["SIRI"] ["NeTEx"] ["GeoJSON"] ["JSON"] ["CSV"]])
                         :show-option first
                         :required? true
                         :is-empty? validation/empty-enum-dropdown?
                         :update! (fn [v] [v])}

                        {:name ::t-service/external-service-url
                         :type :component
                         :label (tr [:field-labels :transport-service-common ::t-service/external-service-url])
                         :field-class "col-xs-12 col-sm-6 col-md-6"
                         :read #(identity %)
                         :write (fn [row val]
                                  (assoc-in row [::t-service/external-interface ::t-service/url] val))
                         :component (fn [{{external-interface ::t-service/external-interface format ::t-service/format
                                           :as row} :data
                                          update-form! :update-form!
                                          row-number :row-number}]

                                      [:div (stylefy/use-style {:display "flex" :flex-flow "row nowrap"})
                                       [form-fields/field
                                        (merge
                                          {:name ::t-service/external-service-url
                                           :type :string
                                           :disabled? in-validation?
                                           :label (tr [:field-labels :transport-service-common ::t-service/external-service-url])
                                           :required? true
                                           :full-width? true
                                           :update! #(update-form! %)
                                           :on-blur (fn [e]
                                                      (e! (ts-controller/->EnsureExternalInterfaceUrl (-> e .-target .-value) (first format))))
                                           :max-length 200}
                                          ;; For first row: If there is data in other fields, show this required field warning
                                          ;; For other rows, if this required field is missing, show the warning.
                                          (when (or (and (= row-number 0) (seq row) (empty? (::t-service/url external-interface)))
                                                    (and (> row-number 0) (empty? (::t-service/url external-interface))))
                                            {:warning (tr [:common-texts :required-field])}))
                                        (::t-service/url external-interface)]

                                       [:span {:style {:width "30%" :margin-left "0.5rem" :padding-top "1.5rem"}}
                                        (let [url-status (get-in external-interface [:url-status :status])
                                              url-error (get-in external-interface [:url-status :error])]
                                          (if-not url-status
                                            [gtfs-viewer-link row]

                                            (if (= :success url-status)
                                              [:span (stylefy/use-style {:display "flex" :flex-flow "row nowrap"})
                                               [(tooltip-wrapper ic/action-done)
                                                {:style (merge style-base/icon-small
                                                               {:color "green"
                                                                :position "relative"
                                                                :top "15px"})}
                                                {:text (tr [:field-labels :transport-service-common :external-interfaces-ok])}]
                                               [gtfs-viewer-link row]]
                                              [:span [(tooltip-wrapper ic/alert-warning)
                                                      {:style (merge style-base/icon-small
                                                                     {:color "cccc00"
                                                                      :position "relative"
                                                                      :top "15px"})}

                                                      {:text (tr [:field-labels :transport-service-common
                                                                  (if (= :zip-validation-failed url-error)
                                                                    :external-interfaces-validation-error
                                                                    :external-interfaces-warning)])}]])))]])}

                        {:name ::t-service/external-service-description
                         :type :localized-text
                         :disabled? in-validation?
                         :label (tr [:field-labels :transport-service-common ::t-service/external-service-description])
                         :field-class "col-xs-12 col-sm-6 col-md-6"
                         :full-width? true
                         :read (comp ::t-service/description ::t-service/external-interface)
                         :write #(assoc-in %1 [::t-service/external-interface ::t-service/description] %2)
                         :required? false}
                        (if in-validation?
                          {:name ::t-service/license
                           :type :string
                           :disabled? in-validation?
                           :read (fn [val] (get val ::t-service/license))
                           :label (tr [:field-labels :transport-service-common ::t-service/license])
                           :field-class "col-xs-12 col-sm-3 col-md-3"
                           :full-width? true}
                          {:name ::t-service/license
                           :type :autocomplete
                           :open-on-focus? true
                           :label (tr [:field-labels :transport-service-common ::t-service/license])
                           :field-class "col-xs-12 col-sm-3 col-md-3"
                           :full-width? true
                           :suggestions (tr-tree [:licenses :external-interfaces])
                           :max-results 10})]}
        (when-not in-validation?
          {:add-label (tr [:buttons :add-external-interface])
           :inner-delete? true
           :inner-delete-class "col-xs-12 col-sm-3 col-md-3"
           :inner-delete-label (tr [:buttons :delete-interface])})))))

(defn companies-group
  "Creates a form group for companies. A parent company can list its companies."
  [e! in-validation? service-id db-file-key]
  (form/group
    {:label (tr [:field-labels :transport-service-common ::t-service/companies])
     :columns 3
     :card? false
     :top-border true}

    {:type :info-toggle
     :name :companies-group-info
     :label (tr [:common-texts :filling-info])
     :body [:div (tr [:form-help :companies-main-info])]
     :default-state false
     :full-width? true
     :container-class "col-xs-12 col-sm-12 col-md-12"}

    {:name ::t-service/company-source
     :read identity
     :write #(merge %1 %2)
     :type :company-source
     :disabled? in-validation?
     :in-validation? in-validation?
     :enabled-label (tr [:field-labels :parking :maximum-stay-limited])
     :container-style style-form/full-width
     :on-file-selected (fn [evt filename]
                         (ts-controller/read-companies-csv! e! (.-target evt) service-id db-file-key))
     :on-file-delete #(e! (ts-controller/->DeleteCompanyCsv db-file-key))
     :on-url-given #(e! (ts-controller/->EnsureCsvFile))
     :validate [(fn [data row]
                  (let [companies (::t-service/companies row)]
                    (case (::t-service/company-source data)
                      :form
                      (when (some #(or (empty? (::t-service/name %))
                                       (or (empty? (::t-service/business-id %))
                                           (validation/validate-rule :business-id nil (::t-service/business-id %))))
                                  companies)
                        (tr [:common-texts :required-field]))
                      nil)))]}))

(defn brokerage-group
  "Creates a form group for brokerage selection."
  [e! in-validation?]
  (form/group
    {:label (tr [:passenger-transportation-page :header-brokerage])
     :columns 3
     :card? false
     :top-border true}

    {:name ::t-service/brokerage?
     :extended-help {:help-text (tr [:form-help :brokerage?])
                     :help-link-text (tr [:form-help :brokerage-link])
                     :help-link "https://www.traficom.fi/fi/asioi-kanssamme/ilmoittaudu-valitys-ja-yhdistamispalveluntarjoajaksi"}
     :type :checkbox
     :disabled? in-validation?
     :on-click #(e! (ts-controller/->ShowBrokeringServiceDialog))}))

(defn contact-info-group [in-validation?]
  (form/group
    {:label (tr [:passenger-transportation-page :header-contact-details])
     :columns 3
     :layout :row
     :card? false
     :top-border true}

    {:type :info-toggle
     :name :contact-info
     :label (tr [:common-texts :filling-info])
     :body [:div (tr [:form-help :description-why-contact-info])]
     :default-state false
     :full-width? true
     :container-class "col-xs-12 col-sm-12 col-md-12"}

    {:name ::common/street
     :type :string
     :disabled? in-validation?
     :container-class "col-xs-12 col-sm-6 col-md-6"
     :full-width? true
     :read (comp ::common/street ::t-service/contact-address)
     :write (fn [data street]
              (assoc-in data [::t-service/contact-address ::common/street] street))
     :label (tr [:field-labels ::common/street])
     :max-length 128}

    {:name ::common/postal_code
     :type :string
     :disabled? in-validation?
     :container-class "col-xs-12 col-sm-6 col-md-6"
     :full-width? true
     :read (comp ::common/postal_code ::t-service/contact-address)
     :write (fn [data postal-code]
              (assoc-in data [::t-service/contact-address ::common/postal_code] postal-code))
     :label (tr [:field-labels ::common/postal_code])
     :validate [[:every-postal-code]]}

    {:name ::common/post_office
     :type :string
     :disabled? in-validation?
     :container-class "col-xs-12 col-sm-6 col-md-6"
     :full-width? true
     :read (comp ::common/post_office ::t-service/contact-address)
     :write (fn [data post-office]
              (assoc-in data [::t-service/contact-address ::common/post_office] post-office))
     :label (tr [:field-labels ::common/post_office])
     :max-length 64}

    {:element-id "input-service-country"
     :label (tr [:common-texts :country])
     :name :country
     :type :selection
     :disabled? in-validation?
     :container-class "col-xs-12 col-sm-6 col-md-6"
     :style {:margin-bottom "2rem"}
     :full-width? true
     :show-option (tr-key [:country-list])
     :options (common-c/country-list (tr-tree [:country-list]))
     :read (comp ::common/country_code ::t-service/contact-address)
     :write (fn [data country_code]
              (assoc-in data [::t-service/contact-address ::common/country_code] country_code))}

    {:name ::t-service/contact-email
     :type :string
     :disabled? in-validation?
     :container-class "col-xs-12 col-sm-6 col-md-6"
     :full-width? true
     :max-length 200}

    {:name ::t-service/contact-phone
     :type :string
     :disabled? in-validation?
     :container-class "col-xs-12 col-sm-6 col-md-6"
     :max-length 16
     :full-width? true}

    {:name ::t-service/homepage
     :type :string
     :disabled? in-validation?
     :container-class "col-xs-12 col-sm-6 col-md-6"
     :full-width? true
     :max-length 200}))

(defn- brokering-dialog [e! app]
  (when (get-in app [:transport-service :show-brokering-service-dialog?])
    [ui/dialog
     {:id "brokering-service-dialog"
      :open true
      :actionsContainerStyle style-dialog/dialog-action-container
      :title (tr [:dialog :brokering-service :title])
      :actions [
                (r/as-element
                  [ui/flat-button
                   {:id "confirm-brokering-service"
                    :label (tr [:dialog :brokering-service :ok])
                    :secondary true
                    :primary true
                    :on-click #(e! (ts-controller/->SelectBrokeringService true))}])
                (r/as-element
                  [ui/raised-button
                   {:label (tr [:dialog :brokering-service :cancel])
                    :primary true
                    :on-click #(e! (ts-controller/->SelectBrokeringService false))}])]}
     [:p (tr [:dialog :brokering-service :body])]
     [:div
      (linkify (tr [:dialog :brokering-service :link-url])
               [:span (stylefy/use-style style-base/blue-link-with-icon)
                (ic/content-create {:style {:width 20
                                            :height 20
                                            :margin-right "0.5rem"
                                            :color colors/primary}})
                (tr [:dialog :brokering-service :link-text])]
               {:target "_blank" :style {:text-decoration "none"}})]]))

(defn- open-publish-dialog [e! app]
  (when (get-in app [:admin :in-validation :modal])
    [ui/dialog
     {:open true
      :actionsContainerStyle style-dialog/dialog-action-container
      :title "Oletko varma, että haluat julkaista palvelun"
      :actions [(r/as-element
                  [buttons/cancel
                   {:on-click #(do
                                 (.preventDefault %)
                                 (e! (admin-validation/->CloseConfirmPublishModal)))}
                   (tr [:buttons :cancel])])
                (r/as-element
                  [buttons/save
                   {:on-click #(do
                                 (.preventDefault %)
                                 (e! (admin-validation/->PublishService (get-in app [:admin :in-validation :modal]))))}
                   (tr [:buttons :publish])])]}

     [:div "Julkaise painamalla julkaise"]]))

(defn- open-confirm-cancel-dialog [e! schemas]
  [ui/dialog
   {:open true
    :actionsContainerStyle style-dialog/dialog-action-container
    :title (tr [:dialog :change-to-draft :title])
    :actions [; Leave form and change service state back to validation
              (r/as-element
                [buttons/cancel
                 {:icon (ic/action-delete-forever)
                  :on-click #(e! (ts-controller/->SaveTransportService schemas false))}
                 (tr [:dialog :change-to-draft :ok-button])])
              ; Return to form and do not cancel
              (r/as-element
                [buttons/save
                 {:on-click #(e! (ts-controller/->CloseChangeToDraftModal))}
                 (tr [:dialog :change-to-draft :cancel-button])])]}
   [:span (tr [:dialog :change-to-draft :body]) ]])

(defn- open-cancel-revalidate-dialog [e! service-id]
  [ui/dialog
   {:open true
    :actionsContainerStyle style-dialog/dialog-action-container
    :title (tr [:dialog :navigation-prompt :title])
    :actions [; Leave form and change service state back to validation
              (r/as-element
                [buttons/cancel
                 {:icon (ic/action-delete-forever)
                  :on-click #(e! (ts-controller/->BackToValidation service-id))}
                 (tr [:dialog :navigation-prompt :leave])])
              ; Return to form and do not cancel
              (r/as-element
                [buttons/save
                 {:on-click #(e! (ts-controller/->CloseCancelReValidateModal))}
                 (tr [:dialog :navigation-prompt :stay])])]}
   [:span (tr [:dialog :navigation-prompt :unsaved-validated-data])]])

(defn- open-validate-dialog [e! schemas]
  [ui/dialog
   {:open true
    :actionsContainerStyle style-dialog/dialog-action-container
    :title (tr [:transport-services-common-page :validation-modal-header])
    :actions [(r/as-element
                [buttons/cancel
                 {:on-click #(e! (ts-controller/->CancelSaveTransportService))}
                 (tr [:buttons :cancel])])
              (r/as-element
                [buttons/save
                 {:icon (ic/action-delete-forever)
                  :on-click #(e! (ts-controller/->SaveTransportService schemas true))}
                 (tr [:buttons :send])])]}
   [:span
    (tr [:transport-services-common-page :validation-modal-text])]])

(defn- render-public-state-buttons
  "If validate flag is enabled services must be saved in validate state. If not, mark them as public or draft."
  [e! schemas data name-missing?]
  (if (flags/enabled? :service-validation)
    ;; Flag enabled
    [:div {:style {:display "flex" :flex-direction "row" :flex-wrap "wrap"}}
     [:div {:style {:margin-top "1rem"}}
      [buttons/save-publish {:on-click #(e! (ts-controller/->ConfirmSaveTransportService schemas))
                             :disabled (not (form/can-save? data))}
       (tr [:buttons :save-and-validate])]]
     [:div {:style {:margin-top "1rem"}}
      [buttons/save-draft {:disabled name-missing?
                           :on-click #(do
                                        (.preventDefault %)
                                        (e! (ts-controller/->OpenChangeToDraftModal)))}
       (tr [:buttons :back-to-draft])]]
     [:div {:style {:margin-top "1rem"}}
      [buttons/cancel-with-icon {:on-click #(e! (ts-controller/->CancelTransportServiceForm false))}
       (tr [:buttons :discard])]]]

    ;; Flag not enabled - save as draft or publish
    [:div {:style {:display "flex" :flex-direction "row" :flex-wrap "wrap"}}
     [:div {:style {:margin-top "1rem"}}
      [buttons/save-publish {:on-click #(e! (ts-controller/->SaveTransportService schemas true))
                             :disabled (not (form/can-save? data))}
       (tr [:buttons :save-and-publish])]]
     [:div {:style {:margin-top "1rem"}}
      [buttons/save-draft {:disabled name-missing?
                           :on-click #(do
                                        (.preventDefault %)
                                        (e! (ts-controller/->SaveTransportService schemas false)))}
       (tr [:buttons :back-to-draft])]]
     [:div {:style {:margin-top "1rem"}}
      [buttons/cancel-with-icon {:on-click #(e! (ts-controller/->CancelTransportServiceForm false))}
       (tr [:buttons :discard])]]]))

(defn- render-draft-state-buttons [e! schemas data name-missing?]
  (if (flags/enabled? :service-validation)
    ;; Flag enabled, let user move services to validate
    [:div {:style {:display "flex" :flex-direction "row" :flex-wrap "wrap"}}
     [:div {:style {:margin-top "1rem"}}
      [buttons/save-publish {:on-click #(e! (ts-controller/->ConfirmSaveTransportService schemas))
                             :disabled (not (form/can-save? data))}
       (tr [:buttons :save-and-validate])]]
     [:div {:style {:margin-top "1rem"}}
      [buttons/save-draft {:on-click #(e! (ts-controller/->SaveTransportService schemas false))
                           :disabled name-missing?}
       (tr [:buttons :save-as-draft])]]
     [:div {:style {:margin-top "1rem"}}
      [buttons/cancel-with-icon {:on-click #(e! (ts-controller/->CancelTransportServiceForm false))}
       (tr [:buttons :discard])]]]

    ;; Flag is not enabled, so no chance to move service to validate
    [:div {:style {:display "flex" :flex-direction "row" :flex-wrap "wrap"}}
     [:div {:style {:margin-top "1rem"}}
      [buttons/save-publish {:on-click #(e! (ts-controller/->SaveTransportService schemas true))
                             :disabled (not (form/can-save? data))}
       (tr [:buttons :save-and-publish])]]
     [:div {:style {:margin-top "1rem"}}
      [buttons/save-draft {:on-click #(e! (ts-controller/->SaveTransportService schemas false))
                           :disabled name-missing?}
       (tr [:buttons :save-as-draft])]]
     [:div {:style {:margin-top "1rem"}}
      [buttons/cancel-with-icon {:on-click #(e! (ts-controller/->CancelTransportServiceForm false))}
       (tr [:buttons :discard])]]]))

(defn footer
  "Transport service form -footer element. All transport service form should be using this function."
  [e! {published ::t-service/published re-edit ::t-service/re-edit validate ::t-service/validate parent-id ::t-service/parent-id :as data} schemas in-validation? app]
  (let [name-missing? (str/blank? (::t-service/name data))
        service-id (get-in app [:transport-service ::t-service/id])
        service-id (if (nil? service-id) 0 service-id)
        show-footer? (if service-id
                       (ts-controller/is-service-owner? app)
                       true)
        service-state (ts-controller/service-state validate re-edit published (not (nil? parent-id)))
        show-validate-modal? (get-in app [:transport-service :show-confirm-save-dialog?])
        admin-validating-id (get-in app [:admin :in-validation :validating])
        cannot-be-saved-text (if (flags/enabled? :service-validation)
                               (tr [:form-help :validate-missing-required])
                               (tr [:form-help :publish-missing-required]))
        admin-unsaved-data (some #{:unsaved-data} (:before-unload-message app))
        service-key (t-service/service-key-by-type (get-in app [:transport-service ::t-service/type]))
        show-cancel-revalidate-modal? (get-in app [:transport-service :show-cancel-revalidate-dialog?])
        show-confirm-cancel-dialog? (get-in app [:transport-service :show-confirm-cancel-dialog?])
        modified? (not
                    (empty?
                      (get-in app [:transport-service service-key :ote.ui.form/modified])))]
    [:div (stylefy/use-style style-base/form-footer)
     [:div.container

      ;; Show brokering dialog
      [brokering-dialog e! app]

      ;; show-footer? - Take owner check away for now
      ;; If service is in-validation? (true), then do not show footer. It should be enabled first
      ;; But if service is-invalidation? (true) and admin validating points to same service, then show publish button only
      ;; And if :service-validation flag is not enabled, show buttons always
      (cond
        ;; service owner is editing
        (or (not (flags/enabled? :service-validation))
            (and (nil? in-validation?) (not= service-id admin-validating-id)))
        [:div
         (when (not (form/can-save? data))
           [:div (stylefy/use-style style-base/required-data-missing-container)
            [:div {:style {:display "inline-flex"}}
             [ic/alert-warning {:style {:margin-right "1rem" :color colors/red-dark}}]
             [:div {:style {:padding-top "2px"}} cannot-be-saved-text]]])
         [:div
          (case service-state
            :public [render-public-state-buttons e! schemas data name-missing?]
            :validation [:div {:style {:display "flex" :flex-direction "row" :flex-wrap "wrap"}}
                         [:div {:style {:margin-top "1rem"}}
                          [buttons/save-publish {:on-click #(e! (ts-controller/->ConfirmSaveTransportService schemas))
                                                 :disabled (not (form/can-save? data))}
                           (tr [:buttons :save-and-validate])]]
                         [:div {:style {:margin-top "1rem"}}
                          [buttons/save-draft {:on-click #(e! (ts-controller/->OpenChangeToDraftModal))
                                               :disabled name-missing?}
                           (tr [:buttons :back-to-draft])]]
                         [:div {:style {:margin-top "1rem"}}
                          [buttons/cancel-with-icon {:on-click #(e! (ts-controller/->CancelTransportServiceForm false))}
                           (tr [:buttons :discard])]]]
            :re-edit [:div {:style {:display "flex" :flex-direction "row" :flex-wrap "wrap"}}
                      [:div {:style {:margin-top "1rem"}}
                       [buttons/save-publish {:on-click #(e! (ts-controller/->ConfirmSaveTransportService schemas))
                                              :disabled (not (form/can-save? data))}
                        (tr [:buttons :save-and-validate])]]
                      [:div {:style {:margin-top "1rem"}}
                       [buttons/save-draft {:on-click #(e! (ts-controller/->OpenChangeToDraftModal))
                                            :disabled name-missing?}
                        (tr [:buttons :back-to-draft])]]
                      [:div {:style {:margin-top "1rem"}}
                       [buttons/cancel-with-icon {:on-click #(if modified?
                                                               (e! (ts-controller/->OpenCancelRevalidateModal))
                                                               (e! (ts-controller/->BackToValidation service-id)))}
                        (tr [:buttons :discard])]]]
            :draft [render-draft-state-buttons e! schemas data name-missing?])]]
        ;; admin is editing
        (and
          (= false in-validation?)
          (= service-id admin-validating-id))
        [:div
         [:div {:style {:display "flex" :flex-direction "row" :flex-wrap "wrap"}}
          [:div {:style {:margin-top "1rem"}}
           [buttons/save-publish {:on-click #(e! (ts-controller/->ConfirmSaveTransportService schemas))
                                  :disabled (not (form/can-save? data))}
            (tr [:buttons :save])]]

          ;; Admin cannot publish service with modifications
          [:div {:style {:margin-top "1rem"}}
           [buttons/save-publish {:on-click #(e! (admin-validation/->OpenConfirmPublishModal service-id))
                                  :disabled (when (or
                                                    (not (form/can-save? data))
                                                    admin-unsaved-data) true)}
            (tr [:buttons :publish])]]
          [:div {:style {:margin-top "1rem"}}
           [buttons/cancel-with-icon {:on-click #(e! (ts-controller/->CancelTransportServiceForm true))}
            (tr [:buttons :discard-to-admin])]]]]
        ;; In other cases - don't show any buttons
        :else nil)]

     (when show-validate-modal?
       [open-validate-dialog e! schemas])

     (when show-cancel-revalidate-modal?
       [open-cancel-revalidate-dialog e! service-id])

     (when show-confirm-cancel-dialog?
       [open-confirm-cancel-dialog e! schemas])

     [open-publish-dialog e! app]]))

(defn place-search-group [e! key in-validation?]
  (place-search/place-search-form-group
    (tuck/wrap-path e! :transport-service key ::t-service/operation-area)
    (tr [:field-labels :transport-service-common ::t-service/operation-area])
    ::t-service/operation-area
    in-validation?))

(defn service-hours-group [service-type sub-component? in-validation?]
  (let [tr* (tr-key [:field-labels :service-exception])
        write-time (fn [key]
                     (fn [{all-day? ::t-service/all-day :as data} time]
                       ;; Don't allow changing time if all-day checked
                       (if all-day?
                         data
                         (assoc data key time))))]
    (form/group
      (merge
        {:label (tr [:passenger-transportation-page :header-service-hours])
         :columns 3
         :card? false
         :sub-component sub-component?}
        (when (= false sub-component?)
          {:top-border true}))

      (merge
        {:name ::t-service/service-hours
         :id "service-hours-div-table"
         :type :div-table
         :div-class "col-xs-6 col-sm-4 col-md-2"
         :prepare-for-save values/without-empty-rows
         :table-fields
         [{:name ::t-service/week-days
           :label (tr [:field-labels :transport-service ::t-service/week-days])
           :type :multiselect-selection
           :disabled? in-validation?
           :field-class "col-xs-6 col-sm-4 col-md-4"
           :options t-service/days
           :show-option (tr-key [:enums ::t-service/day :full])
           :show-option-short (tr-key [:enums ::t-service/day :short])
           :required? true
           :full-width? true
           :is-empty? validation/empty-enum-dropdown?}
          {:name ::t-service/all-day
           :label (tr [:field-labels :transport-service ::t-service/all-day])
           :type :checkbox
           :disabled? in-validation?
           :style {:padding-top "2.5rem"}
           :field-class "col-xs-6 col-sm-2 col-md-2"
           :write (fn [data all-day?]
                    (merge data
                           {::t-service/all-day all-day?}
                           (if all-day?
                             {::t-service/from (time/->Time 0 0 nil)
                              ::t-service/to (time/->Time 24 0 nil)}
                             {::t-service/from nil
                              ::t-service/to nil})))}

          (merge
            {:name ::t-service/from
             :label (tr [:field-labels :transport-service ::t-service/from])
             :element-id "start-time"
             :wrapper-style style-form/input-element-wrapper-div
             :label-style style-form/input-element-label
             :type :time
             :disabled? in-validation?
             :container-style {:padding-top "1.5rem"}
             :field-class "col-xs-6 col-sm-2 col-md-2"
             :write (write-time ::t-service/from)
             :required? true
             :is-empty? time/empty-time?}
            (when (= "passenger-transportation" service-type)
              {:label (tr [:common-texts :start-time])}))
          (merge
            {:name ::t-service/to
             :label (tr [:field-labels :transport-service ::t-service/to])
             :element-id "end-time"
             :wrapper-style style-form/input-element-wrapper-div
             :label-style style-form/input-element-label
             :type :time
             :disabled? in-validation?
             :container-style {:padding-top "1.5rem"}
             :field-class "col-xs-6 col-sm-2 col-md-2"
             :write (write-time ::t-service/to)
             :required? true
             :is-empty? time/empty-time?}
            (when (= "passenger-transportation" service-type)
              {:label (tr [:common-texts :ending-time])}))]}
        (when-not in-validation?
          {:inner-delete? true
           :add-label (tr [:buttons :add-new-service-hour])
           :inner-delete-label (tr [:buttons :delete-service-hours])}))

      (form/subtitle (tr [:field-labels :service-exception :service-hour-exceptions]))

      (merge
        {:name ::t-service/service-exceptions
         :type :div-table
         :div-class "col-xs-6 col-sm-2 col-md-2"
         :prepare-for-save values/without-empty-rows
         :table-fields [{:name ::t-service/description
                         :label (tr* :description)
                         :type :localized-text
                         :disabled? in-validation?
                         :full-width? true
                         :field-class "col-xs-12 col-sm-6 col-md-6"}
                        {:name ::t-service/from-date
                         :type :date-picker
                         :disabled? in-validation?
                         :full-width? true
                         :label (tr* :from-date)
                         :element-id "service-exception-from-date"
                         :field-class "col-xs-6 col-sm-2 col-md-2"}
                        {:name ::t-service/to-date
                         :type :date-picker
                         :disabled? in-validation?
                         :full-width? true
                         :label (tr* :to-date)
                         :element-id "service-exception-to-date"
                         :field-class "col-xs-6 col-sm-2 col-md-2"}]}
        (when-not in-validation?
          {:inner-delete? true
           :inner-delete-label (tr [:buttons :delete-service-exceptions])
           :add-label (tr [:buttons :add-new-service-exception])}))

      {:name ::t-service/service-hours-info
       :label (tr [:field-labels :transport-service-common ::t-service/service-hours-info])
       :type :localized-text
       :disabled? in-validation?
       :full-width? true
       :container-class "col-xs-12"})))

(defn name-group [label in-validation?]
  (form/group
    {:label label
     :columns 3
     :layout :row
     :card? false
     :top-border true}

    {:type :info-toggle
     :name :name-group-info
     :label (tr [:common-texts :filling-info])
     :body [:div (tr [:form-help :name-info])]
     :default-state false
     :full-width? true
     :container-class "col-xs-12 col-sm-12 col-md-12"}

    {:name ::t-service/name
     :type :string
     :disabled? in-validation?
     :full-width? true
     :container-class "col-xs-12 col-sm-12 col-md-6"
     :required? true
     :max-length 200}

    {:name ::t-service/description
     :type :localized-text
     :disabled? in-validation?
     :rows 1
     :full-width? true
     :container-class "col-xs-12 col-sm-12 col-md-12"}

    (form/subtitle (tr [:field-labels :transport-service ::t-service/available-from-and-to-title]))

    {:type :info-toggle
     :name :available-info
     :label (tr [:common-texts :filling-info])
     :body [:div (tr [:form-help :available-from-and-to])]
     :default-state false
     :full-width? true
     :container-class "col-xs-12 col-sm-12 col-md-12"}

    {:name ::t-service/available-from
     :type :date-picker
     :disabled? in-validation?
     :show-clear? true
     :full-width? true
     :hint-text (tr [:field-labels :transport-service ::t-service/available-from-nil])
     :container-class "col-xs-12 col-sm-6 col-md-3"}
    {:name ::t-service/available-to
     :type :date-picker
     :disabled? in-validation?
     :show-clear? true
     :full-width? true
     :hint-text (tr [:field-labels :transport-service ::t-service/available-to-nil])
     :container-class "col-xs-12 col-sm-6 col-md-3"}))

(defn transport-type [sub-type in-validation?]
  (form/group
    {:label (tr [:field-labels :transport-service-common ::t-service/transport-type])
     :columns 3
     :layout :row
     :card? false
     :top-border true}

    (when (not= sub-type :taxi)
      {:type :info-toggle
       :name :transport-type-info
       :label (tr [:common-texts :filling-info])
       :body [:div (tr [:form-help :transport-type-info])]
       :default-state false
       :full-width? true
       :container-class "col-xs-12 col-sm-12 col-md-12"})

    {:name ::t-service/transport-type
     :type :checkbox-group
     :disabled? in-validation?
     :container-class "col-md-12"
     :header? false
     :required? true
     :options t-service/transport-type
     :show-option (tr-key [:enums ::t-service/transport-type])
     :option-enabled? (fn [option]
                        (if (= sub-type :taxi)
                          false
                          true))}))

(defn place-search-dirty-event [e!]
  ;; To set transport service form dirty when adding / removing places using the place-search component,
  ;; we'll have to manually trigger EditTransportService event with empty data.
  #(do
     (e! (ts-controller/->EditTransportService {}))
     (e! %)))
