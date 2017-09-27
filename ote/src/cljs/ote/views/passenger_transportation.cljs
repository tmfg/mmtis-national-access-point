(ns ote.views.passenger-transportation
  "Required datas for passenger transportation provider"
  (:require [ote.ui.form :as form]
            [ote.ui.napit :as napit]
            [ote.tiedot.palvelu :as service]
            [ote.ui.debug :as debug]
            [ote.domain.liikkumispalvelu :as t]
            [ote.localization :refer [tr tr-key]]))


(defn henkilokuljetus-lisatiedot1 [status]

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


(defn passenger-transportation-info [e! status]
  [:div.row
   [:div {:class "col-lg-4"}
    [:div
     [:h3 "Vaihe 2: Täydennä henkilökuljetukseen liittyvät tiedot."]]
    [form/form
     {:name->label (tr-key [:olennaiset-tiedot :otsikot])
      :update! #(e! (service/->EditTransportService %))
      :name #(tr [:olennaiset-tiedot :otsikot %])
      :footer-fn (fn [data]
                   [napit/tallenna {:on-click #(e! (service/->SavePassengerTransportData))
                                    :disabled (form/disable-save? data)}
                    "Tallenna"])}

     [{
       ;:label "Matkatavaroita koskevat rajoitukset"
       :name    ::t/luggage-restrictions
       :type  :localized-text
       :rows   5}

      ]

     status]

    [debug/debug status]]])
