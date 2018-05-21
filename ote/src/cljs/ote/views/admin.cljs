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

(def id-filter-type [:operators :services :ALL])
(def published-types [:YES :NO :ALL])

(def groups-header-style {:height "10px" :padding "5px 0 5px 0"})
(def groups-row-style {:height "20px" :padding 0})

(defn groups-list [e1 groups]
  [:div {:style {:border-left "1px solid rgb(224, 224, 224)"}}
   (if (seq groups)
     [:div {:style {:padding "0 0 15px 10px"}}

      [ui/table {:selectable false
                 :fixed-header true
                 :body-style {:overflow-y "auto"
                              :max-height "100px"}}
       [ui/table-header {:adjust-for-checkbox false
                         :display-select-all false}
        [ui/table-row {:style groups-header-style}
         [ui/table-header-column {:style groups-header-style} "Nimi"]
         [ui/table-header-column {:style groups-header-style} "ID"]]]
       [ui/table-body {:display-row-checkbox false
                       :show-row-hover true}

        (doall
          (for [{:keys [title name]} groups]
            ^{:key (str "group-" name)}
            [ui/table-row {:selectable false
                           :style groups-row-style}
             [ui/table-row-column {:style groups-row-style} title]
             [ui/table-row-column {:style groups-row-style} name]]))]]]
     [:div {:style {:padding-left "10px"
                    :line-height "48px"}}
      "Ei palveluntuottajia."])])

(defn user-listing [e! app]
  (let [{:keys [loading? results user-filter]} (get-in app [:admin :user-listing])]
    [:div
     [:div {:style {:margin-bottom "25px"}}
      [form-fields/field {:style {:margin-right "10px"}
                          :type :string :label "Nimen tai sähköpostiosoitteen osa"
                          :update! #(e! (admin-controller/->UpdateUserFilter %))}
       user-filter]

      [ui/raised-button {:primary true
                         :disabled (str/blank? filter)
                         :on-click #(e! (admin-controller/->SearchUsers))
                         :label "Hae käyttäjiä"}]]

     (if loading?
       [:span "Ladataan käyttäjiä..."]
       [:div {:style {:margin-bottom "10px"}} "Hakuehdoilla löytyi " (count results) " käyttäjää."])

     (when (seq results)
       [:span
        [ui/table {:selectable false
                   :fixed-header false
                   :style {:width "auto" :table-layout "auto"}
                   :wrapper-style {:border-style "solid"
                                   :border-color "rgb(224, 224, 224)"
                                   :border-width "1px 1px 0 1px"}}
         [ui/table-header {:adjust-for-checkbox false
                           :display-select-all false}
          [ui/table-row
           [ui/table-header-column "Käyttäjätunnus"]
           [ui/table-header-column "Nimi"]
           [ui/table-header-column "Sähköposti"]
           [ui/table-header-column "Palveluntuottajat"]]]
         [ui/table-body {:display-row-checkbox false}
          (doall
            (for [{:keys [username name email groups]} results]
              ^{:key (str "user-" username)}
              [ui/table-row {:style {:border-bottom "3px solid rgb(224, 224, 224)"}
                             :selectable false}
               [ui/table-row-column username]
               [ui/table-row-column name]
               [ui/table-row-column email]
               [ui/table-row-column {:style {:padding 0}}
                 [groups-list e! groups]]]))]]])]))

(defn business-id-report [e! app]
  (let [{:keys [loading? business-id-filter results]} (get-in app [:admin :business-id-report])]
    [:div
      [:div.row
       [:h1 "Y-tunnus raportti"]
       [:p "Listataan joko Palveluntuottajien y-tunnukset, palveluihin lisättyjen yritysten y-tunnukset tai molemmat.
       Palveluista mukaan on otettu vain jo julkaistut palvelut."]]
      [:div.row
       [form-fields/field {:type :selection
                           :label "Y-tunnuksen lähde"
                           :options id-filter-type
                           :show-option (tr-key [:admin-page :business-id-filter])
                           :update! #(e! (admin-controller/->UpdateBusinessIdFilter %))}
        business-id-filter]
       [ui/raised-button {:primary true
                          :disabled (str/blank? filter)
                          :on-click #(e! (admin-controller/->GetBusinessIdReport))}
        "Hae raportti"]]

       (when loading?
         [:div.row "Ladataan raporttia..."])

       (if results
         [:div.row
          [:div "Hakuehdoilla löytyi " (count results) " yritystä."]
          [ui/table {:selectable false}
           [ui/table-header {:adjust-for-checkbox false
                             :display-select-all false}
            [ui/table-row
             [ui/table-header-column {:width "7%"} "Id"]
             [ui/table-header-column {:width "20%"} "Yritys"]
             [ui/table-header-column {:width "13%"} "Y-tunnus"]
             [ui/table-header-column {:width "15%"} "GSM"]
             [ui/table-header-column {:width "10%"} "Puhelin"]
             [ui/table-header-column {:width "20%"} "Sähköposti"]
             [ui/table-header-column {:width "15%"} "Lähde"]]]
           [ui/table-body {:display-row-checkbox false}
            (doall
             (for [{:keys [id operator business-id gsm phone email source]} results]
               [ui/table-row {:selectable false}
                [ui/table-row-column {:width "7%"} id]
                [ui/table-row-column {:width "20%"} operator]
                [ui/table-row-column {:width "13%"} business-id]
                [ui/table-row-column {:width "15%"} gsm]
                [ui/table-row-column {:width "10%"} phone]
                [ui/table-row-column {:width "20%"} email]
                [ui/table-row-column {:width "15%"} (if (= "service" source) "Palvelu" "Palveluntuottaja")]]))]]]
         [:div "Hakuehdoilla ei löydy yrityksiä"])]))

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
     [ui/tab {:label "Palvelut" :value "services"}
      [service-listing e! app]]
     [ui/tab {:label "Y-tunnus raportti" :value "businessid"}
      [business-id-report e! app]]]))
