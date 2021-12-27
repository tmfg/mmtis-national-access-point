(ns taxiui.views.components.header
  (:require [stylefy.core :as stylefy]
            [taxiui.styles.header :as styles]))

(defn header [app]
  [:header (stylefy/use-style styles/header) "Olen header. Sinä olet "(get-in app [:user :name])])
