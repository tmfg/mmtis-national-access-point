(ns ote.views.route
  "Main views for route based traffic views"
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [ote.app.controller.route.route-wizard :as rw]
            [ote.ui.buttons :as buttons]
            [ote.theme.colors :as colors]
            [ote.localization :refer [tr tr-key]]
            [ote.db.transit :as transit]
            [ote.db.transport-operator :as t-operator]

    ;; Subviews for wizard
            [ote.views.route.basic-info :as route-basic-info]
            [ote.views.route.stop-sequence :as route-stop-sequence]
            [ote.views.route.trips :as route-trips]

            [ote.ui.circular_progress :as circular-progress]
            [ote.ui.form-fields :as form-fields]))

(defn route-save [e! {route :route :as app}]
  [ui/raised-button {:primary true
                     :on-click #(e! (rw/->SaveAsGTFS))}
   (tr [:buttons :save-as-gtfs])])

(defn- operator-data [e! app]
  [:div {:style {:background-color colors/gray200
                 :padding "0rem 1rem 2rem 1rem"
                 :margin-bottom "1rem"}}

   [:div {:style {:display "flex" :flex-direction "row" :flex-wrap "nowrap"}}
    [:h3 {:style {:line-height "1rem"}} "Palveluntuottajan tiedot"]]

   [:div {:style {:display "flex" :flex-direction "row" :flex-wrap "wrap"}}
    [:div {:style {:flex 1 :padding "0 0.5rem 0 0"}}
     [form-fields/field
      {:label     (tr [:field-labels ::t-operator/name])
       :name      ::t-operator/name
       :type      :string
       :update!   nil
       :disabled? true}
      (::t-operator/name (:transport-operator app))]]
    [:div {:style {:flex 1 :padding "0 0.5rem 0 0"}}
     [form-fields/field
      {:label     (tr [:field-labels ::t-operator/business-id])
       :name      ::t-operator/business-id
       :type      :string
       :update!   nil
       :disabled? true}
      (::t-operator/business-id (:transport-operator app))]]
    [:div {:style {:flex 1 :padding "0 0.5rem 0 0"}}
     [form-fields/field
      {:label     (tr [:field-labels ::t-operator/homepage])
       :name      ::t-operator/homepage
       :type      :string
       :update!   nil}
      (::t-operator/homepage (:transport-operator app))]]]])

(defn- route-components [e! app]
  [:div
   [operator-data e! app]
   [buttons/open-link "https://s3.eu-central-1.amazonaws.com/ote-assets/sea-route-user-guide.pdf" (tr [:route-list-page :link-to-help-pdf])]
   [route-basic-info/basic-info e! app]
   [route-stop-sequence/stop-sequence e! app]
   [route-trips/trips e! app]])

(defn form-container [e! app]
  [:div
   [ote.ui.list-header/header app (tr [:common-texts :navigation-route])]
   [route-components e! app]
   (when (not (rw/valid-route? (:route app)))
     [:div {:style {:margin "1em 0em 1em 0em"}}
      [:span {:style {:color "#be0000" :padding-bottom "0.6em"}} (tr [:route-wizard-page :publish-missing-required])]])])

(defn new-route [e! app]
  (when-not (nil? (:route app))
    [:span
     [form-container e! app]
     [:div.col-xs-12.col-sm-6.col-md-6 {:style {:padding-top "20px"}}
      [buttons/save {:disabled (not (rw/valid-route? (:route app)))
                     :on-click #(do
                                  (.preventDefault %)
                                  (e! (rw/->SaveToDb true)))}
       (tr [:buttons :save-and-publish])]
      [buttons/save {:disabled (not (rw/valid-route-name? (get-in app [:route ::transit/name])))
                     :on-click #(do
                                  (.preventDefault %)
                                  (e! (rw/->SaveToDb false)))}
       (tr [:buttons :save-as-draft])]
      [buttons/cancel {:on-click #(do
                                    (.preventDefault %)
                                    (e! (rw/->CancelRoute)))}
       (tr [:buttons :cancel])]]]))

(defn edit-route-by-id [e! {route :route :as app}]
  (if (or (nil? route) (:loading? route))
    [circular-progress/circular-progress]
    [:span
     [form-container e! app]
     [:div.col-xs-12.col-sm-6.col-md-6 {:style {:padding-top "20px"}}
      (if (get-in app [:route ::transit/published?])
        [:span
         [buttons/save {:disabled (not (rw/valid-route? (:route app)))
                        :on-click #(do
                                     (.preventDefault %)
                                     (e! (rw/->SaveToDb true)))}
          (tr [:buttons :save])]
         [buttons/save {:disabled (not (rw/valid-route-name? (get-in app [:route ::transit/name])))
                        :on-click #(do
                                     (.preventDefault %)
                                     (e! (rw/->SaveToDb false)))}
          (tr [:buttons :back-to-draft])]]
        [:span
         [buttons/save {:disabled (not (rw/valid-route? (:route app)))
                        :on-click #(do
                                     (.preventDefault %)
                                     (e! (rw/->SaveToDb true)))}
          (tr [:buttons :save-and-publish])]
         [buttons/save {:disabled (not (rw/valid-name (:route app)))
                        :on-click #(do
                                     (.preventDefault %)
                                     (e! (rw/->SaveToDb false)))}
          (tr [:buttons :save-as-draft])]])
      [buttons/cancel {:on-click #(do
                                    (.preventDefault %)
                                    (e! (rw/->CancelRoute)))}
       (tr [:buttons :cancel])]]]))
