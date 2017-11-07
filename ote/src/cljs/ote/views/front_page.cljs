(ns ote.views.front-page
  "Front page for OTE service - Select service type and other basic functionalities"
  (:require [reagent.core :as reagent]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.ui.buttons :as buttons]
            [ote.app.controller.front-page :as fp]
            [ote.app.controller.transport-service :as ts]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]
            [ote.db.transport-service :as t-service]
            [ote.db.transport-operator :as t-operator]
            [ote.db.modification :as modification]
            [ote.time :as time]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]))

(defn transport-services-table-rows [e! services transport-operator-id]
  [ui/table-body {:display-row-checkbox false}
   (doall
     (map-indexed
      (fn [i {::t-service/keys [id type published? name]
              ::modification/keys [created modified] :as row}]
         ^{:key i}
         [ui/table-row {:selectable false :display-border false}
          [ui/table-row-column {:class "hidden-xs hidden-sm " :style {:width "70px"}} (get row :ote.db.transport-service/id)]
          [ui/table-row-column
           [:a {:href (str "/ote/index.html#/edit-service/" id) } name]]
          [ui/table-row-column
           (if published?
             (let [url (str "/ote/export/geojson/" transport-operator-id "/" id)]
               [:a {:href url :target "_blank"} url])
             [:span.draft
              (tr [:field-labels :transport-service ::t-service/published?-values false])])]
          [ui/table-row-column {:class "hidden-xs "} (tr [:field-labels :transport-service ::t-service/published?-values published?])]
          [ui/table-row-column {:class "hidden-xs hidden-sm "} (time/format-timestamp-for-ui modified)]
          [ui/table-row-column {:class "hidden-xs hidden-sm "} (time/format-timestamp-for-ui created)]
          [ui/table-row-column
           [ui/icon-button {:on-click #(e! (ts/->ModifyTransportService id))}
            [ic/content-create]]
           [ui/icon-button {:on-click #(e! (ts/->DeleteTransportService id))}
            [ic/action-delete]]]])
       services))])

(defn transport-services-listing [e! transport-operator-id services section-label]
  (when (> (count services) 0)
    [:div.row
     [:div {:class "col-xs-12 col-md-12"}
      [:h3 section-label]

      [ui/table (stylefy/use-style style-base/front-page-service-table)
       [ui/table-header {:adjust-for-checkbox false
                         :display-select-all false}
        [ui/table-row {:selectable false}
         [ui/table-header-column {:class "hidden-xs hidden-sm " :style {:width "10px"}} "Id"]
         [ui/table-header-column (tr [:front-page :table-header-service-name])]
         [ui/table-header-column (tr [:front-page :table-header-service-url])]
         [ui/table-header-column {:class "hidden-xs "} (tr [:front-page :table-header-NAP-status])]
         [ui/table-header-column {:class "hidden-xs hidden-sm "} (tr [:front-page :table-header-modified])]
         [ui/table-header-column {:class "hidden-xs hidden-sm "} (tr [:front-page :table-header-created])]
         [ui/table-header-column (tr [:front-page :table-header-actions])]]]

       (transport-services-table-rows e! services transport-operator-id)]]]))

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
    [:div (stylefy/use-style style-base/front-page-add-service {::stylefy/with-classes ["col-xs-12 col-md-offset-1 col-md-5"]})
     [:div.row {:style {:height "100px"}}
      [:p (tr [:front-page :do-you-want-to-publish-service])]
      ]
     [:div.row {:style {:text-align "center"}}
     [ui/raised-button {:label    (tr [:buttons :add-hosted-service])
                        :on-click #(e! (fp/->ChangePage :transport-service))
                        :primary  true
                        }]]
     [:div (stylefy/use-style style-base/header-font {::stylefy/with-classes ["row"]})
      (tr [:front-page :header-explain-more])]
     [:div.row
      [:p (tr [:front-page :help-more-explanation1])]
     [:p (tr [:front-page :help-more-explanation2])]
     ]]
    [:div {:class "col-xs-12 col-md-6" :style {:padding-left "20px"} }
     [:div.row {:style {:padding-left "40px" :height "100px"}}
      [:p (tr [:common-texts :front-page-start-ote-query-header])]
      ]
     [:div.row {:style {:text-align "center"}}
     [ui/raised-button {:label    (tr [:buttons :add-transport-service])
                        :on-click #(e! (fp/->ChangePage :transport-service))
                        :primary  true}]]]]])

(defn table-container-for-front-page [e! services status]
  [:div
   [:div.row
    [:div {:class "col-xs-12 col-sm-6 col-md-8"}
     [:h3 (tr [:common-texts :own-api-list])]
     ]
    [:div {:class "col-xs-12 col-sm-6 col-md-4"}
     [ui/raised-button {:style {:margin-top "20px"}
                        :label    (tr [:buttons :add-transport-service])
                        :on-click #(e! (fp/->ChangePage :transport-service))
                        :primary  true}]]]
   [:div.row
    [:div {:class "col-xs-12 col-md-12"}
     ;; Table for transport services

     (for [type t-service/transport-service-types
           :let [services (filter #(= (:ote.db.transport-service/type %) type) services)]
           :when (not (empty? services))]
       ^{:key type}
       [transport-services-listing
        e!
        (get-in status [:transport-operator ::t-operator/id])
        services
        (tr [:titles type])])]]])

(defn front-page [e! app]
  ;; init
  (e! (fp/->GetTransportOperatorData))

  (fn [e! {services :transport-services :as app}]
    [:div
     [:div.row.col-xs-12
      [:h2 (str (tr [:front-page :hello]) (get-in app [:user :name]) " !")]]
     [:diw.row.col-xs-12
      [:p (tr [:front-page :NAP-service-short-description])]]
     [:diw.row.col-xs-12
      [:p [:strong (tr [:common-texts :navigation-own-service-list])]
       (tr [:front-page :own-services-short-description])]]
     [:diw.row.col-xs-12
      [:p (tr [:front-page :right-corner-description])
       [:strong (tr [:common-texts :user-menu-service-operator])]
       (tr [:front-page :service-operator-short-description])]]

     [:div.row.col-xs-12 {:style {:text-align "center" :padding-top "40px"}}
      [:p (tr [:front-page :publish-new-service])]]
     [:div.row.col-xs-12 {:style {:text-align "center"}}
      [ui/raised-button {:style {:margin-top "20px"}
                         :label    (tr [:front-page :move-to-services-page])
                         :on-click #(e! (fp/->ChangePage :own-services))
                         :primary  true}]]]))

(defn own-services [e! status]
  ;; init
  (e! (fp/->GetTransportOperatorData))

  (fn [e! {services :transport-services :as status}]
    [:div

     (if (empty? services)
       (empty-header-for-front-page e!)
       (table-container-for-front-page e! services status))]))
