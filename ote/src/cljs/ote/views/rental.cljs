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
            [ote.localization :refer [tr tr-key]]
            [ote.views.place-search :as place-search]
            [tuck.core :as tuck]
            [ote.views.transport-service-common :as ts-common]
            [ote.time :as time]))

(defn rental-form-options [e!]
  {:name->label (tr-key [:field-labels :rentals]
                        [:field-labels :transport-service]
                        [:field-labels :transport-service-common]
                        [:field-labels])
   :update!     #(e! (ts/->EditTransportService %))
   :name        #(tr [:olennaiset-tiedot :otsikot %])
   :footer-fn   (fn [data]
                  [ts-common/footer e! data])})

(defn name-group [e!]
  (form/group
   {:label (tr [:rentals-page :header-service-info])
    :columns 3
    :layout :row}

   {:name      ::t-service/name
    :type      :string
    :required? true}))

(defn accessibility-group []
  (form/group
   {:label (tr [:rentals-page :header-other-services-and-accessibility])
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

(defn eligibity-requirements []
  (form/group
   {:label (tr [:rentals-page :header-eligibity-requirements])
    :columns 3
    :layout :row}

   {:name ::t-service/eligibility-requirements
    :type :string
    :layout :row}))

(defn service-hours-for-location [update-form! data]
  (reagent/with-let [open? (reagent/atom false)]
    [:div
     [ui/flat-button {:label (tr [:rentals-page :open-service-hours-and-exceptions])
                      :on-click #(reset! open? true)}]
     [ui/dialog
      {:open @open?
       :auto-scroll-body-content true
       :title (tr [:opening-hours-dialog :header-dialog])
       :actions [(reagent/as-element
                  [ui/flat-button {:label (tr [:buttons :close])
                                   :on-click #(reset! open? false)}])]}
      [form/form {:update! update-form!
                  :name->label (tr-key [:field-labels :rentals]
                                       [:field-labels :transport-service]
                                       [:field-labels])}
       [(assoc-in (ts-common/service-hours-group) [:options :card?] false)]
       data]]
     ]))

(defn pick-up-locations [e!]
  (let [tr* (tr-key [:field-labels :service-exception])
        write (fn [key]
                (fn [{all-day? ::t-service/all-day :as data} time]
                  ;; Don't allow changing time if all-day checked
                  (if all-day?
                    data
                    (assoc data key time))))]
    (form/group
     {:label (tr [:passenger-transportation-page :header-pick-up-locations])
      :columns 3}

     {:name ::t-service/pick-up-locations
      :type :table
      :table-fields [{:name ::t-service/name
                      :type :string}
                     {:name ::t-service/pick-up-type
                      :type :selection
                      :options t-service/pick-up-types
                      :show-option (tr-key [:enums ::t-service/pick-up-type])}
                     {:name ::common/street
                      :type :string
                      :read (comp ::common/street ::t-service/pick-up-address)
                      :write #(assoc-in %1 [::t-service/pick-up-address ::common/street] %2)}
                     {:name ::common/postal_code
                      :type :string
                      :regex #"\d{0,5}"
                      :read (comp ::common/postal_code ::t-service/pick-up-address)
                      :write #(assoc-in %1 [::t-service/pick-up-address ::common/postal_code] %2)}
                     {:name ::common/post_office
                      :type :string
                      :read (comp ::common/post_office ::t-service/pick-up-address)
                      :write #(assoc-in %1 [::t-service/pick-up-address ::common/post_office] %2)}
                     {:name ::t-service/service-hours-and-exceptions
                      :type :component
                      :component (fn [{:keys [update-form! data]}]
                                   [service-hours-for-location update-form! data])}
                     ]
      :delete? true
      :add-label (tr [:buttons :add-new-pick-up-location])})))

(defn rental [e! service]
  (reagent/with-let [options (rental-form-options e!)
                     groups [(name-group e!)
                             (ts-common/contact-info-group)
                             (ts-common/place-search-group e! ::t-service/rentals)
                             (ts-common/external-interfaces)
                             (accessibility-group)
                             (eligibity-requirements)
                             (ts-common/service-url
                              (tr [:field-labels :transport-service-common ::t-service/rental-service])
                              ::t-service/rental-service)
                             (pick-up-locations e!)
                             ]]
    [:div.row
     [:div {:class "col-lg-12"}
      [:div
       [:h3 (tr [:rentals-page :header-rental-service])]]
      [form/form options groups (get service ::t-service/rentals)]]]))
