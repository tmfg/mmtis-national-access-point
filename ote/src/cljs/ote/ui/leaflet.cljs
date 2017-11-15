(ns ote.ui.leaflet
  "Wrappers for ReactLeaflet for Reagent"
  (:require [cljsjs.react-leaflet]
            [reagent.core :as r]))


(def Map (r/adapt-react-class js/ReactLeaflet.Map))
(def TileLayer (r/adapt-react-class js/ReactLeaflet.TileLayer))

(def Circle (r/adapt-react-class js/ReactLeaflet.Circle))
(def Marker (r/adapt-react-class js/ReactLeaflet.Marker))
(def Popup (r/adapt-react-class js/ReactLeaflet.Popup))
(def Polygon (r/adapt-react-class js/ReactLeaflet.Polygon))
(def LayerGroup (r/adapt-react-class js/ReactLeaflet.LayerGroup))
(def FeatureGroup (r/adapt-react-class js/ReactLeaflet.FeatureGroup))
(def GeoJSON (r/adapt-react-class js/ReactLeaflet.GeoJSON))

(defmulti geometry (fn [opts geometry] (:type geometry)))

(defmethod geometry :multipolygon [style-options {polygons :polygons}]
  ;; FIXME: positions should be array where first is outer ring and rest are holes
  [Polygon (merge {:positions (clj->js (map :coordinates polygons))}
                  style-options)])

(defmethod geometry :polygon [style-options {coordinates :coordinates}]
  [Polygon (merge {:positions (clj->js coordinates)}
                  style-options)])



(defn ^:export update-map-bounds-from-layers [^js/L.map leaflet]
  (let [bounds (atom nil)
        add-bounds! (fn [nw se]
                      (let [new-bounds (.latLngBounds js/L nw se)]
                        (if (nil? @bounds)
                          (reset! bounds new-bounds)
                          (.extend @bounds new-bounds))))]
    (.eachLayer
      leaflet
      (fn [layer]
        (cond
          (instance? js/L.Path layer)
          (let [^js/L.path
          path layer
                layer-bounds (.getBounds path)]
            (add-bounds! (.getNorthWest layer-bounds)
                         (.getSouthEast layer-bounds)))

          (instance? js/L.Marker layer)
          (let [^js/L.Marker
          marker layer
                pos (.getLatLng marker)
                d 0.01
                lat (.-lat pos)
                lng (.-lng pos)]
            (add-bounds! (.latLng js/L (- lat d) (- lng d))
                         (.latLng js/L (+ lat d) (+ lng d))))

          ;; do nothing for other types
          :default
          nil)))
    (when-let [bounds @bounds]
      (.fitBounds leaflet bounds))))

(defn update-leaflet-bounds-soon [leaflet]
  (.setTimeout js/window #(update-map-bounds-from-layers leaflet) 50)
  )

(defn update-bounds-from-layers [this]
  (let [^js/L.map
  leaflet (aget this "refs" "leaflet" "leafletElement")]
    (update-leaflet-bounds-soon leaflet)))

(defn update-bounds-on-load [this]
  (let [^js/L.map
  leaflet (aget this "refs" "leaflet" "leafletElement")]
    (.on leaflet "layeradd"
         (fn [m]
           (update-leaflet-bounds-soon leaflet)))))