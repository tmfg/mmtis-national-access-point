(ns ote.app.tila
  "Tämä nimiavaruus sisältää sovelluksen app db:n eli
  atomin, jossa on koko sovelluksen tila."
  (:require [reagent.core :as r]))

(defonce app
  (r/atom {:muokattava-palvelu {:ot/nimi "Tatun ajopalvelu"
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
                                                    {:id "satama" :nimi "Satama, Asema, Terminaali"}
                                                    {:id "kuljetus" :nimi "Henkilökuljetuspalvelu"}
                                                    {:id "vuokraus" :nimi "Ajoneuvojen vuokrauspalvleut ja kaupalliset yhteisökäyttöpalvelut"}
                                                    {:id "pysäköinti" :nimi "Yhteiskäyttö-/Pysäköintipalvelu"}
                                                    {:id "välityspalvelu" :nimi "Välityspalvelu"}
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
                                }
           }
           ))

(defn update-state! [update-fn & args]
      (swap! app
             (fn [current-app-state]
                 (apply update-fn current-app-state args))))


(defn organisaatio-tyyppi []
      (let [palvelu (get app :muokattava-palvelu)]
           (println "palvelu -> " palvelu app)
           (get palvelu :ot/tyyppi)))