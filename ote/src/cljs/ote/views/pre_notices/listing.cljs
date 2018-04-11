(ns ote.views.pre-notices.listing
  "Pre notices listing view"
  (:require [reagent.core :as r]
            [ote.app.controller.pre-notices :as pre-notice]
            [ote.ui.table :as table]
            [ote.localization :refer [tr tr-key]]
            [ote.db.transit :as transit]
            [ote.db.modification :as modification]
            [ote.db.transport-operator :as t-operator]
            [ote.views.transport-operator :as t-operator-view]
            [cljs-react-material-ui.icons :as ic]
            [cljs-react-material-ui.reagent :as ui]))


(defn pre-notices [e! {:keys [transport-operator pre-notices] :as app}]
  (if (= :loading pre-notices)
    [:div.loading [:img {:src "/base/images/loading-spinner.gif"}]]
    (let [pre-notices (filter #(= (::t-operator/id transport-operator)
                                  (::t-operator/id %))
                              pre-notices)]
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
        [t-operator-view/transport-operator-selection e! app]

        [table/table {:name->label     (tr-key [:pre-notice-list-page :headers])
                      :key-fn          ::transit/id
                      :no-rows-message (tr [:pre-notice-list-page :no-pre-notices-for-operator])}
         [{:name ::transit/id}
          ;{:name ::transit/pre-notice-type}
          ;{:name ::modification/created}
          ;{:name ::transit/route-description}
          ]

         pre-notices]]])))
