(ns ote.views.transit-visualization
  "Visualization of transit data (GTFS)."
  (:require [reagent.core :as r]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.icon_labeled :as icon-l]
            [stylefy.core :as stylefy]
            [ote.style.transit-changes :as style]
            [ote.style.base :as style-base]
            [ote.theme.colors :as color]
            [ote.app.controller.transit-visualization :as tv]
            [ote.time :as time]
            [cljs-react-material-ui.reagent :as ui]
            [ote.ui.table :as table]
            [ote.db.transport-service :as t-service]
            [ote.localization :refer [tr]]
            [ote.ui.leaflet :as leaflet]
            [clojure.string :as str]
            [ote.ui.page :as page]
            [ote.ui.scroll :as scroll]
            [ote.app.routes :as routes]
            [ote.ui.common :as common]
            [ote.ui.form-fields :as form-fields]
            [ote.views.transit-visualization.calendar :as tv-calendar]
            [ote.views.transit-visualization.change-utilities :as tv-utilities]
            [ote.views.transit-visualization.change-icons :as tv-change-icons]
            [ote.ui.circular_progress :as prog]))

(set! *warn-on-infer* true)

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

(defn hover-day [e! date->hash day]
  (e! (tv/->HighlightHash (date->hash (time/format-date day)) day))
  (e! (tv/->DaysToFirstDiff (time/format-date day) date->hash)))

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
  [^Number offset [w h]]
  (fn [^Object feature ^js/L.Layer layer]
    (let [stop-name (aget feature "properties" "stopname")
          trip-name (aget feature "properties" "trip-name")
          popup-html (str "Pysäkki: " (first (str/split stop-name #"\|\|")))
          ^Function my-layer (aget layer "setOffset")]
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
      (.call my-layer layer offset)))))

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
                    (if-let [^HTMLImageElement icon (aget layer "_icon")]
                      ;; This is a stop, set the icon visibility
                      (let [^CSSStyleDeclaration icon-style (aget icon "style")]
                        (set! (.-visibility icon-style)
                              (if (and
                                    (:stops show)
                                    (not (contains? @removed-route-layers (aget layer "feature" "properties" "trip-name")))
                                    (show (some-> layer (aget "feature") (aget "properties") (aget "trip-name"))))
                                ""
                                "hidden"))))

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
         ^{:key (str "stop-listing-" i)}
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

(defn service-is-using-headsign
  "Routes are combained between gtfs packages using route-hash-id. When trips headsign is not used there is no
  reason to show it in route list or route name."
  [route-hash-id-type]
  (or (nil? route-hash-id-type)
      (= (:gtfs/route-hash-id-type route-hash-id-type) "short-long-headsign")
      (= (:gtfs/route-hash-id-type route-hash-id-type) "long-headsign")))

(def selected-change-keys #{:removed-trips :trip-stop-sequence-changes-lower
                            :trip-stop-sequence-changes-upper :route-hash-id
                            :trip-stop-time-changes-lower :trip-stop-time-changes-upper :change-type :added-trips
                            :different-week-date
                            :route-short-name :route-long-name
                            :count})

(defn route-changes [e! route-changes no-change-routes selected-route route-hash-id-type changes-all]
  (let [route-count (count route-changes)
        no-change-routes-count (count no-change-routes)
        table-height (str
                       (cond
                         ;; 54: 50 (row height) + 4 (2px outline on both sides)
                         (and (< 0 route-count) (> 10 route-count)) (* 54 route-count) ; 1 - 10
                         (= 0 route-count) 100
                         :else 500)
                       "px"); 10+
        no-rows-message (if (and
                              (= 0 route-count)
                              (pos-int? no-change-routes-count))
                          (tr [:transit-visualization-page :no-changes-in-routes])
                          (tr [:transit-visualization-page :loading-routes]))]
    [:div.route-changes
     [tv-utilities/route-changes-legend]
     [table/table {:no-rows-message no-rows-message
                   :height table-height
                   :label-style style-base/table-col-style-wrap
                   :name->label str
                   :show-row-hover? true
                   :on-select #(when (first %)
                                 (let [current-url (str/replace (str js/window.location) #"/now(.*)" "/now/")
                                       route (str/replace (:route-hash-id (first %)) #"\s" "")]
                                   (.pushState js/window.history #js {} js/document.title
                                     (str current-url route))
                                   (e! (tv/->SelectRouteForDisplay (first %)))
                                   (.setTimeout js/window (fn [] (scroll/scroll-to-id "route-calendar-anchor")) 150)))
                   :row-selected? #(= (:route-hash-id %) (:route-hash-id selected-route))}

      [{:name ""
        :read identity
        :format (fn [{:keys [recent-change? change-detected]}]
                  (when recent-change?
                    [:div (merge (stylefy/use-style style/new-change-container)
                                 {:title (str "Muutos tunnistettu: " (time/format-timestamp->date-for-ui change-detected))})
                     [:div (stylefy/use-style style/new-change-indicator)]]))
        :col-style style-base/table-col-style-wrap
        :width "2%"}
       {:name "Reitti" :width "20%"
        :read (juxt :route-short-name :route-long-name)
        :col-style style-base/table-col-style-wrap
        :format (fn [[short long]]
                  (str short " " long))}

       ;; Show Reitti/Määränpää column only if it does affect on routes.
       (when (service-is-using-headsign route-hash-id-type)
         {:name "Reitti/määränpää" :width "21%"
          :read :trip-headsign
          :col-style style-base/table-col-style-wrap})

       {:name "Muutoksia"
        :width "8%"
        :read identity
        :col-style style-base/table-col-style-wrap
        :format (fn [row]
                  (if (= :no-change (:change-type row))
                    "0 kpl"
                    (str (:count row) " kpl")))}
       {:name "Aikaa 1. muutokseen"
        :width "9%"
        :read :different-week-date
        :col-style style-base/table-col-style-wrap
        :format (fn [different-week-date]
                  (if-not different-week-date
                    [icon-l/icon-labeled [ic/navigation-check] "Ei muutoksia"]
                    [:span
                     (str (time/days-until different-week-date) " " (tr [:common-texts :time-days-abbr]))
                     [:div (stylefy/use-style {:color "gray"})
                      (str  "(" (time/format-timestamp->date-for-ui different-week-date) ")")]]))}
       {:name "Muutosten yhteenveto" :width "40%"
        :read identity
        :col-style style-base/table-col-style-wrap
        :format (fn [grouped-route-data]
                  [tv-change-icons/route-change-icons grouped-route-data])}]

      route-changes] e!
     [:div {:id "route-calendar-anchor"}]]))

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

(defn route-trips [e! open-sections {:keys [combined-trips selected-trip-pair] :as compare}]
  (let [date1-label (str " (" (count (:date1-trips compare)) " vuoroa)")
        date2-label (str " (" (count (:date2-trips compare)) " vuoroa)")]
    [tv-utilities/section {:toggle! #(e! (tv/->ToggleSection :route-trips)) :open? (get open-sections :route-trips true)}
     "Vuorot"
     "Vuorolistalla näytetään valitsemasi reitin ja päivämäärien mukaiset vuorot. Sarakkeissa näytetään reitin lähtö- ja päätepysäkkien lähtö- ja saapumisajankohdat. Muutokset-sarakkeessa näytetään reitillä tapahtuvat muutokset vuorokohtaisesti. Napsauta haluttu vuoro listalta nähdäksesi pysäkkikohtaiset aikataulut ja mahdolliset muutokset Pysäkit-osiossa."

     [:div
      [tv-utilities/date-comparison-icons-with-date-labels compare date1-label date2-label false]
      [:div.route-trips

       ;; Group by different (d1 start, d1 stop, d2 start, d2 stop) stops
       (for [[_ trips] (group-by (juxt (comp :gtfs/stop-name first :stoptimes first)
                                       (comp :gtfs/stop-name last :stoptimes first)
                                       (comp :gtfs/stop-name first :stoptimes second)
                                       (comp :gtfs/stop-name last :stoptimes second))
                                 combined-trips)]
         ^{:key (str "trip-table" (rand-int 9999999))}
         [:div.trips-table {:style {:margin-top "1em"}}
          [table/table {:name->label str
                        :row-selected? #(= % selected-trip-pair)
                        :label-style style-base/table-col-style-wrap
                        :on-select #(e! (tv/->SelectTripPair (first %)))}

           [;; name of the first stop of the first trip (FIXME: should be first common?)
            {:name (if (-> trips first first :stoptimes first :gtfs/stop-name)
                     "Reittitunnus"
                     "")
             :read #(:headsign (first %))
             :col-style style-base/table-col-style-wrap}
            {:name (some-> trips first first :stoptimes first :gtfs/stop-name)
             :read #(-> % first :stoptimes first :gtfs/departure-time)
             :format (partial format-stop-time (style/date1-highlight-style))
             :col-style style-base/table-col-style-wrap}
            ;; name of the last stop of the first trip
            {:name (some-> trips first first :stoptimes last :gtfs/stop-name)
             :read #(-> % first :stoptimes last :gtfs/departure-time)
             :format (partial format-stop-time (style/date1-highlight-style))
             :col-style style-base/table-col-style-wrap}

            {:name (if (-> trips first second :stoptimes first :gtfs/stop-name)
                     "Reittitunnus"
                     "")
             :read #(:headsign (second %))
             :col-style style-base/table-col-style-wrap}
            {:name (-> trips first second :stoptimes first :gtfs/stop-name)
             :read (comp :gtfs/departure-time first :stoptimes second)
             :format (partial format-stop-time (style/date2-highlight-style))
             :col-style style-base/table-col-style-wrap}
            {:name (-> trips first second :stoptimes last :gtfs/stop-name)
             :read (comp :gtfs/departure-time last :stoptimes second)
             :format (partial format-stop-time (style/date2-highlight-style))
             :col-style style-base/table-col-style-wrap}

            {:name "Muutokset" :read identity
             :format (fn [[left right {:keys [stop-time-changes stop-seq-changes]}]]
                       (cond
                         (and left (nil? right))
                         [icon-l/icon-labeled [ic/content-remove] "Poistuva vuoro"]

                         (and (nil? left) right)
                         [icon-l/icon-labeled [ic/content-add] "Lisätty vuoro"]

                         (= 0 stop-time-changes stop-seq-changes)
                         [icon-l/icon-labeled [ic/navigation-check] "Ei muutoksia"]

                         :default
                         [:div (stylefy/use-style style/transit-changes-icon-row-container)
                          [:div (stylefy/use-style {:width "50%"})
                           [tv-change-icons/stop-seq-changes-icon stop-seq-changes]]
                          [:div (stylefy/use-style {:width "50%"})
                           [tv-change-icons/stop-time-changes-icon stop-time-changes]]]))
             :col-style style-base/table-col-style-wrap}]
           trips]])]]]))

(defn trip-stop-sequence [e! open-sections {:keys [combined-stop-sequence selected-trip-pair] :as compare}]
  (let [date1-first-stop (first (:stoptimes (first (:selected-trip-pair compare))))
        date1-last-stop (last (:stoptimes (first (:selected-trip-pair compare))))
        date2-first-stop (first (:stoptimes (second (:selected-trip-pair compare))))
        date2-last-stop (last (:stoptimes (second (:selected-trip-pair compare))))
        date1-label (if (and date1-first-stop date1-last-stop)
                      (str ", " (time/format-interval-as-time (:gtfs/departure-time date1-first-stop)) " " (:gtfs/stop-name date1-first-stop) " - "
                           (time/format-interval-as-time (:gtfs/departure-time date1-last-stop)) " " (:gtfs/stop-name date1-last-stop))
                      "")
        date2-label (if (and date2-first-stop date2-last-stop)
                      (str ", " (time/format-interval-as-time (:gtfs/departure-time date2-first-stop)) " " (:gtfs/stop-name date2-first-stop) " - "
                           (time/format-interval-as-time (:gtfs/departure-time date2-last-stop)) " " (:gtfs/stop-name date2-last-stop))
                      "")]
    [tv-utilities/section {:open? (get open-sections :trip-stop-sequence true)
                           :toggle! #(e! (tv/->ToggleSection :trip-stop-sequence))}
     "Pysäkit"
     "Pysäkkilistalla näytetään valitun vuoron pysäkkikohtaiset aikataulut."
     (let [second-stops-empty? (empty? (:stoptimes (second selected-trip-pair)))]
       [:div
        [tv-utilities/date-comparison-icons-with-date-labels compare date1-label date2-label true]
        [:div.trip-stop-sequence {:style {:margin-top "1em"}}
         [table/table {:name->label str
                       :label-style style-base/table-col-style-wrap}
          [{:name "Pysäkki"
            :read :gtfs/stop-name
            :col-style style-base/table-col-style-wrap
            :format (partial format-stop-name)}
           {:name "Lähtöaika"
            :read :gtfs/departure-time-date1
            :col-style style-base/table-col-style-wrap
            :format (partial format-stop-time (style/date1-highlight-style))}
           {:name "Lähtöaika"
            :read :gtfs/departure-time-date2
            :col-style style-base/table-col-style-wrap
            :format (partial format-stop-time (style/date2-highlight-style))}
           {:name "Muutokset"
            :read identity
            :col-style style-base/table-col-style-wrap
            :format (fn [{:gtfs/keys [departure-time-date1 departure-time-date2]}]
                      (cond
                        (and departure-time-date1 (nil? departure-time-date2))
                        (if second-stops-empty? "Poistuva vuoro" "Pysäkki ei kuulu reitille")

                        (and (nil? departure-time-date1) departure-time-date2)
                        "Uusi pysäkki reitillä"

                        (not= departure-time-date1 departure-time-date2)
                        [icon-l/icon-labeled [ic/action-query-builder]
                         (time/format-minutes-elapsed
                           (time/minutes-elapsed departure-time-date1 departure-time-date2))]

                        :default
                        [icon-l/icon-labeled {:style color/icon-disabled}
                         [ic/action-query-builder color/icon-disabled] nil]))}]
          combined-stop-sequence]]])]))

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
                                      (fn [trip]
                                        (get show-route-lines (get-in trip ["route-line" "properties" "routename"])))
                                      (get date1-route-lines "features"))
               date2-filtered-trips (filter
                                      (fn [trip]
                                        (get show-route-lines (get-in trip ["route-line" "properties" "routename"])))
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
  [tv-utilities/section {:toggle! #(e! (tv/->ToggleSection :route-map))
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
      (when (pos-int? (count (:show-route-lines compare)))
        ;; There is more than one distinct route (stop-sequence), show checkboxes for displaying
        (doall
          (for [[routename show?] (sort-by first (seq (:show-route-lines compare)))]
            ^{:key (str "selected-route-map-section-" routename)}
            [ui/checkbox {:label (first (str/split routename #"\|\|"))
                          :checked show?
                          :on-check #(e! (tv/->ToggleShowRouteLine routename))}])))]]]

   [:div
    [tv-utilities/date-comparison-icons compare]
    [:div
     [selected-route-map e! date->hash hash->color compare]]]])

(defn gtfs-package-info [e! open-sections packages service-id]
  (let [grouped-packages (group-by :interface-url packages)
        group-keys (keys grouped-packages)
        latests-packages (mapv
                           (fn [k]
                             (first (get grouped-packages k)))
                           group-keys)
        previous-packages (apply concat
                                 (mapv (fn [k]
                                         (when-not (empty? (rest (get grouped-packages k)))
                                           (rest (get grouped-packages k))))
                                       group-keys))
        open? (get open-sections :gtfs-package-info false)
        pkg (fn [{:keys [created min-date max-date interface-url]} show-link?]
              (when created
                [:div.gtfs-package
                 interface-url
                 " Ladattu NAPiin "
                 (if show-link?
                   (common/linkify
                     (str "/#/transit-visualization/" service-id "/" (time/format-date-iso-8601 created) "/all")
                     (time/format-timestamp-for-ui created))
                   (time/format-timestamp-for-ui created)) ". "
                 "Sisältää tietoa liikennöinnistä ajanjaksolle  " min-date " - " max-date "."]))]
    [:div (stylefy/use-style style/infobox)
     [:div (stylefy/use-style style/infobox-text)
      [:b "Viimeisin aineisto"]
      (doall
        (for [p latests-packages]
          ^{:key (str "latest-package-id-" (:id p))}
          [pkg p false]))]
     (when (and
             (seq previous-packages)
             (not (empty? previous-packages)))
       [:div
        [common/linkify "#" "Näytä tiedot myös aiemmista aineistoista"
         {:icon (if open?
                  [ic/navigation-expand-less]
                  [ic/navigation-expand-more])
          :on-click (fn [^SyntheticMouseEvent event]
                      (.preventDefault event)
                      (e! (tv/->ToggleSection :gtfs-package-info)))
          :style style/infobox-more-link}]
        (when open?
          [:div
           (doall
            (for [{id :id :as p} previous-packages]
              ^{:key (str "gtfs-package-info-" id)}
              [pkg p true]))])])]))

(defn transit-visualization [e! {:keys [hash->color date->hash service-info changes-route-no-change changes-all
                                        changes-route-filtered selected-route compare open-sections route-hash-id-type
                                        all-route-changes-display? all-route-changes-checkbox]
                                 :as transit-visualization}]
  (let [routes (if all-route-changes-display?
                 changes-route-no-change
                 changes-route-filtered)
        route-name (if (service-is-using-headsign route-hash-id-type)
                     (str (:route-short-name selected-route) " "
                          (:route-long-name selected-route)
                          " (" (:trip-headsign selected-route) ")")
                     (str (:route-short-name selected-route) " "
                       (:route-long-name selected-route)))]
    [:div
     [:div.transit-visualization

      [page/page-controls
       [common/back-link-with-event :transit-changes "Takaisin markkinaehtoisen liikenteen muutokset -näkymään"] ; this is done with click event because IE11 doesn't launch popstate event
       "Reittiliikenteen tunnistetut muutokset"
       [:div
        [:h2 (:transport-service-name service-info) " (" (:transport-operator-name service-info) ")"]

        [gtfs-package-info e! open-sections (:gtfs-package-info transit-visualization) (:transport-service-id service-info)]

        ;; Route listing with number of changes
        (tr [:transit-visualization-page :route-description])

        [:div.row {:style {:margin-top "1rem" :display "flex" :justify-content "flex-end" :flex-wrap "wrap"}}
         [:div
          [form-fields/field {:label (tr [:transit-visualization-page :checkbox-show-no-change])
                              :type :checkbox
                              :update! #(e! (tv/->ToggleShowNoChangeRoutes e!))
                              :disabled? (not (tv/route-filtering-available? transit-visualization))
                              :style (when-not (tv/route-filtering-available? transit-visualization) style-base/disabled-control)}
           ;; Toggling table key :all-route-changes-display? may cause rendering delay on large data, blocking also rendering checkbox state changes.
           ;; Thus different key for checkbox allows triggering checkbox disabling logic first and table changes only after that.
           all-route-changes-checkbox]]]

        [route-changes e! routes changes-route-no-change selected-route route-hash-id-type changes-all]]]

      (when selected-route
        [:div.transit-visualization-route.container
         [:h3 "Valittu reitti: " route-name]
         [tv-calendar/route-calendar e! transit-visualization changes-all selected-route]

         (if (tv/loading-trips? transit-visualization)
           [prog/circular-progress (tr [:common-texts :loading])]
           ;; Selecting a new date1 from calendar clears date2 related keys which are used by selected-route-map-section
           ;; Also, displaying below data is always in connection with the date1&date2 pair which is outdated after a
           ;; new selection from calendar.
           (when (:date2-trips compare)
             [:span
              [selected-route-map-section e! open-sections date->hash hash->color compare]
              [route-trips e! open-sections compare]
              [trip-stop-sequence e! open-sections compare]]))])]]))
