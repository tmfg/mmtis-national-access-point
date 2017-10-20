(ns ote.ui.napit
  (:require [cljs-react-material-ui.reagent :as ui]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]))


(defn- button-container [button]
  [:div (stylefy/use-style style-base/action-button-container)
   button])

(defn tallenna [opts label]
  [button-container
   [ui/raised-button
    (merge {:button-style style-base/action-button :primary true} opts)
    label]])

(defn cancel [opts label]
  [button-container
   [ui/raised-button
    (merge {:button-style style-base/action-button :secondary true} opts)
    label]])
