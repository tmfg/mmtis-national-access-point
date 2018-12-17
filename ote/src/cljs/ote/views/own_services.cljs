(ns ote.views.own-services
  (:require [clojure.string :as s]
            [reagent.core :as reagent]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.icons :as icons]
            [ote.ui.common :refer [linkify ckan-iframe-dialog]]
            [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.ui.buttons :as buttons]
            [ote.app.controller.front-page :as fp]
            [ote.app.controller.login :as login]
            [ote.app.controller.transport-service :as ts]
            [ote.app.controller.transport-operator :as to]
            [ote.views.transport-service :as transport-service]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]
            [ote.db.transport-service :as t-service]
            [ote.db.transport-operator :as t-operator]
            [ote.db.modification :as modification]
            [ote.time :as time]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]
            [ote.style.front-page :as style-front-page]
            [reagent.core :as r]
            [ote.ui.form-fields :as form-fields]
            [ote.ui.common :as ui-common]
            [ote.ui.info :as info]
            [ote.views.transport-operator-selection :as t-operator-sel]
            [ote.ui.list-header :as list-header]
            [clojure.string :as str]))


(defn- delete-service-action [e! {::t-service/keys [id name]
                                  :keys [show-delete-modal?]
                                  :as service}]
  [:span
   [ui/icon-button {:href "#"
                    :on-click #(do
                                 (.preventDefault %)
                                 (e! (ts/->DeleteTransportService id)))}
    [ic/action-delete]]
   (when show-delete-modal?
     [ui/dialog
      {:open true
       :title (tr [:dialog :delete-transport-service :title])
       :actions [(r/as-element
                   [ui/flat-button
                    {:label (tr [:buttons :cancel])
                     :primary true
                     :on-click #(e! (ts/->CancelDeleteTransportService id))}])
                 (r/as-element
                   [ui/raised-button
                    {:label (tr [:buttons :delete])
                     :icon (ic/action-delete-forever)
                     :secondary true
                     :primary true
                     :on-click #(e! (ts/->ConfirmDeleteTransportService id))}])]}

      (tr [:dialog :delete-transport-service :confirm] {:name name})])])

(defn transport-services-table-rows [e! services transport-operator-id]
  [ui/table-body {:display-row-checkbox false}
   (doall
     (map-indexed
       (fn [i {::t-service/keys [id type published? name]
               ::modification/keys [created modified] :as row}]
         ^{:key i}
         [ui/table-row {:selectable false :display-border false}
          [ui/table-row-column
           [:a {:href "#" :on-click  #(do
                                        (.preventDefault %)
                                        (e! (fp/->ChangePage :edit-service {:id id})))} name]]
          [ui/table-row-column {:class "hidden-xs hidden-sm "}
           (if published?
             (let [url (str "/ote/export/geojson/" transport-operator-id "/" id)]
               [linkify url url {:target "_blank"}])
             [:span.draft
              (tr [:field-labels :transport-service ::t-service/published?-values false])])]
          [ui/table-row-column {:class "hidden-xs "} (tr [:field-labels :transport-service ::t-service/published?-values published?])]
          [ui/table-row-column {:class "hidden-xs hidden-sm "} (time/format-timestamp-for-ui modified)]
          [ui/table-row-column {:class "hidden-xs hidden-sm "} (time/format-timestamp-for-ui created)]
          [ui/table-row-column
           [ui/icon-button {:href "#" :on-click #(do
                                                   (.preventDefault %)
                                                   (e! (fp/->ChangePage :edit-service {:id id})))}
            [ic/content-create]]
           [delete-service-action e! row]]])
       services))])

(defn transport-services-listing [e! transport-operator-id services section-label]
  (when (> (count services) 0)
    [:div.row (stylefy/use-style style-base/section-margin)
     [:div {:class "col-xs-12 col-md-12"}
      [:h3 section-label]

      [ui/table (stylefy/use-style style-base/front-page-service-table)
       [ui/table-header {:adjust-for-checkbox false
                         :display-select-all false}
        [ui/table-row {:selectable false}
         [ui/table-header-column (tr [:front-page :table-header-service-name])]
         [ui/table-header-column {:class "hidden-xs hidden-sm "} (tr [:front-page :table-header-service-url])]
         [ui/table-header-column {:class "hidden-xs "} (tr [:front-page :table-header-NAP-status])]
         [ui/table-header-column {:class "hidden-xs hidden-sm "} (tr [:front-page :table-header-modified])]
         [ui/table-header-column {:class "hidden-xs hidden-sm "} (tr [:front-page :table-header-created])]
         [ui/table-header-column (tr [:front-page :table-header-actions])]]]

       (transport-services-table-rows e! services transport-operator-id)]]]))

(defn warn-about-test-server []
  (let [page-url (-> (.-location js/window))]
    (when (s/includes? (str page-url) "testi") ;; if url contains "testi" show message -> testi.finap.fi
      [:div {:style {:border "red 4px dashed"}}
       [:p {:style {:padding "10px"}} "TÄMÄ ON TESTIPALVELU!"
        [:br]
        "Julkinen NAP-palvelukatalogi löytyy osoitteesta: "  [:a {:href "https://finap.fi/ote/#/services"} "finap.fi" ]
        [:br]
        "Lisätietoa NAP-palvelukatalogin taustoista saat osoitteesta " [:a {:href "https://www.liikennevirasto.fi/nap"} "www.liikennevirasto.fi/nap" ]]])))

(defn table-container-for-own-services [e! has-services? operator-services state]
  [:div
   (warn-about-test-server)
   [list-header/header
    state
    (tr [:common-texts :own-api-list])
    (when (not (empty? operator-services))
      [ui/raised-button {:label    (tr [:buttons :add-transport-service])
                         :on-click #(do
                                      (.preventDefault %)
                                      (e! (ts/->OpenTransportServiceTypePage)))
                         :primary  true
                         :icon     (ic/content-add)}])
    [t-operator-sel/transport-operator-selection e! state true]]
   [:div.row
    [:div {:class "col-xs-12 col-md-12"}
     (if (and has-services? (not (empty? operator-services)))
       ;; TRUE -> Table for transport services
       (doall
         (for [type t-service/transport-service-types
               :let [services (filter #(= (:ote.db.transport-service/type %) type) operator-services)]
               :when (not (empty? services))]
           ^{:key type}
           [transport-services-listing
            e! (get-in state [:transport-operator ::t-operator/id])
            services (tr [:titles type])]))

       ;; FALSE -> explain user why table is empty
       [:div
        [:br]
        [:p (tr [:front-page :operator-dont-have-any-services])]
        [:div {:style {:padding-top "20px"}}]
        [ui/raised-button {:label (tr [:buttons :add-transport-service])
                           :on-click #(do
                                        (.preventDefault %)
                                        (e! (ts/->OpenTransportServiceTypePage)))
                           :primary  true
                           :icon (ic/content-add)}]])]]])

(defn no-operator
  "If user haven't added service-operator, we will ask to do so."
  [e! state]
  [:div
   [:div.row
    [:div {:class "col-xs-12 col-sm-12 col-md-12"}

     [:h1 (tr [:front-page :header-no-operator])]
     [:h3 (tr [:front-page :desc-to-add-new-operator])]
     (warn-about-test-server)

     [:p (tr [:front-page :desc-to-add-new-operator-2])]
     [:p (tr [:front-page :desc-to-add-new-operator-3])]
     [ui/raised-button {:label (tr [:front-page :move-to-organizations-page])
                        :primary true
                        :on-click #(do
                                     (.preventDefault %)
                                     (e! (fp/->ChangePage :operators nil)))
                        :style {:margin "20px 0px 20px 0px"}}]
     [:p (tr [:front-page :desc-to-add-new-operator-4])]

     [:div.row {:style {:padding-top "60px"}}
      [:p (tr [:front-page :desc-to-add-new-operator-5])]
      [ui/raised-button {:label (tr [:buttons :add-new-transport-operator])
                         :primary true
                         :on-click #(do
                                      (.preventDefault %)
                                      (e! (to/->CreateTransportOperator)))
                         :style {:margin-top "20px"}}]]]]])

(defn own-services [e! state]
  (e! (fp/->EnsureTransportOperator))
  (fn [e! state]
    (if (and (:transport-operator-data-loaded? state)
             (not (contains? state :transport-operators-with-services)))
      [no-operator e! state]

      ;; Get services by default from first organization
      (let [has-services? (not (empty? (map #(get-in % [:transport-service-vector ::t-service/id]) state)))
            operator-services (some #(when (= (get-in state [:transport-operator ::t-operator/id]) (get-in % [:transport-operator ::t-operator/id]))
                                       %)
                                    (:transport-operators-with-services state))
            operator-services (if (empty? operator-services)
                                (:transport-service-vector (first (:transport-operators-with-services state)))
                                (:transport-service-vector state))]
        [:div
         (if has-services?
           [table-container-for-own-services e! has-services? operator-services state]
           ;; Render service type selection page if no services added
           [transport-service/select-service-type e! state])]))))


