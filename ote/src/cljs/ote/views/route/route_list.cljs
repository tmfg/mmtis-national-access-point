(ns ote.views.route.route-list
  "List own routes"
  (:require
    [ote.localization :refer [tr tr-key]]
    [cljs-react-material-ui.reagent :as ui]
    [ote.app.controller.route.route-list :as route-list]
    [cljs-react-material-ui.icons :as ic]
    [ote.app.controller.transport-operator :as to]
    [ote.db.transport-operator :as t-operator]
    [ote.ui.form-fields :as form-fields]
    [ote.db.transit :as transit]
    [ote.db.modification :as modification]
    [ote.time :as time]
    [ote.app.controller.front-page :as fp]
    [ote.ui.common :as common]
    [ote.db.transport-service :as t-service]
    [ote.localization :refer [selected-language]]
    [reagent.core :as r]
    [ote.ui.buttons :as buttons]
    [ote.style.dialog :as style-dialog]
    [ote.ui.page :as page]
    [ote.style.buttons :as style-buttons]
    [stylefy.core :as stylefy]
    [ote.style.base :as style-base]))

(defn- delete-route-action [e! {::transit/keys [id name]
                                :keys [show-delete-modal?]
                                :as route}]
  [:span
   [ui/icon-button {:id (str "delete-route-" id)
                    :href "#"
                    :on-click #(do
                                 (.preventDefault %)
                                 (e! (route-list/->OpenDeleteRouteModal id)))}
    [ic/action-delete]]
   (when show-delete-modal?
     [ui/dialog
      {:open true
       :actionsContainerStyle style-dialog/dialog-action-container
       :title (tr [:route-list-page :delete-dialog-header])
       :actions [(r/as-element
                   [buttons/cancel
                    {:on-click #(do
                                  (.preventDefault %)
                                  (e! (route-list/->CancelDeleteRoute id)))}
                    (tr [:buttons :cancel])])
                 (r/as-element
                   [buttons/delete
                    {:on-click #(do
                                  (.preventDefault %)
                                  (e! (route-list/->ConfirmDeleteRoute id)))}
                    (tr [:buttons :delete])])]}

      (str (tr [:route-list-page :delete-dialog-remove-route]) (t-service/localized-text-with-fallback @selected-language name))])])


(defn- route-table [e! routes]
  (.log js/console "route-table routes " (pr-str routes))
  [ui/table (stylefy/use-style style-base/basic-table)
   [ui/table-header {:adjust-for-checkbox false
                     :display-select-all false}
    [ui/table-row {:selectable false}
     [ui/table-header-column {:class "table-header" :style {:width "18%"}} (tr [:route-list-page :route-list-table-name])]
     [ui/table-header-column {:class "table-header" :style {:width "10%"}} (tr [:route-list-page :route-list-table-valid-from])]
     [ui/table-header-column {:class "table-header" :style {:width "10%"}} (tr [:route-list-page :route-list-table-valid-to])]
     [ui/table-header-column {:class "table-header hidden-xs hidden-sm " :style {:width "15%"}} (tr [:route-list-page :route-list-table-created-modified])]
     [ui/table-header-column {:class "table-header hidden-xs hidden-sm " :style {:width "11%"}} (tr [:route-list-page :route-list-table-actions])]]]
   [ui/table-body {:display-row-checkbox false}
    (doall
      (map-indexed
        (fn [i {::transit/keys [id name published? available-from available-to
                                departure-point-name destination-point-name]
                ::modification/keys [created modified] :as row}]
          ^{:key (str "route-" i)}
          [ui/table-row {:key (str "route-" i) :selectable false :display-border false}
           [ui/table-row-column {:style {:width "18%"}}
            [:a {:href "#"
                 :on-click #(do
                              (.preventDefault %)
                              (e! (fp/->ChangePage :edit-route {:id id})))} (t-service/localized-text-with-fallback @selected-language name)]]
           [ui/table-row-column {:style {:width "10%"}} (when available-from (time/format-date available-from))]
           [ui/table-row-column {:style {:width "10%"}} (when available-to (time/format-date available-to))]
           [ui/table-row-column {:class "hidden-xs hidden-sm " :style {:width "15%"}} (time/format-timestamp-for-ui (or modified created))]
           [ui/table-row-column {:class "hidden-xs hidden-sm " :style {:width "13%"}}
            [ui/icon-button {:href "#"
                             :on-click #(do
                                          (.preventDefault %)
                                          (e! (fp/->ChangePage :edit-route {:id id})))}
             [ic/content-create]]
            [delete-route-action e! row]]])
        routes))]])

(defn list-routes [e! routes]
  (let [public-routes (filter (fn [r]
                                (true? (::transit/published? r))) routes)
        draft-routes (filter (fn [r]
                               (false? (::transit/published? r))) routes)
        _ (.log js/console "public-routes " (pr-str public-routes))
        _ (.log js/console "routes " (pr-str routes))]
  [:div {:style {:padding-top "20px"}}
   [:h4 "Reittiluonnokset"]
   [:p "Taulukossa on listattuna "
    [:strong "luonnostilassa"]
    " olevat reitit. Nämä reitit eivät sisälly koneluettavaan merenkulun reitti- ja aikataulurajapintaan."]
   [route-table e! draft-routes]
   [:h4 "Valmiit reitit"]
   [:p "Taulukossa on listattuna "
    [:strong "valmiit"]
    " olevat reitit. Nämä reitit sisältyvät koneluettavaan merenkulun reitti- ja aikataulurajapintaan."]
   [route-table e! public-routes]]))

(defn routes [e! {operators :transport-operators-with-services :as app}]
  (e! (route-list/->LoadRoutes))
  (fn [e! {routes :routes-vector operator :transport-operator :as app}]
    [:div
     [page/page-controls "" (tr [:route-list-page :header-route-list])
      [:div
       [:h4 {:style {:margin "0"}}
        (tr [:field-labels :select-transport-operator])]
       [form-fields/field
        {:element-id "select-operator-at-own-services"
         :name :select-transport-operator
         :type :selection
         :show-option #(::t-operator/name %)
         :update! #(e! (to/->SelectOperator %))
         :options (mapv to/take-operator-api-keys (mapv :transport-operator operators))
         :auto-width? true
         :class-name "mui-select-button"}
        (to/take-operator-api-keys operator)]]]

     [:div.container
      [:h2 (get-in app [:transport-operator :ote.db.transport-operator/name])]
      [:a (merge {:href (str "#/new-route/")
                  :id "new-route-button"
                  :on-click #(do
                               (.preventDefault %)
                               (e! (route-list/->CreateNewRoute)))}
                 (stylefy/use-style style-buttons/primary-button))
       (tr [:buttons :add-new-route])]
      (when routes
        [list-routes e! routes])
      (when routes
        (let [loc (.-location js/document)
              url (str (.-protocol loc) "//" (.-host loc) (.-pathname loc)
                       "export/gtfs/" (::t-operator/id operator))]
          [:span
           [:br]
           [common/help
            [:span
             [:div (tr [:route-list-page :route-list-active-routes])
              [common/linkify url (tr [:route-list-page :route-list-gtfs-zip-file])]]
             [:div {:style {:width "100%"}}
              (tr [:route-list-page :route-list-copy-link])
              [common/copy-to-clipboard url]]]]]))]]))
