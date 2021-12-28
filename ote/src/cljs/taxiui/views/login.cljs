(ns taxiui.views.login
  "Login page view"
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]
            [ote.app.controller.login :as lc]
            [ote.localization :refer [tr tr-key]]
            [ote.ui.form :as form]
            [ote.ui.form-fields :as form-fields]
            [ote.ui.buttons :as buttons]
            [ote.ui.common :refer [linkify] :as common-ui]
            [ote.db.user :as user]
            [clojure.string :as str]
            [ote.ui.notification :as notification]
            [ote.style.buttons :as style-buttons]
            [taxiui.theme :as theme]))

(def login-on-enter (fn [event e!]
                      (when (= "Enter" (.-key event))
                        (e! (lc/->Login)))))

(defn login-form [e! {:keys [credentials failed? error in-progress?] :as login}]
  ; TODO: this looks visually absolutely horrible
  [:div {}
   [:div
    [:label {:for "name"} (tr [:field-labels :login :email-or-username])]
    [:input {:name "name"
             :type "text"
             :autoComplete "email"
             :required "true"
             :on-input #(e! (lc/->UpdateLoginCredentials {:email (-> % .-target .-value)}))
             :on-key-press #(login-on-enter % e!)}]]
   [:div
    [:label {:for "password"} (tr [:field-labels :login :password])]
    [:input {:name "password"
             :type "password"
             :required "true"
             :on-input #(e! (lc/->UpdateLoginCredentials {:password (-> % .-target .-value)}))
             :on-key-press #(login-on-enter % e!)}]]
   [:button (merge (stylefy/use-style style-buttons/primary-button)
                   {:on-click #(e! (lc/->Login))})
    (tr [:login :login-button])]])

(defn login-error
  [error]
  [:div {:style {:margin-top "1rem"}}
   (cond
     (#{:unconfirmed-email} error) [notification/notification {:type :warning}
                                    [:div
                                     [:p {:style {:margin-top 0}}
                                      (tr [:login :error error])]
                                     [linkify "#/confirm-email/resend-token" (tr [:common-texts :send-new-message])]]]
     (#{:login-error} error) [notification/notification {:type :error}
                              (tr [:login :error error])])])

(defn login
  [_ _]
  (fn [e! {:keys [credentials failed? error in-progress?] :as login}]
    [:main (stylefy/use-style theme/main-container)
     [:h2 "Kirjaudu sisään"]
     [login-form e! login]
     [login-error error]]))
