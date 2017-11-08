(ns ote.views.service-search
  "A service search page that allows filtering and listing published services."
  (:require [reagent.core :as r]
            [ote.db.transport-service :as t-service]
            [ote.db.transport-operator :as t-operator]
            [ote.localization :refer [tr tr-key]]
            [ote.ui.form-fields :as form-fields]
            [ote.ui.buttons :as buttons]
            [ote.ui.form :as form]
            [cljs-react-material-ui.reagent :as ui]
            [ote.app.controller.service-search :as ss]))


(defn results-listing [e! results]
  [ui/grid-list
   (doall
    (for [{::t-service/keys [id name] :as service} results]
      [ui/grid-tile {:key id}
       [:div "resultti on " (pr-str service)]]))])

(defn filters-form [e! {filters :filters
                        facets :facets
                        :as service-search}]
  (let [sub-type (tr-key [:enums ::t-service/sub-type]
                         [:enums ::t-service/type])]
    [:div
     [:h3 (tr [:service-search :label])]
     [form/form {:update! #(e! (ss/->UpdateSearchFilters %))
                 :name->label (tr-key [:service-search]
                                      [:field-labels :transport-service-common]
                                      [:field-labels :transport-service])}
      [(form/group
        {:label (tr [:service-search :filters-label])
         :columns 3
         :layout :row}

        {:name :text-search
         :type :string
         :placeholder (tr [:service-search :text-search-placeholder])}

        ;; ekaksi tämä
        {:name ::t-service/operation-area
         :type :multiselect-selection
         :show-option #(str (:text %) " (" (:count %) ")")
         :options (::t-service/operation-area facets)}

        ;; subtypet tässä listassa, jos niitä on
        {:name ::t-service/sub-type
         :type :multiselect-selection
         :show-option #(str (sub-type (:sub-type %)) " (" (:count %) ")")
         :options (::t-service/sub-type facets)
         :auto-width? true})]
      filters]]))

(defn service-search [e! _]
  (e! (ss/->InitServiceSearch))
  (fn [e! {results :results
           :as service-search}]
    [:div.service-search
     [filters-form e! service-search]
     (if (empty? results)
       [:div "tee vähän hakuja"]
       [results-listing e! results])]))
