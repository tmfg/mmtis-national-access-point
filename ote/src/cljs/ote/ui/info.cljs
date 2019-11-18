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
  Can receive options for :default-open?, :icon"
  ([title inner-component]
   (info-toggle title inner-component {:default-open? true}))
  ([title inner-component options]
   (let [is-open? (r/atom (:default-open? options))]
     (fn []
       [:div (stylefy/use-style style-info/info-container)
        [:button (merge {:style (merge
                                  style-info/info-button
                                  {:padding "0.5rem 1rem 0.5rem 1rem"})}
                        {:on-click #(swap! is-open? not)})
         [:span {:style {:margin-right "0.5rem" :padding-top "0.5rem"}}
          (if @is-open?
            [ic/hardware-keyboard-arrow-up]
            [ic/hardware-keyboard-arrow-down])]
         (when (:icon options)
           [:span (stylefy/use-style style-info/info-icon)
            (:icon options)])
         title]
        [:div
         (if @is-open?
           (stylefy/use-style style-info/info-open)
           (stylefy/use-style style-info/info-closed))
         inner-component]]))))

