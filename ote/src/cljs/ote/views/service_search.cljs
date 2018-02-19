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
            [ote.ui.common :as common-ui]
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
  [:div {:style {:color "#fff"}}
   [ui/icon-button {:href     "#"
                    :on-click #(do
                                 (.preventDefault %)
                                 (e! (admin/->DeleteTransportService id)))}
    [ic/action-delete {:class-name (:class (stylefy/use-style style/delete-icon))
                       :style      {:color "rgba(255, 255, 255, 0,75)"}}]]
   (when show-delete-modal?
     [ui/dialog
      {:open    true
       :title   (tr [:dialog :delete-transport-service :title])
       :actions [(r/as-element
                   [ui/flat-button
                    {:label    (tr [:buttons :cancel])
                     :primary  true
                     :on-click #(e! (admin/->CancelDeleteTransportService id))}])
                 (r/as-element
                   [ui/raised-button
                    {:label     (tr [:buttons :delete])
                     :icon      (ic/action-delete-forever)
                     :secondary true
                     :primary   true
                     :on-click  #(e! (admin/->ConfirmDeleteTransportService id))}])]}
      (tr [:dialog :delete-transport-service :confirm] {:name name})])])


(defn data-item [icon item]
  [:div (stylefy/use-style style/data-items)
   [:div (stylefy/use-style style-base/item-list-row-margin)
    (when (and icon item)
      [:div (stylefy/use-style style/icon-div) icon])
    (when item
      [:div (stylefy/use-style style-base/item-list-item)
       item])]])

(defn- format-address [{::common/keys [street postal_code post_office]}]
  (let [comma (if (not (empty? street)) ", " " ")]
  (str street comma postal_code " " post_office)))

(def external-interface-table-columns
  ;; [label width value-fn]
  [[:table-header-external-interface "10%"
    (comp #(common-ui/linkify % % {:target "_blank"}) ::t-service/url ::t-service/external-interface)]
   [::t-service/format-short "10%" ::t-service/format]
   [::t-service/license "10%" ::t-service/license]
   [::t-service/external-service-description "10%"
    (comp #(t-service/localized-text-for "FI" %) ::t-service/description ::t-service/external-interface)]])

(defn- external-interface-links [e! {::t-service/keys [id external-interface-links name
                                                       transport-operator-id ckan-resource-id]}]
  (when-not (empty? external-interface-links)
    [:div
     [:span.search-card-title (tr [:service-search :external-interfaces])]
     [:table {:style {:margin-top "10px"}}
      [:thead (stylefy/use-style style/external-interface-header)
       [:tr
        (doall
          (for [[k w _] external-interface-table-columns]
            ^{:key k}
            [:th {:style {:width w :height "20px"}}
             (tr [:field-labels :transport-service-common k])]))]]
      [:tbody (stylefy/use-style style/external-interface-body)
       (doall
         (map-indexed
           (fn [i row]
             ^{:key i}
             [:tr {:selectable false :style {:height "20px"}}
              (doall
                (for [[k w value-fn] external-interface-table-columns]
                  ^{:key k}
                  [:td {:style {:width w :font-size "14px"}}
                   (value-fn row)]))])
           external-interface-links))]]]))

(defn- result-card [e! admin?
                    {::t-service/keys [id name sub-type contact-address
                                       operation-area-description description contact-phone contact-email
                                       operator-name ckan-resource-id transport-operator-id]
                     :as              service}]
  (let [sub-type-tr (tr-key [:enums ::t-service/sub-type])
        e-links [external-interface-links e! service]
        service-desc (t-service/localized-text-for "FI" description)]
    [:div.result-card {:style (merge style/result-card {:margin-top "20px"})}

     [:a {:style    {:color "#FFF"}
          :href     "#"
          :on-click #(do
                       (.preventDefault %)
                       (e! (ss/->ShowServiceGeoJSON
                             (str js/document.location.protocol "//" js/document.location.host
                                  "/ote/export/geojson/" transport-operator-id "/" id))))}
      [:div.result-title (stylefy/use-style style/result-card-label) name
       [:span.small-text {:style {:font-size "12px" :font-weight "20" :padding-left "20px"}}
        (sub-type-tr sub-type)]]]

     (when admin?
       [:div {:style {:float "right" :position "relative" :top "-50px"}}
        [delete-service-action e! id name (get service :show-delete-modal?)]])


     [:div.result-body (stylefy/use-style style/result-card-body)
      (when-not (empty? service-desc)
        [:div.description {:style style/result-border}
         (common-ui/shortened-description service-desc 270)])
      [:div.nap-interface {:style style/result-border}
       [:span.search-card-title (tr [:service-search :nap-interface])]
       (let [url (str js/window.location.origin "/ote/export/geojson/" transport-operator-id "/" id)]
         [common-ui/linkify url url {:target "_blank"}])]

      (when-not (empty? (::t-service/external-interface-links service))
        [:div.result-interfaces {:style style/result-border}
         e-links])
      [:div
       [data-item nil operator-name]

       [data-item [ic/action-home {:style style/contact-icon}]
        (format-address contact-address)]

       [data-item [ic/communication-phone {:style style/contact-icon}]
        contact-phone]

       [data-item [ic/communication-email {:style style/contact-icon}]
        contact-email]
       ]]]))

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
                               :result-count)]
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
         [common-ui/scroll-sensor #(e! (ss/->FetchMore))]))]))

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
                        :maxSearchResults 12
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
          {:label (tr [:service-search :filters-label])
           :columns 3
           :layout :row
           :card-style style-base/filters-form}

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
