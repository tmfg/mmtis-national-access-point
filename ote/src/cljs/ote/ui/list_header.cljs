(ns ote.ui.list-header
  "Header component for OTE list pages"
  (:require [cljs-react-material-ui.reagent :as ui]
            [ote.ui.common :as common]))

(defn header [title add-button operator-selection]
  [:div
   [:div.row [:h1 title]]
   [:div.row operator-selection]
   [:div.row add-button]])