(ns ote.views.transport-service
  "Transport service related functionality"
  (:require [reagent.core :as reagent]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.app.controller.transport-service :as ts]
            [ote.app.controller.transport-operator :as to]
            [ote.db.transport-service :as t-service]
            [ote.db.transport-operator :as t-operator]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]
            [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.views.passenger-transportation :as pt]
            [ote.views.terminal :as terminal]
            [ote.app.routes :as routes]
            [ote.views.brokerage :as brokerage]
            [ote.views.parking :as parking]
            [ote.views.rental :as rental]
            [ote.ui.form-fields :as form-fields]
            [ote.ui.common :as ui-common]))

(def modified-transport-service-types
  ;; Create order for service type selection dropdown
  [:passenger-transportation-taxi
   :passenger-transportation-request
   :passenger-transportation-other
   :passenger-transportation-schedule
   :terminal
   :rentals
   :parking])

(defn select-service-type [e! state]
  (let [multiple-operators (if (second (:transport-operators-with-services state)) true false)
        disabled? (if (or (nil? (get-in state [:transport-service :transport-service-type-subtype]))
                          (nil? (get state :transport-operator))
                           ) true false)]
  [:div.row
   [:div {:class "col-sx-12 col-md-12"}
    [:div
     [:h1 (tr [:select-service-type-page :title-required-data])]]
    [:div.row
     [:p (tr [:select-service-type-page :transport-service-type-selection-help-text])]]
    [:div.row {:style {:padding-top "20px"}}

      (if multiple-operators
        [:div
          [:div {:class "col-sx-12 col-sm-4 col-md-4"}
          [form-fields/field
            {:label (tr [:field-labels :transport-service-type-subtype])
             :name        :transport-service-type-subtype
             :type        :selection
             :update!      #(e! (ts/->SelectServiceType %))
             :show-option (tr-key [:service-type-dropdown])
             :options     modified-transport-service-types
             :auto-width? true}
            (get-in state [:transport-service :transport-service-type-subtype])]
            ]

           [:div {:class "col-sx-12 col-sm-4 col-md-4"}
             [form-fields/field
              {:label (tr [:field-labels :select-transport-operator])
               :name        :select-transport-operator
               :type        :selection
               :update!     #(e! (to/->SelectOperator %))
               :show-option ::t-operator/name
               :options     (map :transport-operator (:transport-operators-with-services state))
               :auto-width? true}
              (get state :transport-operator)]
              ]
             [:div {:class "col-sx-12 col-sm-4 col-md-4"}
             [ui/raised-button {:style {:margin-top "20px"}
                                :label    (tr [:buttons :add-transport-service])
                                :on-click #(e! (ts/->SelectTransportServiceType))
                                :primary  true
                                :disabled disabled?}]]]
        ; else
        [:div {:class "col-sx-12 col-sm-4 col-md-4"}
         [form-fields/field
          {:label (tr [:field-labels :transport-service-type-subtype])
           :name        :transport-service-type-subtype
           :type        :selection
           :update!     #(e! (ts/->SelectOnlyServiceType %))
           :show-option (tr-key [:service-type-dropdown])
           :options     modified-transport-service-types
           :auto-width? true}
          (get-in state [:transport-service :transport-service-type-subtype])]])]]]))

(defn edit-service [e! type {service :transport-service :as app}]
  [:span
   (case type
     :passenger-transportation [pt/passenger-transportation-info e! (:transport-service app)]
     :terminal [terminal/terminal e! (:transport-service app)]
     :rentals [rental/rental e! (:transport-service app)]
     :parking [parking/parking e! (:transport-service app)]
     :brokerage [brokerage/brokerage e! (:transport-service app)])])

(defn edit-service-by-id [e! app]
  (e! (ts/->ModifyTransportService (get-in app [:params :id])))
  (fn [e! {loaded? :transport-service-loaded? service :transport-service :as app}]
    (if (or (nil? service) (not loaded?))
      [:div.loading [:img {:src "/base/images/loading-spinner.gif"}]]
      [edit-service e! (::t-service/type service) app])))

(defn edit-new-service [e! app]
  (e! (ts/->SetNewServiceType (keyword (get-in app [:params :type]))))
  (fn [e! app]
    [edit-service e! (keyword (get-in app [:params :type]))  app]))
