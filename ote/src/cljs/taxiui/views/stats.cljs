(ns taxiui.views.stats
  (:require [ote.localization :refer [tr tr-key]]
            [stylefy.core :as stylefy]
            [taxiui.app.controller.stats :as controller]
            [taxiui.styles.stats :as styles]
            [taxiui.theme :as theme]
            [taxiui.views.components.formatters :as formatters]
            [re-svg-icons.feather-icons :as feather-icons]
            [ote.theme.colors :as colors]
            [reagent.core :as r]
            [clojure.string :as str]))

(defn- table-rows
  [columns companies]
  (into [:tbody]
        (doall
          (for [company companies]
    (into ^{:key (str/join "-" ["row" (:operator-id company) (:service-id company) (:name company)])} [:tr (stylefy/use-style styles/table-row)]
          (doall
            (for [{:keys [label renderer]} columns]
              [:td (stylefy/use-style styles/table-cell)
               (renderer (get company label))])))))))

(defn- sort-direction-transitions
  [current]
  ({:ascending  :descending
    :descending :ascending} current))

(defn- table
  [e! companies]
  (let [state (r/atom {:columns [{:label :name                :sortable? false :renderer str}
                                 {:label :updated             :sortable? false :renderer (partial formatters/street-light 0 6 12)}
                                 #_{:label :example-trip      :sortable? true  :renderer formatters/currency}
                                 {:label :start-price-daytime :sortable? true  :renderer formatters/currency}
                                 {:label :price-per-kilometer :sortable? true  :renderer formatters/currency}
                                 {:label :price-per-minute    :sortable? true  :renderer formatters/currency}
                                 #_{:label :operation-area    :sortable? true  :renderer str}]
                       :sorting {:column    :start-price-daytime  ;; TODO: just a hardcoded test value
                                 :direction :ascending}})]  ; cycles between :ascending, :descending
    (fn [e! companies]
      (let [{:keys [columns sorting]} @state]
        [:table {:cellSpacing "0"
                 :style {:width "100%"}}
         [:thead
          [:tr (stylefy/use-style styles/table-headers)
           (doall
             (for [{:keys [label sortable?]} columns]
               ^{:key (str "col-" label)}
               [:th (stylefy.core/use-style styles/table-header)
                [:span (stylefy/use-style styles/table-header-title) (tr [:taxi-ui :stats label])
                 ; TODO: linkify/persist sort state to reagent component
                 (when sortable?
                   (let [{:keys [column direction]} sorting]
                     [:a (stylefy/use-style styles/table-header-sorts
                                            {:href "#"
                                             :on-click (fn [e]
                                                         (do
                                                          (.preventDefault e)
                                                          (swap! state update-in [:sorting :column] (constantly label))
                                                          (swap! state update-in [:sorting :direction] #(if (= label column)
                                                                                                          (sort-direction-transitions direction)
                                                                                                          :ascending))
                                                          (e! (controller/->LoadStatistics (get @state :sorting)))))})
                      (feather-icons/chevron-up {:height  ".75em"
                                                 :viewBox "0 6 24 12"
                                                 :stroke  (cond
                                                            (and (= label column)
                                                                 (= :ascending direction)) colors/accessible-black
                                                            :else colors/basic-gray)})
                      (feather-icons/chevron-down {:height ".75em"
                                                   :viewBox "0 6 24 12"
                                                   :stroke  (cond
                                                              (and (= label column)
                                                                   (= :descending direction)) colors/accessible-black
                                                              :else colors/basic-gray)})]))]]))]]
         [table-rows columns companies]]))))

(defn stats
  [_ _]
  (fn [e! app]
    [:main (stylefy/use-style theme/main-container)
     [:h2 "Kokonaiskatsaus"]
     [table e! (get-in app [:taxi-ui :stats :companies])]]))