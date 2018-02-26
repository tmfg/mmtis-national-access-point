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

(defn route-stepper [page]
  [ui/stepper {:active-step page}
   (doall
    (for [s ["Reitin nimi"
             "Reittipysäkit"
             "Vuorot"
             "Kalenteri"
             "Reitin tallennus"]]
      ^{:key s}
      [ui/step
       [ui/step-label s]]))])

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

(defn route-basic-info [e! route operators]
  ;; Initially select the first operator
  (e! (rc/->EditRoute {:transport-operator (first operators)}))
  (fn [e! route operators]
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
         :options operators
         :show-option ::t-operator/name})]
      route]]))

(defn route-stop-sequence [e! route]
  [:div {:style {:display "flex" :flex-direction "row"}}
   [route-map e! route]
   [route-stop-times e! (:stop-sequence route)]])

(defn route-times [e! route]
  [:div "tässä vuorot"])

(defn route-service-calendar [e! route]
  [service-calendar/service-calendar
   {:selected-date? (fn [d]
                      (let [selected (or (:dates route) #{})
                            df (time/date-fields d)]
                        (selected df)))
    :on-select #(e! (rc/->ToggleDate %))}])

(defn route-save [e! route]
  [:div "tässä tallennellaan"])

(defn new-route [e! _]
  (e! (rc/->LoadStops))
  (fn [e! {route :route :as app}]
    (let [page (or (:page route) 0)]
      [:div.route
       [route-stepper page]
       (case page
         0 [route-basic-info e! route (map :transport-operator
                                           (:transport-operators-with-services app))]
         1 [route-stop-sequence e! route]
         2 [route-times e! route]
         3 [route-service-calendar e! route]
         4 [route-save e! route])
       [:div.route-wizard-navigation
        (when (pos? page)
          [ui/raised-button {:primary true
                             :on-click #(e! (rc/->PreviousStep))}
           "Edellinen"])
        (when (< page 4)
          [ui/raised-button {:primary true
                             :on-click #(e! (rc/->NextStep))}
           "Seuraava"])]])))
