(ns ote.ui.common
  "Common small UI utilities"
  (:require [cljs-react-material-ui.icons :as ic]
            [cljs-react-material-ui.reagent :as ui]
            [ote.localization :refer [tr tr-key]]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]
            [ote.style.form :as style-form]
            [reagent.core :as r]
            [clojure.string :as str]
            [reagent.core :as reagent]))

(def mobile?
  (let [ua (str/lower-case js/window.navigator.userAgent)]
    (boolean (some (partial str/includes? ua) ["android" "iphone" "ipad" "mobile"]))))

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


(defn tooltip-wrapper
  "Wrap any ui component with balloon.css tooltip bindings."
  [component & [wrapper-opts]]
  (fn [data {:keys [text pos len] :as opts}]
    [:span (merge {:data-balloon        text
                   :data-balloon-pos    (or pos "up")
                   :data-balloon-length (or len "medium")}
                  wrapper-opts)
     (component data)]))

(defn dialog
  "Creates a dialog with a link trigger. The body can be in hiccup format."
  [link-label title body]
  (reagent/with-let
    [open? (reagent/atom false)]
    [:div
     [:a {:href "#" :on-click #(do (.preventDefault %)
                                   (reset! open? true))} link-label]
     [ui/dialog
      {:open @open?
       :auto-scroll-body-content true
       :title (or title "")
       :actions [(reagent/as-element [ui/flat-button {:label (tr [:buttons :close])
                                                      :on-click #(reset! open? false)}])]
       :on-request-close #(reset! open? false)}
      body]]))

(defn help [help]
  [:div.help (stylefy/use-style style-base/help)
   [:div (stylefy/use-style style-form/help-text-element) help]])

(defn extended-help
  "Used currently only in passenger transportation form. Give help-text link-text, link address and one form element
   as a parameter."
  [help-text help-link-text help-link component]
  [:div.help (stylefy/use-style style-base/help)
      [:div.col-md-12 (stylefy/use-style style-form/help-text-element) help-text]
      [:div.col-md-12 {:style {:padding-top "5px" :padding-left "10px"}} (linkify help-link help-link-text {:target "_blank"}) ]
      (if component component [:span " "])
   ])

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
