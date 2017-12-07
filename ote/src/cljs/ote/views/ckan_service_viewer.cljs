(ns ote.views.ckan-service-viewer
  "NAP viewer view. Shows a resource embedded in CKAN resource page.
  Loads GeoJSON data from given URL (proxied by our backend) and
  displays a map and data from the geojson file."
  (:require [ote.app.controller.ckan-service-viewer :as v]
            [ote.ui.leaflet :as leaflet]
            [ote.ui.common :refer [linkify]]
            [clojure.string :as str]
            [ote.localization :refer [tr tr-or]]
            [stylefy.core :as stylefy]
            [ote.style.ckan :as style-ckan]
            [ote.style.base :as style-base]
            [ote.app.controller.transport-service :as ts]
            [ote.ui.buttons :as buttons]
            [cljs-react-material-ui.reagent :as ui]
            [ote.ui.leaflet :as leaflet]
            [ote.views.theme :refer [theme]]
            [ote.views.place-search :as place-search]
            [reagent.core :as r]
            cljsjs.leaflet))



(defmulti transform-value (fn [key value] key))

(defmethod transform-value "url" [_ value] (linkify value value))
(defmethod transform-value "homepage" [_ value] (linkify value value))
(defmethod transform-value "contact-email" [_ value] (linkify (str "mailto:" value) value))

(defmethod transform-value "lang" [_ value]
  ;; Show unicode country flags for supported languages, otherwise show language code
  (case value
    "FI" "\ud83c\uddeb\ud83c\uddee" ; finnish flag
    "SV" "\ud83c\uddf8\ud83c\uddea" ; swedish flag
    "EN" "\ud83c\uddec\ud83c\udde7" ; united kingdom flag
    value))

(defmethod transform-value :default [_ value]
  (tr-or (tr [:viewer :values value]) (str value)))


(defn effectively-empty? [value]
  (or (nil? value)

      ;; This is a map that is empty or only has effectively empty values
      (and (map? value)
           (or (empty? value)
               (every? effectively-empty? (vals value))))

      ;; This is an empty collection or only has effectively empty values
      (and (coll? value)
           (or (empty? value)
               (every? effectively-empty? value)))

      ;; This is a blank (empty or just whitespace) string
      (and (string? value) (str/blank? value))))

(declare properties-table records-table)

(defn show-value [key value]
  (cond
    ;; This is an object, show key/value table
    (map? value)
    [properties-table value]

    ;; This is a collection of maps, each having the same keys
    ;; we can show this as a table
    (and (coll? value)
         (every? map? value)
         (apply = (map keys value)))
    [records-table value]

    ;; Collection of values
    (coll? value)
    [:span
     (map-indexed
      (fn [i value]
        ^{:key i}
        [:div (stylefy/use-style style-ckan/info-block)
         [show-value "" value]]) value)]

    ;; Other values, like strings and numbers
    :default
    [:span (transform-value key value)]))

(defn properties-table [properties]
   [:table.table.table-striped.table-bordered.table-condensed
     [:tbody
      (for [[key value] (sort-by first properties)
            :when (not (effectively-empty? value))]
        ^{:key key}
        [:tr
         [:th {:scope "row" :width "25%"}
          (tr-or (tr [:viewer key]) key)]
         [:td [show-value key value]]])]])

(defn records-table [rows]
  (let [headers (keys (first rows))
        labels (into {}
                     (map (juxt identity #(tr [:viewer %])))
                     headers)
        sorted-headers (sort-by (comp str/lower-case labels) headers)]
    [:table
     [:thead
      (for [h sorted-headers]
        ^{:key h}
        [:th {:scope "col"} (labels h)])]
     [:tbody
      (map-indexed
       (fn [i row]
         ^{:key i}
         [:tr
          (for [h sorted-headers]
            ^{:key h}
            [:td [show-value h (get row h)]])])
       rows)]]))

(defn show-features [{:strs [features] :as resource}]
  (let [{:strs [transport-operator transport-service] :as props}
        (-> features first (get "properties"))]
    [:div
     (when transport-operator
       [:div
        [:h2 (tr [:viewer "transport-operator"])]
        [properties-table transport-operator]])
     (when transport-service
       [:div
        [:h2 (tr [:viewer "transport-service"])]
        [properties-table transport-service]])]))

(defn operation-area [e! {:keys [geojson] :as app}]
  (r/create-class
    {:display-name "operation-area-map"
     :component-did-mount #(leaflet/update-bounds-on-load %)
     :component-did-update leaflet/update-bounds-from-layers
     :reagent-render
     (fn [e! {:keys [geojson] :as app}]
       [leaflet/Map {:ref "leaflet"
                     :center #js [65 25]
                     :zoom 5}
        [leaflet/TileLayer {:url "http://{s}.tile.osm.org/{z}/{x}/{y}.png"
                            :attribution "&copy; <a href=\"http://osm.org/copyright\">OpenStreetMap</a> contributors"}]

        (when geojson
          [leaflet/GeoJSON {:data geojson
                            :style {:color "green"}}])])}))

(defn viewer [e! _]
  (e! (v/->StartViewer))
  (fn [e! {:keys [authorized? logged-in? loading? geojson resource] :as app}]
    (if loading?

      [:div.loading [:img {:src "/base/images/loading-spinner.gif"}]]

      [theme e! app
        [:div.transport-service-view
          [:div.row.pull-right
            (if (and logged-in? authorized?)
              [:div {:style {
                             :margin-right "-15px"  ;; Ugly hack to fix our buttons margins
                             :margin-top "-25px" ;; Ugly hack to fix ckan styles
                             :padding-bottom "10px"
                             }}
                [buttons/save {:on-click #(e! (ts/->OpenTransportServicePage (last (clojure.string/split (get app :url) "/"))))
                             :disabled false
                             :primary  true}
                (tr [:buttons :edit-service])]])]

         [operation-area e! app]
       [show-features resource]]])))
