(ns ote.views.transit-visualization
  "Visualization of transit data (GTFS)."
  (:require [reagent.core :as r]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.icons :as ote-icons]
            [stylefy.core :as stylefy]
            [ote.style.transit-changes :as style]
            [ote.style.base :as style-base]
            [tuck.core :as tuck]
            [ote.ui.service-calendar :as service-calendar]
            [ote.app.controller.transit-visualization :as tv]
            [taoensso.timbre :as log]
            [ote.time :as time]
            [cljs-react-material-ui.reagent :as ui]
            [ote.ui.table :as table]
            [ote.db.transport-service :as t-service]
            [ote.localization :refer [tr]]
            [ote.ui.leaflet :as leaflet]
            [clojure.string :as str]
            [ote.ui.page :as page]
            [ote.ui.scroll :as scroll]
            [ote.ui.common :as common]
            [ote.ui.form-fields :as form-fields]))

(set! *warn-on-infer* true)

(defn labeled-icon
  ([icon label]
   [labeled-icon {} icon label])
  ([wrapper-attrs icon label]
   [:div wrapper-attrs
    icon
    [:div (stylefy/use-style style/change-icon-value)
     label]]))

(defn highlight-style [hash->color date->hash day highlight]
  (let [d (time/format-date day)
        d-hash (date->hash d)
        hash-color (hash->color (date->hash d))
        hover-day (:day highlight)
        hover-day-formatted (time/format-date hover-day)
        hover-hash (:hash highlight)
        mode (:mode highlight)]

    (when hash-color
      (case mode
        :same (if (and (= d-hash hover-hash) (not= d hover-day-formatted))
                {:box-shadow "inset 0 0 0 2px black, inset 0 0 0 100px rgba(255,255,255,.5)"}
                (if (= d hover-day-formatted)
                  {:box-shadow "inset 0 0 0 2px black, inset 0 0 0 100px rgba(255,255,255,.75)"}
                  {:box-shadow "inset 0 0 0 2px transparent, inset 0 0 0 100px rgba(0,0,0,.25)"}))
        :diff (if (and (not= d-hash hover-hash)
                       (= (time/day-of-week day) (time/day-of-week hover-day)))
                {:box-shadow "inset 0 0 0 2px crimson, inset 0 0 0 3px black, inset 0 0 0 100px rgba(255,255,255,.65)"}
                (if (and (= d-hash hover-hash) (= d hover-day-formatted))
                  {:box-shadow "inset 0 0 0 2px black,
                                inset 0 0 0 3px transparent,
                                inset 0 0 0 100px rgba(255,255,255,.75)"}
                  {:box-shadow "inset 0 0 0 2px transparent,
                                inset 0 0 0 3px transparent,
                                inset 0 0 0 100px rgba(0,0,0,.25)"}))))))

(defn day-style [hash->color date->hash date1 date2 day]
  (let [prev-week-date (time/format-date-iso-8601 (time/days-from day -7))
        next-week-date (time/format-date-iso-8601 (time/days-from day 7))
        prev-week-hash (date->hash prev-week-date)
        next-week-hash (date->hash next-week-date)

        d (time/format-date-iso-8601 day)
        hash (date->hash d)
        hash-color (hash->color hash)]
    (merge
     {:font-size "0.75rem"
      :background-color hash-color
      :color "rgb (0, 255, 255)"
      :transition "box-shadow 0.25s"
      :box-shadow "inset 0 0 0 2px transparent, inset 0 0 0 3px transparent, inset 0 0 0 100px transparent"}
     (when (and prev-week-hash hash (not= hash prev-week-hash) (> day (time/now)))
       {:box-shadow "inset 0 0 0 1px black,
                     inset 0 0 0 2px transparent"})
     (cond (= (time/format-date-iso-8601 date1) d)
           (style/date1-highlight-style hash-color)

           (= (time/format-date-iso-8601 date2) d)
           (style/date2-highlight-style hash-color)))))

(defn hover-day [e! date->hash day]
  (e! (tv/->HighlightHash (date->hash (time/format-date day)) day))
  (e! (tv/->DaysToFirstDiff (time/format-date day) date->hash)))

(defn select-day [e! day]
  (e! (tv/->SelectDateForComparison day)))

(defn day-of-week-short [dt]
  (tr [:enums ::t-service/day :short (case (time/day-of-week dt)
                                       :monday :MON
                                       :tuesday :TUE
                                       :wednesday :WED
                                       :thursday :THU
                                       :friday :FRI
                                       :saturday :SAT
                                       :sunday :SUN)]))

(defn- initialize-route-features
  "Bind popup content and set marker icon for stop marker features."
  [offset [w h]]
  (fn [feature ^js/L.Layer layer]
    (let [stop-name (aget feature "properties" "stopname")
          trip-name (aget feature "properties" "trip-name")
          popup-html (str "Pysäkki: " (first (str/split stop-name #"\|\|")))]
      (if stop-name
      ;; This features is a stop marker
      (do
        ;; Add trip-name for every stop marker to find them when the trip whom they belong to is hidden.
        (aset (aget layer "feature" "properties") "trip-name" trip-name)
        ;; Add icon for every stop marker
        (aset (aget layer "options") "icon"
              (js/L.icon #js {:iconUrl (str js/document.location.protocol "//" js/document.location.host "/img/stop_map_marker.svg")
                               :iconSize #js [w h]
                               :iconAnchor #js [(int (/ w 2)) (int (/ h 2))]}))
        ;; Add popup
        (.bindPopup layer popup-html))
      ;; This feature has no name, it is the route line, apply pixel offset
      (.call (aget layer "setOffset") layer offset)))))

(defn update-marker-visibility [this show-atom removed-route-layers]
  (let [^js/L.map m (aget this "refs" "leaflet" "leafletElement")
        show @show-atom]

    ;; add layers that should be shown
    (doseq [[name layers] @removed-route-layers
            :when (show name)]
      (doseq [layer layers]
        (.addLayer m layer)))

    ;; Remove from atom the layers that were just added
    (swap! removed-route-layers
           #(reduce (fn [layers [name show?]]
                      (if show?
                        (dissoc layers name)
                        layers))
                    % show))

    (.eachLayer m (fn [layer]
                    (if-let [icon (aget layer "_icon")]
                      ;; This is a stop, set the icon visibility
                      (set! (.-visibility (aget icon "style"))
                              (if (and
                                    (:stops show)
                                    (not (contains? @removed-route-layers (aget layer "feature" "properties" "trip-name")))
                                    (show (some-> layer (aget "feature") (aget "properties") (aget "trip-name"))))
                                ""
                                "hidden")))

                      (when-let [routename (some-> layer (aget "feature") (aget "properties") (aget "routename"))]
                        (when-not (show routename)
                          ;; This is a layer for a routeline that should be removed
                          (swap! removed-route-layers update routename conj layer)))))

    ;; Remove layers that were added to removed-route-layers
    (doseq [[_ layers] @removed-route-layers]
      (doseq [layer layers]
        (.removeLayer m layer)))))



(defn stop-listing [stops]
  [:table
   [:thead
    [:tr [:th {:width "75%"} "Pysäkki"] [:th {:width "25%"} "Lähtöaika"]]]
   [:tbody
    (map-indexed
     (fn [i stoptime]
       (let [[stop time] (str/split stoptime #"@")]
         ^{:key i}
         [:tr
          [:td stop]
          [:td time]]))
     (str/split stops #"->"))]])

(defn short-trip-description [{:keys [trip-headsign stops]}]
  (let [stops (mapv #(zipmap [:stop-name :time] (str/split % #"@"))
                    (str/split stops #"->"))]
    {:departure (first stops)
     :destination (last stops)
     :stops (count stops)
     :headsign trip-headsign}))

(defn date-trips [e! {:keys [date1 date1-trips date2 date2-trips
                             route-short-name route-long-name trip-headsign
                             selected-trip-description]}]
  (let [date1-trip-descriptions (into #{} (map short-trip-description) date1-trips)
        date2-trip-descriptions (into #{} (map short-trip-description) date2-trips)
        all-trip-descriptions (into #{} (concat date1-trip-descriptions
                                                date2-trip-descriptions))]
    [:div
     [:span "Vuorot reitille " [:b route-short-name " " route-long-name]
      (when trip-headsign
        [:span " otsatunnuksella " [:b trip-headsign]])
      " valittuina päivinä:"]
     [table/table {:height 300 :name->label str
                   :on-select #(e! (tv/->SelectTripDescription (first %)))
                   :row-selected? (comp boolean #{selected-trip-description})}
      [{:name "Lähtö" :width "35%" :format #(str (:time %) " " (:stop-name %)) :read :departure}
       {:name "Määränpää" :width "35%" :format #(str (:time %) " " (:stop-name %)) :read :destination}
       {:name "Pysäkkejä" :width "12%" :read :stops}
       {:name date1 :width "9%" :format #(if % "\u2713" "-") :read (comp boolean date1-trip-descriptions)}
       {:name date2 :width "9%" :format #(if % "\u2713" "-") :read (comp boolean date2-trip-descriptions)}]
      (sort-by (juxt (comp :time :departure) (comp :stop-name :departure))
               all-trip-descriptions)]

     (when selected-trip-description
       (let [matching-trip #(when (= selected-trip-description (short-trip-description %)) %)
             date1-trip (some matching-trip date1-trips)
             date2-trip (some matching-trip date2-trips)]
         [:div
          [:span "Valitun vuoron pysäkkilistaus: "]
          [:table {:style {:width "100%"}}
           [:thead
            [:tr
             [:th {:width "50%"} date1] [:th {:width "50%"} date2]]]
           [:tbody
            [:tr
             [:td {:style {:vertical-align "top"}}
              [stop-listing (:stops date1-trip)]]
             [:td {:style {:vertical-align "top"}}
              [stop-listing (:stops date2-trip)]]]]]]))]))

(defn date-comparison [e! {:keys [date->hash hash->color compare]}]
  (let [date1 (:date1 compare)
        date2 (:date2 compare)
        dow1 (day-of-week-short (time/parse-date-eu date1))
        dow2 (day-of-week-short (time/parse-date-eu date2))]
    (when (and date1 date2)
      [:div.transit-visualization-compare
       "Vertaillaan päiviä: " [:b dow1 " " date1] " ja " [:b dow2 " " date2]
       (when (= (date->hash date1) (date->hash date2))
         [:div "Päivien liikenne on samanlaista"])

       ;; List routes and trip counts for seleted dates
       #_[route-listing e! compare]

       ;; If a route is selected, show map
       #_[selected-route-map e! date->hash hash->color compare]

       ;; Show trip list
       [date-trips e! compare]])))


(defn days-to-diff-info [e! {:keys [days-to-diff]} highlight]
  (let [hovered-date (:day highlight)
        days (:days days-to-diff)
        diff-date (:date days-to-diff)]
    (when (and hovered-date days (:hash highlight))
      [:div {:style {:position "fixed"
                     :top "80px"
                     :left "50px"
                     :min-height "50px"
                     :width "250px"
                     :border "solid black 1px"
                     :padding "5px"
                     :background-color "#fff"
                     :z-index 9}}
       [:div [:b (str days " päivää")]
        [:div "ensimmäiseen muutokseen"]]
       [:div
        [:div (str "päivänä: " (time/format-date diff-date) " (" (day-of-week-short diff-date) ")")]
        [:div (str "alkaen: " (time/format-date hovered-date) " (" (day-of-week-short hovered-date) ")")]
        ]])))

(defn highlight-mode-switch [e! highlight]
  [:div
   [ui/radio-button-group {:name "select-highlight-mode"
                           :on-change #(e! (tv/->SetHighlightMode (keyword %2)))
                           :value-selected (:mode highlight)
                           :style {:display "flex" :justify-content "flex-start" :flex-direction "row wrap"}}
    [ui/radio-button {:label "Ei korostusta"
                      :value nil
                      :style {:white-space "nowrap"
                              :width "auto"
                              :margin-right "20px"
                              :font-size "12px"
                              :font-weight "bold"}}]
    [ui/radio-button {:label "Korosta samanlaiset"
                      :value :same
                      :style {:white-space "nowrap"
                              :width "auto"
                              :margin-right "20px"
                              :font-size "12px"
                              :font-weight "bold"}}]
    [ui/radio-button {:label "Korosta poikkeukset"
                      :value :diff
                      :style {:white-space "nowrap"
                              :width "auto"
                              :font-size "12px"
                              :font-weight "bold"}}]]])

(defn calendar-style-switch [e! transit-visualization]
  [:div
   [ui/radio-button-group {:name "select-highlight-mode"
                           :on-change #(e! (tv/->SetCalendarMode (keyword %2)))
                           :value-selected (:calendar-mode transit-visualization)
                           :style {:display "flex" :justify-content "flex-start" :flex-direction "row wrap"}}
    [ui/radio-button {:label "Tiivis näkymä"
                      :value :compact
                      :style {:white-space "nowrap"
                              :width "auto"
                              :margin-right "20px"
                              :font-size "12px"
                              :font-weight "bold"}}]
    [ui/radio-button {:label "Viikkonäkymä"
                      :value :weeks
                      :style {:white-space "nowrap"
                              :width "auto"
                              :margin-right "20px"
                              :font-size "12px"
                              :font-weight "bold"}}]
    [ui/radio-button {:label "Aikajana"
                      :value :timeline
                      :style {:white-space "nowrap"
                              :width "auto"
                              :font-size "12px"
                              :font-weight "bold"}}]]])

(defn route-changes-legend []
  [:div.transit-changes-legend (stylefy/use-style style/transit-changes-legend)
   [:div [:b "Taulukon ikonien selitteet"]]
   (doall
    (for [[icon label] [[ote-icons/outline-add-box " Uusia vuoroja"]
                        [ote-icons/outline-indeterminate-checkbox " Poistuvia vuoroja"]
                        [ic/action-timeline " Pysäkkimuutoksia per vuoro"]
                        [ic/action-query-builder " Aikataulumuutoksia per vuoro"]]]
      ^{:key label}
      [labeled-icon (stylefy/use-style style/transit-changes-legend-icon) [icon] label]))])

(defn format-range [{:keys [lower upper lower-inclusive? upper-inclusive?]}]
  (if (and (nil? lower) (nil? upper))
    "0"
    (let [upper (when upper
                  (if upper-inclusive?
                    upper
                    (dec upper)))]
      (if (= lower upper)
        (str lower)
        (str lower "\u2014" upper)))))

(defn stop-seq-changes-icon [trip-stop-sequence-changes with-labels?]
  (let [seq-changes (if (number? trip-stop-sequence-changes)
                      trip-stop-sequence-changes
                      (format-range trip-stop-sequence-changes))]
    [labeled-icon (stylefy/use-style style/transit-changes-legend-icon)
     [ic/action-timeline {:color (when (= "0" seq-changes)
                                   style/no-change-color)}]
     [:span
      seq-changes
      (when with-labels? " pysäkkimuutosta")]]))

(defn stop-time-changes-icon [trip-stop-time-changes with-labels?]
  (let [time-changes (if (number? trip-stop-time-changes)
                       trip-stop-time-changes
                       (format-range trip-stop-time-changes))]
    [labeled-icon (stylefy/use-style style/transit-changes-legend-icon)
     [ic/action-query-builder {:color (when (= "0" time-changes)
                                        style/no-change-color)}]
     [:span
      time-changes
      (when with-labels? " aikataulumuutosta")]]))

(defn change-icons
  ([diff]
   [change-icons diff false])
  ([{:gtfs/keys [added-trips removed-trips trip-stop-sequence-changes trip-stop-time-changes]}
    with-labels?]
   [:div.transit-change-icons
    (stylefy/use-style (merge
                        (style-base/flex-container "row")
                        {:width "100%"}))
    [:div {:style {:width "20%"}}
     [labeled-icon (stylefy/use-style style/transit-changes-legend-icon)
      [ote-icons/outline-add-box {:color (if (= 0 added-trips)
                                           style/no-change-color
                                           style/add-color)}]
      [:span added-trips
       (when with-labels? " lisättyä vuoroa")]]]
    [:div {:style {:width "20%"}}
     [labeled-icon (stylefy/use-style style/transit-changes-legend-icon)
      [ote-icons/outline-indeterminate-checkbox {:color (if (= 0 removed-trips)
                                                  style/no-change-color
                                                  style/remove-color)}]
      [:span removed-trips
       (when with-labels? " poistettua vuoroa")]]]

    [:div {:style {:width "25%"}}
     [stop-seq-changes-icon trip-stop-sequence-changes with-labels?]]

    [:div {:style {:width "35%"}}
     [stop-time-changes-icon trip-stop-time-changes with-labels?]]]))

(defn section [{:keys [open? toggle!]} title help-content body-content]
  [:div.transit-visualization-section (stylefy/use-style (if open?
                                                           style/section
                                                           style/section-closed))
   [:div.transit-visualization-section-title (merge
                                              (stylefy/use-style style/section-title)
                                              {:on-click toggle!})
    [(if open?
       ic/navigation-expand-less
       ic/navigation-expand-more) {:color "white"
                                   :style {:position "relative"
                                           :top "6px"
                                           :margin-right "0.5rem"}}]
    title]
   (when open?
     [:span
      [:div.transit-visualization-section-header (stylefy/use-style style/section-header)
       help-content]
      [:div.transit-visualization-section-body (stylefy/use-style style/section-body)
       body-content]])])

(defn service-is-using-headsign
  "Routes are combained between gtfs packages using route-hash-id. When trips headsign is not used there is no
  reason to show it in route list or route name."
  [route-hash-id-type]
  (or (nil? route-hash-id-type)
      (= (:gtfs/route-hash-id-type route-hash-id-type) "short-long-headsign")
      (= (:gtfs/route-hash-id-type route-hash-id-type) "long-headsign")))


(defn- route-changes-wrapper [el e!] ;; Wrapper allows hooking to React component lifespan events
  (with-meta el {:component-did-mount (e! (tv/->LoadingRoutesResponse))}))

(defn route-changes [e! route-changes no-change-routes selected-route route-hash-id-type]
  (let [route-count (count route-changes)
        no-change-routes-count (count no-change-routes)
        table-height (str
                       (cond
                         (and (< 0 route-count) (> 10 route-count)) (* 50 route-count) ; 1 - 10
                         (= 0 route-count) 100
                         :else 500)
                       "px"); 10+
        no-rows-message (if (and
                              (= 0 route-count)
                              (pos-int? no-change-routes-count))
                          (tr [:transit-visualization-page :no-changes-in-routes])
                          (tr [:transit-visualization-page :loading-routes]))]
    [:div.route-changes
     [route-changes-legend]
     ;; Wrapper hooks to React event when rendering is done, allowing disabling any UI indication, potentially show during rendering.
     [route-changes-wrapper
      [table/table {:no-rows-message no-rows-message
                    :height table-height
                    :name->label str
                    :show-row-hover? true
                    :on-select #(when (first %)
                                  (do
                                    (e! (tv/->SelectRouteForDisplay (first %)))
                                    (.setTimeout js/window (fn [] (scroll/scroll-to-id "route-calendar-anchor")) 150)))
                    :row-selected? #(= % selected-route)}
       [{:name "Reitti" :width "30%"
         :read (juxt :gtfs/route-short-name :gtfs/route-long-name)
         :format (fn [[short long]]
                   (str short " " long))}
        ;; Show Reitti/Määrämpää column only if it does affect on routes.
        (when (service-is-using-headsign route-hash-id-type)
          {:name "Reitti/määränpää" :width "20%"
           :read :gtfs/trip-headsign})

        {:name "Aikaa 1. muutokseen"
         :width "20%"
         :read :gtfs/different-week-date
         :format (fn [different-week-date]
                   (if-not different-week-date
                     [labeled-icon [ic/navigation-check] "Ei muutoksia"]
                     [:span
                      (str (time/days-until different-week-date) " pv")
                      [:span (stylefy/use-style {:margin-left "5px"
                                                 :color "gray"})
                       (str "(" (time/format-timestamp->date-for-ui different-week-date) ")")]]))}

        {:name "Muutokset" :width "30%"
         :read identity
         :format (fn [{change-type :gtfs/change-type :as route}]
                   (case change-type
                     :no-traffic
                     [labeled-icon
                      [ic/av-not-interested]
                      "Tauko liikennöinnissä"]

                     :added
                     [labeled-icon
                      [ic/content-add-box {:color style/add-color}]
                      "Uusi reitti"]

                     :removed
                     [labeled-icon
                      [ote-icons/outline-indeterminate-checkbox {:color style/remove-color}]
                      "Päättyvä reitti"]

                     :no-change
                     [labeled-icon
                      [ic/navigation-check]
                      "Ei muutoksia"]

                     :changed
                     [change-icons route]))}]

       route-changes] e!]
     [:div {:id "route-calendar-anchor"}]]))

(defn comparison-dates [{:keys [date1 date2]}]
  [:div (stylefy/use-style (merge (style-base/flex-container "row")
                                  {:justify-content "space-between"
                                   :width "20%"}))
   [:div [:div {:style (merge {:display "inline-block"
                               :position "relative"
                               :top "5px"
                               :margin-right "0.5em"
                               :width "20px"
                               :height "20px"}
                              (style/date1-highlight-style))}]
    (time/format-date date1)]
   [:div [:div {:style (merge {:display "inline-block"
                               :position "relative"
                               :top "5px"
                               :margin-right "0.5em"
                               :width "20px"
                               :height "20px"}
                              (style/date2-highlight-style))}]
    (time/format-date date2)]])

(defn comparison-date-changes [{diff :differences :as compare}]
  [:span
   [comparison-dates compare]

   (when (seq diff)
     [change-icons diff true])])

(defn route-calendar [e! {:keys [date->hash hash->color show-previous-year? compare open-sections]}]
  (let [current-year (time/year (time/now))]
    [:div.route-service-calendar
     [section {:toggle! #(e! (tv/->ToggleSection :route-service-calendar))
               :open? (get open-sections :route-service-calendar true)}
      "Kalenteri"
      [:span
       "Valitut päivät on korostettu sinisellä ja lilalla taustavärillä. Oletuksiksi valitut päivämäärät ovat reitin ensimmäinen tunnistettu muutospäivä sekä vastaava viikonpäivä kuluvalta viikolta. Ne kalenteripäivät, joiden pysäkkiketjut ja aikataulut ovat keskenään samanlaiset, on väritetty samalla taustavärillä kokonaisuuden hahmottamiseksi. Taustaväreillä ei ole muita merkityksiä. Kaikki NAP-palvelun havaitsemat päivät, jolloin liikennöinnissä tapahtuu muutoksia suhteessa edellisen viikon vastaavaan viikonpäivään, on merkitty mustilla kehyksillä. Voit myös valita kalenterista itse päivät, joiden reitti- ja aikatauluja haluat vertailla. Valinta tehdään napsauttamalla haluttuja päiviä kalenterista."
       [:div (stylefy/use-style (merge (style-base/flex-container "row")
                                       {:margin-top "1rem"
                                        :margin-bottom "1rem"}))
        [ui/checkbox {:label "Näytä myös edellinen vuosi"
                      :checked show-previous-year?
                      :on-check #(e! (tv/->ToggleShowPreviousYear))}]]]
      [:div.route-service-calendar-content



       [service-calendar/service-calendar {:selected-date? (constantly false)
                                           :on-select #(e! (tv/->SelectDateForComparison %))
                                           :day-style (r/partial day-style hash->color date->hash
                                                                 (:date1 compare) (:date2 compare))
                                           :years (vec (concat (when show-previous-year?
                                                                 [(dec current-year)])
                                                               [current-year]
                                                               [(inc current-year)]))
                                           :hover-style #(let [d (time/format-date-iso-8601 %)
                                                               hash (date->hash d)
                                                               hash-color (hash->color hash)]
                                                           (when-not (or (= (time/format-date-iso-8601 (:date1 compare)) d)
                                                                         (= (time/format-date-iso-8601 (:date2 compare)) d))
                                                             (if (= 2 (get compare :last-selected-date 2))
                                                               (style/date1-highlight-style hash-color
                                                                                            style/date1-highlight-color-hover)
                                                               (style/date2-highlight-style hash-color
                                                                                            style/date2-highlight-color-hover))))}]

       [:h3 "Valittujen päivämäärien väliset muutokset"]
       [comparison-date-changes compare]]]]))

(defn format-stop-name [stop-name]
  (let [splitted-stop-name (if (str/includes? stop-name "->")
                             (str/split stop-name #"->")
                             stop-name)
        formatted-name (if (and (vector? splitted-stop-name) (> (count splitted-stop-name) 1))
                         [:span (second splitted-stop-name)
                          [:br] (str "(Vanha nimi: " (first splitted-stop-name) ")")]
                         splitted-stop-name)]
    formatted-name))

(defn format-stop-time [highlight-style interval]
  (when interval
    [:span
     [:div (stylefy/use-style (merge highlight-style
                                     {:display "inline-block"
                                      :width "20px"
                                      :height "5px"}))]
     (time/format-interval-as-time interval)]))

(defn route-trips [e! open-sections {:keys [trips date1 date2 date1-trips date2-trips combined-trips
                                            selected-trip-pair]}]
  [section {:toggle! #(e! (tv/->ToggleSection :route-trips)) :open? (get open-sections :route-trips true)}
   "Vuorot"
   "Vuorolistalla näytetään valitsemasi reitin ja päivämäärien mukaiset vuorot. Sarakkeissa näytetään reitin lähtö- ja päätepysäkkien lähtö- ja saapumisajankohdat. Muutokset-sarakkeessa näytetään reitillä tapahtuvat muutokset vuorokohtaisesti. Napsauta haluttu vuoro listalta nähdäksesi pysäkkikohtaiset aikataulut ja mahdolliset muutokset Pysäkit-osiossa."
   [:div.route-trips

    ;; Group by different (d1 start, d1 stop, d2 start, d2 stop) stops
    (for [[_ trips] (group-by (juxt (comp :gtfs/stop-name first :stoptimes first)
                                    (comp :gtfs/stop-name last :stoptimes first)
                                    (comp :gtfs/stop-name first :stoptimes second)
                                    (comp :gtfs/stop-name last :stoptimes second))
                              combined-trips)]
      [:div.trips-table {:style {:margin-top "1em"}}
       [table/table {:name->label str
                     :row-selected? #(= % selected-trip-pair)
                     :on-select #(e! (tv/->SelectTripPair (first %)))}

        [;; name of the first stop of the first trip (FIXME: should be first common?)
         {:name (if (-> trips first first :stoptimes first :gtfs/stop-name)
                  "Reittitunnus"
                  "")
          :read #(:headsign (first %))
          :col-style {:padding-left "10px" :padding-right "5px"}}
         {:name (some-> trips first first :stoptimes first :gtfs/stop-name)
          :read #(-> % first :stoptimes first :gtfs/departure-time)
          :format (partial format-stop-time (style/date1-highlight-style) )
          :col-style {:padding-left "10px" :padding-right "5px"}}
         ;; name of the last stop of the first trip
         {:name (some-> trips first first :stoptimes last :gtfs/stop-name)
          :read #(-> % first :stoptimes last :gtfs/departure-time)
          :format (partial format-stop-time (style/date1-highlight-style))
          :col-style {:padding-left "10px" :padding-right "5px"}}

         {:name (if (-> trips first second :stoptimes first :gtfs/stop-name)
                    "Reittitunnus"
                    "")
          :read #(:headsign (second %))
          :col-style {:padding-left "10px" :padding-right "5px"}}
         {:name (-> trips first second :stoptimes first :gtfs/stop-name)
          :read (comp :gtfs/departure-time first :stoptimes second)
          :format (partial format-stop-time (style/date2-highlight-style))
          :col-style {:padding-left "10px" :padding-right "5px"}}
         {:name (-> trips first second :stoptimes last :gtfs/stop-name)
          :read (comp :gtfs/departure-time last :stoptimes second)
          :format (partial format-stop-time (style/date2-highlight-style))
          :col-style {:padding-left "10px" :padding-right "5px"}}

         {:name "Muutokset" :read identity
          :format (fn [[left right {:keys [stop-time-changes stop-seq-changes]}]]
                    (cond
                      (and left (nil? right))
                      [labeled-icon [ic/content-remove] "Poistuva vuoro"]

                      (and (nil? left) right)
                      [labeled-icon [ic/content-add] "Lisätty vuoro"]

                      (= 0 stop-time-changes stop-seq-changes)
                      [labeled-icon [ic/navigation-check] "Ei muutoksia"]

                      :default
                      [:div
                       [stop-seq-changes-icon stop-seq-changes]
                       [stop-time-changes-icon stop-time-changes]]))
          :col-style {:padding-left "10px" :padding-right "5px"}}]
        trips]])]])

(defn trip-stop-sequence [e! open-sections {:keys [date1 date2 selected-trip-pair
                                                   combined-stop-sequence selected-trip-pair] :as compare}]
  [section {:open? (get open-sections :trip-stop-sequence true)
            :toggle! #(e! (tv/->ToggleSection :trip-stop-sequence))}
   "Pysäkit"
   "Pysäkkilistalla näytetään valitun vuoron pysäkkikohtaiset aikataulut."
   (let [second-stops-empty? (empty? (:stoptimes (second selected-trip-pair)))]
     [:div.trip-stop-sequence
      [table/table {:name->label str :key-fn :gtfs/stop-name}
       [{:name "Pysäkki" :read :gtfs/stop-name :format (partial format-stop-name)}
        {:name "Lähtöaika" :read :gtfs/departure-time-date1 :format (partial format-stop-time (style/date1-highlight-style))}
        {:name "Lähtöaika" :read :gtfs/departure-time-date2 :format (partial format-stop-time (style/date2-highlight-style))}
        {:name "Muutokset" :read identity
         :format (fn [{:gtfs/keys [departure-time-date1 departure-time-date2]}]
                   (cond
                     (and departure-time-date1 (nil? departure-time-date2))
                     (if second-stops-empty? "Poistuva vuoro" "Pysäkki ei kuulu reitille")

                     (and (nil? departure-time-date1) departure-time-date2)
                     "Uusi pysäkki reitillä"

                     (not= departure-time-date1 departure-time-date2)
                     [labeled-icon [ic/action-query-builder]
                      (time/format-minutes-elapsed
                        (time/minutes-elapsed departure-time-date1 departure-time-date2))]

                     :default
                     [labeled-icon {:style {:color "lightgray"}}
                      [ic/action-query-builder {:color "lightgray"}]
                      "00:00"]))}]
       combined-stop-sequence]])])

(defn- selected-route-map [_ _ _ {show-stops? :show-stops?
                                  show-route-lines :show-route-lines}]
  (let [show-atom (atom (assoc show-route-lines :stops show-stops?))
        removed-route-layers (atom {})
        inhibit-zoom (atom false)
        update (fn [this]
                 (update-marker-visibility this show-atom removed-route-layers)
                 (when-not @inhibit-zoom
                   (leaflet/update-bounds-from-layers this))
                 (reset! inhibit-zoom false))
        zoom-level (r/atom 5)]
    (r/create-class
      {:component-did-update update
       :component-did-mount (fn [this]
                              (let [^js/L.map m (aget this "refs" "leaflet" "leafletElement")]
                                (.on m "zoomend" #(do
                                                    (reset! inhibit-zoom true)
                                                    (reset! zoom-level (.getZoom m)))))
                              (update this))
       :component-will-receive-props
       (fn [this [_ _ _ _ {show-stops? :show-stops?
                           show-route-lines :show-route-lines}]]
         ;; This is a bit of a kludge, but because the stops are in the
         ;; same GeoJSON layer as the lines, we can't easily control their
         ;; visibility using react components.
         (let [show (assoc show-route-lines :stops show-stops?)]
           (when (not= @show-atom show)
             ;; Don't zoom if we changed stops or route lines visible/hidden toggle
             (reset! inhibit-zoom true))
           (reset! show-atom show)))
       :reagent-render
       (fn [e! date->hash hash->color {:keys [route-short-name route-long-name
                                              date1 date1-route-lines date1-show?
                                              date2 date2-route-lines date2-show?
                                              show-stops?
                                              show-route-lines]}]
         (let [show-date1? (and date1-show?
                                (not (empty? (get date1-route-lines "features"))))
               show-date2? (and date2-show?
                                (not (empty? (get date2-route-lines "features"))))
               zoom @zoom-level
               [line-weight offset icon-size] (cond
                                                (< zoom 9) [2 1 [6 6]]
                                                (< zoom 12) [2 2 [12 12]]
                                                (< zoom 14) [3 2 [16 16]]
                                                :default [3 2 [18 18]])
               date1-filtered-trips (filter
                                      (fn [trip] (get show-route-lines (get-in trip ["route-line" "properties" "routename"])))
                                      (get date1-route-lines "features"))
               date2-filtered-trips (filter
                                      (fn [trip] (get show-route-lines (get-in trip ["route-line" "properties" "routename"])))
                                      (get date2-route-lines "features"))
               date1-data {:features (mapcat
                                       #(conj
                                          (get-in % ["route-line" "stops"])
                                          {:type "Feature"
                                           :properties (get-in % ["route-line" "properties"])
                                           :geometry (get-in % ["route-line" "geometry"])})
                                       date1-filtered-trips)}
               date2-data {:features (mapcat
                                       #(conj
                                          (get-in % ["route-line" "stops"])
                                          {:type "Feature"
                                           :properties (get-in % ["route-line" "properties"])
                                           :geometry (get-in % ["route-line" "geometry"])})
                                       date2-filtered-trips)}]
           [:div.transit-visualization-route-map {:style {:z-index 99 :position "relative"}}
            [leaflet/Map {:ref "leaflet"
                          :center #js [65 25]
                          :zoomControl true
                          :zoom 5}
             (leaflet/background-tile-map)
             (when show-date1?
               ^{:key (str date1 "_" route-short-name "_" route-long-name "_" zoom)}
               [leaflet/GeoJSON {:data date1-data
                                 :onEachFeature (initialize-route-features (- offset) icon-size)
                                 :style {:lineJoin "miter"
                                         :lineCap "miter"
                                         :color style/date1-highlight-color
                                         :weight line-weight}}])
             (when show-date2?
               ^{:key (str date2 "_" route-short-name "_" route-long-name "_" zoom)}
               [leaflet/GeoJSON {:data date2-data
                                 :onEachFeature (initialize-route-features (+ 2 offset) icon-size)
                                 :style {:lineJoin "miter"
                                         :lineCap "miter"
                                         :color style/date2-highlight-color
                                         :weight line-weight}}])]]))})))

(defn selected-route-map-section [e! open-sections date->hash hash->color compare]
  [section {:toggle! #(e! (tv/->ToggleSection :route-map))
            :open? (get open-sections :route-map true)}
   "Kartta"
   [:div
    [:span
     "Valitse kartalla näytettävät pysäkkiketjut. Alla olevaan listaan on koostettu kaikki erilaiset pysäkkiketjut valitsemasi reitin ja kalenterin päivien vuorojen perusteella."]
    [:div (stylefy/use-style style/map-checkbox-container)

     [:div {:style {:flex 1}}
      [ui/checkbox {:label "Näytä pysäkit"
                    :checked (boolean (:show-stops? compare))
                    :on-check #(e! (tv/->ToggleRouteDisplayStops))}]]
     [:div {:style {:flex 4}}
      (when (pos-int? (:show-route-lines compare))
        ;; There is more than one distinct route (stop-sequence), show checkboxes for displaying
        (doall
          (for [[routename show?] (sort-by first (seq (:show-route-lines compare)))]
            ^{:key routename}
            [ui/checkbox {:label (first (str/split routename #"\|\|"))
                          :checked show?
                          :on-check #(e! (tv/->ToggleShowRouteLine routename))}])))]]
   [selected-route-map e! date->hash hash->color compare]]])

(defn gtfs-package-info [e! open-sections packages]
  (let [[latest-package & previous-packages] packages
        open? (get open-sections :gtfs-package-info false)
        pkg (fn [{:keys [created min-date max-date interface-url]}]
              [:div.gtfs-package
               interface-url
               " Ladattu NAPiin " (time/format-timestamp-for-ui created) ". "
               "Kattaa liikennöinnin aikavälillä " min-date " - " max-date "."])]
    [:div (stylefy/use-style style/infobox)
     [:div (stylefy/use-style style/infobox-text)
      [:b "Viimeisin aineisto"]
      [pkg latest-package]]
     (when (seq previous-packages)
       [:div
        [common/linkify "#" "Näytä tiedot myös aiemmista aineistoista"
         {:icon (if open?
                  [ic/navigation-expand-less]
                  [ic/navigation-expand-more])
          :on-click #(do (.preventDefault %)
                         (e! (tv/->ToggleSection :gtfs-package-info)))
          :style style/infobox-more-link}]
        (when open?
          [:div
           (doall
            (for [{id :id :as p} previous-packages]
              ^{:key id}
              [pkg p]))])])]))

(defn transit-visualization [e! {:keys [hash->color date->hash service-info changes-no-change changes-filtered selected-route compare open-sections route-hash-id-type show-no-change-routes? show-no-change-routes-checkbox?]
                                 :as transit-visualization}]
  (let [routes (if show-no-change-routes?
                 (:gtfs/route-changes changes-no-change)
                 (:gtfs/route-changes changes-filtered))
        route-name (if (service-is-using-headsign route-hash-id-type)
                     (str (:gtfs/route-short-name selected-route) " "
                          (:gtfs/route-long-name selected-route)
                          " (" (:gtfs/trip-headsign selected-route) ")")
                     (str (:gtfs/route-short-name selected-route) " "
                          (:gtfs/route-long-name selected-route)))]
    [:div
     [:div.transit-visualization

      [page/page-controls
       [common/back-link "#/transit-changes" "Takaisin markkinaehtoisen liikenteen muutokset -näkymään"]
       "Reittiliikenteen tunnistetut muutokset"
       [:div
        [:h2 (:transport-service-name service-info) " (" (:transport-operator-name service-info) ")"]

        [gtfs-package-info e! open-sections (:gtfs-package-info transit-visualization)]

        ;; Route listing with number of changes
        "Taulukossa on listattu valitussa palvelussa havaittuja muutoksia. Voit valita listalta yhden reitin kerrallaan tarkasteluun. Valitun reitin reitti- ja aikataulutiedot näytetään taulukon alapuolella kalenterissa, kartalla, vuorolistalla ja pysäkkiaikataululistalla."

        [:div.row {:style {:margin-top "1rem" :display "flex" :justify-content "flex-end" :flex-wrap "wrap"}}
         [:div
          [form-fields/field {:label (tr [:transit-visualization-page :checkbox-show-no-change])
                              :type :checkbox
                              :update! #(e! (tv/->ToggleShowNoChangeRoutes e!))
                              :disabled? (not (tv/route-filtering-available? transit-visualization))
                              :style (when-not (tv/route-filtering-available? transit-visualization) style-base/disabled-control)}
           ;; Toggling table key :show-no-change-routes may cause rendering delay on large data, blocking also rendering checkbox disabling changes.
           ;; Different value key for checkbox allows triggering checkbox disabling logic first and table changes only after that.
           show-no-change-routes-checkbox?]]]

        [route-changes e! routes (:gtfs/route-changes changes-no-change) selected-route route-hash-id-type]]]

      (when selected-route
        [:div.transit-visualization-route.container
         [:h3 "Valittu reitti: " route-name]

         (when (and hash->color date->hash (tv/loaded-from-server? transit-visualization))
           [:span
            [route-calendar e! transit-visualization]
            [selected-route-map-section e! open-sections date->hash hash->color compare]
            [route-trips e! open-sections compare]
            [trip-stop-sequence e! open-sections compare]])])]]))
