(ns ote.views.transport-operator
  "Olennaisten tietojen lomakenäkymä"
  (:require [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.ui.napit :as napit]
            [ote.app.controller.transport-operator :as to]
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
  [:span
   [:div
    [:h3 "Organisaation tiedot"]]
   [form/form
    {:name->label (tr-key [:field-labels])
     :update! #(e! (to/->EditTransportOperatorState %))
     :name #(tr [:olennaiset-tiedot :otsikot %])
     :footer-fn (fn [data]
                  [napit/tallenna {:on-click #(e! (to/->SaveTransportOperatorToDb))
                                   :disabled (form/disable-save? data)}
                   "Tallenna"])}

    [(form/group
      {:label "Perustiedot"
       :columns 1}
      {:name ::to-definitions/name
       :type :string
       :validate [[:non-empty "Anna nimi"]]}

      {:name ::to-definitions/business-id
       :type :string
       :validate [[:business-id]]}

      {:name ::common/street
       :type :string
       :read (comp ::common/street ::to-definitions/visiting-address)
       :write (fn [data street]
                (assoc-in data [::to-definitions/visiting-address ::common/street] street))}

      {:name ::common/postal-code
       :type :string
       :read (comp ::common/postal_code ::to-definitions/visiting-address)
       :write (fn [data postal-code]
                (assoc-in data [::to-definitions/visiting-address ::common/postal-code] postal-code))}

      {:name ::common/post-office
       :type :string
       :read (comp ::common/postal_office ::to-definitions/visiting-address)
       :write (fn [data post-office]
                (assoc-in data [::to-definitions/visiting-address ::common/post-office] post-office))}

      {:name ::to-definitions/homepage
       :type :string})

     (form/group
      {:label "Yhteystavat" ;;FIXME: translate
       :columns 1}

      {:name ::to-definitions/phone :type :string}
      {:name ::to-definitions/gsm :type :string}
      {:name ::to-definitions/email :type :string}
      {:name ::to-definitions/facebook :type :string}
      {:name ::to-definitions/twitter :type :string}
      {:name ::to-definitions/instant-message :type :string})]

    status]
   ])
