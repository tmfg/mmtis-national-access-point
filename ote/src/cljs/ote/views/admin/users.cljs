(ns ote.views.admin.users
  "Admin User List"
  (:require [cljs-react-material-ui.reagent :as ui]
            [ote.ui.form-fields :as form-fields]
            [ote.app.controller.admin :as admin-controller]
            [ote.db.transport-service :as t-service]
            [ote.db.transport-operator :as t-operator]
            [ote.db.modification :as modification]
            [clojure.string :as str]
            [ote.app.controller.front-page :as fp]
            [ote.localization :refer [tr tr-key]]
            [ote.ui.common :refer [linkify]]
            [ote.time :as time]
            [cljs-react-material-ui.icons :as ic]
            [reagent.core :as r]
            [ote.ui.common :as ui-common]
            [ote.ui.common :as common-ui]))

(defn- delete-user-action [e! {:keys [id show-delete-modal?] :as user}]
  [:span
   [ui/icon-button {:id       (str "delete-user-" id)
                    :href     "#"
                    :on-click #(do
                                 (.preventDefault %)
                                 (e! (admin-controller/->OpenDeleteUserModal id)))}
    [ic/action-delete]]
   (when show-delete-modal?
     [ui/dialog
      {:open    true
       :title   "Poista käyttäjä"
       :actions [(r/as-element
                   [ui/flat-button
                    {:label    (tr [:buttons :cancel])
                     :primary  true
                     :on-click #(do
                                  (.preventDefault %)
                                  (e! (admin-controller/->CancelDeleteUser id)))}])
                 (r/as-element
                   [ui/raised-button
                    {:label     (tr [:buttons :delete])
                     :icon      (ic/action-delete-forever)
                     :secondary true
                     :primary   true
                     :on-click  #(do
                                   (.preventDefault %)
                                   (e! (admin-controller/->ConfirmDeleteUser id)))}])]}

      [:div [:p "Oletko varma, että haluat poistaa käyttäjän?
      Käyttäjän lisäämät palvelut ja palveluntuottajat jätetään palveluun. Tämä poistaa vain käyttäjän."]
       [:p (str "Käyttäjän id: " id)]

       [form-fields/field {:name        :ensured-id
                           :type        :string
                           :full-width? true
                           :label       "Anna varmistukseksi käyttäjän id"
                           :update!     #(e! (admin-controller/->EnsureUserId id %))}
        (:ensured-id user)]]])])


(def groups-header-style {:height "10px" :padding "5px 0 5px 0"})
(def groups-row-style {:height "20px" :padding 0})

(defn groups-list [groups]
  [:div {:style {:border-left "1px solid rgb(224, 224, 224)"}}
   (if (seq groups)
     [:div {:style {:padding "0 0 15px 10px"}}

      [ui/table {:selectable   false
                 :fixed-header true
                 :body-style   {:overflow-y "auto"
                                :max-height "100px"}}
       [ui/table-header {:adjust-for-checkbox false
                         :display-select-all  false}
        [ui/table-row {:style groups-header-style}
         [ui/table-header-column {:style groups-header-style} "Nimi"]
         [ui/table-header-column {:style groups-header-style} "ID"]]]
       [ui/table-body {:display-row-checkbox false
                       :show-row-hover       true}

        (doall
          (for [{:keys [title name]} groups]
            ^{:key (str "group-" name)}
            [ui/table-row {:selectable false
                           :style      groups-row-style}
             [ui/table-row-column {:style groups-row-style} title]
             [ui/table-row-column {:style groups-row-style} name]]))]]]
     [:div {:style {:padding-left "10px"
                    :line-height  "48px"}}
      "Ei palveluntuottajia."])])

(defn user-listing [e! app]
  (let [{:keys [loading? results user-filter]} (get-in app [:admin :user-listing])]
    [:div
     [:div {:style {:margin-bottom "25px"}}
      [form-fields/field {:style   {:margin-right "10px"}
                          :type    :string :label "Nimen tai sähköpostiosoitteen osa"
                          :update! #(e! (admin-controller/->UpdateUserFilter %))}
       user-filter]

      [ui/raised-button {:primary  true
                         :disabled (str/blank? filter)
                         :on-click #(e! (admin-controller/->SearchUsers))
                         :label    "Hae käyttäjiä"}]]

     (if loading?
       [:span "Ladataan käyttäjiä..."]
       [:div {:style {:margin-bottom "10px"}} "Hakuehdoilla löytyi " (count results) " käyttäjää."])

     (when (seq results)
       [:span
        [ui/table {:selectable    false
                   :fixed-header  false
                   :style         {:width "auto" :table-layout "auto"}
                   :wrapper-style {:border-style "solid"
                                   :border-color "rgb(224, 224, 224)"
                                   :border-width "1px 1px 0 1px"}}
         [ui/table-header {:adjust-for-checkbox false
                           :display-select-all  false}
          [ui/table-row
           [ui/table-header-column "Käyttäjätunnus"]
           [ui/table-header-column "Nimi"]
           [ui/table-header-column "Sähköposti"]
           [ui/table-header-column "Palveluntuottajat"]
           [ui/table-header-column "Toiminnot"]]]

         [ui/table-body {:display-row-checkbox false}
          (doall
            (for [{:keys [id username name email groups] :as user} results]
              ^{:key (str "user-" username)}
              [ui/table-row {:style      {:border-bottom "3px solid rgb(224, 224, 224)"}
                             :selectable false}
               [ui/table-row-column username]
               [ui/table-row-column name]
               [ui/table-row-column email]
               [ui/table-row-column {:style {:padding 0}}
                [groups-list groups]]
               [ui/table-row-column [delete-user-action e! user]]
               ]))]]])]))
