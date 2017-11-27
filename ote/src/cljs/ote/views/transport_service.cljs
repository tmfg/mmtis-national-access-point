(ns ote.views.transport-service
  "Transport service related functionality"
  (:require [reagent.core :as reagent]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.app.controller.transport-service :as ts]
            [ote.db.transport-service :as transport-service]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]
            [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.views.passenger-transportation :as pt]
            [ote.views.terminal :as terminal]
            [ote.app.routes :as routes]
            [ote.views.brokerage :as brokerage]
            [ote.views.parking :as parking]
            [ote.views.rental :as rental]))

(def modified-transport-service-types

  ;; Create order for service type selection dropdown
   [:passenger-transportation-taxi
    :passenger-transportation-request
    :passenger-transportation-other
    :passenger-transportation-schedule
    :terminal
    :rentals
    :parking
    :brokerage])

(defn service-form-options [e!]
  {:name->label (tr-key [:field-labels])
   :update!     #(e! (ts/->SelectTransportServiceType %))})

(defn select-service-type [e! status]
  [:div.row
   [:div {:class "col-sx-12 col-md-9"}
    [:div
     [:h1 (tr [:select-service-type-page :title-required-data])]]
    [:div.row
     [:p (tr [:select-service-type-page :transport-service-type-selection-help-text])]]
    [:div.row
     [form/form (service-form-options e!)
      [
       (form/group
         {:label   nil
          :columns 3}
         {:style {:width "100%"}
          :name        :transport-service-type-subtype
          :type        :selection
          :show-option  (tr-key [:service-type-dropdown])
          :options modified-transport-service-types})]
    nil]]]])

(defn edit-service [e! app]
  (e! (ts/->ModifyTransportService (get-in app [:params :id])))
  (fn [e! {loaded? :transport-service-loaded? service :transport-service :as app}]
    (if (or (nil? service) (not loaded?))
      [:div.loading [:img {:src "/base/images/loading-spinner.gif"}]]
      (case (::transport-service/type service)
        :passenger-transportation [pt/passenger-transportation-info e! (:transport-service app)]
        :terminal [terminal/terminal e! (:transport-service app)]
        :rentals [rental/rental e! (:transport-service app)]
        :parking [parking/parking e! (:transport-service app)]
        :brokerage [brokerage/brokerage e! (:transport-service app)]))))
