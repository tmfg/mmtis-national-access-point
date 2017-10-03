(ns ote.views.olennaiset-tiedot
  "Olennaisten tietojen lomakenäkymä"
  (:require [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.ui.napit :as napit]
            [ote.tiedot.palvelu :as service]
            [ote.ui.debug :as debug]
            [ote.db.transport-operator :as to-definitions]
            [ote.db.common :as common]
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

(defn olennaiset-tiedot [e! status]
  [:div.row
   [:div 
    [:div
     [:h3 "Vaihe 1: Lisää liikkumispalveluita tuottava organisaatio."]]
    [form/form
     {:name->label (tr-key [:field-labels])
      :update! #(e! (service/->EditTransportOperator %))
      :name #(tr [:olennaiset-tiedot :otsikot %])
      :footer-fn (fn [data]
                   [napit/tallenna {:on-click #(e! (service/->SaveTransportOperator))
                                    :disabled (form/disable-save? data)}
                    "Tallenna"])}

     [{:name ::to-definitions/name
       :type :string
       :validate [[:non-empty "Anna nimi"]]}

      #_{:name :to-definitions/service-type
       :type :selection
       :show-option (tr-key [::to-definitions/service-type])
       :options to-definitions/transport-service-types }

      {:name ::to-definitions/business-id
       :type :string
       :validate [[:business-id]]}

      (form-groups/address (tr [:field-labels ::to-definitions/visiting-address]) ::to-definitions/visiting-address)
      (form-groups/address (tr [:field-labels ::to-definitions/billing-address]) ::to-definitions/billing-address)

      {:name ::to-definitions/phone
       :type :string}

      {:name ::to-definitions/gsm
       :type :string}

      {:name ::to-definitions/email
       :type :string}

      {:name ::to-definitions/homepage
       :type :string}

      ;
      ;(satamapalvelun-lisatiedot status)
      ;(vuokrauspalveluiden-lisatiedot status)
      ]

     status]

    [debug/debug status]]])
