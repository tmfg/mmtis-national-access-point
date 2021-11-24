(ns ote.views.gtfs-viewer
  "GTFS viewer"
  (:require [clojure.string :as str]
            [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [stylefy.core :refer [use-style]]
            [stylefy.core :as stylefy]
            [ote.gtfs.query :as gq]
            [ote.localization :refer [tr tr-key]]
            [ote.time :as time]
            [ote.ui.table :as table]
            [ote.ui.leaflet :as leaflet]
            [ote.style.base :as style-base]
            [ote.style.transit-changes :as style]
            [ote.app.controller.gtfs-viewer :as gc]
            [ote.ui.page :as page]))

(defn routes-table [e! {:gtfs/keys [agency-txt routes-txt] :as gtfs}]
  (let [agency-by-id (into {} (map (juxt :gtfs/agency-id identity)) agency-txt)]
    [:div.route-section {:style (merge
                                  {:margin-top "2rem"}
                                  (dissoc style/section :padding-bottom))}
     [:div.route-section-title (stylefy/use-style style/section-title) (tr [:gtfs-viewer :gtfs-routes])]
     [:div
      [table/html-table
       (vector {:text (tr [:gtfs-viewer :gtfs-agency])
                :width "37%"}
               {:text (tr [:gtfs-viewer :gtfs-route])
                :width "53%"}
               {:text (tr [:gtfs-viewer :gtfs-trips])
                :width "10%"})
       (mapv
         (fn [c]
           (let [route-trips (gq/route-trips gtfs (:gtfs/route-id c))]
             {:on-click #(e! (gc/->SelectRoute c))
              :data (vector
                      {:text (str (:gtfs/agency-name (agency-by-id (:gtfs/agency-id c))))
                       :width "37%"}
                      {:text (str (:gtfs/route-short-name c) " " (:gtfs/route-long-name c))
                       :width "55%"}
                      {:text (count route-trips)
                       :width "8%"})}))
         routes-txt)
       {:fixed-header true
        :max-height-px 250
        :overflow "auto"}]]]))

(defn stop-popup [stop-id name {:gtfs/keys [stop-times-txt]}]
  [:div
   [:b name]])

(defn trips-map [_ _]
  (r/create-class
    {:component-did-update leaflet/update-bounds-from-layers
     :reagent-render
     (fn [e! {selected-route :selected-route :as gtfs}]
       [:div.container
        [:div.map-section {:style (dissoc style/section :padding-bottom)}
         [:div.map-section-title (stylefy/use-style style/section-title) (tr [:gtfs-viewer :gtfs-map])]
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
                                   :color (if color
                                            color
                                            "#8c4799")}])
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
                 :icon (js/L.divIcon #js {:className "route-stop-icon"
                                          :iconSize  #js [11 11]})}]
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
              (:bearing-markers selected-route)))]]])}))

(defn gtfs-viewer [e! {gtfs :gtfs-viewer}]
  [:div.gtfs-viewer
   (if-let [err (:error-message gtfs)]
     [:div (use-style style-base/error-element)
      err]
     [:div
      [page/page-controls
       ""
       (tr [:gtfs-viewer :gtfs-routes-and-time-tables])
       [routes-table e! gtfs]]
      [trips-map e! gtfs]])])
