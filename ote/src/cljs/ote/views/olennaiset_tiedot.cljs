(ns ote.views.olennaiset-tiedot
  "Olennaisten tietojen lomakenäkymä"
  (:require [ote.ui.lomake :as lomake]
            [ote.ui.napit :as napit]
            [ote.tiedot.palvelu :as p]
            [ote.ui.debug :as debug]
            [ote.domain.liikkumispalvelu :as liikkumispalvelu]
            [ote.lokalisaatio :refer [tr tr-avain]]))

(defn vuokrauspalveluiden-lisatiedot[tila]
      (when (= :vuokraus (:ot/tyyppi tila))
            (lomake/ryhma "Ajoneuvojen vuokrauspalveluiden ja kaupallisten yhteiskäyttöpalveluiden lisätiedot"
                          {:otsikko "Onko käytössä pyörätuolien kuljetukseen soveltuvaa kalustoa"
                           :nimi    :vuokraus/pyoratuoli
                           :tyyppi  :string
                           :validoi [[:ei-tyhja "Onko pyörätuoleja käytössä"]]
                           }

                          {:otsikko "Kelpoisuusvaatimukset"
                           :nimi    :vuokraus/kelpoisuus
                           :tyyppi  :tekstialue
                           :rivit   5
                           :validoi [[:ei-tyhja]]}

                          {:otsikko "Sisätilakartta"
                           :nimi    :satama/sisätilakarttakuva
                           :tyyppi  :string}

                          {:otsikko "Sisätilakartan osoite"
                           :nimi    :satama/www-kartakuva
                           :tyyppi  :string}

                          {:otsikko "Avustuspalvelut"
                           :nimi    :satama/avustuspalvelut
                           :tyyppi  :tekstialue
                           :rivit   5}

                          {:otsikko "Erityispalvelut"
                           :nimi    :satama/erityispalvelut
                           :tyyppi  :tekstialue
                           :rivit   5}

                          {:otsikko "Lisätietoihin viittaava www osoite"
                           :nimi    :satama/lisätietoja-www
                           :tyyppi  :string}

                          {:otsikko "Lisätieto www-osoitteen kuvaus"
                           :nimi    :satama/lisätietoja-kuvaus
                           :tyyppi  :tekstialue
                           :rivit   5}
                          ))
      )


(defn satamapalvelun-lisatiedot [tila]

      (when (= :satama (:ot/tyyppi tila))

            (lomake/ryhma "Satamien, Asemien ja Terminaalien lisätiedot"
                          {:otsikko "Sijainti"
                           :nimi    :satama/sijainti
                           :tyyppi  :string
                           :validoi [[:ei-tyhja "Anna sijainti"]]
                           }

                          {:otsikko "Aukioloajat"
                           :nimi    :satama/aukioloajat
                           :tyyppi  :tekstialue
                           :rivit   5
                           :validoi [[:ei-tyhja]]}

                          {:otsikko "Sisätilakartta"
                           :nimi    :satama/sisätilakarttakuva
                           :tyyppi  :string}

                          {:otsikko "Sisätilakartan osoite"
                           :nimi    :satama/www-kartakuva
                           :tyyppi  :string}

                          {:otsikko "Avustuspalvelut"
                           :nimi    :satama/avustuspalvelut
                           :tyyppi  :tekstialue
                           :rivit   5}

                          {:otsikko "Erityispalvelut"
                           :nimi    :satama/erityispalvelut
                           :tyyppi  :tekstialue
                           :rivit   5}

                          {:otsikko "Lisätietoihin viittaava www osoite"
                           :nimi    :satama/lisätietoja-www
                           :tyyppi  :string}

                          {:otsikko "Lisätieto www-osoitteen kuvaus"
                           :nimi    :satama/lisätietoja-kuvaus
                           :tyyppi  :tekstialue
                           :rivit   5}
                          )
            )
      )

(defn olennaiset-tiedot [e! tila]

      [:div.row
      [:div {:class "col-lg-4"}
   [:div
    [:h3
     "Olennaiset tiedot"
     ]
    ]
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

     (satamapalvelun-lisatiedot tila)
     (vuokrauspalveluiden-lisatiedot tila)
     ]

    tila]

   [debug/debug tila]
   ]
       ]
    )
