(ns ote.views.transport-service
  "Select Transport service type"
  (:require [reagent.core :as reagent]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.app.controller.transport-service :as ts]
            [ote.db.transport-service :as transport-service]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]))

(def modified-transport-service-types

  ;; Create order for service type selection dropdown
   [:passenger-transportation-taxi
    :passenger-transportation-request
    :passenger-transportation-schedule
    :terminal
    :rentals
    :parking
    :brokerage])

(defn service-form-options [e!]
  {:name->label (tr-key [:field-labels])
   :update!     #(e! (ts/->SelectTransportServiceType %))
   })

(defn select-service-type [e! status]
  [:div.row
   [:div {:class "col-sx-12 col-md-9"}
    [:div
     [:h3 (tr [:common-texts :title-required-data-with-OTE])]
     ]
    [:div.row
     [:p (tr [:common-texts :transport-service-type-selection-help-text])]
     ]
    [:div.row
     [form/form (service-form-options e!)
      [
       (form/group
         {:label   nil
          :columns 3}
         {:style {:width "100%"}
          :name        :transport-service-type-subtype
          :type        :selection
          :show-option  (tr-key [:service-type-dropdown])
          ;:options     transport-service/transport-service-types}
          :options modified-transport-service-types})
       ]
    nil]
     ]
    ]

   ]
  )
