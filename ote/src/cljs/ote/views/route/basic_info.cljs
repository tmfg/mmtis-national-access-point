(ns ote.views.route.basic-info
  "Route wizard: basic info form"
  (:require [ote.ui.form :as form]
            [ote.app.controller.route :as rc]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transit :as transit]
            [cljs-react-material-ui.reagent :as ui]))

(defn basic-info [e! app]
  (fn [e! {route :route :as app}]
    [:div.route-basic-info
     [form/form {:update! #(e! (rc/->EditRoute %))}
      [(form/group
        {:label "Reitin nimi ja palveluntuottaja"
         :columns 2
         :layout :row}
        {:name ::transit/name
         :type :string
         :label "Reitin nimi"
         :required? true}
        {:name ::transit/transport-operator-id
         :option-value ::t-operator/id
         :type :selection
         :label "Palveluntuottaja"
         :options (mapv :transport-operator (:transport-operators-with-services app))
         :show-option ::t-operator/name}

        ;; Departure and destination
        {:name ::transit/departure-point-name
         :type :string
         :label "Lähtöpaikka"}
        {:name ::transit/destination-point-name
         :type :string
         :label "Määränpää"}

        ;; Availability of this route
        {:name ::transit/available-from
         :type :date-picker
         :label "Voimassa alkaen"}
        {:name ::transit/available-to
         :type :date-picker
         :label "Voimassa asti"})]
      route]]))
