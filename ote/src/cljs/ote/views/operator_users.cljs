(ns ote.views.operator-users
  (:require [ote.app.controller.operator-users :as ou]
            [ote.ui.table :as table]
            [ote.app.utils :as utils]
            [ote.localization :refer [tr tr-key]]
            [cljs-react-material-ui.reagent :as ui]
            [ote.ui.form-fields :as form-fields]
            [cljs-react-material-ui.icons :as ic]
            [stylefy.core :as stylefy]
            [ote.style.buttons :as style-buttons]
            [ote.theme.colors :as colors]
            [ote.ui.circular_progress :as prog]
            [ote.ui.buttons :as buttons]
            [reagent.core :as r]
            [ote.style.dialog :as style-dialog]))

(defn remove-modal
  [e! open? row-info operator-name]
  (let [token? (some? (get-in row-info [:member :token]))
        open? (true? open?)]
    [ui/dialog
     {:open open?
      :actionsContainerStyle style-dialog/dialog-action-container
      :title (tr [:transport-users-page :remove-dialog-title])
      :actions [(r/as-element
                  [buttons/cancel
                   {:on-click #(e! (ou/->CloseConfirmationDialog))}
                   (tr [:buttons :cancel])])
                (r/as-element
                  [buttons/delete
                   {:icon (ic/action-delete-forever)
                    :id "confirm-delete"
                    :on-click #(if token?
                                 (e! (ou/->RemoveToken (:member row-info) (:operator-id row-info)))
                                 (e! (ou/->RemoveMember (:member row-info) (:operator-id row-info))))}
                   (tr [:transport-users-page :confirm-remove])])]}
     (tr [:transport-users-page :remove-dialog-text]
       {:user-email (get-in row-info [:member :email]) :operator-name operator-name})]))

(defn access-table
  [e! user users operator-id]
  [:div#user-table-container
   [:h3 (tr [:transport-users-page :members])]
   [table/table {:stripedRows true
                 :key-fn #(or (:id %) (:token %))
                 :name->label identity
                 :show-row-hover? true
                 :no-outline-on-selection? true}
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
                (when (:authority-group-admin? user)
                  [:button#remove-member (merge
                                           {:on-click #(e! (ou/->OpenConfirmationDialog member operator-id))}
                                           (stylefy/use-style
                                             (merge style-buttons/svg-button
                                               {:display "flex"
                                                :align-items "center"})))
                   [ic/action-delete]
                   (tr [:buttons :delete])]))}]
    users]])

(defn invite-member
  [e! {:keys [new-member-email new-member-loading?] :as state} ckan-group-id]
  [:div
   [:form {:on-submit (fn [e]
                        (.preventDefault e)
                        (when (re-matches utils/email-regex new-member-email)
                          (e! (ou/->PostNewUser new-member-email ckan-group-id))))}
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
     [:button#add-member
      (if (and new-member-email (re-matches utils/email-regex new-member-email) (not new-member-loading?))
        (stylefy/use-style style-buttons/primary-button)
        (stylefy/use-style style-buttons/disabled-button))
      (tr [:transport-users-page :add-member])]
     (when new-member-loading?
       [:span {:style {:margin-left "1rem"}}
        [prog/circular-progress (tr [:common-texts :loading])]])]]])

(defn manage-access
  [e! state]
  (let [loaded? (get-in state [:manage-access :loaded?])
        access-users (get-in state [:manage-access :users])
        access-state (:manage-access state)
        operator-name (get-in state [:manage-access :operator-name])
        confirm (get-in state [:manage-access :confirmation])]
    [:div
     [:h1 (tr [:transport-users-page :manage-users])]
     [:h2 operator-name]
     (if loaded?
       [:div
        [access-table e! (get state :user) access-users (get-in state [:params :ckan-group-id])]
        (when (= true (get-in state [:user :authority-group-admin?]))
          [invite-member e! access-state (get-in state [:params :ckan-group-id])])]
       [prog/circular-progress (tr [:common-texts :loading])])
     [remove-modal e! (:open? confirm) confirm operator-name]]))

