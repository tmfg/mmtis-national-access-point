(ns ote.views.pre-notices.authority-listing
  "Listing of pre notices for transit authority (city, ELY center) users"
  (:require [reagent.core :as r]
            [ote.db.transit :as transit]
            [ote.localization :refer [tr tr-key]]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.app.controller.pre-notices :as pre-notice]
            [ote.ui.table :as table]))

(defn pre-notices [e! {:keys [pre-notices] :as app}]
  (if (= :loading pre-notices)
    [:div.loading [:img {:src "/base/images/loading-spinner.gif"}]]
    [:div
     [:div.row
      [:div.col-xs-12.col-sm-6.col-md-9
       [:h1 (tr [:pre-notice-list-page :header-pre-notice-list])]]
      [:div.col-xs-12.col-sm-6.col-md-3
       [ui/raised-button {:label    (tr [:buttons :add-new-pre-notice])
                          :style    {:float "right"}
                          :on-click #(do
                                       (.preventDefault %)
                                       (e! (pre-notice/->CreateNewPreNotice)))
                          :primary  true
                          :icon     (ic/content-add)}]]]
     [:div.row


      [table/table {:name->label     (tr-key [:pre-notice-list-page :headers])
                    :key-fn          ::transit/id
                    :no-rows-message (tr [:pre-notice-list-page :no-pre-notices-for-operator])}
       [{:name ::transit/id}
        ;;{:name ::transit/pre-notice-type}
        ;;{:name ::modification/created}
        ;;{:name ::transit/route-description}
        ]

       pre-notices]]]))
