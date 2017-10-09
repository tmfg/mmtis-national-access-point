(ns ote.views.place-search
  "View for searching for places and showing them on the map."
  (:require [reagent.core :as r]
            [ote.app.place-search :as ps]
            [ote.ui.form-fields :as form-fields]
            [ote.ui.napit :as napit]
            [ote.ui.debug :as debug]
            [ote.ui.leaflet :as leaflet]
            [ote.ui.form :as form]
            [ote.localization :refer [tr]]
            [cljs-react-material-ui.reagent :as ui]
            [ote.db.transport-service :as t-service]
            [ote.db.places :as places]))

(defn result-chips [e! results search-component]
  [:div.place-search-results {:style {:display "flex" :flex-wrap "wrap"}}
   (for [{::places/keys [name id] :as result} results]
     ^{:key id}
     [ui/chip {:style {:margin 4}
               :on-request-delete #(e! (ps/->RemovePlaceById id))} name])])

(defn result-geometry [{::places/keys [name location]}]
  [leaflet/FeatureGroup
   [leaflet/geometry {:color "green"
                      :dashArray "5,5"} location]
   [leaflet/Popup [:div name]]])

(defn places-map [e! results]
  (r/create-class
   {:should-component-update
    ;; Do NOT rerender unless the geometries have changed
    (fn [_ [_ _ old-results] [_ _ new-results]]
      (not (identical? old-results new-results)))
    :reagent-render
    (fn [e! results]
      (.log js/console "places map rendering")
      [leaflet/Map {:center #js [65 25]
                    :zoom 10}
       [leaflet/TileLayer {:url "http://{s}.tile.osm.org/{z}/{x}/{y}.png"
                           :attribution "&copy; <a href=\"http://osm.org/copyright\">OpenStreetMap</a> contributors"}]

       (for [{id ::places/id :as result} results]
         ^{:key id}
         [result-geometry result])])}))

(defn place-search [e! place-search]
  (e! (ps/->LoadPlaceNames))
  (fn [e! place-search]
    (let [results (:results place-search)]
      [:div.place-search

       [result-chips e! results]

       [ui/auto-complete {:floating-label-text (tr [:place-search :place-auto-complete])
                          :dataSource (clj->js (or (:names place-search) []))
                          :on-update-input #(e! (ps/->SetPlaceName %))
                          :search-text (or (:name place-search) "")
                          :on-new-request #(e! (ps/->AddPlace %))}]

       [places-map e! results]])))

(defn place-search-form-group [e! label name]
  (form/group
   {:label label
    :columns 3}
   {:type :component
    :name name
    :component (fn [{data :data}]
                 [place-search e! (:place-search data)])}))
