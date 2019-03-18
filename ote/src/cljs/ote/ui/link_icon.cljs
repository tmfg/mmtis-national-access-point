(ns ote.ui.link-icon
  (:require [stylefy.core :as stylefy]
            [ote.style.base :as style-base]))


(defn link-with-icon
  [icon url link-text on-click]
  [:div
   [:a (merge {:href url
               :style {:margin-right "2rem"}
               :id "edit-transport-operator-btn"}
              (when (not-empty on-click)
                :on-click on-click)
              (stylefy/use-style style-base/blue-link-with-icon))
    icon
    link-text]])