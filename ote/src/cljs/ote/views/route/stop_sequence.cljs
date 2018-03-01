(ns ote.views.route.stop-sequence
  "Route wizard: defining a stop sequence"
  (:require [ote.ui.leaflet :as leaflet]
            [ote.app.controller.route :as rc]
            [ote.ui.form-fields :as form-fields]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
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

(defn stop-sequence [e! {route :route :as app}]
  [:div {:style {:display "flex" :flex-direction "row"}}
   [route-map e! route]
   [route-stop-times e! (:stop-sequence route)]])
