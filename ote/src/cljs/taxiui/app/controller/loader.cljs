(ns taxiui.app.controller.loader
  (:require [taxiui.app.routes :as routes]
            [tuck.core :as tuck]))

(defn- bag-counter
  "Function for counting hits in a bag (associative data structure counting \"hits\").

  Each bag has a separate `hits` counter and `data` for biggypacking other information with the key."
  ([m op k]
   (bag-counter m op k {}))
  ([m op k d]
   (let [{:keys [hits data]
          :or   {hits 0 data d}} (get m k)
         r (op hits)]
     (if (pos? r)
       (assoc m k {:hits r :data data})
       (dissoc m k)))))

(tuck/define-event AddHit [element data]
  {}
  (update-in
    app
    [:taxi-ui :uix :loader]
    (fn [v]
     (bag-counter v inc element data))))

(tuck/define-event RemoveHit [element]
  {}
  (update-in
    app
    [:taxi-ui :uix :loader]
    (fn [v]
      (bag-counter v dec element))))

(tuck/define-event UpdateBagData [element data]
                   {}
                   (-> app
                       (update-in [:taxi-ui :uix :loader element :data]
                                  (fn [v] (merge v data)))))

(tuck/define-event ShowSplashMessage [delay-ms element data]
  {}
  (tuck/fx
    app
    (fn [e!]
      (e! (->AddHit element data))
      (.setTimeout js/window #(e! (->UpdateBagData element {:phase :fade-out})) (- delay-ms (:speed data)))
      (.setTimeout js/window #(e! (->RemoveHit element)) delay-ms))))