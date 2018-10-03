(ns ote.style.tabs
  "Styles for own tabs component"
  (:require [stylefy.core :as stylefy]
            [cljs-react-material-ui.core :refer [color]]))


(def tab {:font-weight "bold"
         :font-size "1em"
         :display "inline"
         :padding "0px 20px 10px 20px"
         ::stylefy/mode {:hover {:border-bottom "4px solid #1976d2"}}})
(def tab-selected (merge tab {:border-bottom "4px solid #1976d2" :color (color :blue700)}))

(def grey-border {:width "100%" :height "2px" :background-color (color :grey300) :z-index 1})