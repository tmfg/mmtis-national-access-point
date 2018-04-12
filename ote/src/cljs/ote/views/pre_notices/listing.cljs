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
            [cljs-react-material-ui.reagent :as ui]
            [ote.time :as time]
            [clojure.string :as str]))

(defn pre-notice-type->str
  [types]
  (str/join ", " (map #(case %
                     :route-change "Reitin muutos"
                     :termination "Reitin poisto",
                     :new "Uusi reitti",
                     :schedule-change "Aikataulun muutos",
                     :other "Muu") types)))


(defn pre-notices [e! {:keys [transport-operator pre-notices] :as app}]
  (if (= :loading pre-notices)
    [:div.loading [:img {:src "/base/images/loading-spinner.gif"}]]
    (let [pre-notices (filter #(= (::t-operator/id transport-operator)
                                  (::t-operator/id %))
                              pre-notices)]
      (println pre-notices)
      [:div
       [:div.row
        [:div.col-xs-12.col-sm-6.col-md-9
         [:h1 (tr [:pre-notice-list-page :header-pre-notice-list])]]
        [:div.col-xs-12.col-sm-6.col-md-3
         [ui/raised-button {:label (tr [:buttons :add-new-pre-notice])
                            :style {:float "right"}
                            :on-click #(do
                                         (.preventDefault %)
                                         (e! (pre-notice/->CreateNewPreNotice)))
                            :primary true
                            :icon (ic/content-add)}]]]
       [:div.row
        [t-operator-view/transport-operator-selection e! app]

        [table/table {:name->label (tr-key [:pre-notice-list-page :headers])
                      :key-fn ::transit/id
                      :no-rows-message (tr [:pre-notice-list-page :no-pre-notices-for-operator])
                      :on-select #(when (seq %)
                                   (e! (pre-notice/->ModifyPreNotice (first (::transit/id %)))))}
         [{:name ::transit/id}
          {:name ::transit/pre-notice-type
           :read (comp pre-notice-type->str ::transit/pre-notice-type)}
          {:name ::modification/created
           :read (comp time/format-timestamp-for-ui ::modification/created)}
          {:name ::modification/modified
           :read (comp time/format-timestamp-for-ui ::modification/modified)}
          {:name ::transit/route-description}]
         pre-notices]]])))
