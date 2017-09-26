(ns ote.views.liikennevalineet
  "Pysäköintialueen käyttämät liikennevälineet"
  (:require [ote.ui.form :as form]
            [ote.ui.napit :as napit]
            [ote.tiedot.palvelu :as p]
            [ote.ui.debug :as debug]))

(defn liikennevalineet [e! tila]

  [:div {:class "row"}
   [:div {:class "col-lg-4"}
    [form/form
     {:muokkaa! #(e! (p/->MuokkaaPalvelua %))
      :footer-fn (fn [data]
                   [napit/tallenna {:on-click #(e! :FIXME)
                                    :disabled (form/disable-save? tila)}
                    "Tallenna"])}

     [{:label "Liikennevälineen nimi"
       :name :liikennevalineet/nimi
       :type :string}

      {:label "Liikennevälineen tyyppi"
       :name :liikennevalineet/tyyppi
       :type :string}]

     tila]
    [debug/debug tila]]])
