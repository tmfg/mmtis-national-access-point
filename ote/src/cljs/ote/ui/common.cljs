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
   (let [a-props (if (= (:target a-props) "_blank")
                   ;; https://mathiasbynens.github.io/rel-noopener/ Avoid a browser vulnerability by using noopener noreferrer.
                   (assoc a-props :rel "noopener noreferrer")
                   a-props)]

     (if-not url
       [:span]
       ;; Lazy check if url has a protocol, or if it is a relative path
       (let [url (if (re-matches #"^(\w+:|.?.?/).*" url)
                   url
                   (str "http://" url))]
         [:a (merge {:href url} a-props) label])))))

(defn help [help]
  [:div.help (stylefy/use-style style-base/help)
   [:div (stylefy/use-style style-form/help-text-element) help]])

(defn table2 [& items]
  [:table
   [:tbody
    (map-indexed
     (fn [i [left right]]
       ^{:key i}
       [:tr
        [:td left]
        [:td right]])
     (partition 2 items))]])

(defn scroll-sensor [on-scroll]
  (let [sensor-node (atom nil)
        check-scroll (fn [event]
                       (let [viewport-y-min 0
                             viewport-y-max (.-innerHeight js/window)
                             element-y (.-top (.getBoundingClientRect @sensor-node))]

                         (when (<= viewport-y-min element-y viewport-y-max)
                           (on-scroll))))]

    (r/create-class
     {:component-did-mount
      (fn [this]
        (reset! sensor-node (aget this "refs" "sensor"))
        (.addEventListener js/window "scroll" check-scroll))
      :component-will-unmount
      (fn [this]
        (.removeEventListener js/window "scroll" check-scroll))
      :reagent-render
      (fn [_]
        [:span {:ref "sensor"}])})))
