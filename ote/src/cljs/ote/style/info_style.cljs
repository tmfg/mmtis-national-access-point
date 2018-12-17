(ns ote.style.info-style
  (:require [stylefy.core :as stylefy]))


(def info-button
  {:background-color "#E6E6E6"
   :width "100%"
   :padding "1rem"
   :border "none"
   :text-align "left"
   :transition "background-color 0.15s ease-in-out"
   :display "flex"
   :align-items "center"
   :color "#323232"
   ::stylefy/mode {:hover {:background-color "#CCCCCC"}}})

(def info-container
  {:margin-bottom "1rem"})

(def info-icon
  {:color "#505050"
   :margin-right "0.5rem"})

(def info-text
  {:background-color "#F0F0F0"
   :transform-origin "top"
   :display "block"
   :margin 0
   :padding "1rem"
   ::stylefy/mode {:hover {:background-color "#CCCCCC"}}})

(def info-open
  (merge info-text
    {:display "block"}))

(def info-closed
  (merge info-text
    {:display "none"}))

