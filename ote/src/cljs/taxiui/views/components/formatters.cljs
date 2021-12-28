(ns taxiui.views.components.formatters
  "Various single value decorator formatters, such as pretty formatting for currencies")

(defn currency
  "Formats given value as euros as per Finnish locale. Tries to normalize decimal delimiter."
  [value]
  (-> (js/Intl.NumberFormat. "fi-FI", #js {:style "currency" :currency "EUR"})
      (.format (if (string? value)
                 (clojure.string/replace value "," ".")
                 value))))
