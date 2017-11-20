(ns ote.views.passenger-transportation
  "Required datas for passenger transportation provider"
  (:require [reagent.core :as reagent]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.ui.buttons :as buttons]
            [ote.app.controller.transport-service :as ts]
            [ote.app.controller.passenger-transportation :as pt]
            [ote.db.transport-service :as t-service]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]
            [ote.views.place-search :as place-search]
            [tuck.core :as tuck]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]
            [ote.views.transport-service-common :as ts-common])
  (:require-macros [reagent.core :refer [with-let]]))



(defn transportation-form-options [e!]
  {:name->label (tr-key [:field-labels :passenger-transportation] [:field-labels :transport-service-common] [:field-labels :transport-service])
   :update!     #(e! (pt/->EditPassengerTransportationState %))
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

   {:name        ::t-service/additional-services
    :type        :multiselect-selection
    :show-option (tr-key [:enums ::t-service/additional-services])
    :options     t-service/additional-services}

   {:name        ::t-service/accessibility-tool
    :type        :multiselect-selection
    :show-option (tr-key [:enums ::t-service/accessibility-tool])
    :options     t-service/accessibility-tool}

   {:name ::t-service/accessibility-description
    :type :localized-text
    :rows 1 :max-rows 5}))

(defn pricing-group [e!]
  (form/group
    {:label (tr [:passenger-transportation-page :header-price-information])
     :columns 3
     :actions [buttons/save
               {:style    (stylefy/use-style style-base/base-button)
                :label-style {:color "#FFFFFF" :font-weight "bold" :font-size "12px"}
                :label    (tr [:buttons :add-new-price-class])
                :disabled false
                :on-click #(e! (ts/->AddPriceClassRow))}]}

    {:name         ::t-service/price-classes
    :type         :table
    :table-fields [{:name ::t-service/name :type :string
                    :label (tr [:field-labels :passenger-transportation ::t-service/price-class-name])}
                   {:name ::t-service/price-per-unit :type :number}
                   {:name ::t-service/unit :type :string}
                   {:name ::t-service/currency :type :string :width "100px"}
                   ]
    :delete?      true}))

(defn service-hours-group [e!]
  (let [tr* (tr-key [:field-labels :service-exception])]
    (form/group
     {:label (tr [:passenger-transportation-page :header-service-hours])
      :columns 3}

     {:name         ::t-service/service-hours
      :type         :table
      :table-fields [{:name ::t-service/week-days
                      :type :multiselect-selection
                      :options t-service/days
                      :show-option (tr-key [:enums ::t-service/day :full])
                      :show-option-short (tr-key [:enums ::t-service/day :short])}
                     {:name ::t-service/from
                      :type :time-picker
                      :cancel-label (tr [:buttons :cancel])
                      :ok-label (tr [:buttons :save])
                      :default-time {:hours "08" :minutes "00"}}
                     {:name ::t-service/to
                      :type :time-picker
                      :cancel-label (tr [:buttons :cancel])
                      :ok-label (tr [:buttons :save])
                      :default-time {:hours "19" :minutes "00"}}]
      :delete?      true
      :add-label (tr [:buttons :add-new-service-hour])}

     {:name ::t-service/service-exceptions
      :type :table
      :table-fields [{:name ::t-service/description
                      :label (tr* :description)
                      :type :localized-text}
                     {:name ::t-service/from-date
                      :type :date-picker
                      :label (tr* :from-date)}
                     {:name ::t-service/to-date
                      :type :date-picker
                      :label (tr* :to-date)}]
      :delete? true
      :add-label (tr [:buttons :add-new-service-exception])})))

(defn passenger-transportation-info [e! {form-data ::t-service/passenger-transportation}]
  (with-let [form-options (transportation-form-options e!)
             form-groups [(name-and-type-group e!)
                          (ts-common/contact-info-group)
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
                          (service-hours-group e!)]]
    [:div.row
     [:div {:class "col-lg-12"}
      [:div
       [:h3 (tr [:passenger-transportation-page :header-passenger-transportation-service])]]
      [form/form form-options form-groups form-data]]]))
