(ns ote.views.register
  "OTE registration form page."
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [ote.ui.form :as form]
            [ote.app.controller.login :as lc]
            [ote.localization :refer [tr tr-key]]
            [ote.ui.buttons :as buttons]
            [ote.ui.common :as common]

            [ote.db.user :as user]))


(defn register [e! {:keys [form-data email-taken username-taken] :as register} user]
  [:div {:style {:margin-top "40px"
                 :display "flex"
                 :flex-direction "column"
                 :align-items "center"
                 :width "100%"}}
   [:div {:style {:width "50%"}}
    [form/form
     {:update! #(e! (lc/->UpdateRegistrationForm %))
      :name->label (tr-key [:register :fields])
      :footer-fn (fn [data]
                   [:span
                    (when (some? user)
                      [common/help (tr [:register :errors :logged-in] user)])
                    [buttons/save {:on-click #(e! (lc/->Register (form/without-form-metadata data)))
                                   :disabled (or (some? user)
                                                 (form/disable-save? data))}
                     (tr [:register :label])]])}
     [(form/group
       {:label (tr [:register :label]) :expandable? false :columns 3}
       {:name :username :type :string :required? true :full-width? true
        :placeholder (tr [:register :placeholder :username])
        :validate [(fn [data _]
                     (when (not (user/username-valid? data))
                       (tr [:common-texts :required-field])))
                   (fn [data _]
                     (when (and username-taken (username-taken data))
                       (tr [:register :errors :username-taken])))]}
       {:name :name :type :string :required? true :full-width? true
        :placeholder (tr [:register :placeholder :name])}
       {:name :email :type :string :autocomplete "email" :required? true
        :full-width? true :placeholder (tr [:register :placeholder :email])
        :validate [(fn [data _]
                     (when (not (user/email-valid? data))
                       (tr [:common-texts :required-field])))
                   (fn [data _]
                     (when (and email-taken (email-taken data))
                       (tr [:register :errors :email-taken])))]}
       {:name :password :type :string :password? true :required? true
        :full-width? true
        :validate [(fn [data _]
                     (when (not (user/password-valid? data))
                       (tr [:register :errors :password-not-valid])))]}
       {:name :confirm :type :string :password? true :required? true
        :full-width? true
        :validate [(fn [data row]
                      (when (not= data (:password row))
                        (tr [:register :errors :passwords-must-match])))]})]
     form-data]]])
