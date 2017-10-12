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
    [:div
     (when (nil? (get status :transport-services))
       [:div.row {:class "main-notification-panel"}
        [:div {:class "col-xs-1"}
         [ic/action-info-outline]
         ]
        [:div {:class "col-xs-11"}
         [:p (tr [:common-texts :front-page-help-text])
          ]
         ]

        ]

       [:div.row
        [:div {:class "col-xs-12 col-md-offset-2 col-md-4"}
         [ui/raised-button {:label    (tr [:common-texts :link-add-new-api])
                            :icon     (ic/content-add)
                            :on-click #(e! (fp/->ChangePage :transport-service))
                            :primary  true
                            }]
         ]
       ]
       )
     (when (not= nil (get status :transport-services))
       [:div.row
        [:div {:class "col-xs-12  col-md-8"}
          [:h3 (tr [:common-texts :own-api-list])]
         ]
        [:div {:class "col-xs-12 col-md-4"}
         [ui/raised-button {:label    (tr [:common-texts :link-add-new-api])
                            :icon     (ic/content-add)
                            :on-click #(e! (fp/->ChangePage :transport-service))
                            :primary  true
                            }]
         ]
        ]
       )

     ;; Table for transport services
     (when (not= nil (get status :transport-services))
       [:div.row
        [:div {:class "col-xs-12  col-md-12"}
        [:h3  (tr [:common-texts :passenger-transportation-service-text])]

        [ui/table
         [ui/table-header {:adjust-for-checkbox false
                           :display-select-all false}
          [ui/table-row {:selectable false}
           [ui/table-header-column "Id"]
           [ui/table-header-column (tr [:common-texts :api-name])]
           [ui/table-header-column (tr [:common-texts :api-url])]
           [ui/table-header-column "Muut tietoaineistot"]
           [ui/table-header-column (tr [:common-texts :finap-status])]
           ]
          ]

         [ui/table-body {:display-row-checkbox false}
          (doall
            (map-indexed
              (fn [i row]
                [ui/table-row {:selectable false :display-border false}
                 [ui/table-row-column (get row :ote.db.transport-service/id)]
                 [ui/table-row-column
                  [:a
                   {:href "#"
                    :on-click #(e! (ts/->ModifyTransportService (get row :ote.db.transport-service/id)))
                    }
                    (get row :ote.db.transport-service/type)]]
                 [ui/table-row-column (get row :ote.db.transport-service/id)]
                 [ui/table-row-column (get row :ote.db.transport-service/published?)]
                 [ui/table-row-column (str (get row :ote.db.transport-service/published?))]
                ]
              )
              (get status :transport-services)))
             ]
         ]
        ]
        ]
       )
     ]))