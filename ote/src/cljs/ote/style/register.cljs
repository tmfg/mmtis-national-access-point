(ns ote.style.register
  (:require
    [stylefy.core :as stylefy]))


(stylefy/keyframes "fade-out"
  [:from
   {:opacity 1}]
  ["99%"
   {:opacity 0}]
  [:to
   {:opacity 0
    :max-height 0
    :visibility "hidden"}])

(stylefy/keyframes "fade-in"
  [:from
   {:opacity 0
    :max-height "200px"}]                                   ;;Doesn't matter what this number is as long as it's bigger than the height of the element
  [:to
   {:opacity 1
    :max-height "200px"}])


(def success-fade-in {:opacity 0
                      :max-height "0px"
                      :overflow "hidden"
                      :animation-name "fade-in"
                      :animation-duration "0.3s"
                      :animation-fill-mode "forwards"
                      :animation-delay "0.3s"
                      ::stylefy/vendors ["webkit" "moz" "o"]
                      ::stylefy/auto-prefix #{:animation-delay :animation-fill-mode :animation-name :animation-duration}})

(def form-fadeout {:animation-name "fade-out"
                   :animation-duration "0.3s"
                   :max-height "1000px"                     ;;Doesn't matter what this number is as long as it's bigger than the height of the form
                   :overflow "hidden"
                   :animation-fill-mode "forwards"
                   ::stylefy/auto-prefix #{:animation-delay :animation-fill-mode :animation-name :animation-duration}})
