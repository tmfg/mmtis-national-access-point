(ns ote.views.transport-service
  "Transport service related functionality"
  (:require [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [reagent.core :as r]
            [ote.db.transport-service :as t-service]
            [ote.db.transport-operator :as t-operator]
            [ote.localization :refer [tr tr-key]]
            [stylefy.core :as stylefy]
            [ote.theme.colors :as colors]
            [ote.style.base :as style-base]
            [ote.style.form :as style-form]
            [ote.style.dialog :as style-dialog]
            [ote.ui.buttons :as buttons]
            [ote.ui.circular_progress :as circular-progress]
            [ote.ui.info :as info]
            [ote.ui.form-fields :as form-fields]
            [ote.ui.common :as ui-common]
            [ote.app.controller.transport-service :as ts-controller]
            [ote.app.controller.transport-operator :as to]
            [ote.views.passenger-transportation :as pt]
            [ote.views.terminal :as terminal]
            [ote.views.parking :as parking]
            [ote.views.rental :as rental]))

(def modified-transport-service-types
  ;; Create order for service type selection dropdown
  [:taxi
   :request
   :schedule
   :terminal
   :rentals
   :parking])

(defn select-service-type [e! {:keys [transport-operator transport-service] :as state}]
  (let [disabled? (or (nil? (::t-service/sub-type transport-service))
                      (nil? (::t-operator/id transport-operator)))]
    [:div.row
     [:div {:class "col-sx-12 col-md-12"}
      [:div
       [:h1 (tr [:select-service-type-page :title-required-data])]]
      [:div.row
       [info/info-toggle
        (tr [:common-texts :instructions])
        [:span
         [:p (tr [:select-service-type-page :transport-service-type-selection-help-text])]
         [:p (tr [:select-service-type-page :transport-service-type-brokerage-help-text])]
         [:p {:style {:font-style "italic"}}
          (tr [:select-service-type-page :transport-service-type-selection-help-example])]]
        {:default-open? false}]]

      [:div.row
       [:div
        [:div {:class "col-sx-12 col-sm-4 col-md-4"}
         [form-fields/field

          {:label (tr [:field-labels :transport-service-type-subtype])
           :name ::t-service/sub-type
           :type :selection
           :update! #(e! (ts-controller/->SelectServiceType %))
           :show-option (tr-key [:enums ::t-service/sub-type])
           :options modified-transport-service-types
           :auto-width? true}

          (::t-service/sub-type transport-service)]]

        [:div {:class "col-sx-12 col-sm-4 col-md-4"}
         [form-fields/field
          {:label (tr [:field-labels :select-transport-operator])
           :name :select-transport-operator
           :type :selection
           :update! #(e! (to/->SelectOperator %))
           :show-option ::t-operator/name
           :options (mapv :transport-operator (:transport-operators-with-services state))
           :auto-width? true}

          transport-operator]]]]
      [:div.row
       [:div {:class "col-sx-12 col-sm-4 col-md-4"}
        [ui/raised-button {:id "own-service-next-btn"
                           :style {:margin-top "20px"}
                           :label (tr [:buttons :next])
                           :on-click #(e! (ts-controller/->NavigateToNewService))
                           :primary true
                           :disabled disabled?}]]]]]))

(defn edit-service-header-text [service-type]
  (case service-type
    :passenger-transportation (tr [:passenger-transportation-page :header-edit-passenger-transportation])
    :terminal (tr [:terminal-page :header-edit-terminal])
    :rentals (tr [:rentals-page :header-edit-rentals])
    :parking (tr [:parking-page :header-edit-parking])))

(defn new-service-header-text [service-type]
  (case service-type
    :passenger-transportation (tr [:passenger-transportation-page :header-new-passenger-transportation])
    :terminal (tr [:terminal-page :header-new-terminal])
    :rentals (tr [:rentals-page :header-new-rentals])
    :parking (tr [:parking-page :header-new-parking])))

(defn- license-info []
  [:div.container {:style {:padding-top "20px"
                           :padding-bottom "20px"}}
   (tr [:common-texts :nap-data-license])
   [ui-common/linkify (tr [:common-texts :nap-data-license-url]) (tr [:common-texts :nap-data-license-url-label]) {:target "_blank"}]
   "."])

(defn edit-service [e! type {service :transport-service :as app}]
  [:div
   [license-info]
   (case type
     :passenger-transportation [pt/passenger-transportation-info e! (:transport-service app) app]
     :terminal [terminal/terminal e! (:transport-service app) app]
     :rentals [rental/rental e! (:transport-service app) app]
     :parking [parking/parking e! (:transport-service app) app])])

(defn edit-service-by-id [e! {loaded? :transport-service-loaded? service :transport-service :as app}]
  (if (or (nil? service) (not loaded?))
    [circular-progress/circular-progress]
    ;; Render transport service page only if given id matches with the loaded id
    ;; This will prevent page render with "wrong" or "empty" transport-service data
    (when (= (get-in app [:params :id]) (str (get-in app [:transport-service ::t-service/id])))
      (let [sub-service (keyword (str "ote.db.transport-service/" (name (::t-service/type service))))
            admin-validating-id (get-in app [:admin :in-validation :validating])
            service-id (get service ::t-service/id)
            in-validation? (get-in service [sub-service ::t-service/validate])
            service-state (ts-controller/service-state (get-in service [sub-service ::t-service/validate])
                                                       (get-in service [sub-service ::t-service/re-edit])
                                                       (get-in service [sub-service ::t-service/published])
                                                       (not (nil? (get-in service [sub-service ::t-service/parent-id]))))
            show-editing-dialog? (:edit-dialog service)
            service-in-validation-text (if (= :re-validation service-state)
                                         (tr [:passenger-transportation-page :published-service-is-in-validation])
                                         (tr [:passenger-transportation-page :service-is-in-validation]))]
        [:div
         [ui-common/rotate-device-notice]
         [:div.container {:style {:margin-top "40px" :padding-top "3rem"}}
          [:h1 (edit-service-header-text (keyword (::t-service/type service)))]
          (when (ts-controller/in-readonly? in-validation? admin-validating-id service-id)
            [:div {:style {:margin-bottom "1.5rem"}}
             [:div (stylefy/use-style style-base/notification-container)
              [:div {:style {:display "inline-flex"}}
               [ic/action-info {:style {:margin-right "1rem" :color colors/purple-darker}}]
               [:div {:style {:padding-top "2px"}} service-in-validation-text]]]
             [buttons/save {:on-click #(do
                                         (.preventDefault %)
                                         (e! (ts-controller/->ToggleEditingDialog)))}
              (tr [:buttons :continue-editing])]])
          ;; Passenger transport service has sub type, and here it is shown to users
          (when (= :passenger-transportation (keyword (::t-service/type service)))
            [:p (stylefy/use-style style-form/subheader)
             (tr [:enums :ote.db.transport-service/sub-type
                  (get-in app [:transport-service ::t-service/sub-type])])])
          ;; Show service owner name only for service owners
          (when (ts-controller/is-service-owner? app)
            [:h2 {:style {:margin-top "-0.5rem"}}
             (get-in app [:transport-operator ::t-operator/name])])]
         ;; Render the form
         [edit-service e! (::t-service/type service) app]
         (when show-editing-dialog?
           [ui/dialog
            {:open true
             :actionsContainerStyle style-dialog/dialog-action-container
             :title (tr [:dialog :continue-editing :title])
             :actions [(r/as-element
                         [buttons/cancel
                          {:on-click #(do
                                        (.preventDefault %)
                                        (e! (ts-controller/->ToggleEditingDialog)))}
                          (tr [:buttons :cancel])])
                       (r/as-element
                         [buttons/save
                          {:icon (ic/action-delete-forever)
                           :on-click #(do
                                        (.preventDefault %)
                                        (e! (ts-controller/->ConfirmEditing)))}
                          (tr [:buttons :continue-editing])])]}
            (tr [:dialog :continue-editing :confirm])])]))))


(defn create-new-service
  "Render container and headers for empty service form"
  [e! app]
  (when (get-in app [:transport-service ::t-service/type])
    (let [service-sub-type (get-in app [:transport-service ::t-service/sub-type])
          service-type (get-in app [:transport-service ::t-service/type])
          new-header-text (new-service-header-text service-type)]

      [:div
       [ui-common/rotate-device-notice]
       [:div.container {:style {:margin-top "40px" :padding-top "3rem"}}
        [:h1 new-header-text]
        ;; Passenger transport service has sub type, and here it is shown to users
        (when (= :passenger-transportation service-type)
          [:p (stylefy/use-style style-form/subheader)
           (tr [:enums :ote.db.transport-service/sub-type
                (get-in app [:transport-service ::t-service/sub-type])])])

       [:div.row
        [:h2 (get-in app [:transport-operator ::t-operator/name])]]]
       [edit-service e! service-type app]])))
