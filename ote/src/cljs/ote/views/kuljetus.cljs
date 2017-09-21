(ns ote.views.kuljetus
  "Henkilöstokuljetuspalveluita koskevat tarkemmat säädöt"
  (:require [ote.ui.lomake :as lomake]
            [ote.ui.napit :as napit]
            [ote.tiedot.palvelu :as p]
            [ote.ui.debug :as debug]))

(defn reitti [e! tila]
      [:div {:class "row"}
       [:div {:class "col-lg-4"}
        [lomake/lomake
         {:muokkaa! #(e! (p/->MuokkaaPalvelua %))
          :footer-fn (fn [data]
                         [napit/tallenna {:on-click #(e! :FIXME)
                                          :disabled (not (lomake/voi-tallentaa-ja-muokattu? data))}
                          "Tallenna"])}

         [{:otsikko "Muodosta reitti"
           :nimi :liikennevalineet/nimi
           :tyyppi :string}
          ]

         tila]
        [debug/debug tila]]])
