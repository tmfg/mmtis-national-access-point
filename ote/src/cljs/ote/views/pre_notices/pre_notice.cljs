(ns ote.views.pre-notices.pre-notice
  "Pre notice main form"
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [ote.localization :refer [tr tr-key]]
            [ote.app.controller.pre-notices :as pre-notice]
            [ote.ui.buttons :as buttons]
            [ote.ui.form-fields :as form-fields]
            ;; db
            [ote.db.transport-operator :as t-operator]
            [ote.db.common :as db-common]
            [ote.db.transit :as transit]
            [ote.ui.form :as form]
            [ote.ui.common :as common]
            [cljs-react-material-ui.icons :as ic]
            [ote.style.form :as style-form]
            [stylefy.core :as stylefy]
            [ote.ui.leaflet :as leaflet]
            [ote.ui.mui-chip-input :as chip-input]
            [clojure.string :as str]))

(def notice-types [:termination :new :schedule-change :route-change :other])

(defn- pre-notice-send-modal [e! app]
  (when (:show-pre-notice-send-modal? app)
    [ui/dialog
     {:open true
      :title (tr [:dialog :send-pre-notice :title])
      :actions [(r/as-element
                  [ui/flat-button
                   {:label (tr [:buttons :cancel])
                    :primary true
                    :on-click #(e! (pre-notice/->CloseSendModal))}])
                (r/as-element
                  [ui/raised-button
                   {:label (tr [:buttons :save-and-send])
                    :icon (ic/action-delete-forever)
                    :secondary true
                    :primary true
                    :on-click #(do
                                 (e! (pre-notice/->CloseSendModal))
                                 (e! (pre-notice/->SaveToDb true)))}])]}
     (tr [:dialog :send-pre-notice :confirm])]))

(defn select-operator [e! operator operators]
  [:div
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
   [:div.row.col-xs-12.col-sm-12.col-md-8
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
      (::t-operator/email operator)]]]])

(defn transport-type [e! app]
  (fn [e! {pre-notice :pre-notice :as app}]
    (let [addition [form-fields/field
                    {:label     nil
                     :name      ::transit/other-type-description
                     :type      :string
                     :update!   #(e! (pre-notice/->EditSingleFormElement ::transit/other-type-description %))}
                    (::transit/other-type-description pre-notice)]]
    [:div {:style {:padding-top "20px"}}
    [form/form {:update! #(e! (pre-notice/->EditForm %))}
     [(form/group
        {:label  (tr [:pre-notice-page :notice-type-title])
         :columns 3
         :layout  :row}

        {:name            ::transit/pre-notice-type
         :should-update-check (juxt ::transit/pre-notice-type ::transit/other-type-description)
         :type            :checkbox-group
         :container-class "col-md-12"
         :header?         false
         :required?       true
         :options         notice-types
         :option-addition {:value :other :addition addition}
         :show-option     (tr-key [:enums ::transit/pre-notice-type])})]
     pre-notice]])))

(defn effective-dates [e! app]
  (fn [e! {pre-notice :pre-notice :as app}]
    (let [effective-dates (:ote.db.notice/effective-dates pre-notice)]
      [form/form {:update! #(e! (pre-notice/->EditForm %))}
       [(form/group
          {:label   (tr [:pre-notice-page :effective-dates-title])
           :columns 3
           :layout  :row}

          {:name         ::transit/effective-dates
           :type         :table
           :table-fields [{:name  ::transit/effective-date
                           :type  :date-picker
                           ;:required? true
                           :label (tr [:pre-notice-page :effective-date-from])}

                          {:name  ::transit/effective-date-description
                           :type  :string
                           :label (tr [:pre-notice-page :effective-date-description])
                           ;:required? true
                           }]
           :delete?      true
           :add-label    (tr [:buttons :add-new-effective-date])}
          )]
       pre-notice])))

(defn notice-area [e! app]
  (fn [e! {pre-notice :pre-notice :as app}]
    [:div {:style {:padding-top "0px"}}
     [:div (stylefy/use-style style-form/form-card)
      [:div (stylefy/use-style style-form/form-card-label) (tr [:pre-notice-page :route-and-area-information-title])]
      [:div (merge (stylefy/use-style style-form/form-card-body))
       [:div.row
        [:div.col-md-6
         [form-fields/field
          {:id "route-description"
           :label (tr [:field-labels :pre-notice ::transit/route-description])
           :type :string
           :hint-text (tr [:pre-notice-page :route-description-hint])
           :full-width? true
           :update! #(e! (pre-notice/->EditSingleFormElement ::transit/route-description %))}
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
             :suggestions-config {:text :show
                                  :value :id}
             :suggestions  (clj->js regions-with-show)
             :max-results 25
             :open-on-focus? true
             :auto-select? true
             :filter (fn [query key]
                       (str/includes? (str/lower-case key)
                                      (str/lower-case query)))}
            (into #{}
                  (keep #(when (selected-ids (:id %)) %))
                  regions-with-show)])]
        [:div.col-md-6
         [leaflet/Map {:ref         "notice-area-map"
                       :center      #js [65 25]
                       :zoomControl true
                       :zoom        5}
          (leaflet/background-tile-map)
          (doall
           (for [region (::transit/regions pre-notice)
                 :let [region-geojson (get-in pre-notice [:regions region :geojson])]
                 :when region-geojson]
             ^{:key region}
             [leaflet/GeoJSON {:data region-geojson
                               :style {:color "green"}}]))]]]]]]))

(defn notice-attatchments [e! app]
  (fn [e! {pre-notice :pre-notice :as app}]
    [:div {:style {:padding-top "20px"}}
     [form/form {:name->label (tr-key [:field-labels :pre-notice])
                 :update! #(e! (pre-notice/->EditForm %))}
      [(form/group
         {:label   (tr [:pre-notice-page :effective-dates-title])
          :columns 3
          :layout  :row}
         (form/info (tr [:form-help :pre-notice-attatchment-info]) {:type :generic})

         {:name ::transit/url
          :type :string
          }
         )]
      pre-notice]]))



(defn new-pre-notice [e! {:keys [transport-operator] :as app}]
  (let [operators (mapv :transport-operator (:transport-operators-with-services app))]
    [:span
     [:h1 (tr [:pre-notice-page :pre-notice-form-title])]
     ;; Select operator
     [pre-notice-send-modal e! app]
     [select-operator e! transport-operator operators]
     [transport-type e! app]
     [effective-dates e! app]
     [notice-area e! app]
     [notice-attatchments e! app]
     (when (not (pre-notice/valid-notice? (:route app)))
       [ui/card {:style {:margin "1em 0em 1em 0em"}}
        [ui/card-text {:style {:color "#be0000" :padding-bottom "0.6em"}} (tr [:pre-notice-page :publish-missing-required])]])
     [:div.col-xs-12.col-sm-6.col-md-6 {:style {:padding-top "20px"}}
      [buttons/save {:disabled (not (pre-notice/valid-notice? (:pre-notice app)))
                     :on-click #(do
                                  (.preventDefault %)
                                  (e! (pre-notice/->OpenSendModal)))}
       (tr [:buttons :save-and-send])]
      [buttons/save {:on-click #(do
                                  (.preventDefault %)
                                  (e! (pre-notice/->SaveToDb false)))}
       (tr [:buttons :save-as-draft])]
      [buttons/cancel {:on-click #(do
                                    (.preventDefault %)
                                    (e! (pre-notice/->CancelNotice)))}
       (tr [:buttons :cancel])]]]))

(defn edit-pre-notice-by-id [e! {:keys [pre-notice transport-operator] :as app}]
  (if (or (nil? pre-notice) (= :loading pre-notice))
    [:div.loading [:img {:src "/base/images/loading-spinner.gif"}]]
    (if (= (::transit/pre-notice-state pre-notice) :draft)
      (let [operators (mapv :transport-operator (:transport-operators-with-services app))]
        [:span
         [:h1 (tr [:pre-notice-page :pre-notice-form-title])]
         [pre-notice-send-modal e! app]
         ;; Select operator
         [select-operator e! transport-operator operators]
         [transport-type e! app]
         [effective-dates e! app]
         [notice-area e! app]
         [notice-attatchments e! app]
         (when (not (pre-notice/valid-notice? (:route app)))
           [ui/card {:style {:margin "1em 0em 1em 0em"}}
            [ui/card-text {:style {:color "#be0000" :padding-bottom "0.6em"}} (tr [:pre-notice-page :publish-missing-required])]])
         [:div.col-xs-12.col-sm-6.col-md-6 {:style {:padding-top "20px"}}
          [buttons/save {:disabled (not (pre-notice/valid-notice? (:pre-notice app)))
                         :on-click #(do
                                      (.preventDefault %)
                                      (e! (pre-notice/->OpenSendModal)))}
           (tr [:buttons :save-and-send])]
          [buttons/save {:on-click #(do
                                      (.preventDefault %)
                                      (e! (pre-notice/->SaveToDb false)))}
           (tr [:buttons :save-as-draft])]
          [buttons/cancel {:on-click #(do
                                        (.preventDefault %)
                                        (e! (pre-notice/->CancelNotice)))}
           (tr [:buttons :cancel])]]])
      [:span "Lähetettyä ilmoitusta ei voi enää muokata!"])))
