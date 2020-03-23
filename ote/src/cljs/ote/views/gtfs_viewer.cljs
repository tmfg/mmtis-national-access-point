(ns ote.views.gtfs-viewer
  "GTFS viewer"
  (:require [clojure.string :as str]
            [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [stylefy.core :refer [use-style]]
            [ote.gtfs.query :as gq]
            [ote.time :as time]
            [ote.ui.table :as table]
            [ote.ui.leaflet :as leaflet]
            [ote.style.base :as style-base]
            [ote.app.controller.gtfs-viewer :as gc]))

(defn routes-table [e! {:gtfs/keys [agency-txt routes-txt trips-txt]
                        selected-route :selected-route :as gtfs}]
  (let [agency-by-id (into {} (map (juxt :gtfs/agency-id identity)) agency-txt)
        trips-by-route (group-by :gtfs/route-id trips-txt)]
    [:div {:style {:height "300px" :overflow "auto"}}
     [table/html-table
      (vector "Liikennöitsijä" "Linja" "Vuoroja")
      (mapv
        (fn [c]
          (let [;;route-trips (trips-by-route (:gtfs/route-id c))
                route-trips (gq/route-trips gtfs (:gtfs/route-id c))
                _ (.log js/console "route-trips " (count route-trips) (pr-str route-trips))]
            {:on-click #(e! (gc/->SelectRoute c))
             :data (vector
                     (str (:gtfs/agency-name (agency-by-id (:gtfs/agency-id c)))) #_(comp :gtfs/agency-name agency-by-id (:gtfs/agency-id c))
                     (str (:gtfs/route-short-name c) " " (:gtfs/route-long-name c))
                     (count route-trips))}))
        routes-txt)]]))

(defn stop-popup [stop-id name {:gtfs/keys [stop-times-txt]}]
  (let [_ (.log js/console "stop-times-txt " (pr-str stop-times-txt))
        stop-times (for [{arr :gtfs/arrival-time
                          dep :gtfs/departure-time :as st} stop-times-txt
                         :when (and (= (:gtfs/stop-id st) stop-id)
                                    (or arr dep))]
                     (or arr dep))
        _ (.log js/console "stop-popup :: stop-times" (pr-str stop-times))]
    [:div
     [:b name]
     [:ul
      (doall
       (for [st (if (> (count stop-times) 6)
                  (concat (take 3 stop-times)
                          [:ellipsis]
                          (reverse (take 3 (reverse stop-times))))
                  stop-times)
             :let [time st]]
         (if (= :ellipsis st)
           ^{:key "ellipsis"}
           [:ul "\u22ee"]
           ^{:key time}
           [:ul time])))]]))

(defn trips-map [_ _]
  (r/create-class
   {:component-did-update leaflet/update-bounds-from-layers
    :reagent-render
    (fn [e! {selected-route :selected-route :as gtfs}]
      [leaflet/Map {:ref "leaflet"
                    :center #js [65 25]
                    :zoomControl true
                    :zoom 5
                    :style {:height 600}}
       (leaflet/background-tile-map)

       ;; Show polyline for each stop sequence
       (doall
        (map-indexed
         (fn [i {:keys [positions color]}]
           ^{:key i}
           [leaflet/Polyline {:positions positions
                              :color color}])
         (:lines selected-route)))

       ;; Show marker for all stops
       (doall
        (for [{:gtfs/keys [stop-id stop-lat stop-lon stop-name]}
              (:stops selected-route)
              :let [stop-times (get (:stop-times-for-stop selected-route) stop-id)]]
          ^{:key stop-id}
          [leaflet/FeatureGroup
           [leaflet/Marker
            {:position [stop-lat stop-lon]
             :title stop-name
             :icon (js/L.divIcon #js {:className "route-stop-icon"})}]
           [leaflet/Popup {}
            [stop-popup stop-id stop-name gtfs]]]))

       (doall
        (map-indexed
         (fn [i {:keys [position bearing]}]
           ^{:key i}
           [leaflet/Marker
            {:position (clj->js position)
             :icon (js/L.divIcon #js {:className "route-bearing"
                                      :html (str "<div style=\"transform: translateY(-12px) rotate(" bearing "deg);\">^</div>")})}])
         (:bearing-markers selected-route)))])}))

(defn gtfs-viewer [e! {gtfs :gtfs-viewer}]
  [:div.gtfs-viewer
   (if-let [err (:error-message gtfs)]
     [:div (use-style (merge style-base/error-element
                             {:margin-top "40px"}))
      err]
     [:span
      [routes-table e! gtfs]
      [trips-map e! gtfs]])])
