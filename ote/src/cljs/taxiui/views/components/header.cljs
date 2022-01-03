(ns taxiui.views.components.header
  (:require [stylefy.core :as stylefy]
            [taxiui.styles.header :as styles]))

(defn header [app]
  [:header (stylefy/use-style styles/header)
   [:img (merge (stylefy/use-style styles/nap-logo)
                {:src "img/icons/nap-logo.svg"})]
   "Sinä olet "(get-in app [:user :name])])
