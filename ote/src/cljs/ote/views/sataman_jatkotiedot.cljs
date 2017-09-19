(ns ote.views.satama
  "Sataman jatkotietojen lomakenäkymä - Laajentaa perustietonäkymää vain satamille, Asemille ja terminaaleille"
  (:require [ote.ui.lomake :as lomake]
            [ote.ui.napit :as napit]
            [ote.tiedot.palvelu :as p]
            [ote.ui.debug :as debug]))

(defn sataman-jatkotiedot [e! tila]

       [:div {:class "col-lg-4"}
        [lomake/lomake
         {:muokkaa! #(e! (p/->MuokkaaPalvelua %))
          :footer-fn (fn [data]
                         [napit/tallenna {:on-click #(e! :FIXME)
                                          :disabled (not (lomake/voi-tallentaa-ja-muokattu? data))}
                          "Tallenna"])}

         [{:otsikko "Sijainti"
           :nimi :satama/sijainti
           :tyyppi :string
           :validoi [[:ei-tyhja "Anna sijainti"]]
           }

          {:otsikko "Aukioloajat"
           :nimi :satama/aukioloajat
           :tyyppi :tekstialue
           :rivit 5
           :validoi [[:ei-tyhja]]}

          {:otsikko "Sisätilakartta"
           :nimi :satama/sisätilakarttakuva
           :tyyppi :string}

          {:otsikko "Sisätilakartan osoite"
           :nimi :satama/www-kartakuva
           :tyyppi :string}

          {:otsikko "Avustuspalvelut"
           :nimi :satama/avustuspalvelut
           :tyyppi :tekstialue
           :rivit 5}

          {:otsikko "Erityispalvelut"
           :nimi :satama/erityispalvelut
           :tyyppi :tekstialue
           :rivit 5}

          {:otsikko "Lisätietoihin viittaava www osoite"
           :nimi :satama/lisätietoja-www
           :tyyppi :string}

          {:otsikko "Lisätieto www-osoitteen kuvaus"
           :nimi :satama/lisätietoja-kuvaus
           :tyyppi :tekstialue
           :rivit 5}


          ]

         tila]
        [debug/debug tila]
        ]
      )
