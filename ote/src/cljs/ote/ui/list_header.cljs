(ns ote.ui.list-header
  "Header component for OTE list pages"
  (:require [cljs-react-material-ui.reagent :as ui]
            [ote.ui.common :as common]))




(defn header [title add-button operator-selection]

  [:div.row
   [:div.col-xs-12.col-sm-6.col-md-9
    [:h1 title]]]
  [:div.row operator-selection]
  [:div.row
   [:div.col-xs-12.col-sm-6.col-md-3
    add-button]]

  )