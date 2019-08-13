(ns ote.views.user
  "User's own info view"
  (:require [reagent.core :as r]
            [ote.ui.form :as form]
            [ote.db.user :as user]
            [ote.localization :refer [tr tr-key]]
            [ote.ui.buttons :as buttons]
            [ote.app.controller.login :as lc]
            [clojure.string :as str]
            [ote.ui.list-header :as list-header]
            [ote.ui.notification :as notification]
            [ote.app.controller.front-page :as fp]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]))

(defn merge-user-data [user form-data]
  (merge (select-keys user #{:username :name :email})
         form-data))

(defn user
  "Edit own user info"
  [e! app]
  (r/create-class
    {:component-did-mount #(e! (lc/->UserSettingsInit false))
     :component-will-unmount #(e! (lc/->UserSettingsInit false))
     :reagent-render
     (fn
       [e! {:keys [form-data email-taken password-incorrect? edit-response] :as user}]

       [:div.user-edit.col-xs-12.col-sm-8.col-md-8.col-lg-6
        [list-header/header app (tr [:common-texts :user-menu-profile])]

        (if (:success? edit-response)
          ;; After submitting a successful email change action display only info box and action button
          [:div
           (when (:email-changed? edit-response)
             [:div {:style {:margin-bottom "2rem"}}
              [notification/notification {:text (str (tr [:user :change-email-confirmation-sent]) " " (:new-email edit-response))
                                          :type :success}]])
           [:a (merge {:href (str "#/own-services")
                       :on-click #(do (.preventDefault %)
                                      (e! (fp/->ChangePage :own-services nil)))}
                      (stylefy/use-style style-base/blue-link-with-icon))
            (tr [:front-page :move-to-services-page])]]
          ;; When arriving to view or after a failed form submission: display form.
          [form/form
           {:update! #(e! (lc/->UpdateUser %))
            :name->label (tr-key [:register :fields])
            :footer-fn (fn [data]
                         [:div {:style {:margin-top "1em"}}
                          [buttons/save {:on-click #(e! (lc/->SaveUser
                                                          (merge-user-data
                                                            user
                                                            (form/without-form-metadata data))))
                                         :disabled (form/disable-save? data)}
                           (tr [:buttons :save])]
                          [buttons/cancel {:on-click #(e! (lc/->UserSettingsInit true))}
                           (tr [:buttons :cancel])]])}
           [(form/group
              {:expandable? false :columns 3 :layout :raw :card? false}

              {:name :name :type :string :required? true :full-width? true
               :placeholder (tr [:register :placeholder :name])
               :should-update-check form/always-update}
              {:name :email :type :string :autocomplete "email" :required? true
               :full-width? true :placeholder (tr [:register :placeholder :email])
               :validate [(fn [data _]
                            (when (not (user/email-valid? data))
                              (tr [:common-texts :required-field])))
                          (fn [data _]
                            (when (and email-taken (email-taken data))
                              (tr [:register :errors :email-taken])))]
               :should-update-check form/always-update}

              (when (and (not (clojure.string/blank? (:email form-data)))
                         (not= (:email form-data) (:email app)))
                (form/info (tr [:user :change-email-warning])))

              (form/subtitle :h3 (tr [:register :change-password]) {:margin-top "3rem"})
              {:name :password :type :string :password? true
               :label (tr [:register :fields :new-password])
               :full-width? true
               :validate [(fn [data _]
                            (when (and (not (str/blank? data))
                                       (not (user/password-valid? data)))
                              (tr [:register :errors :password-not-valid])))]
               :should-update-check form/always-update}
              {:name :confirm :type :string :password? true
               :full-width? true
               :validate [(fn [data row]
                            (when (not= data (:password row))
                              (tr [:register :errors :passwords-must-match])))]
               :should-update-check form/always-update}

              ;; Show current password field if it is required for update
              (form/subtitle :h3 (tr [:register :confirm-changes]) {:margin-top "3rem"})
              {:name :current-password :type :string :password? true
               :full-width? true
               :required? (seq (::form/modified form-data))
               :validate [(fn [_ _]
                            (when password-incorrect?
                              (tr [:login :error :incorrect-password])))]})]
           (merge-user-data user form-data)])])}))
