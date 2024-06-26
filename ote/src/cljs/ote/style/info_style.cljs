(ns ote.style.info-style
  (:require [stylefy.core :as stylefy]
            [ote.theme.colors :as colors]))


(def info-button
  {:background-color colors/gray350
   :width "100%"
   :padding "1rem"
   :border "none"
   :text-align "left"
   :transition "background-color 0.15s ease-in-out"
   :display "flex"
   :align-items "center"
   :cursor "pointer"
   :color colors/gray900
   ::stylefy/mode {:hover {:background-color colors/gray450}}})

(def info-container
  {:margin-bottom "1rem"
   :font-size "0.875rem"})

(def info-icon
  {:color colors/gray800
   :margin-right "0.5rem"})

(def info-text
  {:background-color colors/gray200
   :transform-origin "top"
   :display "block"
   :margin 0
   :padding "1rem"})

(def info-open
  (merge info-text
    {:display "block"}))

(def info-closed
  (merge info-text
    {:display "none"}))

