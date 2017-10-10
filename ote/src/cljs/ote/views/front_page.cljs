(ns ote.views.front-page
  "Front page for OTE service - Select service type and other basic functionalities"
  (:require [reagent.core :as reagent]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.ui.napit :as napit]
            [ote.app.controller.front-page :as fp]
            [ote.app.controller.transport-service :as ts]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]))

(defn front-page [e! status]

  ;; init
  ;(e! (fp/->GetTransportOperator))
  (e! (fp/->GetTransportOperatorData))

  (fn [e! status]
    (.log js/console " status heti alussa " (clj->js status))
    [:div
     (when (nil? (get status :transport-services))
       [:div.row {:class "main-notification-panel"}
        [:div {:class "col-xs-1"}
         [ic/action-info-outline]
         ]
        [:div {:class "col-xs-11"}
         [:p "Et ole vielä kirjannut liikkumispalvelutietoja FINAP-palveluun.
        Voit lisätä olemassaolevan palvelurajapintasi tai täyttää palvelusi olennaiset tiedot käyttämällä
        Liikenneviraston tähän tarkoitukseen kehittämää OTE-palvelua."]
         ]

        ]

       [:div.row
        [:div {:class "col-xs-12 col-md-offset-2 col-md-4"}
         [ui/raised-button {:label    "Lisää rajanpinta"
                            :icon     (ic/social-group)
                            :on-click #(e! (fp/->ChangePage :transport-service))
                            :primary  true
                            }]
         ]
        [:div {:class "col-xs-12 col-md-6"}
         [ui/raised-button {:label    "Kirjaa olennaiset tiedot"
                            :icon     (ic/social-group)
                            :on-click #(e! (fp/->ChangePage :transport-operator))
                            :primary  true
                            }]]]
       )
     (when (not= nil (get status :transport-services))
       [:div.row
        [:div {:class "col-xs-12  col-md-8"}
          [:h3 "Omat palvelutiedot (Lisätty OTE:lla)"]
         ]
        [:div {:class "col-xs-12 col-md-4"}
         [ui/raised-button {:label    "Lisää rajanpinta"
                            :icon     (ic/social-group)
                            :on-click #(e! (fp/->ChangePage :transport-service))
                            :primary  true
                            }]
         ]
        ]
       )

     ;; Table for transport services
     (when (not= nil (get status :transport-services))
       [:div.row
        [:h2 " Henkilöiden kuljetuspalvelut"]

        [ui/table
         [ui/table-header {:adjust-for-checkbox false
                           :display-select-all false}
          [ui/table-row {:selectable false}
           [ui/table-header-column "Id"]
           [ui/table-header-column "Rajapinnan nimi"]
           [ui/table-header-column "Rajapinnan verkko-osoite"]
           [ui/table-header-column "Muut tietoaineistot"]
           [ui/table-header-column "FINAP-tila"]
           ]
          ]

         [ui/table-body {:display-row-checkbox false}
          (.log js/console " (:transport-services status) " (clj->js (get status :transport-services)))
          (doall
            (map-indexed
              (fn [i row]
                [ui/table-row {:selectable false :display-border false}
                 [ui/table-row-column (get row :ote.db.transport-service/id)]
                 [ui/table-row-column
                  [:a
                   {:href "#"
                    ;:on-click #(e! (ts/->ModifyTransportService (get row :ote.db.transport-service/id)))
                    }
                    (get row :ote.db.transport-service/type)]]
                 [ui/table-row-column (get row :ote.db.transport-service/id)]
                 [ui/table-row-column (get row :ote.db.transport-service/id)]
                 [ui/table-row-column (get row :ote.db.transport-service/id)]
                ]
              )
              (get status :transport-services)))
             ]
         ]
        ]
       )
     ]))