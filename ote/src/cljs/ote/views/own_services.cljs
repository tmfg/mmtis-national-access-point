(ns ote.views.own-services
  (:require [clojure.string :as s]
            [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [clojure.string :as str]
            [stylefy.core :as stylefy]
            [ote.db.modification :as modification]
            [ote.db.transport-service :as t-service]
            [ote.db.transport-operator :as t-operator]
            [ote.time :as time]
            [ote.localization :refer [tr tr-key]]
            [ote.app.utils :as utils]
            [ote.ui.common :refer [linkify]]
            [ote.ui.buttons :as buttons]
            [ote.ui.warning_msg :as warning-msg]
            [ote.ui.page :as page]
            [ote.style.base :as style-base]
            [ote.style.buttons :as style-buttons]
            [ote.style.dialog :as style-dialog]
            [ote.theme.colors :as colors]
            [ote.ui.form-fields :as form-fields]
            [ote.ui.info :as info]
            [ote.app.controller.front-page :as fp-controller]
            [ote.app.controller.own-services :as os-controller]
            [ote.app.controller.transport-operator :as to-controller]
            [ote.app.controller.transport-service :as ts-controller]
            [ote.views.transport-service :as transport-service]
            [ote.ui.common :as common]))

(def ic-warning [ic/alert-warning {:style {:color colors/negative-button
                                           :margin-bottom "5px"}}])

(defn delete-service-action [e! {::t-service/keys [id name]
                                 :keys [show-delete-modal?]
                                 :as service}]
  [:span
   [:a (merge {:href "#"
               :id (str "delete-service-button" id)
               :on-click #(do
                            (.preventDefault %)
                            (e! (ts-controller/->DeleteTransportService id)))}
              (stylefy/use-style style-base/gray-link-with-icon))
    (ic/action-delete {:style {:width 24
                               :height 24
                               :margin-right "2px"
                               :color colors/icon-gray}})
    [:span {:style {:padding-top "4px"}} (tr [:buttons :delete])]]
   (when show-delete-modal?
     [ui/dialog
      {:open true
       :actionsContainerStyle style-dialog/dialog-action-container
       :title (tr [:dialog :delete-transport-service :title])
       :actions [(r/as-element
                   [buttons/cancel
                    {:on-click #(e! (ts-controller/->CancelDeleteTransportService id))}
                    (tr [:buttons :cancel])])
                 (r/as-element
                   [buttons/delete
                    {:icon (ic/action-delete-forever)
                     :on-click #(e! (ts-controller/->ConfirmDeleteTransportService id))}
                    (tr [:buttons :delete])])]}
      (tr [:dialog :delete-transport-service :confirm] {:name name})])])

(defn- service-errors
  [{::t-service/keys [transport-type interface-types sub-type name]
    ::modification/keys [created] :as service}]
  (when (and (= sub-type :schedule) (not ((set interface-types) :route-and-schedule)))
    (let [tr-types (set transport-type)]
      (cond
        (tr-types :road)
        {:service-name name
         :error :no-schedule-road
         :created created}
        (tr-types :sea)
        {:service-name name
         :error :no-schedule-sea
         :created created}))))

(defn transport-services-table-rows [e! services transport-operator-id]
  [ui/table-body {:class "table-body"
                  :display-row-checkbox false}
   (doall
     (map
       (fn [{::t-service/keys [id type sub-type interface-types published validate re-edit name]
             ::modification/keys [created modified] :as row}]
         (let [service-state (ts-controller/service-state validate re-edit published)]
           ^{:key id}
           [ui/table-row {:selectable false :display-border false :style {:border-bottom (str "1px solid" colors/gray650)}}
            [ui/table-row-column {:class "table-col-style-semi-wrap"
                                  :style {:width "20%"}}
             [:a (merge {:href (str "/#/edit-service/" id)
                         :on-click #(do (.preventDefault %)
                                        (e! (fp-controller/->ChangePage :edit-service {:id id})))}
                        (stylefy/use-sub-style style-base/basic-table :link)) name]]
            [ui/table-row-column {:class "hidden-xs table-col-style-semi-wrap"
                                  :style {:overflow "visible"
                                          :width "20%"}}
             (cond
               ;; Published but with errors
               (service-errors row)
               [:span (stylefy/use-style style-base/icon-with-text)
                (tr [:field-labels :transport-service ::t-service/published?-values service-state])
                [ic/alert-warning {:style {:color colors/negative-button
                                           :margin-left "0.5rem"
                                           :margin-bottom "5px"}}]]
               ;; When state in validation
               (= :validation service-state)
               [:span (tr [:field-labels :transport-service ::t-service/published?-values service-state])
                [common/tooltip-icon {:text (tr [:own-services-page :service-in-validation-info])
                                      :len "medium"
                                      :pos "up"}]]
               ;; Normal case
               :else
               (tr [:field-labels :transport-service ::t-service/published?-values service-state]))]
            [ui/table-row-column {:class "hidden-xs hidden-sm table-col-style-semi-wrap" :style {:width "12%"}} (time/format-timestamp-for-ui modified)]
            [ui/table-row-column {:class "hidden-xs hidden-sm table-col-style-semi-wrap" :style {:width "13%"}} (time/format-timestamp-for-ui created)]
            [ui/table-row-column {:class "hidden-xs hidden-sm table-col-style-semi-wrap" :style {:width "15%"}}
             (if published
               (let [url (str "/export/geojson/" transport-operator-id "/" id)]
                 [linkify url
                  (tr [:own-services-page :open-geojson])
                  {:target "_blank"
                   :style {:text-decoration "none"
                           ::stylefy/mode {:hover {:text-decoration "underline"}}}}])
               [:span.draft
                (tr [:field-labels :transport-service ::t-service/published?-values false])])]
            [ui/table-row-column {:class "table-col-style-semi-wrap"
                                  :style {:width "20%"
                                          :padding-top "0.5rem"}}
             [:a (merge {:href (str "#/edit-service/" id)
                         :style {:padding-right "0.5rem"}
                         :id (str "edit-service-button" id)
                         :on-click #(do
                                      (.preventDefault %)
                                      (e! (fp-controller/->ChangePage :edit-service {:id id})))}
                        (stylefy/use-style style-base/gray-link-with-icon))
              (ic/content-create {:style {:width 24
                                          :height 24
                                          :margin-right "2px"
                                          :color colors/icon-gray}})
              [:span {:style {:padding-top "4px"}} (tr [:buttons :edit])]]
             [delete-service-action e! row]]]))
       services))])

(defn- route-error
  [errors]
  (let [error-keys (reduce
                     (fn [set error]
                       (conj set (:error error)))
                     #{}
                     errors)]
    [info/info-toggle
     (tr [:own-services-page :missing-service-info])
     [:div
      [:h5 {:style {:margin-top 0}} (tr [:own-services-page :flaws])]
      (doall
        (for [error errors]
          ^{:key (str error)}
          [:p {:style {:margin 0}}
           [:strong
            [ic/alert-warning {:style {:color colors/negative-button
                                       :margin-bottom "-5px"}}]
            (:service-name error)]
           " • " (tr [:own-services-page :missing-schedule])]))
      [:h5 (tr [:own-services-page :guide-to-completion])]
      [:span
       [:strong (tr [:own-services-page :add-schedule-to-service]) " "]
       (tr [:own-services-page :add-schedule-to-service-2]) " ("
       [ic/content-create {:style {:margin-bottom "-5px"}}]
       ") " (tr [:own-services-page :add-schedule-to-service-3])
       [:strong " " (tr [:service-viewer :published-interfaces])]]
      [:h5 (tr [:own-services-page :schedule-creation])]
      [:p {:style {:margin-top 0}}
       (tr [:own-services-page :if-no-interfaces])]
      (when (error-keys :no-schedule-road)
        [:p {:style {:margin 0}}
         [:strong (tr [:enums ::t-service/transport-type :road]) " • "]
         [linkify "https://www.traficom.fi/fi/asioi-kanssamme/saannollisen-henkiloliikenteen-reitti-ja-aikataulutiedon-digitoiminen"
          (tr [:own-services-page :open-rae])
          {:target "_blank"}]])
      (when (error-keys :no-schedule-sea)
        [:p {:style {:margin 0}}
         [:strong (tr [:enums ::t-service/transport-type :sea]) " • "]
         [linkify "/#/routes" (tr [:own-services-page :open-sea-rae]) {:target "_blank"}]])]
     {:icon ic-warning
      :default-open? true}]))

(defn transport-services-listing [e! transport-operator-id services section-label]
  (when (> (count services) 0)
    (let [errors (filter
                   #(not (nil? %))
                   (map
                     service-errors
                     services))]
      [:div.row (stylefy/use-style style-base/section-margin)
       [:div {:class "col-xs-12 col-md-12"}
        [:h4 section-label]
        [ui/table (stylefy/use-style style-base/basic-table)
         [ui/table-header {:adjust-for-checkbox false
                           :display-select-all false}
          [ui/table-row {:selectable false :style {:border-bottom (str "1px solid" colors/gray650)}}
           [ui/table-header-column {:class "table-header-semi-wrap" :style {:width "20%"}} (tr [:front-page :table-header-service-name])]
           [ui/table-header-column {:class "hidden-xs table-header-semi-wrap " :style {:width "20%"}} (tr [:front-page :table-header-NAP-status])]
           [ui/table-header-column {:class "hidden-xs hidden-sm table-header-semi-wrap " :style {:width "12%"}} (tr [:front-page :table-header-modified])]
           [ui/table-header-column {:class "hidden-xs hidden-sm table-header-semi-wrap " :style {:width "13%"}} (tr [:front-page :table-header-created])]
           [ui/table-header-column {:class "hidden-xs hidden-sm table-header-semi-wrap" :style {:width "15%"}} (tr [:front-page :table-header-service-url])]
           [ui/table-header-column {:class "table-header-semi-wrap " :style {:width "20%"}} (tr [:front-page :table-header-actions])]]]

         (transport-services-table-rows e! services transport-operator-id)]
        [:div {:style {:margin-top "2rem"}}
         (when (not-empty errors)
           (route-error errors))]]])))

(defn warn-about-test-server []
  (let [page-url (-> (.-location js/window))]
    (when (s/includes? (str page-url) "testi")              ;; if url contains "testi" show message -> testi.finap.fi
      [:div {:style {:border "red 4px dashed"}}
       [:p {:style {:padding "10px"}} "TÄMÄ ON TESTIPALVELU!"
        [:br]
        "Julkinen NAP-palvelukatalogi löytyy osoitteesta: " [:a {:href "https://finap.fi/#/services"} "finap.fi"]
        [:br]
        "Lisätietoa NAP-palvelukatalogin taustoista saat osoitteesta " [:a {:href (tr [:common-texts :footer-livi-url-link])}
                                                                        (tr [:common-texts :footer-livi-url-link])]]])))

(defn own-services-header
  [e! has-services? operator-services state]
  (let [operator (:transport-operator state)
        operators (:transport-operators-with-services state)]
    (when (and (not (empty? operators))
               (not (:new? operator)))
      [:div.row {:style {:margin-bottom "2rem"
                         :margin-top "3rem"
                         :align-items "center"
                         :display "flex"
                         :flex-wrap "wrap"}}
       [:div {:class "col-sm-6"}
        [:h4 {:style {:margin "0"}}
         (tr [:field-labels :select-transport-operator])]
        [form-fields/field
         {:element-id "select-operator-at-own-services"
          :name :select-transport-operator
          :type :selection
          :show-option #(::t-operator/name %)
          :update! #(e! (to-controller/->SelectOperator %))
          :options (mapv to-controller/take-operator-api-keys (mapv :transport-operator operators))
          :auto-width? true
          :class-name "mui-select-button"}
         (to-controller/take-operator-api-keys operator)]]
       [:div.col-sm-6.col-md-6
        [:a (merge {:id "btn-add-new-transport-operator"
                    :href "#/transport-operator"
                    :on-click #(do
                                 (.preventDefault %)
                                 (e! (to-controller/->CreateTransportOperator)))}
                   (stylefy/use-style style-buttons/outline-button))
         (tr [:buttons :add-new-transport-operator])]]])))

(defn table-container-for-own-services [e! has-services? operator-services state]
  [:div {:class "col-xs-12 col-md-12" :style {:margin-bottom "3rem"}}
   [:h3 {:style {:margin-bottom "2rem"}} (tr [:own-services-page :own-services])]
   [info/info-toggle
    (tr [:own-services-page :own-services-directions-short])
    [:p (tr [:own-services-page :own-services-info-long])]
    {:default-open? false}]
   [:a (merge {:href (str "#/new-service/" (get-in state [:transport-operator ::t-operator/id]))
               :id "new-service-button"
               :on-click #(do
                            (.preventDefault %)
                            (e! (ts-controller/->OpenTransportServiceTypePage)))}
              (stylefy/use-style style-buttons/primary-button))
    (tr [:buttons :add-transport-service])]
   (if (and has-services? (not (empty? operator-services)))
     ;; TRUE -> Table for transport services
     (doall
       (for [type t-service/transport-service-types
             :let [services (filter #(= (:ote.db.transport-service/type %) type) operator-services)]
             :when (not (empty? services))]
         ^{:key type}
         [transport-services-listing
          e!
          (get-in state [:transport-operator ::t-operator/id])
          services (tr [:titles type])])))])


(defn service-provider-controls
  [e! has-services? operator-services {::t-operator/keys [id name ckan-group-id business-id] :as operator} show-add-member-dialog?]
  [:div.col-xs-12
   [:div
    [:h2 {:style {:margin-bottom "2rem"}}
     name]
    [:div {:style {:margin-bottom "2rem"}}
     [:a (merge {:href (str "#/transport-operator/" id)
                 :style {:margin-right "2rem"}
                 :id "edit-transport-operator-btn"
                 :on-click #(do
                              (.preventDefault %)
                              (e! (fp-controller/->ChangePage :transport-operator {:id id})))}
                (stylefy/use-style style-base/blue-link-with-icon))
      (ic/content-create {:style {:width 20
                                  :height 20
                                  :margin-right "0.5rem"
                                  :color colors/primary}})
      (tr [:own-services-page :edit-business-id] {:business-id business-id})]
     [:a (merge {:href (str "#/transport-operator/" ckan-group-id "/users")
                 :id "operator-users-link"
                 :on-click #(do
                              (.preventDefault %)
                              (e! (fp-controller/->ChangePage :operator-users {:ckan-group-id ckan-group-id})))}
                (stylefy/use-style style-base/blue-link-with-icon))
      (ic/social-person {:style {:width 20
                                 :height 20
                                 :margin-right "0.5rem"
                                 :color colors/primary}})
      (tr [:buttons :manage-access-rights])]]]
   (let [has-assoc-services? (not
                               (and
                                 (empty? (::t-operator/own-associations operator))
                                 (empty? (::t-operator/associated-services operator))))]
     (if (and (not has-assoc-services?) (not (and has-services? (not (empty? operator-services)))))
       [:div
        [:p
         [:span {:style {:float "left"}}                    ;;this is done because translations with variables don't support markdown and we have to fix md and variables
          (tr [:own-services-page :own-services-new-provider1])
          [:strong name]]
         (tr [:own-services-page :own-services-new-provider2])]]))
   [:hr {:style {:border-bottom "0"}}]])

(defn- added-associations
  [e! a-services oa-services state]
  [:div
   [:h4 (tr [:own-services-page :other-service-associations])]
   (if (empty? a-services)
     [:span (stylefy/use-style style-base/gray-text)
      (tr [:own-services-page :no-associations] {:operator-name
                                                 (::t-operator/name (:transport-operator state))})]
     [:ul.unstyled
      (doall
        (for [as a-services]
          [:li {:key (:service-id as)}
           (str (:service-name as) " - (" (:operator-name as) ", " (:operator-business-id as) ")")]))])
   [:h4 (tr [:own-services-page :added-services])]
   (if (empty? oa-services)
     [:span (stylefy/use-style style-base/gray-text)
      (tr [:own-services-page :no-own-associations] {:operator-name
                                                     (::t-operator/name (:transport-operator state))})]
     [:ul.unstyled {:style {:display "inline-block"}}
      (doall
        (for [as oa-services]
          [:li {:key (:service-id as)
                :id (str "service-id-" (:service-id as))}
           [:div {:style {:display "flex"
                          :align-items "center"
                          :justify-content "space-between"
                          :padding "0.25rem 0"}}
            (str (:service-name as) " - (" (:operator-name as) ", " (:operator-business-id as) ")")
            [buttons/icon-button
             (merge
               {:on-click #(e! (os-controller/->RemoveSelection (:service-id as)))
                :id (str "delete-service-" (:service-id as))})
             [ic/content-clear]]]]))])])

(defn- add-associated-services
  [e! state]
  (let [suggestions (filter :service-id (:suggestions (:service-search state)))
        associated (::t-operator/associated-services (:transport-operator state))
        associated-ids (set (map :service-id associated))
        own-associated-ids (set (map :service-id (get-in state [:transport-operator ::t-operator/own-associations])))
        cur-op-id (::t-operator/id (:transport-operator state))
        filtered-suggestions (filter
                               #(and
                                  (not (associated-ids (:service-id %)))
                                  (not= cur-op-id (:operator-id %))
                                  (not (own-associated-ids (:service-id %))))
                               suggestions)
        current-operator (::t-operator/id (:transport-operator state))
        show-error? (:association-failed state)]
    [:div {:style {:margin-top "1rem"}}
     (if show-error?
       [warning-msg/warning-msg [:span (tr [:common-texts :save-failure])]])
     [form-fields/field
      {:type :chip-input
       :full-width? true
       :full-width-input? false
       :element-id "chip-input"
       :hint-text (tr [:own-services-page :associated-search-hint])
       :hint-style {:top "20px"}
       ;; No filter, back-end returns what we want
       :filter (constantly true)
       :suggestions-config {:text :service-operator :value :service-id}
       ;; Filter away transport-operators that have no business-id. (Note: It should be mandatory!)
       :suggestions filtered-suggestions
       :open-on-focus? true
       :on-update-input (utils/debounce #(e! (os-controller/->SearchInvolved %)) 300)
       ;; Select first match from autocomplete filter result list after pressing enter
       :auto-select? true
       :on-request-add (fn [chip]
                         (e! (os-controller/->AddSelection
                               (:service-name chip)
                               (:service-id chip)
                               (:operator-name chip)
                               (:operator-business-id chip)
                               current-operator
                               (:service-operator chip)))
                         chip)}
      nil]]))                                               ;We don't want to display the chips in the chip input so we pass nil

(defn- associated-services
  [e! state]
  (let [a-services (::t-operator/associated-services (:transport-operator state))
        oa-services (sort #(compare (:service-operator %1) (:service-operator %2)) (::t-operator/own-associations (:transport-operator state)))]
    [:div {:class "col-xs-12"
           :style {:margin-bottom "3rem"}}
     [:h3 (tr [:own-services-page :other-services-where-involved])]
     [info/info-toggle
      (tr [:common-texts :instructions])
      [:p (tr [:own-services-page :filling-info-content])]
      {:default-open? false}]
     [added-associations e! a-services oa-services state]
     [add-associated-services e! state]]))

(defn- operator-info-container
  [e! has-services? operator-services state]
  [:div
   [page/page-controls "" (tr [:common-texts :own-api-list])
    [own-services-header e! has-services? operator-services state]]
   [:div.container
    (if (empty? operator-services)
      [:div.row
       [service-provider-controls e! has-services? operator-services (:transport-operator state) (:show-add-member-dialog? state)]
       [associated-services e! state]
       [table-container-for-own-services e! has-services? operator-services state]]
      [:div.row
       [service-provider-controls e! has-services? operator-services (:transport-operator state) (:show-add-member-dialog? state)]
       [table-container-for-own-services e! has-services? operator-services state]
       [associated-services e! state]])]])

(defn- no-operator-texts
  [e! state]
  [:div {:style {:margin "3rem 0"}}
   [:p (tr [:own-services-page :own-services-no-providers])]
   [:a (merge {:href "#/transport-operator"
               :on-click #(do
                            (.preventDefault %)
                            (e! (to-controller/->CreateTransportOperator)))}
              (stylefy/use-style style-buttons/outline-button))
    (tr [:buttons :add-new-transport-operator])]])

(defn- no-operator
  "If the user has not added service-operator, we will ask to do so."
  [e! state]
  [page/page-controls "" (tr [:common-texts :own-api-list])
   [no-operator-texts e! state]])

(defn own-services [e! state]
  (e! (fp-controller/->EnsureTransportOperator))

  (fn [e! state]
    (if (and (:transport-operator-data-loaded? state)
             (not (contains? state :transport-operators-with-services)))
      [no-operator e! state]

      ;; Get services by default from first organization
      (let [page (:page state)
            has-services? (not (empty? (map #(get-in % [:transport-service-vector ::t-service/id]) state)))
            operator-services (some #(when (= (get-in state [:transport-operator ::t-operator/id]) (get-in % [:transport-operator ::t-operator/id]))
                                       %)
                                    (:transport-operators-with-services state))
            operator-services (if (empty? operator-services)
                                (:transport-service-vector (first (:transport-operators-with-services state)))
                                (:transport-service-vector state))]
        [:div
         (if has-services?
           [operator-info-container e! has-services? operator-services state]
           ;; Render service type selection page if no services added
           [transport-service/select-service-type e! state])]))))


