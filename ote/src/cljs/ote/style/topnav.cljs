(ns ote.style.topnav
  (:require [stylefy.core :as stylefy]))

(def topnav {:min-height "56px"
             :overflow "hidden"
             :background-color "#2D75B4"})

(def topnav-desktop (merge topnav {:height "56px"
                                   :line-height "56px"}))

(def clear {:clear "both"})

(def ul {:list-style-type "none" })

(def li {:display "inline-block"
         :padding-top "10px"})

(def mobile-li {:padding-top "10px" :padding-bottom "10px"})

(def link {:bottom "0"
           :color "#ffffff"
           :text-align "center"
           :padding "10px 15px 10px 15px"
           :text-decoration "none"
           :font-size "14px"
           :font-weight "700"
           ::stylefy/mode {:hover {:background "rgba(0, 0, 0, 0.2)"}}})

(def img {:padding-top "10px"
          :margin-left "-8px"})

(def link-left (merge link
                      {:float "left"}))
(def desktop-link
  (merge link-left
         {:padding "0px 15px 0px 15px"}))

(def active-style {:background "rgba(0, 0, 0, 0.3)"})

(def active (merge link active-style))

(def desktop-active (merge desktop-link active-style))

(def right {:float "right"})
