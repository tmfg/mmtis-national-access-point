 (ns ote.views.passenger-transportation
  "Required datas for passenger transportation provider"
  (:require [reagent.core :as reagent]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.ui.buttons :as buttons]
            [ote.app.controller.transport-service :as ts]
            [ote.db.transport-service :as t-service]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]
            [ote.views.place-search :as place-search]
            [tuck.core :as tuck]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]
            [ote.views.transport-service-common :as ts-common]
            [ote.time :as time]
            [ote.style.form :as style-form])
  (:require-macros [reagent.core :refer [with-let]]))



(defn transportation-form-options [e!]
  {:name->label (tr-key [:field-labels :passenger-transportation] [:field-labels :transport-service-common] [:field-labels :transport-service])
   :update!     #(e! (ts/->EditTransportService %))
   :name        #(tr [:olennaiset-tiedot :otsikot %])
   :footer-fn   (fn [data]
                  [ts-common/footer e! data])})

(defn name-and-type-group [e!]
  (form/group
   {:label (tr [:passenger-transportation-page :header-service-info])
    :columns 3
    :layout :row}

   {:name ::t-service/name
    :type :string
    :required? true}

   {:style style-base/long-drowpdown ;; Pass only style from stylefy base
    :name ::t-service/sub-type
    :type        :selection
    :show-option (tr-key [:enums :ote.db.transport-service/sub-type])
    :options     t-service/passenger-transportation-sub-types
    :required? true}))



(defn luggage-restrictions-group []
  (form/group
   {:label (tr [:passenger-transportation-page :header-restrictions-payments])
    :columns 3
    :layout :row}

   {:name ::t-service/luggage-restrictions
    :type :localized-text
    :rows 1 :max-rows 5}

   {:name        ::t-service/payment-methods
    :type        :multiselect-selection
    :show-option (tr-key [:enums ::t-service/payment-methods])
    :options     t-service/payment-methods}))



(defn accessibility-group []
  (form/group
   {:label (tr [:passenger-transportation-page :header-other-services-and-accessibility])
    :columns 3
    :layout :row}

   {:name        ::t-service/guaranteed-vehicle-accessibility
    :help (tr [:form-help :guaranteed-vehicle-accessibility])
    :type        :checkbox-group
    :show-option (tr-key [:enums ::t-service/vehicle-accessibility])
    :options     t-service/vehicle-accessibility
    :full-width? true
    :container-style (merge style-form/half-width
                            style-form/border-right)}

   {:name        ::t-service/limited-vehicle-accessibility
    :help (tr [:form-help :limited-vehicle-accessibility])
    :type        :checkbox-group
    :show-option (tr-key [:enums ::t-service/vehicle-accessibility])
    :options     t-service/vehicle-accessibility
    :full-width? true
    :container-style style-form/half-width}

   {:name ::t-service/guaranteed-info-service-accessibility
    :type :checkbox-group
    :show-option (tr-key [:enums ::t-service/information-service-accessibility])
    :options t-service/information-service-accessibility
    :full-width? true
    :container-style (merge style-form/half-width
                            style-form/border-right)}

   {:name ::t-service/limited-info-service-accessibility
    :type :checkbox-group
    :show-option (tr-key [:enums ::t-service/information-service-accessibility])
    :options t-service/information-service-accessibility
    :full-width? true
    :container-style style-form/half-width}

   {:name ::t-service/guaranteed-transportable-aid
    :type :checkbox-group
    :show-option (tr-key [:enums ::t-service/transportable-aid])
    :options t-service/transportable-aid
    :full-width? true
    :container-style (merge style-form/half-width
                            style-form/border-right)}

   {:name ::t-service/limited-transportable-aid
    :type :checkbox-group
    :show-option (tr-key [:enums ::t-service/transportable-aid])
    :options t-service/transportable-aid
    :full-width? true
    :container-style style-form/half-width}

   {:name ::t-service/guaranteed-accessibility-description
    :type :localized-text
    :rows 1 :max-rows 5
    :full-width? true
    :container-style style-form/half-width}

   {:name ::t-service/limited-accessibility-description
    :type :localized-text
    :rows 1 :max-rows 5
    :container-style style-form/half-width
    :full-width? true}

   {:name ::t-service/accessibility-info-url
    :type :string
    :container-style style-form/half-width
    :full-width? true}

   {:name        ::t-service/additional-services
    :type        :multiselect-selection
    :show-option (tr-key [:enums ::t-service/additional-services])
    :options     t-service/additional-services
    :container-style style-form/half-width
    :full-width? true}
   ))

(defn pricing-group [e!]
  (form/group
   {:label (tr [:passenger-transportation-page :header-price-information])
    :columns 3
    :layout :row}

   {:name         ::t-service/price-classes
    :type         :table
    :table-fields [{:name ::t-service/name :type :string
                    :label (tr [:field-labels :passenger-transportation ::t-service/price-class-name])}
                   {:name ::t-service/price-per-unit :type :number}
                   {:name ::t-service/unit :type :string}
                   {:name ::t-service/currency :type :string :width "100px"}]
    :add-label (tr [:buttons :add-new-price-class])
    :delete?      true}

   {:name ::t-service/pricing-description
    :type :localized-text
    :write #(assoc-in %1 [::t-service/pricing ::t-service/description] %2)
    :read (comp ::t-service/description ::t-service/pricing)
    :columns 1}

   {:name ::t-service/pricing-url
    :type :string
    :write #(assoc-in %1 [::t-service/pricing ::t-service/url] %2)
    :read (comp ::t-service/url ::t-service/pricing)
    :columns 1}))



(defn passenger-transportation-info [e! {form-data ::t-service/passenger-transportation}]
  (with-let [form-options (transportation-form-options e!)
             form-groups
             (remove nil?
                     [(name-and-type-group e!)
                      (ts-common/contact-info-group)
                      (when (= (::t-service/sub-type form-data) :schedule)
                        (ts-common/companies-group))
                      (ts-common/place-search-group e! ::t-service/passenger-transportation)
                      (ts-common/external-interfaces)
                      (luggage-restrictions-group)
                      (ts-common/service-url
                       (tr [:field-labels :passenger-transportation ::t-service/real-time-information])
                       ::t-service/real-time-information)
                      (ts-common/service-url
                       (tr [:field-labels :transport-service-common ::t-service/booking-service])
                       ::t-service/booking-service)
                      (accessibility-group)
                      (pricing-group e!)
                      (ts-common/service-hours-group)])]
    [:div.row
     [:div {:class "col-lg-12"}
      [:div
       [:h1 (tr [:passenger-transportation-page :header-passenger-transportation-service])]]
      [form/form form-options form-groups form-data]]]))
