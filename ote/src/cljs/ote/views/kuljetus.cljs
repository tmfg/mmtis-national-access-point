(ns ote.views.kuljetus
  "Henkilöstokuljetuspalveluita koskevat tarkemmat säädöt"
  (:require [ote.ui.form :as form]
            [ote.ui.napit :as napit]
            [ote.tiedot.palvelu :as p]
            [ote.ui.debug :as debug]))

(defn reitti [e! tila]
  [:div {:class "row"}
   [:div {:class "col-lg-4"}
    [form/form
     {:update! #(e! (p/->EditTransportOperator %))
      :footer-fn (fn [data]
                   [napit/tallenna {:on-click #(e! :FIXME)
                                    :disabled (form/disable-save? tila)}
                    "Tallenna"])}

     [{:label "Muodosta reitti"
       :name :liikennevalineet/nimi
       :type :string}
      ]

     tila]
    [debug/debug tila]]])
