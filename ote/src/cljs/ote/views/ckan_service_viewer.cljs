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
            [ote.util.values :as values]
            cljsjs.leaflet
            [ote.time :as time]))



(defn ignore-key? [key]
  (str/ends-with? key "-id"))

(defmulti transform-value (fn [key value] key))

(defmethod transform-value "url" [_ value] (linkify value value {:target "_blank"}))
(defmethod transform-value "homepage" [_ value] (linkify value value {:target "_blank"}))
(defmethod transform-value "contact-email" [_ value] (linkify (str "mailto:" value) value))

(defn lang-name-for [lang]
  ;; Show unicode country flags for supported languages, otherwise show language code
  (case lang
    "FI" "suomi" ; finnish flag
    "SV" "svenska" ; swedish flag
    "EN" "english" ; united kingdom flag
    lang))

(defmethod transform-value :default [_ value]
  (tr-or (tr [:viewer :values value]) (str value)))

(declare properties-table records-table)

(def keyset-formatter {#{"hours" "minutes" "seconds"}
                       (fn [{:strs [hours minutes seconds] :as v}]
                         (time/format-time {:hours hours
                                            :minutes minutes
                                            :seconds seconds}))
                       #{"lang" "text"}
                       (fn [{:strs [lang text]}]
                         (str (lang-name-for lang) ": " text))})

(defn show-value [key value]
  (let [formatter (when (map? value)
                    (keyset-formatter (set (keys value))))
        value ((or formatter identity) value)]
    (cond
      ;; This is an object, show key/value table
      (map? value)
      [properties-table value]

      ;; This is a collection of maps, each having the same keys
      ;; we can show this as a table
      (and (coll? value)
           (every? map? value)
           (apply = (map keys value)))
      (if-let [fmt (keyset-formatter (set (keys (first value))))]
        ;; If there is a formatter for this keyset, just join the items
        [:div.list-of-values
         (doall
          (map-indexed
           (fn [i v]
             ^{:key i}
             [:div.list-item (fmt v)])
           value))]
        ;; otherwise, show a table
        [records-table value])

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
      [:span (transform-value key value)])))

(defn properties-table [properties]
  [:table.table.table-striped.table-bordered.table-condensed
   [:tbody
    (for [[key value] (sort-by first properties)
          :when (and (not (ignore-key? key))
                     (not (values/effectively-empty? value)))]
      ^{:key key}
      [:tr
       [:th {:scope "row" :width "25%"}
        (tr-or (tr [:viewer key]) key)]
       [:td [show-value key value]]])]])

(defn records-table [rows]
  (let [headers (filter (complement ignore-key?) (keys (first rows)))
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
