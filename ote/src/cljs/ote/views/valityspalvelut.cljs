(ns ote.views.valituspalvelut
  "Välityspalvelut listaavat tiedot välitettävästä palvelusta palvelutyypeittäin"
  (:require [ote.ui.lomake :as lomake]
            [ote.ui.napit :as napit]
            [ote.tiedot.palvelu :as p]
            [ote.ui.debug :as debug]))

(defn valityspalvelu [e! tila]

      [:div {:class "row"}
       [:div {:class "col-lg-4"}
        [lomake/lomake
         {:muokkaa! #(e! (p/->MuokkaaPalvelua %))
          :footer-fn (fn [data]
                         [napit/tallenna {:on-click #(e! :FIXME)
                                          :disabled (not (lomake/voi-tallentaa-ja-muokattu? data))}
                          "Tallenna"])}

         [{:otsikko "Nimi"
           :nimi :valityspalvelu/nimi
           :tyyppi :string
           :validoi [[:ei-tyhja "Anna sijainti"]]
           }

          {:otsikko "Palvelun kuvaus"
           :nimi :valityspalvelu/kuvaus
           :tyyppi :tekstialue
           :rivit 5
           :validoi [[:ei-tyhja]]}

          {:otsikko "Maksutavat"
           :nimi :alueet/maksutavat
           :tyyppi :tekstialue
           :rivit 5
           :validoi [[:ei-tyhja]]}

          {:otsikko "Erityisryhmät"
           :nimi :alueet/erityisryhma
           :tyyppi :tekstialue
           :rivit 5
           }

          {:otsikko "Latauspisteet"
           :nimi :alueet/latauspisteet
           :tyyppi :tekstialue
           :rivit 5
           }

          {:otsikko "Mahdolliset varauspalvelun osoite"
           :nimi :alueet/www-varauspalvelu
           :tyyppi :string}
          ]

         tila]
        [debug/debug tila]
        ]
       ]
      )
