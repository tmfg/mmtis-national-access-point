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
            [ote.ui.common :refer [scroll-sensor linkify]]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.app.controller.service-search :as ss]
            [ote.style.base :as style-base]
            [ote.style.service-search :as style]
            [stylefy.core :as stylefy]
            [clojure.string :as str]
            [ote.views.ckan-service-viewer :as ckan-service-viewer]
            [ote.app.controller.admin :as admin]
            [tuck.core :as tuck]))

(defn- delete-service-action [e! id name show-delete-modal?]
  [:span
   [ui/icon-button {:href "/ote/#/services" :on-click #(e! (admin/->DeleteTransportService id))}
    [ic/action-delete]]
   (when show-delete-modal?
     [ui/dialog
      {:open true
       :title (tr [:dialog :delete-transport-service :title])
       :actions [(r/as-element
                   [ui/flat-button
                    {:label (tr [:buttons :cancel])
                     :primary true
                     :on-click #(e! (admin/->CancelDeleteTransportService id))}])
                 (r/as-element
                   [ui/raised-button
                    {:label (tr [:buttons :delete])
                     :icon (ic/action-delete-forever)
                     :secondary true
                     :primary true
                     :on-click #(e! (admin/->ConfirmDeleteTransportService id))}])]}

      (tr [:dialog :delete-transport-service :confirm] {:name name})])])


(defn data-items [& icons-and-items]
  [:div (stylefy/use-style style/data-items)
   (doall
    (keep-indexed
     (fn [i [icon item]]
       (when item
         ^{:key i}
         [:div (stylefy/use-style style-base/item-list-row-margin)
          [:div (stylefy/use-style style/icon-div) icon ]
          [:div (stylefy/use-style style-base/item-list-item)
           item]]))
      (partition 2 icons-and-items)))])

(defn- format-address [{::common/keys [street postal_code post_office]}]
  (let [comma (if (not (empty? street)) ", " " ")]
  (str street comma postal_code " " post_office)))

(def external-interface-table-columns
  ;; [label width value-fn]
  [[::t-service/external-service-description "21%"
    (comp #(t-service/localized-text-for "FI" %) ::t-service/description ::t-service/external-interface)]
   [::t-service/external-service-url "21%"
    (comp #(linkify % % {:target "_blank"}) ::t-service/url ::t-service/external-interface)]
   [::t-service/format-short "16%" ::t-service/format]
   [::t-service/license "21%" ::t-service/license]
   [::t-service/license-url "21%" (comp #(when-not (str/blank? %)
                                           (linkify % % {:target "_blank"})) ::t-service/license-url)]])

(defn- external-interface-links [e! {::t-service/keys [id external-interface-links name
                                                       transport-operator-id ckan-resource-id]}]
  [:div
   [:div.nap-interface
    [:span.search-card-title (tr [:service-search :nap-interface])]
    (let [url (str js/window.location.origin "/ote/export/geojson/" transport-operator-id "/" id)]
      [linkify url url {:target "_blank"}])]
   (when-not (empty? external-interface-links)
     [:span
      [:br]
      [:span.search-card-title (tr [:service-search :external-interfaces])
       [:table
        [:thead (stylefy/use-style style/external-interface-header)
         [:tr
          (doall
           (for [[k w _] external-interface-table-columns]
             ^{:key k}
             [:th {:style {:width w}}
              (tr [:field-labels :transport-service-common k])]))]]
        [:tbody (stylefy/use-style style/external-interface-body)
         (doall
          (map-indexed
           (fn [i row]
             ^{:key i}
             [:tr {:selectable false}
              (doall
               (for [[k w value-fn] external-interface-table-columns]
                 ^{:key k}
                 [:td {:style {:width w :font-size "14px"}}
                  (value-fn row)]))])
           external-interface-links))]]]])])

(defn- result-card [e! admin?
                    {::t-service/keys [id name sub-type contact-address
                                       operation-area-description contact-phone contact-email
                                       operator-name ckan-resource-id transport-operator-id]
                     :as service}]
  (let [sub-type-tr (tr-key [:enums ::t-service/sub-type])]
    [ui/paper {:z-depth 1
               :style style/result-card}
     [:div.result-title (stylefy/use-style style/result-header)
      [:a {:href "#"
           :on-click #(do
                        (.preventDefault %)
                        (e! (ss/->ShowServiceGeoJSON
                             (str js/document.location.protocol "//" js/document.location.host
                                  "/ote/export/geojson/" transport-operator-id "/" id))))}
       name]
      [data-items

       [ic/action-home {:style style/contact-icon}]
       (format-address contact-address)

       [ic/communication-phone {:style style/contact-icon}]
       contact-phone

       [ic/communication-email {:style style/contact-icon}]
       contact-email]

      (when admin?
        [:div {:style {:float "right"}}
         [delete-service-action e! id name (get service :show-delete-modal?)]])]

     [:div.result-subtitle (stylefy/use-style style/subtitle)
      [:div (stylefy/use-style style/subtitle-operator-first)
       operator-name]
      [:div (stylefy/use-style style/subtitle-operator)
      (sub-type-tr sub-type)]]

     [:div.result-interfaces
      [external-interface-links e! service]]]))


(defn results-listing [e! {service-search :service-search user :user :as app}]
  (let [{:keys [results empty-filters? total-service-count
                filter-service-count fetching-more?]} service-search
        operator (:operator (:params app))]
    [:div.col-xs-12.col-md-12.col-lg-12
     [:p
      (if operator
        (tr (if (zero? filter-service-count)
              [:service-search :operator-no-services]
              [:service-search :operator-services])
            {:name (::t-service/operator-name (first results))
             :count filter-service-count})
        (tr [:service-search (if empty-filters?
                               :showing-latest-services
                               (case filter-service-count
                                 0 :no-results
                                 1 :one-result
                                 :many-results))]
            {:count filter-service-count}))
      " "
      (tr [:service-search :total-services] {:total-service-count total-service-count})]
     (doall
      (for [result results]
        ^{:key (::t-service/id result)}
        [result-card e! (:admin? user) result]))

     (if fetching-more?
       [:span (tr [:service-search :fetching-more])]
       (when (> filter-service-count (count results))
         [scroll-sensor #(e! (ss/->FetchMore))]))]))

(defn result-chips [e! chip-results]
  (fn [e! chip-results]
    [:div.place-search-results {:style {:display "flex" :flex-wrap "wrap"}}
    (for [{::t-operator/keys [name id] :as result} chip-results]
      ^{:key (str "transport-operator-" id)}
      [:span
       [ui/chip {:ref               id
                 :style             {:margin 4}
                 :on-request-delete #(do
                                       (e! (ss/->RemoveOperatorById id))
                                       (e! (ss/->UpdateSearchFilters nil)))}
        name]])]))


(defn- parse-operator-data-source [completions]
  (into-array
         (map (fn [{::t-operator/keys [id name]}]
                #js {:text  name
                     :id    id
                     :value (r/as-element
                              [ui/menu-item {:primary-text name}])})
              completions)))

(defn operator-search [e! data]
  (let [results (:results data)
        chip-results (:chip-results data)]
  [:div
   [:div.col-xs-12.col-md-3
     [ui/auto-complete {:floating-label-text (tr [:service-search :operator-search])
                        :floating-label-fixed true
                        :hintText  (tr [:service-search :operator-search-placeholder])
                        :hint-style style-base/placeholder
                        :filter (constantly true) ;; no filter, backend returns what we want
                         :dataSource (parse-operator-data-source results )
                         :on-update-input #(e! (ss/->SetOperatorName %))
                         :search-text (or (:name data) "")
                         :on-new-request #(do
                                            (e! (ss/->AddOperator (aget % "id")))
                                            (e! (ss/->UpdateSearchFilters nil)))}]

      [result-chips e! chip-results]]]))

(defn filters-form [e! {filters :filters
                        facets :facets
                        operators :operators
                        :as service-search}]
  (let [sub-type (tr-key [:enums ::t-service/sub-type]
                         [:enums ::t-service/type])]
    [:div
     [:h1 (tr [:service-search :label])]
     [form/form {:update! #(e! (ss/->UpdateSearchFilters %))
                 :name->label (tr-key [:service-search]
                                      [:field-labels :transport-service-common]
                                      [:field-labels :transport-service])}
       [(form/group
         {:label        (tr [:service-search :filters-label])
          :columns      3
          :layout       :row
          :card-options {:style style-base/filters-form}}

         {:name :text-search
         :type :string
         :placeholder (tr [:service-search :text-search-placeholder])}

         {:name ::t-service/operation-area
         :type :multiselect-selection
         :show-option #(:text %)
         :options (::t-service/operation-area facets)
         :auto-width? true}

         {:name ::t-service/sub-type
         :type :multiselect-selection
         :show-option #(str (sub-type (:sub-type %)))
         :options (::t-service/sub-type facets)
         :auto-width? true}

         {:type      :component
          :name      :operators
          :component (fn [{data :data}]
                       [:span [operator-search e! data]])})]
         filters]]))

(defn service-search [e! app]
  (e! (ss/->InitServiceSearch))
  (fn [e! {{results             :results
            total-service-count :total-service-count
            empty-filters?      :empty-filters?
            resource            :resource
            geojson             :geojson
            loading-geojson?    :loading-geojson?
            :as                 service-search} :service-search
           params                               :params
           :as                                  app}]
    [:div.service-search
     (when (or geojson loading-geojson?)
       [ui/dialog {:title                    (str (get-in resource ["features" 0 "properties" "transport-service" "name"]) " GeoJSON")
                   :open                     true
                   :modal                    false
                   :auto-scroll-body-content true
                   :on-request-close         #(e! (ss/->CloseServiceGeoJSON))
                   :actions                  [(r/as-element
                                                [ui/flat-button {:on-click #(e! (ss/->CloseServiceGeoJSON))
                                                                 :primary  true}
                                                 (tr [:buttons :close])])]}
        [ckan-service-viewer/viewer e! {:resource resource
                                        :geojson  geojson
                                        :loading? loading-geojson?}]])
     [filters-form e! service-search]
     (if (nil? results)
       [:div (tr [:service-search :no-filters])]
       [results-listing e! app])]))
