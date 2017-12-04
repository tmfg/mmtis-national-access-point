(ns ote.ui.common
  "Common small UI utilities"
  (:require [cljs-react-material-ui.icons :as ic]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]
            [ote.style.form :as style-form]
            [reagent.core :as r]))

(defn linkify [url label]
  (if-not url
    [:span]
    (let [url (if (re-matches #"^\w+:.*" url)
                url
                (str "http://" url))]
      [:a {:href url :target "_blank"} label])))

(defn help [help]
  [:div.help (stylefy/use-style style-base/help)
   [:div (stylefy/use-style style-form/help-icon-element) [ic/action-info-outline]]
   [:div (stylefy/use-style style-form/help-text-element) help]])

(defn scroll-into-view
  "Element that scrolls itself into view after being shown. Why? Because IE."
  []
  (r/create-class
   {:component-did-mount
    (fn [this]
      (let [el (aget this "refs" "scroll-me")]
        (.scrollIntoView el)))
    :reagent-render
    (fn []
      [:div {:style {:display "inline-block"}
             :ref "scroll-me"}])}))
