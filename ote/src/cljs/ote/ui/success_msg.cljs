(ns ote.ui.success_msg
  (:require [ote.localization :refer [tr tr-key]]
            [cljs-react-material-ui.icons :as ic]
            [ote.style.base :as style-base]))

(defn success-msg [content]
  [:div
   {:style (merge style-base/msg-container style-base/msg-success)}
   [ic/action-check-circle {:style style-base/msg-success}]
   (when content [:div {:style style-base/msg-item-column-margin} content])])

