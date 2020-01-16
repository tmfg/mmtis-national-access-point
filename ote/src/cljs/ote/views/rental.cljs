(ns ote.views.rental
  "Vuokrauspalvelujen jatkotietojen lomakenäkymä - Laajentaa perustietonäkymää vain
  Vuokraus- ja yhteiskäyttöpalveluille"
  (:require [reagent.core :as reagent]
            [cljs-react-material-ui.reagent :as ui]
            [ote.db.transport-service :as t-service]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key tr-tree]]
            [ote.util.values :as values]
            [ote.style.dialog :as style-dialog]
            [ote.ui.form :as form]
            [ote.ui.buttons :as buttons]
            [ote.app.controller.common :as common-c]
            [ote.app.controller.transport-service :as ts-controller]
            [ote.views.transport-service-common :as ts-common])
  (:require-macros [reagent.core :refer [with-let]]))

(defn rental-form-options [e! schemas in-validation? app]
  {:name->label (tr-key [:field-labels :rentals]
                        [:field-labels :transport-service]
                        [:field-labels :transport-service-common]
                        [:field-labels])
   :update!     #(e! (ts-controller/->EditTransportService %))
   :footer-fn   (fn [data]
                  [ts-common/footer e! data schemas in-validation? app])})

(defn price-group [in-validation?]
  (form/group
    {:label (tr [:rentals-page :header-service-info])
     :columns 3
     :card? false
     :top-border false}

    {:type :info-toggle
     :name :pricing-group-info
     :label (tr [:common-texts :filling-info])
     :body [:div (tr [:field-labels :rentals :rentals-pricing-info])]
     :default-state false
     :full-width? true
     :container-class "col-xs-12 col-sm-12 col-md-12"}

    (merge
      {:name ::t-service/price-classes
       :type :div-table
       :container-class "col-xs-12 col-sm-12 col-md-12"
       :table-fields [{:name ::t-service/price-per-unit
                       :label (tr [:field-labels :rentals ::t-service/price-per-unit])
                       :type :number
                       :disabled? in-validation?
                       :currency? true
                       :style {:width "100px"}
                       :input-style {:text-align "right" :padding-right "5px"}
                       :field-class "col-xs-12 col-sm-4 col-md-4"
                       :required? true}

                      {:name ::t-service/unit
                       :label (tr [:field-labels :rentals ::t-service/unit])
                       :type :string
                       :disabled? in-validation?
                       :field-class "col-xs-12 col-sm-4 col-md-4"
                       :required? true
                       :max-length 128}]}
      (when-not in-validation?
        {:delete? true
         :add-label (tr [:buttons :add-new-price-class])}))))

(defn price-classes [update-form! data in-validation?]
  (reagent/with-let [open? (reagent/atom false)]
    [:div
     [ui/flat-button {:label (tr [:rentals-page :open-rental-prices])
                      :on-click #(reset! open? true)}]
     [ui/dialog
      {:open @open?
       :actionsContainerStyle style-dialog/dialog-action-container
       :auto-scroll-body-content true
       :title (tr [:price-dialog :header-dialog])
       :actions [(reagent/as-element
                   [ui/flat-button {:label (tr [:buttons :close])
                                    :on-click #(reset! open? false)}])]}
      [form/form {:update! update-form!
                  :name->label (tr-key [:field-labels :rentals]
                                       [:field-labels :transport-service]
                                       [:field-labels])}
       [(assoc-in (price-group in-validation?) [:options :card?] false)]
       data]]]))

(defn vehicle-group [in-validation?]
  (form/group
    {:label (tr [:rentals-page :header-vehicles])
     :columns 3
     :card? false
     :top-border true}

    (merge
      {:name ::t-service/vehicle-classes
       :type :div-table
       :add-divider? true
       :table-fields [{:name ::t-service/vehicle-type
                       :label (tr [:field-labels :rentals ::t-service/vehicle-type])
                       :type :string
                       :disabled? in-validation?
                       :full-width? true
                       :field-class "col-xs-12 col-sm-6 col-md-3"}
                      {:name ::t-service/license-required
                       :label (tr [:field-labels :rentals ::t-service/license-required])
                       :type :string
                       :disabled? in-validation?
                       :full-width? true
                       :field-class "col-xs-12 col-sm-6 col-md-3"}
                      {:name ::t-service/minimum-age
                       :label (tr [:field-labels :rentals ::t-service/minimum-age])
                       :type :number
                       :full-width? true
                       :disabled? in-validation?
                       :field-class "col-xs-12 col-sm-6 col-md-2"}
                      {:name :price-group
                       :type :component
                       :field-class "col-xs-12 col-sm-6 col-md-2"
                       :component (fn [{:keys [update-form! data]}]
                                    [price-classes update-form! data in-validation?])}]}
      (when-not in-validation?
        {:inner-delete? true
         :inner-delete-class "col-xs-12 col-sm-3 col-md-2"
         :inner-delete-label (tr [:buttons :delete])
         :add-label (tr [:buttons :add-new-vehicle])}))

    {:name ::t-service/vehicle-price-url
    :type :string
    :disabled? in-validation?
    :full-width? true
    :container-class "col-xs-12 col-sm-12 col-md-6 col-lg-6"}))

(defn accessibility-group [in-validation?]
  (form/group
    {:label (tr [:rentals-page :header-accessibility])
     :columns 2
     :layout :row
     :card? false
     :top-border true}

    {:name ::t-service/guaranteed-vehicle-accessibility
     :help (tr [:form-help :guaranteed-vehicle-accessibility])
     :type :checkbox-group
     :disabled? in-validation?
     :show-option (tr-key [:enums ::t-service/vehicle-accessibility])
     :options t-service/rental-vehicle-accessibility
     :full-width? true
     :container-class "col-xs-12 col-sm-12 col-md-6 col-lg-6"}

    {:name ::t-service/limited-vehicle-accessibility
     :help (tr [:form-help :limited-vehicle-accessibility])
     :type :checkbox-group
     :disabled? in-validation?
     :show-option (tr-key [:enums ::t-service/vehicle-accessibility])
     :options t-service/rental-vehicle-accessibility
     :full-width? true
     :container-class "col-xs-12 col-sm-12 col-md-6 col-lg-6"}

    {:name ::t-service/guaranteed-transportable-aid
     :type :checkbox-group
     :disabled? in-validation?
     :show-option (tr-key [:enums ::t-service/transportable-aid])
     :options t-service/rental-transportable-aid
     :full-width? true
     :container-class "col-xs-12 col-sm-12 col-md-6 col-lg-6"}

    {:name ::t-service/limited-transportable-aid
     :type :checkbox-group
     :disabled? in-validation?
     :show-option (tr-key [:enums ::t-service/transportable-aid])
     :options t-service/rental-transportable-aid
     :full-width? true
     :container-class "col-xs-12 col-sm-12 col-md-6 col-lg-6"}

    {:name ::t-service/guaranteed-accessibility-description
     :type :localized-text
     :disabled? in-validation?
     :floatingLabelStyle {:line-height "1rem"}
     :rows 1
     :full-width? true
     :container-class "col-xs-12 col-sm-12 col-md-6 col-lg-6"}

    {:name ::t-service/limited-accessibility-description
     :type :localized-text
     :disabled? in-validation?
     :floatingLabelStyle {:line-height "1rem"}
     :rows 1
     :container-class "col-xs-12 col-sm-12 col-md-6 col-lg-6"
     :full-width? true}

    {:name ::t-service/accessibility-info-url
     :type :string
     :disabled? in-validation?
     :container-class "col-xs-12 col-sm-12 col-md-6 col-lg-6"
     :full-width? true
     :max-length 200}))

(defn additional-services [in-validation?]
  (form/group
    {:label (tr [:rentals-page :header-additional-services])
     :columns 3
     :card? false
     :top-border true}

    (merge
      {:name ::t-service/rental-additional-services
       :type :div-table
       :table-fields [{:name ::t-service/additional-service-type
                       :label (tr [:field-labels :rentals ::t-service/additional-service-type])
                       :type :selection
                       :disabled? in-validation?
                       :field-class "col-xs-12 col-sm-6 col-md-3"
                       :show-option (tr-key [:enums ::t-service/additional-services])
                       :options t-service/additional-services
                       :required? true}

                      {:name ::t-service/additional-service-price
                       :label (tr [:field-labels :rentals ::t-service/additional-service-price])
                       :type :number
                       :disabled? in-validation?
                       :field-class "col-xs-12 col-sm-6 col-md-3"
                       :currency? true
                       :style {:width "100px"}
                       :input-style {:text-align "right" :padding-right "5px"}
                       :read (comp ::t-service/price-per-unit ::t-service/additional-service-price)
                       :write #(assoc-in %1 [::t-service/additional-service-price ::t-service/price-per-unit] %2)
                       :required? true}

                      {:name ::t-service/additional-service-unit
                       :label (tr [:field-labels :rentals ::t-service/additional-service-unit])
                       :type :string
                       :disabled? in-validation?
                       :field-class "col-xs-12 col-sm-6 col-md-3"
                       :read (comp ::t-service/unit ::t-service/additional-service-price)
                       :write #(assoc-in %1 [::t-service/additional-service-price ::t-service/unit] %2)}]}
      (when-not in-validation?
        {:inner-delete? true
         :inner-delete-class "col-xs-12 col-sm-3 col-md-3"
         :inner-delete-label (tr [:buttons :delete])
         :add-label (tr [:buttons :add-new-additional-service])}))))

(defn luggage-restrictions-groups [in-validation?]
  (form/group
    {:label (tr [:rentals-page :header-restrictions-payments])
     :columns 2
     :layout :row
     :card? false
     :top-border true}

   {:name ::t-service/luggage-restrictions
    :type :localized-text
    :disabled? in-validation?
    :rows 1
    :container-class "col-xs-12 col-sm-12 col-md-6 col-lg-6"
    :full-width?  true}

    {:name ::t-service/payment-methods
     :type :multiselect-selection
     :disabled? in-validation?
     :show-option (tr-key [:enums ::t-service/payment-methods])
     :options t-service/payment-methods
     :container-class "col-xs-12 col-sm-12 col-md-6 col-lg-6"
     :style {:margin-bottom "2rem"}
     :full-width? true})
  )

(defn usage-area [in-validation?]
  (form/group
    {:label (tr [:rentals-page :header-usage-area])
     :columns 1
     :layout :row
     :card? false
     :top-border true}

   {:name ::t-service/usage-area
    :type :string
    :disabled? in-validation?
    :full-width? true
    :container-class "col-xs-12 col-sm-12 col-md-6 col-lg-6"
    :rows 1}))

(defn service-hours-for-location [update-form! data in-validation?]
  [form/form {:update! update-form!
              :name->label (tr-key [:field-labels :rentals]
                                   [:field-labels :transport-service]
                                   [:field-labels])}
   [(ts-common/service-hours-group "rental" true in-validation?)]
   data])

(defn service-hour-form-element
  "Not used currently: Define immutable function for service hours (and exceptions) so that reagent won't re-render the whole component in every
  keystroke. This had to be made to make :localized-text element to work inside [service-hours-for-location] component."
  [{:keys [update-form! data] :as component-content}]
  [service-hours-for-location update-form! data])

(defn modal-service-hours-for-location
  "Add pick up locations service hours and exceptions to modal."
  [update-form! data in-validation?]
  (reagent/with-let [open? (reagent/atom false)]
    [:div
     [buttons/open-dialog-row {:on-click #(reset! open? true)}
      (tr [:rentals-page :open-service-hours-and-exceptions])]
     [ui/dialog
      {:open @open?
       :actionsContainerStyle style-dialog/dialog-action-container
       :auto-scroll-body-content true
       :title (tr [:opening-hours-dialog :header-dialog])
       :actions [(reagent/as-element
                   [ui/flat-button {:label (tr [:buttons :close])
                                    :on-click #(reset! open? false)}])]}
      [form/form {:update! update-form!
                  :name->label (tr-key [:field-labels :rentals]
                                       [:field-labels :transport-service]
                                       [:field-labels])}
       [(ts-common/service-hours-group "rental" true in-validation?)]
       data]]]))

(defn pick-up-locations [in-validation?]
  (form/group
    {:label (tr [:passenger-transportation-page :header-pick-up-locations])
     :columns 1
     :card? false
     :top-border true}

    (merge
      {:name ::t-service/pick-up-locations
       :id "pick-up-locations-div-table"
       :type :div-table
       :div-class "col-xs-12 col-sm-6 col-md-4"
       :add-divider? true
       :prepare-for-save values/without-empty-rows
       :table-fields [{:name ::t-service/pick-up-name
                       :label (tr [:field-labels :rentals ::t-service/pick-up-name])
                       :full-width? true
                       :type :string
                       :disabled? in-validation?
                       :required? true}
                      {:name ::t-service/pick-up-type
                       :label (tr [:field-labels :rentals ::t-service/pick-up-type])
                       :type :selection
                       :disabled? in-validation?
                       :full-width? true
                       :auto-width? true
                       :style {:width "100%"}
                       :options t-service/pick-up-types
                       :show-option (tr-key [:enums ::t-service/pick-up-type])
                       :required? true}
                      {:name ::common/street
                       :label (tr [:field-labels ::common/street])
                       :full-width? true
                       :type :string
                       :disabled? in-validation?
                       :read (comp ::common/street ::t-service/pick-up-address)
                       :write #(assoc-in %1 [::t-service/pick-up-address ::common/street] %2)}
                      {:name ::common/postal_code
                       :label (tr [:field-labels ::common/postal_code])
                       :type :string
                       :disabled? in-validation?
                       :full-width? true
                       :validate [[:every-postal-code]]
                       :read (comp ::common/postal_code ::t-service/pick-up-address)
                       :write #(assoc-in %1 [::t-service/pick-up-address ::common/postal_code] %2)}
                      {:name ::common/post_office
                       :label (tr [:field-labels ::common/post_office])
                       :full-width? true
                       :type :string
                       :disabled? in-validation?
                       :read (comp ::common/post_office ::t-service/pick-up-address)
                       :write #(assoc-in %1 [::t-service/pick-up-address ::common/post_office] %2)}
                      {:name :country
                       :label (tr [:common-texts :country])
                       :full-width? true
                       :auto-width? true
                       :type :selection
                       :disabled? in-validation?
                       :show-option (tr-key [:country-list])
                       :options (common-c/country-list (tr-tree [:country-list]))
                       :read (comp ::common/country_code ::t-service/pick-up-address)
                       :write #(assoc-in %1 [::t-service/pick-up-address ::common/country_code] %2)}
                      {:name ::t-service/service-hours-and-exceptions
                       :type :component
                       :component (fn [{:keys [update-form! data]}]
                                    [modal-service-hours-for-location update-form! data in-validation?])}]}
      (when-not in-validation?
        {:delete? true
         :delete-label (tr [:buttons :delete-pick-up-location])
         :add-label (tr [:buttons :add-new-pick-up-location])}))

    {:type :info-toggle
     :name :pricing-group-info
     :label (tr [:common-texts :filling-info])
     :body [:div (tr [:form-help :pick-up-locations-url])]
     :default-state false
     :full-width? true
     :container-class "col-xs-12 col-sm-12 col-md-12"}

    {:name ::t-service/pick-up-locations-url
     :type :string
     :disabled? in-validation?
     :container-class "col-xs-12 col-sm-12 col-md-6"
     :full-width? true}))

(defn rental [e! {form-data ::t-service/rentals :as service} app]
  (let [validate (::t-service/validate form-data)
        service-id (::t-service/id service)
        admin-validating-id (get-in app [:admin :in-validation :validating])
        in-validation? (ts-controller/in-readonly? validate admin-validating-id service-id)
             groups [(ts-common/transport-type ::t-service/rentals in-validation?)
                     (ts-common/name-group (tr [:rentals-page :header-service-info]) in-validation?)
                     (ts-common/contact-info-group in-validation?)
                     (ts-common/place-search-group (ts-common/place-search-dirty-event e!) ::t-service/rentals in-validation?)
                     (ts-common/external-interfaces e!
                                                    (get service ::t-service/type)
                                                    (get service ::t-service/sub-type)
                                                    (get-in service [::t-service/passenger-transportation ::t-service/transport-type])
                                                    in-validation?)
                     (vehicle-group in-validation?)
                     (luggage-restrictions-groups in-validation?)
                     (accessibility-group in-validation?)
                     (additional-services in-validation?)
                     (usage-area in-validation?)
                     (ts-common/service-url "real-time-information-url"
                                            (tr [:field-labels :rentals ::t-service/real-time-information])
                                            ::t-service/real-time-information
                                            (tr [:form-help :real-time-info])
                                            in-validation?)
                     (ts-common/advance-reservation-group in-validation?)
                     (ts-common/service-url "booking-service-url"
                                            (tr [:field-labels :transport-service-common ::t-service/booking-service])
                                            ::t-service/booking-service
                                            nil
                                            in-validation?)
                     (pick-up-locations in-validation?)]
                     options (rental-form-options e! groups in-validation? app)]
    [:div.row
     [form/form options groups form-data]]))
