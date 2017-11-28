(ns ote.views.brokerage
  "Todo"
  (:require [reagent.core :as reagent]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.ui.buttons :as buttons]
            [ote.app.controller.transport-service :as ts]
            [ote.app.controller.brokerage :as brokerage]
            [ote.db.transport-service :as transport-service]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]
            [ote.views.place-search :as place-search]
            [tuck.core :as tuck]))

(defn brokerage-form-options [e!]
  {:name->label (tr-key [:field-labels :terminal])
   :update!     #(e! (brokerage/->EditBrokerageState %))
   :name        #(tr [:olennaiset-tiedot :otsikot %])
   :footer-fn   (fn [data]
                  [buttons/save {:on-click #(e! (brokerage/->SaveBrokerageToDb))
                                   :disabled (form/disable-save? data)}
                   (tr [:buttons :save])])})

(defn brokerage [e! status]
  [:div.row
   [:div {:class "col-lg-12"}
    [:div
     [:h1 "Täydennä välityspalveluun liittyvät tiedot."]]

    [:div {:style {:border "dotted 5px red"
                   :padding "2em"
                   :margin "1em"}}
     "Tämä lomake on työn alla! Tulossa testikäyttöön pian!"]

    [form/form (brokerage-form-options e!)

     [
      (form/group
        {:label   "Välityspalveluun jotain"
         :columns 1}
        {:name        ::transport-service/:eligibility-requirements
         :type        :localized-text
         }
        )
      ]

     (get status ::transport-service/brokerage)]

    ]])
