(ns ote.views.olennaiset-tiedot
  "Olennaisten tietojen lomakenäkymä"
  (:require [ote.ui.lomake :as lomake]
            [ote.ui.napit :as napit]
            [ote.tiedot.palvelu :as p]
            [ote.ui.debug :as debug]
            [ote.domain.liikkumispalvelu :as liikkumispalvelu]))


(defn olennaiset-tiedot [e! tila]

  [:div {:class "col-lg-4"}
   [lomake/lomake
    {:muokkaa! #(e! (p/->MuokkaaPalvelua %))
     :footer-fn (fn [data]
                  [napit/tallenna {:on-click #(e! :FIXME)
                                   :disabled (not (lomake/voi-tallentaa-ja-muokattu? data))}
                   "Tallenna"])}

    [{:otsikko "Palveluntarjoajan nimi"
      :nimi :ot/nimi
      :tyyppi :string
      :validoi [[:ei-tyhja "Anna nimi"]]
      }

     {:otsikko "Y-tunnus"
      :nimi :ot/ytunnus
      :tyyppi :string
      :validoi [[:ytunnus]]}

     {:otsikko "Palveluntarjoajan tyyppi"
      :nimi :ot/tyyppi
      :tyyppi :valinta
      :valinta-nayta liikkumispalvelu/palvelutyypin-nimi
      :valinnat liikkumispalvelu/palvelutyypit }

     ;;(fn [evt idx value] (products/select-category-by-id! value))

     {:otsikko "Puhelin"
      :nimi :ot/puhelin
      :tyyppi :string}

     {:otsikko "GSM"
      :nimi :ot/gsm
      :tyyppi :string}

     {:otsikko "Sähköpostiosoite"
      :nimi :ot/email
      :tyyppi :string}

     {:otsikko "Osoite"
      :nimi :ot/osoite
      :tyyppi :string}

     {:otsikko "Postinumero"
      :nimi :ot/postinumero
      :tyyppi :string}

     {:otsikko "Postitoimipaikka"
      :nimi :ot/postitoimipaikka
      :tyyppi :string}

     {:otsikko "www-osoite"
      :nimi :ot/www-osoite
      :tyyppi :string}

     ]

    tila]

   [debug/debug tila]
   ]
    )
