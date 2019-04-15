(ns ote.ui.link-icon
  (:require [stylefy.core :as stylefy]
            [ote.style.base :as style-base]))


(defn link-with-icon
  [{:keys [id target-blank?]} icon url link-text on-click]
  [:div
   [:a (merge {:href url
               :style {:margin-right "2rem"}}
              (when target-blank?
                {:target "_blank"
                 :rel "noopener noreferrer"})
              (when id
                {:id id})
              (when (not-empty on-click)
                {:on-click on-click})
              (stylefy/use-style style-base/blue-link-with-icon))
    icon
    link-text]])