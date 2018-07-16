(ns ote.views.login
  "Login page view"
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]
            [ote.app.controller.login :as lc]
            [ote.app.controller.front-page :as fp-controller]
            [ote.localization :refer [tr tr-key]]
            [ote.ui.form :as form]
            [ote.ui.form-fields :as form-fields]))


(defn login-form [e! {:keys [credentials failed? error in-progress?] :as login}]
  [:div.login-form
   (when failed?
     [:div (stylefy/use-style style-base/error-element)
      (tr [:login :error error])])
   [:h1 (tr [:login :label])]
   [form/form {:name->label (tr-key [:field-labels :login])
               :update! #(e! (lc/->UpdateLoginCredentials %))
               :footer-fn (fn [data]
                            [:span.login-dialog-footer
                             [ui/raised-button {:primary true
                                                :on-click #(e! (lc/->Login))
                                                :label (tr [:login :login-button])}]])}
    [(form/group
      {:expandable? false :columns 3 :layout :raw :card? false}
      {:name :email
       :label (tr [:field-labels :login :email-or-username])
       :type :string
       :autocomplete "email"
       :on-enter #(e! (lc/->Login))
       :full-width? true}
      {:name :password
       :autocomplete "password"
       :type :string
       :password? true
       :on-enter #(e! (lc/->Login))
       :full-width? true})]
    credentials]])

(defn login-action-cards [e!]
  [:div {:style {:padding-top "2em"}}
   [:div
    [:h5 (tr [:login :no-account?])]
    [:div (tr [:login :no-account-help])]
    [ui/flat-button {:on-click #(e! (fp-controller/->ToggleRegistrationDialog))
                     :label (tr [:login :no-account-button])
                     :primary true}]]

   [:div
    [:h5 (tr [:login :forgot-password?])]
    [:div (tr [:login :forgot-password-help])]
    [ui/flat-button {:on-click #(e! (fp-controller/->ToggleUserResetDialog))
                     :label (tr [:login :forgot-password-button])
                     :primary true}]]])

(defn login [e! {:keys [credentials failed? error in-progress?] :as login}]
  [:div {:style {:margin-top "40px"
                 :display "flex"
                 :flex-direction "column"
                 :align-items "center"
                 :width "100%"}}
   [:div.col-xs-12.col-md-6
    [login-form e! login]
    [login-action-cards e!]]])
