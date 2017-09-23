(ns ote.views.valituspalvelut
  "Välityspalvelut listaavat tiedot välitettävästä palvelusta palvelutyypeittäin"
  (:require [ote.ui.lomake :as lomake]
            [ote.ui.napit :as napit]
            [ote.tiedot.palvelu :as p]
            [ote.ui.debug :as debug]
            [ote.domain.liikkumispalvelu :as liikkumispalvelu]
            [ote.localization :refer [tr tr-key]]))

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
           :validoi [[:ei-tyhja "Anna välityspalvelun nimi"]]
           }

          {:otsikko "Palvelun kuvaus"
           :nimi :valityspalvelu/kuvaus
           :tyyppi :tekstialue
           :rivit 5
           :validoi [[:ei-tyhja]]}

          {:otsikko "Välityspalvelun tyyppi"
           :nimi :valityspalvelu/tyyppi
           :tyyppi :valinta
           :valinta-nayta (tr-key [::liikkumispalvelu/palvelutyypin-nimi])
           :valinnat liikkumispalvelu/palvelutyypit }

          {:otsikko "Pääasiallinen toiminta-alue"
           :nimi :valityspalvelu/paa-alue
           :tyyppi :tekstialue
           :rivit 2
           }

          {:otsikko "Toissijainen toiminta-alue"
           :nimi :valityspalvelu/toissijainen-alue
           :tyyppi :tekstialue
           :rivit 2
           }

          {:otsikko "Hinnasto"
           :nimi :valityspalvelu/hinnasto
           :tyyppi :tekstialue
           :rivit 3
           }
          ]

         tila]
        [debug/debug tila]
        ]
       ]
      )
