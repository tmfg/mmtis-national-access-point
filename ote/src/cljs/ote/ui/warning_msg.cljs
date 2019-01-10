(ns ote.ui.warning_msg
  (:require [ote.localization :refer [tr tr-key]]
            [cljs-react-material-ui.icons :as ic]
            [ote.style.base :as style-base]))

(defn warning-msg [content]
  [:div
   {:style (merge style-base/msg-container style-base/msg-warning)}
   [ic/alert-warning {:style style-base/msg-warning}]
   (when content [:div {:style style-base/msg-item-column-margin} content])])

