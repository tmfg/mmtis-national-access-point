(ns ote.app.utils
  (:import [goog.async Debouncer]))

(defn debounce
  "Calls function after interval, and replaces previous call if called during interval."
  [f interval]
  (let [dbnc (Debouncer. f interval)]
    (fn [& args] (.apply (.-fire dbnc) dbnc (to-array args)))))
