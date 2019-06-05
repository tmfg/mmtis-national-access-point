(ns ote.ui.spinner
  (:require [stylefy.core :as stylefy]
            [ote.theme.colors :as colors]))

(stylefy/keyframes "spinner-bounce"
                   [:0%, :100%
                    {:transform "scale(0.0)"}]
                   [:50%
                    {:transform "scale(1.0)"}])

(def spinner-base
  {:width "40px"
   :height "40px"
   :position "relative"})

(def component-base
  {:width "100%"
   :height "100%"
   :border-radius "50%"
   :background-color colors/spinner
   :opacity "0.6"
   :position "absolute"
   :top 0
   :left 0
   :animation "spinner-bounce 2.0s infinite ease-in-out"})

(def component-2
  (merge component-base
         {:animation-delay "-1s"}))

(defn spinner2
  [options]
  [:div (stylefy/use-style
          (merge spinner-base
                 (when (:style options)
                   (:style options))))
   [:div (stylefy/use-style component-base)]
   [:div (stylefy/use-style component-2)]])

(stylefy/keyframes
  "circle-fade"
  [:0%, :39%, :100%
   {:opacity 0}]
  [:40%
   {:opacity 1}])

(def fading-circle
  {:width "40px"
   :height "40px"
   :position "relative"})

(def circle
  {:width "100%"
   :height "100%"
   :position "absolute"
   :left 0
   :right 0})

(def circle-before
  {:content "''"
   :display "block"
   :margin "0 auto"
   :width "15%"
   :height "15%"
   :background-color colors/cyan
   :border-radius "100%"
   :animation "circle-fade 1.2s infinite ease-in-out both"})


(defn primary-spinner
  [options]
  [:div (stylefy/use-style
          (merge
            (when (:style options) (:style options))
            (when (:id options) {:id (:id options)})
            fading-circle))
   (for [x (map #(+ 1 %) (range 12))
         :let [rotation (* x 30)
               delay (/ (- x 13) 10.0)]]
     [:div (stylefy/use-style
             (merge circle
                    {:transform (str "rotate(" rotation "deg)")
                     ::stylefy/mode {:before (merge circle-before
                                                    {:animation-delay (str delay "s")})}}))])])
