(ns ote.ui.leaflet
  "Wrappers for ReactLeaflet for Reagent"
  (:require [cljsjs.react-leaflet]
            [reagent.core :as r]))


(def Map (r/adapt-react-class js/ReactLeaflet.Map))
(def TileLayer (r/adapt-react-class js/ReactLeaflet.TileLayer))

(def Circle (r/adapt-react-class js/ReactLeaflet.Circle))
(def Marker (r/adapt-react-class js/ReactLeaflet.Marker))
(def Popup (r/adapt-react-class js/ReactLeaflet.Popup))
