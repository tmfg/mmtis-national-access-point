(ns ote.views.admin.admin
  "Admin panel views. Note this has a limited set of users and is not
  currently localized, all UI text is in Finnish."
  (:require [cljs-react-material-ui.reagent :as ui]
            [clojure.string :as str]
            [cljs-react-material-ui.icons :as ic]
            [reagent.core :as r]
            [stylefy.core :as stylefy]
            [ote.db.transport-operator :as t-operator]
            [ote.localization :refer [tr tr-key]]
            [ote.ui.buttons :as buttons]
            [ote.style.base :as style-base]
            [ote.style.dialog :as style-dialog]
            [ote.ui.page :as page]
            [ote.ui.tabs :as tabs]
            [ote.ui.form-fields :as form-fields]
            [ote.app.controller.admin :as admin-controller]
            [ote.app.controller.front-page :as fp]
            [ote.views.admin.authority-group-admin :as authority-group-admin]
            [ote.views.admin.interfaces :as interfaces]
            [ote.views.admin.reports :as report-view]
            [ote.views.admin.users :as users]
            [ote.views.admin.service-list :as service-list]
            [ote.views.admin.validate-service :as validate-view]
            [ote.views.admin.sea-routes :as sea-routes]
            [ote.views.admin.taxi-prices :as taxi-prices]
            [ote.views.admin.netex :as netex]
            [ote.views.admin.company-csv :as companycsv]))

(def id-filter-type [:operators :services :ALL])

(defn- delete-transport-operator-action [e! {::t-operator/keys [id name]
                                             :keys [show-delete-modal?]
                                             :as operator}]
  [:span
   [ui/icon-button {:id (str "delete-operator-" id)
                    :href "#"
                    :on-click #(do
                                 (.preventDefault %)
                                 (e! (admin-controller/->OpenDeleteOperatorModal id)))}
    [ic/action-delete]]
   (when show-delete-modal?
     [ui/dialog
      {:open true
       :actionsContainerStyle style-dialog/dialog-action-container
       :title "Poista palveluntuottaja ja kaikki sen tiedot"
       :actions [(r/as-element
                   [ui/flat-button
                    {:label (tr [:buttons :cancel])
                     :primary true
                     :on-click #(do
                                  (.preventDefault %)
                                  (e! (admin-controller/->CancelDeleteOperator id)))}])
                 (r/as-element
                   [ui/raised-button
                    {:label (tr [:buttons :delete])
                     :icon (ic/action-delete-forever)
                     :secondary true
                     :primary true
                     :on-click #(do
                                  (.preventDefault %)
                                  (e! (admin-controller/->ConfirmDeleteOperator id)))}])]}

      [:div [:p "Oletko varma, että haluat poistaa palveluntuottajan?"
             [:br]
             (str "\"" name "\", id: " id)
             [:br]
             " Samalla poistetaan kaikki palvelut ja tiedot mitä tuottajalle on syötetty."]

       [form-fields/field {:name :ensured-id
                           :type :string
                           :full-width? true
                           :label "Anna varmistukseksi palveluntuottajan id"
                           :update! #(e! (admin-controller/->EnsureServiceOperatorId id %))}
        (:ensured-id operator)]]])])

(defn operator-page-controls [e! app]
  [:div.row
   [form-fields/field {:type :string
                       :label "Hae palveluntuottajan nimellä tai sen osalla"
                       :update! #(e! (admin-controller/->UpdateOperatorFilter %))
                       :on-enter #(e! (admin-controller/->SearchOperators))}
    (get-in app [:admin :operator-list :operator-filter])]

   [buttons/save
    {:on-click  #(e! (admin-controller/->SearchOperators))
     :disabled (str/blank? filter)
     :style {:margin-left "1rem"}}
    "Hae palveluntuottajia"]])

(defn operator-list [e! app]
  (let [{:keys [loading? results]} (get-in app [:admin :operator-list])]
    [:div.row
     (when loading?
       [:span "Ladataan palveluntuottajia..."])

     (when results
       [:span
        [:div "Hakuehdoilla löytyi " (count results) " palveluntuottajaa."]
        [ui/table {:selectable false}
         [ui/table-header {:class "table-header-wrap"
                           :adjust-for-checkbox false
                           :display-select-all false}
          [ui/table-row
           [ui/table-header-column
            {:class "table-header-wrap" :style {:width "7%"}}
            "Id"]
           [ui/table-header-column
            {:class "table-header-wrap" :style {:width "15%"}}
            "Y-tunnus"]
           [ui/table-header-column
            {:class "table-header-wrap" :style {:width "25%"}}
            "Nimi"]
           [ui/table-header-column
            {:class "table-header-wrap" :style {:width "10%"}}
            "GSM"]
           [ui/table-header-column
            {:class "table-header-wrap" :style {:width "10%"}}
            "Puhelin"]
           [ui/table-header-column
            {:class "table-header-wrap" :style {:width "18%"}}
            "Sähköposti"]
           [ui/table-header-column
            {:class "table-header-wrap" :style {:width "20%"}}
            "Toiminnot"]]]
         [ui/table-body {:display-row-checkbox false}
          (doall
            (for [{::t-operator/keys [id name gsm phone business-id ckan-group-id email] :as result} results]
              ^{:key (::t-operator/id result)}
              [ui/table-row {:selectable false}
               [ui/table-row-column
                (merge (stylefy/use-style style-base/table-col-style-wrap)
                       {:width "7%"})
                id]
               [ui/table-row-column
                (merge (stylefy/use-style style-base/table-col-style-wrap)
                       {:width "15%"})
                business-id]
               [ui/table-row-column
                (merge (stylefy/use-style style-base/table-col-style-wrap)
                       {:width "25%"})
                [:a {:href (str "#/transport-operator/" id)
                     :on-click #(do
                                  (.preventDefault %)
                                  (e! (admin-controller/->EditTransportOperator business-id)))} name]]
               [ui/table-row-column
                (merge (stylefy/use-style style-base/table-col-style-wrap)
                       {:width "10%"})
                gsm]
               [ui/table-row-column
                (merge (stylefy/use-style style-base/table-col-style-wrap)
                       {:width "10%"})
                phone]
               [ui/table-row-column
                (merge (stylefy/use-style style-base/table-col-style-wrap)
                       {:width "18%"})
                email]
               [ui/table-row-column
                (merge (stylefy/use-style style-base/table-col-style-wrap)
                       {:width "20%"})
                [ui/icon-button {:href "#"
                                 :on-click #(do
                                              (.preventDefault %)
                                              (e! (fp/->ChangePage :operator-users {:ckan-group-id ckan-group-id})))}
                 [ic/social-people]]
                [ui/icon-button {:href "#"
                                 :on-click #(do
                                              (.preventDefault %)
                                              (e! (admin-controller/->EditTransportOperator business-id)))}
                 [ic/content-create]]
                [delete-transport-operator-action e! result]]]))]]])]))

(def services-row-style {:height "20px" :padding "0 0 0px 1px"})

(defn services-list [e! services]
  [:div {:style {:border-left "1px solid rgb(224, 224, 224)"}}
   (if (seq services)
     [:div {:style {:padding "0 0 0px 5px"}}

      [ui/table {:selectable false
                 :fixed-header true
                 :body-style {:overflow-y "auto"
                              :max-height "100px"}}
       [ui/table-body {:display-row-checkbox false
                       :show-row-hover true}
        (doall
          (for [{:keys [id name transport-type sub-type brokerage? operation-area]} services]
            ^{:key (str "service-" name)}
            [ui/table-row {:selectable false :style services-row-style}
             [ui/table-row-column {:style services-row-style
                                   :width "25%"}
              [:a {:href "#" :on-click #(do
                                          (.preventDefault %)
                                          (e! (fp/->ChangePage :edit-service {:id id})))} name]]
             [ui/table-row-column {:style services-row-style :width "15%"} (str/join ", " (mapv #(tr [:enums :ote.db.transport-service/transport-type (keyword %)]) transport-type))]
             [ui/table-row-column {:style services-row-style :width "25%"} (tr [:enums :ote.db.transport-service/sub-type (keyword sub-type)])]
             [ui/table-row-column {:style (merge services-row-style {:text-align "center"}) :width "10%"} (when brokerage? "X")]
             [ui/table-row-column {:style services-row-style :width "25%"} (str/join ", " operation-area)]
             ]))]]]
     [:div {:style {:padding-left "10px"
                    :line-height "48px"}}
      "Ei palveluja."])])

(defn business-id-page-controls [e! app]
  [:div
   [:div.row
    [:p "Listataan joko Palveluntuottajien y-tunnukset, palveluihin lisättyjen yritysten y-tunnukset tai molemmat.
       Palveluista mukaan on otettu vain jo julkaistut palvelut."]]
   [:div.row
    [form-fields/field {:type :selection
                        :label "Y-tunnuksen lähde"
                        :options id-filter-type
                        :show-option (tr-key [:admin-page :business-id-filter])
                        :update! #(e! (admin-controller/->UpdateBusinessIdFilter %))
                        :on-enter #(e! (admin-controller/->GetBusinessIdReport))}
     (get-in app [:admin :business-id-report :business-id-filter])]

    [buttons/save
     {:on-click  #(e! (admin-controller/->GetBusinessIdReport))
      :disabled (str/blank? filter)
      :style {:margin-left "1rem"}}
     "Hae raportti"]]])

(defn business-id-report [e! app]
  (let [{:keys [loading? results]} (get-in app [:admin :business-id-report])]
    [:div
     (when loading?
       [:div.row "Ladataan raporttia..."])

     (if results
       [:div.row
        [:div "Hakuehdoilla löytyi " (count results) " yritystä."]
        [:div
         [ui/table {:selectable false}
          [ui/table-header {:class "table-header-wrap"
                            :adjust-for-checkbox false
                            :display-select-all false}
           [ui/table-row
            [ui/table-header-column
             {:class "table-header-wrap" :style {:width "3%"}}
             "Id"]
            [ui/table-header-column
             {:class "table-header-wrap" :style {:width "10%"}}
             "Yritys"]
            [ui/table-header-column
             {:class "table-header-wrap" :style {:width "6%"}}
             "Y-tunnus"]
            [ui/table-header-column
             {:class "table-header-wrap" :style {:width "8%"}}
             "Palvelu"]
            [ui/table-header-column
             {:class "table-header-wrap" :style {:width "7%"}}
             "Liikennemuoto"]
            [ui/table-header-column
             {:class "table-header-wrap" :style {:width "10%"}}
             "Palvelutyyppi"]
            [ui/table-header-column
             {:class "table-header-wrap" :style {:width "7%"}}
             "Välityspalvelu"]
            [ui/table-header-column
             {:class "table-header-wrap" :style {:width "10%"}}
             "Toiminta-alue"]
            [ui/table-header-column
             {:class "table-header-wrap" :style {:width "7%"}}
             "GSM"]
            [ui/table-header-column
             {:class "table-header-wrap" :style {:width "7%"}}
             "Puhelin"]
            [ui/table-header-column
             {:class "table-header-wrap" :style {:width "10%"}}
             "Sähköposti"]
            [ui/table-header-column
             {:class "table-header-wrap" :style {:width "8%"}}
             "Lähde"]]]
          [ui/table-body {:display-row-checkbox false}
           (doall
             (for [{:keys [id operator business-id services gsm phone email source]} results]
               ^{:key (str "report-" business-id)}
               [ui/table-row {:selectable false}
                [ui/table-row-column
                 (merge (stylefy/use-style style-base/table-col-style-wrap)
                        {:width "3%"})
                 id]
                [ui/table-row-column
                 (merge (stylefy/use-style style-base/table-col-style-wrap)
                        {:width "10%"})
                 [:a {:href "#"
                      :on-click #(do
                                   (.preventDefault %)
                                   (e! (fp/->ChangePage :transport-operator {:id id})))} operator]]
                [ui/table-row-column (merge (stylefy/use-style style-base/table-col-style-wrap)
                                            {:width "6%"})
                 business-id]
                [ui/table-row-column
                 (merge (stylefy/use-style style-base/table-col-style-wrap)
                        {:width "40%"})
                 [services-list e! services]]
                [ui/table-row-column
                 (merge (stylefy/use-style style-base/table-col-style-wrap)
                        {:width "7%"})
                 gsm]
                [ui/table-row-column
                 (merge (stylefy/use-style style-base/table-col-style-wrap)
                        {:width "7%"})
                 phone]
                [ui/table-row-column
                 (merge (stylefy/use-style style-base/table-col-style-wrap)
                        {:width "10%"})
                 email]
                [ui/table-row-column
                 (merge (stylefy/use-style style-base/table-col-style-wrap)
                        {:width "8%"})
                 (if (= "service" source)
                   "Palvelu"
                   "Palveluntuottaja")]]))]]]]
       [:div "Hakuehdoilla ei löydy yrityksiä"])]))

(defn admin-panel [e! app]
  (let [tabs [{:label "Tarkistettavat palvelut" :value "validation"}
              {:label "Käyttäjä" :value "users"}
              {:label "Palvelut" :value "services"}
              {:label "Y-tunnus raportti" :value "businessid"}
              ;;{:label "Yritys csv:t" :value "companycsv"} - Stop copying csv:s to s3
              {:label "Palveluntuottajat" :value "operators"}
              {:label "Rajapinnat" :value "interfaces"}
              {:label "CSV Raportit" :value "reports"}
              {:label "Merireitit" :value "sea-routes"}
              {:label "Netex" :value "netex"}
              {:label "Taksien hintatiedot" :value "taxi-prices"}
              {:label "Viranomaisryhmän hallinta" :value "authority-group-admin"}]
        selected-tab (or (get-in app [:admin :tab :admin-page]) "validation")]
    [:div
     [:div {:style {:position "absolute" :right "20px"}}
      [:a (merge {:href "/#/admin/detected-changes/detect-changes"
                  :style {:margin-right "2rem"}
                  :id "admin-transit-changes-settings-btn"
                  :on-click #(do
                               (.preventDefault %)
                               (e! (fp/->ChangePage :admin-detected-changes nil)))}
                 (stylefy/use-style style-base/blue-link-with-icon))
       [ic/action-settings]
       [:span {:style {:padding-left "0.5rem"}}
        "Muutostunnistuksen asetukset"]]]
     [page/page-controls "" "Ylläpitopaneeli"
      [:div {:style {:padding-bottom "20px"}}
       [tabs/tabs tabs {:update-fn #(e! (admin-controller/->ChangeTab %))
                        :selected-tab (get-in app [:admin :tab :admin-page])}]
       ;; Show search parameters in page-controls section
       (when (= "validation" selected-tab)
         [validate-view/page-controls e! app])
       (when (= "users" selected-tab)
         [users/users-page-controls e! app])
       (when (= "services" selected-tab)
         [service-list/service-list-page-controls e! app])
       (when (= "businessid" selected-tab)
         [business-id-page-controls e! app])
       ;; (when (= "companycsv" selected-tab) [companycsv/page-controls e! app]) - Take csv file upload functionality off for now
       (when (= "operators" selected-tab)
         [operator-page-controls e! app])
       (when (= "sea-routes" selected-tab)
         [sea-routes/sea-routes-page-controls e! app])
       (when (= "netex" selected-tab)
         [netex/netex-page-controls e! app])
       (when (= "taxi-prices" selected-tab)
         [taxi-prices/page-controls e! app])
       (when (and (= "authority-group-admin" selected-tab)
                  (= true (get-in app [:user :authority-group-admin?])))
         [authority-group-admin/page-controls e! app])]]
     [:div.container {:style {:margin-top "20px"}}
      (case selected-tab
        "validation" [validate-view/validate-services e! app]
        "users" [users/user-listing e! app]
        "services" [service-list/service-listing e! app]
        "businessid" [business-id-report e! app]
        ;; "companycsv" [companycsv/company-csv-list e! app] - Take csv file upload functionality off for now
        "operators" [operator-list e! app]
        "interfaces" [interfaces/interface-list e! app]
        "reports" [report-view/reports e! app]
        "sea-routes" [sea-routes/sea-routes e! app]
        "netex" [netex/netex e! app]
        "taxi-prices" [taxi-prices/taxi-prices e! app]
        "authority-group-admin" [authority-group-admin/authority-group-admin e! app]
        ;;default
        [validate-view/validate-services e! app])]]))
