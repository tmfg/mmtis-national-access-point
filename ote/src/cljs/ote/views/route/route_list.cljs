(ns ote.views.route.route-list
  "List own routes"
  (:require
    [reagent.core :as r]
    [ote.localization :refer [tr tr-key]]
    [ote.localization :refer [selected-language]]
    [cljs-react-material-ui.reagent :as ui]
    [cljs-react-material-ui.icons :as ic]
    [ote.time :as time]
    [ote.db.transport-operator :as t-operator]
    [ote.db.transit :as transit]
    [ote.db.modification :as modification]
    [ote.db.transport-service :as t-service]
    [ote.util.transport-operator-util :as op-util]

    [ote.app.controller.front-page :as fp]
    [ote.app.controller.route.route-list :as route-list]
    [ote.app.controller.transport-operator :as to]
    [ote.ui.common :as common]
    [ote.ui.form-fields :as form-fields]
    [ote.ui.buttons :as buttons]
    [ote.ui.page :as page]

    [stylefy.core :as stylefy]
    [ote.style.dialog :as style-dialog]
    [ote.style.buttons :as style-buttons]
    [ote.style.base :as style-base]
    [ote.theme.colors :as colors]))

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

(defn- route-table [e! routes empty-row-text]
  [ui/table {:style style-base/basic-table}
   [ui/table-header {:adjust-for-checkbox false
                     :display-select-all false}
    [ui/table-row {:selectable false}
     [ui/table-header-column {:class "table-header" :style {:width "18%"}} (tr [:route-list-page :route-list-table-name])]
     [ui/table-header-column {:class "table-header hidden-xs hidden-sm " :style {:width "15%"}} (tr [:route-list-page :route-list-table-modified])]
     [ui/table-header-column {:class "table-header hidden-xs hidden-sm " :style {:width "15%"}} (tr [:route-list-page :route-list-table-created])]
     [ui/table-header-column {:class "table-header hidden-xs hidden-sm " :style {:width "11%"}} (tr [:route-list-page :route-list-table-actions])]]]
   [ui/table-body {:display-row-checkbox false}
    (if empty-row-text
      (doall
        [ui/table-row {:key (str empty-row-text) :selectable false :display-border false}
         [ui/table-row-column {:style {:width "100%"}} empty-row-text]
         [ui/table-row-column ""]
         [ui/table-row-column ""]
         [ui/table-row-column ""]]
        )
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
             [ui/table-row-column {:class "hidden-xs hidden-sm " :style {:width "15%"}} (time/format-timestamp-for-ui modified)]
             [ui/table-row-column {:class "hidden-xs hidden-sm " :style {:width "15%"}} (time/format-timestamp-for-ui created)]
             [ui/table-row-column {:class "hidden-xs hidden-sm " :style {:width "13%"}}
              [ui/icon-button {:href "#"
                               :on-click #(do
                                            (.preventDefault %)
                                            (e! (fp/->ChangePage :edit-route {:id id})))}
               [ic/content-create]]
              [delete-route-action e! row]]])
          routes)))]])

(defn service-linked-to-route [service services-with-route]
  (let [my-list (some
                  (fn [service-with-route]
                    (= (:id service-with-route)
                       (::t-service/id service)))
                  services-with-route)]
    (if my-list true false)))

(defn- link-services-to-routes [e! services services-with-route has-public-routes]
  (if has-public-routes
    [:div
     [:h4 "Valmiiden reittien liittäminen palveluun"]
     [:p "Valitse alapuolelta palvelut, joihin haluat liittää valmiit merenkulun reitit."]

     (when (empty? services-with-route)
       [:div
        [:span (stylefy/use-style style-base/icon-with-text)
         [ic/alert-warning {:style {:color colors/negative-button
                                    :margin-right "0.5rem"
                                    :margin-bottom "5px"}}]
         [:p "Et ole liittänyt valmiita merenkulun reittejä vielä yhteenkään palveluun."]]])

     (doall
       (for [{::t-service/keys [id name] :as s} services]
         ^{:key (str "link-service-id " id)}
         [:div
          [ui/checkbox {:label name
                        :id (str "checkbox " id)
                        :checked (service-linked-to-route s services-with-route)
                        :on-click #(e! (route-list/->ToggleLinkInterfaceToService
                                         id
                                         (service-linked-to-route s services-with-route)))}]]))]
    [:div
     [:h4 "Rajapinnan liittäminen palveluun"]
     [:p "Rajapinnan voi liittää yhteen tai useampaan palveluun vasta sitten, kun olet luonut yhden valmiin reitin."]]))

(defn list-routes [e! public-routes draft-routes]
  [:div {:style {:padding-top "20px"}}
   [:h4 "Reittiluonnokset"]
   [:p "Taulukossa on listattuna "
    [:strong "luonnostilassa"]
    " olevat reitit. Nämä reitit eivät sisälly koneluettavaan merenkulun reitti- ja aikataulurajapintaan."]
   [route-table e! draft-routes (if (not (empty? draft-routes)) nil "Ei reittiluonnoksia.")]
   [:h4 "Valmiit reitit"]
   [:p "Taulukossa on listattuna "
    [:strong "valmiit"]
    " olevat reitit. Nämä reitit sisältyvät koneluettavaan merenkulun reitti- ja aikataulurajapintaan."]
   [route-table e! public-routes (if (not (empty? public-routes)) nil "Ei valmiita reittejä.")]])



(defn routes [e! {operator :transport-operator
                  operators :transport-operators-with-services
                  :as app}]
  (let [routes (get-in app [:routes :routes-vector])
        services-with-route (get-in app [:routes :route-used-in-services])
        public-routes (filter (fn [r]
                                (true? (::transit/published? r)))
                              routes)
        draft-routes (filter (fn [r]
                               (false? (::transit/published? r)))
                             routes)
        operator-services (to/current-operator-services app)
        schedule-services (filter
                            (fn [{::t-service/keys [sub-type] :as s}]
                              (or (= :schedule sub-type) false))
                            operator-services)]
    [:div
     [page/page-controls "" (tr [:route-list-page :header-route-list])
      [:div
       [:h4 {:style {:margin "0"}}
        (tr [:field-labels :select-transport-operator])]
       [form-fields/field
        {:element-id "select-operator-at-searoute-frontpage"
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
      [list-routes e! public-routes draft-routes]
      (when schedule-services
        [link-services-to-routes e! schedule-services services-with-route (not (empty? public-routes))])
      (if (not (empty? public-routes))
        (let [loc (.-location js/document)
              url (str (.-protocol loc) "//" (.-host loc) (.-pathname loc)
                       "export/gtfs/" (::t-operator/id operator))]
          [:div
           [:h4 "Merenkulun reitti- ja aikataulurajapinta"]
           [:p "NAP-palvelu luo ja päivittää merenkulun reitti- ja aikataulurajapintaa automaattisesti. Rajapinta sisältää "
            [:strong "valmiit reitit"]
            " -taulukossa listattujen reittien tiedot. Mikäli haluat käyttää merenkulun rajapintaa myös muualla kuin
            NAP-palvelussa, voit ladata ajantaisaisen rajapintatiedoston alla olevasta linkistä."]
           [common/linkify url (op-util/gtfs-file-name operator)]])
        [:div
         [:h4 "Rajapinnan lataaminen"]
         [:p "Rajapinta muodostetaan ja on ladattavissa vasta sitten, kun olet luonut yhden valmiin reitin."]])]]))
