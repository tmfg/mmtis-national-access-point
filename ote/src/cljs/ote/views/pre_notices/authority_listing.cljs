(ns ote.views.pre-notices.authority-listing
  "Listing of pre notices for transit authority (city, ELY center) users"
  (:require [reagent.core :as r]
            [ote.db.transit :as transit]
            [ote.localization :refer [tr tr-key]]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.app.controller.pre-notices :as pre-notice]
            [ote.ui.table :as table]
            [clojure.string :as str]
            [ote.time :as time]
            [ote.db.modification :as modification]
            [ote.db.transport-operator :as t-operator]))

(defn pre-notices [e! {:keys [pre-notices] :as app}]
  (if (= :loading pre-notices)
    [:div.loading [:img {:src "/base/images/loading-spinner.gif"}]]
    [:div
     [:div.row
      [:div.col-xs-12.col-sm-12.col-md-12
       [:h1 (tr [:pre-notice-list-page :header-authority-pre-notice-list])]]]
     [:div.row

      [table/table {:name->label     (tr-key [:pre-notice-list-page :headers])
                    :key-fn          ::transit/id
                    :no-rows-message (tr [:pre-notice-list-page :no-pre-notices-for-operator])}
       [{:name ::modification/created :format (comp str time/format-timestamp-for-ui)}
        {:name ::transit/pre-notice-type
         :format #(str/join ", " (map (tr-key [:enums ::transit/pre-notice-type]) %))}
        {:name ::transit/route-description}
        {:name :operator :read (comp ::t-operator/name ::t-operator/transport-operator)}]

       pre-notices]]]))
