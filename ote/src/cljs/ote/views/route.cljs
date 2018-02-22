(ns ote.views.route
  "Main views for route based traffic views"
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [ote.ui.leaflet :as leaflet]
            [ote.app.controller.route :as rc]
            [ote.ui.form-fields :as form-fields]))

(defn route-stepper []
  [ui/stepper {:active-step 1}
   (doall
    (for [s ["Reitin nimi"
             "Reittipysäkit"
             "Vuorot"
             "Kalenteri"
             "Reitin tallennus"]]
      ^{:key s}
      [ui/step
       [ui/step-label s]]))])

(defn- stop-marker [e! point lat-lng]
  (let [marker
        (js/L.circleMarker
         lat-lng
         #js {:radius 8
              :fillColor "green"
              :opacity 1
              :fillOpacity 0.65})]
    (.on marker "click"
         (fn [_]
           (e! (rc/->AddStop point))))))

(defn- route-map [e! {route :route :as app}]
  [:div.stops-map {:style {:width "50%"}}
   [leaflet/Map {:ref "stops-map"
                 :center #js [65 25]
                 :zoomControl true
                 :zoom 5}
    (leaflet/background-tile-map)
    (when-let [stops (get-in app [:route :stops])]
      [leaflet/GeoJSON {:data stops
                        :style {:color "green"}
                        :pointToLayer (partial stop-marker e!)}])
    (when-let [stop-sequence (seq (:stop-sequence route))]
      [leaflet/Polyline
       {:positions (clj->js (mapv (comp (fn [[c1 c2]]
                                          [c2 c1]) :coordinates) stop-sequence))
        :color "red"}])]])

(defn new-route [e! _]
  (e! (rc/->LoadStops))
  (fn [e! {route :route :as app}]
    [:div.route
     [route-stepper]
     [:div {:style {:display "flex" :flex-direction "row"}}
      [route-map e! app]
      [:div {:style {:width "50%"}}
       [form-fields/field
        {:name :stop-sequence
         :type :table
         :update! #(e! (rc/->UpdateStops %))
         :table-fields [{:label "Satama"
                         :name :port-name
                         :type :component
                         :component (fn [{name :data}] [:span name])}
                        {:label "Saapumisaika"
                         :name :arrival-time
                         :type :time}
                        {:label "Lähtöaika"
                         :name :departure-time
                         :type :time}]}
        (:stop-sequence route)]]]]))
