(ns taxiui.views.components.formatters
  "Various single value decorator formatters, such as pretty formatting for currencies")

(defn currency
  [value]
  (-> (js/Intl.NumberFormat. "fi-FI", #js {:style "currency" :currency "EUR"})
      (.format value)))
