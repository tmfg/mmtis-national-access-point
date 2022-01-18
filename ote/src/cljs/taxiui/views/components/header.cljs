(ns taxiui.views.components.header
  (:require [stylefy.core :as stylefy]
            [taxiui.styles.header :as styles]))

(defn header [_]
  [:header (stylefy/use-style styles/header)
   [:img (merge (stylefy/use-style styles/nap-logo)
                {:src "img/icons/nap-simple.svg"})]])
