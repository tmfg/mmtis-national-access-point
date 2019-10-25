(ns ote.views.admin.sea-routes
  "Admin panel views. Note this has a limited set of users and is not
  currently localized, all UI text is in Finnish."
  (:require [cljs-react-material-ui.reagent :as ui]
            [ote.app.controller.admin :as admin-controller]
            [clojure.string :as str]
            [ote.localization :refer [selected-language]]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]
            [ote.app.controller.front-page :as fp]
            [ote.ui.form-fields :as form-fields]
            [ote.db.transport-service :as t-service]
            [ote.db.transit :as transit]
            [ote.ui.circular_progress :as circular]
            [ote.ui.buttons :as buttons]
            [ote.time :as time]
            [cljs-time.core :as t]))

(defn- edit-sea-route-link [e! route-id route-name]
  [:a {:href (str "/#/edit-route/" route-id)
       :on-click #(do
                    (.preventDefault %)
                    (e! (admin-controller/->ChangeRedirectTo :admin))
                    (e! (fp/->ChangePage :edit-route {:id route-id})))}
   (t-service/localized-text-with-fallback @selected-language route-name)])

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

   [buttons/save
    {:on-click #(e! (admin-controller/->SearchSeaRoutes))
     :disabled (str/blank? filter)
     :style {:margin-left "1rem"}}
    "Hae merireitit"]])

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
      6 ::transit/saturday
      ::transit/sunday)))

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
                               ;; return 0 days if only to-date and weekday are given and selected weekdays are missing
                               ;; because there is no need to count backwards the days when exact date is given (to-date without weekday)
                               (when (and to-date weekday)
                                 0)))]
    (when-not (nil? last-weekday-count)
      (time/native->date
        (t/plus (time/native->date-time to-date) (t/days last-weekday-count))))))

(defn sea-routes [e! app]
  (let [{:keys [loading? results]}
        (get-in app [:admin :sea-routes])
        loc (.-location js/document)]
    [:div.row {:style {:padding-top "40px"}}
     (when loading?
       [circular/circular-progress
        [:span "Ladataan merireittejä..."]])

     (when results
       [:div
        [:div "Hakuehdoilla löytyi " (count results) " merireittiä."]
        [ui/table {:selectable false}
         [ui/table-header {:class "table-header-wrap"
                           :adjust-for-checkbox false
                           :display-select-all false}
          [ui/table-row
           [ui/table-header-column
            {:class "table-header-wrap" :style {:width "25%"}}
            "Palveluntuottaja"]
           [ui/table-header-column
            {:class "table-header-wrap" :style {:width "25%"}}
            "Merireitti"]
           [ui/table-header-column
            {:class "table-header-wrap" :style {:width "10%"}}
            "Julkaistu?"]
           [ui/table-header-column
            {:class "table-header-wrap" :style {:width "15%"}}
            "Viim. Muokkaus"]
           [ui/table-header-column
            {:class "table-header-wrap" :style {:width "15%"}}
            "Viim. Ajopäivä"]
           [ui/table-header-column
            {:class "table-header-wrap" :style {:width "10%"}}
            "GTFS paketti"]]]
         [ui/table-body {:display-row-checkbox false}
          (doall
            (for [{::transit/keys [id name operator-name operator-id published? modified created] :as sea-route} results
                  :let [last-active-date (last-date-in-use sea-route)
                        last-modification (max created modified)]]
              ^{:key (str "link_" sea-route)}
              [ui/table-row {:selectable false}
               [ui/table-row-column
                (merge (stylefy/use-style style-base/table-col-style-wrap)
                       {:width "25%"})
                operator-name]
               [ui/table-row-column
                (merge (stylefy/use-style style-base/table-col-style-wrap)
                       {:width "25%"})
                [edit-sea-route-link e! id name]]
               [ui/table-row-column
                (merge (stylefy/use-style style-base/table-col-style-wrap)
                       {:width "10%"})
                (if published? "Kyllä" "Ei")]
               [ui/table-row-column
                (merge (stylefy/use-style style-base/table-col-style-wrap)
                       {:width "15%"})
                (time/format-timestamp-for-ui last-modification)]
               [ui/table-row-column
                (merge (stylefy/use-style style-base/table-col-style-wrap)
                       {:width "15%"})
                (if (not (nil? last-active-date))
                  (time/format-date last-active-date)
                  "Ajopäivissä virhe")]
               [ui/table-row-column
                (merge (stylefy/use-style style-base/table-col-style-wrap)
                       {:width "10%"})
                [:a {:href (str (.-protocol loc) "//" (.-host loc) (.-pathname loc)
                                "export/gtfs/" operator-id)}
                 "Lataa gtfs"]]]))]]])]))
