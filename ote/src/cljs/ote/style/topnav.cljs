(ns ote.style.topnav
  (:require [stylefy.core :as stylefy]))

(def topnav {:overflow "hidden"
             :background-color "#2D75B4"})

(def clear {:clear "both"})

(def ul {:list-style-type "none"
         :margin "0"
         :padding "0"})

(def li {:display "inline-block"
         :padding-top "10px"})

(def link {:bottom "0"
           :color "#ffffff"
           :text-align "center"
           :padding "10px 15px 0px 15px"
           :text-decoration "none"
           :font-size "14px"
           :font-weight "700"
           :line-height "38px"
           ::stylefy/mode {:hover {:border-bottom "4px solid #1976D2"}}})

(def link-left (merge link
                      {:float "left"}))
(def desktop-link
  (merge link-left
         {:padding "0px 15px 0px 15px"}))

(def active-style {:background "rgba(0, 0, 0, 0.3)"})

(def active (merge link active-style))

(def desktop-active (merge desktop-link active-style))

(def right {:float "right"})
