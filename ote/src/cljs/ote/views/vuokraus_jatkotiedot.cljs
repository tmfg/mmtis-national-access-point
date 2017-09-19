(ns ote.views.vuokraus
  "Vuokrauspalvelujen jatkotietojen lomakenäkymä - Laajentaa perustietonäkymää vain
  Vuokraus- ja yhteiskäyttöpalveluille"
  (:require [ote.ui.lomake :as lomake]
            [ote.ui.napit :as napit]
            [ote.tiedot.palvelu :as p]
            [ote.ui.debug :as debug]))

(defn vuokrauspalvelu-jatkotiedot [e! tila]

      [:div {:class "row"}
       [:div {:class "col-lg-4"}
        [lomake/lomake
         {:muokkaa! #(e! (p/->MuokkaaPalvelua %))
          :footer-fn (fn [data]
                         [napit/tallenna {:on-click #(e! :FIXME)
                                          :disabled (not (lomake/voi-tallentaa-ja-muokattu? data))}
                          "Tallenna"])}

         [{:otsikko "Onko käytössä pyörätuolien kuljetukseen soveltuvaa kalustoa"
           :nimi :vuokraus/pyoratuoli
           :tyyppi :string
           :validoi [[:ei-tyhja "Onko pyörätuoleja käytössä"]]
           }

          {:otsikko "Kelpoisuusvaaitumkset"
           :nimi :vuokraus/kelpoisuus
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
       ]
      )
