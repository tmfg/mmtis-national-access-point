(ns ote.style.transit-changes
  "Transit changes styling"
  (:require [stylefy.core :as stylefy]))

(def add-color "rgb(0,170,0)")
(def remove-color "rgb(221,0,0)")
(def no-change-color "rgv(235,235,235)")

(def transit-changes-legend
  {:display "block"
   :padding "1em"
   :background-color "rgb(235,235,235)"})

(def transit-changes-legend-icon
  {:display "inline-block"
   :margin-right "1em"})

(def change-icon-value
  {:display "inline-block"
   :position "relative"
   :top "-0.5em"
   :left "0.2em"})
