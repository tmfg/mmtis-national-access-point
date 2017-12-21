(ns ote.style.service-viewer
  "Styles for service viewer (GeoJSON view)"
  (:require [stylefy.core :as stylefy]))

(def properties-table {:text-align "left"
                       ;;:border "solid 1px black"
                       ;;:border-collapse "collapse"

                       })



(def striped-even {:background-color "#f2f2f2"})
(def striped-odd {:background-color "white"})

(def striped-styles [striped-even striped-odd])

(def border {}  #_{:border-bottom "solid #f1f1f1 1px"})

(def th (merge {:vertical-align "top"
                :padding-top "0.2em"
                :padding-right "0.5em"}
               border))

(def td border)
