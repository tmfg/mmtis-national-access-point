(ns ote.views.front-page
  "Front page for OTE service - Select service type and other basic functionalities"
  (:require [reagent.core :as reagent]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.ui.napit :as napit]
            [ote.app.controller.front-page   :as fp]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]))

(defn front-page [e! status]

  ;; init
  (e! (fp/->GetTransportOperator))

  (fn [e! status]
    [:div
    [:div.row {:class "main-notification-panel"}
       [:div {:class "col-xs-1"}
       [ic/action-info-outline]
       ]
       [:div {:class "col-xs-11"}
        [:p "Et ole vielä kirjannut liikkumispalvelutietoja FINAP-palveluun.
        Voit lisätä olemassaolevan palvelurajapintasi tai täyttää palvelusi olennaiset tiedot käyttämällä
        Liikenneviraston tähän tarkoitukseen kehittämää OTE-palvelua."]
        #_[ui/raised-button {:label    "Hae operaattori"
                           :icon     (ic/social-group)
                           :on-click #(e! (fp/->GetTransportOperator))
                           :primary true
                           }]
        ]

      ]
    [:div.row
     [:div {:class "col-xs-12 col-md-offset-2 col-md-4"}
      [ui/raised-button {:label    "Lisää rajanpinta"
                         :icon     (ic/social-group)
                         :on-click #(e! (fp/->AddTransportService))
                         :primary true
                         }]
      ]
     [:div {:class "col-xs-12 col-md-6"}
      [ui/raised-button {:label    "Kirjaa olennaiset tiedot"
                         :icon     (ic/social-group)
                         :on-click #(e! (fp/->ModifyTransportOperator))
                         :primary true
                         }]]]]))