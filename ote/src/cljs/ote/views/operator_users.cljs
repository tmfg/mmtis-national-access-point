(ns ote.views.operator-users
  (:require [ote.app.controller.operator-users :as ou]
            [ote.ui.table :as table]
            [ote.app.utils :as utils]
            [ote.localization :refer [tr tr-key]]
            [ote.ui.form-fields :as form-fields]
            [cljs-react-material-ui.icons :as ic]
            [stylefy.core :as stylefy]
            [ote.style.buttons :as buttons]
            [ote.theme.colors :as colors]
            [ote.ui.circular_progress :as prog]))

(defn access-table
  [e! users operator-id]
  [:div
   [:h3 (tr [:transport-users-page :members])]
   [table/table {:stripedRows true
                 :key-fn #(or (:id %) (:token %))
                 :name->label identity
                 :show-row-hover? true
                 :no-selection? true}
    [{:name (tr [:transport-users-page :username])
      :read identity
      :format (fn [row]
                (if (:pending? row)
                  [:span {:style {:display "flex"
                                  :align-items "center"}}
                   [ic/action-info {:style {:color colors/primary-light
                                            :margin-right "3px"}}]
                   (tr [:transport-users-page :invite-pending])]
                  (:fullname row)))}
     {:name (tr [:register :fields :email])
      :read :email}
     {:name (tr [:front-page :table-header-actions])
      :read identity
      :format (fn [member]
                [:div
                 [:button.remove-member (merge {:on-click #(e! (ou/->RemoveMember member operator-id))}
                                          (stylefy/use-style
                                            (merge buttons/svg-button
                                              {:display "flex"
                                               :align-items "center"})))
                  [ic/action-delete]
                  (tr [:buttons :delete])]])}]
    users]])

(defn invite-member
  [e! {:keys [new-member-email new-member-loading?] :as state} op-id]
  [:div
   [:form {:on-submit (fn [e]
                        (.preventDefault e)
                        (when (re-matches utils/email-regex new-member-email)
                          (e! (ou/->PostNewUser new-member-email op-id))))}
    [form-fields/field {:element-id "operator-user-email"
                        :type :string
                        :full-width? true
                        :name :add-member
                        :update! #(e! (ou/->EmailFieldOnChange %))
                        :label (tr [:transport-users-page :add-member])
                        :placeholder (tr [:transport-users-page :email-placeholder])}
     new-member-email]
    [:div {:style {:display "flex"
                   :align-items "center"}}
     [:button
      (if (and new-member-email (re-matches utils/email-regex new-member-email) (not new-member-loading?))
        (stylefy/use-style buttons/primary-button)
        (stylefy/use-style buttons/disabled-button))
      (tr [:transport-users-page :add-member])]
     (when new-member-loading?
       [:span {:style {:margin-left "1rem"}}
        [prog/circular-progress (tr [:common-texts :loading])]])]]])

(defn manage-access
  [e! state]
  (let [loaded? (get-in state [:manage-access :loaded?])
        access-users (get-in state [:manage-access :users])
        access-state (:manage-access state)
        name (get-in state [:manage-access :operator-name])]
    [:div
     [:h1 (tr [:transport-users-page :manage-users])]
     [:h2 name]
     (if loaded?
       [:div
        [access-table e! access-users (get-in state [:params :operator-id])]
        [invite-member e! access-state (get-in state [:params :operator-id])]]
       [prog/circular-progress (tr [:common-texts :loading])])]))

