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

(defn tooltip
  "Render child-component with tooltip"
  [{:keys [text pos len]} child-component]
  [:span {:data-balloon text
          :data-balloon-pos (or pos "up")
          :data-balloon-length (or len "medium")}
   child-component])

(defn dialog
  "Creates a dialog with a link trigger. The body can be in hiccup format."
  [link-label title body]
  (reagent/with-let
    [open? (reagent/atom false)]
    [:span
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

;; Full width gray generic help box
(defn generic-help [help]
  [:div.help (stylefy/use-style style-base/generic-help)
   [:div (stylefy/use-style style-form/help-text-element) help]])

(defn help [help]
  [:div.help (stylefy/use-style style-base/help)
   [:div (stylefy/use-style style-form/help-text-element) help]])

(defn- extended-help-link [help-link help-link-text]
  [:div (stylefy/use-style style-base/help-link-container)
   [:div (stylefy/use-style style-base/link-icon-container)
    [ic/action-open-in-new {:style style-base/link-icon}]]
   [:div
    (linkify help-link help-link-text {:target "_blank"})]])

(defn extended-help
  "Used currently only in passenger transportation form. Give help-text link-text, link address and one form element
   as a parameter."
  [help-text help-link-text help-link component]
  [:div.help (stylefy/use-style style-base/generic-help)
   [:div (stylefy/use-style style-form/help-text-element) help-text]
   [:div (extended-help-link help-link help-link-text)]
   component])

(defn shorten-text-to [max-length text]
  (str (subs text 0 max-length) "\u2026"))

(defn shortened-description [desc max-length]
  (if (< max-length (count desc))
    [:span (shorten-text-to max-length desc)]
    [:span desc]))

(defn maybe-shorten-text-to [max-length text]
  (if (< max-length (count text))
    (shorten-text-to max-length text)
    text))

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

(defn copy-to-clipboard [text-to-copy]
  (let [id (name (gensym "ctc"))
        copy! #(let [elt (.getElementById js/document id)]
                 (.select elt)
                 (.execCommand js/document "Copy"))]
    [:div {:style {:display "inline-block"}}
     [:input {:id id
              :readOnly true
              :on-focus copy!
              :value text-to-copy}]
     [ui/flat-button {:icon (ic/content-content-copy)
                      :on-click copy!}]]))

(defn should-component-update?
  "Helper function to create a :should-component-update lifecycle function.
  Uses get-in to fetch the given accessor paths from both the old and the new
  arguments and returns true if any path's values differ.

  For example if the component has arguments: [e! my-thing foo]
  the path to access key :name from my-thing is: [0 :name]."
  [& accessor-paths]
  (fn [_ old-argv new-argv]
    (let [old-argv  (subvec old-argv 1)
          new-argv (subvec new-argv 1)]
      (boolean
       (some #(not= (get-in old-argv %) (get-in new-argv %))
             accessor-paths)))))
