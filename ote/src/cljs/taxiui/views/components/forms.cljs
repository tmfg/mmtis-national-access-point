(ns taxiui.views.components.forms
  (:require [taxiui.theme :as theme]
            [stylefy.core :as stylefy]
            [ote.theme.colors :as colors]))

(def ^:private scaling-and-borders {:border        (str "0.0625em solid " colors/light-gray)
                                    :border-radius "0.3em"
                                    :height        "3rem"
                                    :font-size     "2em"
                                    :width         "100%"
                                    :box-sizing    "border-box"})

(def ^:private input-element (-> scaling-and-borders
                                 (theme/breather-padding)))

(def ^:private button-element (-> scaling-and-borders
                                  (theme/breather-padding)))

(defn input
  [props]
  [:input (merge (stylefy/use-style input-element) props)])

(defn button
  ([label] (button label nil))
  ([label props]
   [:button (merge (stylefy/use-style button-element) props) label]))