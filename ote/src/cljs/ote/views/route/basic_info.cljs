(ns ote.views.route.basic-info
  "Route wizard: basic info form"
  (:require [ote.ui.form :as form]
            [ote.app.controller.route.route-wizard :as rw]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transit :as transit]
            [cljs-react-material-ui.reagent :as ui]
            [ote.localization :refer [tr tr-key]]))

(defn basic-info [e! app]
  (fn [e! {route :route :as app}]
    [:div.route-basic-info
     [form/form {:update! #(e! (rw/->EditBasicInfo %))}
      [(form/group
        {:label (tr [:route-wizard-page :basic-info-header])
         :columns 3
         :layout :row}
        {:name ::transit/name
         :type :string
         :label (tr [:route-wizard-page :basic-info-route-name])
         :required? true}
        {:name ::transit/transport-operator-id
         :option-value ::t-operator/id
         :type :selection
         :label (tr [:route-wizard-page :basic-info-transport-operator])
         :options (mapv :transport-operator (:transport-operators-with-services app))
         :show-option ::t-operator/name}

        ;; Departure and destination
        {:name ::transit/departure-point-name
         :type :string
         :label (tr [:route-wizard-page :basic-info-departure-point-name])}
        {:name ::transit/destination-point-name
         :type :string
         :label (tr [:route-wizard-page :basic-info-destination-point-name])}

        ;; Availability of this route
        {:name ::transit/available-from
         :type :date-picker
         :label (tr [:route-wizard-page :basic-info-available-from])}
        {:name ::transit/available-to
         :type :date-picker
         :label (tr [:route-wizard-page :basic-info-available-to])})]
      route]]))
