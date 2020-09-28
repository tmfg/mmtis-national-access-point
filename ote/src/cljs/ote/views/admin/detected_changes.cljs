(ns ote.views.admin.detected-changes
  "Helper methods to help test and configure automatic traffic changes detection"
  (:require [cljs-react-material-ui.reagent :as ui]
            [reagent.core :as r]
            [cljs-react-material-ui.icons :as ic]
            [stylefy.core :as stylefy]
            [ote.localization :refer [tr tr-key]]
            [cljs-time.core :as t]
            [ote.time :as time]
            [ote.db.transport-service :as t-service]
            [ote.style.base :as style-base]
            [ote.style.admin :as style-admin]
            [ote.style.buttons :as button-styles]
            [ote.ui.buttons :as buttons]
            [ote.ui.common :refer [linkify]]
            [ote.ui.common :as common]
            [ote.ui.form :as form]
            [ote.ui.form-fields :as form-fields]
            [ote.ui.tabs :as tabs]
            [ote.ui.info :as info]
            [ote.ui.notification :as notification]
            [ote.app.controller.admin-transit-changes :as admin-transit-changes]))

(defn hash-recalculation-warning
  "When hash calculation is on going we need to block users to start it again."
  [e! app-state]
  (let [calculation (first (get-in app-state [:admin :transit-changes :hash-recalculations :calculations]))]
    [:div
     [:strong "Taustalaskenta on menossa. Muutostunnistuksen työkalut ovat poissa käytöstä."]
     [:br]
     [:div
      [:div "Laskenta aloitettu " (str (:gtfs/started calculation))]
      [:div "Paketteja yhteensä " (:gtfs/packets-total calculation)]
      [:div "Paketeja laskettu " (:gtfs/packets-ready calculation)]]
     [:br]
     [buttons/save
      {:id "update-hash-recalculation-status"
       :href "#"
       :on-click #(do
                    (.preventDefault %)
                    (e! (admin-transit-changes/->LoadHashRecalculations)))
       :icon (ic/content-filter-list)}
      [:span "Tarkista laskennan eteneminen"]]
     [buttons/delete
      {:id "reset-hash-recalculation-status"
       :href "#"
       :on-click #(do
                    (.preventDefault %)
                    (e! (admin-transit-changes/->ResetHashRecalculations)))
       :icon (ic/content-filter-list)}
      [:span "Nollaa status, jos se on jäänyt jumiin"]]]))

(defn contract-traffic [e! app-state]
  (let [services (get-in app-state [:admin :transit-changes :commercial-services])]
    [:div
     [ui/table {:selectable false}
      [ui/table-header {:adjust-for-checkbox false
                        :display-select-all false}
       [ui/table-row
        [ui/table-header-column "Palveluntuottaja"]
        [ui/table-header-column "Palvelu"]
        [ui/table-header-column "Kaupallinen?"]]]
      [ui/table-body {:display-row-checkbox false}
       (doall
         (for [s services]
           ^{:key (:service-id s)}
           [ui/table-row {:selectable false}
            [ui/table-row-column (:operator-name s)]
            [ui/table-row-column (:service-name s)]
            [ui/table-row-column
             [form-fields/field
              {:label "Kaupallinen"
               :type :checkbox
               :update! #(e! (admin-transit-changes/->ToggleCommercialTraffic (:service-id s) (:commercial? s)))}
              (:commercial? s)]]]))]]]))

(defn- day-hash-button-element [e! description btn-text calculation-fn]
  [:div
   [:div (stylefy/use-style style-admin/detection-info-text)
    description]
   [:div (stylefy/use-style style-admin/detection-button-container)
    [:a (merge (stylefy/use-style button-styles/primary-button)
               {:href "#"
                :on-click #(do
                             (.preventDefault %)
                             (calculation-fn)
                             (.setTimeout js/window
                                          (fn [] (e! (admin-transit-changes/->LoadHashRecalculations)))
                                          500))})
     [:span btn-text]]]])

(defn detect-changes [e! {:keys [admin] :as app-state}]
  (let [single-download-service-id (get-in app-state [:admin :transit-changes :single-download-gtfs-service-id])
        service-interfaces (get-in app-state [:admin :transit-changes :upload-gtfs :interfaces])
        selected-interface (get-in app-state [:admin :transit-changes :single-download-gtfs-interface])]
    [:div (stylefy/use-style (style-base/flex-container "column"))

     [:h3 "Rajapintoihin kohdistuvat toimenpiteet"]
     [:div
      [:div (stylefy/use-style style-admin/detection-info-text)
       "Palvelun rajapinnoille annetaan url, josta gtfs/kalkati paketit voidaan ladata. Paketit ladataan öisin 00 - 04 välissä.
       Tätä nappia painamalla voidaan pakottaa lataus annetulle palvelulle."]
      [:div (stylefy/use-style style-admin/detection-button-container)
       [:div.row

        [:div.col-xs-6
         [form-fields/field
          {:name :service-id
           :type :string
           :label "Palvelun id"
           :hint-text "Palvelun id"
           :update! #(do
                       (e! (admin-transit-changes/->SetSingleDownloadGtfsServiceId %))
                       (e! (admin-transit-changes/->UpdateInterfaceServiceId %)))}
          single-download-service-id]
         [:button {:on-click #(e! (admin-transit-changes/->GetServiceInterfaces))}
          "Hae rajapinnat"]
         [form-fields/field
          {:name :interface-id
           :type :selection
           :label "Valitse palvelun rajapinta"
           :show-option #(str (get-in % [:ote.db.transport-service/external-interface ::t-service/url]) " :: "
                              (get-in % [::t-service/format]))
           :options service-interfaces
           :update! #(do
                       (e! (admin-transit-changes/->SetSingleDownloadGtfsInterfaceId %)))
           :full-width? false}
          selected-interface]]
        [:div.col-xs-6
         [:a (merge (stylefy/use-style (merge style-admin/detection-button-with-input button-styles/primary-button))
                    {:id "force-import"
                     :href "#"
                     :on-click #(do
                                  (.preventDefault %)
                                  (e! (admin-transit-changes/->ForceInterfaceImportForGivenService)))
                     :icon (ic/content-filter-list)})
          [:span "Lataa palvelun rajapintaan kuuluva gtfs paketti"]]]]]]

     (when-let [response (get-in app-state [:admin :transit-changes :single-download-gtfs-service-response])]
       [:div (stylefy/use-style style-admin/detection-info-text)
        [notification/notification {:type (:status response)} (:msg response)]])

     [:h3 "Muutostunnistuksen käynnistys"]
     [:div
      [:div
       [:div (stylefy/use-style style-admin/detection-info-text)
        "Pakota muutostunnistus kaikkille palveluille. Tämä vie noin 3 minuuttia."]
       [:div (stylefy/use-style style-admin/detection-button-container)
        [:a (merge (stylefy/use-style button-styles/primary-button)
                   {:id "force-detect-transit-changes"
                    :href "#"
                    :on-click #(do
                                 (.preventDefault %)
                                 (e! (admin-transit-changes/->ForceDetectTransitChanges)))
                    :icon (ic/content-filter-list)})
         [:span "Käynnistä muutostunnistus"]]]]
      [:br]
      [:div
       [:div (stylefy/use-style style-admin/detection-info-text)
        "Käynnistä muutostunnistus vain yhdelle palvelulle. Anna kenttään palvelun id.
        Löydät palvelun id:n omat palvelutiedot sivun kautta tai muutostunnistuksen visualisointisivun url:stä."]
       [:div (stylefy/use-style style-admin/detection-button-container)
        [ui/text-field
         {:id "detection-service-id"
          :name "detection-service-name"
          :floating-label-text "Palvelun id"
          :value (get-in app-state [:admin :transit-changes :single-detection-service-id])
          :on-change #(do
                        (.preventDefault %)
                        (e! (admin-transit-changes/->SetSingleDetectionServiceId %2)))}]
        [:a (merge (stylefy/use-style (merge button-styles/primary-button style-admin/detection-button-with-input))
                   {:id "detect-changes-for-given-service"
                    :href "#"
                    :on-click #(do
                                 (.preventDefault %)
                                 (e! (admin-transit-changes/->DetectChangesForGivenService)))
                    :icon (ic/content-filter-list)})
         [:span "Muutostunnitus yhdelle palvelulle"]]]]]

     [:h3 "Muutostunnistuksen käynnistys tiettynä päivänä"]
     [:div
      [:div
       [:div (stylefy/use-style style-admin/detection-info-text)
        "Pakota muutostunnistus kaikkille palveluille. Tämä vie noin 3 minuuttia."]
       [:div (stylefy/use-style style-admin/detection-button-container)
        [:a (merge (stylefy/use-style button-styles/primary-button)
                   {:id "force-detect-transit-changes"
                    :href "#"
                    :on-click #(do
                                 (.preventDefault %)
                                 (e! (admin-transit-changes/->ForceDetectTransitChanges)))
                    :icon (ic/content-filter-list)})
         [:span "Käynnistä muutostunnistus"]]]]
      [:br]
      [:div
       [:div (stylefy/use-style style-admin/detection-info-text)
        "Käynnistä muutostunnistus vain yhdelle palvelulle. Anna palvelun id ja päivä yyyy-mm-dd."]
       [:div (stylefy/use-style style-admin/detection-button-container)
        [ui/text-field
         {:id "detection-service-id"
          :name "detection-service-name"
          :floating-label-text "Palvelun id"
          :value (get-in app-state [:admin :transit-changes :single-detection-service-id])
          :on-change #(do
                        (.preventDefault %)
                        (e! (admin-transit-changes/->SetSingleDetectionServiceId %2)))}]
        [ui/text-field
         {:id "detection-date"
          :name "detection-date"
          :floating-label-text "Muutostunnistuspäivä"
          :value (get-in app-state [:admin :transit-changes :single-detection-date])
          :on-change #(do
                        (.preventDefault %)
                        (e! (admin-transit-changes/->SetSingleDetectionDate %2)))}]
        [:a (merge (stylefy/use-style (merge button-styles/primary-button style-admin/detection-button-with-input))
                   {:id "detect-changes-for-given-service-and-date"
                    :href "#"
                    :on-click #(do
                                 (.preventDefault %)
                                 (e! (admin-transit-changes/->DetectChangesForGivenServiceAndDate)))
                    :icon (ic/content-filter-list)})
         [:span "Muutostunnitus yhdelle palvelulle päivän perusteella"]]]]]

     [:hr {:style {:width "100%" :margin "4em 0 1em 0"}}]

     [:h2 "Päivätiivisteet - käytä vain jos tiedät mitä olet tekemässä"]
     [day-hash-button-element e!
      "Laske kuukausittaiset päivätiivisteet tulevaisuuden osalta. Tämä vie noin tunnin. Laskenta jättää sopimusliikenteet huomoimatta."
      "Laske päivätiivisteet kuukausittain tulevaisuuteen"
      #(e! (admin-transit-changes/->CalculateDayHash "month" "true"))]

     [:br]
     [day-hash-button-element e!
      "Laske kaikki päivätiivisteet tulevaisuuden osalta. Tämä vie noin 15 tuntia. Laskenta jättää sopimusliikenteet huomioimatta."
      "Laske kaikki päivätiivisteet tulevaisuuteen"
      #(e! (admin-transit-changes/->CalculateDayHash "day" "true"))]

     [:br]
     [day-hash-button-element e!
      "Kaikkien päivätiivisteiden laskenta vie kauan. Tuotannossa arviolta 24h+. Tämä laskenta ottaa jokaiselta
      palvelulta vain kuukauden viimeisimmän paketin ja laskee sille päivätiivisteet. Tämä vähentää laskentaa käytettyä aikaa.
      Kun käynnistät tämän laskennan joudut odottamaan arviolta 1-3h. Laskenta jättää sopimusliikenteet huomioimatta."
      "Laske päivätiivisteet kuukausittain uusiksi"
      #(e! (admin-transit-changes/->CalculateDayHash "month" "false"))]

     [:br]
     [day-hash-button-element e!
      "Laske kaikille paketeille päivätiivisteet uusiksi. Tämä laskenta ottaa tuotannossa arviolta 24h+. Laskenta ei ota huomioon sopimusliikennettä.
        Oletko varma, että haluat käynnistää laskennan?"
      "Laske kaikki päivätiivisteet uusiksi"
      #(e! (admin-transit-changes/->CalculateDayHash "day" "false"))]

     [:br]
     [day-hash-button-element e!
      "Laske sopimusliikenteelle kaikki päivätiivisteet uusiksi. Laskenta ei ota huomioon kaupallista liikennettä."
      "Laske sopimusliikenteelle päivätiivisteet"
      #(e! (admin-transit-changes/->CalculateDayHash "contract" "false"))]

     [:br]
     [day-hash-button-element e!
      "Käynnistä uusi muutosilmoitusten sähköposti-ilmoituksen lähetys. Operaatio ei lähetä edellistä sähköpostia uudestaan,
      vaan koostaa uudet ilmoitukset, lähettää ne, sekä päivittää lähetyshistorian.
      Lähetettyjä ilmoituksia ei siis lähetetä uudestaan seuraavalla ajastetulla tai käsin käynnistetyllä ajolla."
      "Käynnistä muutosilmoitusten lähetys"
      #(e! (admin-transit-changes/->SendPreNotices))]
     [:div (stylefy/use-style style-admin/detection-info-text)
      (when-let [resp (get-in admin [:pre-notice :pre-notice-notify-send])]
        [notification/notification
         (if (= :success resp)
           {:type :success :text (str "Mahdolliset muutosilmoitusten sähköpostit lähetetty")}
           {:type :error :text (str "Mahdollisten muutosilmoitusten sähköpostien lähettäminen ei onnistunut. Virhetietoja: " resp)})])]

     [:br]
     [day-hash-button-element e!
      "Selvitä tuotannon päiväyksiin liittyvää formatointiongelmaa"
      "Logita päiväykset"
      #(e! (admin-transit-changes/->GeneralTroubleshootingLog))]

     [:br]
     [day-hash-button-element e!
      "Käynnistä yöllinen muutostunnistusten gtfs-transit-changes siivous, koska muutokset voivat vanhentua kalenteripäivien edetessä ja muutosten jäädessä historiaan."
      "Siivoa vanhat muutostunnistukset"
      #(e! (admin-transit-changes/->CleanupOldTransitChanges))]]))

(defn route-id [e! app-state recalc?]
  (let [services (get-in app-state [:admin :transit-changes :route-hash-services])]
    [:div
     (if recalc?
       ;; Show recalc status
       [hash-recalculation-warning e! app-state]
       ;; else - Show form
       [:div.form-container
        [:div
         [:h4 "Päivitä palvelulle reitin tunnistustyyppi"]
         [form/form
          {:update! #(e! (admin-transit-changes/->UpdateRouteHashCalculationValues %))
           :footer-fn (fn [data]
                        [:span
                         [buttons/save {:primary true
                                        :on-click #(e! (admin-transit-changes/->ForceRouteHashCalculationForService))}
                          "Päivitä"]])}
          [(form/group
             {:columns 3
              :layout :raw
              :card? false}
             {:name :service-id
              :type :string
              :label "Palvelun id"
              :hint-text "Palvelun id"
              :required? true}
             {:name :package-count
              :type :string
              :label "Pakettien määrä"
              :hint-text "5"
              :required? true}
             {:name :route-id-type
              :type :selection
              :options ["short-long" "short-long-headsign" "route-id" "long-headsign" "long"]
              :show-option (fn [x] x)
              :required? true})]
          (get-in app-state [:admin :transit-changes :route-hash-values])]]

        [:div
         [:h4 "Päivitä palvelulle päivittäiset hash tunnisteet"]
         [form/form
          {:update! #(e! (admin-transit-changes/->UpdateHashCalculationValues %))
           :footer-fn (fn [data]
                        [:span
                         [buttons/save {:primary true
                                        :on-click #(do
                                                     (.preventDefault %)
                                                     (e! (admin-transit-changes/->ForceHashCalculationForService))
                                                     (.setTimeout js/window
                                                                  (fn [] (e! (admin-transit-changes/->LoadHashRecalculations)))
                                                                  500))}
                          "Laske"]])}
          [(form/group
             {:columns 3
              :layout :raw
              :card? false}
             {:name :service-id
              :type :string
              :label "Palvelun id"
              :hint-text "Palvelun id"
              :required? true}
             {:name :package-count
              :type :string
              :label "Pakettien määrä"
              :hint-text "5"
              :required? true})]
          (get-in app-state [:admin :transit-changes :daily-hash-values])]]])

     [:div
      [:br]
      [buttons/save
       {:id "load-services"
        :on-click #(do
                     (.preventDefault %)
                     (e! (admin-transit-changes/->LoadRouteHashServices)))
        :primary true
        :icon (ic/content-report)}
       "Lataa palvelut, joilla reitintunnistusmuutos"]

      [:br]

      [ui/table {:selectable false}
       [ui/table-header {:adjust-for-checkbox false
                         :display-select-all false}
        [ui/table-row
         [ui/table-header-column "Palveluntuottaja"]
         [ui/table-header-column "Palvelun id"]
         [ui/table-header-column "Palvelu"]
         [ui/table-header-column "Tunnisteen tyyppi"]]]
       [ui/table-body {:display-row-checkbox false}
        (doall
          (for [s services]
            ^{:key (:service-id s)}
            [ui/table-row {:selectable false}
             [ui/table-row-column (:operator s)]
             [ui/table-row-column (:service-id s)]
             [ui/table-row-column (:service s)]
             [ui/table-row-column (:type s)]]))]]]]))


(defn csv-loading-status
  [app-state]
  (let [status (get-in app-state [:admin :exceptions-from-csv :status])
        changes (get-in app-state [:admin :exceptions-from-csv :exceptions])
        error (get-in app-state [:admin :exceptions-from-csv :error])
        inner-component-success [:ul {:style {:margin-left "3rem"}}
                                 (for [change changes]
                                   ^{:key (:gtfs/date change)}
                                   [:li (str (time/format-timestamp->date-for-ui (:gtfs/date change)) " " (:gtfs/reason change))])]
        inner-component-fail [:span (str error)]]
    (if (= status 200)
      [:div
       ^{:key "success"}
       [info/info-toggle [:span {:style {:color "green"}}
                          "CSV ladattu onnistuneesti. Ladatut päivät sisällä"] inner-component-success {:default-open? false}]]
      [:div
       ^{:key "error"}
       [info/info-toggle [:span {:style {:color "red"}}
                          "CSV lataus epäonnistui. Lähetä sisällön viesti kehittäjille."] inner-component-fail {:default-open? false}]])))


(defn admin-exception-days [e! app-state]
  [:div
   [:div (stylefy/use-style (style-base/flex-container "column"))
    [:h2 "Poikkeuspäivät"]
    (when (get-in app-state [:admin :exceptions-from-csv :status])
      [csv-loading-status app-state])
    [:div (stylefy/use-style style-admin/detection-button-container)
     [:div (stylefy/use-style style-admin/detection-info-text)
      [:span "Lataa osoitteesta: http://traffic.navici.com/tiedostot/poikkeavat_ajopaivat.csv uusi csv poikkeuspäiviä backendin käyttöön."]]
     [:div {:style {:flex 2}}
      [:button (merge (stylefy/use-style button-styles/primary-button)
                      {:id "import-csv"
                       :on-click #(e! (admin-transit-changes/->StartCSVLoad))
                       :icon (ic/content-filter-list)})
       "Lataa uusi CSV"]]]]])

(defn upload-gtfs [e! app-state]
  (let [service-id (get-in app-state [:admin :transit-changes :upload-gtfs :service-id])
        interfaces (get-in app-state [:admin :transit-changes :upload-gtfs :interfaces])]
    [:div.col-xs-12.col-md-6
     [:h4 "Lataa palvelulle gtfs tiedosto tietylle päivälle"]

     [form-fields/field
      {:name :service-id
       :type :string
       :label "Palvelun id"
       :hint-text "Palvelun id"
       :update! #(e! (admin-transit-changes/->UpdateInterfaceServiceId %))}
      service-id]
     [:button {:on-click #(e! (admin-transit-changes/->GetServiceInterfaces))}
      "Hae rajapinnat"]

     (if interfaces
       [form/form
        {:update! #(e! (admin-transit-changes/->UpdateUploadValues %))}
        [(form/group
           {:label ""
            :columns 3
            :layout :raw
            :card? false}

           {:name :interface-id
            :type :selection
            :label "Valitse palvelun rajapinta"
            :show-option #(str (get-in % [:ote.db.transport-service/external-interface ::t-service/url]) " :: "
                               (get-in % [::t-service/format]))
            :options interfaces
            :should-update-check form/always-update
            :full-width? true}

           {:name :date
            :type :string
            :label "Latauspäivä"
            :hint-text "2018-12-12"}

           {:name :attachments
            :type :table
            :add-label "Ladattava tiedosto"
            :table-fields [{:name :attachment-file-name
                            :type :string
                            :disabled? true}

                           {:name :attachment-file
                            :button-label "Lataa"
                            :type :file-and-delete
                            :allowed-file-types [".zip"]
                            :on-change #(e! (admin-transit-changes/->UploadAttachment (.-target %)))}]})]
        (get-in app-state [:admin :transit-changes :upload-gtfs])]
       [:div "Anna sellaisen palvelun id, jolla on rajapintoja"])]))

(defn configure-detected-changes [e! app-state]
  (r/create-class
    {:component-will-mount #(e! (admin-transit-changes/->InitAdminDetectedChanges))
     :reagent-render
     (fn [e! app-state]
       (let [tabs [{:label "Tunnista muutokset" :value "admin-detected-changes"}
                   {:label "Reitin tunnistus" :value "admin-route-id"}
                   {:label "Lataa gtfs" :value "admin-upload-gtfs"}
                   {:label "Sopimusliikenne" :value "admin-commercial-services"}
                   {:label "Poikkeuspäivät" :value "admin-exception-days"}]
             selected-tab (or (get-in app-state [:admin :transit-changes :tab]) "admin-detected-changes")
             recalc? (some? (get-in app-state [:admin :transit-changes :hash-recalculations]))]
         [:div
          [common/back-link-with-event :admin "Takaisin ylläpitopaneelin etusivulle"]
          [:h2 "Muutostunnistukseen liittyviä työkaluja"]

          ;; If hash recalculations are ongoing disable some of the tabs
          [:div
           [tabs/tabs tabs {:update-fn #(e! (admin-transit-changes/->ChangeDetectionTab %))
                            :selected-tab (get-in app-state [:admin :transit-changes :tab])}]
           [:div.container {:style {:margin-top "20px"}}
            (case selected-tab
              "admin-detected-changes" (if recalc? [hash-recalculation-warning e! app-state] [detect-changes e! app-state])
              "admin-route-id" [route-id e! app-state recalc?]
              "admin-upload-gtfs" (if recalc? [hash-recalculation-warning e! app-state] [upload-gtfs e! app-state])
              "admin-commercial-services" [contract-traffic e! app-state]
              "admin-exception-days" [admin-exception-days e! app-state]
              ;;default
              [detect-changes e! app-state])]]]))}))
