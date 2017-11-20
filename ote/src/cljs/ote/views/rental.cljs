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
            [ote.app.controller.rental :as rental]
            [ote.db.transport-service :as t-service]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]
            [ote.views.place-search :as place-search]
            [tuck.core :as tuck]
            [ote.views.transport-service-common :as ts-common]))

(defn rental-form-options [e!]
  {:name->label (tr-key [:field-labels :rentals] [:field-labels :transport-service-common])
   :update!     #(e! (rental/->EditRentalState %))
   :name        #(tr [:olennaiset-tiedot :otsikot %])
   :footer-fn   (fn [data]
                  [buttons/save {:on-click #(e! (rental/->SaveRentalToDb))
                                 :disabled (form/disable-save? data)}
                   (tr [:buttons :save])])})

(defn name-group [e!]
  (form/group
   {:label (tr [:rentals-page :header-service-info])
    :columns 3
    :layout :row}

   {:name      ::t-service/name
    :type      :string
    :required? false}))

(defn accessibility-group []
  (form/group
   {:label (tr [:rentals-page :header-accessibility])
    :columns 3
    :layout :row}

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

(defn rental [e! service]
  (reagent/with-let [options (rental-form-options e!)
                     groups [(name-group e!)
                             (ts-common/contact-info-group)
                             (ts-common/place-search-group e! ::t-service/rentals)
                             (ts-common/external-interfaces)
                             (accessibility-group)
                             (eligibity-requirements)
                             (ts-common/service-url
                              (tr [:field-labels :transport-service-common ::t-service/booking-service])
                              ::t-service/booking-service)
                             ]]
    [:div.row
     [:div {:class "col-lg-12"}
      [:div
       [:h3 (tr [:rentals-page :header-rental-service])]]
      [form/form options groups (get service ::t-service/rentals)]]]))


