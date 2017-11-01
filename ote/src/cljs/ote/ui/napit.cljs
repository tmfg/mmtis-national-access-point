(ns ote.ui.napit
  (:require [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.core :refer [color]]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]))


(defn- button-container [button]
  [:div (stylefy/use-style style-base/action-button-container)
   button])

(defn tallenna [opts label]
  ;; Render different button if button is disabled (one or more fields are required or other errors in the form)
  (let [button (if (= (get opts :disabled) false)
                 [ui/raised-button
                   (merge {:button-style style-base/base-button} opts)
                   label]
                 [ui/raised-button
                   (merge {:button-style style-base/disabled-button :disabled true} opts)
                   label])]
    ;; Render button
    [button-container button ]))

(defn cancel [opts label]
  [button-container
   [ui/flat-button
    (merge {:button-style style-base/base-button
            :style {:padding-left "1.1em"
                    :padding-right "1.1em"
                    :text-transform "uppercase"
                    :color (color :blue700)
                    :font-size "12px"
                    :font-weight "bold"}} opts)
    label]])
