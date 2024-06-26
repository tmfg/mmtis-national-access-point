(ns ote.views.transport-service.parking
  "Required data input fields for parking services"
  (:require [reagent.core :as r]
            [ote.db.transport-service :as t-service]
            [ote.localization :refer [tr tr-key]]
            [ote.time :as time]
            [ote.util.values :as values]
            [ote.style.form :as style-form]
            [ote.ui.validation :as validation]
            [ote.ui.form :as form]
            [ote.app.controller.transport-service :as ts-controller]
            [ote.views.transport-service.transport-service-common :as ts-common]))

(defn form-options [e! schemas in-validation? app]
  {:name->label (tr-key [:field-labels :parking]
                        [:field-labels :transport-service-common]
                        [:field-labels :transport-service]
                        [:field-labels])
   :update!     #(e! (ts-controller/->EditTransportService %))
   :use-container true
   :footer-fn   (fn [data]
                  [ts-common/footer e! data schemas in-validation? app])})

(defn pricing-group [e! in-validation?]
  (form/group
    {:label (tr [:parking-page :header-price-and-payment-methods])
     :columns 3
     :layout :row
     :card? false
     :top-border true}

    {:type :info-toggle
     :name :pricing-group-info
     :label (tr [:common-texts :filling-info])
     :body [:div (tr [:form-help :pricing-info])]
     :default-state false
     :full-width? true
     :container-class "col-xs-12 col-sm-12 col-md-12"}

    (merge
      {:name ::t-service/price-classes
       :type :div-table
       :container-class "col-xs-12"
       :prepare-for-save values/without-empty-rows
       :table-fields [{:name ::t-service/name
                       :label (tr [:field-labels :parking ::t-service/price-class-name])
                       :type :string
                       :disabled? in-validation?
                       :required? true
                       :full-width? true
                       :field-class "col-xs-12 col-sm-3 col-md-3"
                       :max-length 200}
                      {:name ::t-service/price-per-unit
                       :label (tr [:field-labels :parking ::t-service/price-per-unit])
                       :type :number
                       :disabled? in-validation?
                       :currency? true
                       :full-width? true
                       :required? true
                       :field-class "col-xs-12 col-sm-3 col-md-3"}
                      {:name ::t-service/unit
                       :label (tr [:field-labels :parking ::t-service/unit])
                       :type :string
                       :disabled? in-validation?
                       :full-width? true
                       :max-length 128
                       :field-class "col-xs-12 col-sm-3 col-md-3"}]}
      (when-not in-validation?
        {:add-label (tr [:buttons :add-new-price-class])
         :inner-delete? true
         :inner-delete-class "col-xs-12 col-sm-3 col-md-3"
         :inner-delete-label (tr [:buttons :delete])}))

    {:container-class "col-xs-12 col-sm-6 col-md-6"
     :name ::t-service/pricing-description
     :type :localized-text
     :full-width? true
     :write #(assoc-in %1 [::t-service/pricing ::t-service/description] %2)
     :read (comp ::t-service/description ::t-service/pricing)}

    {:container-class "col-xs-12 col-sm-6 col-md-6"
     :name ::t-service/pricing-url
     :type :string
     :full-width? true
     :write #(assoc-in %1 [::t-service/pricing ::t-service/url] %2)
     :read (comp ::t-service/url ::t-service/pricing)}

    {:container-class "col-xs-12 col-sm-6 col-md-4"
     :name ::t-service/payment-methods
     :type :multiselect-selection
     :show-option (tr-key [:enums ::t-service/payment-methods])
     :options t-service/payment-methods
     :full-width? true
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

      (merge

        {:name ::t-service/service-hours
         :type :div-table
         :container-class "col-xs-12"
         :prepare-for-save values/without-empty-rows
         :table-fields
         [{:name ::t-service/week-days
           :label (tr [:field-labels :transport-service ::t-service/week-days])
           :type :multiselect-selection
           :disabled? in-validation?
           :options t-service/days
           :show-option (tr-key [:enums ::t-service/day :full])
           :show-option-short (tr-key [:enums ::t-service/day :short])
           :required? true
           :full-width? true
           :is-empty? validation/empty-enum-dropdown?
           :field-class "col-xs-6 col-sm-4 col-md-4"}
          {:name ::t-service/all-day
           :label (tr [:field-labels :parking ::t-service/all-day])
           :type :checkbox
           :disabled? in-validation?
           :full-width? true
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

          {:name ::t-service/from
           :label (tr [:field-labels :parking ::t-service/from])
           :wrapper-style style-form/input-element-wrapper-div
           :label-style style-form/input-element-label
           :type :time
           :disabled? in-validation?
           :write (write-time ::t-service/from)
           :required? true
           :is-empty? time/empty-time?
           :full-width? true
           :container-style {:padding-top "1.5rem"}
           :field-class "col-xs-6 col-sm-2 col-md-2"}
          {:name ::t-service/to
           :label (tr [:field-labels :parking ::t-service/to])
           :wrapper-style style-form/input-element-wrapper-div
           :label-style style-form/input-element-label
           :type :time
           :disabled? in-validation?
           :write (write-time ::t-service/to)
           :required? true
           :is-empty? time/empty-time?
           :full-width? true
           :container-style {:padding-top "1.5rem"}
           :field-class "col-xs-6 col-sm-2 col-md-2"}]}
        (when-not in-validation?
          {:inner-delete? true
           :add-label (tr [:buttons :add-new-service-hour])
           ;:inner-delete-class "col-xs-12 col-sm-3 col-md-3"
           :inner-delete-label (tr [:buttons :delete])}))

      (merge
        {:name ::t-service/service-exceptions
         :type :div-table
         :container-class "col-xs-12"
         :prepare-for-save values/without-empty-rows
         :table-fields [{:name ::t-service/description
                         :label (tr* :description)
                         :type :localized-text
                         :disabled? in-validation?
                         :full-width? true
                         :field-class "col-xs-12 col-sm-4 col-md-4"}
                        {:name ::t-service/from-date
                         :label (tr* :from-date)
                         :type :date-picker
                         :disabled? in-validation?
                         :full-width? true
                         :field-class "col-xs-12 col-sm-2 col-md-2"}
                        {:name ::t-service/to-date
                         :label (tr* :to-date)
                         :type :date-picker
                         :disabled? in-validation?
                         :field-class "col-xs-12 col-sm-2 col-md-2"
                         :full-width? true}]}
        (when-not in-validation?
          {:inner-delete? true
           :add-label (tr [:buttons :add-new-service-exception])
           :inner-delete-class "col-xs-12 col-sm-4 col-md-4"
           :inner-delete-label (tr [:buttons :delete])}))

      {:name ::t-service/maximum-stay
       :type :interval
       :disabled? in-validation?
       :enabled-label (tr [:field-labels :parking :maximum-stay-limited])
       :container-style style-form/full-width})))

(defn capacities [e! in-validation?]
  (form/group
    {:label   (tr [:parking-page :header-facilities-and-capacities])
     :columns 3
     :card? false
     :top-border true}

    (merge
      {:name ::t-service/parking-capacities
       :type :div-table
       :container-class "col-xs-12"
       :prepare-for-save values/without-empty-rows
       :table-fields [{:name ::t-service/parking-facility
                       :label (tr [:field-labels :parking ::t-service/parking-facility])
                       :type :selection
                       :disabled? in-validation?
                       :show-option (tr-key [:enums ::t-service/parking-facility])
                       :options t-service/parking-facilities
                       :required? true
                       :full-width? true
                       :field-class "col-xs-12 col-sm-5 col-md-5"}
                      {:name ::t-service/capacity
                       :label (tr [:field-labels :parking ::t-service/capacity])
                       :type :number
                       :disabled? in-validation?
                       :required? true
                       :full-width? true
                       :field-class "col-xs-12 col-sm-5 col-md-5"}]}
       (when-not in-validation?
         {:add-label (tr [:buttons :add-new-parking-capacity])
          :inner-delete? true
          :inner-delete-class "col-xs-12 col-sm-2 col-md-2"
          :inner-delete-label (tr [:buttons :delete])}))))

(defn charging-points [e! in-validation?]
  (form/group
    {:label (tr [:parking-page :header-charging-points])
     :columns 3
     :layout :row
     :card? false
     :top-border true}

    {:name ::t-service/charging-points
     :rows 1
     :type :localized-text
     :disabled? in-validation?
     :full-width? true
     :container-class "col-xs-12 col-sm-12 col-md-12"}))

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
     :container-class "col-xs-12 col-sm-12 col-md-6"
     :full-width? true}

    {:name ::t-service/accessibility-info-url
     :type :string
     :disabled? in-validation?
     :container-class "col-xs-12 col-sm-12 col-md-6"
     :full-width? true
     :max-length 200}))

(defn parking [e! {form-data ::t-service/parking :as service} app]
  (let [validate (::t-service/validate form-data)
        service-id (::t-service/id service)
        admin-validating-id (get-in app [:admin :in-validation :validating])
        in-validation? (ts-controller/in-readonly? validate admin-validating-id service-id)
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
    [:div
     [form/form options groups (merge
                                 {:maximum-stay-unit :hours}
                                 form-data)]]))
