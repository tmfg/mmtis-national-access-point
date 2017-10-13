(ns ote.views.passenger-transportation
  "Required datas for passenger transportation provider"
  (:require [reagent.core :as reagent]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.ui.napit :as napit]
            [ote.app.controller.transport-service :as ts]
            [ote.app.controller.passenger-transportation :as pt]
            [ote.db.transport-service :as transport-service]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]
            [ote.views.place-search :as place-search]
            [tuck.core :as tuck]))

(defn transportation-form-options [e!]
  {:name->label (tr-key [:field-labels :passenger-transportation])
   :update!     #(e! (pt/->EditPassengerTransportationState %))
   :name        #(tr [:olennaiset-tiedot :otsikot %])
   :footer-fn   (fn [data]
                  [napit/tallenna {:on-click #(e! (pt/->SavePassengerTransportationToDb))
                                   :disabled (not (form/can-save? data))}
                   (tr [:buttons :save])])})

(defn passenger-transportation-info [e! status]
  [:div.row
   [:div {:class "col-lg-12"}
    [:div
     [:h3 "Vaihe 2: Täydennä henkilökuljetukseen liittyvät tiedot."]]
    [form/form (transportation-form-options e!)

     [
      (place-search/place-search-form-group
       (tuck/wrap-path e! :transport-service ::transport-service/passenger-transportation ::transport-service/operation-area)
       (tr [:field-labels :passenger-transportation ::transport-service/operation-area])
       ::transport-service/operation-area)

      (form/group
       {:label   nil
        :columns 1}

       {:name ::transport-service/luggage-restrictions
        :type :localized-text
        :rows 5}

       ;; Payment method is a list in database so we need to enable multible choises
       {
        :name        ::transport-service/payment-methods
        :type        :multiselect-selection
        :show-option (tr-key [:enums ::transport-service/payment-methods])
        :options     transport-service/payment-methods})

      (form-groups/service-url (tr [:field-labels ::transport-service/real-time-information]) ::transport-service/real-time-information)
      (form-groups/service-url (tr [:field-labels ::transport-service/booking-service]) ::transport-service/booking-service)

      (form/group
        {:label   "Palvelun yhteystiedot"
         :columns 3
         :col-class "row-form-field"
         :class " hjee "}
        {:name        ::transport-service/contact-phone
         :type        :string
         :col-class       "row-form-field"}
        {:name        ::common/street
         :type        :string
         :col-class       "row-form-field"
         :read (comp ::common/street ::transport-service/contact-address)
         :write (fn [data street]
                  (assoc-in data [::transport-service/contact-address ::common/street] street))}
        {:name        ::transport-service/contact-email
         :type        :string}
        {:name        ::common/post-office
         :type        :string
         :read (comp ::common/post_office ::transport-service/contact-address)
         :write (fn [data post-office]
                  (assoc-in data [::transport-service/contact-address ::common/post-office] post-office))}
        {:name        ::transport-service/homepage
         :type        :string}
        {:name        ::common/postal-code
         :type        :string
         :read (comp ::common/postal_code ::transport-service/contact-address)
         :write (fn [data postal-code]
                  (assoc-in data [::transport-service/contact-address ::common/postal-code] postal-code))}


        )

      (form/group
       {:label   "Muut palvelut ja esteettömyys"
        :columns 1}

       {:name        ::transport-service/additional-services
        :type        :multiselect-selection
        :show-option (tr-key [:enums ::transport-service/additional-services])
        :options     transport-service/additional-services}

       {:name        ::transport-service/accessibility-tool
        :type        :multiselect-selection
        :show-option (tr-key [:enums ::transport-service/accessibility-tool])
        :options     transport-service/accessibility-tool}

       {:name ::transport-service/accessibility-description
        :type :localized-text
        :rows 5})

      (form/group
        {:label   "Hintatiedot"
         :columns 3
         :actions [ui/raised-button
                   {:label    "Lisää hintarivi"
                    :icon     (ic/action-note-add)
                    :on-click #(e! (ts/->AddPriceClassRow))}]}

       {:name         ::transport-service/price-classes
        :type         :table
        :table-fields [{:name ::transport-service/name :type :string}
                       {:name ::transport-service/price-per-unit :type :number}
                       {:name ::transport-service/unit :type :string}
                       {:name ::transport-service/currency :type :string :width "100px"}
                       ]
        :delete?      true
        })]

     (get status ::transport-service/passenger-transportation)]

    ]])
