(ns ote.views.pre-notices.pre-notice
  "Pre notice main form"
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [clojure.string :as str]
            [stylefy.core :as stylefy]
            [ote.db.transport-operator :as t-operator]
            [ote.db.common :as db-common]
            [ote.db.transit :as transit]
            [ote.localization :refer [tr tr-key]]
            [ote.theme.colors :as colors]
            [ote.style.base :as style-base]
            [ote.style.form :as style-form]
            [ote.style.dialog :as style-dialog]
            [ote.ui.buttons :as buttons]
            [ote.ui.form-fields :as form-fields]
            [ote.ui.form :as form]
            [ote.ui.leaflet :as leaflet]
            [ote.ui.circular_progress :as circular-progress]
            [ote.ui.common :as common]
            [ote.ui.info :as info]
            [ote.app.controller.pre-notices :as pre-notice]))

(def notice-types [:termination :new :schedule-change :route-change :other])
(def effective-date-descriptions [:year-start :school-start :school-end :season-schedule-change])

(defn- valid-notice? [pre-notice]
  (and
    (form/valid? pre-notice)
    (not (empty? (::transit/pre-notice-type pre-notice)))
    (not (str/blank? (::transit/route-description pre-notice)))
    (not (str/blank? (::transit/description pre-notice)))
    (not (empty? (::transit/effective-dates pre-notice)))
    (not (empty? (::transit/regions pre-notice)))))

(defn- valid-pre-notice-container [pre-notice]
  (let [valid-notice? (valid-notice? pre-notice)]
    (when (not valid-notice?)
      [:div {:style {:margin "1em 0em 1em 0em"}}
       [:span {:style {:color "#be0000" :padding-bottom "0.6em"}} (tr [:pre-notice-page :publish-missing-required])]])))

(defn footer [e! pre-notice sent?]
  (let [valid-notice? (valid-notice? pre-notice)]
    (when (not valid-notice?)
      [ui/card {:style {:margin "1em 0em 1em 0em"}}
       [ui/card-text {:style {:color "#be0000" :padding-bottom "0.6em"}} (tr [:pre-notice-page :publish-missing-required])]])
    [:div.col-xs-12.col-sm-6.col-md-6 {:style {:padding-top "20px"}}
     (when-not sent?
       [:span
        [buttons/save {:disabled (not valid-notice?)
                       :on-click #(do
                                    (.preventDefault %)
                                    (e! (pre-notice/->OpenSendModal)))}
         (tr [:buttons :save-and-send])]
        [buttons/save {:on-click #(do
                                    (.preventDefault %)
                                    (e! (pre-notice/->SaveToDb false)))}
         (tr [:buttons :save-as-draft])]])
     [buttons/cancel {:on-click #(do
                                   (.preventDefault %)
                                   (e! (pre-notice/->CancelNotice)))}
      (if sent?
        (tr [:buttons :close])
        (tr [:buttons :cancel]))]]))

(defn- pre-notice-send-modal [e! app]
  (when (:show-pre-notice-send-modal? app)
    [ui/dialog
     {:open true
      :actionsContainerStyle style-dialog/dialog-action-container
      :title (tr [:dialog :send-pre-notice :title])
      :actions [(r/as-element
                  [ui/flat-button
                   {:label (tr [:buttons :cancel])
                    :primary true
                    :on-click #(e! (pre-notice/->CloseSendModal))}])
                (r/as-element
                  [ui/raised-button
                   {:id "confirm-send-pre-notice"
                    :label (tr [:buttons :save-and-send])
                    :secondary true
                    :primary true
                    :on-click #(do
                                 (e! (pre-notice/->CloseSendModal))
                                 (e! (pre-notice/->SaveToDb true)))}])]}
     (tr [:dialog :send-pre-notice :confirm])]))

(defn select-operator [e! operator operators]
  [:div {:style {:margin-bottom "20px"}}
   [:div.row
    [form-fields/field
     {:label       (tr [:field-labels :select-transport-operator])
      :name        :select-transport-operator
      :type        :selection
      :show-option ::t-operator/name
      :update!     #(e! (pre-notice/->SelectOperatorForNotice %))
      :options     operators
      :auto-width? true}
     operator]]
   [:div.row
    [:div.col-xs-12.col-sm-12.col-md-8
     [:div.col-xs-8.col-sm-6.col-md-6
      [form-fields/field
       {:label     (tr [:field-labels ::t-operator/business-id])
        :name      ::t-operator/business-id
        :type      :string
        :update!   nil
        :disabled? true}
       (::t-operator/business-id operator)]

      [form-fields/field
       {:label     (tr [:field-labels ::db-common/street])
        :name      ::db-common/street
        :type      :string
        :update!   nil
        :disabled? true}
       (::db-common/street (::t-operator/visiting-address operator))]

      [form-fields/field
       {:label     (tr [:field-labels ::db-common/postal_code])
        :name      ::db-common/postal_code
        :type      :string
        :update!   nil
        :disabled? true}
       (::db-common/postal_code (::t-operator/visiting-address operator))]
      [form-fields/field
       {:label     (tr [:field-labels ::db-common/post_office])
        :name      ::db-common/post_office
        :type      :string
        :update!   nil
        :disabled? true}
       (::db-common/post_office (::t-operator/visiting-address operator))]]
     [:div.col-xs-8.col-sm-6.col-md-6
      [form-fields/field
       {:label     (tr [:field-labels ::t-operator/homepage])
        :name      ::t-operator/business-id
        :type      :string
        :update!   nil
        :disabled? true}
       (::t-operator/homepage operator)]
      [form-fields/field
       {:label     (tr [:field-labels ::t-operator/phone])
        :name      ::t-operator/phone
        :type      :string
        :update!   nil
        :disabled? true}
       (::t-operator/phone operator)]
      [form-fields/field
       {:label     (tr [:field-labels ::t-operator/gsm])
        :name      ::t-operator/gsm
        :type      :string
        :update!   nil
        :disabled? true}
       (::t-operator/gsm operator)]
      [form-fields/field
       {:label     (tr [:field-labels ::t-operator/email])
        :name      ::t-operator/email
        :type      :string
        :update!   nil
        :disabled? true}
       (::t-operator/email operator)]]]]])

(defn transport-type [e! sent?]
  (form/group
    {:label (tr [:pre-notice-page :notice-type-title])
     :columns 3
     :layout :row
     :card? false
     :top-border true}

    (merge
      (when sent?
        {:option-enabled? (constantly false)})
      {:name ::transit/pre-notice-type
       :type :checkbox-group
       :container-class "col-md-12"
       :header? false
       :required? true
       :options notice-types
       :show-option (tr-key [:enums ::transit/pre-notice-type])})

    (when sent?
      {:element-id "label-transit-description"
       :name :label-transit-description
       :label (tr [:field-labels :pre-notice ::transit/description])
       :type :text-label
       :full-width? true
       :h-style :p
       :h-inner-style {:margin-bottom "0px"}})
    (if sent?
      {:name ::transit/description
       :type :text
       :full-width? true}
      {:name ::transit/description
       :type :text-area
       :rows 1
       :hint-text (tr [:pre-notice-page :notice-description-hint])
       :full-width? true
       :required? true
       :container-class "col-xs-12 col-sm-12 col-md-6"})))

(defn effective-dates [e! sent?]
  (form/group
    {:label (tr [:pre-notice-page :effective-dates-title])
     :columns 3
     :layout :row
     :card? false
     :top-border true}

    (merge
      (when-not sent? {:add-label (tr [:buttons :add-new-effective-date])
                       :delete? true})
      {:name ::transit/effective-dates
       :type :table
       :id :effective-dates-table
       :table-style {:background-color colors/gray50}
       :table-fields [{:name ::transit/effective-date
                       :type :date-picker
                       :required? true
                       :disabled? sent?
                       :label (tr [:pre-notice-page :effective-date-from])}

                      {:name ::transit/effective-date-description
                       :type :autocomplete
                       :hint-text (tr [:form-help :autocomplete-hint])
                       :disabled? sent?
                       :label (tr [:pre-notice-page :effective-date-description])
                       :open-on-focus? true
                       :auto-select? true
                       :suggestions (mapv #(tr [:pre-notice-page :effective-date-descriptions %]) effective-date-descriptions)
                       :write #(assoc-in %1 [::transit/effective-date-description] #{%2})
                       :read #(first (get-in % [::transit/effective-date-description]))}]})))

(defn- notice-area-map [_]
  (r/create-class
   {:component-did-update leaflet/update-bounds-from-layers
    :component-did-mount leaflet/update-bounds-from-layers
    :reagent-render
    (fn [pre-notice]
      [leaflet/Map {:ref "leaflet"
                    :center      #js [65 25]
                    :zoomControl true
                    :zoom        5}
       (leaflet/background-tile-map)
       (doall
        (for [region (::transit/regions pre-notice)
              :let [region-geojson (get-in pre-notice [:regions region :geojson])]
              :when region-geojson]
          ^{:key region}
          [leaflet/GeoJSON {:data  region-geojson
                            :style {:color "green"}}]))])}))

(defn notice-area [e! sent?]
  (form/group
    {:label (tr [:pre-notice-page :route-and-area-information-title])
     :columns 3
     :layout :row
     :card? false
     :top-border true}

    {:type :component
     :name ::transit/notice-area
     :should-update-check (juxt ::transit/route-description ::transit/regions :regions)
     :read identity
     :disabled? sent?
     ;:required? true
     :is-empty? (fn [{regions ::transit/regions description ::transit/route-description}]
                  (or (empty? regions) (str/blank? description)))
     :container-style style-form/full-width
     :component (fn [{pre-notice :data}]
                  [:div
                   [:div.col-md-5
                    #_(when (empty-regions? pre-notice)
                        [:div (stylefy/use-style style-base/required-element)
                         (tr [:common-texts :required-field])])
                    [form-fields/field
                     {:id "route-description"
                      :label (tr [:field-labels :pre-notice ::transit/route-description])
                      :type :text-area
                      :hint-text (tr [:pre-notice-page :route-description-hint])
                      :full-width? true
                      :disabled? sent?
                      :required? true
                      :warning (when (str/blank? (::transit/route-description pre-notice))
                                 (tr [:common-texts :required-field]))
                      :rows 1
                      :update! #(e! (pre-notice/->EditSingleFormElement ::transit/route-description %))
                      }
                     (::transit/route-description pre-notice)]

                    (let [regions-with-show
                          (mapv #(assoc % :show (str (:id %) " " (:name %)))
                                (sort-by :id (vals (:regions pre-notice))))
                          selected-ids (set (::transit/regions pre-notice))]
                      [form-fields/field
                       {:id "regions"
                        :label (tr [:field-labels :pre-notice ::transit/regions])
                        :type :chip-input
                        :update! #(e! (pre-notice/->SelectedRegions %))
                        :full-width? true
                        :disabled? sent?
                        :suggestions-config {:text :show
                                             :value :id}
                        :suggestions (when (not sent?) (clj->js regions-with-show))
                        :max-results 25
                        :open-on-focus? true
                        :auto-select? true
                        :filter (fn [query key]
                                  (str/includes? (str/lower-case key)
                                                 (str/lower-case query)))
                        :warning (when (empty? (::transit/regions pre-notice))
                                   (tr [:common-texts :required-field]))}
                       (into #{}
                             (keep #(when (selected-ids (:id %)) %))
                             regions-with-show)])]
                   [:div.col-md-7
                    [notice-area-map pre-notice]]])}))

(defn notice-attachments [e! sent?]
  (form/group
    {:label (tr [:pre-notice-page :attachment-section-title])
     :columns 1
     :layout :raw
     :card? false
     :top-border true}

    {:name :attachment-instructions
     :type :component
     :full-width? true
     :component (fn [_]
                  [info/info-toggle
                   (tr [:common-texts :instructions])
                   [:div [:p (tr [:form-help :pre-notice-attatchment-info])]]
                   {:default-open? false}])}

    {:name ::transit/url
     :type :string
     :full-width? true
     :disabled? sent?
     :container-class "col-xs-12 col-sm-12 col-md-6"
     :update! #(e! (pre-notice/->EditForm %))}
    {:name :attachments
     :type :table
     :add-label (tr [:pre-notice-page :add-attachment])
     :add-label-disabled? (constantly sent?)
     :table-style {:background-color colors/gray50}
     :table-fields [{:name ::transit/attachment-file-name
                     :type :component
                     :read identity
                     :component (fn [{data :data}]
                                  (let [id (:ote.db.transit/id data)
                                        file-name (:ote.db.transit/attachment-file-name data)]
                                    [:div
                                     (if id
                                       (common/linkify (str "pre-notice/attachment/" id) file-name {:target "_blank"})
                                       file-name)]))}
                    {:name :attachment-file
                     :button-label (tr [:pre-notice-page :select-attachment])
                     :type :file-and-delete
                     :allowed-file-types [".pdf" ".png" ".jpeg"]
                     :disabled? sent?
                     :on-change #(e! (pre-notice/->UploadAttachment (.-target %)))
                     :on-delete #(e! (pre-notice/->DeleteAttachment %))}]}))


(defn- pre-notice-form [e! {:keys [pre-notice transport-operator] :as app}]
  (let [operators (mapv :transport-operator (:transport-operators-with-services app))
        sent? (= :sent (::transit/pre-notice-state pre-notice))
        form-options {:name->label (tr-key [:field-labels :pre-notice])
                      :footer-fn (r/partial footer e! sent?)
                      :update! #(e! (pre-notice/->EditForm %))}]
    [:div
     [common/back-link-with-event :pre-notices (tr [:pre-notice-page :back-to-pre-notices])]
     [:h1 (tr [:pre-notice-page :pre-notice-form-title])]
     [select-operator e! transport-operator operators]
     [form/form
      {:update! #(e! (pre-notice/->EditForm %))
       :name->label (tr-key [:field-labels :pre-notice])}
      [(transport-type e! sent?)
       (effective-dates e! sent?)
       (notice-area e! sent?)
       (notice-attachments e! sent?)]
      pre-notice]
     [pre-notice-send-modal e! app]]))

(defn new-pre-notice [e! app]
  (let [pre-notice (:pre-notice app)
        sent? (= :sent (::transit/pre-notice-state pre-notice))]
    [:div
     [:div.container {:style {:margin-top "40px" :padding-top "3rem"}}
      [pre-notice-form e! app]]
     [:div (stylefy/use-style style-base/form-footer)
      [:div.container
       [:div.col-xs-12.col-sm-12.col-md-12
        [valid-pre-notice-container pre-notice]
        [footer e! pre-notice sent?]]]]]))

(defn edit-pre-notice-by-id [e! app]
  (let [pre-notice (:pre-notice app)
        sent? (= :sent (::transit/pre-notice-state pre-notice))]
    (if (or (nil? pre-notice) (:loading pre-notice))
      [circular-progress/circular-progress]
      [:div
       [:div.container {:style {:margin-top "40px" :padding-top "3rem"}}
        [pre-notice-form e! app]]
       [:div (stylefy/use-style style-base/form-footer)
        [:div.container
         [:div.col-xs-12.col-sm-12.col-md-12
          [valid-pre-notice-container pre-notice]
          [footer e! pre-notice sent?]]]]])))
