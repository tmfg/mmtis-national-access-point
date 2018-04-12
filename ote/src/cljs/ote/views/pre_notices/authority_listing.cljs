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

(defn pre-notice-view [e! pre-notice]
  [ui/dialog
   {:open true
    :modal true
    :auto-scroll-body-content true
    :title   "ilmoitus jee"
    :actions [(r/as-element
               [ui/flat-button
                {:label     (tr [:buttons :close])
                 :secondary true
                 :primary   true
                 :on-click  #(e! (pre-notice/->ClosePreNotice))}])]}
   [:div (pr-str pre-notice)]])

(defn pre-notices-listing [e! pre-notices]
  (if (= :loading pre-notices)
    [:div.loading [:img {:src "/base/images/loading-spinner.gif"}]]
    [:div
     [:div.row
      [:div.col-xs-12.col-sm-12.col-md-12
       [:h1 (tr [:pre-notice-list-page :header-authority-pre-notice-list])]]]
     [:div.row

      [table/table {:name->label     (tr-key [:pre-notice-list-page :headers])
                    :key-fn          ::transit/id
                    :no-rows-message (tr [:pre-notice-list-page :no-pre-notices-for-operator])
                    :on-select #(e! (pre-notice/->ShowPreNotice (::transit/id (first %))))}
       [{:name ::modification/created :format (comp str time/format-timestamp-for-ui)}
        {:name ::transit/pre-notice-type
         :format #(str/join ", " (map (tr-key [:enums ::transit/pre-notice-type]) %))}
        {:name ::transit/route-description}
        {:name :operator :read (comp ::t-operator/name ::t-operator/transport-operator)}]

       pre-notices]]]))

(defn pre-notices [e! {:keys [pre-notices pre-notice-dialog] :as app}]
  (if pre-notice-dialog
    [pre-notice-view e! pre-notice-dialog]
    [pre-notices-listing e! pre-notices]))
