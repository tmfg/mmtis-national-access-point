(ns taxiui.views.stats
  (:require [goog.functions :as gf]
            [ote.localization :refer [tr tr-key]]
            [stylefy.core :as stylefy]
            [taxiui.app.controller.stats :as controller]
            [taxiui.styles.stats :as styles]
            [taxiui.theme :as theme]
            [taxiui.views.components.formatters :as formatters]
            [re-svg-icons.feather-icons :as feather-icons]
            [ote.theme.colors :as colors]
            [reagent.core :as r]
            [clojure.string :as str]
            [taxiui.views.components.forms :as forms]))

(defn- table-rows
  [columns companies]
  (into [:tbody]
        (doall
          (for [company companies]
    (into ^{:key (str/join "-" ["row" (:operator-id company) (:service-id company) (:name company)])} [:tr (stylefy/use-style styles/table-row)]
          (doall
            (for [{:keys [label renderer styles]} columns]
              [:td (stylefy/use-style (merge styles/table-cell styles))
               (renderer (get company label))])))))))

(defn- sort-direction-transitions
  [current]
  ({:ascending  :descending
    :descending :ascending} current))

(defn- table
  [e! companies]
  (let [state (r/atom {:columns [{:label :name                :sortable? true   :renderer str :styles {:width "16em"}}
                                 {:label :updated             :sortable? false  :renderer (partial formatters/street-light 0 6 12)}
                                 {:label :example-trip        :sortable? true   :renderer formatters/currency}
                                 {:label :start-price-daytime :sortable? true   :renderer formatters/currency}
                                 {:label :price-per-kilometer :sortable? true   :renderer formatters/currency}
                                 {:label :price-per-minute    :sortable? true   :renderer formatters/currency}
                                 {:label :operating-areas     :sortable? true   :renderer (formatters/joining ", " str)}]
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
                [:span (stylefy/use-style styles/table-header-title) (tr [:taxi-ui :stats :columns label])
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
                                                          (e! (controller/->SetSorting (get @state :sorting)))
                                                          (e! (controller/->LoadStatistics))))})
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

(defn- filter-input
  [e! app title subtitle filter-element]
  [:div {:style {:display "flex"
                 :flex-direction "column"
                 :padding-right "2em"}}
   [:span {:style {:font-weight "700"
                   :min-height "1.5em"}} title]
   [:span {:style {:font-weight "700"
                   :min-height "1.5em"}} subtitle]
   [:div {:style {:margin-top "auto"}}
    filter-element]])

(defn- age-filter
  [e! id radio-group-id age]
  [:span {:style {:display "flex"
                  :align-items "center"
                  :padding-bottom "0.4em"}}
   [formatters/street-light 0 6 12 age]
   [:label {:for id
            :style {:padding-left "0.4em" :padding-right "0.4em"}} (tr [:taxi-ui :stats :sections :filters id])]
   [:input {:id id
            :name radio-group-id
            :type "radio"
            :value id
            :style {:margin-left "auto"}
            :on-click (fn [e]
                        (e! (controller/->SetFilter :age-filter id)))
            }]])

(def set-filter (goog.functions.debounce (fn [e! id value]
                               (e! (controller/->SetFilter id value))) 500))

(defn- filters
  [e! app]
  [:section {:style {:display "flex"}}
   [filter-input e! app (tr [:taxi-ui :stats :sections :filters :prices-updated-last]) ""
    [:div {:style {:display "flex"
                   :flex-direction "column"}}
     [age-filter e! :within-six-months :age-filter 0]
     [age-filter e! :within-one-year :age-filter 6]
     [age-filter e! :over-year-ago :age-filter 12]]]

   [filter-input e! app (tr [:taxi-ui :stats :sections :filters :company-by-name]) ""
    [:div {:style {:padding-bottom "0.4em"}}
     [forms/simple-input
      :name-filter
      {:styles {:margin-top     "auto"
                :padding-bottom "0.4em"}
       :on-change #(set-filter e! :name (-> % .-target .-value))}]]]

   #_[filter-input e! app "Toiminta-alue" "(kuntakoodeittain)"]])

(defn stats
  [_ _]
  (fn [e! app]
    [:main (stylefy/use-style theme/main-container)
     ; this title is reserved for the statistics filter boxes, not the table itself
     #_[:h2 (tr [:taxi-ui :stats :page-main-title])]

     [:h2 (tr [:taxi-ui :stats :sections :filters :title])]
     [filters e! app]

     [table e! (get-in app [:taxi-ui :stats :companies])]]))