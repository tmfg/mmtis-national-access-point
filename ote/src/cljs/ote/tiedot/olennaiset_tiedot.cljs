(ns ote.tiedot.palvelu
  "Liikkumispalvelun tietojen käsittely"
  (:require [tuck.core :as t]
            [ote.app.tila :refer [update-state! ]]))

(defrecord MuokkaaPalvelua [tiedot])

(extend-protocol t/Event

  MuokkaaPalvelua
  (process-event [{tiedot :tiedot} app]
    (update app :muokattava-palvelu merge tiedot)))


(defn aseta-perustietotyyppi [uusi-tyyppi]
      (update-state!
        (fn [app]
            (let [nykyinen-tyyppi (get (get app :muokattava-palvelu) :ot/tyyppi)
                  muokattava-palvelu (get app :muokattava-palvelu)
                  uusi-palvelu (assoc muokattava-palvelu :ot/tyyppi uusi-tyyppi)]
                 (assoc app :muokattava-palvelu uusi-palvelu)))))