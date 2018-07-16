(ns ote.views.transit-visualization
  "Visualization of transit data (GTFS)."
  (:require [reagent.core :as r]
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
            [clojure.string :as str]))

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
        :same (if (and (= d-hash hover-hash) (not (= d hover-day-formatted)))
                {:box-shadow "inset 0 0 0 2px black, inset 0 0 0 100px rgba(255,255,255,.5)"}
                (if (= d hover-day-formatted)
                  {:box-shadow "inset 0 0 0 2px black, inset 0 0 0 100px rgba(255,255,255,.75)"}
                  {:box-shadow "inset 0 0 0 2px transparent, inset 0 0 0 100px rgba(0,0,0,.25)"}))
        :diff (if (and (not (= d-hash hover-hash))
                       (= (time/day-of-week day) (time/day-of-week hover-day)))
                {:box-shadow "inset 0 0 0 2px crimson, inset 0 0 0 3px black, inset 0 0 0 100px rgba(255,255,255,.65)"}
                (if (and (= d-hash hover-hash) (= d hover-day-formatted))
                  {:box-shadow "inset 0 0 0 2px black,
                                inset 0 0 0 3px transparent,
                                inset 0 0 0 100px rgba(255,255,255,.75)"}
                  {:box-shadow "inset 0 0 0 2px transparent,
                                inset 0 0 0 3px transparent,
                                inset 0 0 0 100px rgba(0,0,0,.25)"}))))))

(defn day-style [hash->color date->hash highlight day selected?]
  (let [d (time/format-date day)
        hash-color (hash->color (date->hash d))]

    (merge
      {:background-color hash-color
       :color "rgb (0, 255, 255)"
       :transition "box-shadow 0.25s"
       :box-shadow "inset 0 0 0 2px transparent, inset 0 0 0 3px transparent, inset 0 0 0 100px transparent"}
      (when (:hash highlight)
        (highlight-style hash->color date->hash day highlight)))))

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

(defn- route-listing [e! {:keys [date1 date2 different?] :as compare}]
  [:div
   [ui/checkbox {:label "Näytä vain reitit, jotka erilaisia"
                 :checked different?
                 :on-check #(e! (tv/->ToggleDifferent))}]
   [table/table {:no-rows-message "Ei reittejä"
                 :height 300
                 :name->label str
                 :show-row-hover? true
                 :on-select #(e! (tv/->SelectRouteForDisplay (:route-short-name (first %))
                                                             (:route-long-name (first %))
                                                             (:trip-headsign (first %))))}
    [{:name "Nimi" :width "50%"
      :read identity
      :format #(str (:route-short-name %) " " (:route-long-name %))}
     {:name "Otsatunnus" :width "20%"
      :read :trip-headsign}
     {:name (str "Vuoroja " date1) :width "15%"
      :read :date1-trips}
     {:name (str "Vuoroja " date2) :width "15%"
      :read :date2-trips}]
    (if different?
      (filter :different? (:routes compare))
      (:routes compare))]])

(defn- initialize-route-features
  "Bind popup content and set marker icon for stop marker features."
  [offset]
  (fn [feature ^js/L.Layer layer]
    (if-let [name (aget feature "properties" "name")]
      ;; This features is a stop marker
      (do
        (aset (aget layer "options") "icon"
              (js/L.icon #js {:iconUrl (str js/document.location.protocol "//" js/document.location.host "/img/stop_map_marker.svg")
                               :iconSize #js [20 20]
                               :iconAnchor #js [10 10]}))
        (.bindPopup layer name))
      ;; This feature has no name, it is the route line, apply pixel offset
      (.setOffset layer offset))))

(defn update-marker-visibility-by-zoom [this]
  (let [^js/L.map m (aget this "refs" "leaflet" "leafletElement")
        show? (>= (.getZoom m) 13)]
    (.eachLayer m (fn [layer]
                    (when-let [icon (aget layer "_icon")]
                      (set! (.-visibility (aget icon "style"))
                            (if show? "" "hidden")))))))

(defn- selected-route-map [_ _ _ _]
  (r/create-class
   {:component-did-update (fn [this]
                            (update-marker-visibility-by-zoom this)
                            (leaflet/update-bounds-from-layers this))
    :component-did-mount (fn [this]
                           (let [^js/L.map m (aget this "refs" "leaflet" "leafletElement")]
                             (.on m "zoomend" (fn [_] (update-marker-visibility-by-zoom this)))
                             (leaflet/update-bounds-from-layers this)))
     :reagent-render
     (fn [e! date->hash hash->color {:keys [route-short-name route-long-name
                                            date1 date1-route-lines date1-show?
                                            date2 date2-route-lines date2-show?]}]
       (let [show-date1? (and date1-show?
                              (not (empty? (get date1-route-lines "features"))))
             show-date2? (and date2-show?
                              (not (empty? (get date2-route-lines "features"))))]
         [:div.transit-visualization-route-map {:style {:z-index 99 :position "relative"}}

          (when date1-route-lines
            [ui/checkbox {:label (str "Näytä " date1 " (musta)")
                          :checked (boolean date1-show?)
                          :on-check #(e! (tv/->ToggleRouteDisplayDate date1))}])
          (when date2-route-lines
            [ui/checkbox {:label (str "Näytä " date2 " (punainen)")
                          :checked (boolean date2-show?)
                          :on-check #(e! (tv/->ToggleRouteDisplayDate date2))}])
          [leaflet/Map {:ref "leaflet"
                        :center #js [65 25]
                        :zoomControl true
                        :zoom 5}
           (leaflet/background-tile-map)
           (when show-date1?
             ^{:key (str date1 "_" route-short-name "_" route-long-name)}
             [leaflet/GeoJSON {:data date1-route-lines
                               :onEachFeature (initialize-route-features -3)
                               :style (merge
                                       {:color "black"
                                        :weight 6})}])
           (when show-date2?
             ^{:key (str date2 "_" route-short-name "_" route-long-name)}
             [leaflet/GeoJSON {:data date2-route-lines
                               :onEachFeature (initialize-route-features 3)
                               :style (merge
                                       {:color "red"
                                        :weight 6})}])]]))}))

(defn stop-listing [trips]
  [:div {:style {:width "100%"}}
   (for [{:keys [trip-id trip-headsign stops]} trips]
     ^{:key trip-id}
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
         (str/split stops #"->"))]])])

(defn short-trip-description [{:keys [trip-headsign stops]}]
  (let [stops (mapv #(zipmap [:stop-name :time] (str/split % #"@"))
                    (str/split stops #"->"))]
    {:departure (first stops)
     :destination (last stops)
     :stops (count stops)
     :headsign trip-headsign}))

(defn date-trips [e! {:keys [date1 date1-trips date2 date2-trips route-short-name route-long-name trip-headsign]}]
  (let [date1-trip-descriptions (into #{} (map short-trip-description) date1-trips)
        date2-trip-descriptions (into #{} (map short-trip-description) date2-trips)
        all-trip-descriptions (into #{} (concat date1-trip-descriptions
                                                date2-trip-descriptions))]
    [:div
     [:span "Vuorot reitille " [:b route-short-name " " route-long-name]
      (when trip-headsign
        [:span " otsatunnuksella " [:b trip-headsign]])
      " valittuina päivinä:"]
     [table/table {:height 300 :name->label str}
      [{:name "Lähtö" :width "35%" :format #(str (:time %) " " (:stop-name %)) :read :departure}
       {:name "Määränpää" :width "35%" :format #(str (:time %) " " (:stop-name %)) :read :destination}
       {:name "Pysäkkejä" :width "12%" :read :stops}
       {:name date1 :width "9%" :format #(if % "\u2713" "-") :read (comp boolean date1-trip-descriptions)}
       {:name date2 :width "9%" :format #(if % "\u2713" "-") :read (comp boolean date2-trip-descriptions)}]
      (sort-by (juxt (comp :time :departure) (comp :stop-name :departure))
               all-trip-descriptions)]

     #_[:table {:style {:width "100%"}}
        [:thead
         [:tr
          [:th {:width "50%"} date1] [:th {:width "50%"} date2]]]
        [:tbody
         [:tr
          [:td
           [stop-listing date1-trips]]
          [:td
           [stop-listing date2-trips]]]]]]))

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
       [route-listing e! compare]

       ;; If a route is selected, show map
       [selected-route-map e! date->hash hash->color compare]

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


(defn transit-visualization [e! {:keys [hash->color date->hash loading? highlight operator-name]
                                 :as transit-visualization}]
  [:div
   (when (and (not loading?) hash->color)
     [:div.transit-visualization
      [days-to-diff-info e! transit-visualization highlight]
      [:h3 operator-name]
      [highlight-mode-switch e! highlight]
      [calendar-style-switch e! transit-visualization]
      [service-calendar/service-calendar (merge {:view-mode (:calendar-mode transit-visualization)
                                                 :selected-date? (constantly false)
                                                 :on-select (r/partial select-day e!)}
                                                (when (get highlight :mode)
                                                  {:on-hover (r/partial hover-day e! date->hash)})
                                                {:day-style (r/partial day-style hash->color date->hash
                                                                       (:highlight transit-visualization))
                                                 :years (or (:years transit-visualization)
                                                            [2017 2018])})]
      [date-comparison e! transit-visualization]])])
