(ns ote.ui.leaflet-draw
  "Leaflet draw plugin integration for map"
  (:require [ote.ui.leaflet :as leaflet]
            [ote.localization :refer [tr-tree]]))

(defn install-draw-control!
   "Install Leaflet draw plugin to `this` map component.

  Options:

  :on-create  Callback to invoke when a new geometry is created.
              The new geometry is given as geojson parameter.

  :ref-name   The ref name of a leaflet Map component, defaults to \"leaflet\".

  :disabled-geometry-types
              Geometry types to disable, defaults to
              #{:polyline :circlemarker :circle}

  :on-control-created
              Callback to call with the newly created L.Control.Draw instance
  "
  [this {:keys [on-create disabled-geometry-types ref-name
                on-control-created]}]

  (set! (.-draw js/L.drawLocal) (clj->js (tr-tree [:leaflet-draw])))

  (let [^js/L.map
        m (aget this "refs" (or ref-name "leaflet") "leafletElement")
        fg (new js/L.FeatureGroup)
        draw-opts (clj->js
                   (into {}
                         (zipmap (or disabled-geometry-types
                                     #{:polyline :circlemarker :circle})
                                 (constantly false))))
        draw-control (new js/L.Control.Draw #js {:draw draw-opts
                                                 :edit #js {:featureGroup fg
                                                            :remove false
                                                            :edit false}})]
    (when on-control-created
      (on-control-created draw-control))
    (.addLayer m fg)
    (.on m (aget js/L "Draw" "Event" "CREATED")
         #(let [^js/L.Path
                layer (aget % "layer")
                geojson (.toGeoJSON layer)]
            ;;(aset js/window "the_geom" geojson)
            (on-create geojson)))))
