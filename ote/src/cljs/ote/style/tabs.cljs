(ns ote.style.tabs
  "Styles for own tabs component"
  (:require [stylefy.core :as stylefy]
            [cljs-react-material-ui.core :refer [color]]))


(def tab {:font-weight "bold"
          :font-size "1rem"
          :white-space "nowrap"
          :text-align "center"
          :padding "10px 15px 10px 15px"
          :width "100%"
          :border-bottom (str "3px solid" (color :grey300))
          ::stylefy/mode {:hover {:border-bottom "4px solid #1976d2"}}})

(def tab-selected (merge tab {:border-bottom "4px solid #1976d2"
                              :color (color :blue700)}))

(def grey-border {:width "100%"
                  :height "2px"
                  :background-color (color :grey300)
                  :z-index 1})