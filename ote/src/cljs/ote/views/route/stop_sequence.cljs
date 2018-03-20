(ns ote.views.route.stop-sequence
  "Route wizard: defining a stop sequence"
  (:require [ote.ui.leaflet :as leaflet]
            [ote.app.controller.route :as rc]
            [ote.ui.form-fields :as form-fields]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.common :as common]
            [ote.db.transit :as transit]
            [reagent.core :as r]
            [ote.ui.leaflet-draw :as leaflet-draw]
            [clojure.string :as str]))


(def stop-marker-style
  #js {:radius 8
       :fillColor "green"
       :opacity 1
       :fillOpacity 0.65})

(defn- stop-marker [e! point lat-lng]
  (-> lat-lng
      #_(js/L.circleMarker stop-marker-style)
      (js/L.marker #js {:opacity 0.7
                        :title (aget point "properties" "name")})
      (.on  "click"
            (fn [_]
              (e! (rc/->AddStop point))))))

(defn- flip-coords [[c1 c2]]
  [c2 c1])

(defn- custom-stop-dialog [e! route]
  (when (:custom-stop-dialog route)
    [ui/dialog
     {:open true
      :modal true
      :title "Lisää uusi satama tai laituri"
      :actions [(r/as-element
                 [ui/flat-button
                  {:label "Lisää"
                   :primary true
                   :on-click #(e! (rc/->CloseCustomStopDialog))}])]}
     [:span
      [form-fields/field {:type :string
                          :label "Nimi"
                          :update! #(e! (rc/->UpdateCustomStop {:name %}))
                          :on-enter #(e! (rc/->CloseCustomStopDialog))}
       (-> route :custom-stops last :name)]
      [common/help
       "Voit muuttaa laiturin / sataman sijaintia tai poistaa sen karttatyökaluilla.
Reitin tallennuksen jälkeen satamaa / laituria ei voi muokata."]]]))

(defn- route-map [e! route]
  (r/create-class
   {:component-did-mount
    (fn [this]
      (let [deleting? (atom false)
            ^js/L.Map
            m (aget this "refs" "stops-map" "leafletElement")]

        ;; Keep track if we are in delete mode
        (.on m "draw:deletestart" #(reset! deleting? true))
        (.on m "draw:deletestop" #(reset! deleting? false))

        (leaflet-draw/install-draw-control!
            this
          {:add? true
           :ref-name "stops-map"
           ;; Disable all other geometry types
           :disabled-geometry-types #{:circle :circlemarker :rectangle :polyline :polygon}
           :on-create (fn [^js/L.Path layer]
                        (let [id (leaflet-draw/layer-id layer)]
                          (.on layer "click"
                               (fn [_]
                                 (when-not @deleting?
                                   (e! (rc/->AddCustomStop id)))))
                          (e! (rc/->CreateCustomStop id (leaflet-draw/layer-geojson layer)))))
           :on-remove #(e! (rc/->RemoveCustomStop (leaflet-draw/layer-id %)))
           :on-edit #(e! (rc/->UpdateCustomStopGeometry
                          (leaflet-draw/layer-id %)
                          (leaflet-draw/layer-geojson %)))

           :add-features? true
           :localization {:toolbar {:buttons {:marker "Lisää uusi satama/laituri"}}
                          :handlers {:marker {:tooltip {:start "Klikkaa karttaa lisätäksesi satama/laituri"}}}}})))
    :reagent-render
    (fn [e! route]
      [:div.stops-map {:style {:width "50%"}}
       [custom-stop-dialog e! route]
       [leaflet/Map {:ref "stops-map"
                     :center #js [65 25]
                     :zoomControl true
                     :zoom 5}
        (leaflet/background-tile-map)
        (when-let [stops (:stops route)]
          [leaflet/GeoJSON {:data stops
                            :style {:color "green"}
                            :pointToLayer (partial stop-marker e!)}])
        #_(for [s (:custom-stops route)]
          ^{:key (:id s)}
          [leaflet/GeoJSON {:data (:geojson s)
                            :style {:color "green"}
                            :pointToLayer (partial stop-marker e!)}])

        (when-let [stop-sequence (seq (::transit/stops route))]
          [leaflet/Polyline
           {:positions (clj->js (mapv (comp flip-coords ::transit/location) stop-sequence))
            :color "red"}])]])}))

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
       (fn [i {::transit/keys [code name arrival-time departure-time]}]
         ^{:key code}
         [:tr {:style {:border-bottom "solid 1px black"}}
          [:td name]
          [:td {:style {:text-align "center"}}
           (if (zero? i)
             "-"
             [form-fields/field
              {:type :time
               :update! #(e! (rc/->UpdateStop i {::transit/arrival-time %}))}
              arrival-time])]
          [:td {:style {:text-align "center"}}
           (if (= (inc i) (count stop-sequence))
                 "-"
                 [form-fields/field
                  {:type :time
                   :update! #(e! (rc/->UpdateStop i {::transit/departure-time %}))}
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
   [route-stop-times e! (::transit/stops route)]])
