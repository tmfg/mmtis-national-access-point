(ns taxiui.views.stats
  (:require [ote.localization :refer [tr tr-key]]
            [stylefy.core :as stylefy]
            [taxiui.styles.stats :as styles]
            [taxiui.theme :as theme]
            [taxiui.views.components.formatters :as formatters]
            [re-svg-icons.feather-icons :as feather-icons]))

(def test-data [{:name "Lavishbay Oy"      :updated 4  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "atlas Oy"          :updated 7  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Putkosen Kyyti Oy" :updated 14 :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Inter Oy"          :updated 8  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "sense Oy"          :updated 7  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "dock Oy"           :updated 4  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Puresierra Oy"     :updated 9  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Overustic Oy"      :updated 2  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Tribecapsule Oy"   :updated 1  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Peakgram Oy"       :updated 0  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Yonderness Oy"     :updated 5  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Isletware Oy"      :updated 4  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Omnitramp Oy"      :updated 6  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Outway Oy"         :updated 11 :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Wayeon Oy"         :updated 10 :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Oneventure Oy"     :updated 9  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Gocompass Oy"      :updated 8  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Peakdistance Oy"   :updated 7  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Migratestripe Oy"  :updated 12 :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Flycase Oy"        :updated 13 :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Pioneerload Oy"    :updated 22 :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}])

(defn- table-rows
  [columns companies]
  (into [:tbody]
        (doall
          (for [company companies]
    (into ^{:key (str "row-" (:name company))} [:tr (stylefy/use-style styles/table-row)]
          (doall
            (for [{:keys [label renderer]} columns]
              [:td
               (renderer (get company label))])))))))

(defn- table
  [companies]
  (let [{:keys [columns]}
        {:columns [{:label :name               :renderer str}
                   {:label :updated            :renderer (partial formatters/street-light 0 6 12)}
                   {:label :example-trip       :renderer formatters/currency}
                   {:label :cost-start-daytime :renderer formatters/currency}
                   {:label :cost-travel-km     :renderer formatters/currency}
                   {:label :cost-travel-min    :renderer formatters/currency}
                   {:label :operation-area     :renderer str}]}]
    (fn [companies]
      [:table {:cellspacing "0"
               :style {:width "100%"}}
       [:thead
        [:tr (stylefy/use-style styles/table-headers)
         (doall
           (for [{:keys [label]} columns]
             ^{:key (str "col-" label)}
             [:th (stylefy.core/use-style styles/table-header)
              (tr [:taxi-ui :stats label])
              (feather-icons/chevron-down)]))]]
       [table-rows columns companies]])))

(defn stats
  [_ _]
  (fn [_ _]
    [:main (stylefy/use-style theme/main-container)
     [:h2 "Kokonaiskatsaus"]
     [table test-data]]))