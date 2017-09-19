(ns ote.views.olennaiset-tiedot
  "Olennaisten tietojen lomakenäkymä"
  (:require [ote.ui.lomake :as lomake]
            [ote.ui.napit :as napit]
            [ote.tiedot.palvelu :as p]
            [ote.ui.debug :as debug]
            [ote.domain.liikkumispalvelu :as liikkumispalvelu]
            [ote.lokalisaatio :refer [tr tr-avain]]))


(defn olennaiset-tiedot [e! tila]

  [:div {:class "col-lg-4"}
   [lomake/lomake
    {:muokkaa! #(e! (p/->MuokkaaPalvelua %))
     :nimi->otsikko #(tr [:olennaiset-tiedot :otsikot %])
     :footer-fn (fn [data]
                   [napit/tallenna {:on-click #(e! :FIXME)
                                   :disabled (not (lomake/voi-tallentaa-ja-muokattu? data))}
                    "Tallenna"])}

    [{:nimi :ot/nimi
      :tyyppi :string
      :validoi [[:ei-tyhja "Anna nimi"]]}

     {:nimi :ot/y-tunnus
      :tyyppi :string
      :validoi [[:ytunnus]]}

     {:nimi :ot/tyyppi
      :tyyppi :valinta
      :valinta-nayta (tr-avain [::liikkumispalvelu/palvelutyypin-nimi])
      :valinnat liikkumispalvelu/palvelutyypit }

     {:nimi :ot/puhelin :tyyppi :string}
     {:nimi :ot/gsm :tyyppi :string}
     {:nimi :ot/email :tyyppi :string}
     {:nimi :ot/osoite :tyyppi :string}
     {:nimi :ot/postinumero :tyyppi :string}
     {:nimi :ot/postitoimipaikka :tyyppi :string}
     {:nimi :ot/www-osoite :tyyppi :string}]

    tila]

   [debug/debug tila]])
