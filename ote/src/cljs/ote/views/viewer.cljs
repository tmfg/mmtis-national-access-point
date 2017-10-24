(ns ote.views.viewer
  "NAP viewer view. Shows a resource embedded in CKAN resource page.
  Loads GeoJSON data from given URL (proxied by our backend) and
  displays a map and data from the geojson file."
  (:require [ote.app.controller.viewer :as v]
            [ote.ui.leaflet :as leaflet]))

(defn viewer [e! _]
  (e! (v/->StartViewer))
  (fn [e! {:keys [loading? geojson resource] :as app}]
    (if loading?
      [:div.loading
       [:img {:src "/base/images/loading-spinner.gif"}]]

      [:div.transport-service-view
       [leaflet/Map {:center #js [65 25]
                     :zoom 5}
        [leaflet/TileLayer {:url "http://{s}.tile.osm.org/{z}/{x}/{y}.png"
                            :attribution "&copy; <a href=\"http://osm.org/copyright\">OpenStreetMap</a> contributors"}]

        [leaflet/GeoJSON {:data geojson
                          :style {:color "green"}}]]
       [:div "showing you the geojson for " (:url app)]])))
