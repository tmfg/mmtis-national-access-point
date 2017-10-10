(ns ote.views.vuokraus
  "Vuokrauspalvelujen jatkotietojen lomakenäkymä - Laajentaa perustietonäkymää vain
  Vuokraus- ja yhteiskäyttöpalveluille"
  (:require [ote.ui.form :as form]
            [ote.ui.napit :as napit]
            [ote.app.controller.transport-operator :as p]
            [ote.ui.debug :as debug]))

(defn vuokrauspalvelu-jatkotiedot [e! tila]
  [:div {:class "row"}
   [:div {:class "col-lg-4"}
    [form/form
     {:muokkaa! #(e! (p/->EditTransportOperatorState %))
      :footer-fn (fn [data]
                   [napit/tallenna {:on-click #(e! :FIXME)
                                    :disabled (form/disable-save? tila)}
                    "Tallenna"])}

     [{:label "Onko käytössä pyörätuolien kuljetukseen soveltuvaa kalustoa"
       :name :vuokraus/pyoratuoli
       :type :string
       :validate [[:non-empty "Onko pyörätuoleja käytössä"]]
       }

      {:label "Kelpoisuusvaatimukset"
       :name :vuokraus/kelpoisuus
       :type :text-area
       :rows 5
       :validate [[:non-empty]]}

      {:label "Sisätilakartta"
       :name :satama/sisätilakarttakuva
       :type :string}

      {:label "Sisätilakartan osoite"
       :name :satama/www-kartakuva
       :type :string}

      {:label "Avustuspalvelut"
       :name :satama/avustuspalvelut
       :type :text-area
       :rows 5}

      {:label "Erityispalvelut"
       :name :satama/erityispalvelut
       :type :text-area
       :rows 5}

      {:label "Lisätietoihin viittaava www osoite"
       :name :satama/lisätietoja-www
       :type :string}

      {:label "Lisätieto www-osoitteen kuvaus"
       :name :satama/lisätietoja-kuvaus
       :type :text-area
       :rows 5}]

     tila]]])
