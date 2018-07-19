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
            [ote.ui.form-fields :as form-fields]
            [ote.ui.common :refer [linkify]]))


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
  [:div {:style {:margin "2rem 0 2rem 0"
                 :padding "1rem 0.5rem 1.5rem 1rem"
                 :background-color "#f5f5f5"}}
   [:div
    [:h3 {:style {:margin-top "0.5rem"}} (tr [:login :no-account?])]
    [linkify "#/register" (tr [:login :no-account-button])]]

   [:div {:style {:margin-top "2rem"}}
    [:h3 (tr [:login :forgot-password?])]
    [linkify "#" (tr [:login :forgot-password-button])
     {:on-click #(do
                   (.preventDefault %)
                   (e! (fp-controller/->ToggleUserResetDialog)))}]]])

(defn login [e! {:keys [credentials failed? error in-progress?] :as login}]
  [:div.col-xs-12.col-md-6
   [login-form e! login]
   [login-action-cards e!]])
