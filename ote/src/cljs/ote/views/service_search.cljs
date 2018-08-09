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
            [ote.ui.icons :as icons]
            [ote.app.controller.service-search :as ss]
            [ote.style.base :as style-base]
            [ote.style.service-search :as style]
            [stylefy.core :as stylefy]
            [clojure.string :as str]
            [ote.views.ckan-service-viewer :as ckan-service-viewer]
            [ote.app.controller.admin :as admin]
            [tuck.core :as tuck]
            [ote.ui.validation :as validation]
            [ote.util.text :as text]))

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

(defn- gtfs-viewer-link [{interface ::t-service/external-interface [format] ::t-service/format
                          has-errors? :has-errors?}]
  (when format
    (let [format (str/lower-case format)]
      (when (or (= "gtfs" format) (= "kalkati.net" format))
        [:span " | "
         (if-not has-errors?
           (common-ui/linkify
             (str "#/routes/view-gtfs?url=" (.encodeURIComponent js/window
                                                                 (::t-service/url interface))
                  (when (= "kalkati.net" format)
                    "&type=kalkati"))
             [:span
              [ic/action-open-in-new {:style {:position "relative" :top "6px" :color "#06c" :padding-right "3px"}}]
              (tr [:service-search :view-routes])]
             {:target "_blank"})
           [:span
            [ic/alert-warning {:style {:position "relative" :top "6px" :color "#f80" :padding-right "3px"}}]
            (tr [:service-search :view-routes-failure])])]))))

(defn parse-content-value [value-array]
  (let [data-content-value #(tr [:enums ::t-service/interface-data-content %])
        value-str (str/join ", " (map #(data-content-value %) value-array))
        return-value (text/maybe-shorten-text-to 45 value-str)]
    return-value))

(defn- external-interface-links [{::t-service/keys [id external-interface-links transport-operator-id]}]
  (let [nap-url (str js/window.location.origin "/ote/export/geojson/" transport-operator-id "/" id)]
    [:div {:key id}
     [:div.row (stylefy/use-style style/link-result-card-row)
      [common-ui/linkify nap-url "NAP-rajapinta" {:target "_blank"}]
      [:span " | "]
      [:span "GeoJSON"]]
     (when-not (empty? external-interface-links)
       [:div.row
        (doall
          (map-indexed
            (fn [i {::t-service/keys [external-interface format data-content] :as row}]
              (let [data-content (if (nil? data-content)
                                   (::t-service/url external-interface)
                                   (parse-content-value data-content))]
                [:div.row (merge {:key (str i "_" id)}
                                 (stylefy/use-style style/link-result-card-row))
                 [common-ui/linkify (::t-service/url external-interface) data-content {:target "_blank" :style {:color "#06c"}}]
                 [:span " | " (str/join ", " format)]
                 [gtfs-viewer-link row]]))
            external-interface-links))])]))

(defn- list-service-companies [service-companies service-search]
  (when (not (nil? service-companies))
    (let [searched-business-ids (str/split (get-in service-search [:params :operators]) ",")
          found-business-ids (keep (fn [sc]
                                     (let [s (keep #(when (= (::t-service/business-id sc) %) sc) searched-business-ids)]
                                       (when (not (empty? s)) (first s))))
                                   service-companies)
          presented-companies-count (if (not (empty? found-business-ids))
                                      (count found-business-ids)
                                      2)
          extra-companies (- (count service-companies) presented-companies-count)]
      [:div
       [:h4 (tr [:service-search :other-involved-companies])]
       (if (not (empty? found-business-ids))
         (doall (for [c found-business-ids]
                  (when (::t-service/name c)
                    [:div.row (merge {:key (::t-service/business-id c)}
                                     (stylefy/use-style style/simple-result-card-row))
                     (str (::t-service/name c) " (" (::t-service/business-id c) ")")])))
         (doall
           (for [c (take 2 service-companies)]
             (when (::t-service/name c)
               [:div.row (merge {:key (::t-service/business-id c)}
                                (stylefy/use-style style/simple-result-card-row))
                (str (::t-service/name c) " (" (::t-service/business-id c) ")")]))))
       (when (> extra-companies 0)
         [:div.row (stylefy/use-style style/simple-result-card-row)
          (str " + " extra-companies (tr [:service-search :other-company]))])])))

(defn- result-card [e! admin?
                    {::t-service/keys [id name sub-type contact-address
                                       description contact-phone contact-email
                                       operator-name business-id transport-operator-id service-companies companies]
                     :as              service} service-search card-width]
  (let [sub-type-tr (tr-key [:enums ::t-service/sub-type])
        e-links [external-interface-links service]
        service-desc (t-service/localized-text-for "FI" description)
        service-companies (cond
                            (and service-companies companies) (merge service-companies companies)
                            (and service-companies (empty? companies)) service-companies
                            :else companies)
        open-link (fn [content]
                    [:a {:style (merge {:color "#fff"} style/result-card-header-link)
                         :href (str "/#/service/" transport-operator-id "/" id)}
                     content])
        card-padding (if (< card-width 767) "15px" "30px")]
    [:div.result-card (stylefy/use-style style/result-card)
     [:div
      [:div.result-title {:style (merge {:padding-left card-padding} style/result-card-title)}
       (open-link name)

       (when admin?
         [:div (stylefy/use-style style/result-card-delete)
          [delete-service-action e! id name (get service :show-delete-modal?)]])]
      (open-link
        [:span [:div (stylefy/use-style style/result-card-chevron)
                [ic/navigation-chevron-right {:color "#fff" :height 24}]]

         [:div (stylefy/use-style style/result-card-show-data)
          [:span {:style {:padding-top "10px"}} (tr [:service-search :show-all-information])]]])]


     [:div.row.result-body (stylefy/use-style style/result-card-body)
      [:div.col-sm-8.col-md-8 {:style {:padding-left card-padding}}
       [:h4 (stylefy/use-style style/result-card-header) (sub-type-tr sub-type)]
       (when-not (empty? service-desc)
         [:div.description {:style {:padding-bottom "20px"}}
          (common-ui/shortened-description service-desc 270)])

       [:div.result-interfaces e-links]]
      [:div.col-sm-3.col-md-3 {:style {:padding-left card-padding}}
       [:h4 (stylefy/use-style style/result-card-header) operator-name]
       [:div (stylefy/use-style style/simple-result-card-row)
        (str (tr [:field-labels :ote.db.transport-operator/business-id]) ": " business-id)]
       [:div (stylefy/use-style style/simple-result-card-row)
        (format-address contact-address)]
       (when contact-phone
         [:div (stylefy/use-style style/simple-result-card-row)
          (str contact-phone)])
       (when contact-email
         [:div (stylefy/use-style style/simple-result-card-row)
          contact-email])
       (list-service-companies service-companies service-search)]]]))

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
         [result-card e! (:admin? user) result service-search (:width app)]))

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

(defn operator-search [e! {:keys [results chip-results name] :as data}]
  [:div
   [ui/auto-complete {:floating-label-text (tr [:service-search :operator-search])
                      :floating-label-fixed true
                      :hintText (tr [:service-search :operator-search-placeholder])
                      :hint-style style-base/placeholder
                      :style {:width "100%"}
                      :filter (constantly true)          ;; no filter, backend returns what we want
                      :maxSearchResults 12
                      :dataSource (parse-operator-data-source results)
                      :on-update-input #(e! (ss/->SetOperatorName %))
                      :search-text (or name "")
                      :on-new-request #(do
                                         (e! (ss/->AddOperator
                                              (aget % "business-id")
                                              (aget % "text")))
                                         (e! (ss/->UpdateSearchFilters nil)))}]

   [operator-result-chips e! chip-results]])

(defn- capitalize-operation-area-postal-code [sentence]
  (str/replace sentence #"([^A-Öa-ö0-9_])(\w)"
               (fn [[_ before capitalize]]
                 (str before (str/upper-case capitalize)))))

(defn- sort-places [places]
  (when-not (nil? places)
    (let [names (filter #(re-matches #"^\D+$" (:text %)) places)
          names (mapv #(update % :text str/capitalize) names)
          ;; Place names starting with a number, like postal code areas
          numeric (filter #(re-matches #"^\d.*" (:text %)) places)
          numeric (mapv (fn [val]
                          (update val :text capitalize-operation-area-postal-code))
                        numeric)]
      (concat
        (sort-by :text names)
        (sort-by :text numeric)))))

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
          :layout :row}

         {:type :component
          :name :operators
          :full-width? true
          :container-class "col-xs-12 col-sm-4 col-md-4"
          :component (fn [{data :data}]
                       [:span [operator-search e! data]])}

         {:name :text-search
          :type :string
          :container-class "col-xs-12 col-sm-4 col-md-4"
          :full-width? true
          :hint-text (tr [:service-search :text-search-placeholder])}

         {:id "transport-types"
          :name ::t-service/transport-type
          :type :chip-input
          :container-class "col-xs-12 col-sm-4 col-md-4"
          :full-width? true
          :full-width-input? false
          :suggestions-config {:text :text :value :text}
          :suggestions (transport-types-to-list transport-types)
          :open-on-focus? true}

         {:name ::t-service/operation-area
          :type :chip-input
          :container-class "col-xs-12 col-sm-4 col-md-4"
          :hint-text (tr [:service-search :operation-area-search-placeholder])
          :full-width? true
          :full-width-input? false
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
          :auto-select? true}

         {:id "sub-types"
          :name ::t-service/sub-type
          :label (tr [:service-search :type-search])
          :type :chip-input
          :container-class "col-xs-12 col-sm-4 col-md-4"
          :full-width? true
          :full-width-input? false
          :suggestions-config {:text :text :value :text}
          :suggestions (sub-types-to-list (::t-service/sub-type facets))
          :open-on-focus? true}

         {:name               ::t-service/data-content
          :label              (tr [:service-search :data-content-search-label])
          :type               :chip-input
          :full-width?        true
          :container-class    "col-xs-12 col-sm-4 col-md-4"
          :auto-select?       true
          :open-on-focus?     true
          ;; Translate visible suggestion text, but keep the value intact.
          :suggestions        (mapv (fn [val]
                                      {:text  (tr [:enums ::t-service/interface-data-content val])
                                       :value val})
                                    t-service/interface-data-contents)
          :suggestions-config {:text :text :value :value}
          :is-empty?          validation/empty-enum-dropdown?})]
      filters]]))


(defn service-geojson [e! {:keys [resource geojson loading-geojson?]}]
  [:div.service-geojson
   [common-ui/linkify "/#/services" [:span [icons/arrow-back {:position "relative"
                                                              :top "6px"
                                                              :padding-right "5px"
                                                              :color style-base/link-color}]
                                     (tr [:service-search :back-link])]]
   [:span
    [:h3 (str (get-in resource ["features" 0 "properties" "transport-service" "name"])
              " GeoJSON")]

    (when (false? loading-geojson?)
      [ckan-service-viewer/viewer e! {:resource resource
                                      :geojson geojson}])]])

(defn service-search [e! _]
  (r/create-class
   {:component-will-unmount #(e! (ss/->SaveScrollPosition))
    :component-did-mount #(e! (ss/->RestoreScrollPosition))
    :reagent-render
    (fn [e! {{results :results :as service-search} :service-search
             params :params
             :as app}]
      [:div.service-search
       [filters-form e! service-search]
       (if (nil? results)
         [:div (tr [:service-search :no-filters])]
         [results-listing e! app])])}))
