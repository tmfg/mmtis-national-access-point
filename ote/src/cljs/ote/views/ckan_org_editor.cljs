(ns ote.views.ckan-org-editor
  "OTE organization data editor for CKAN organization info page. (CKAN embedded view)"
  (:require [ote.views.theme :refer [theme]]
            [cljs-react-material-ui.reagent :as ui]
            [clojure.string :as str]
            [ote.app.controller.transport-operator :as to]
            [ote.db.transport-operator :as to-definitions]
            [ote.db.common :as common]
            [stylefy.core :as stylefy]
            [ote.style.ckan :as style-ckan]
            [ote.style.base :as style-base]
            [ote.localization :refer [tr tr-or]]
            [ote.views.transport-operator :as to-view]
            [ote.app.controller.front-page :as fp-controller]))

(defn editor [e! _]
  ;; init
  (e! (fp-controller/->GetTransportOperatorData))
  (fn [e! state]
    [theme e! state
     [:div.ote-sovellus
      (if (get-in state [:transport-operator :loading?])
        [:div.loading [:img {:src "/base/images/loading-spinner.gif"}]]
        [to-view/operator e! state])]]))
