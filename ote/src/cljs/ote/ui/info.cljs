(ns ote.ui.info
  (:require [cljs-react-material-ui.reagent :as ui]
            [ote.localization :refer [tr tr-key]]
            [cljs-react-material-ui.core :refer [color]]
            [stylefy.core :as stylefy]
            [cljs-react-material-ui.icons :as ic]
            [reagent.core :as r]
            [ote.style.info-style :as style-info]))

(defn info-toggle
  "Displays a clickable area that opens to show more info. Can be given default parameter for open state.
  In options can recieve info for :atom, :default-open?, :icon"
  ([title inner-component]
   (info-toggle title inner-component {:default-open? true}))
  ([title inner-component options]
   (let [is-open? (or (:atom options) (r/atom (:default-open? options)))]
     (fn []
       [:div (stylefy/use-style style-info/info-container)
        [:button (merge (stylefy/use-style style-info/info-button)
                        {:on-click #(swap! is-open? not)})
         [:span (stylefy/use-style style-info/info-icon)
          (if-let [icon (:icon options)]
            icon
            [ic/action-help])] title]
        [:div
         (if @is-open?
           (stylefy/use-style style-info/info-open)
           (stylefy/use-style style-info/info-closed))
         inner-component]]))))

