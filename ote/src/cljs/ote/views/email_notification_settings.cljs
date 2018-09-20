(ns ote.views.email-notification-settings
  "Email notification settings"
  (:require [reagent.core :as reagent]
            [cljs-react-material-ui.reagent :as ui]
            [ote.ui.form :as form]
            [ote.db.user-notifications :as user-notifications]
            [ote.localization :refer [tr tr-key]]
            [reagent.core :as r]
            [ote.ui.buttons :as buttons]
            [ote.app.controller.email-notification-settings :as email-settings]))

(defn select-province [e! email-settings]
  [(form/group
     {:label   ""
      :columns 3
      :card?   false
      :layout  :row}

     {:label                (tr [:email-notification-settings-page :regions-description-text])
      :name                 ::user-notifications/finnish-regions
      :type                 :checkbox-group
      :show-option          (tr-key [:regions])
      :options              (map #(keyword (:id %)) (get email-settings :regions))
      :full-width?          true
      :container-class      "col-xs-12 col-sm-12 col-md-12"
      :use-label-width?     true
      :checkbox-group-style {:display "flex" :flex-wrap "wrap"}}
     )
   #_ (form/group
     {:label   ""
      :card?   false
      :columns 3
      :layout  :row}
     {:name ::user-notifications/service-changed-6-months-ago
      :type :checkbox})])

(defn- form-options [e!]
  {:name->label (tr-key [:field-labels :email-notification-settings])
   :update!     #(e! (email-settings/->UpdateSettings %))
   :name        #(tr [:olennaiset-tiedot :otsikot %])
   :footer-fn   (fn [_]
                  [:div
                   [buttons/save {:on-click #(e! (email-settings/->SaveEmailNotificationSettings))}
                    (tr [:buttons :save])]])})

(defn email-notification-settings-form [e! state]
  (let [form-data (get-in state [:email-settings :user-notifications])]
    [:div
     [:h1 (tr [:email-notification-settings-page :page-title])]
     [:p (tr [:email-notification-settings-page :page-description])]
     [:h2 (tr [:email-notification-settings-page :secondary-header])]
     [form/form
      (form-options e!)
      (select-province e! (:email-settings state))
      form-data]]))

(defn email-notification-settings [e! state]
  (if (and (not (get-in state [:email-settings :regions-loading])) (not (get-in state [:email-settings :user-notifications-loading])))
    [email-notification-settings-form e! state]
    [:div.loading [:img {:src "/base/images/loading-spinner.gif"}]]))