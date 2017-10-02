(ns ote.views.passenger-transportation
  "Required datas for passenger transportation provider"
  (:require [reagent.core :as reagent]
            [cljs-react-material-ui.reagent :as ui]
            [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.ui.napit :as napit]
            [ote.tiedot.palvelu :as service]
            [ote.services.transport-service-services :as transport-service-services]
            [ote.ui.debug :as debug]
            [ote.db.transport-service :as transport-service]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]))

(defn passenger-transportation-info [e! status]
  (.log js/console " Avataanko dialog " (boolean (get status :price-class-open)))
  [:div.row
   [:div {:class "col-lg-12"}
    [:div
     [:h3 "Vaihe 2: Täydennä henkilökuljetukseen liittyvät tiedot."]]
    [form/form
     {:name->label (tr-key [:field-labels])
      :update!     #(e! (service/->EditTransportService %))
      :name        #(tr [:olennaiset-tiedot :otsikot %])
      :footer-fn   (fn [data]
                     [napit/tallenna {:on-click #(e! (service/->SavePassengerTransportData))
                                      :disabled (form/disable-save? data)}
                      "Tallenna"])}

     [{
       :name ::transport-service/luggage-restrictions
       :type :localized-text
       :rows 5
       }

      ;; Payment method is a list in database so we need to enable multible choises
      {:label       "Valitseppa maksutapa"
       :name        ::transport-service/payment-methods
       :type        :multiselect-selection
       :show-option (tr-key [:enums ::transport-service/payment-methods])
       :options     transport-service/payment-methods}


      #_{
         :label "Alue"
         :name  ::transport-service/description
         :type  :localized-text
         :rows  3
         :read  (comp ::transport-service/description ::transport-service/main-operation-area)
         :write (fn [data desc]
                  (assoc-in data [::ts-definitionsmain-operation-area ::ts-definitionsdescription] desc))}

      #_{
         :label "Lokaatio"
         :name  ::transport-service/location
         :type  :string
         :read  (comp ::transport-service/location ::ts-definitionsmain-operation-area)
         :write (fn [data location]
                  (assoc-in data [::ts-definitionsmain-operation-area ::ts-definitionslocation] location))}



      (form-groups/service-url (tr [:field-labels ::transport-service/real-time-information]) ::transport-service/real-time-information)
      (form-groups/service-url (tr [:field-labels ::transport-service/booking-service]) ::transport-service/booking-service)

      {
       :name        ::transport-service/additional-services
       :type        :multiselect-selection
       :show-option (tr-key [:enums ::transport-service/additional-services])
       :options     transport-service/additional-services}

      {
       :name        ::transport-service/accessibility-tool
       :type        :multiselect-selection
       :show-option (tr-key [:enums ::transport-service/accessibility-tool])
       :options     transport-service/accessibility-tool}

      {
       :name ::transport-service/accessibility-description
       :type :localized-text
       :rows 5
       }

      ]

     status]
    [:div.row
     [:h3 "Lisää uusi hintatieto"]
     (reagent/as-element [ui/raised-button
                          {:label    "Lisää rivi"
                           ;:icon     (ic/social-group)
                           :on-click #(e! (transport-service-services/->AddPriceClassRow))
                           }]
                         )
     (.log js/console " lisätäänkö  " (:transport-service :add-price-class status))
     (when (:transport-service :add-price-class status)
       (.log js/console " lisää!!  ")

       [form/form
        {:name->label (tr-key [:field-labels])
         :update!     #(e! (service/->EditTransportService %))
         :name        #(tr [:olennaiset-tiedot :otsikot %])
         :footer-fn   (fn [data]
                        [napit/tallenna {:on-click #(e! (service/->SavePassengerTransportData))
                                         :disabled (form/disable-save? data)}
                         "Tallenna"])}

        [(form-groups/price-class (tr [:field-labels ::transport-service/price-class]) ::transport-service/price-class)
         ]
        status
        ]
       )
     ]

    [debug/debug status]
    ]
   ])
