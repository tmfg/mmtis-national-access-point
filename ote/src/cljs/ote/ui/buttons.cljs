(ns ote.ui.buttons
  (:require [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.core :refer [color]]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]))


(defn- button-container [button]
  [:div (stylefy/use-style style-base/action-button-container)
   button])

(defn save [opts label]
  [button-container [ui/raised-button
                     (merge opts
                            (if (:disabled opts)
                              {:button-style style-base/disabled-button :disabled true}
                              {:button-style style-base/base-button}))
                     label]])

(defn cancel [opts label]
  [button-container
   [ui/flat-button
    (merge {:style {:padding-left "1.1em"
                    :padding-right "1.1em"
                    :text-transform "uppercase"
                    :color (color :blue700)
                    :font-size "12px"
                    :font-weight "bold"}} opts)
    label]])
