(ns ote.views.olennaiset-tiedot
  "Olennaisten tietojen lomakenäkymä"
  (:require [ote.ui.lomake :as lomake]
            [ote.ui.napit :as napit]
            [ote.tiedot.palvelu :as p]
            [ote.ui.debug :as debug]))


(defn olennaiset-tiedot [e! tila]
  [:div
   [lomake/lomake
    {:muokkaa! #(e! (p/->MuokkaaPalvelua %))
     :footer-fn (fn [data]
                  [napit/tallenna {:on-click #(e! :FIXME)
                                   :disabled (not (lomake/voi-tallentaa-ja-muokattu? data))}
                   "Tallenna"])}

    [{:otsikko "Nimi"
      :nimi :ot/nimi
      :tyyppi :string}

     {:otsikko "Y-tunnus"
      :nimi :ot/y-tunnus
      :tyyppi :string
      :validoi [[:ytunnus]]}]

    tila]

   [debug/debug tila]
   ])
