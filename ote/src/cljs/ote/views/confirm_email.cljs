(ns ote.views.confirm-email
  "Email confirmation page"
  (:require [ote.app.controller.confirm-email :as ce]
            [ote.localization :refer [tr tr-key]]
            [ote.ui.notification :as notification]
            [ote.ui.circular_progress :as prog]
            [ote.ui.common :as common-ui]))

(defn confirm-success
  []
  [:div
   [notification/notification {:text (tr [:register :confirmation-success])
                               :type :success}]
   [:div {:style {:margin-top "1rem"}}
    [common-ui/linkify "/#/login" (tr [:login :login-button])]]])

(defn confirm-email
  [e! app]
  (let [loaded? (get-in app [:confirm-email :loaded?])
        success? (get-in app [:confirm-email :success?])]
    [:div
     [:h1 (tr [:register :confirm-email])]
     (if loaded?
       (if success?
         [confirm-success]
         [notification/notification {:type :warning}
          [:div
           [:p {:style {:margin 0}}
            (tr [:register :confirmation-failed])]
           [common-ui/linkify "#/confirm-email/resend-token" (tr [:common-texts :send-new-message])]]])
       [prog/circular-progress])]))
