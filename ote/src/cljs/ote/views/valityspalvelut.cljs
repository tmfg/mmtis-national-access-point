(ns ote.views.valituspalvelut
  "Välityspalvelut listaavat tiedot välitettävästä palvelusta palvelutyypeittäin"
  (:require [ote.ui.form :as form]
            [ote.ui.napit :as napit]
            [ote.tiedot.palvelu :as p]
            [ote.ui.debug :as debug]
            [ote.domain.liikkumispalvelu :as liikkumispalvelu]
            [ote.localization :refer [tr tr-key]]))

(defn valityspalvelu [e! tila]

      [:div {:class "row"}
       [:div {:class "col-lg-4"}
        [form/form
         {:update! #(e! (p/->MuokkaaPalvelua %))
          :footer-fn (fn [data]
                       [napit/tallenna {:on-click #(e! :FIXME)
                                        :disabled (form/disable-save? tila)}
                        "Tallenna"])}

         [{:label "Nimi"
           :name :valityspalvelu/nimi
           :type :string
           :validate [[:non-empty "Anna välityspalvelun nimi"]]
           }

          {:label "Palvelun kuvaus"
           :name :valityspalvelu/kuvaus
           :type :text-area
           :rows 5
           :validate [[:non-empty]]}

          {:label "Välityspalvelun tyyppi"
           :name :valityspalvelu/tyyppi
           :type :valinta
           :valinta-nayta (tr-key [::liikkumispalvelu/palvelutyypin-nimi])
           :valinnat liikkumispalvelu/transport-service-types }

          {:label "Pääasiallinen toiminta-alue"
           :name :valityspalvelu/paa-alue
           :type :text-area
           :rows 2
           }

          {:label "Toissijainen toiminta-alue"
           :name :valityspalvelu/toissijainen-alue
           :type :text-area
           :rows 2
           }

          {:label "Hinnasto"
           :name :valityspalvelu/hinnasto
           :type :text-area
           :rows 3
           }
          ]

         tila]
        [debug/debug tila]
        ]
       ]
      )
