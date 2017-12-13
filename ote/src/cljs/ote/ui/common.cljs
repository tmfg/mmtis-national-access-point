(ns ote.ui.common
  "Common small UI utilities"
  (:require [cljs-react-material-ui.icons :as ic]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]
            [ote.style.form :as style-form]
            [reagent.core :as r]))

(defn linkify
  ([url label]
   (linkify url label nil))
  ([url label a-props]
   (let [a-props (when (= (:target a-props) "_blank")
                ;; https://mathiasbynens.github.io/rel-noopener/ Avoid a browser vulnerability by using noopener noreferrer.
                (assoc a-props :rel "noopener noreferrer"))]

     (if-not url
       [:span]
       ;; Lazy check if url has a protocol, or if it is a relative path
       (let [url (if (re-matches #"^(\w+:|.?.?/).*" url)
                   url
                   (str "http://" url))]
         [:a (merge {:href url} a-props) label])))))

(defn help [help]
  [:div.help (stylefy/use-style style-base/help)
   [:div (stylefy/use-style style-form/help-icon-element) [ic/action-info-outline]]
   [:div (stylefy/use-style style-form/help-text-element) help]])
