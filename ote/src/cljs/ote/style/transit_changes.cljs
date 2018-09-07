(ns ote.style.transit-changes
  "Transit changes styling"
  (:require [stylefy.core :as stylefy]))

(def add-color "rgb(0,170,0)")
(def remove-color "rgb(221,0,0)")
(def no-change-color "rgb(235,235,235)")

(def transit-changes-legend
  {:display "block"
   :padding "1em"
   :background-color "rgb(235,235,235)"})

(def transit-changes-legend-icon
  {:display "inline-block"
   :margin-right "1rem"})

(def change-icon-value
  {:display "inline-block"
   :position "relative"
   :top "-0.5rem"
   :left "0.2rem"})

(defn date1-highlight-style
  ([]
   (date1-highlight-style "rgba(0,0,0,0)"))
  ([hash-color]
   {:background (str "radial-gradient(circle at center, #353CD9 60%, " hash-color " 40%) 0px 0px")
    :color "#E1E1F9"}))

(defn date2-highlight-style
  ([]
   (date2-highlight-style "rgba(0,0,0,0)"))
  ([hash-color]
   {:background (str "radial-gradient(circle at center, #DB19A9 60%, " hash-color " 40%) 0px 0px")
    :color "#F6C6EA"}))
