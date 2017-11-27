(ns ote.views.parking
  "Todo"
  (:require [reagent.core :as reagent]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.ui.buttons :as buttons]
            [ote.app.controller.transport-service :as ts]
            [ote.app.controller.parking :as parking]
            [ote.db.transport-service :as transport-service]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]
            [ote.views.place-search :as place-search]
            [tuck.core :as tuck]))

(defn parking-form-options [e!]
  {:name->label (tr-key [:field-labels :terminal])
   :update!     #(e! (parking/->EditParkingState %))
   :name        #(tr [:olennaiset-tiedot :otsikot %])
   :footer-fn   (fn [data]
                  [buttons/save {:on-click #(e! (parking/->SaveParkingToDb))
                                   :disabled (form/disable-save? data)}
                   (tr [:buttons :save])])})

(defn parking [e! status]
  [:div.row
   [:div {:class "col-lg-12"}
    [:div
     [:h3 "Täydennä pysäköintiin liittyvät tiedot."]]

    [:div {:style {:border "dotted 5px red"
                   :padding "2em"
                   :margin "1em"}}
     "Tämä lomake on työn alla! Tulossa testikäyttöön pian!"]

    [form/form (parking-form-options e!)

     [
      (form/group
        {:label   "Pakkipaikka - tai jotain"
         :columns 1}
        {:name        ::transport-service/:eligibility-requirements
         :type        :localized-text
         }
        )
      ]

     (get status ::transport-service/parking)]

    ]])
