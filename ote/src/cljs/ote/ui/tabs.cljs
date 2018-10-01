(ns ote.ui.tabs
  "Simple Material UI data table"
  (:require [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.core :refer [color]]
            [ote.ui.common :as common]
            [reagent.core :as r]
            [cljs-react-material-ui.icons :as ic]))
;{:label "Käyttäjä" :value "users" ::tab-content [users/user-listing e! app]}
(defn tab [t]
   [ui/tab {:label (:label t) :value (:value t)
            :style {:text-transform "none" :fontWeight "600"}
            :indicator {:background-color (color :grey900)}}
    [:div
     [:div {:style {:width "100%" :height "1px" :background-color "gray" :z-index 1}}]
    (:tab-content t)]]

  )