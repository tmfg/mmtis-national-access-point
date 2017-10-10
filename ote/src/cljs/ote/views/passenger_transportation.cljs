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
            [ote.ui.debug :as debug]
            [ote.db.transport-service :as transport-service]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]))

(defn transportation-form-options [e!]
  {:name->label (tr-key [:field-labels :passenger-transportation])
   :update!     #(e! (pt/->EditPassengerTransportationState %))
   :name        #(tr [:olennaiset-tiedot :otsikot %])
   :footer-fn   (fn [data]
                  [napit/tallenna {:on-click #(e! (pt/->SavePassengerTransportationToDb))
                                   :disabled (form/disable-save? data)}
                   (tr [:buttons :save])])})

(defn passenger-transportation-info [e! status]
  [:div.row
   [:div {:class "col-lg-12"}
    [:div
     [:h3 "Vaihe 2: Täydennä henkilökuljetukseen liittyvät tiedot."]]
    [form/form (transportation-form-options e!)

     [
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

     (get status :ote.db.transport-service/passenger-transportation)]

    ]])
