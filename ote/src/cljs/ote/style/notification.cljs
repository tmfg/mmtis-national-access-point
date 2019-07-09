(ns ote.style.notification
  (:require [ote.theme.colors :as colors]))

(defn success-notification
  [type]
  (cond
    (= type :success)
    {:border (str "1px solid " colors/success "7d")
     :background-color (str colors/success "1f")
     :padding "1rem"
     :border-radius "4px"}
    (= type :warning)
    {:border (str "1px solid " colors/yellow-basic "7d")
     :background-color (str colors/yellow-basic "1f")
     :padding "1rem"
     :border-radius "4px"}
    (= type :error)
    {:border (str "1px solid " colors/red-darker "7d")
     :background-color (str colors/red-darker "1f")
     :padding "1rem"
     :border-radius "4px"}))
