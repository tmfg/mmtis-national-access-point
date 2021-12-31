(ns taxiui.views.components.forms
  (:require [taxiui.theme :as theme]
            [stylefy.core :as stylefy]
            [ote.theme.colors :as colors]))


(def ^:private pricing-input-element (-> {:border        (str "0.0625em solid " colors/light-gray)
                                          :border-radius "0.3em"
                                          :height        "3rem"
                                          :font-size     "2em"
                                          :width         "100%"
                                          :box-sizing    "border-box"}
                                         (theme/breather-padding)))

(defn input
  [props]
  [:input (merge (stylefy/use-style pricing-input-element) props)])