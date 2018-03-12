(ns ote.views.route.basic-info
  "Route wizard: basic info form"
  (:require [ote.ui.form :as form]
            [ote.app.controller.route :as rc]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transit :as transit]))

(defn basic-info [e! app]
  ;; Initially select the first operator
  (e! (rc/->EditRoute
       {::transit/transport-operator-id
        (::t-operator/id
         (:transport-operator
          (first (:transport-operators-with-services app))))}))
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
         :options (map :transport-operator
                       (:transport-operators-with-services app))
         :show-option ::t-operator/name})]
      route]]))
