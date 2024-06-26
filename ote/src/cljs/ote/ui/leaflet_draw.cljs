(ns ote.ui.leaflet-draw
  "Leaflet draw plugin integration for map"
  (:require [ote.ui.leaflet :as leaflet]
            [ote.localization :refer [tr-tree]]))

(defn layer-id [^js/L.path layer]
  (aget layer "__LAYER_ID"))

(defn- set-layer-id! [^js/L.Path layer]
  (aset layer "__LAYER_ID" (name (gensym "draw-layer-"))))

(defn layer-geojson [^js/L.Path layer]
  (.toGeoJSON layer))

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

  :add?       If true, add control to map directly (defaults to false).

  :localization
              A nested tree of localizations to override.

  :on-remove  Called when a feature is removed
  :add-features?
              If true add features to draw feature group.
              Specify this if you don't have a different
              display for the created features.
              Must be true for delete to work

  :on-edit    Called when a feature is edited.
              If not specified, editing is disabled.

  "
  [this {:keys [on-create disabled-geometry-types ref-name
                on-control-created add? localization
                on-remove add-features? on-edit leaflet-edit-tr-key]}]
  (do
    (set! (.-draw js/L.drawLocal)
          (clj->js (merge-with merge
                               (tr-tree [:leaflet-draw])
                               localization)))
    (when leaflet-edit-tr-key
      (set! (.-edit js/L.drawLocal)
            (clj->js (tr-tree [leaflet-edit-tr-key])))))

  (let [^js/L.map
        m (aget this "refs" (or ref-name "leaflet") "leafletElement")
        fg (new js/L.FeatureGroup)
        draw-opts (clj->js
                   (into {}
                         (zipmap (or disabled-geometry-types
                                     #{:polyline :circlemarker :circle})
                                 (repeat false))))
        draw-control (new js/L.Control.Draw #js
                          {:draw draw-opts
                           :edit #js {:featureGroup fg
                                      :remove (if on-remove
                                                #js {}
                                                false)
                                      :edit (if on-edit
                                              #js {}
                                              false)}})]
    (when on-control-created
      (on-control-created draw-control))
    (when add?
      (.addControl m draw-control))
    (.addLayer m fg)
    (.on m "draw:created"
         #(let [^js/L.Path
                layer (aget % "layer")]
            (set-layer-id! layer)
            (when add-features?
              (.addLayer fg layer))
            (on-create layer)))
    (when on-remove
      (.on m "draw:deleted"
           #(let [^js/L.LayerGroup
                  layers (aget % "layers")]
              (.eachLayer layers on-remove))))
    (when on-edit
      (.on m "draw:edited"
           #(let [^js/L.LayerGroup
                  layers (aget % "layers")]
              (.eachLayer layers on-edit))))))
