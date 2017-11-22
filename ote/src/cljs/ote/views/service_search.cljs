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
            [ote.ui.common :refer [linkify]]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.app.controller.service-search :as ss]
            [ote.style.base :as style-base]
            [ote.style.service-search :as style]
            [stylefy.core :as stylefy]
            [clojure.string :as str]))

(defn data-items [& icons-and-items]
  [:div (stylefy/use-style style/data-items)
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

(def external-interface-table-columns
  ;; [label width value-fn]
  [[::t-service/external-service-description "21%"
    (comp #(t-service/localized-text-for "FI" %) ::t-service/description ::t-service/external-interface)]
   [::t-service/external-service-url "21%"
    (comp #(linkify % %) ::t-service/url ::t-service/external-interface)]
   [::t-service/format-short "16%" ::t-service/format]
   [::t-service/license "21%" ::t-service/license]
   [::t-service/license-url "21%" (comp #(when-not (str/blank? %)
                                           (linkify % %)) ::t-service/license-url)]])

(defn- external-interface-links [e! {::t-service/keys [id external-interface-links name
                                                       transport-operator-id ckan-resource-id]}]
  [:div
   [:div.nap-interface
    [:b (tr [:service-search :nap-interface])]
    (let [url (str js/window.location.origin "/ote/export/geojson/" transport-operator-id "/" id)]
      [:a {:href url :target "_blank"} url])]
   (when-not (empty? external-interface-links)
     [:span
      [:br]
      [:b (tr [:service-search :external-interfaces])
       [:table
        [:thead (stylefy/use-style style/external-interface-header)
         [:tr
          (for [[k w _] external-interface-table-columns]
            ^{:key k}
            [:th {:style {:width w}}
             (tr [:field-labels :transport-service-common k])])]]
        [:tbody (stylefy/use-style style/external-interface-body)
         (map-indexed
          (fn [i row]
            ^{:key i}
            [:tr {:selectable false}
             (for [[k w value-fn] external-interface-table-columns]
               ^{:key k}
               [:td {:style {:width w}}
                (value-fn row)])])
          external-interface-links)]]]])])

(defn- result-card [e! {::t-service/keys [id name sub-type contact-address
                                          operation-area-description contact-phone contact-email
                                          operator-name ckan-resource-id transport-operator-id]
                        :as service}]

  (let [sub-type-tr (tr-key [:enums ::t-service/sub-type]
                            [:enums ::t-service/type])]
    [ui/paper {:z-depth 2
               :style style/result-card}
     [:div.result-title (stylefy/use-style style/result-header)
      [:a {:href "#"
           :style style/result-link
           :on-click #(do
                        (.preventDefault %)
                        (e! (ss/->OpenInterfaceInCKAN transport-operator-id id
                                                      ckan-resource-id)))}
       name]
      [data-items

       [ic/action-home]
       (format-address contact-address)

       [ic/communication-phone]
       contact-phone

       [ic/communication-email]
       contact-email]]
     [:div.result-subtitle (stylefy/use-style style/subtitle)
      [:div (stylefy/use-style style/subtitle-operator)
       operator-name]
      (sub-type-tr sub-type)]

     [:div.result-interfaces
      [external-interface-links e! service]]]))

(defn results-listing [e! results empty-filters?]
  (let [result-count (count results)]
    [:div.col-xs-12.col-md-12.col-lg-12

     [:h2 (stylefy/use-style style-base/large-title)
      (tr [:service-search (if empty-filters?
                             :showing-latest-services
                             (case result-count
                               0 :no-results
                               1 :one-result
                               :many-results))]
          {:count result-count})]
     (doall
      (for [result results]
        ^{:key (::t-service/id result)}
        [result-card e! result]))]))

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
           empty-filters? :empty-filters?
           :as service-search}]
    [:div.service-search
     [filters-form e! service-search]
     [ui/divider]
     (if (nil? results)
       [:div (tr [:service-search :no-filters])]
       [results-listing e! results empty-filters?])]))
