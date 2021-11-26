(ns ote.style.buttons
  (:require
    [stylefy.core :as stylefy]
    [ote.theme.colors :as colors]
    [ote.theme.screen-sizes :refer [width-xxs width-xs width-sm width-md width-l width-xl]]))

(def action-button-icon
  {:padding "0"
   :margin "0 1em 0 0"
   :color colors/white-basic})

(def outline-btn-hover-focus
  {:text-decoration "none"
   :outline (str "1px solid " colors/primary-button-background-color)
   :box-shadow "1px 1px 2px 0 rgba(0, 0, 0, .2)"
   :transform "scale(0.98)"})

(def outline-btn-active
  {:box-shadow "0 0 0 0 rgba(0, 0, 0, .2)"
   :transform "scale(0.97)"})

(def primary-btn-hover-focus
  {:text-decoration "none"
   :box-shadow "1px 1px 2px 0 rgba(0, 0, 0, .2)"
   :background-color colors/primary-dark
   :transform "scale(0.98)"})

(def primary-btn-active
  {:background-color colors/primary-darker
   :transform "scale(0.97)"})

(def negative-btn-hover-focus
  {:text-decoration "none"
   :box-shadow "0 4px 4px rgba(0,0,0,0.2)"
   :background-color colors/negative-button-hover
   :transform "scale(0.98)"})

(def button-common
  {:padding "1.3rem"
   :line-height "1.7rem"
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

(def row-button (merge
                  button-common
                  {:padding "0.65rem 1.3rem 0.65rem 1.3rem"
                   :display "inline-flex"}))

(def disabled-button (merge
                       button-common
                       {:color colors/primary-text
                        :background-color colors/primary-disabled
                        :pointer-events "none"
                        :box-shadow "none"
                        :cursor "not-allowed"}))

(def delete-row-button
  (merge row-button
         {:color colors/negative-text
          :border 0
          :background-color colors/gray700
          ::stylefy/mode {:hover negative-btn-hover-focus
                          :focus negative-btn-hover-focus}}))

(def save-row-button
  (merge row-button
         {:color colors/negative-text
          :border 0
          :background-color colors/primary-button-background-color
          ::stylefy/mode {:hover primary-btn-hover-focus
                          :focus primary-btn-hover-focus}}))

(def open-dialog-row-button
  (merge row-button
         {:color colors/negative-text
          :border 0
          :background-color colors/primary-button-background-color
          ::stylefy/mode {:hover negative-btn-hover-focus
                          :focus negative-btn-hover-focus}}))

(def negative-button
  (merge button-common
         {:color colors/negative-text
          :border 0
          :background-color colors/negative-button
          ::stylefy/mode {:hover negative-btn-hover-focus
                          :focus negative-btn-hover-focus}}))

(def delete-set-button
  (merge button-common
         {:color colors/negative-text
          :border 0
          :background-color colors/gray700
          ::stylefy/mode {:hover negative-btn-hover-focus
                          :focus negative-btn-hover-focus}}))

(def outline-button
  (merge button-common
         {:background-color "white"
          :color colors/primary-button-background-color
          :border "1px solid"
          :cursor "pointer"
          :border-color colors/primary-button-background-color
          ::stylefy/mode {:hover outline-btn-hover-focus
                          :focus outline-btn-hover-focus
                          :active outline-btn-active}}))

(def primary-button
  (merge button-common
         {:color colors/primary-text
          :cursor "pointer"
          :border-color colors/primary-button-background-color
          :background-color colors/primary-button-background-color
          ::stylefy/mode {:hover primary-btn-hover-focus
                          :focus primary-btn-hover-focus
                          :active primary-btn-active}}))

(def svg-button
  {:background-color "transparent"
   :border "none"
   :cursor "pointer"
   ::stylefy/manual [[:&:hover [:svg {:color (str colors/primary-button-background-color " !important")}]]]})
