(ns ote.ui.notification
  (:require [ote.style.notification :as notification-style]
            [stylefy.core :as stylefy]))


(defn notification
  [{:keys [type text]}]
  [:div (stylefy/use-style (notification-style/success-notification type))
   [:div text]])
