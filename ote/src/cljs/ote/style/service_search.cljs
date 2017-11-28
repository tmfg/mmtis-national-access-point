(ns ote.style.service-search
  (:require [stylefy.core :as stylefy]
            [ote.style.base :as base]))

(def divider-color "lightGray")
(def subtitle-color "#A4A4A4")

(def result-card {:margin-top "1em" :padding "1em" :padding-bottom "1.5em"})

(def right-divider {:padding-right "0.5em"
                    :margin-right "0.5em"
                    :border-right (str "solid 2px " divider-color)})

(def bottom-divider {:padding-bottom "1em"
                     :margin-bottom "1em"
                     :border-bottom (str "solid 2px " divider-color)})

(def result-link
  (merge
   {:font-size "21px"
    :font-weight 400
    :color "#2D75B4"
    ::stylefy/mode {:hover {:text-decoration "underline"}}
    }
   right-divider))

(def subtitle (merge
               {:color subtitle-color
                :font-size "90%"}
               bottom-divider))

(def subtitle-operator  {:display "inline-block" :color "#666"})
(def subtitle-operator-first (merge {:display "inline-block" :color "#666"}
                              right-divider))

(def result-header {:width "100%" :display "block"
                    :margin-bottom "0.5em"})

(def data-items
  (merge base/item-list-container
         {:display "inline-flex"
          :position "relative"
          :top "7px"
          :font-size "14px"
          :color "#666"}))

(def external-interface-header
  {:color subtitle-color
   :font-size "80%"
   :text-align "left"})

(def external-interface-body
  {:font-weight "normal"})

(def icon-div { :display "inline-block"

               })
(def item-div { :display "inline-block"

               })