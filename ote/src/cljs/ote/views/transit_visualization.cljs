(ns ote.views.transit-visualization
  "Visualization of transit data (GTFS)."
  (:require [reagent.core :as r]
            [cljs-react-material-ui.icons :as ic]
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

(defn day-style [hash->color date->hash date1 date2 day]
  (let [prev-week-date (time/format-date-iso-8601 (time/days-from day -7))
        prev-week-hash (date->hash prev-week-date)
        d (time/format-date-iso-8601 day)
        hash (date->hash d)
        hash-color (hash->color hash)]
    (merge
     {:background-color hash-color
      :color "rgb (0, 255, 255)"
      :transition "box-shadow 0.25s"
      :box-shadow "inset 0 0 0 2px transparent, inset 0 0 0 3px transparent, inset 0 0 0 100px transparent"}
     (when (and prev-week-hash hash (not= hash prev-week-hash))
       {:box-shadow "inset 0 0 0 2px black,
                     inset 0 0 0 3px transparent"})
     (cond (= (time/format-date-iso-8601 date1) d)
           (style/date1-highlight-style hash-color)

           (= (time/format-date-iso-8601 date2) d)
           (style/date2-highlight-style hash-color))
     #_(when (:hash highlight)
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

#_(defn- route-listing [e! {:keys [date1 date2 different?] :as compare}]
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
  [offset [w h]]
  (fn [feature ^js/L.Layer layer]
    (if-let [name (aget feature "properties" "name")]
      ;; This features is a stop marker
      (do
        (aset (aget layer "options") "icon"
              (js/L.icon #js {:iconUrl (str js/document.location.protocol "//" js/document.location.host "/img/stop_map_marker.svg")
                               :iconSize #js [w h]
                               :iconAnchor #js [(int (/ w 2)) (int (/ h 2))]}))
        (.bindPopup layer name))
      ;; This feature has no name, it is the route line, apply pixel offset
      (.call (aget layer "setOffset") layer offset))))

(defn update-marker-visibility [this show?-atom]
  (let [^js/L.map m (aget this "refs" "leaflet" "leafletElement")
        show? @show?-atom]
    (.eachLayer m (fn [layer]
                    (when-let [icon (aget layer "_icon")]
                      (set! (.-visibility (aget icon "style"))
                            (if show? "" "hidden")))))))



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
   (for [[icon label] [[ic/content-add " Uusia vuoroja"]
                       [ic/content-remove " Poistuvia vuoroja"]
                       [ic/action-timeline " Pysäkkimuutoksia"]
                       [ic/action-query-builder " Aikataulumuutoksia"]]]
     ^{:key label}
     [:div (stylefy/use-style style/transit-changes-legend-icon)
      [icon]
      [:div (stylefy/use-style style/change-icon-value) label]])])

(defn format-range [{:keys [lower upper lower-inclusive? upper-inclusive?]}]
  (if (and (nil? lower) (nil? upper))
    "0"
    (str lower "\u2014" (when upper
                          (if upper-inclusive?
                            upper
                            (inc upper))))))

(defn change-icons [{:gtfs/keys [added-trips removed-trips trip-stop-sequence-changes trip-stop-time-changes]}]
  (let [seq-changes (format-range trip-stop-sequence-changes)
        time-changes (format-range trip-stop-time-changes)]
    [:div.transit-change-icons
     [:div (stylefy/use-style style/transit-changes-legend-icon)
      [ic/content-add-circle-outline {:color (if (= 0 added-trips)
                                               style/no-change-color
                                               style/add-color)}]
      [:div (stylefy/use-style style/change-icon-value) added-trips]]
     [:div (stylefy/use-style style/transit-changes-legend-icon)
      [ic/content-remove-circle-outline {:color (if (= 0 removed-trips)
                                                  style/no-change-color
                                                  style/remove-color)}]
      [:div (stylefy/use-style style/change-icon-value) removed-trips]]

     [:div (stylefy/use-style style/transit-changes-legend-icon)
      [ic/action-timeline {:color (when (= "0" seq-changes)
                                    style/no-change-color)}]
      [:div (stylefy/use-style style/change-icon-value)
       seq-changes]]

     [:div (stylefy/use-style style/transit-changes-legend-icon)
      [ic/action-query-builder {:color (when (= "0" time-changes)
                                         style/no-change-color)}]
      [:div (stylefy/use-style style/change-icon-value)
       time-changes]]]))



(defn section [title help-content body-content]
  [ui/card
   [ui/card-title {:title title}]
   [ui/card-header {:subtitle help-content}]
   [ui/card-text
    body-content]])

(defn route-changes [e! route-changes selected-route]
  [section
   "Reitit"
   "Taulukossa on listattu valitussa palvelussa havaittuja muutoksia. Voit valita listalta yhden reitin kerrallaan tarkasteluun. Valitun reitin reitti- ja aikataulutiedot näytetään taulukon alapuolella kalenterissa, kartalla, vuorolistalla ja pysäkkiaikataululistalla."
   [:div.route-changes
    [route-changes-legend]
    [table/table {:no-rows-message "Ei reittejä"
                  :height 300
                  :name->label str
                  :show-row-hover? true
                  :on-select #(when (first %)
                                (e! (tv/->SelectRouteForDisplay (first %))))
                  :row-selected? #(= % selected-route)}
     [{:name "Reitti" :width "30%"
       :read (juxt :gtfs/route-short-name :gtfs/route-long-name)
       :format (fn [[short long]]
                 (str short " " long))}
      {:name "Otsatunnus" :width "20%"
       :read :gtfs/trip-headsign}

      {:name "Aikaa 1:seen muutokseen"
       :width "20%"
       :read :gtfs/change-date
       :format (fn [change-date]
                 (if-not change-date
                   [:div
                    [ic/navigation-check]
                    [:div (stylefy/use-style style/change-icon-value)
                     "Ei muutoksia"]]
                   [:span
                    (str (time/days-until change-date) " pv")
                    [:span (stylefy/use-style {:margin-left "5px"
                                               :color "gray"})
                     (str  "(" (time/format-timestamp->date-for-ui change-date) ")")]]))}

      {:name "Muutokset" :width "30%"
       :read identity
       :format (fn [{change-type :gtfs/change-type :as route-changes}]
                 (case change-type
                   :added
                   [:div [ic/content-add-circle-outline {:color style/add-color}]
                    [:div (stylefy/use-style style/change-icon-value)
                     "Uusi reitti"]]

                   :removed
                   [:div [ic/content-remove-circle-outline {:color style/remove-color}]
                    [:div (stylefy/use-style style/change-icon-value)
                     "Päättyvä reitti"]]

                   :no-change
                   [:div [ic/navigation-check]
                    [:div (stylefy/use-style style/change-icon-value)
                     "Ei muutoksia"]]

                   :changed
                   [change-icons route-changes]))}]

     route-changes]]])

(defn route-service-calendar [e! {:keys [date->hash hash->color
                                         show-previous-year? show-next-year?
                                         compare]}]
  (let [current-year (time/year (time/now))]
    [:div.route-service-calendar
     [section
      "Kalenteri"
      "Valitut päivät on korostettu sinisellä ja lilalla taustavärillä. Oletuksiksi valitut päivämäärät ovat reitin ensimmäinen tunnistettu muutospäivä sekä vastaava viikonpäivä kuluvalta viikolta. Ne kalenteripäivät, joiden pysäkkiketjut ja aikataulut ovat keskenään samanlaiset, on väritetty samalla taustavärillä kokonaisuuden hahmottamiseksi. Taustaväreillä ei ole muita merkityksiä. Kaikki NAP-palvelun havaitsemat päivät, jolloin liikennöinnissä tapahtuu muutoksia suhteessa edellisen viikon vastaavaan viikonpäivään, on merkitty mustilla kehyksillä. Voit myös valita kalenterista itse päivät, joiden reitti- ja aikatauluja haluat vertailla. Valinta tehdään napsauttamalla haluttuja päiviä kalenterista."
      [:div.route-service-calendar-content

       [:div (stylefy/use-style (style-base/flex-container "row"))
        [ui/checkbox {:label "Näytä myös edellinen vuosi"
                      :checked show-previous-year?
                      :on-check #(e! (tv/->ToggleShowPreviousYear))}]
        [ui/checkbox {:label "Näytä myös tuleva vuosi"
                      :checked show-next-year?
                      :on-check #(e! (tv/->ToggleShowNextYear))}]]

       [service-calendar/service-calendar {:selected-date? (constantly false)
                                           :on-select :D
                                           :day-style (r/partial day-style hash->color date->hash
                                                                 (:date1 compare) (:date2 compare))
                                           :years (vec (concat (when show-previous-year?
                                                                 [(dec current-year)])
                                                               [current-year]
                                                               (when show-next-year?
                                                                 [(inc current-year)])))}]

       [:h3 "Valittujen päivämäärien väliset muutokset"]
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
         (time/format-date (:date1 compare))]
        [:div [:div {:style (merge {:display "inline-block"
                                    :position "relative"
                                    :top "5px"
                                    :margin-right "0.5em"
                                    :width "20px"
                                    :height "20px"}
                                   (style/date2-highlight-style))}] (time/format-date (:date2 compare))]]
       ]]]))

(defn route-trips [e! {:keys [trips date1 date2]}]
  [section
   "Vuorot"
   "Vuorolistalla näytetään valitsemasi reitin ja päivämäärien mukaiset vuorot. Sarakkeissa näytetään reitin lähtö- ja päätepysäkkien lähtö- ja saapumisajankohdat. Muutokset-sarakkeessa näytetään reitillä tapahtuvat muutokset vuorokohtaisesti. Napsauta haluttu vuoro listalta nähdäksesi pysäkkikohtaiset aikataulut ja mahdolliset muutokset Pysäkit-osiossa."
   [:div.route-trips
    "tännepä vuorolista ja muutokset"]
   ])

(defn- selected-route-map [_ _ _ {show-stops? :show-stops?}]
  (let [show?-atom (atom show-stops?)
        inhibit-zoom (atom false)
        update (fn [this]
                 (.log js/console "UPDATE, inhibit-zoom? " @inhibit-zoom)
                 (update-marker-visibility this show?-atom)

                 (when-not @inhibit-zoom
                   (leaflet/update-bounds-from-layers this))
                 (reset! inhibit-zoom false))
        zoom-level (r/atom 5)]
    (r/create-class
     {:component-did-update (fn [this]
                              (.log js/console "UPDATE!" this)
                              (update this))
      :component-did-mount (fn [this]
                             (.log js/console "MOUNTED!" this)
                             (let [^js/L.map m (aget this "refs" "leaflet" "leafletElement")]
                               (.on m "zoomend" #(do
                                                   (reset! inhibit-zoom true)
                                                   (reset! zoom-level (.getZoom m)))))
                             (update this))
      :component-will-receive-props
      (fn [this [_ _ _ _ {show-stops? :show-stops?}]]
        ;; This is a bit of a kludge, but because the stops are in the
        ;; same GeoJSON layer as the lines, we can't easily control their
        ;; visibility using react components.
        (when (not= @show?-atom show-stops?)
          ;; Don't zoom if we changed stops visible/hidden toggle
          (reset! inhibit-zoom true))
        (reset! show?-atom show-stops?))
      :reagent-render
      (fn [e! date->hash hash->color {:keys [route-short-name route-long-name
                                             date1 date1-route-lines date1-show?
                                             date2 date2-route-lines date2-show?
                                             show-stops?]}]
        (let [show-date1? (and date1-show?
                               (not (empty? (get date1-route-lines "features"))))
              show-date2? (and date2-show?
                               (not (empty? (get date2-route-lines "features"))))
              zoom @zoom-level
              [line-weight offset icon-size] (cond
                                               (< zoom 12) [3 2 [10 10]]
                                               (< zoom 14) [5 2 [14 14]]
                                               :default    [6 3 [20 20]])]
          [:div.transit-visualization-route-map {:style {:z-index 99 :position "relative"}}

           [ui/checkbox {:label "Näytä pysäkit"
                         :checked (boolean show-stops?)
                         :on-check #(e! (tv/->ToggleRouteDisplayStops))}]
           [leaflet/Map {:ref "leaflet"
                         :center #js [65 25]
                         :zoomControl true
                         :zoom 5}
            (leaflet/background-tile-map)
            (when show-date1?
              ^{:key (str date1 "_" route-short-name "_" route-long-name "_" zoom)}
              [leaflet/GeoJSON {:data date1-route-lines
                                :onEachFeature (initialize-route-features (- offset) icon-size)
                                :style {:lineJoin "miter"
                                        :lineCap "miter"
                                        :color "black"
                                        :weight line-weight}}])
            (when show-date2?
              ^{:key (str date2 "_" route-short-name "_" route-long-name "_" zoom)}
              [leaflet/GeoJSON {:data date2-route-lines
                                :onEachFeature (initialize-route-features offset icon-size)
                                :style {:lineJoin "miter"
                                        :lineCap "miter"
                                        :color "red"
                                        :weight line-weight}}])]]))})))

(defn selected-route-map-section [e! date->hash hash->color compare]
  [section
   "Kartta"
   "Kartalla näytetään valitsemasi reitin ja päivämäärien mukainen pysäkkiketju sekä ajoreitti, mikäli se on saatavilla."
   [selected-route-map e! date->hash hash->color compare]])

(defn transit-visualization [e! {:keys [hash->color date->hash loading? highlight service-info
                                        changes selected-route compare]
                                 :as transit-visualization}]
  [:div
   (when (not loading?)
     [:div.transit-visualization

      [:h1 "Reittiliikenteen tunnistetut muutokset"]

      [:h2 (:transport-service-name service-info) " (" (:transport-operator-name service-info) ")"]
      ;; Route listing with number of changes
      [route-changes e! (:gtfs/route-changes changes) selected-route]

      (when selected-route
        [:div.transit-visualization-route
         [:h3 "Valittu reitti: "
          (:gtfs/route-short-name selected-route) " "
          (:gtfs/route-long-name selected-route)
          " (" (:gtfs/trip-headsign selected-route) ")"]

         (when (and hash->color date->hash)
           [:span
            [route-service-calendar e! transit-visualization]
            [selected-route-map-section e! date->hash hash->color compare]])])
      #_[days-to-diff-info e! transit-visualization highlight]
      #_[:h3 operator-name]
      #_[highlight-mode-switch e! highlight]
      #_[calendar-style-switch e! transit-visualization]
      #_[service-calendar/service-calendar (merge {:view-mode (:calendar-mode transit-visualization)
                                                 :selected-date? (constantly false)
                                                 :on-select (r/partial select-day e!)}
                                                (when (get highlight :mode)
                                                  {:on-hover (r/partial hover-day e! date->hash)})
                                                {:day-style (r/partial day-style hash->color date->hash
                                                                       (:highlight transit-visualization))
                                                 :years (or (:years transit-visualization)
                                                            [2017 2018])})]
      #_[date-comparison e! transit-visualization]])])
