(ns taxiui.views.stats
  (:require [ote.localization :refer [tr tr-key]]
            [stylefy.core :as stylefy]
            [taxiui.app.controller.stats :as controller]
            [taxiui.styles.stats :as styles]
            [taxiui.theme :as theme]
            [taxiui.views.components.formatters :as formatters]
            [re-svg-icons.feather-icons :as feather-icons]
            [ote.theme.colors :as colors]
            [reagent.core :as r]))

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
  (fn [_ app]
    [:main (stylefy/use-style theme/main-container)
     [:h2 "Kokonaiskatsaus"]
     [table (get-in app [:taxi-ui :companies])]]))