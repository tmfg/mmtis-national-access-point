(ns ote.views.route
  "Main views for route based traffic views"
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [ote.ui.leaflet :as leaflet]
            [ote.app.controller.route.route-wizard :as rw]
            [cljs-react-material-ui.icons :as ic]
            [ote.time :as time]
            [ote.ui.form :as form]
            [ote.style.route :as style-route]
            [stylefy.core :as stylefy]
            [ote.ui.buttons :as buttons]
            [ote.localization :refer [tr tr-key]]

            ;; Subviews for wizard
            [ote.views.route.basic-info :as route-basic-info]
            [ote.views.route.stop-sequence :as route-stop-sequence]
            [ote.views.route.trips :as route-trips]
            [ote.views.route.service-calendar :as route-service-calendar]))

(defn route-save [e! {route :route :as app}]
  [ui/raised-button {:primary true
                     :on-click #(e! (rw/->SaveAsGTFS))}
   (tr [:buttons :save-as-gtfs])])

(defn- route-components [e! app]
  [:div
   [route-basic-info/basic-info e! app]
   [route-stop-sequence/stop-sequence e! app]
   [route-trips/trips e! app]])

(defn new-route [e! app]
  (e! (rw/->InitRoute))
  (fn [e! app]
    [:span
     [route-components e! app]
     [:div.col-xs-12.col-sm-6.col-md-6 {:style {:padding-top "20px"}}
      [buttons/save {:disabled false
                     :on-click #(do
                                  (.preventDefault %)
                                  (e! (rw/->SaveToDb)))}
       (tr [:buttons :save])]
      [buttons/cancel {:on-click #(do
                                    (.preventDefault %)
                                    (e! (rw/->CancelRoute)))}
       (tr [:buttons :cancel])]]]))

(defn edit-route-by-id [e! app]
  (e! (rw/->LoadRoute (get-in app [:params :id])))
  (fn [e! app]
    [:span
     [:h1 (tr [:common-texts :navigation-route])]
     [route-components e! app]
     [:div.col-xs-12.col-sm-6.col-md-6 {:style {:padding-top "20px"}}
      [buttons/save {:disabled false
                     :on-click #(do
                                  (.preventDefault %)
                                  (e! (rw/->SaveToDb)))}
       (tr [:buttons :save])]
      [buttons/cancel {:on-click #(do
                                    (.preventDefault %)
                                    (e! (rw/->CancelRoute)))}
       (tr [:buttons :cancel])]]]))