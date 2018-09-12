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

(def date1-highlight-color "rgba(53,140,217,1)")
(def date1-highlight-color-hover "rgba(53,140,217,0.5)")
(def date2-highlight-color "rgba(219,25,169,1)")
(def date2-highlight-color-hover "rgba(219,25,169,0.5)")

(defn date1-highlight-style
  ([]
   (date1-highlight-style "rgba(0,0,0,0)"))
  ([hash-color]
   (date1-highlight-style hash-color date1-highlight-color))
  ([hash-color highlight-color]
   {:background (str "radial-gradient(circle at center, " highlight-color " 50%, " hash-color " 40%) 0px 0px")
    :color "#E1E1F9"}))

(defn date2-highlight-style
  ([]
   (date2-highlight-style "rgba(0,0,0,0)"))
  ([hash-color]
   (date2-highlight-style hash-color date2-highlight-color))
  ([hash-color highlight-color]
   {:background (str "radial-gradient(circle at center, " highlight-color " 50%, " hash-color " 40%) 0px 0px")
    :color "#F6C6EA"}))

(def section
  {:border "solid 1px #646464"
   :padding-bottom "1.25rem"
   :margin-bottom "2.5rem"})

(def section-title
  {:background-color "#646464"
   :color "white"
   :font-size "1.125rem"
   :font-family "Montserrat"
   :padding "0.875rem 0.875rem 0.875rem 0.625rem"
   :line-height 2
   :font-weight "600"})

(def section-header
  {:padding "1rem"
   :background-color "#F0F0F0"
   :line-height 1.5})

(def section-body
  {:padding-left "1.25rem"
   :padding-right "1.25rem"})
