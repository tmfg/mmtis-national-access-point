(ns ote.views.admin.sea-routes
  "Admin panel views. Note this has a limited set of users and is not
  currently localized, all UI text is in Finnish."
  (:require [cljs-react-material-ui.reagent :as ui]
            [ote.app.controller.admin :as admin-controller]
            [clojure.string :as str]
            [ote.localization :refer [tr tr-key]]
            [ote.ui.common :refer [linkify]]
            [ote.localization :refer [selected-language]]
            [ote.app.controller.front-page :as fp]
            [ote.ui.form-fields :as form-fields]
            [ote.db.transport-service :as t-service]
            [ote.db.transit :as transit]
            [ote.time :as time]
            [cljs-time.core :as t]))

(defn sea-routes-page-controls [e! app]
  [:div.row {:style {:padding-top "20px"}}
   [form-fields/field {:update! #(e! (admin-controller/->UpdateSeaRouteFilters %))
                       :on-enter #(e! (admin-controller/->SearchSeaRoutes))
                       :name :operator-name
                       :label "Palveluntuottaja"
                       :type :string
                       :hint-text "Palveluntuottajan nimi tai sen osa"
                       :container-class "col-xs-12 col-sm-4 col-md-4"}
    (get-in app [:admin :sea-routes :filters])]

   [ui/raised-button {:primary true
                      :disabled (str/blank? filter)
                      :on-click #(e! (admin-controller/->SearchSeaRoutes))
                      :label "Hae merireitit"}]])

(defn weekday-index-to-keyword [weekday-index]
  (let [weekday-index (if (< weekday-index 0)
                        (+ 7 weekday-index)                 ; works only if weekday-index is in range (6) -  (-7), but it is enough for this purpose
                        weekday-index)]
    (case weekday-index
      0 ::transit/sunday
      1 ::transit/monday
      2 ::transit/tuesday
      3 ::transit/wednesday
      4 ::transit/thursday
      5 ::transit/friday
      6 ::transit/saturday)))

(defn- last-date-in-use
  "Admin needs to know which is last date when route has action. to-date is not always the case bacause there won't be
  activity every weekday. So we need to calculate what is the last day when route has action."
  [{::transit/keys [to-date weekday] :as route}]
  (let [last-weekday-count (loop [index 0
                                  last-weekday-index weekday]
                             (if (>= 7 index)
                               (if (true? ((weekday-index-to-keyword last-weekday-index) route))
                                 (* -1 index)
                                 (recur (inc index) (dec last-weekday-index)))
                               -7))
        new-date (t/plus (time/native->date-time to-date) (t/days last-weekday-count))]
    (time/native->date new-date)))

(defn sea-routes [e! app]
  (let [{:keys [loading? results filters]}
        (get-in app [:admin :sea-routes])
        loc (.-location js/document)]
    [:div.row {:style {:padding-top "40px"}}
     (when loading?
       [:span "Ladataan merireittejä..."])

     (when results
       [:div
        [:div "Hakuehdoilla löytyi " (count results) " merireittiä."]
        [ui/table {:selectable false}
         [ui/table-header {:adjust-for-checkbox false
                           :display-select-all false}
          [ui/table-row
           [ui/table-header-column {:style {:width "25%"}} "Palveluntuottaja"]
           [ui/table-header-column {:style {:width "25%"}} "Merireitti"]
           [ui/table-header-column {:style {:width "10%"}} "Julkaistu?"]
           [ui/table-header-column {:style {:width "15%"}} "Luotu"]
           [ui/table-header-column {:style {:width "15%"}} "Viim. Ajopäivä"]
           [ui/table-header-column {:style {:width "10%"}} "GTFS paketti"]]]
         [ui/table-body {:display-row-checkbox false}
          (doall
            (for [{::transit/keys [id name operator-name operator-id published? created] :as sea-route} results]
              ^{:key (str "link_" sea-route)}
              [ui/table-row {:selectable false}
               [ui/table-row-column {:style {:width "25%"}} operator-name]
               [ui/table-row-column {:style {:width "25%"}} [:a {:href (str "/#/edit-route/" id)
                                                                 :on-click #(do
                                                                              (.preventDefault %)
                                                                              (e! (admin-controller/->ChangeRedirectTo :admin))
                                                                              (e! (fp/->ChangePage :edit-route {:id id})))}
                                                             (t-service/localized-text-with-fallback @selected-language name)]]
               [ui/table-row-column {:style {:width "10%"}} (if published? "Kyllä" "Ei")]
               [ui/table-row-column {:style {:width "15%"}} (time/format-timestamp-for-ui created)]
               [ui/table-row-column {:style {:width "15%"}} (time/format-date (last-date-in-use sea-route))]
               [ui/table-row-column {:style {:width "10%"}}
                [:a {:href (str (.-protocol loc) "//" (.-host loc) (.-pathname loc)
                                "export/gtfs/" operator-id)}
                 "Lataa gtfs"]]]))]]])]))
