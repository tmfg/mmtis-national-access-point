(ns ote.app.tila
  "Tämä nimiavaruus sisältää sovelluksen app db:n eli
  atomin, jossa on koko sovelluksen tila."
  (:require [reagent.core :as r]))

(defonce app
  (r/atom {:muokattava-palvelu {:ot/nimi "Tatun ajopalvelu"
                                :ot/ytunnus "412341231"}}))
