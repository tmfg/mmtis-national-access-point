(ns ote.views.olennaiset-tiedot
  "Olennaisten tietojen lomakenäkymä"
  (:require [ote.ui.form :as form]
            [ote.ui.napit :as napit]
            [ote.tiedot.palvelu :as service]
            [ote.ui.debug :as debug]
            [ote.domain.liikkumispalvelu :as t]
            [ote.localization :refer [tr tr-key]]))

(defn vuokrauspalveluiden-lisatiedot[status]
  (when (= :vuokraus (:ot/tyyppi status))
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


(defn satamapalvelun-lisatiedot [status]
  (when (= :satama (:ot/tyyppi status))

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

(defn henkilokuljetus-lisatiedot [status]

  (when (= :passenger-transportation (:to/service-type status))
    (form/group
     "Henkilöstökuljetuspalveluiden lisätiedot"
    #_ {:label "Reaaliaikapalveluiden www osoite"
      :name    :passenger_transportation_info/www-reaaliaikatiedot
      :type  :string}

     {:label "Matkatavaroita koskevat rajoitukset"
      :name    :passenger_transportation_info/luggage-restrictions
      :type  :text-area
      :rows   5}

   #_  {:label "Pääasiallinen toiminta-alue"
      :name    :kuljetus/paa_toiminta-alue
      :type  :string}

    #_ {:label "Toissijainen toiminta-alue"
      :name    :kuljetus/toissijainen_toiminta-alue
      :type  :string}

    #_ {:label "Anna varauspalvelun www osoite, mikäli sellainen on"
      :name    :kuljetus/www-varauspalvelu
      :type  :string})))


(defn olennaiset-tiedot [e! status]
  [:div.row
   [:div {:class "col-lg-4"}
    [:div
     [:h3 "Vaihe 1: Lisää liikkumispalveluita tuottava organisaatio."]]
    [form/form
     {:name->label (tr-key [:olennaiset-tiedot :otsikot])
      :update! #(e! (service/->EditTransportOperator %))
      :name #(tr [:olennaiset-tiedot :otsikot %])
      :footer-fn (fn [data]
                   [napit/tallenna {:on-click #(e! (service/->SaveTransportOperator))
                                    :disabled (form/disable-save? data)}
                    "Tallenna"])}

     [{:name ::t/name
       :type :string
       :validate [[:non-empty "Anna nimi"]]}

      #_{:name :to/service-type
       :type :selection
       :show-option (tr-key [::t/service-type])
       :options t/transport-service-types }

      {:name ::t/business-id
       :type :string
       :validate [[:business-id]]}

      {:name ::t/phone
       :type :string}

      {:name ::t/gsm
       :type :string}

      {:name ::t/email
       :type :string}

      {:name ::t/homepage
       :type :string}

      ;
      ;(satamapalvelun-lisatiedot status)
      ;(vuokrauspalveluiden-lisatiedot status)
      (henkilokuljetus-lisatiedot status)
      ]

     status]

    [debug/debug status]]])
