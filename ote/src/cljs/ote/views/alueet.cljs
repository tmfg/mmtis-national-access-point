(ns ote.views.alueet
  "Vuokrauspalvelujen jatkotietojen lomakenäkymä - Laajentaa perustietonäkymää vain
  Vuokraus- ja yhteiskäyttöpalveluille"
  (:require [ote.ui.form :as form]
            [ote.ui.napit :as napit]
            [ote.tiedot.palvelu :as p]
            [ote.ui.debug :as debug]))

(defn pysakointialueet [e! tila]
  [:div {:class "row"}
   [:div {:class "col-lg-4"}
    [form/form
     {:update! #(e! (p/->MuokkaaPalvelua %))
      :footer-fn (fn [data]
                   [napit/tallenna {:on-click #(e! :FIXME)
                                    :disabled (form/disable-save? tila)}
                    "Tallenna"])}

     [{:label "Sijainti"
       :name :alueet/sijainti
       :type :string
       :validate [[:non-empty "Anna sijainti"]]
       }

      {:label "Pysäköintirajaukset"
       :name :alueet/pysakointirajaukset
       :type :text-area
       :rows 5
       :validate [[:non-empty]]}

      {:label "Maksutavat"
       :name :alueet/maksutavat
       :type :text-area
       :rows 5
       :validate [[:non-empty]]}

      {:label "Erityisryhmät"
       :name :alueet/erityisryhma
       :type :text-area
       :rows 5}

      {:label "Latauspisteet"
       :name :alueet/latauspisteet
       :type :text-area
       :rows 5}

      {:label "Mahdolliset varauspalvelun osoite"
       :name :alueet/www-varauspalvelu
       :type :string}]

     tila]
    [debug/debug tila]]])
