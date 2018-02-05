(ns ote.views.admin
  "Admin panel views. Note this has a limited set of users and is not
  currently localized, all UI text is in Finnish."
  (:require [cljs-react-material-ui.reagent :as ui]
            [ote.ui.form-fields :as form-fields]
            [ote.app.controller.admin :as admin-controller]
            [ote.db.transport-service :as t-service]
            [ote.db.modification :as modification]
            [clojure.string :as str]
            [ote.app.controller.front-page :as fp]
            [ote.localization :refer [tr tr-key]]
            [ote.time :as time]))


(def published-types [:YES :NO :ALL])

(defn user-listing [e! app]
  (let [{:keys [loading? results user-filter]} (get-in app [:admin :user-listing])]
    [:div
     [form-fields/field {:type :string :label "Nimen tai sähköpostiosoitteen osa"
                         :update! #(e! (admin-controller/->UpdateUserFilter %))}
      user-filter]

     [ui/raised-button {:primary true
                        :disabled (str/blank? filter)
                        :on-click #(e! (admin-controller/->SearchUsers))}
      "Hae käyttäjiä"]

     (when loading?
       [:span "Ladataan käyttäjiä..."])

     (when results
       [:span
        [:div "Hakuehdoilla löytyi " (count results) " käyttäjää."]
        [ui/table {:selectable false}
         [ui/table-header {:adjust-for-checkbox false
                           :display-select-all false}
          [ui/table-row
           [ui/table-header-column "Käyttäjätunnus"]
           [ui/table-header-column "Nimi"]
           [ui/table-header-column "Sähköposti"]
           [ui/table-header-column "Organisaatiot"]]]
         [ui/table-body {:display-row-checkbox false}
          (doall
           (for [{:keys [username name email groups]} results]
             [ui/table-row {:selectable false}
              [ui/table-row-column username]
              [ui/table-row-column name]
              [ui/table-row-column email]
              [ui/table-row-column groups]]))]]])]))

(defn service-listing [e! app]
  (let [{:keys [loading? results service-filter operator-filter published-filter]} (get-in app [:admin :service-listing])]
    [:div.row
     [:div.row.col-md-5
     [form-fields/field {:type :string :label "Hae palvelun nimellä tai sen osalla"
                         :update! #(e! (admin-controller/->UpdateServiceFilter %))}
      service-filter]

     [ui/raised-button {:primary true
                        :disabled (str/blank? filter)
                        :on-click #(e! (admin-controller/->SearchServices))}
      "Hae"]
      ]
     [:div.col-md-5

     [form-fields/field {:type :string :label "Hae palveluntuottajan nimellä tai sen osalla"
                         :update! #(e! (admin-controller/->UpdateOperatorFilter %))}
      operator-filter]

     [ui/raised-button {:primary true
                        :disabled (str/blank? filter)
                        :on-click #(e! (admin-controller/->SearchServicesByOperator))}
      "Hae"]
      ]

     [:div.row.col-md-2
      [form-fields/field {:type :selection
                          :label "Julkaistu?"
                          :options published-types
                          :show-option (tr-key [:admin-page :published-types])
                          :update! #(e! (admin-controller/->UpdatePublishedFilter %))}
       published-filter]]
     [:div.row
     (when loading?
       [:span "Ladataan palveluita..."])

     (when results
       [:span
        [:div "Hakuehdoilla löytyi " (count results) " palvelua."]
        [ui/table {:selectable false}
         [ui/table-header {:adjust-for-checkbox false
                           :display-select-all false}
          [ui/table-row
           [ui/table-header-column {:style {:width "20%"}} "Nimi"]
           [ui/table-header-column {:style {:width "20%"}}  "Palveluntuottaja"]
           [ui/table-header-column {:style {:width "20%"}} "Tyyppi"]
           [ui/table-header-column {:style {:width "20%"}} "Alityyppi"]
           [ui/table-header-column {:style {:width "5%"}}  "Julkaistu"]
           [ui/table-header-column {:style {:width "15%"}} "Luotu"]]]
         [ui/table-body {:display-row-checkbox false}
          (doall
            (for [{::t-service/keys [id name operator-name type sub-type published?]
                   ::modification/keys [created] :as result} results]
              ^{:key (::t-service/id result)}
              [ui/table-row {:selectable false}
               [ui/table-row-column {:style {:width "20%"}}  [:a {:href "#" :on-click #(do
                                                                (.preventDefault %)
                                                                (e! (fp/->ChangePage :edit-service {:id id})))} name]]
               [ui/table-row-column {:style {:width "20%"}}  operator-name]
               [ui/table-row-column {:style {:width "20%"}} (tr [:enums :ote.db.transport-service/type (keyword type)])]
               [ui/table-row-column {:style {:width "20%"}} (tr [:enums :ote.db.transport-service/sub-type (keyword sub-type)])]
               [ui/table-row-column {:style {:width "5%"}} (if published? "Kyllä" "Ei") ]
               [ui/table-row-column {:style {:width "15%"}}  (time/format-timestamp-for-ui created)]]))]]])]]))

(defn admin-panel [e! app]
  (let [selected-tab (or (get-in app [:params :admin-page]) "users")]
    [ui/tabs {:value selected-tab
              :on-change #(e! (admin-controller/->ChangeAdminTab %))}
     [ui/tab {:label "Käyttäjät" :value "users"}
      [user-listing e! app]]
     [ui/tab {:label "Palvelut" :value "serivces"}
      [service-listing e! app]]]))
