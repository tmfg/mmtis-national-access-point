(ns ote.views.ckan-org-editor
  "OTE organization data editor for CKAN organization info page. (CKAN embedded view)"
  (:require [ote.app.controller.ckan-org-editor :as org-edit]
            [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [cljs-react-material-ui.reagent :as ui]
            [clojure.string :as str]
            [ote.app.controller.transport-operator :as to]
            [ote.db.transport-operator :as to-definitions]
            [ote.db.common :as common]
            [stylefy.core :as stylefy]
            [ote.style.ckan :as style-ckan]
            [ote.style.base :as style-base]
            [ote.localization :refer [tr tr-or]]
            [ote.views.transport-operator :as to-view]))

(defn editor [e! status]
  ;; init
  (e! (org-edit/->StartEditor))
  (fn [e! {:keys [transport-operator] :as app}]
    [ui/mui-theme-provider
     {:mui-theme
      (get-mui-theme
        {:palette   {;; primary nav color - Also Focus color in text fields
                     :primary1-color (color :blue700)

                     ;; Hint color in text fields
                     :disabledColor  (color :grey900)

                     ;; canvas color
                     ;;:canvas-color  (color :lightBlue50)

                     ;; Main text color
                     :text-color     (color :grey900)
                     }

         :button    {:labelColor "#fff"}
         :menu-item {:selected-text-color (color :blue700)} ;; Change drop down list items selected color
         })}

     [:div.container
      [to-view/operator e! transport-operator]]]))
