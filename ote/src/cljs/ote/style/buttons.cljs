(ns ote.style.buttons
  (:require
    [stylefy.core :as stylefy]
    [ote.theme.colors :as colors]))

(def outline-btn-hover-focus {:text-decoration "none"
                  :border-width "2px"
                  :box-shadow "1px 1px 2px 0 rgba(0, 0, 0, .2)"
                  :padding "19px"})

(def outline-button
  {:background-color "white"
   :color colors/primary
   :border "1px solid"
   :border-color colors/primary
   :padding "20px"
   :height "60px"                                           ;;Hard coded so the height doesn't change with transitions
   :display "inline-block"
   :transition "all 200ms ease"
   :text-decoration "none"
   :box-shadow "4px 4px 8px 0 rgba(0, 0, 0, .2)"
   ::stylefy/mode {:hover outline-btn-hover-focus
                   :focus outline-btn-hover-focus}
   ::stylefy/vendors ["webkit" "moz" "ms"]
   ::stylefy/auto-prefix #{:transition}})