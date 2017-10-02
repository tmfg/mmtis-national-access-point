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

(defmethod geometry :multipolygon [{color :color} {polygons :polygons}]
  [Polygon {:positions (clj->js (mapcat :coordinates polygons))
            :color color}])
