(ns ote.ui.icon_labeled
  (:require [ote.style.base :as style-base]
            [stylefy.core :as stylefy]))
(defn icon-labeled
  ([icon label]
   [icon-labeled {} icon label])
  ([wrapper-attrs icon label]
   [:div (stylefy/use-style (merge style-base/icon-labeled-container wrapper-attrs))
    [:span (stylefy/use-style style-base/icon-labeled-icon)
     icon]
    [:span
     label]]))
