(ns ote.views.olennaiset-tiedot
  "Olennaisten tietojen lomakenäkymä"
  (:require [ote.ui.form :as form]
            [ote.ui.napit :as napit]
            [ote.tiedot.palvelu :as p]
            [ote.ui.debug :as debug]
            [ote.domain.liikkumispalvelu :as liikkumispalvelu]
            [ote.localization :refer [tr tr-key]]))

(defn vuokrauspalveluiden-lisatiedot[tila]
  (when (= :vuokraus (:ot/tyyppi tila))
    (form/group
     "Ajoneuvojen vuokrauspalveluiden ja kaupallisten yhteiskäyttöpalveluiden lisätiedot"
     {:label "Onko käytössä pyörätuolien kuljetukseen soveltuvaa kalustoa"
      :name    :vuokraus/pyoratuoli
      :type  :string
      :validate [[:non-empty "Onko pyörätuoleja käytössä"]]
      }

     {:label "Kelpoisuusvaatimukset"
      :name    :vuokraus/kelpoisuus
      :type  :text-area
      :rows   5
      :validate [[:non-empty]]}

     {:label "Sisätilakartta"
      :name    :satama/sisätilakarttakuva
      :type  :string}

     {:label "Sisätilakartan osoite"
      :name    :satama/www-kartakuva
      :type  :string}

     {:label "Avustuspalvelut"
      :name    :satama/avustuspalvelut
      :type  :text-area
      :rows   5}

     {:label "Erityispalvelut"
      :name    :satama/erityispalvelut
      :type  :text-area
      :rows   5}

     {:label "Lisätietoihin viittaava www osoite"
      :name    :satama/lisätietoja-www
      :type  :string}

     {:label "Lisätieto www-osoitteen kuvaus"
      :name    :satama/lisätietoja-kuvaus
      :type  :text-area
      :rows   5}
     )))


(defn satamapalvelun-lisatiedot [tila]
  (when (= :satama (:ot/tyyppi tila))

    (form/group
     "Satamien, Asemien ja Terminaalien lisätiedot"
     {:label "Sijainti"
      :name    :satama/sijainti
      :type  :string
      :validate [[:non-empty "Anna sijainti"]]}

     {:label "Aukioloajat"
      :name    :satama/aukioloajat
      :type  :text-area
      :rows   5
      :validate [[:non-empty]]}

     {:label "Sisätilakartta"
      :name    :satama/sisätilakarttakuva
      :type  :string}

     {:label "Sisätilakartan osoite"
      :name    :satama/www-kartakuva
      :type  :string}

     {:label "Avustuspalvelut"
      :name    :satama/avustuspalvelut
      :type  :text-area
      :rows   5}

     {:label "Erityispalvelut"
      :name    :satama/erityispalvelut
      :type  :text-area
      :rows   5}

     {:label "Lisätietoihin viittaava www osoite"
      :name    :satama/lisätietoja-www
      :type  :string}

     {:label "Lisätieto www-osoitteen kuvaus"
      :name    :satama/lisätietoja-kuvaus
      :type  :text-area
      :rows   5})))

(defn henkilokuljetus-lisatiedot [tila]
  (when (= :kuljetus (:ot/tyyppi tila))
    (form/group
     "Henkilöstökuljetuspalveluiden lisätiedot"
     {:label "Reaaliaikapalveluiden www osoite"
      :name    :kuljetus/www-reaaliaikatiedot
      :type  :string}

     {:label "Matkatavaroita koskevat rajoitukset"
      :name    :kuljetus/matkatavara_rajoitukset
      :type  :text-area
      :rows   5}

     {:label "Pääasiallinen toiminta-alue"
      :name    :kuljetus/paa_toiminta-alue
      :type  :string}

     {:label "Toissijainen toiminta-alue"
      :name    :kuljetus/toissijainen_toiminta-alue
      :type  :string}
     {:label "Anna varauspalvelun www osoite, mikäli sellainen on"
      :name    :kuljetus/www-varauspalvelu
      :type  :string})))


(defn olennaiset-tiedot [e! tila]
  [:div.row
   [:div {:class "col-lg-4"}
    [:div
     [:h3 "Olennaiset tiedot"]]
    [form/form
     {:update! #(e! (p/->MuokkaaPalvelua %))
      :name #(tr [:olennaiset-tiedot :otsikot %])
      :footer-fn (fn [data]
                   [napit/tallenna {:on-click #(e! :FIXME)
                                    :disabled (form/disable-save? tila)}
                    "Tallenna"])}

     [{:name :ot/nimi
       :type :string
       :validate [[:non-empty "Anna nimi"]]}

      {:name :ot/y-tunnus
       :type :string
       :validate [[:business-id]]}

      {:name :ot/tyyppi
       :type :selection
       :show-option (tr-key [::liikkumispalvelu/palvelutyypin-nimi])
       :options liikkumispalvelu/transport-service-types }

      {:label "Puhelin"
       :name :ot/puhelin
       :type :string}

      {:label "GSM"
       :name :ot/gsm
       :type :string}

      {:label "Sähköpostiosoite"
       :name :ot/email
       :type :string}

      {:label "Osoite"
       :name :ot/osoite
       :type :string}

      {:label "Postinumero"
       :name :ot/postinumero
       :type :string}

      {:label "Postitoimipaikka"
       :name :ot/postitoimipaikka
       :type :string}

      {:label "www-osoite"
       :name :ot/www-osoite
       :type :string}

      (satamapalvelun-lisatiedot tila)
      (vuokrauspalveluiden-lisatiedot tila)
      (henkilokuljetus-lisatiedot tila)]

     tila]

    [debug/debug tila]]])
