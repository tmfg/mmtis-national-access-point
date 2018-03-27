(ns ote.views.route.stop-sequence
  "Route wizard: defining a stop sequence"
  (:require [ote.ui.leaflet :as leaflet]
            [ote.app.controller.route.route-wizard :as rw]
            [ote.ui.form-fields :as form-fields]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.common :as common]
            [ote.db.transit :as transit]
            [reagent.core :as r]
            [ote.ui.leaflet-draw :as leaflet-draw]
            [clojure.string :as str]
            [ote.localization :refer [tr tr-key]]))


(def stop-marker-style
  #js {:radius 8
       :fillColor "green"
       :opacity 1
       :fillOpacity 0.65})

(defn- stop-marker [e! point lat-lng]
  (-> lat-lng
      (js/L.marker #js {:opacity 0.7
                        :title (aget point "properties" "name")})
      (.on  "click"
            (fn [_]
              (e! (rw/->AddStop point))))))

(defn- flip-coords [[c1 c2]]
  [c2 c1])

(defn- custom-stop-dialog [e! route]
  (when (:custom-stop-dialog route)
    [ui/dialog
     {:open true
      :modal true
      :title (tr [:route-wizard-page :stop-sequence-custom-dialog-title])
      :actions [(r/as-element
                 [ui/flat-button
                  {:label (tr [:route-wizard-page :stop-sequence-custom-dialog-add])
                   :primary true
                   :on-click #(e! (rw/->CloseCustomStopDialog))}])]}
     [:span
      [form-fields/field {:type :string
                          :label (tr [:route-wizard-page :stop-sequence-custom-dialog-name])
                          :update! #(e! (rw/->UpdateCustomStop {:name %}))
                          :on-enter #(e! (rw/->CloseCustomStopDialog))}
       (-> route :custom-stops last :name)]
      [common/help (tr [:route-wizard-page :stop-sequence-custom-dialog-help])]]]))

;;;;;;
;; Unfortunately controlling a stateful Leaflet Draw control is difficult.
;; We need to reuse an instance if the map is unmounted and a new map created.
;; Keep track of things here, we don't want this in the app state.
(def leaflet-draw-control (atom nil))
(def deleting? (atom false))

(defn- route-map [e! route]
  (r/create-class
   {:component-did-mount
    (fn [this]
      (let [^js/L.Map
            m (aget this "refs" "stops-map" "leafletElement")

            fg (js/L.FeatureGroup.)

            draw-opts
            {:add? true
             :ref-name "stops-map"
             :feature-group fg
             :on-control-created
             #(do (reset! leaflet-draw-control
                          {:control %
                           :feature-group fg})
                  (swap! rw/clear-ui-state conj
                         (fn []
                           ;; Clear on init route so that draw control is recreated
                           ;; when a new route is being edited.
                           (reset! leaflet-draw-control nil))))

             ;; Disable all other geometry types
             :disabled-geometry-types #{:circle :circlemarker :rectangle :polyline :polygon}
             :on-create (fn [^js/L.Path layer]
                          (let [id (leaflet-draw/layer-id layer)]
                            (.on layer "click"
                                 (fn [_]
                                   (when-not @deleting?
                                     (e! (rw/->AddCustomStop id)))))
                            (e! (rw/->CreateCustomStop id (leaflet-draw/layer-geojson layer)))))
             :on-remove #(e! (rw/->RemoveCustomStop (leaflet-draw/layer-id %)))
             :on-edit #(e! (rw/->UpdateCustomStopGeometry
                            (leaflet-draw/layer-id %)
                            (leaflet-draw/layer-geojson %)))

             :add-features? true
             :localization {:toolbar {:buttons {:marker (tr [:route-wizard-page :stop-sequence-leaflet-button-marker])}}
                            :handlers {:marker {:tooltip {:start (tr [:route-wizard-page :stop-sequence-leaflet-button-start])}}}}}]

        ;; Keep track if we are in delete mode
        (.on m "draw:deletestart" #(reset! deleting? true))
        (.on m "draw:deletestop" #(reset! deleting? false))

        (if-let [dc @leaflet-draw-control]
          ;; Reuse existing
          (do
            (.addLayer m (:feature-group dc))
            (.addControl m (:control dc))
            (leaflet-draw/install-map-event-handlers! m draw-opts))

          ;; Create and install a new control
          (leaflet-draw/install-draw-control! this draw-opts))))

    :component-will-unmount
    (fn [this]
      (let [^js/L.Map
            m (aget this "refs" "stops-map" "leafletElement")
            {:keys [control feature-group]} @leaflet-draw-control]
        (.removeControl m control)
        (.removeLayer m feature-group)))

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

        (when-let [stop-sequence (seq (::transit/stops route))]
          [leaflet/Polyline
           {:positions (clj->js (mapv (comp flip-coords ::transit/location) stop-sequence))
            :color "red"}])]])}))

(defn- route-stop-times [e! stop-sequence]
  [:div {:style {:width "50%" :margin "1em"}}
   [:table {:style {:width "100%"}}
    [:thead {:style {:text-align "left"}}
     [:tr
      [:th {:style {:width "50%"}} (tr [:route-wizard-page :stop-sequence-port-header])]
      [:th {:style {:width "20%" :text-align "center"}} (tr [:route-wizard-page :stop-sequence-arrival-header])]
      [:th {:style {:width "20%" :text-align "center"}} (tr [:route-wizard-page :stop-sequence-departure-header])]
      [:th {:style {:width "10%"}} ""]]]
    [:tbody {:style {:text-align "left"}}
     (doall
      (map-indexed
       (fn [i {::transit/keys [code name arrival-time departure-time]}]
         ^{:key (str code "_" i)}
         [:tr {:style {:border-bottom "solid 1px black"}}
          [:td name]
          [:td {:style {:text-align "center"}}
           (if (zero? i)
             "-"
             [form-fields/field
              {:type :time
               :update! #(e! (rw/->UpdateStop i {::transit/arrival-time %}))}
              arrival-time])]
          [:td {:style {:text-align "center"}}
           (if (= (inc i) (count stop-sequence))
                 "-"
                 [form-fields/field
                  {:type :time
                   :update! #(e! (rw/->UpdateStop i {::transit/departure-time %}))}
                  departure-time])]
          [:td [ui/icon-button {:on-click #(e! (rw/->DeleteStop i))}
                [ic/action-delete]]]])
       stop-sequence))]
    (when (empty? stop-sequence)
      [:tbody
       [:tr
        [:td {:colSpan 4}
         [common/help (tr [:route-wizard-page :stop-sequence-map-help])]]]])]])

(defn stop-sequence [e! {route :route :as app}]
  (e! (rw/->LoadStops))
  (fn [e! {route :route :as app}]
    (if (nil? (get route :stops))
      [:div.loading [:img {:src "/base/images/loading-spinner.gif"}]]
      [:div {:style {:display "flex" :flex-direction "row"}}
        [route-map e! route]
        [route-stop-times e! (::transit/stops route)]])))
