(ns ote.views.pre-notices.listing
  "Pre notices listing view"
  (:require [reagent.core :as r]
            [ote.app.controller.pre-notices :as pn]
            [ote.ui.table :as table]
            [ote.localization :refer [tr tr-key]]
            [ote.db.transit :as transit]
            [ote.db.modification :as modification]
            [ote.db.transport-operator :as t-operator]
            [ote.views.transport-operator :as t-operator-view]))


(defn pre-notices [e! {:keys [transport-operator pre-notices] :as app}]
  (let [pre-notices (filter #(= (::t-operator/id transport-operator)
                                (::t-operator/id %))
                            pre-notices)]
    [:div
     [t-operator-view/transport-operator-selection e! app]
     [table/table {:name->label (tr-key [:pre-notices :headers])
                   :key-fn ::transit/id
                   :no-rows-message (tr [:pre-notices :no-pre-notices-for-operator])}
      [{:name ::transit/pre-notice-type}
       {:name ::modification/created}
       {:name ::transit/route-description}]

      pre-notices]]))
