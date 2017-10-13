(ns ote.views.terminal
  "Required datas for port, station and terminal service"
  (:require [reagent.core :as reagent]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.ui.napit :as napit]
            [ote.app.controller.transport-service :as ts]
            [ote.app.controller.terminal :as terminal]
            [ote.db.transport-service :as transport-service]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]
            [ote.views.place-search :as place-search]
            [tuck.core :as tuck]))

(defn terminal-form-options [e!]
  {:name->label (tr-key [:field-labels :terminal])
   :update!     #(e! (terminal/->EditTerminalState %))
   :name        #(tr [:olennaiset-tiedot :otsikot %])
   :footer-fn   (fn [data]
                  [napit/tallenna {:on-click #(e! (terminal/->SaveTerminalToDb))
                                   :disabled (form/disable-save? data)}
                   (tr [:buttons :save])])})

(defn terminal [e! status]
  (.log js/console " Dippadaa TERMINAALIIIN! ")
  [:div.row
   [:div {:class "col-lg-12"}
    [:div
     [:h3 "Täydennä Satamaan, Asemaan tai Terminaaliin liittyvät tiedot."]]
    [form/form (terminal-form-options e!)

     [
      (form-groups/service-url (tr [:field-labels ::transport-service/indoor-map]) ::transport-service/indoor-map)
      (form/group
        {:label   "Muut palvelut ja esteettömyys"
         :columns 1}
        {:name        ::transport-service/accessibility-tool
         :type        :multiselect-selection
         :show-option (tr-key [:enums ::transport-service/accessibility-tool])
         :options     transport-service/accessibility-tool}
        )
     ]

     (get status ::transport-service/terminal)]

    ]])
