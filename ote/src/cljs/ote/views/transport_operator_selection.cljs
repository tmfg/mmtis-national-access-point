(ns ote.views.transport-operator-selection
  "Component for selecting a transport operator"
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]

            [ote.ui.common :as ui-common]
            [ote.ui.form-fields :as form-fields]

            [ote.app.controller.transport-operator :as to]
            [ote.app.controller.front-page :as fp]

            [ote.db.transport-operator :as t-operator]
            [ote.localization :refer [tr tr-key]]))

(defn transport-operator-selection [e! {operator :transport-operator
                                        operators :transport-operators-with-services} & extended]
  [:div
   ;; Show operator selection if there are operators and we are not creating a new one
   (when (and (not (empty? operators))
              (not (:new? operator)))
     [:div.row
     [:div.col-sm-4.col-md-3
      [form-fields/field
       {:label (tr [:field-labels :select-transport-operator])
        :name :select-transport-operator
        :type :selection
        :show-option #(::t-operator/name %)
        :update! #(e! (to/->SelectOperator %))
        :options (mapv :transport-operator operators)
        :auto-width? true}
       operator]]

      (when extended
       [:div.col-xs-12.col-sm-3.col-md-2
       [ui/flat-button {:label (tr [:buttons :edit])
                        :style {:margin-top "1.5em"
                                :font-size "8pt"}
                        :icon (ic/content-create {:style {:width 16 :height 16}})
                        :on-click #(do
                                     (.preventDefault %)
                                     (e! (fp/->ChangePage :transport-operator {:id (::t-operator/id operator)})))}]])])])
