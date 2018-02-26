(ns ote.views.route
  "Main views for route based traffic views"
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [ote.ui.leaflet :as leaflet]
            [ote.app.controller.route :as rc]
            [ote.ui.form-fields :as form-fields]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.service-calendar :as service-calendar]
            [ote.time :as time]
            [ote.ui.form :as form]
            [ote.db.transport-operator :as t-operator]
            [ote.ui.common :as common]))



(def stop-marker-style
  #js {:radius 8
       :fillColor "green"
       :opacity 1
       :fillOpacity 0.65})

(defn- stop-marker [e! point lat-lng]
  (-> lat-lng
      (js/L.circleMarker stop-marker-style)
      (.on  "click"
            (fn [_]
              (e! (rc/->AddStop point))))))

(defn- flip-coords [[c1 c2]]
  [c2 c1])

(defn- route-map [e! route]
  [:div.stops-map {:style {:width "50%"}}
   [leaflet/Map {:ref "stops-map"
                 :center #js [65 25]
                 :zoomControl true
                 :zoom 5}
    (leaflet/background-tile-map)
    (when-let [stops (:stops route)]
      [leaflet/GeoJSON {:data stops
                        :style {:color "green"}
                        :pointToLayer (partial stop-marker e!)}])
    (when-let [stop-sequence (seq (:stop-sequence route))]
      [leaflet/Polyline
       {:positions (clj->js (mapv (comp flip-coords :coordinates) stop-sequence))
        :color "red"}])]])

(defn- route-stop-times [e! stop-sequence]
  [:div {:style {:width "50%" :margin "1em"}}
   [:table {:style {:width "100%"}}
    [:thead {:style {:text-align "left"}}
     [:tr
      [:th {:style {:width "50%"}} "Satama"]
      [:th {:style {:width "20%" :text-align "center"}} "Saapumisaika"]
      [:th {:style {:width "20%" :text-align "center"}} "Lähtöaika"]
      [:th {:style {:width "10%"}} ""]]]
    [:tbody {:style {:text-align "left"}}
     (doall
      (map-indexed
       (fn [i {:keys [port-id port-name arrival-time departure-time]}]
         ^{:key port-id}
         [:tr {:style {:border-bottom "solid 1px black"}}
          [:td port-name]
          [:td {:style {:text-align "center"}}
           (if (zero? i)
             "-"
             [form-fields/field
              {:type :time
               :update! #(e! (rc/->UpdateStop i {:arrival-time %}))}
              arrival-time])]
          [:td {:style {:text-align "center"}}
           (if (= (inc i) (count stop-sequence))
                 "-"
                 [form-fields/field
                  {:type :time
                   :update! #(e! (rc/->UpdateStop i {:departure-time %}))}
                  departure-time])]
          [:td [ui/icon-button {:on-click #(e! (rc/->DeleteStop i))}
                [ic/action-delete]]]])
       stop-sequence))]
    (when (empty? stop-sequence)
      [:tbody
       [:tr
        [:td {:colSpan 4}
         [common/help "Valitse reitin pysäkkiketju klikkaamalla pysäkkejä kartalta."]]]])]])

(defn route-basic-info [e! app]
  ;; Initially select the first operator
  (e! (rc/->EditRoute
       {:transport-operator (:transport-operator
                             (first (:transport-operators-with-services app)))}))
  (fn [e! {route :route :as app}]
    [:div.route-basic-info
     [form/form {:update! #(e! (rc/->EditRoute %))}
      [(form/group
        {:label "Reitin nimi ja palveluntuottaja"
         :columns 2
         :layout :row}
        {:name :name
         :type :string
         :label "Reitin nimi"
         :required? true}
        {:name :transport-operator
         :type :selection
         :label "Palveluntuottaja"
         :options (map :transport-operator
                       (:transport-operators-with-services app))
         :show-option ::t-operator/name})]
      route]]))

(defn route-stop-sequence [e! {route :route :as app}]
  [:div {:style {:display "flex" :flex-direction "row"}}
   [route-map e! route]
   [route-stop-times e! (:stop-sequence route)]])

(defn route-times [e! {route :route :as app}]
  (let [stop-sequence (:stop-sequence route)
        first-departure (:departure-time (first stop-sequence))
        first-arrival (:arrival-time (last stop-sequence))
        route-duration (time/minutes-elapsed
                        first-departure
                        first-arrival)
        stop-count (count stop-sequence)
        times (rc/route-times route)]

    [:div.route-times
     [:table {:style {:text-align "center"}}
      [:thead
       [:tr
        (doall
         (for [{:keys [port-id port-name]} stop-sequence]
           ^{:key port-id}
           [:th {:colSpan 2} port-name]))]
       [:tr
        (doall
         (for [{:keys [port-id port-name]} stop-sequence]
           (list
            ^{:key (str port-id "-arr")}
            [:th {:style {:font-size "80%" :font-variant "small-caps"}} "tulo"]
            ^{:key (str port-id "-dep")}
            [:th {:style {:font-size "80%" :font-variant "small-caps"}} "lähtö"])))]]
      [:tbody
       (doall
        (map-indexed
         (fn [i {stops :stops :as time}]
           ^{:key i}
           [:tr
            (map-indexed
             (fn [j {:keys [arrival-time departure-time] :as stop}]
               (let [style {:style {:padding-left "5px"
                                    :padding-right "5px"
                                    :width "75px"
                                    :background-color (if (even? j)
                                                        "#f4f4f4"
                                                        "#fafafa")}}]
                 (list
                  (if (zero? j)
                    ^{:key (str j "-first")}
                    [:td style " - "]
                    ^{:key (str j "-arr")}
                    [:td style [form-fields/field {:type :time} arrival-time]])
                  (if (= j (dec stop-count))
                    ^{:key (str j "-last")}
                    [:td style " - "]
                    ^{:key (str j "-dep")}
                    [:td style [form-fields/field {:type :time} departure-time]])
                  )))
             stops)])
         times))]]
     [:div
      "Uuden vuoron lähtöaika: "
      [form-fields/field {:type :time
                          :update! #(e! (rc/->NewStartTime %))} (:new-start-time route)]
      [ui/raised-button {:primary true
                         :disabled (not (:new-start-time route))
                         :on-click #(e! (rc/->AddRouteTime))}
       "Lisää vuoro"]
      #_[:div "tässä vuorot, kestää " route-duration " minuuttia"]]]))

(defn route-service-calendar [e! {route :route :as route}]
  [service-calendar/service-calendar
   {:selected-date? (fn [d]
                      (let [selected (or (:dates route) #{})
                            df (time/date-fields d)]
                        (selected df)))
    :on-select #(e! (rc/->ToggleDate %))}])

(defn route-save [e! {route :route :as app}]
  [:div "tässä tallennellaan"])

(defn- valid-stop-sequence? [{:keys [stop-sequence]}]
  (and (not (empty? stop-sequence))
       (:departure-time (first stop-sequence))
       (:arrival-time (last stop-sequence))))

(def wizard-steps
  [{:name :basic-info :label "Reitin nimi" :component route-basic-info :validate form/valid?}
   {:name :stop-sequence :label "Reittipysäkit" :component route-stop-sequence :validate valid-stop-sequence?}
   {:name :times :label "Vuorot" :component route-times}
   {:name :calendar :label "Kalenteri" :component route-service-calendar}
   {:name :save :label "Reitin tallennus" :component route-save}])

(defn- route-wizard [e! wizard-steps {route :route :as app}]
  (let [step (or (:step route) (:name (first wizard-steps)))

        [i {:keys [component validate] :as current-step}]
        (first (keep-indexed
                (fn [i s]
                  (when (= step (:name s))
                    [i s]))
                wizard-steps))
        validate (or validate (constantly true))

        previous-step (some (fn [[prev current]]
                              (when (= step (:name current))
                                (:name prev)))
                            (partition-all 2 1 wizard-steps))
        next-step (some (fn [[current next]]
                          (when (= step (:name current))
                            (:name next)))
                        (partition-all 2 1 wizard-steps))]
    [:div.route
     [ui/stepper {:active-step i}
      (doall
       (for [{label :label} wizard-steps]
         ^{:key label}
         [ui/step
          [ui/step-label label]]))]

     [component e! app]

     [:div.route-wizard-navigation
      {:style {:display "flex"
               :justify-content "space-between"}}
      (if (= step (:name (first wizard-steps)))
        [:span]
        [ui/flat-button {:primary true
                         :on-click #(e! (rc/->GoToStep previous-step))
                         :icon (ic/navigation-arrow-back)
                         :label-position "before"}
         "Edellinen"])
      (when (not= step (:name (last wizard-steps)))
        [ui/flat-button {:primary true
                         :disabled (not (validate route))
                         :on-click #(e! (rc/->GoToStep next-step))
                         :icon (ic/navigation-arrow-forward)}
         "Seuraava"])]]))

(defn new-route [e! _]
  (e! (rc/->LoadStops))
  (fn [e! {route :route :as app}]
    (let [page (or (:page route) 0)]
      [route-wizard
       e! wizard-steps
       app])))
