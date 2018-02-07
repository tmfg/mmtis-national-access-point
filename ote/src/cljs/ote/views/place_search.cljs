(ns ote.views.place-search
  "View for searching for places and showing them on the map."
  (:require [reagent.core :as r]
            [ote.app.controller.place-search :as ps]
            [ote.ui.form-fields :as form-fields]
            [ote.ui.buttons :as buttons]
            [ote.ui.leaflet :as leaflet]
            [ote.ui.form :as form]
            [ote.localization :refer [tr tr-tree]]
            [cljs-react-material-ui.reagent :as ui]
            [ote.db.transport-service :as t-service]
            [ote.db.places :as places]
            [goog.object :as gobj]
            cljsjs.leaflet
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]
            [clojure.string :as str]))

(set! *warn-on-infer* true)

(defn monkey-patch-chip-backspace
  "Pre 1.0 fix for bug in MaterialUI Chip which unconditionally
  cancels a backspace events (causing an embedded input field to
  not be able to erase text."
  [this]
  (let [refs (aget this "refs")]
    (gobj/forEach
     refs
     (fn [chip ref _]
       (when-not (aget chip "__backspace_monkey_patch")
         (let [old (aget chip "handleKeyDown")]
           (aset chip "handleKeyDown"
                 (fn [event]
                   (when-not (= "Backspace" (.-key event))
                     (old event))))
           (aset chip "__backspace_monkey_patch" true)))))))

(defn result-chips [e! results]
  (r/create-class
   {:component-did-mount monkey-patch-chip-backspace
    :component-did-update monkey-patch-chip-backspace
    :reagent-render
    (fn [e! results]
      [:div.place-search-results {:style {:display "flex" :flex-wrap "wrap"}}
       (for [{::places/keys [namefin type id] editing? :editing? :as result} (map :place results)]
         ^{:key id}
         [:span
          [ui/chip {:ref id
                    :style {:margin 4}

                    ;; Toggle edit mode when clicking (for hand drawn geometries)
                    :on-click
                    (if (and (= "drawn" type) (not editing?))
                      #(e! (ps/->EditDrawnGeometryName id))
                      (constantly false))

                    :on-request-delete #(e! (ps/->RemovePlaceById id))}
          (if editing?
            [ui/text-field
             {:value namefin
              :floating-label-text (tr [:place-search :rename-place])
              :on-key-press #(when (= "Enter" (.-key %1))
                               (e! (ps/->EditDrawnGeometryName id)))
              :on-change #(e! (ps/->SetDrawnGeometryName id %2))}]
            namefin)]])])}))

(defn result-geometry [{::places/keys [name location]}]
  [leaflet/FeatureGroup
   [leaflet/geometry {:color "green"
                      :dashArray "5,5"} location]
   [leaflet/Popup [:div name]]])

(defn install-draw-control!
  "Install Leaflet draw plugin to to places-map component."
  [e! this]

  (set! (.-draw js/L.drawLocal) (clj->js (tr-tree [:leaflet-draw])))

  (let [^js/L.map
        m (aget this "refs" "leaflet" "leafletElement")

        fg (new js/L.FeatureGroup)
        dc (new js/L.Control.Draw #js {:draw #js {:circlemarker false
                                                  :circle false}
                                       :edit #js {:featureGroup fg
                                                  :remove false
                                                  :edit false}})]
    (.addLayer m fg)
    (.addControl m dc)
    (.on m (aget js/L "Draw" "Event" "CREATED")
         #(let [^js/L.Path
                layer (aget % "layer")
                geojson (.toGeoJSON layer)]
            ;;(aset js/window "the_geom" geojson)
            (e! (ps/->AddDrawnGeometry geojson))))))


(defn places-map [e! results]
   (r/create-class
     {:component-did-mount #(do
                              (leaflet/customize-zoom-controls e! % {:zoomInTitle (tr [:leaflet :zoom-in])
                                                                     :zoomOutTitle (tr [:leaflet :zoom-out])})
                              (install-draw-control! e! %)
                              (leaflet/update-bounds-from-layers %))
      :component-did-update leaflet/update-bounds-from-layers
      :reagent-render
      (fn [e! results]
        [leaflet/Map {;;:prefer-canvas true
                      :ref "leaflet"
                      :center #js [65 25]
                      :zoomControl false
                      :zoom 5}
         [leaflet/TileLayer {:url "http://{s}.tile.osm.org/{z}/{x}/{y}.png"
                             :attribution "&copy; <a href=\"http://osm.org/copyright\">OpenStreetMap</a> contributors"}]

         ;[leaflet/FeatureGroup]

         (for [{:keys [place geojson]} results]
           ^{:key (::places/id place)}
           [leaflet/GeoJSON {:data geojson
                             :style {:color "green"}}])])}))


(defn marker-map [e! coordinate]
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

     [:div.col-xs-12.col-md-3
     [result-chips e! results]

     [ui/auto-complete {:floating-label-text (tr [:place-search :place-auto-complete])

                        :filter (constantly true) ;; no filter, backend returns what we want
                        :dataSource (completions (:completions place-search))
                        :maxSearchResults 12
                        :on-update-input #(e! (ps/->SetPlaceName %))
                        :search-text (or (:name place-search) "")
                        :on-new-request #(e! (ps/->AddPlace (aget % "id")))}]]
    [:div.col-xs-12.col-md-8
     [places-map e! results]]]))

(defn place-search-form-group [e! label name]
  (let [empty-places? (comp empty? :results :place-search)]
    (form/group
     {:label label
      :columns 3}

     (form/info (tr [:form-help :operation-area]))

     {:type :component
      :name name
      :required? true
      :is-empty? empty-places?
      :component (fn [{data :data}]
                   [:span
                    (when (empty-places? data)
                      [:div (stylefy/use-style style-base/required-element)
                       (tr [:common-texts :required-field])])
                    [place-search e! (:place-search data)]])})))


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
