 (ns ote.views.transport-service.passenger-transportation
  "Required datas for passenger transportation provider"
   (:require [ote.app.utils :refer [user-operates-service-type?]]
             [ote.db.transport-service :as t-service]
             [ote.db.rental-booking-service :as rental-booking]
             [ote.localization :refer [tr tr-key tr-tree]]
             [ote.util.values :as values]
             [ote.ui.common :refer [linkify]]
             [ote.ui.form :as form]
             [ote.app.controller.transport-service :as ts-controller]
             [ote.views.transport-service.transport-service-common :as ts-common]
             [taxiui.app.routes :as taxiui-router])
  (:require-macros [reagent.core :refer [with-let]]))

(defn transportation-form-options [e! schemas in-validation? app]
  {:name->label (tr-key [:field-labels :passenger-transportation] [:field-labels :transport-service-common] [:field-labels :transport-service])
   :update!     #(e! (ts-controller/->EditTransportService %))
   :use-container true
   :footer-fn   (fn [data]
                  [ts-common/footer e! data schemas in-validation? app])})

(defn luggage-restrictions-group [in-validation?]
  (form/group
    {:label (tr [:passenger-transportation-page :header-restrictions])
     :columns 3
     :layout :row
     :card? false
     :top-border true}

    {:name ::t-service/luggage-restrictions
     :type :localized-text
     :disabled? in-validation?
     :rows 1
     :full-width? true
     :container-class "col-xs-12"}))

(defn accessibility-group [in-validation?]
  (form/group
    {:label (tr [:passenger-transportation-page :header-other-services-and-accessibility])
     :columns 3
     :layout :row
     :card? false
     :top-border true}

    {:name ::t-service/guaranteed-vehicle-accessibility
     :help (tr [:form-help :guaranteed-vehicle-accessibility])
     :type :checkbox-group
     :disabled? in-validation?
     :show-option (tr-key [:enums ::t-service/vehicle-accessibility])
     :options t-service/vehicle-accessibility
     :full-width? true
     :container-class "col-xs-12 col-sm-6 col-md-6"}

    {:name ::t-service/limited-vehicle-accessibility
     :help (tr [:form-help :limited-vehicle-accessibility])
     :type :checkbox-group
     :disabled? in-validation?
     :show-option (tr-key [:enums ::t-service/vehicle-accessibility])
     :options t-service/vehicle-accessibility
     :full-width? true
     :container-class "col-xs-12 col-sm-6 col-md-6"}

    {:name ::t-service/guaranteed-info-service-accessibility
     :type :checkbox-group
     :disabled? in-validation?
     :show-option (tr-key [:enums ::t-service/information-service-accessibility])
     :options t-service/information-service-accessibility
     :full-width? true
     :container-class "col-xs-12 col-sm-6 col-md-6"}

    {:name ::t-service/limited-info-service-accessibility
     :type :checkbox-group
     :disabled? in-validation?
     :show-option (tr-key [:enums ::t-service/information-service-accessibility])
     :options t-service/information-service-accessibility
     :full-width? true
     :container-class "col-xs-12 col-sm-6 col-md-6"}

    {:name ::t-service/guaranteed-transportable-aid
     :type :checkbox-group
     :disabled? in-validation?
     :show-option (tr-key [:enums ::t-service/transportable-aid])
     :options t-service/transportable-aid
     :full-width? true
     :container-class "col-xs-12 col-sm-6 col-md-6"}

    {:name ::t-service/limited-transportable-aid
     :type :checkbox-group
     :disabled? in-validation?
     :show-option (tr-key [:enums ::t-service/transportable-aid])
     :options t-service/transportable-aid
     :full-width? true
     :container-class "col-xs-12 col-sm-6 col-md-6"}

    {:name ::t-service/guaranteed-accessibility-description
     :type :localized-text
     :disabled? in-validation?
     :floatingLabelStyle {:line-height "1rem"}
     :rows 1
     :full-width? true
     :container-class "col-xs-12 col-sm-6 col-md-6"}

    {:name ::t-service/limited-accessibility-description
     :type :localized-text
     :disabled? in-validation?
     :floatingLabelStyle {:line-height "1rem"}
     :rows 1
     :container-class "col-xs-12 col-sm-6 col-md-6"
     :full-width? true}

    {:name ::t-service/accessibility-info-url
     :type :string
     :disabled? in-validation?
     :container-class "col-xs-12 col-sm-6 col-md-6"
     :full-width? true
     :max-length 200}

    {:name ::t-service/additional-services
     :type :multiselect-selection
     :disabled? in-validation?
     :style {:margin-bottom "2rem"}
     :show-option (tr-key [:enums ::t-service/additional-services])
     :options t-service/additional-services
     :container-class "col-xs-12 col-sm-6 col-md-6"
     :full-width? true}))

(defn pricing-group [operator-id service-id sub-type in-validation?]
  (let [price-class-name-label
          (cond
            (= :taxi sub-type) (tr [:field-labels :passenger-transportation ::t-service/price-class-name-taxi])
            (= :request sub-type) (tr [:field-labels :passenger-transportation ::t-service/price-class-name-request])
            (= :schedule sub-type) (tr [:field-labels :passenger-transportation ::t-service/price-class-name-schedule])
            :else (tr [:field-labels :passenger-transportation ::t-service/price-class-name-other]))
        price-description-label (if (= :taxi sub-type)
                                  (tr [:field-labels :passenger-transportation ::t-service/pricing-description-taxi])
                                  (tr [:field-labels :passenger-transportation ::t-service/pricing-description]))]
  (form/group
   {:label (tr [:passenger-transportation-page :header-price-information])
    :columns 3
    :layout :row
    :card? false
    :top-border true}

   ; Cross-promo for Taxi UI
   (when (= :taxi sub-type)
     {:name        :netex-info-text
      :type        :component
      :full-width? true
      :component   (fn [_]
                     [:div {:style {:margin-top "1rem"}}
                      (tr [:taxi-ui :cross-promo :service-editor :instructions])
                      [linkify
                       (str "/taxiui#" (taxiui-router/resolve :taxi-ui/pricing-details {:operator-id operator-id
                                                                                        :service-id  service-id}))
                       (tr [:taxi-ui :cross-promo :service-editor :link-to-taxiui-text])
                       {:target               "_blank"
                        :force-external-icon? true}]])})

   (when-not (= :taxi sub-type)
     {:type            :info-toggle
      :name            :pricing-group-info
      :label           (tr [:common-texts :filling-info])
      :body            [:div (tr [:form-help :pricing-info])]
      :default-state   false
      :full-width?     true
      :container-class "col-xs-12 col-sm-12 col-md-12"})

   (when-not (= :taxi sub-type)
     (merge
     {:container-class "col-xs-12"
      :name ::t-service/price-classes
      :type :div-table
      :prepare-for-save values/without-empty-rows
      :table-fields [{:name ::t-service/name
                      :label price-class-name-label
                      :type :string
                      :disabled? in-validation?
                      :field-class "col-xs-12 col-sm-8 col-md-4"
                      :full-width? true
                      :max-length 200}
                     {:name ::t-service/price-per-unit
                      :label (tr [:field-labels :passenger-transportation ::t-service/price-per-unit])
                      :type :number
                      :disabled? in-validation?
                      :field-class "col-xs-12 col-sm-4 col-md-2"
                      :full-width? true
                      :currency? true}
                     {:name ::t-service/unit
                      :label (tr [:field-labels :passenger-transportation ::t-service/unit])
                      :type :string
                      :disabled? in-validation?
                      :field-class "col-xs-12 col-sm-6 col-md-4"
                      :full-width? true
                      :max-length 128}]}
     (when-not in-validation?
       {:add-label (tr [:buttons :add-new-price-class])
        :inner-delete? true
        :inner-delete-class "col-xs-12 col-sm-6 col-md-2"
        :inner-delete-label (tr [:buttons :delete])})))

   {:container-class "col-xs-12 col-sm-6 col-md-6"
    :name        ::t-service/payment-methods
    :type        :checkbox-group
    :disabled? in-validation?
    :show-option (tr-key [:enums ::t-service/payment-methods])
    :options     t-service/payment-methods}

   {:container-class "col-xs-12 col-sm-6 col-md-6"
    :name ::t-service/payment-method-description
    :type :localized-text
    :disabled? in-validation?
    :rows 6
    :full-width? true}

   {:container-class "col-xs-12 col-sm-6 col-md-6"
    :name ::t-service/pricing-description
    :label price-description-label
    :type :localized-text
    :disabled? in-validation?
    :full-width? true
    :write #(assoc-in %1 [::t-service/pricing ::t-service/description] %2)
    :read (comp ::t-service/description ::t-service/pricing)}

   {:container-class "col-xs-12 col-sm-6 col-md-6"
    :name ::t-service/pricing-url
    :full-width? true
    :type :string
    :disabled? in-validation?
    :write #(assoc-in %1 [::t-service/pricing ::t-service/url] %2)
    :read (comp ::t-service/url ::t-service/pricing)})))

(defn rental-booking-info
  [element-id label info-message in-validation?]
  (apply
    form/group
    {:label label
     :layout :row
     :columns 3
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
        :name ::rental-booking/application-link
        :type :string
        :disabled? in-validation?
        :read (comp ::rental-booking/application-link ::rental-booking/rental-booking-info)
        :write (fn [data url]
                 (assoc-in data [::rental-booking/rental-booking-info ::rental-booking/application-link] url))
        :full-width? true
        :container-class "col-xs-12 col-sm-12 col-md-12 col-lg-12"
        :max-length 200}

       {:element-id "input-service-country"
        ;:label (tr [:common-texts :country])
        :name :ote.db.rental-booking-service/phone-countrycode
        :type :selection
        :disabled? in-validation?
        :container-class "col-xs-3 col-sm-3 col-md-3 col-lg-3"
        :style {:margin-bottom "2rem"}
        :full-width? true
        ; translate viewed options + sort by translated names to make the list intuitive for users
        :options (->> (tr-tree [:phone-countrycodes])
                      (mapv (fn [cc]
                              (assoc cc :country (tr [:country-list (:country cc)]))))
                      (sort-by :country)
                      (vec))
        :option-value :code
        ; format dropdown values for human readability with correct translations
        :show-option (fn [o] (str (:country o) " (" (:code o) ")"))
        :read (comp ::rental-booking/phone-countrycode ::rental-booking/rental-booking-info)
        :write (fn [data desc]
                 (assoc-in data [::rental-booking/rental-booking-info ::rental-booking/phone-countrycode] desc))}

       {:name :ote.db.rental-booking-service/phone-number
        :type :string
        :disabled? in-validation?
        :rows 1
        :read (comp ::rental-booking/phone-number ::rental-booking/rental-booking-info)
        :regex #"\d*"
        :write (fn [data desc]
                 (assoc-in data [::rental-booking/rental-booking-info ::rental-booking/phone-number] desc))
        :container-class "col-xs-5 col-sm-5 col-md-5 col-lg-5"
        :full-width? true}])))

(defn passenger-transportation-info [e! {form-data ::t-service/passenger-transportation :as service} app]
  (let [validate (::t-service/validate form-data)
        service-id (::t-service/id service)
        db-file-key (:db-file-key form-data)
        admin-validating-id (get-in app [:admin :in-validation :validating])
        in-validation? (ts-controller/in-readonly? validate admin-validating-id service-id)
        form-groups
             [(ts-common/transport-type (::t-service/sub-type service) in-validation?)
              (ts-common/name-group (tr [:passenger-transportation-page :header-service-info]) in-validation?)
              (ts-common/contact-info-group in-validation?)
              (ts-common/companies-group e! in-validation? service-id db-file-key)
              (ts-common/brokerage-group e! in-validation?)
              (ts-common/place-search-group (ts-common/place-search-dirty-event e!) ::t-service/passenger-transportation in-validation?)
              (pricing-group
                (-> app :transport-operator :ote.db.transport-operator/id)
                service-id
                (get service ::t-service/sub-type)
                in-validation?)
              (ts-common/external-interfaces e!
                                             (get service ::t-service/type)
                                             (get service ::t-service/sub-type)
                                             (get-in service [::t-service/passenger-transportation ::t-service/transport-type])
                                             in-validation?)
              (luggage-restrictions-group in-validation?)
              (ts-common/service-url "real-time-information-url"
                                     (tr [:field-labels :passenger-transportation ::t-service/real-time-information])
                                     ::t-service/real-time-information
                                     (tr [:form-help :real-time-info])
                                     in-validation?)
              (ts-common/advance-reservation-group in-validation?)
              (if (and (= (::t-service/type service) :passenger-transportation)
                       (contains? #{:taxi :request} (::t-service/sub-type service)))
                (rental-booking-info "rental-booking-id"
                                     (tr [:field-labels :transport-service-common ::t-service/booking-service])
                                     nil #_[:div (str (::t-service/type service) "/" (::t-service/sub-type service))
                                      [:br]
                                      (str (::rental-booking/rental-booking-info form-data))]
                                     in-validation?)
                (ts-common/service-url "booking-service-url"
                                       (tr [:field-labels :transport-service-common ::t-service/booking-service])
                                       ::t-service/booking-service
                                       nil
                                       in-validation?))
              (accessibility-group in-validation?)
              (ts-common/service-hours-group "passenger-transportation" false in-validation?)]
             form-options (transportation-form-options e! form-groups in-validation? app)]
    [:div
     [form/form form-options form-groups form-data]]))
