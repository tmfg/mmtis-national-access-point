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

      [ui/table
       [ui/table-header {:adjust-for-checkbox false
                         :display-select-all false}
        [ui/table-row {:selectable false}
         [ui/table-header-column "Id"]
         [ui/table-header-column "Rajapinnan nimi"]
         [ui/table-header-column "Rajapinnan verkko-osoite"]
         [ui/table-header-column "Muut tietoaineistot"]
         [ui/table-header-column "FINAP-tila"]]]

       [ui/table-body {:display-row-checkbox false}
        (doall
         (map-indexed
          (fn [i {::t-service/keys [id type published?] :as row}]
            ^{:key i}
            [ui/table-row {:selectable false :display-border false}
             [ui/table-row-column (get row :ote.db.transport-service/id)]
             [ui/table-row-column
              [:a {:href "#" :on-click #(e! (ts/->ModifyTransportService id))} type]]
             [ui/table-row-column
              (if published?
                (let [url (str "/ote/export/geojson/" transport-operator-id "/" id)]
                  [:a {:href url :target "_blank"} url])
                [:span.publish
                 (tr [:field-labels :transport-service ::t-service/published?-values false])
                 [ui/flat-button {:primary true
                                  :on-click #(e! (ts/->PublishTransportService id))}
                  (tr [:buttons :publish])]])]
             [ui/table-row-column "FIXME"]
             [ui/table-row-column (tr [:field-labels :transport-service ::t-service/published?-values published?])]])
          services))]]]]))

(defn front-page [e! status]

  ;; init
  ;(e! (fp/->GetTransportOperator))
  (e! (fp/->GetTransportOperatorData))

  (fn [e! {services :transport-services :as status}]
    [:div
     (when (nil? services)
       [:div.row {:class "main-notification-panel"}
        [:div {:class "col-xs-1"}
         [ic/action-info-outline]]
        [:div {:class "col-xs-11"}
         [:p "Et ole vielä kirjannut liikkumispalvelutietoja FINAP-palveluun.
        Voit lisätä olemassaolevan palvelurajapintasi tai täyttää palvelusi olennaiset tiedot käyttämällä
        Liikenneviraston tähän tarkoitukseen kehittämää OTE-palvelua."]]]

       [:div.row
        [:div {:class "col-xs-12 col-md-offset-2 col-md-4"}
         [ui/raised-button {:label    "Lisää rajanpinta"
                            :icon     (ic/social-group)
                            :on-click #(e! (fp/->ChangePage :transport-service))
                            :primary  true
                            }]]
        [:div {:class "col-xs-12 col-md-6"}
         [ui/raised-button {:label    "Kirjaa olennaiset tiedot"
                            :icon     (ic/social-group)
                            :on-click #(e! (fp/->ChangePage :transport-operator))
                            :primary  true
                            }]]])

     (when (not= nil (get status :transport-services))
       [:div.row
        [:div {:class "col-xs-12  col-md-8"}
          [:h3 "Omat palvelutiedot (Lisätty OTE:lla)"]]
        [:div {:class "col-xs-12 col-md-4"}
         [ui/raised-button {:label    "Lisää rajanpinta"
                            :icon     (ic/social-group)
                            :on-click #(e! (fp/->ChangePage :transport-service))
                            :primary  true}]]])

     ;; Table for transport services
     [transport-services-listing e!
      (get-in status [:transport-operator ::t-operator/id])
      services]]))
