(ns ote.style.topnav
  (:require [stylefy.core :as stylefy]))

(def topnav-wrapper {:position "fixed"
                     :width "100%"
                     :top 0
                     :z-index 999
                     :transition "all 300ms ease"})

(def topnav-dropdown {:width "100%"
                      :background-color "#323232"
                      :min-height "200px"
                      :padding-top "20px"
                      :padding-bottom "20px"
                      :border-top "2px solid #646464"
                      :transition "visibility 300ms ease, opacity 300ms ease"})


(def topnav {:min-height "56px"
             :background-color "#323232"
             :overflow "hidden"
             :box-shadow "0 4px 4px 0 rgba(0, 0, 0, .2)"
             :transition "all 300ms ease"})

(def topnav-desktop (merge topnav {:height "80px"
                                   :line-height "80px"}))

(def clear {:clear "both"})

(def bottom-border {:-moz-box-sizing "border-box"
                    :-webkit-box-sizing "border-box"
                    :box-sizing "border-box"
                    :height "80px"})

(def ul {:list-style-type "none" })

(def li-right {:font-size "0.875rem"
                     :float "right"
                     :padding-right "10px"})

(def li-right-div {:cursor  "pointer"
                   :display "-webkit-box"
                   :padding-right "10px"
                   :margin-left "10px"})

(def li-right-div-blue (merge bottom-border li-right-div
                              {:cursor  "pointer" :display "-webkit-box"
                               :border-bottom "4px solid rgb(102,163,224)"}))

(def li-right-div-white  (merge bottom-border li-right-div
                                {:cursor  "pointer"
                         :display "-webkit-box"
                         ::stylefy/mode {:hover {:border-bottom "4px solid #fafafa"}}}))

(def mobile-li {:padding-top "10px" :padding-bottom "10px"})

(def link {:transition "padding-top 300ms ease, height 300ms ease"
           :bottom "0"
           :color "#ffffff"
           :text-align "center"
           :padding "10px 15px 10px 15px"
           :text-decoration "none"
           :font-size "0.875em"
           :display "block"
           ::stylefy/mode {:hover {:padding-bottom "0px"
                                   :border-bottom "4px solid #fafafa"}}})

(def topnav-dropdown-link
  {:color "#fafafa"
   :padding "10px 0 10px 0"
   :text-decoration "none"
   :font-size "1rem"
   :display "block"
   ::stylefy/mode {:hover {:text-decoration "underline"}}})

(def logo {:transition "margin-top 300ms ease, height 300ms ease"
           :margin-top "13px"
           :margin-left "-8px"
           :display "block"
           :height "35px"
           :flex "0 auto"})

(def logo-small
  (merge logo
         {:transition "margin-top 300ms ease, height 300ms ease"
          :margin-top "16px"
          :height "24px"}))


(def link-left (merge link
                      {:float "left"}))

(def white-hover {:padding-bottom "0px"
                  :border-bottom "4px solid #fafafa"})

(def default-main-header-object (merge bottom-border
                                       {:padding-top "0px"
                                        :padding-right "15px"
                                        :padding-bottom "0px"
                                        :padding-left "15px"}))
(def desktop-link
  (merge link-left default-main-header-object
         {::stylefy/mode {:hover white-hover}}))

(def active-style {:background "rgba(0, 0, 0, 0.3)"})

(def active (merge link active-style))

(def right {:float "right"})
