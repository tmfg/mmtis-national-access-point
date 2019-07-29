(ns ote.views.user-edit
  (:require [ote.ui.form :as form]
            [ote.ui.list-header :as list-header]
            [ote.ui.buttons :as buttons]
            [ote.localization :refer [tr tr-key]]
            [reagent.core :as r]
            [ote.db.user :as user]
            [ote.app.controller.user-edit :as uec]
            [clojure.string :as str]))

(defn merge-user-data [user form-data]
  (merge (select-keys user #{:name :username :email})
    form-data))

(defn edit-user
  "Edit own user info"
  [e! app]
  (r/create-class
    {:reagent-render
     (fn
       [e! {:keys [user user-edit params] :as app}]
       (let [{:keys [email-taken username-taken form-data email-confirmed?]} user-edit]
         [:div.user-edit.col-xs-12.col-sm-8.col-md-8.col-lg-6
          [list-header/header app (tr [:common-texts :user-menu-profile])]
          (when user-edit
            [form/form
             {:update! #(e! (uec/->UpdateForm %))
              :name->label (tr-key [:register :fields])
              :footer-fn (fn [data]
                           [:div {:style {:margin-top "1em"}}
                            [buttons/save {:on-click #(e! (uec/->SaveUserInfo
                                                            (merge-user-data
                                                              user
                                                              (form/without-form-metadata data))
                                                            (:id params)))
                                           :disabled (form/disable-save? data)}
                             (tr [:buttons :save])]
                            [buttons/cancel {:on-click #(e! (uec/->CancelEdit))}
                             (tr [:buttons :cancel])]])}
             [(form/group
                {:expandable? false :columns 3 :layout :raw :card? false}

                {:name :username :type :string :required? true :full-width? true
                 :placeholder (tr [:register :placeholder :username])
                 :validate [(fn [data _]
                              (if (< (count data) 3)
                                (tr [:common-texts :required-field])
                                (when (not (user/username-valid? data))
                                  (tr [:register :errors :username-invalid]))))
                            (fn [data _]
                              (when (and username-taken (username-taken data))
                                (tr [:register :errors :username-taken])))]
                 :should-update-check form/always-update}

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
                (if email-confirmed?
                  (form/info "Käyttäjä on varmentanut sähköpostiosoitteen.")
                  (form/info "Käyttäjä ei ole varmentanut sähköpostiosoitetta."))
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
                 :should-update-check form/always-update})]
             (merge-user-data user-edit form-data)])]))}))
