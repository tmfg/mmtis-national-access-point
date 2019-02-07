(ns ote.style.buttons
  (:require
    [stylefy.core :as stylefy]
    [ote.theme.colors :as colors]
    [ote.style.front-page :refer [width-xxs width-xs width-sm width-md width-l width-xl]]))

(def action-button-icon
  {:padding "0"
   :margin "0 1em 0 0"
   :color colors/white-basic})

(def outline-btn-hover-focus
  {:text-decoration "none"
   :border-width "2px"
   :box-shadow "1px 1px 2px 0 rgba(0, 0, 0, .2)"
   :transform "scale(0.98)"})

(def primary-btn-hover-focus
  {:text-decoration "none"
   :box-shadow "1px 1px 2px 0 rgba(0, 0, 0, .2)"
   :background-color colors/primary
   :transform "scale(0.98)"})

(def negative-btn-hover-focus
  {:text-decoration "none"
   :box-shadow "0 4px 4px rgba(0,0,0,0.2)"
   :background-color colors/negative-button-hover
   :transform "scale(0.98)"})

(def button-common
  {:padding "1.3rem"
   :min-width "4rem"
   :display "inline-flex"
   :white-space "nowrap"
   :justify-content "center"
   :text-align "center"
   :transition "all 200ms ease"
   :text-decoration "none"
   :box-shadow "4px 4px 8px 0 rgba(0, 0, 0, .2)"
   ::stylefy/vendors ["webkit" "moz" "o"]
   ::stylefy/media {{:max-width (str width-sm "px")} {:padding "0.7rem"
                                                      :min-width "2rem"}}
   ::stylefy/auto-prefix #{:transition}})

(def disabled-button (merge
                       button-common
                       {:color colors/primary-text
                        :background-color colors/primary-disabled
                        :pointer-events "none"
                        :box-shadow "none"}))

(def negative-button
  (merge button-common
         {:color colors/negative-text
          :border 0
          :background-color colors/negative-button
          ::stylefy/mode {:hover negative-btn-hover-focus
                          :focus negative-btn-hover-focus}}))

(def outline-button
  (merge button-common
         {:background-color "white"
          :color colors/primary
          :border "1px solid"
          :border-color colors/primary
          ::stylefy/mode {:hover outline-btn-hover-focus
                          :focus outline-btn-hover-focus}}))

(def primary-button
  (merge button-common
         {:color colors/primary-text
          :border 0
          :background-color colors/primary-light
          ::stylefy/mode {:hover primary-btn-hover-focus
                          :focus primary-btn-hover-focus}}))

(def svg-button
  {:background-color "transparent"
   :border "none"
   :cursor "pointer"
   ::stylefy/manual [[:&:hover [:svg {:color (str colors/primary " !important")}]]]})
