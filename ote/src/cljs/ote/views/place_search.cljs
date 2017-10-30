(ns ote.views.place-search
  "View for searching for places and showing them on the map."
  (:require [reagent.core :as r]
            [ote.app.controller.place-search :as ps]
            [ote.ui.form-fields :as form-fields]
            [ote.ui.napit :as napit]
            [ote.ui.leaflet :as leaflet]
            [ote.ui.form :as form]
            [ote.localization :refer [tr]]
            [cljs-react-material-ui.reagent :as ui]
            [ote.db.transport-service :as t-service]
            [ote.db.places :as places]))

(defn result-chips [e! results]
  [:div.place-search-results {:style {:display "flex" :flex-wrap "wrap"}}
   (for [{::places/keys [namefin type id] editing? :editing? :as result} (map :place results)]
     ^{:key id}
     [:span
      [ui/chip (merge
                {:style {:margin 4}

                 ;; Toggle edit mode when clicking (for hand drawn geometries)
                 :on-click
                 (when (and (= "drawn" type) (not editing?))
                   #(e! (ps/->EditDrawnGeometryName id)))



                 :on-request-delete #(e! (ps/->RemovePlaceById id))})
       namefin]

      ;; FIXME: current version of material-ui (0.19.4) has unconditional
      ;; event cancel if a BACKSPACE key event is fired in a chip
      ;; this breaks the editing input field (can't erase text)
      ;; this has been fixed in latest 1.0 betas.
      ;;
      ;; This means that we can't move this text-field "inside"
      ;; the chip as its content.
      ;;
      (when editing?
        [ui/text-field {:value namefin
                        :floating-label-text (tr [:place-search :rename-place])
                        :on-key-press #(when (= "Enter" (.-key %1))
                                         (e! (ps/->EditDrawnGeometryName id)))
                        :on-change #(e! (ps/->SetDrawnGeometryName id %2))}])])])

(defn result-geometry [{::places/keys [name location]}]
  [leaflet/FeatureGroup
   [leaflet/geometry {:color "green"
                      :dashArray "5,5"} location]
   [leaflet/Popup [:div name]]])


(defn install-draw-control!
  "Install Leaflet draw plugin to to places-map component."
  [e! this]
  (let [m  (aget this "refs" "leaflet" "leafletElement")
        fg (new js/L.FeatureGroup)
        dc (new js/L.Control.Draw #js {:edit #js {:featureGroup fg
                                                  :remove false}})]
    (.addLayer m fg)
    (.addControl m dc)
    (.on m (aget js/L "Draw" "Event" "CREATED")
         #(let [layer (aget % "layer")
                geojson (.toGeoJSON layer)]
            ;;(aset js/window "the_geom" geojson)
            (e! (ps/->AddDrawnGeometry geojson))))))

(defn places-map [e! results]
  (let [feature-group (atom nil)]
    (r/create-class
     {:component-did-mount (partial install-draw-control! e!)
      :reagent-render
      (fn [e! results]
        [leaflet/Map {;;:prefer-canvas true
                      :ref "leaflet"
                      :center #js [65 25]
                      :zoom 5}
         [leaflet/TileLayer {:url "http://{s}.tile.osm.org/{z}/{x}/{y}.png"
                             :attribution "&copy; <a href=\"http://osm.org/copyright\">OpenStreetMap</a> contributors"}]

         [leaflet/FeatureGroup]

         (for [{:keys [place geojson]} results]
           ^{:key (::places/id place)}
           [leaflet/GeoJSON {:data geojson
                             :style {:color "green"}}])])})))

(defn marker-map [e! coordinate]
  (.log js/console "rendering marker map coordinate->"coordinate)
  [leaflet/Map {
                :center #js [65.01212149716532 25.47065377235413]
                :zoom 16
                :on-click  #(e! (ps/->SetMarker %))
                }
   [leaflet/TileLayer {:url "http://{s}.tile.osm.org/{z}/{x}/{y}.png"
                       :attribution "&copy; <a href=\"http://osm.org/copyright\">OpenStreetMap</a> contributors"}]

   [leaflet/Marker
    {:position [(first (get-in coordinate  [:coordinates  :coordinates]))
                (second (get-in coordinate  [:coordinates  :coordinates]))]}]])

(defn- completions [completions]
  (apply array
         (map (fn [{::places/keys [id namefin type]}]
                #js {:text namefin
                     :id id
                     :value (r/as-element
                             [ui/menu-item {:primary-text namefin}])})
              completions)))

(defn place-search [e! place-search]
  (let [results (:results place-search)]
    [:div.place-search

     [result-chips e! results]

     [ui/auto-complete {:floating-label-text (tr [:place-search :place-auto-complete])
                        :filter (constantly true) ;; no filter, backend returns what we want
                        :dataSource (completions (:completions place-search))
                        :on-update-input #(e! (ps/->SetPlaceName %))
                        :search-text (or (:name place-search) "")
                        :on-new-request #(e! (ps/->AddPlace (aget % "id")))}]

     [places-map e! results]]))

(defn place-search-form-group [e! label name]
  (form/group
   {:label label
    :columns 3}
   {:type :component
    :name name
    :component (fn [{data :data}]
                 [place-search e! (:place-search data)])}))


(defn place-marker [e! coordinates]
  (let [coordinate  coordinates]
    [:div
     [marker-map e! coordinates]]))

(defn place-marker-form-group [e! label name]
  (form/group
    {:label label
     :columns 3}
    {:type :component
     :name name
     :component (fn [{data :data}] [place-marker e! {:coordinates data}])}))
