(ns ote.views.valituspalvelut
  "Välityspalvelut listaavat tiedot välitettävästä palvelusta palvelutyypeittäin"
  (:require [ote.ui.form :as form]
            [ote.ui.napit :as napit]
            [ote.app.controller.transport-operator :as p]
            [ote.ui.debug :as debug]
            [ote.db.transport-service :as transport-service]
            [ote.localization :refer [tr tr-key]]))

(defn valityspalvelu [e! tila]

      [:div {:class "row"}
       [:div {:class "col-lg-4"}
        [form/form
         {:update! #(e! (p/->EditTransportOperatorState %))
          :footer-fn (fn [data]
                       [napit/tallenna {:on-click #(e! :FIXME)
                                        :disabled (form/disable-save? tila)}
                        "Tallenna"])}

         [{:label "Nimi"
           :name :valityspalvelu/nimi
           :type :string
           :validate [[:non-empty "Anna välityspalvelun nimi"]]}

          {:label "Palvelun kuvaus"
           :name :valityspalvelu/kuvaus
           :type :text-area
           :rows 5
           :validate [[:non-empty]]}

          {:label "Välityspalvelun tyyppi"
           :name :valityspalvelu/tyyppi
           :type :selection
           :show-option (tr-key [::transport-service/palvelutyypin-nimi])
           :options transport-service/transport-service-types }

          {:label "Pääasiallinen toiminta-alue"
           :name :valityspalvelu/paa-alue
           :type :text-area
           :rows 2}

          {:label "Toissijainen toiminta-alue"
           :name :valityspalvelu/toissijainen-alue
           :type :text-area
           :rows 2}

          {:label "Hinnasto"
           :name :valityspalvelu/hinnasto
           :type :text-area
           :rows 3}]

         tila]]])
