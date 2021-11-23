(ns ote.views.transport-service.transport-service
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
            [ote.views.transport-service.passenger-transportation :as pt]
            [ote.views.transport-service.terminal :as terminal]
            [ote.views.transport-service.parking :as parking]
            [ote.views.transport-service.rental :as rental]))

(defn new-service-header-text [service-type]
  (case service-type
    :passenger-transportation (tr [:passenger-transportation-page :header-new-passenger-transportation])
    :terminal (tr [:terminal-page :header-new-terminal])
    :rentals (tr [:rentals-page :header-new-rentals])
    :parking (tr [:parking-page :header-new-parking])))

(defn edit-service-header-text [service-type]
  (case service-type
    :passenger-transportation (tr [:passenger-transportation-page :header-edit-passenger-transportation])
    :terminal (tr [:terminal-page :header-edit-terminal])
    :rentals (tr [:rentals-page :header-edit-rentals])
    :parking (tr [:parking-page :header-edit-parking])))

(defn- license-info []
  [:div.container {:style {:margin-top "2rem"
                           :padding-bottom "1.5rem"}}
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
            service-type (get-in app [:transport-service ::t-service/type])
            service-sub-type (get-in app [:transport-service ::t-service/sub-type])
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
                                         (tr [:passenger-transportation-page :service-is-in-validation]))
            operator-name (get-in app [:service-operator ::t-operator/name])
            edit-header-text (edit-service-header-text service-type)]
        [:div
         [ui-common/rotate-device-notice]
         [:div.container {:style {:padding-top "3rem"}}
          [:h1 edit-header-text]
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

          (when (not (empty? (::t-service/name (sub-service service))))
            [:h3 (str (tr [:transport-services-common-page :service]) ": " (::t-service/name (sub-service service)))])
          [:div {:style {:padding-top "1rem"}}
           [:div [:strong (str (tr [:transport-services-common-page :title-transport-operator]) ": ")] operator-name]
           [:div [:strong (str (tr [:transport-services-common-page :service-sub-type]) ": ")]
            (tr [:enums :ote.db.transport-service/sub-type
                 service-sub-type])]]]
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
          new-header-text (new-service-header-text service-type)
          operator-name (get-in app [:transport-operator ::t-operator/name])]

      [:div
       [ui-common/rotate-device-notice]
       [:div.container {:style {:padding-top "3rem"}}
        [:h1 new-header-text]
        [:div {:style {:padding-top "1rem"}}
         [:div [:strong (str (tr [:transport-services-common-page :title-transport-operator]) ": ")] operator-name]
         [:div [:strong (str (tr [:transport-services-common-page :service-sub-type]) ": ")] (tr [:enums :ote.db.transport-service/sub-type
                                                                                                  service-sub-type])]]]
       [edit-service e! service-type app]])))
