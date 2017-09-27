(ns ote.app.state
  "Contains the frontend application `app` database. 
  Everything that is in the current state of the frontend is in the app atom."
  (:require [reagent.core :as r]))

(defonce app
  (r/atom {:valittu-sivu "Perustiedot" ;; Voi olla vaikka mitä esim Perustiedot, Välityspalvelut, Liikennevälineet
           :muokattava-palvelu {:ot/nimi "Tatun ajopalvelu"
                                :ot/ytunnus "4123412-1"
                                :ot/tyyppi :satama
                                :ot/puhelin "0500 123456"
                                :ot/gsm "0500 123456"
                                :ot/email "erkki.esimerkki@esimerkki.esi"
                                :ot/osoite "Pikkutie 5"
                                :ot/postinumero "90556"
                                :ot/postitoimipaikka "Juupasjärvi"
                                :ot/www-osoite "www.esimerkkijuupasjarvi.fi"
                                :operaattorityyppi [
                                                    {:id "satama" :name "Satama, Asema, Terminaali"}
                                                    {:id "kuljetus" :name "Henkilökuljetuspalvelu"}
                                                    {:id "vuokraus" :name "Ajoneuvojen vuokrauspalvleut ja kaupalliset yhteisökäyttöpalvelut"}
                                                    {:id "pysäköinti" :name "Yhteiskäyttö-/Pysäköintipalvelu"}
                                                    {:id "välityspalvelu" :name "Välityspalvelu"}
                                                    ]
                                :satama/sijainti ["lat ""60.169856" "lon" "24.938379"]
                                :satama/aukioloajat "Maanantaisin 08.00 - 16.00 \n Tiistaisin 08.00 - 16.00"
                                :satama/sisätilakarttakuva "www.solita.fi" ;; Nyt vain viittaus johonkin netissä
                                :satama/www-kartakuva "www.solita.fi"
                                :satama/avustuspalvelut "Ei vielä avustuksia"
                                :satama/erityispalvelut "Lastenhoito saatavilla"
                                :satama/lisätietoja-www "www.solita.fi" ;: tiedot pysäköinnistä, matkatavaroista yms
                                :satama/lisätietoja-kuvaus "Lue lisää pysäköinnistä ja matkatavaroiden käsittelystä"
                                :vuokraus/kelpoisuus "Ajokortti vaaditaan"
                                :vuokraus/pyoratuoli "Ei pyörätuolipaikkoja"
                                }}))
