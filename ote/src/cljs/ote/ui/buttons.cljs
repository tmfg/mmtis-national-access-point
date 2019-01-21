(ns ote.ui.buttons
  (:require [cljs-react-material-ui.reagent :as ui]
            [ote.localization :refer [tr tr-key]]
            [cljs-react-material-ui.core :refer [color]]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]
            [ote.style.buttons :as style-buttons]
            [ote.ui.common :as common]))


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

(defn delete [opts label]
  [button-container [ui/raised-button
                     (merge opts
                            (if (:disabled opts)
                              {:button-style style-base/disabled-button :disabled true}
                              {:button-style style-base/delete-button}))
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

(defn open-link
  "Create button like linkify link"
  [url label]
  [button-container
   (common/linkify url
                   [ui/flat-button {:label label :primary true}]
                   {:target "_blank"})])

(defn icon-button
  [opts label]
  [:button.unstyled-button (merge
             opts
             (stylefy/use-style style-buttons/svg-button))
   label])
