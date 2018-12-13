(ns ote.ui.info
  (:require [cljs-react-material-ui.reagent :as ui]
            [ote.localization :refer [tr tr-key]]
            [cljs-react-material-ui.core :refer [color] :as mui]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]
            [ote.ui.common :as common]
            [reagent.core :as r]
            [ote.style.info-style :as style-info]))

(def is-open? (r/atom false))

(defn info-toggle
  "Displays a clickable area that opens to show more info"
  [title text]
  [:div
   [mui/card-actions]
   [:button (merge (stylefy/use-style style-info/info-button)
              {:on-click #(swap! is-open? not)})
    title]
   [:p
      (if @is-open?
             (stylefy/use-style style-info/info-open)
             (stylefy/use-style style-info/info-closed))
    (str @is-open?)]])

