(ns ote.views.viewer
  "NAP viewer view. Shows a resource embedded in CKAN resource page.
  Loads GeoJSON data from given URL (proxied by our backend) and
  displays a map and data from the geojson file."
  (:require [ote.app.controller.viewer :as v]))

(defn viewer [e! app]
  (e! (v/->StartViewer))
  (fn [e! app]
    [:div "showing you the geojson for " (:url app)]))
