(ns ote.views.service-search
  "A service search page that allows filtering and listing published services."
  (:require [reagent.core :as r]
            [ote.db.transport-service :as t-service]
            [ote.db.transport-operator :as t-operator]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]
            [ote.ui.form-fields :as form-fields]
            [ote.ui.buttons :as buttons]
            [ote.ui.form :as form]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.app.controller.service-search :as ss]
            [ote.style.base :as style-base]
            [stylefy.core :as stylefy]
            [clojure.string :as str]))

(defn data-items [& icons-and-items]
  [:div (stylefy/use-style style-base/item-list-container)
   (doall
    (keep-indexed
     (fn [i [icon item]]
       (when item
         ^{:key i}
         [:div (stylefy/use-style style-base/item-list-row-margin)
          icon
          [:div (stylefy/use-style style-base/item-list-item)
           item]]))
      (partition 2 icons-and-items)))])

(defn- format-address [{::common/keys [street postal_code post_office]}]
  (str street ", " postal_code " " post_office))

(defn results-listing [e! results]
  (let [sub-type-tr (tr-key [:enums ::t-service/sub-type]
                            [:enums ::t-service/type])
        result-count (count results)]
    [:div.col-xs-12.col-md-12.col-lg-12
     [:h2 (stylefy/use-style style-base/large-title)
      (tr [:service-search (case result-count
                             0 :no-results
                             1 :one-result
                             :many-results)]
          {:count result-count})]
     (doall
      (for [{::t-service/keys [id name sub-type contact-address operation-area-description
                               contact-phone contact-email external-interface-links operator-name
                               transport-operator-id ckan-resource-id]
             :as service}
            results]
        ^{:key id}
        [ui/card {:z-depth 1}
         [ui/card-header {:title name :style style-base/title
                          :subtitle (sub-type-tr sub-type)}]
         [ui/card-text
          [data-items

           [ic/action-home]
           (format-address contact-address)

           [ic/maps-map]
           (str/join ", " operation-area-description)

           [ic/communication-phone]
           contact-phone

           [ic/communication-email]
           contact-email

           [ic/communication-business]
           operator-name]]
         [ui/card-actions
          (r/as-element
           [:div
            (tr [:service-search :interfaces])
            [ui/flat-button
             {:style {:margin-left "1em"}
              :primary true
              :on-click #(e! (ss/->OpenInterfaceInCKAN transport-operator-id id ckan-resource-id))}
             (str name " GeoJSON")]
            (doall
             (for [{::t-service/keys [external-interface format ckan-resource-id]} external-interface-links
                   :let [description (::t-service/description external-interface)]]
               [ui/flat-button
                {:style {:margin-left "1em"}
                 :primary true
                 :on-click #(e! (ss/->OpenInterfaceInCKAN transport-operator-id id ckan-resource-id))}
                (str (t-service/localized-text-for "FI" description)
                     " (" format ")")]))])]]))]))

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

        {:name ::t-service/operation-area
         :type :multiselect-selection
         :show-option #(str (:text %) " (" (:count %) ")")
         :options (::t-service/operation-area facets)}

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
     [ui/divider]
     (if (nil? results)
       [:div (tr [:service-search :no-filters])]
       [results-listing e! results])]))
