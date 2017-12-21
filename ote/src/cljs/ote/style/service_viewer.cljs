(ns ote.style.service-viewer
  "Styles for service viewer (GeoJSON view)"
  (:require [stylefy.core :as stylefy]))

(def properties-table {:text-align "left"})

(def striped-even {:background-color "#f2f2f2"})
(def striped-odd {:background-color "white"})

(def striped-styles [striped-even striped-odd])

(def th {:vertical-align "top"
         :padding-top "0.2em"
         :padding-right "0.5em"})

(def td {})
