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
            [ote.localization :refer [tr tr-key]]
            [ote.db.transport-service :as t-service]
            [ote.db.transport-operator :as t-operator]))

(defn transport-services-listing [e! transport-operator-id services]
  (when services
    [:div.row
     [:div {:class "col-xs-12  col-md-12"}
      [:h3 " Henkilöiden kuljetuspalvelut"]

      [ui/table {:class "front-page-service-table" }
       [ui/table-header {:adjust-for-checkbox false
                         :display-select-all false}
        [ui/table-row {:selectable false}
         [ui/table-header-column {:class "hidden-xs hidden-sm " :style {:width "10px"}} "Id"]
         [ui/table-header-column "Palvelun nimi"]
         [ui/table-header-column "Palvelun verkko-osoite"]
         [ui/table-header-column {:class "hidden-xs "} "NAP-tila"]
         [ui/table-header-column {:class "hidden-xs hidden-sm "} "Muokattu"]
         [ui/table-header-column {:class "hidden-xs hidden-sm "} "Luotu"]
         [ui/table-header-column "Toiminnot"]]]

       [ui/table-body {:display-row-checkbox false}
        (doall
         (map-indexed
          (fn [i {::t-service/keys [id type published?] :as row}]
            ^{:key i}
             [ui/table-row {:selectable false :display-border false}
             [ui/table-row-column {:class "hidden-xs hidden-sm " :style {:width "70px"}} (get row :ote.db.transport-service/id)]
             [ui/table-row-column
              [:a.front-page-link {:href "#" :on-click #(e! (ts/->ModifyTransportService id))} "Savon taksipalvelu"]]
             [ui/table-row-column
              (if published?
                (let [url (str "/ote/export/geojson/" transport-operator-id "/" id)]
                  [:a {:href url :target "_blank"} url])
                [:span.publish
                 (tr [:field-labels :transport-service ::t-service/published?-values false])
                 [ui/flat-button {:primary true
                                  :on-click #(e! (ts/->PublishTransportService id))}
                  (tr [:buttons :publish])]])]
             [ui/table-row-column {:class "hidden-xs "} (tr [:field-labels :transport-service ::t-service/published?-values published?])]
              [ui/table-row-column {:class "hidden-xs hidden-sm "} "12.08.2017"]
              [ui/table-row-column {:class "hidden-xs hidden-sm "} "12.08.2017"]
              [ui/table-row-column
              [ui/icon-button {:on-click #(e! (ts/->ModifyTransportService id))}
               [ic/content-create]]

              [ui/icon-button {:on-click #(e! (ts/->DeleteTransportService id))}
               [ic/action-delete]]
              ]
             ])
          services))]]]]))

(defn empty-header-for-front-page [e!]
  [:div
  [:div.row
    [:div {:class "col-xs-12"}
     [:div {:class "main-notification-panel"}
      [:div {:class "col-xs-1"}
       [ic/action-info-outline]]
      [:div {:class "col-xs-11"}
       [:p (tr [:common-texts :front-page-help-text])]]]]]


   [:div.row {:style {:margin-top "50px"}}
    [:div.front-page-add-service {:class "col-xs-12 col-md-offset-1 col-md-5"}
     [:div.row {:style {:height "100px"}}
      [:p "Sinulla on jo oma, itse ylläpidetty rajapinta (esimerkiksi GTFS-muodossa), jonka haluat julkaista"]
      ]
     [:div.row {:style {:text-align "center"}}
     [ui/raised-button {:label    "Lisää itse ylläpitämäsi rajapinta"
                        ;:icon     (ic/content-add)
                        :on-click #(e! (fp/->ChangePage :transport-service))
                        :primary  true
                        }]]]
    [:div {:class "col-xs-12 col-md-6" :style {:padding-left "20px;"} }
     [:div.row {:style {:padding-left "40px" :height "100px"}}
      [:p "Haluat digitoida liikkumispalvelusi olennaisia tietoja?"]
      ]
     [:div.row {:style {:text-align "center"}}
     [ui/raised-button {:label    "Digitoi liikkumispalvelusi tiedot"
                        ;:icon     (ic/social-group)
                        :on-click #(e! (fp/->ChangePage :transport-service))
                        :primary  true
                        }]]]]]
  )

(defn table-container-for-front-page [e! services status]
  [:div
   [:div.row
    [:div {:class "col-xs-12 col-sm-6 col-md-8"}
     [:h3 (tr [:common-texts :own-api-list])]
     ]
    [:div {:class "col-xs-12 col-sm-6 col-md-4"}
     [ui/raised-button {:style {:margin-top "20px"}
                        :label    "Digitoi liikkumispalvelusi tiedot"
                        ;:icon     (ic/content-add)
                        :on-click #(e! (fp/->ChangePage :transport-service))
                        :primary  true}]]]
   [:div.row
    [:div {:class "col-xs-12  col-md-12"}
     ;; Table for transport services
     [transport-services-listing e! (get-in status [:transport-operator ::t-operator/id]) services]]]]

  )

(defn front-page [e! status]

  ;; init
  (e! (fp/->GetTransportOperatorData))

  (fn [e! {services :transport-services :as status}]
    [:div

     (if (empty? services)
       (empty-header-for-front-page e!)
       (table-container-for-front-page e! services status)
       )]))
