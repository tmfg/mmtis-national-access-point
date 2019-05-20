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

(defn primary-loading
  [options]
  [:div (stylefy/use-style
          (merge spinner-base
                 (when (:style options)
                   (:style options))))
   [:div (stylefy/use-style component-base)]
   [:div (stylefy/use-style component-2)]])
