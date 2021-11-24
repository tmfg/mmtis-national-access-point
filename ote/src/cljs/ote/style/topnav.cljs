(ns ote.style.topnav
  (:require [stylefy.core :as stylefy]
            [ote.theme.screen-sizes :refer [width-xxs width-xs width-sm width-md width-l width-xl]]
            [ote.style.base :as base]))

(def clear {:clear "both"})

(def bottom-border {:box-sizing "border-box"
                    :height "80px"
                    ::stylefy/vendors ["webkit" "moz" "ms"]
                    ::stylefy/auto-prefix #{:box-sizing}})

(def link {:display "block"
           :transition "padding-top 300ms ease, height 300ms ease"
           :bottom "0"
           :color "#ffffff"
           :text-align "center"
           :padding "10px 15px 10px 15px"
           :text-decoration "none"
           :font-size "0.875em"
           ::stylefy/mode {:hover {:padding-bottom "0px"
                                   :border-bottom "4px solid #fafafa"}}
           ::stylefy/vendors ["webkit" "moz" "ms"]
           ::stylefy/auto-prefix #{:transition}})

(def header-topbar {:display "flex"
                    :padding-left "2rem"
                    :padding-right "2rem"
                    :padding-top ".5rem"
                    :padding-bottom ".5rem"
                    :background-color "#000"
                    :min-height "44px"
                    :position "relative"
                    })

(def header-bottombar {:padding-left "2rem"
                       :padding-right "2rem"
                       :display "flex"
                       :border-bottom "1px solid #f2f2f2"
                       :box-shadow "0 8px 6px -6px #f2f2f2"
                       :background-color "#fff"
                       :flex-direction "row"
                       :justify-content "flex-start"
                       :min-height "54px"})

(def nap-menu {:height "100%"
               :width "100%"})

(def bottombar-entry-icon {:color "#000"
                           :width "16px"
                           :height "16px"
                           :align-self "center"})

(def bottombar-entry-button {:display "flex"
                       :justify-content "center"
                       :background-color "transparent"
                       :border "0"
                       :cursor "pointer"
                       :padding 0})

(def nap-languages-switcher-active {:margin-right ".5rem"})

(def nap-languages-switcher-menu {::stylefy/mode {:before {:content "\"\""  ; this is the magic triangle again...
                                                           :display "block"
                                                           :position "absolute"
                                                           :background-color "#fff"
                                                           :width ".5rem"
                                                           :height ".5rem"
                                                           :top "-5px"
                                                           :right ".5rem"
                                                           :transform "rotate(45deg)"
                                                           :border-left "1px solid #ddd"
                                                           :border-top "1px solid #ddd"}}
                                  ;:display "none"
                                  :background-color "#fff"
                                  :border "1px solid #ddd"
                                  :border-radius "3px"
                                  :position "absolute"
                                  :top "93px"  ; XXX: This was originally 1rem, but due to layout differences was positioned manually
                                  :z-index "1"})

(def nap-languages-switcher-item {:border-bottom "1px solid #ddd"
                                  :display "block"
                                  :margin-right "0"
                                  :white-space "nowrap"})

(def nap-languages-switcher-link {:display "block"
                                  :padding ".5rem 1rem"
                                  :transition "color .15s ease-out"
                                  :color "#000"
                                  :text-decoration "none"})

(def fintraffic-logo-link {:align-self "center"
                           :display "inline-flex"
                           :margin-right "1.5rem"})

(def fintraffic-logo {:height "18px"
                      :width "106px"})

(def fintraffic-quick-links-menu {:list-style "none"
                                  :margin "0"
                                  :padding "0"
                                  :align-self "center"})

(def fintraffic-quick-links-item {:display "inline-block"
                                  :margin-right "1.5rem"})

(def fintraffic-quick-links-link {:color "#FFFFFF"
                                  :font-weight "600"
                                  :text-decoration "none"})

(def fintraffic-quick-links-active {:position "relative"})

(def fintraffic-quick-links-uparrow {::stylefy/mode {:before {:content "\"\""  ; this is the magic triangle
                                                              :position "absolute"
                                                              :left "50%" ; TODO: garden units?
                                                              :border-width "5px"
                                                              :border-style "solid"
                                                              :border-color "transparent transparent white transparent"
                                                              :transform "translateX(-50%)"
                                                              }}})

(def style {:content ""})

(def logo {:transition "margin-top 300ms ease, height 300ms ease"
           :margin-top "13px"
           :margin-left "-8px"
           :display "block"
           :height "35px"
           :flex "0 auto"
           ::stylefy/vendors ["webkit" "moz" "ms"]
           ::stylefy/auto-prefix #{:flex :transition}})

(def link-left (merge link
                      {:float "left"}))

(def white-hover {:padding-bottom "0px"
                  :border-bottom "4px solid #fafafa"})

(def active-style {:background "rgba(0, 0, 0, 0.3)"})

(def active (merge link active-style))

(def right {:float "right"})

(def gray-info-text {:color "lightgray"
                     :font-size "0.875rem"
                     :padding-bottom "10px"})

(def tos-container {:z-index 1001
                    ;:position "fixed"
                    :height "50px"
                    :width "100%"
                    :background-color "#0034ac"
                    :padding "12px"
                    :color "#FFFFFF"
                    :font-size "0.875rem"
                    :line-height "0.875rem"})

(def tos-texts {:padding-left "0.5rem"
                ::stylefy/media {{:max-width (str width-xl "px")} {:padding-top "6px"}
                                 {:max-width (str width-l "px")}  {:padding-top "6px"}
                                 {:max-width (str width-sm "px")} {:padding-top "4px"}
                                 {:max-width (str width-xs "px")} {:padding-top "1px"}}})

(def tos-toplink (merge
                   base/base-link
                   {:color "#FFFFFF"
                    :text-decoration "underline"
                    :padding-left "0.25rem"
                    :padding-right "0.25rem"
                    ::stylefy/mode {:hover {:color "#FFFFFF"}}}))