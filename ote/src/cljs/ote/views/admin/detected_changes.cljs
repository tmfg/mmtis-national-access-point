(ns ote.views.admin.detected-changes
  "Helper methods to help test and configure automatic traffic changes detection"
  (:require [cljs-react-material-ui.reagent :as ui]
            [ote.ui.form-fields :as form-fields]
            [ote.app.controller.admin :as admin-controller]
            [ote.db.transport-service :as t-service]
            [clojure.string :as str]
            [ote.localization :refer [tr tr-key]]
            [ote.ui.common :refer [linkify]]
            [ote.time :as time]
            [cljs-react-material-ui.icons :as ic]
            [reagent.core :as r]
            [ote.ui.common :as ui-common]
            [ote.ui.common :as common-ui]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]
            [ote.style.admin :as style-admin]
            [cljs-time.core :as t]
            [ote.ui.form :as form]
            [ote.ui.tabs :as tabs]
            [ote.style.buttons :as button-styles]))

(defn detect-changes [e! app-state]
  [:div
   [:div (stylefy/use-style (style-base/flex-container "column"))
    [:div (stylefy/use-style style-admin/button-container)
     [:div {:style {:flex 2}}
      [:span "Palvelun rajapinnoille annetaan url, josta gtfs/kalkati paketit voidaan ladata. Paketit ladataan öisin 00 - 04 välissä.
      Tätä nappia painamalla voidaan pakottaa lataus."]]
     [:div {:style {:flex 2}}
      [:a (merge (stylefy/use-style button-styles/primary-button)
                 {:id "force-import"
                  :href "#"
                  :on-click #(do
                               (.preventDefault %)
                               (e! (admin-controller/->ForceInterfaceImport)))
                  :icon (ic/content-filter-list)})
       [:span "Pakota yhden lataamattoman pakettin lataus ulkoisesta osoitteesta"]]]]
    [:br]
    [:div (stylefy/use-style style-admin/button-container)
     [:div {:style {:flex 2}}
      [:span "Kaikkien päivätiivisteiden laskenta vie kauan. Tuotannossa arviolta 24h+. Tämä laskenta ottaa jokaiselta
     palvelulta vain kuukauden viimeisimmän paketin ja laskee sille päivätiivisteet. Tämä vähentää laskentaa käytettyä aikaa.
     Kun käynnistät tämän laskennan joudut odottamaan arviolta 1-3h."]]
     [:div {:style {:flex 2}}
      [:a (merge (stylefy/use-style button-styles/primary-button)
                 {:href "#"
                  :on-click #(do
                               (.preventDefault %)
                               (e! (admin-controller/->ForceMonthlyDayHashCalculation)))})
       [:span "Laske päivätiivisteet kuukausittain uusiksi"]]]]

    [:br]
    [:div (stylefy/use-style style-admin/button-container)
     [:div {:style {:flex 2}}
      [:span "Laske kaikille paketeille päivätiivisteet uusiksi. Tämä laskenta ottaa tuotannossa arviolta 24h+.
      Oletko varma, että haluat käynnistää laskennan?"]]
     [:div {:style {:flex 2}}
      [:a (merge (stylefy/use-style button-styles/primary-button)
                 {:href "#"
                  :on-click #(do
                               (.preventDefault %)
                               (e! (admin-controller/->ForceDayHashCalculation)))})
       [:span "Laske kaikki päivätiivisteet uusiksi"]]]]

    [:br]
    [:div (stylefy/use-style style-admin/button-container)
     [:div {:style {:flex 2}}
      [:span "Pakota muutostunnistus kaikkille palveluille"]]
     [:div {:style {:flex 2}}
      [:a (merge (stylefy/use-style button-styles/primary-button)
                 {:id "force-detect-transit-changes"
                  :href "#"
                  :on-click #(do
                               (.preventDefault %)
                               (e! (admin-controller/->ForceDetectTransitChanges)))
                  :icon (ic/content-filter-list)})
       [:span "Käynnistä muutostunnitus"]]]]]])

(defn route-id [e! app-state]
  (let [services (get-in app-state [:admin :transit-changes :route-hash-services])]
    [:div
     [:div
      [:h4 "Päivitä palvelulle reitin tunnistustyyppi"]
      [form/form
       {:update!   #(e! (admin-controller/->UpdateRouteHashCalculationValues %))
        :footer-fn (fn [data]
                     [:span
                      [ui/raised-button {:primary  true
                                         :on-click #(e! (admin-controller/->ForceRouteHashCalculationForService))
                                         :label    "Päivitä"}]])}
       [(form/group
          {:label   "Route hash id:n hashien uudelleen laskenta"
           :columns 3
           :layout  :raw
           :card?   false}
          {:name      :service-id
           :type      :string
           :label     "Palvelun id"
           :hint-text "Palvelun id"
           :required? true}
          {:name      :package-count
           :type      :string
           :label     "Pakettien määrä"
           :hint-text "5"
           :required? true}
          {:name        :route-id-type
           :type        :selection
           :options     ["short-long" "short-long-headsign" "route-id" "long-headsign" "long"]
           :show-option (fn [x] x)
           :required?   true})]
       (get-in app-state [:admin :transit-changes :route-hash-values])]]

     [:div
      [:h4 "Päivitä palvelulle päivittäiset hash tunnisteet"]
      [form/form
       {:update!   #(e! (admin-controller/->UpdateHashCalculationValues %))
        :footer-fn (fn [data]
                     [:span
                      [ui/raised-button {:primary  true
                                         :on-click #(e! (admin-controller/->ForceHashCalculationForService))
                                         :label    "Laske"}]])}
       [(form/group
          {:label   "Päivä hashien uudelleen laskenta"
           :columns 3
           :layout  :raw
           :card?   false}
          {:name      :service-id
           :type      :string
           :label     "Palvelun id"
           :hint-text "Palvelun id"
           :required? true}
          {:name      :package-count
           :type      :string
           :label     "Pakettien määrä"
           :hint-text "5"
           :required? true})]
       (get-in app-state [:admin :transit-changes :daily-hash-values])]]

     [:div (stylefy/use-style (style-base/flex-container "column"))
      [:br]
      [ui/raised-button
       {:id       "force-detect-transit-changes"
        :label    "Pakota kaikkien muutosten tunnistus"
        :on-click #(do
                     (.preventDefault %)
                     (e! (admin-controller/->ForceDetectTransitChanges)))
        :primary  true
        :icon     (ic/content-filter-list)}]]

     [:div
      [:br]
      [ui/raised-button
       {:id       "load-services"
        :label    "Lataa palvelut, joilla reitintunnistusmuutos"
        :on-click #(do
                     (.preventDefault %)
                     (e! (admin-controller/->LoadRouteHashServices)))
        :primary  true
        :icon     (ic/content-report)}]

      [:br]

      [ui/table {:selectable false}
       [ui/table-header {:adjust-for-checkbox false
                         :display-select-all  false}
        [ui/table-row
         [ui/table-header-column "Palveluntuottaja"]
         [ui/table-header-column "Palvelun id"]
         [ui/table-header-column "Palvelu"]
         [ui/table-header-column "Tunnisteen tyyppi"]]]
       [ui/table-body {:display-row-checkbox false}
        (doall
          (for [s services]
            ^{:key (str "link_" s)}
            [ui/table-row {:selectable false}
             [ui/table-row-column (:operator s)]
             [ui/table-row-column (:service-id s)]
             [ui/table-row-column (:service s)]
             [ui/table-row-column (:type s)]]))]]]]))

(defn upload-gtfs [e! app-state]
  [:div
    [:h4 "Lataa palvelulle gtfs tiedosto tietylle päivälle"]
    [form/form
     {:update!   #(e! (admin-controller/->UpdateUploadValues %))}
     [(form/group
        {:label   ""
         :columns 3
         :layout  :raw
         :card?   false}

        {:name      :service-id
         :type      :string
         :label     "Palvelun id"
         :hint-text "Palvelun id"
         }
        {:name      :date
         :type      :string
         :label     "Latauspäivä"
         :hint-text "2018-12-12"
         }
        {:name         :attachments
         :type         :table
         :add-label    "Ladattava tiedosto"
         :table-fields [{:name      :attachment-file-name
                         :type      :string
                         :disabled? true}

                        {:name               :attachment-file
                         :button-label       "Lataa"
                         :type               :file-and-delete
                         :allowed-file-types [".zip"]
                         :on-change          #(e! (admin-controller/->UploadAttachment (.-target %)))}]})]
     (get-in app-state [:admin :transit-changes :upload-gtfs])]])

(defn configure-detected-changes [e! app-state]
  (let [page (:page app-state)
        tabs [{:label "Tunnista muutokset" :value "detect-changes"}
              {:label "Reitin tunnistus" :value "route-id"}
              {:label "Lataa gtfs" :value "upload-gtfs"}]
        selected-tab (or (get-in app-state [:admin :transit-changes :tab]) "detect-changes")]
    [:div
     [:h2 "Muutostunnistukseen liittyviä työkaluja"]
     [tabs/tabs tabs {:update-fn    #(e! (admin-controller/->ChangeDetectionTab %))
                      :selected-tab (get-in app-state [:admin :transit-changes :tab])}]
     [:div.container {:style {:margin-top "20px"}}
      (case selected-tab
            "detect-changes" [detect-changes e! app-state]
            "route-id" [route-id e! app-state]
            "upload-gtfs" [upload-gtfs e! app-state]
            ;;default
            [detect-changes e! app-state])]]))
