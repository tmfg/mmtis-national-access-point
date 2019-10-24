(ns ote.views.rental
  "Vuokrauspalvelujen jatkotietojen lomakenäkymä - Laajentaa perustietonäkymää vain
  Vuokraus- ja yhteiskäyttöpalveluille"
  (:require [reagent.core :as reagent]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.ui.buttons :as buttons]
            [ote.app.controller.transport-service :as ts]
            [ote.db.transport-service :as t-service]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key tr-tree]]
            [ote.views.place-search :as place-search]
            [tuck.core :as tuck]
            [ote.views.transport-service-common :as ts-common]
            [ote.time :as time]
            [ote.style.form :as style-form]
            [ote.style.dialog :as style-dialog]
            [ote.util.values :as values]
            [ote.ui.info :as info]
            [ote.ui.validation :as validation]
            [ote.app.controller.common :as common-c])
  (:require-macros [reagent.core :refer [with-let]]))

(defn rental-form-options [e! schemas app]
  {:name->label (tr-key [:field-labels :rentals]
                        [:field-labels :transport-service]
                        [:field-labels :transport-service-common]
                        [:field-labels])
   :update!     #(e! (ts/->EditTransportService %))
   :footer-fn   (fn [data]
                  [ts-common/footer e! data schemas app])})

(defn price-group []
  (form/group
    {:label (tr [:rentals-page :header-service-info])
     :columns 3
     :layout :row
     :card? false
     :top-border true}

   (form/info
    (tr [:field-labels :rentals :rentals-pricing-info]))

   {:name ::t-service/price-classes
    :type :table
    :table-fields [{:name ::t-service/price-per-unit
                    :type :number
                    :currency? true
                    :style {:width "100px"}
                    :input-style {:text-align "right" :padding-right "5px"}
                    :required? true}

                   {:name ::t-service/unit
                    :type :string
                    :required? true
                    :max-length 128}]
    :delete? true
    :add-label (tr [:buttons :add-new-price-class])}))

(defn price-classes [update-form! data]
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
       [(assoc-in (price-group) [:options :card?] false)]
       data]]]))

(defn vehicle-group []
  (form/group
    {:label (tr [:rentals-page :header-vehicles])
     :columns 1
     :layout :row
     :card? false
     :top-border true}

   {:name ::t-service/vehicle-classes
    :type :table
    :table-fields [{:name ::t-service/vehicle-type
                    :type :string}
                   {:name ::t-service/license-required
                    :type :string}
                   {:name ::t-service/minimum-age
                    :type :number}
                   {:name :price-group
                    :type :component
                    :component (fn [{:keys [update-form! data]}]
                                 [price-classes update-form! data])}]
    :delete? true
    :add-label (tr [:buttons :add-new-vehicle])}
   {:name ::t-service/vehicle-price-url
    :type :string
    :full-width? true
    :container-class "col-xs-12 col-sm-12 col-md-6 col-lg-6"})
  )

(defn accessibility-group []
  (form/group
    {:label (tr [:rentals-page :header-accessibility])
     :columns 2
     :layout :row
     :card? false
     :top-border true}

    {:name        ::t-service/guaranteed-vehicle-accessibility
    :help (tr [:form-help :guaranteed-vehicle-accessibility])
    :type        :checkbox-group
    :show-option (tr-key [:enums ::t-service/vehicle-accessibility])
    :options     t-service/rental-vehicle-accessibility
    :full-width? true
     :container-class "col-xs-12 col-sm-12 col-md-6 col-lg-6"}

    {:name        ::t-service/limited-vehicle-accessibility
    :help (tr [:form-help :limited-vehicle-accessibility])
    :type        :checkbox-group
    :show-option (tr-key [:enums ::t-service/vehicle-accessibility])
    :options     t-service/rental-vehicle-accessibility
    :full-width? true
     :container-class "col-xs-12 col-sm-12 col-md-6 col-lg-6"}

    {:name ::t-service/guaranteed-transportable-aid
    :type :checkbox-group
    :show-option (tr-key [:enums ::t-service/transportable-aid])
    :options t-service/rental-transportable-aid
    :full-width? true
     :container-class "col-xs-12 col-sm-12 col-md-6 col-lg-6"}

    {:name ::t-service/limited-transportable-aid
    :type :checkbox-group
    :show-option (tr-key [:enums ::t-service/transportable-aid])
    :options t-service/rental-transportable-aid
    :full-width? true
     :container-class "col-xs-12 col-sm-12 col-md-6 col-lg-6"}

    {:name            ::t-service/guaranteed-accessibility-description
     :type            :localized-text
     :rows            1
     :full-width?     true
     :container-class "col-xs-12 col-sm-12 col-md-6 col-lg-6"}

    {:name ::t-service/limited-accessibility-description
     :type :localized-text
     :rows 1
     :container-class "col-xs-12 col-sm-12 col-md-6 col-lg-6"
     :full-width? true}

    {:name ::t-service/accessibility-info-url
    :type :string
     :container-class "col-xs-12 col-sm-12 col-md-6 col-lg-6"
    :full-width? true
    :max-length 200}))

(defn additional-services []
  (form/group
    {:label (tr [:rentals-page :header-additional-services])
     :columns 3
     :layout :row
     :card? false
     :top-border true}

   {:name ::t-service/rental-additional-services
    :type :table
    :table-fields [{:name ::t-service/additional-service-type
                    :type        :selection
                    :show-option (tr-key [:enums ::t-service/additional-services])
                    :options     t-service/additional-services
                    :required? true}

                   {:name ::t-service/additional-service-price
                    :type :number
                    :currency? true
                    :style {:width "100px"}
                    :input-style {:text-align "right" :padding-right "5px"}
                    :read (comp ::t-service/price-per-unit ::t-service/additional-service-price)
                    :write #(assoc-in %1 [::t-service/additional-service-price ::t-service/price-per-unit] %2)
                    :required? true}

                   {:name ::t-service/additional-service-unit
                    :type :string
                    :read (comp ::t-service/unit ::t-service/additional-service-price)
                    :write #(assoc-in %1 [::t-service/additional-service-price ::t-service/unit] %2)}]
    :delete? true
    :add-label (tr [:buttons :add-new-additional-service])}))

(defn luggage-restrictions-groups []
  (form/group
    {:label (tr [:rentals-page :header-restrictions-payments])
     :columns 2
     :layout :row
     :card? false
     :top-border true}

   {:name ::t-service/luggage-restrictions
    :type :localized-text
    :rows 1
    :container-class "col-xs-12 col-sm-12 col-md-6 col-lg-6"
    :full-width?  true}

    {:name ::t-service/payment-methods
     :type :multiselect-selection
     :show-option (tr-key [:enums ::t-service/payment-methods])
     :options t-service/payment-methods
     :container-class "col-xs-12 col-sm-12 col-md-6 col-lg-6"
     :style {:margin-bottom "2rem"}
     :full-width? true})
  )

(defn usage-area []
  (form/group
    {:label (tr [:rentals-page :header-usage-area])
     :columns 1
     :layout :row
     :card? false
     :top-border true}

   {:name ::t-service/usage-area
    :type :string
    :full-width? true
    :container-class "col-xs-12 col-sm-12 col-md-6 col-lg-6"
    :rows 1}))

(defn service-hours-for-location [update-form! data]
  [form/form {:update! update-form!
              :name->label (tr-key [:field-labels :rentals]
                                   [:field-labels :transport-service]
                                   [:field-labels])}
   [(ts-common/service-hours-group "rental")]
   data])

(defn service-hour-form-element
  "Define immutable function for service hours (and exceptions) so that reagent won't re-render the whole component in every
  keystroke. This had to be made to make :localized-text element to work inside [service-hours-for-location] component."
  [{:keys [update-form! data] :as component-content}]
  [service-hours-for-location update-form! data])

(defn pick-up-locations []
  (form/group
    {:label (tr [:passenger-transportation-page :header-pick-up-locations])
     :columns 1
     :card? false
     :top-border true}

    {:name ::t-service/pick-up-locations
     :id "pick-up-locations-div-table"
     :type :div-table
     :div-class "col-xs-12 col-sm-6 col-md-4"
     :delete? true
     :delete-label (tr [:buttons :delete-pick-up-location])
     :add-label (tr [:buttons :add-new-pick-up-location])
     :add-divider? true
     :prepare-for-save values/without-empty-rows
     :table-fields [{:name ::t-service/pick-up-name
                     :label (tr [:field-labels :rentals ::t-service/pick-up-name])
                     :full-width? true
                     :type :string
                     :required? true}
                    {:name ::t-service/pick-up-type
                     :label (tr [:field-labels :rentals ::t-service/pick-up-type])
                     :type :selection
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
                     :read (comp ::common/street ::t-service/pick-up-address)
                     :write #(assoc-in %1 [::t-service/pick-up-address ::common/street] %2)}
                    {:name ::common/postal_code
                     :label (tr [:field-labels ::common/postal_code])
                     :type :string
                     :full-width? true
                     :validate [[:every-postal-code]]
                     :read (comp ::common/postal_code ::t-service/pick-up-address)
                     :write #(assoc-in %1 [::t-service/pick-up-address ::common/postal_code] %2)}
                    {:name ::common/post_office
                     :label (tr [:field-labels ::common/post_office])
                     :full-width? true
                     :type :string
                     :read (comp ::common/post_office ::t-service/pick-up-address)
                     :write #(assoc-in %1 [::t-service/pick-up-address ::common/post_office] %2)}
                    {:name :country
                     :label (tr [:common-texts :country])
                     :full-width? true
                     :type :selection
                     :show-option (tr-key [:country-list])
                     :options (common-c/country-list (tr-tree [:country-list]))
                     :read (comp ::common/country_code ::t-service/pick-up-address)
                     :write #(assoc-in %1 [::t-service/pick-up-address ::common/country_code] %2)}
                    {:name ::t-service/service-hours-and-exceptions
                     :type :component
                     :component-type :inner-row
                     :component service-hour-form-element}]}

    (form/info (tr [:form-help :pick-up-locations-url]))

    {:name ::t-service/pick-up-locations-url
     :type :string
     :container-class "col-xs-12 col-sm-12 col-md-6"
     :full-width? true}))

(defn rental [e! service app]
  (with-let [groups [(ts-common/transport-type ::t-service/rentals)
                     (ts-common/name-group (tr [:rentals-page :header-service-info]))
                     (ts-common/contact-info-group)
                     (ts-common/place-search-group (ts-common/place-search-dirty-event e!) ::t-service/rentals)
                     (ts-common/external-interfaces e!)
                     (vehicle-group)
                     (luggage-restrictions-groups)
                     (accessibility-group)
                     (additional-services)
                     (usage-area)
                     (ts-common/service-url "real-time-information-url"
                      (tr [:field-labels :rentals ::t-service/real-time-information])
                      ::t-service/real-time-information
                      (tr [:form-help :real-time-info]))
                     (ts-common/advance-reservation-group)
                     (ts-common/service-url "booking-service-url"
                      (tr [:field-labels :transport-service-common ::t-service/booking-service])
                      ::t-service/booking-service)
                     (pick-up-locations)]
                     options (rental-form-options e! groups app)]
    [:div.row
     [form/form options groups (get service ::t-service/rentals)]]))
