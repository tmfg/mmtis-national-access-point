(ns ote.views.admin.validate-service
  "Services that are in validation."
  (:require [cljs-react-material-ui.reagent :as ui]
            [clojure.string :as str]
            [stylefy.core :as stylefy]
            [ote.localization :refer [tr tr-key]]
            [ote.time :as time]
            [ote.style.base :as style-base]
            [ote.ui.buttons :as buttons]
            [ote.ui.common :refer [linkify]]
            [ote.app.controller.admin-validation :as admin-validation]
            [ote.app.controller.front-page :as fp]
            [reagent.core :as r]
            [ote.style.dialog :as style-dialog]))

(defn page-controls [e! app]
  [:div])

(defn- open-publish-dialog [e! app]
  (when (get-in app [:admin :in-validation :modal])
    [ui/dialog
     {:open true
      :actionsContainerStyle style-dialog/dialog-action-container
      :title "Oletko varma, että haluat julkaista palvelun"
      :actions [(r/as-element
                  [buttons/cancel
                   {:on-click #(do
                                 (.preventDefault %)
                                 (e! (admin-validation/->CloseConfirmPublishModal)))}
                   (tr [:buttons :cancel])])
                (r/as-element
                  [buttons/save
                   {:on-click #(do
                                 (.preventDefault %)
                                 (e! (admin-validation/->PublishService (get-in app [:admin :in-validation :modal]))))}
                   (tr [:buttons :publish])])]}

     [:div "Julkaise painamalla julkaise"]]))

(defn validate-services [e! app]
  (let [services (get-in app [:admin :in-validation :results])]
    [:div.row
     [open-publish-dialog e! app]
     (when services
       [:span
        [ui/table {:selectable false}
         [ui/table-header {:class "table-header-wrap"
                           :adjust-for-checkbox false
                           :display-select-all false}
          [ui/table-row
           [ui/table-header-column {:class "table-header-wrap" :style {:width "20%"}} "Nimi"]
           [ui/table-header-column {:class "table-header-wrap" :style {:width "15%"}} "Palveluntuottaja"]
           [ui/table-header-column {:class "table-header-wrap" :style {:width "15%"}} "Tyyppi"]
           [ui/table-header-column {:class "table-header-wrap" :style {:width "15%"}} "Alityyppi"]
           [ui/table-header-column {:class "table-header-wrap" :style {:width "10%"}} "Tarkistettavaksi"]
           [ui/table-header-column {:class "table-header-wrap" :style {:width "10%"}} "Luotu / Muokattu"]
           [ui/table-header-column {:class "table-header-wrap" :style {:width "15%"}} "Julkaise"]]]
         [ui/table-body {:display-row-checkbox false}
          (doall
            (for [{:keys [id name operator-name type sub-type published validate created modified] :as result} services]
              ^{:key (:id result)}
              [ui/table-row {:selectable false}
               [ui/table-row-column (merge (stylefy/use-style style-base/table-col-style-wrap) {:width "20%"})
                [:a {:href (str "/edit-service/" id)
                     :on-click #(do
                                  (.preventDefault %)
                                  (e! (fp/->ChangePage :edit-service {:id id})))} name]]
               [ui/table-row-column (merge (stylefy/use-style style-base/table-col-style-wrap) {:width "15%"})
                operator-name]
               [ui/table-row-column (merge (stylefy/use-style style-base/table-col-style-wrap) {:width "15%"})
                (tr [:enums :ote.db.transport-service/type (keyword type)])]
               [ui/table-row-column (merge (stylefy/use-style style-base/table-col-style-wrap) {:width "15%"})
                (tr [:enums :ote.db.transport-service/sub-type (keyword sub-type)])]
               [ui/table-row-column (merge (stylefy/use-style style-base/table-col-style-wrap) {:width "10%"})
                (time/format-timestamp-for-ui validate)]
               [ui/table-row-column (merge (stylefy/use-style style-base/table-col-style-wrap) {:width "10%"})
                [:span (time/format-timestamp-for-ui created) [:br] (time/format-timestamp-for-ui modified)]]
               [ui/table-row-column (merge (stylefy/use-style style-base/table-col-style-wrap) {:width "15%"})
                [buttons/save-publish-table-row {:on-click #(e! (admin-validation/->OpenConfirmPublishModal id))}
                 "Julkaise"]]]))]]])]))
