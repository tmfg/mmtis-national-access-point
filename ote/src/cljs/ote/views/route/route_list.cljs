(ns ote.views.route.route-list
  "List own routes"
  (:require
    [ote.localization :refer [tr tr-key]]
    [cljs-react-material-ui.reagent :as ui]
    [ote.app.controller.route.route-list :as route-list]
    [cljs-react-material-ui.icons :as ic]))

(defn list-routes [e! routes]
  [ui/table                                                 ;(stylefy/use-style style-base/front-page-service-table)
   [ui/table-header {:adjust-for-checkbox false
                     :display-select-all  false}
    [ui/table-row {:selectable false}
     [ui/table-header-column "Id"]
     [ui/table-header-column "Nimi"]]]
   [ui/table-body {:display-row-checkbox false}
    (doall
      (map-indexed
        (fn [i {:keys [id name] :as row}]
          ^{:key (str "route-" i)}
          [ui/table-row {:key (str "route-" i) :selectable false :display-border false}
           [ui/table-row-column id]
           [ui/table-row-column name]])
        routes))]])

(defn list-operators [e! operators]
  (.log js/console " my operators " (clj->js operators))
  [:div
   (doall
     (map-indexed
       (fn [i row]
         ^{:key (str "operator-" i)}

         [:div
          [:h2 (:operator row)]
          [list-routes e! (:routes row)]])
       operators))])



(defn routes [e! app]
  (e! (route-list/->LoadRoutes))
  (fn [e! app]
    [:div
     [:div.row
      [:div.col-xs-12.col-sm-6.col-md-9
       [:h1 (tr [:route-list-page :header-route-list])]]
      [:div.col-xs-12.col-sm-6.col-md-3

       [ui/raised-button {:label    (tr [:buttons :add-new-route])
                          :style    {:float "right"}
                          :on-click #(do
                                       (.preventDefault %)
                                       (e! (route-list/->CreateNewRoute)))
                          :primary  true
                          :icon     (ic/content-add)}]]]

     [:div.row
      (when (:route-list app)
        (.log js/console " route list pitäis löytyä")
        [list-operators e! (:route-list app)]
        )

      ]]))