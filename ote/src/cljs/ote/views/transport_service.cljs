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

(defn service-form-options [e!]
  {:name->label (tr-key [:field-labels])
   :update!     #(e! (ts/->SelectTransportServiceType %))
   })

(defn select-service-type [e! status]
  [:div
   [:div.row
    [:div
     [:h3 "Olennaisten tietojen kirjaaminen OTE:lla"]
     ]
    [:div.row
     [:p "Voit kirjata eri liikkumispalveluiden olennaisia tietoja valitsemalla ensin liikkumispalvelun
     tyypin ja täyttämällä sen jälkeen näytölle avautuneen lomakkeen."]
     ]
    [:div.row
     [form/form (service-form-options e!)
      [
       (form/group
         {:label   nil
          :columns 1}
         {
         :name        ::transport-service/service-type
         :type        :selection
         :show-option (tr-key [:enums ::transport-service/service-type])
         :options     transport-service/transport-service-types})
       ]
    nil]
     ]
    ]

   ]
  )
