(ns ote.views.admin.authority-group-admin
  (:require [cljs-react-material-ui.reagent :as ui]
            [clojure.string :as str]
            [stylefy.core :as stylefy]
            [ote.localization :refer [tr tr-key]]
            [ote.time :as time]
            [ote.style.base :as style-base]
            [ote.ui.buttons :as buttons]
            [ote.ui.common :refer [linkify]]
            [ote.app.controller.admin-validation :as admin-validation]
            [ote.app.controller.taxi-prices :as taxi-prices-controller]
            [reagent.core :as r]
            [ote.style.dialog :as style-dialog]
            [ote.views.operator-users :as ou-v]
            [cljs-react-material-ui.icons :as ic]
            [taxiui.views.components.formatters :as formatters]
            [ote.ui.form-fields :as form-fields]
            [ote.ui.circular_progress :as prog]))

; Hello from previous developer!
;
; Before you edit this page, do note that this is a bastardization of the `ote.views.operator-users` namespace's main
; components. Therefore, if you make modifications in this namespace, you will probably need to make sure that the
; operator user editing page still works as intended.

(defn page-controls [e! state]
  (fn [e! state]
    (let [loaded? (get-in state [:manage-access :loaded?])
          access-state (:manage-access state)]
      [:div
       (if loaded?
         [:div
          [ou-v/invite-member e! access-state (get-in state [:params :ckan-group-id])]]
         [prog/circular-progress (tr [:common-texts :loading])])])))

(defn authority-group-admin [e! state]
  (fn [e! state]
    (let [loaded?       (get-in state [:manage-access :loaded?])
          access-users  (get-in state [:manage-access :users])
          operator-name (get-in state [:manage-access :operator-name])
          confirm       (get-in state [:manage-access :confirmation])]
      [:div
       [:h1 (tr [:transport-users-page :manage-users])]
       [:h2 operator-name]
       (if loaded?
         [:div
          [ou-v/access-table e! access-users (get-in state [:params :ckan-group-id])]]
         [prog/circular-progress (tr [:common-texts :loading])])
       [ou-v/remove-modal e! (:open? confirm) confirm operator-name]])))
