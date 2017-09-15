(ns ote.tiedot.olennaiset-tiedot
  (:require [tuck.core :as t]))

(defrecord MuokkaaPalvelua [tiedot])

(extend-protocol t/Event

  MuokkaaPalvelua
  (process-event [{tiedot :tiedot} app]
    (update app :muokattava-palvelu merge tiedot)))
