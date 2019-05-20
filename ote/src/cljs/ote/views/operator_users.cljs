(ns ote.views.operator-users
  (:require [ote.app.controller.operator-users :as ou]
            [ote.ui.spinner :as spinner]
            [ote.ui.table :as table]
            [ote.app.utils :as utils]
            [ote.localization :refer [tr tr-key]]
            [ote.ui.form-fields :as form-fields]
            [cljs-react-material-ui.icons :as ic]
            [stylefy.core :as stylefy]
            [ote.style.buttons :as buttons]))

(defn access-table
  [e! users]
  [:div
   [:h3 (tr [:transport-users-page :members])]
   [table/table {:stripedRows true
                 :key-fn :id
                 :name->label identity
                 :show-row-hover? true
                 :no-selection? true}
    [{:name (tr [:transport-users-page :username])
      :read identity
      :format :fullname}
     {:name (tr [:register :fields :email])
      :read identity
      :format :email}
     {:name (tr [:front-page :table-header-actions])
      :read identity
      :format (fn [x]
                [:div
                 [:button (merge {:on-click #(println (:id x))}
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
    [form-fields/field {:type :string
                        :full-width? true
                        :name :add-member
                        :update! #(e! (ou/->EmailFieldOnChange %))
                        :label (tr [:transport-users-page :add-member])
                        :placeholder (tr [:transport-users-page :email-placeholder])
                        :on-enter #(println %)}
     new-member-email]
    [:div {:style {:display "flex"
                   :align-items "center"}}
     [:button
      (if (and new-member-email (re-matches utils/email-regex new-member-email) (not new-member-loading?))
        (stylefy/use-style buttons/primary-button)
        (stylefy/use-style buttons/disabled-button))
      (tr [:transport-users-page :add-member])]
     (when new-member-loading?
       [spinner/primary-loading {:style {:margin-left "1rem"}}])]]])

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
        [access-table e! access-users name]
        [invite-member e! access-state (get-in state [:params :operator-id])]]
       [spinner/primary-loading])]))
