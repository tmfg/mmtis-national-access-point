(ns ote.ui.common
  "Common small UI utilities"
  (:require [cljs-react-material-ui.icons :as ic]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]))

(defn linkify [url label]
  (if-not url
    [:span]
    (let [url (if (re-matches #"^\w+:.*" url)
                url
                (str "http://" url))]
      [:a {:href url :target "_blank"} label])))

(defn help [help]
  [:div.help (stylefy/use-style style-base/help)
   [:div [ic/action-info-outline]]
   [:div help]])
