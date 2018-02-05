(ns ote.views.transport-service-common
  "View parts that are common to all transport service forms."
  (:require [tuck.core :as tuck]
            [ote.db.transport-service :as t-service]
            [ote.db.transport-operator :as t-operator]
            [ote.localization :refer [tr tr-key]]
            [ote.ui.form :as form]
            [ote.db.common :as common]
            [ote.ui.common :refer [linkify]]
            [ote.ui.buttons :as buttons]
            [ote.app.controller.transport-service :as ts]
            [ote.views.place-search :as place-search]
            [clojure.string :as str]
            [ote.time :as time]
            [ote.util.values :as values]
            [ote.style.form :as style-form]
            [cljs-react-material-ui.reagent :as ui]
            [ote.ui.validation :as validation]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]
            [cljs-react-material-ui.icons :as ic]))

(defn service-url
  "Creates a form group for service url that creates two form elements url and localized text area"
  [label service-url-field]
  (form/group
    {:label label
    :layout :row
    :columns 3}

    {:class "set-bottom"
     :name   ::t-service/url
     :type   :string
     :read   (comp ::t-service/url service-url-field)
     :write  (fn [data url]
             (assoc-in data [service-url-field ::t-service/url] url))
     :full-width? true
     :container-class "col-xs-12 col-sm-6 col-md-6"}

    {:name ::t-service/description
     :type  :localized-text
     :is-empty? validation/empty-localized-text?
     :rows  1
     :read  (comp ::t-service/description service-url-field)
     :write (fn [data desc]
             (assoc-in data [service-url-field ::t-service/description] desc))
     :container-class "col-xs-12 col-sm-6 col-md-6"
     :full-width?  true}))

(defn service-urls
  "Creates a table for additional service urls."
  [label service-url-field]
  (form/group
    {:label label
     :columns 3}

    {:name         service-url-field
     :type         :table
     :prepare-for-save values/without-empty-rows
     :table-fields [{:name ::t-service/url
                     :type :string}
                    {:name ::t-service/description
                     :type :localized-text
                     :is-empty? validation/empty-localized-text?}]
     :delete?      true
     :add-label    (tr [:buttons :add-new-service-link])}))

(defn external-interfaces
  "Creates a form group for external services."
  [& [e! rae-info?]]
  (form/group
    {:label  (tr [:field-labels :transport-service-common ::t-service/external-interfaces])
    :columns 3}

    (form/info
     [:div
      [:p (tr [:form-help :external-interfaces])]
      (when rae-info?
        [:p (tr [:form-help :external-interfaces-eg-rae])
         [linkify "https://liikennevirasto.fi/rae" (tr [:form-help :RAE-tool])
          {:target "_blank"}]])])

    {:name             ::t-service/external-interfaces
     :type             :table
     :prepare-for-save values/without-empty-rows
     :table-fields     [{:name ::t-service/data-content
                         :width "20%"
                         :auto-width? true
                         :full-width? true
                         :type :multiselect-selection
                         :options t-service/interface-data-contents
                         :show-option (tr-key [:enums ::t-service/interface-data-content])
                         :required? true
                         :is-empty? validation/empty-enum-dropdown?}
                        {:name      ::t-service/external-service-url
                         :type      :string
                         :width     "20%"
                         :on-blur #(e! (ts/->EnsureExternalInterfaceUrl (-> % .-target .-value)))
                         :full-width? true
                         :read      (comp ::t-service/url ::t-service/external-interface)
                         :write     #(assoc-in %1 [::t-service/external-interface ::t-service/url] %2)
                         :required? true}
                        {:name      :ext-validation
                         :type      :component
                         :component (fn [{{status :status} :data}]
                                      (if-not status
                                        [:span]
                                        (if (= :success  status)
                                          [:div
                                           {:title (tr [:field-labels :transport-service-common :external-interfaces-ok])}
                                           [ic/action-done {:style {:width 24 :height 24 :color "green"}}]]
                                          [:div
                                           {:title (tr [:field-labels :transport-service-common :external-interfaces-warning])}
                                           [ic/alert-warning {:style {:width 24 :height 24 :color "#cccc00"}}]])))
                         :read (comp :url-status ::t-service/external-interface)
                         :width "5%"
                         }
                        {:name      ::t-service/format
                         :type      :string
                         :width     "15%"
                         :full-width? true
                         :required? true}
                        {:name  ::t-service/license
                         :type  :string
                         :width "20%"
                         :full-width? true}
                        {:name      ::t-service/external-service-description
                         :type      :localized-text
                         :width     "20%"
                         :full-width? true
                         :read      (comp ::t-service/description ::t-service/external-interface)
                         :write     #(assoc-in %1 [::t-service/external-interface ::t-service/description] %2)
                         :required? true
                         :is-empty? validation/empty-localized-text?}]
     :delete?          true
     :add-label        (tr [:buttons :add-external-interface])}

    (form/info
     [:div
      [:p (tr [:form-help :external-interfaces-end])]])

    {:name ::t-service/notice-external-interfaces?
     :type :checkbox
     :required? true
     :style style-form/padding-top
     :validate [[:checked?]]}))

(defn companies-group
  "Creates a form group for companies. A parent company can list its companies."
  [e!]
  (form/group
    {:label (tr [:field-labels :transport-service-common ::t-service/companies])
    :columns 3}

    (form/info (tr [:form-help :companies-main-info]))
    (form/info-with-link (tr [:form-help :csv-info]) "/ote/csv/palveluyritykset.csv" (tr [:form-help :csv-file-example]))

    {:name ::t-service/companies-csv-url
    :full-width?  true
    :on-blur #(e! (ts/->EnsureCsvFile))
    :container-class "col-xs-12 col-sm-6 col-md-6"
    :type :string}

    {:name      :csv-count
     :type      :component
     :component (fn [data]
                  (let [success (if (= :success (get-in data [:data :status]))
                                  true
                                  false)
                        count (if (get-in data [:data :count])
                                (get-in data [:data :count])
                                nil)]

                    (cond
                      (and data success) [:span {:style {:color "green"}} (tr [:csv :parsing-success] {:count count})]
                      (and data (= false success)) [:span (stylefy/use-style style-base/required-element) (tr [:csv (get-in data [:data :error])])]
                      :defalt [:span]
                      )))}

    (form/info (tr [:form-help :companies-info]))

    {:name ::t-service/companies
    :type :table
    :prepare-for-save values/without-empty-rows
    :table-fields [{:name ::t-service/name :type :string
                    :label (tr [:field-labels :transport-service-common ::t-service/company-name])
                    :required? true}
                   {:name ::t-service/business-id :type :string
                    :validate [[:business-id]]
                    :required? true
                    :regex #"\d{0,7}(-\d?)?"}]
    :delete? true
    :add-label (tr [:buttons :add-new-company])}

    {:name ::t-service/brokerage?
     :style style-form/padding-top
     :extended-help {:help-text      (tr [:form-help :brokerage?])
                     :help-link-text (tr [:form-help :brokerage-link])
                     :help-link      "https://www.trafi.fi/tieliikenne/ammattiliikenne/liikenneluvat_trafiin/valitys-_ja_yhdistamispalvelut"}
     :type :checkbox}))

(defn contact-info-group []
  (form/group
   {:label  (tr [:passenger-transportation-page :header-contact-details])
    :columns 3
    :layout :row}

   (form/info (tr [:form-help :description-why-contact-info]))

   {:name        ::common/street
    :type        :string
    :container-class "col-xs-12 col-sm-6 col-md-4"
    :full-width?  true
    :read (comp ::common/street ::t-service/contact-address)
    :write (fn [data street]
             (assoc-in data [::t-service/contact-address ::common/street] street))
    :label (tr [:field-labels ::common/street])
    :required? true}

   {:name        ::common/postal_code
    :type        :string
    :container-class "col-xs-12 col-sm-6 col-md-2"
    :full-width?  true
    :regex #"\d{0,5}"
    :read (comp ::common/postal_code ::t-service/contact-address)
    :write (fn [data postal-code]
             (assoc-in data [::t-service/contact-address ::common/postal_code] postal-code))
    :label (tr [:field-labels ::common/postal_code])
    :required? true
    :validate [[:postal-code]]}

   {:name        ::common/post_office
    :type        :string
    :container-class "col-xs-12 col-sm-6 col-md-5"
    :full-width?  true
    :read (comp ::common/post_office ::t-service/contact-address)
    :write (fn [data post-office]
             (assoc-in data [::t-service/contact-address ::common/post_office] post-office))
    :label (tr [:field-labels ::common/post_office])
    :required? true}

   {:name        ::t-service/contact-email
    :type        :string
    :container-class "col-xs-12 col-sm-6 col-md-4"
    :full-width?  true}

   {:name        ::t-service/contact-phone
    :type        :string
    :container-class "col-xs-12 col-sm-6 col-md-2"
    :max-length  16
    :full-width? true}

   {:name        ::t-service/homepage
    :type        :string
    :container-class "col-xs-12 col-sm-6 col-md-5"
    :full-width?  true}))

(defn footer
  "Transport service form -footer element. All transport service form should be using this function."
  [e! {published? ::t-service/published? :as data} schemas app]
  (let [name-missing? (str/blank? (::t-service/name data))
        show-footer? (if (get-in app [:transport-service ::t-service/id])
                       (ts/is-service-owner? app)
                       true)]

    (when show-footer?
      [:div.row
       (when (not (form/can-save? data))
         [ui/card {:style {:margin-bottom "1em"}}
          [ui/card-text {:style {:color "#be0000" :padding-bottom "0.6em"}} (tr [:form-help :publish-missing-required])]])

       (if published?
         ;; True
         [buttons/save {:on-click #(e! (ts/->SaveTransportService schemas true))
                        :disabled (not (form/can-save? data))}
          (tr [:buttons :save-updated])]
         ;; False
         [:span
          [buttons/save {:on-click #(e! (ts/->SaveTransportService schemas true))
                         :disabled (not (form/can-save? data))}
           (tr [:buttons :save-and-publish])]
          [buttons/save  {:on-click #(e! (ts/->SaveTransportService schemas false))
                          :disabled name-missing?}
           (tr [:buttons :save-as-draft])]])
       [buttons/cancel {:on-click #(e! (ts/->CancelTransportServiceForm))}
        (tr [:buttons :discard])]])))

(defn place-search-group [e! key]
  (place-search/place-search-form-group
   (tuck/wrap-path e! :transport-service key ::t-service/operation-area)
   (tr [:field-labels :transport-service-common ::t-service/operation-area])
   ::t-service/operation-area))

(defn service-hours-group []
  (let [tr* (tr-key [:field-labels :service-exception])
        write-time (fn [key]
                (fn [{all-day? ::t-service/all-day :as data} time]
                  ;; Don't allow changing time if all-day checked
                  (if all-day?
                    data
                    (assoc data key time))))]
    (form/group
     {:label (tr [:passenger-transportation-page :header-service-hours])
      :columns 3}

     {:name         ::t-service/service-hours
      :type         :table
      :prepare-for-save values/without-empty-rows
      :table-fields
      [{:name ::t-service/week-days
        :width "40%"
        :type :multiselect-selection
        :options t-service/days
        :show-option (tr-key [:enums ::t-service/day :full])
        :show-option-short (tr-key [:enums ::t-service/day :short])
        :required? true
        :is-empty? validation/empty-enum-dropdown?}
       {:name ::t-service/all-day
        :width "10%"
        :type :checkbox
        :write (fn [data all-day?]
                 (merge data
                        {::t-service/all-day all-day?}
                        (if all-day?
                          {::t-service/from (time/->Time 0 0 nil)
                           ::t-service/to (time/->Time 24 0 nil)}
                          {::t-service/from nil
                           ::t-service/to nil})))}

       {:name ::t-service/from
        :width "25%"
        :type :time
        :write (write-time ::t-service/from)
        :required? true
        :is-empty? time/empty-time?}
       {:name ::t-service/to
        :width "25%"
        :type :time
        :write (write-time ::t-service/to)
        :required? true
        :is-empty? time/empty-time?}]
      :delete?      true
      :add-label (tr [:buttons :add-new-service-hour])}

     {:name ::t-service/service-exceptions
      :type :table
      :prepare-for-save values/without-empty-rows
      :table-fields [{:name ::t-service/description
                      :label (tr* :description)
                      :type :localized-text
                      :is-empty? validation/empty-localized-text?}
                     {:name ::t-service/from-date
                      :type :date-picker
                      :label (tr* :from-date)}
                     {:name ::t-service/to-date
                      :type :date-picker
                      :label (tr* :to-date)}]
      :delete? true
      :add-label (tr [:buttons :add-new-service-exception])}

     {:name ::t-service/service-hours-info
      :label (tr [:field-labels :transport-service-common ::t-service/service-hours-info])
      :type :localized-text
      :is-empty? validation/empty-localized-text?
      :full-width? true
      :container-class "col-xs-12"})))

(defn name-group [label]
  (form/group
   {:label label
    :columns 3
    :layout :row}

   (form/info (tr [:form-help :name-info]))

   {:name           ::t-service/name
    :type           :string
    :full-width?    true
    :container-class "col-xs-12 col-sm-12 col-md-6"
    :required?      true}

   {:name ::t-service/description
    :type :localized-text
    :is-empty? validation/empty-localized-text?
    :rows 2
    :full-width? true
    :container-class "col-xs-12 col-sm-12 col-md-8"}

   (form/subtitle (tr [:field-labels :transport-service ::t-service/available-from-and-to-title]))

   (form/info (tr [:form-help :available-from-and-to]))
    {:name ::t-service/available-from
    :type :date-picker
    :show-clear? true
    :hint-text (tr [:field-labels :transport-service ::t-service/available-from-nil])
     :container-class "col-xs-12 col-sm-6 col-md-3"}

    {:name ::t-service/available-to
    :type :date-picker
    :show-clear? true
    :hint-text (tr [:field-labels :transport-service ::t-service/available-to-nil])
     :container-class "col-xs-12 col-sm-6 col-md-3"}))
