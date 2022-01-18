(ns taxiui.views.components.formatters
  "Various single value decorator formatters, such as pretty formatting for currencies"
  (:require [ote.theme.colors :as colors]
            [clojure.string :as str]))

(defn currency
  "Formats given value as euros as per Finnish locale. Tries to normalize decimal delimiter."
  [value]
  (-> (js/Intl.NumberFormat. "fi-FI", #js {:style "currency" :currency "EUR"})
      (.format (if (string? value)
                 (clojure.string/replace value "," ".")
                 value))))

(defn street-light
  "Renders a red/yellow/green ball based on value tresholds."
  [low middle high value]
  ; TODO: CSS elsewhere, possibly injectable?
  [:span {:style {:border-radius "50%"
                  :display "inline-block"
                  :width "0.5em"
                  :height "0.5em"
                  :background-color (cond
                                      (< value low)    colors/accessible-blue
                                      (< value middle) colors/basic-green
                                      (< value high)   colors/basic-yellow
                                      :else            colors/basic-red)
                  }}])

(defn joining
  [delimiter renderer]
  (fn [s]
    (str/join delimiter (map renderer s))))