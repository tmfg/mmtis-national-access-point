(ns taxiui.views.components.forms
  (:require [taxiui.theme :as theme]
            [stylefy.core :as stylefy]
            [ote.theme.colors :as colors]
            [clojure.string :as str]))

(defn deep-merge [& maps]
  (apply merge-with (fn [& args]
                      (if (every? map? args)
                        (apply deep-merge args)
                        (last args)))
         maps))

(def ^:private scaling-and-borders {:border-width  "0.0625em"
                                    :border-style  "solid"
                                    :border-color  colors/basic-gray
                                    :border-radius "0.3em"
                                    :height        "3rem"
                                    :font-size     "1.5em"
                                    :width         "100%"
                                    :box-sizing    "border-box"
                                    ::stylefy/mode {:focus {:outline-width "0"}}})

(def ^:private input-element (-> (deep-merge scaling-and-borders
                                             {::stylefy/mode {"::placeholder" {:color   colors/accessible-black
                                                                               :opacity 1}}})
                                 (theme/breather-padding)))

(def ^:private button-element (-> (merge scaling-and-borders
                                         {:border-color colors/basic-black
                                          :background-color colors/basic-white})
                                  (theme/breather-padding)))

(defn- form-element
  "Creates an accessible form element container with fancy label and optional content."
  [el id label styles props inner-content post-content]
  [:div
   [:h5
    (cond
      (nil? label) [:br]  ; this simulates empty header line, which aligns form elements when placed together
      (vector? label) [:label {:for id} (first label) [:br] (second label)]
      (string? label) [:label {:for id} label])]
   (let [extra-styles (:styles props)
         all-styles   (deep-merge styles extra-styles)
         props        (merge (stylefy/use-style all-styles) (dissoc props :styles) {:id id})]
     (if (some? inner-content)
       [el props inner-content]
       [el props]))
   post-content])

(defn input
  ([id label] (input id label nil nil))
  ([id label props post-content]
   (form-element :input id label input-element props nil post-content)))

(defn button
  ([id label] (button id label nil))
  ([id label props]
   (form-element :button id nil button-element props label nil)))

(defn simple-input
  "Simple input-box only input, defaults to text, is styled similarly to all other inputs on the site."
  [id props]
  (let [extra-styles (:styles props)
        all-styles   (deep-merge input-element extra-styles)
        props        (merge (stylefy/use-style all-styles) (dissoc props :styles) {:id id})]
    [:input props]))