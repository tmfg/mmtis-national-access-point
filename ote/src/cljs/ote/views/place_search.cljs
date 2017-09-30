(ns ote.views.place-search
  "View for searching for places and showing them on the map."
  (:require [reagent.core :as r]
            [ote.app.place-search :as ps]
            [ote.ui.form-fields :as form-fields]
            [ote.ui.napit :as napit]
            [ote.ui.debug :as debug]
            [ote.ui.leaflet :as leaflet]))

(defn place-search [e! place-search]
  [:div.place-search
   [leaflet/Map {:center #js [65 25]
                 :zoom 10}
    [leaflet/TileLayer {:url "http://{s}.tile.osm.org/{z}/{x}/{y}.png"
                        :attribution "&copy; <a href=\"http://osm.org/copyright\">OpenStreetMap</a> contributors"}]

    (for [{:keys [namefin geometry]} (get-in place-search [:results :finnish-municipalities])]
      ^{:key namefin}
      [leaflet/FeatureGroup
       [leaflet/geometry {:color "red"} geometry]
       [leaflet/Popup [:div namefin]]])]

   [form-fields/field {:type :string :label "Paikan nimi"
                        :update! #(e! (ps/->SetPlaceName %))}
    (get-in app [:place-search :name])]

   [napit/tallenna {:on-click #(e! (ps/->SearchPlaces))} "Hae paikkoja"]

   [debug/debug (:place-search app)]])
