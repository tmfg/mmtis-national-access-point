(ns ote.views.parking
  "Required data input fields for parking services"
  (:require [reagent.core :as r]
            [ote.db.transport-service :as t-service]
            [ote.localization :refer [tr tr-key]]
            [ote.time :as time]
            [ote.util.values :as values]
            [ote.style.form :as style-form]
            [ote.ui.validation :as validation]
            [ote.ui.form :as form]
            [ote.app.controller.transport-service :as ts]
            [ote.views.transport-service-common :as ts-common]))

(defn form-options [e! schemas in-validation? app]
  {:name->label (tr-key [:field-labels :parking]
                        [:field-labels :transport-service-common]
                        [:field-labels :transport-service]
                        [:field-labels])
   :update!     #(e! (ts/->EditTransportService %))
   :footer-fn   (fn [data]
                  [ts-common/footer e! data schemas in-validation? app])})

(defn pricing-group [e! in-validation?]
  (form/group
    {:label   (tr [:parking-page :header-price-and-payment-methods])
     :columns 3
     :layout  :row
     :card? false
     :top-border true}

    {:type :info-toggle
     :name :pricing-group-info
     :label (tr [:common-texts :filling-info])
     :body [:div (tr [:form-help :pricing-info])]
     :default-state false
     :full-width? true
     :container-class "col-xs-12 col-sm-12 col-md-12"}

    {:name         ::t-service/price-classes
     :type         :table
     :prepare-for-save values/without-empty-rows
     :table-fields [{:name  ::t-service/name
                     :type :string
                     :disabled? in-validation?
                     :label (tr [:field-labels :parking ::t-service/price-class-name])
                     :required? true
                     :max-length 200}
                    {:name ::t-service/price-per-unit
                     :type :number
                     :disabled? in-validation?
                     :currency? true
                     :style {:width "100px"}
                     :input-style {:text-align "right" :padding-right "5px"}
                     :required? true}
                    {:name ::t-service/unit
                     :type :string
                     :disabled? in-validation?
                     :style {:width "100px"}
                     :max-length 128}]
     :add-label    (tr [:buttons :add-new-price-class])
     :delete?      true}

    {:container-class "col-xs-12 col-sm-6 col-md-6"
     :name            ::t-service/pricing-description
     :type            :localized-text
     :full-width?     true
     :write           #(assoc-in %1 [::t-service/pricing ::t-service/description] %2)
     :read            (comp ::t-service/description ::t-service/pricing)}

    {:container-class "col-xs-12 col-sm-6 col-md-6"
     :name  ::t-service/pricing-url
     :type  :string
     :full-width? true
     :write #(assoc-in %1 [::t-service/pricing ::t-service/url] %2)
     :read  (comp ::t-service/url ::t-service/pricing)}

    {:container-class "col-xs-12 col-sm-6 col-md-4"
     :name            ::t-service/payment-methods
     :type            :multiselect-selection
     :show-option     (tr-key [:enums ::t-service/payment-methods])
     :options         t-service/payment-methods
     :container-style {:padding-bottom "2rem"}}

    {:container-class "col-xs-12 col-sm-6 col-md-8"
     :name ::t-service/payment-method-description
     :type :localized-text
     :rows 1
     :full-width? true}))

(defn service-hours-group [e! in-validation?]
  (let [tr* (tr-key [:field-labels :service-exception])
        write-time (fn [key]
                (fn [{all-day? ::t-service/all-day :as data} time]
                  ;; Don't allow changing time if all-day checked
                  (if all-day?
                    data
                    (assoc data key time))))]
    (form/group
      {:label   (tr [:parking-page :header-service-hours])
       :columns 3
       :layout  :row
       :card? false
       :top-border true}

      {:name      ::t-service/service-hours
       :type      :table
       :prepare-for-save values/without-empty-rows
       :table-fields
                  [{:name              ::t-service/week-days
                    :width             "40%"
                    :type              :multiselect-selection
                    :disabled? in-validation?
                    :options           t-service/days
                    :show-option       (tr-key [:enums ::t-service/day :full])
                    :show-option-short (tr-key [:enums ::t-service/day :short])
                    :required? true
                    :is-empty? validation/empty-enum-dropdown?}
                   {:name  ::t-service/all-day
                    :width "10%"
                    :type  :checkbox
                    :disabled? in-validation?
                    :write (fn [data all-day?]
                             (merge data
                                    {::t-service/all-day all-day?}
                                    (if all-day?
                                      {::t-service/from (time/->Time 0 0 nil)
                                       ::t-service/to (time/->Time 24 0 nil)}
                                      {::t-service/from nil
                                       ::t-service/to nil})))}

                   {:name         ::t-service/from
                    :width        "25%"
                    :type         :time
                    :disabled? in-validation?
                    :write        (write-time ::t-service/from)
                    :required? true
                    :is-empty? time/empty-time?}
                   {:name         ::t-service/to
                    :width        "25%"
                    :type         :time
                    :disabled? in-validation?
                    :write        (write-time ::t-service/to)
                    :required? true
                    :is-empty? time/empty-time?}]
       :delete?   true
       :add-label (tr [:buttons :add-new-service-hour])}

      {:name ::t-service/service-exceptions
       :type :table
       :prepare-for-save values/without-empty-rows
       :table-fields [{:name ::t-service/description
                       :label (tr* :description)
                       :type :localized-text
                       :disabled? in-validation?}
                      {:name ::t-service/from-date
                       :type :date-picker
                       :disabled? in-validation?
                       :label (tr* :from-date)}
                      {:name ::t-service/to-date
                       :type :date-picker
                       :disabled? in-validation?
                       :label (tr* :to-date)}]
       :delete? true
       :add-label (tr [:buttons :add-new-service-exception])}

      ;;

      {:name ::t-service/maximum-stay
       :type :interval
       :disabled? in-validation?
       :enabled-label (tr [:field-labels :parking :maximum-stay-limited])
       :container-style style-form/full-width})))

(defn capacities [e! in-validation?]
  (form/group
    {:label   (tr [:parking-page :header-facilities-and-capacities])
     :columns 3
     :layout  :row
     :card? false
     :top-border true}

    {:name         ::t-service/parking-capacities
     :type         :table
     :prepare-for-save values/without-empty-rows
     :table-fields [{:name        ::t-service/parking-facility
                     :type        :selection
                     :disabled? in-validation?
                     :show-option (tr-key [:enums ::t-service/parking-facility])
                     :options     t-service/parking-facilities
                     :required? true}
                    {:name ::t-service/capacity :type :number
                     :required? true}]
     :add-label    (tr [:buttons :add-new-parking-capacity])
     :delete?      true}))

(defn charging-points [e! in-validation?]
  (form/group
    {:label   (tr [:parking-page :header-charging-points])
     :columns 3
     :layout  :row
     :card? false
     :top-border true}

    {:name            ::t-service/charging-points
     :rows            2
     :type            :localized-text
     :disabled? in-validation?
     :full-width?     true
     :container-class "col-md-6"}))

(defn accessibility-group [in-validation?]
  (form/group
    {:label (tr [:parking-page :header-accessibility])
     :columns 3
     :layout :row
     :card? false
     :top-border true}

    {:name ::t-service/accessibility
     :type :checkbox-group
     :disabled? in-validation?
     :show-option (tr-key [:enums ::t-service/accessibility])
     :options t-service/parking-accessibility
     :full-width? true
     :container-class "col-md-6"}

    {:name ::t-service/information-service-accessibility
     :type :checkbox-group
     :disabled? in-validation?
     :show-option (tr-key [:enums ::t-service/information-service-accessibility])
     :options t-service/parking-information-service-accessibility
     :full-width? true
     :container-class "col-md-5"
     :container-style {:align-self "baseline"}}

    {:name ::t-service/accessibility-description
     :type :localized-text
     :disabled? in-validation?
     :rows 1
     :container-class "col-md-6"
     :full-width? true}

    {:name ::t-service/accessibility-info-url
     :type :string
     :disabled? in-validation?
     :container-class "col-md-5"
     :full-width? true
     :max-length 200}))

(defn parking [e! {form-data ::t-service/parking :as service} app]
  (let [in-validation? (::t-service/validate form-data)
        groups [(ts-common/transport-type ::t-service/parking in-validation?)
                (ts-common/name-group (tr [:parking-page :header-service-info]) in-validation?)
                (ts-common/contact-info-group in-validation?)
                (ts-common/place-search-group (ts-common/place-search-dirty-event e!) ::t-service/parking in-validation?)
                (ts-common/external-interfaces e!
                                               (get service ::t-service/type)
                                               (get service ::t-service/sub-type)
                                               (get-in service [::t-service/passenger-transportation ::t-service/transport-type])
                                               in-validation?)
                (ts-common/advance-reservation-group in-validation?)
                (ts-common/service-url "real-time-information-url"
                                       (tr [:field-labels :parking ::t-service/real-time-information])
                                       ::t-service/real-time-information
                                       (tr [:form-help :real-time-info])
                                       in-validation?)
                (ts-common/service-url "booking-service-url"
                                       (tr [:field-labels :parking ::t-service/booking-service])
                                       ::t-service/booking-service
                                       nil
                                       in-validation?)
                (ts-common/service-urls (tr [:field-labels :parking ::t-service/additional-service-links])
                                        ::t-service/additional-service-links
                                        in-validation?)
                (capacities e! in-validation?)
                (charging-points e! in-validation?)
                (pricing-group e! in-validation?)
                (accessibility-group in-validation?)
                (service-hours-group e! in-validation?)]
        options (form-options e! groups in-validation? app)]
    [:div.row
     [form/form options groups (merge
                                 {:maximum-stay-unit :hours}
                                 form-data)]]))
