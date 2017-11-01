(ns ote.views.rental
  "Vuokrauspalvelujen jatkotietojen lomakenäkymä - Laajentaa perustietonäkymää vain
  Vuokraus- ja yhteiskäyttöpalveluille"
  (:require [reagent.core :as reagent]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.ui.buttons :as buttons]
            [ote.app.controller.transport-service :as ts]
            [ote.app.controller.rental :as rental]
            [ote.db.transport-service :as transport-service]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]
            [ote.views.place-search :as place-search]
            [tuck.core :as tuck]))

(defn rental-form-options [e!]
  {:name->label (tr-key [:field-labels :terminal])
   :update!     #(e! (rental/->EditRentalState %))
   :name        #(tr [:olennaiset-tiedot :otsikot %])
   :footer-fn   (fn [data]
                  [buttons/save {:on-click #(e! (rental/->SaveRentalToDb))
                                   :disabled (form/disable-save? data)}
                   (tr [:buttons :save])])})

(defn rental [e! status]
  (.log js/console " Dippadaa rental ")
  [:div.row
   [:div {:class "col-lg-12"}
    [:div
     [:h3 "Täydennä vuokraukseen liittyvät tiedot."]]
    [form/form (rental-form-options e!)

     [
      (form/group
        {:label   "Kelpoiusuus"
         :columns 1}
        {:name        ::transport-service/:eligibility-requirements
         :type        :localized-text
         }
        )
      ]

     (get status ::transport-service/rental)]

    ]])