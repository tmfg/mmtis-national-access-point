(ns ote.style.info-style
  (:require [stylefy.core :as stylefy]))


(def info-button
  {:background-color "#E6E6E6"
   :width "100%"
   :padding "1rem"
   :border "none"
   :text-align "left"
   :transition "background-color 0.15s ease-in-out"
   ::stylefy/mode {:hover {:background-color "#CCCCCC"}}})

(def info-text
  {:transition "max-height 0.2s ease-in-out"
   :background-color "blue"
   :transform-origin "top"
   :display "block"
   :color "orange"})

(def info-open
  (merge info-text
    {:display "block"}))

(def info-closed
  (merge info-text
    {:display "none"}))

