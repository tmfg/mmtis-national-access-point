(ns ote.ui.buttons
  (:require [cljs-react-material-ui.reagent :as ui]
            [ote.localization :refer [tr tr-key]]
            [cljs-react-material-ui.core :refer [color]]
            [cljs-react-material-ui.icons :as ic]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]
            [ote.style.buttons :as style-buttons]
            [ote.ui.common :as common]
            [ote.theme.colors :as colors]))

(defn- button-container [button]
  [:div (stylefy/use-style style-base/action-button-container)
   button])

(defn save [opts label]
  [button-container
   [:button (merge opts
                   (if (:disabled opts)
                     (stylefy/use-style style-buttons/disabled-button)
                     (stylefy/use-style style-buttons/primary-button)))
    label]])

(defn cancel [opts label]
  [button-container
   [:button (merge opts
                   (if (:disabled opts)
                     (stylefy/use-style style-buttons/disabled-button)
                     (stylefy/use-style style-buttons/outline-button)))
    label]])

(defn delete [opts label]
  [button-container
   [:button (merge opts
                   (if (:disabled opts)
                     (stylefy/use-style style-buttons/disabled-button)
                     (stylefy/use-style style-buttons/negative-button)))
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
             (stylefy/use-style style-buttons/svg-button)
             opts)
   label])

(defn big-icon-button-with-label
  "Give icon-element in format: (ic/action-description {:style {:width 30
                                                        :height 30
                                                        :margin-right \"0.5rem\"
                                                        :color colors/primary}})
  And label-text as text.
  element-tag is :button or :a depending on do you want to move the user to different page or do magic right where she/he is."
  [options icon-element label-text]
  [:button (merge options
                      {:style {:align-items "center"}}
                      (stylefy/use-style style-base/blue-link-with-icon)
                  (stylefy/use-style style-buttons/outline-button))
   icon-element
   [:span label-text]])
