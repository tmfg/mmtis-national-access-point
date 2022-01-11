(ns taxiui.app.controller.loader
  (:require [taxiui.app.routes :as routes]
            [tuck.core :as tuck]))

(defn- bag-counter
  "Function for counting hits in a bag (associative data structure counting \"hits\")."
  [m op k]
  (let [r (op (or (get m k) 0))]
    (if (pos? r)
      (assoc m k r)
      (dissoc m k))))

(tuck/define-event AddHit [element]
  {}
  (update-in
    app
    [:taxi-ui :uix :loader]
    (fn [v]
     (bag-counter v inc element))))

(tuck/define-event RemoveHit [element]
  {}
  (update-in
    app
    [:taxi-ui :uix :loader]
    (fn [v]
      (bag-counter v dec element))))