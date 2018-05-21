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
   [ui/icon-button {:href "#"
                    :on-click #(do
                                 (.preventDefault %)
                                 (e! (admin/->DeleteTransportService id)))}
    [ic/action-delete {:class-name (:class (stylefy/use-style style/delete-icon))
                       :style style/partly-visible-delete-icon}]]
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

(defn- gtfs-viewer-link [{interface ::t-service/external-interface [format] ::t-service/format}]
  (let [format (str/lower-case format)]
    (when (or (= "gtfs" format) (= "kalkati.net" format))
      (common-ui/linkify
        (str "#/routes/view-gtfs?url=" (::t-service/url interface)
             (when (= "kalkati.net" format)
               "&type=kalkati"))
        [ui/flat-button {:label (tr [:service-search :view-routes])
                         :primary true
                         :label-position "before"
                         :icon (ic/action-open-in-new)}]
        {:target "_blank"}))))

(defn parse-content-value [value-array]
  (let [data-content-value #(tr [:enums ::t-service/interface-data-content %])
        value-str (str/join ", " (map #(data-content-value %) value-array))
        return-value (common-ui/maybe-shorten-text-to 45 value-str)]
    return-value))

(def external-interface-table-columns
  ;; [label width value-fn]
  [[:table-header-external-interface "25%"
    (fn [{::t-service/keys [data-content external-interface]}]
      (common-ui/linkify (::t-service/url external-interface)
                         (if (nil? data-content)
                           (::t-service/url external-interface)
                           (parse-content-value data-content))
                         {:target "_blank"}))]
   [::t-service/format-short "10%" (comp #(str/join ", " %) ::t-service/format)]
   [::t-service/license "15%" ::t-service/license]
   [::t-service/external-service-description "25%"
    (comp #(t-service/localized-text-for "FI" %) ::t-service/description ::t-service/external-interface)]
   [:gtfs-viewer "25%" (comp #(gtfs-viewer-link %))]])

(defn- external-interface-links [e! {::t-service/keys [id external-interface-links name
                                                       transport-operator-id ckan-resource-id]}]
  (when-not (empty? external-interface-links)
    [:div
     [:span.search-card-title {:style {:padding "0.5em 0em 1em 0em"}} (tr [:service-search :external-interfaces])]
     [:table {:style {:margin-top "10px" :width "100%"}}
      [:thead (stylefy/use-style style/external-interface-header)
       [:tr
        (doall
          (for [[k w _] external-interface-table-columns]
            ^{:key k}
            [:th {:style (merge {:width w} style/external-table-header)}
             (tr [:field-labels :transport-service-common k])]))]]
      [:tbody (stylefy/use-style style/external-interface-body)
       (doall
         (map-indexed
           (fn [i {::t-service/keys [data-content external-interface format license description] :as row}]
             ^{:key (str "external-table-row-" i)}
             [:tr {:style style/external-table-row}
              ^{:key (str "external-interface-" i)}
              (doall
                (for [[k w value-fn] external-interface-table-columns]
                  ^{:key k}
                  [:td {:style {:width w :font-size "14px"}}
                   (value-fn row)]))])
           external-interface-links))]]]))

(defn- result-card [e! admin?
                    {::t-service/keys [id name sub-type contact-address
                                       operation-area-description description contact-phone contact-email
                                       operator-name business-id ckan-resource-id transport-operator-id]
                     :as service}]
  (let [sub-type-tr (tr-key [:enums ::t-service/sub-type])
        e-links [external-interface-links e! service]
        service-desc (t-service/localized-text-for "FI" description)]
    [:div.result-card (stylefy/use-style style/result-card)

     [:a {:href "#"
          :on-click #(do
                       (.preventDefault %)
                       (e! (ss/->ShowServiceGeoJSON
                             (str js/document.location.protocol "//" js/document.location.host
                                  "/export/geojson/" transport-operator-id "/" id))))}
      [:div.result-title (stylefy/use-style style/result-card-label) name
       [:span.small-text (stylefy/use-style style/result-card-small-label)
        (sub-type-tr sub-type)]]]

     (when admin?
       [:div (stylefy/use-style style/result-card-delete)
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
       [data-item nil (str operator-name " " business-id)]

       [data-item [ic/action-home {:style style/contact-icon}]
        (format-address contact-address)]

       [data-item [ic/communication-phone {:style style/contact-icon}]
        contact-phone]

       [data-item [ic/communication-email {:style style/contact-icon}]
        contact-email]]]]))

(defn results-listing [e! {service-search :service-search user :user :as app}]
  (let [{:keys [results empty-filters? total-service-count
                filter-service-count fetching-more?]} service-search
        operator (:operator (:params app))]
    [:div.col-xs-12.col-md-12.col-lg-12
     [:span
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

(defn operator-result-chips [e! chip-results]
  (fn [e! chip-results]
    [:div.place-search-results {:style {:display "flex" :flex-wrap "wrap"}}
     (for [{:keys [operator business-id]} chip-results]
       ^{:key (str "transport-operator-" business-id)}
       [:span
        [ui/chip {:ref business-id
                  :style {:margin 4}
                  :on-request-delete #(do
                                        (e! (ss/->RemoveOperatorById business-id))
                                        (e! (ss/->UpdateSearchFilters nil)))}
         operator]])]))

(defn- parse-operator-data-source [completions]
  (into-array
    (map (fn [{:keys [business-id operator]}]
           #js {:text operator
                :business-id business-id
                :value (r/as-element
                         [ui/menu-item {:primary-text operator}])})
         completions)))

(defn operator-search [e! data]
  (let [results (:results data)
        chip-results (:chip-results data)]
    [:div
      [ui/auto-complete {:floating-label-text (tr [:service-search :operator-search])
                         :floating-label-fixed true
                         :hintText (tr [:service-search :operator-search-placeholder])
                         :hint-style style-base/placeholder
                         :full-width? true
                         :style {:width "100%"}
                         :filter (constantly true)          ;; no filter, backend returns what we want
                         :maxSearchResults 12
                         :dataSource (parse-operator-data-source results)
                         :on-update-input #(e! (ss/->SetOperatorName %))
                         :search-text (or (:operator data) "")
                         :on-new-request #(do
                                            (e! (ss/->AddOperator (aget % "business-id")))
                                            (e! (ss/->UpdateSearchFilters nil)))}]

      [operator-result-chips e! chip-results]]))

(defn- sort-places [places]
  (let [names (filter #(re-matches #"^\D+$" (:text %)) places)
        ;; Place names starting with a number, like postal code areas
        numeric (filter #(re-matches #"^\d.*" (:text %)) places)]
    (concat
      (sort-by :text names)
      (sort-by :text numeric))))

(def transport-types [:road :rail :sea :aviation])

(defn filters-form [e! {filters :filters
                        facets  :facets}]
  (let [sub-types-to-list (fn [data]
                            (keep (fn [val]
                                    (let [subtype (:sub-type val)]
                                      (when-not (= :other subtype)
                                        (into (sorted-map)
                                              (-> val
                                                  (dissoc :sub-type)
                                                  (assoc :value subtype
                                                         :text (tr [:enums ::t-service/sub-type subtype])))))))
                                  data))
        transport-types-to-list (fn [data]
                                  (keep (fn [val]
                                          (into (sorted-map)
                                                (assoc {} :text (tr [:enums ::t-service/transport-type val])
                                                          :value val)))
                                        data))]
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

         {:type :component
          :name :operators
          :full-width? true
          :container-class "col-xs-12 col-sm-12 col-md-4"
          :component (fn [{data :data}]
                       [:span [operator-search e! data]])}

         {:name :text-search
          :type :string
          :container-class "col-xs-12 col-sm-12 col-md-4"
          :full-width? true
          :hint-text (tr [:service-search :text-search-placeholder])}

         {:id "transport-types"
          :name ::t-service/transport-type
          :type :chip-input
          :container-class "col-xs-12 col-sm-12 col-md-4"
          :full-width? true
          :full-width-input? false
          :suggestions-config {:text :text :value :text}
          :suggestions (transport-types-to-list transport-types)
          :open-on-focus? true}

         {:name ::t-service/operation-area
          :type :chip-input
          :container-class "col-xs-12 col-sm-12 col-md-4"
          :filter (fn [query, key]
                    (let [k (str/lower-case key)
                          q (str/lower-case query)]
                      (when (> (count q) 1)
                        (if (re-matches #"^\D+" q)
                          (str/starts-with?
                            (second (re-matches #"(?:[0-9]+\s*)?(.*)$" k)) q)
                          (str/starts-with? k q)))))
          :open-on-focus? true
          :max-results 10
          :suggestions-config {:text :text :value :text}
          :suggestions (sort-places (::t-service/operation-area facets))
          ;; Select first match from autocomplete filter result list after pressing enter
          :auto-select? true
          :full-width? true}

         {:id "sub-types"
          :name ::t-service/sub-type
          :label (tr [:service-search :type-search])
          :type :chip-input
          :container-class "col-xs-12 col-sm-12 col-md-4"
          :full-width? true
          :full-width-input? false
          :suggestions-config {:text :text :value :text}
          :suggestions (sub-types-to-list (::t-service/sub-type facets))
          :open-on-focus? true})]
      filters]]))

(defn service-search [e! app]
  (e! (ss/->InitServiceSearch))
  (fn [e! {{results :results
            total-service-count :total-service-count
            empty-filters? :empty-filters?
            resource :resource
            geojson :geojson
            loading-geojson? :loading-geojson?
            :as service-search} :service-search
           params :params
           :as app}]
    [:div.service-search
     (when (or geojson loading-geojson?)
       [ui/dialog {:title (str (get-in resource ["features" 0 "properties" "transport-service" "name"]) " GeoJSON")
                   :open true
                   :modal false
                   :auto-scroll-body-content true
                   :on-request-close #(e! (ss/->CloseServiceGeoJSON))
                   :actions [(r/as-element
                               [ui/flat-button {:on-click #(e! (ss/->CloseServiceGeoJSON))
                                                :primary true}
                                (tr [:buttons :close])])]}
        [ckan-service-viewer/viewer e! {:resource resource
                                        :geojson geojson
                                        :loading? loading-geojson?}]])
     [filters-form e! service-search]
     (if (nil? results)
       [:div (tr [:service-search :no-filters])]
       [results-listing e! app])]))
