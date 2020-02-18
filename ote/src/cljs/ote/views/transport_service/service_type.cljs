(ns ote.views.transport-service.service-type
  "Select type for transport service"
  (:require [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [reagent.core :as r]
            [ote.db.transport-service :as t-service]
            [ote.db.transport-operator :as t-operator]
            [ote.localization :refer [tr tr-key]]
            [stylefy.core :as stylefy]
            [ote.theme.colors :as colors]
            [ote.style.base :as style-base]
            [ote.style.form :as style-form]
            [ote.style.dialog :as style-dialog]
            [ote.ui.buttons :as buttons]
            [ote.ui.circular_progress :as circular-progress]
            [ote.ui.info :as info]
            [ote.ui.form-fields :as form-fields]
            [ote.ui.common :as ui-common]
            [ote.app.controller.transport-service :as ts-controller]
            [ote.app.controller.transport-operator :as to]
            [ote.views.transport-service.passenger-transportation :as pt]
            [ote.views.transport-service.terminal :as terminal]
            [ote.views.transport-service.parking :as parking]
            [ote.views.transport-service.rental :as rental]))

(def modified-transport-service-types
  ;; Create order for service type selection dropdown
  [:taxi
   :request
   :schedule
   :terminal
   :rentals
   :parking])

(defn select-service-type [e! {:keys [transport-operator transport-service] :as state}]
  (let [disabled? (or (nil? (::t-service/sub-type transport-service))
                      (nil? (::t-operator/id transport-operator)))]
    [:div.row
     [:div {:class "col-sx-12 col-md-12"}
      [:div
       [:h1 (tr [:select-service-type-page :title-required-data])]]
      [:div.row
       [info/info-toggle
        (tr [:common-texts :instructions])
        [:span
         [:p (tr [:select-service-type-page :transport-service-type-selection-help-text])]
         [:p (tr [:select-service-type-page :transport-service-type-brokerage-help-text])]
         [:p {:style {:font-style "italic"}}
          (tr [:select-service-type-page :transport-service-type-selection-help-example])]]
        {:default-open? false}]]

      [:div.row
       [:div
        [:div {:class "col-sx-12 col-sm-4 col-md-4"}
         [form-fields/field

          {:label (tr [:field-labels :transport-service-type-subtype])
           :name ::t-service/sub-type
           :type :selection
           :update! #(e! (ts-controller/->SelectServiceType %))
           :show-option (tr-key [:enums ::t-service/sub-type])
           :options modified-transport-service-types
           :auto-width? true}

          (::t-service/sub-type transport-service)]]

        [:div {:class "col-sx-12 col-sm-4 col-md-4"}
         [form-fields/field
          {:label (tr [:field-labels :select-transport-operator])
           :name :select-transport-operator
           :type :selection
           :update! #(e! (to/->SelectOperator %))
           :show-option ::t-operator/name
           :options (mapv :transport-operator (:transport-operators-with-services state))
           :auto-width? true}

          transport-operator]]]]
      [:div.row
       [:div {:class "col-sx-12 col-sm-4 col-md-4"}
        [ui/raised-button {:id "own-service-next-btn"
                           :style {:margin-top "20px"}
                           :label (tr [:buttons :next])
                           :on-click #(e! (ts-controller/->NavigateToNewService))
                           :primary true
                           :disabled disabled?}]]]]]))