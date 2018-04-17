(ns ote.ui.list-header
  "Header component for OTE list pages"
  (:require [cljs-react-material-ui.reagent :as ui]
            [ote.ui.common :as common]))

(defn header [title add-button operator-selection]
  [:div
   (when title
     [:div.row [:h1 title]])
   (when operator-selection
     [:div.row operator-selection])
   (when add-button
     [:div.row add-button])])