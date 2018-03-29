(ns ote.views.route
  "Main views for route based traffic views"
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [ote.app.controller.route.route-wizard :as rw]
            [ote.ui.buttons :as buttons]
            [ote.localization :refer [tr tr-key]]
            [ote.db.transit :as transit]

            ;; Subviews for wizard
            [ote.views.route.basic-info :as route-basic-info]
            [ote.views.route.stop-sequence :as route-stop-sequence]
            [ote.views.route.trips :as route-trips]))

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
      [buttons/save {:disabled (not (rw/valid-route? (:route app)))
                     :on-click #(do
                                  (.preventDefault %)
                                  (e! (rw/->SaveToDb true)))}
       (tr [:buttons :save-and-publish])]
      [buttons/save {:on-click #(do
                                  (.preventDefault %)
                                  (e! (rw/->SaveToDb false)))}
       (tr [:buttons :save-as-draft])]
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
     (when (not (rw/valid-route? (:route app)))
       [ui/card {:style {:margin "1em 0em 1em 0em"}}
        [ui/card-text {:style {:color "#be0000" :padding-bottom "0.6em"}} (tr [:form-help :publish-missing-required])]])
     [:div.col-xs-12.col-sm-6.col-md-6 {:style {:padding-top "20px"}}
      (if (get-in app [:route ::transit/published?])
        [:span
         [buttons/save {:disabled (not (rw/valid-route? (:route app)))
                        :on-click #(do
                                     (.preventDefault %)
                                     (e! (rw/->SaveToDb true)))}
          (tr [:buttons :save])]
         [buttons/save {:on-click #(do
                                  (.preventDefault %)
                                  (e! (rw/->SaveToDb false)))}
          "Muuta luonnokseksi"]]
        [:span
         [buttons/save {:disabled (not (rw/valid-route? (:route app)))
                        :on-click #(do
                                     (.preventDefault %)
                                     (e! (rw/->SaveToDb true)))}
          (tr [:buttons :save-and-publish])]
         [buttons/save {:on-click #(do
                                  (.preventDefault %)
                                  (e! (rw/->SaveToDb false)))}
           (tr [:buttons :save-as-draft])]])
      [buttons/cancel {:on-click #(do
                                    (.preventDefault %)
                                    (e! (rw/->CancelRoute)))}
       (tr [:buttons :cancel])]]]))
