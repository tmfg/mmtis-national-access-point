(ns ote.views.admin.interfaces
  "Admin panel views. Note this has a limited set of users and is not
  currently localized, all UI text is in Finnish."
  (:require [cljs-react-material-ui.reagent :as ui]
            [ote.app.controller.admin :as admin-controller]
            [clojure.string :as str]
            [ote.localization :refer [tr tr-key]]
            [ote.ui.common :as common-ui]
            [ote.time :as time]
            [reagent.core :as r]
            [ote.ui.buttons :as buttons]
            [ote.theme.colors :as colors]
            [stylefy.core :as stylefy]
            [ote.style.admin :as style-admin]
            [ote.app.controller.front-page :as fp]
            [ote.ui.form :as form]
            [ote.style.dialog :as style-dialog]))

(defn error-modal [e! app]
  (let [interface (get-in app [:admin :interface-list :error-modal])
        title (if (:db-error interface)
                "Rajapinnan käsittelyssä tapahtunut virhe"
                "Rajapinnan lataamisessa tapahtunut virhe")]
    (when interface
      [ui/dialog
       {:open true
        :actionsContainerStyle style-dialog/dialog-action-container
        :modal false
        :auto-scroll-body-content true
        :on-request-close #(e! (admin-controller/->CloseInterfaceErrorModal))
        :title title
        :actions [(r/as-element
                    [ui/flat-button
                     {:label (tr [:buttons :close])
                      :secondary true
                      :primary true
                      :on-click #(e! (admin-controller/->CloseInterfaceErrorModal))}])]}
       [:div.col-md-8
        [:div.row
         [:div.col-md-6 (stylefy/use-style style-admin/modal-data-label) "Rajapinnan osoite: "]
         [:div.col-md-6 [linkify/linkify (:url interface) (:url interface) {:target "_blank"}]]]
        [:div.row
         [:div.col-md-6 (stylefy/use-style style-admin/modal-data-label) "Rajapinnan tyyppi: "]
         [:div.col-md-6 (str/join ", " (:format interface))]]
        [:div.row
         [:div.col-md-6 (stylefy/use-style style-admin/modal-data-label) "Virhe: "]
         [:div.col-md-6
          (when (:import-error interface)
            [:p (:import-error interface)])
          (when (:db-error interface)
            [:p (:db-error interface)])]]]])))

(defn operator-modal [e! app]
  (let [interface (get-in app [:admin :interface-list :operator-modal])]
    (when interface
      [ui/dialog
       {:open true
        :actionsContainerStyle style-dialog/dialog-action-container
        :modal false
        :auto-scroll-body-content true
        :on-request-close #(e! (admin-controller/->CloseOperatorModal))
        :title "Palvelun ja palveluntuottajan tiedot"
        :actions [(r/as-element
                    [ui/flat-button
                     {:label (tr [:buttons :close])
                      :secondary true
                      :primary true
                      :on-click #(e! (admin-controller/->CloseOperatorModal))}])]}
       [:div.col-md-6 {:style {:border-right "1px solid gray"}}
        [:div.row [:h3 "Rajapinnan tuottaa"]]
        [:div.row
         [:div.col-md-4 (stylefy/use-style style-admin/modal-data-label) "Nimi: "]
         [:div.col-md-6 [:a {:href "#"
                             :on-click #(do
                                          (.preventDefault %)
                                          (e! (fp/->ChangePage :transport-operator {:id (:operator-id interface)})))}
                         (:operator-name interface)]]]
        [:div.row
         [:div.col-md-4 (stylefy/use-style style-admin/modal-data-label) "Puhelin: "]
         [:div.col-md-6 (:operator-phone interface)]]
        [:div.row
         [:div.col-md-4 (stylefy/use-style style-admin/modal-data-label) "GSM: "]
         [:div.col-md-6 (:operator-gsm interface)]]
        [:div.row
         [:div.col-md-4 (stylefy/use-style style-admin/modal-data-label) "Sähköposti: "]
         [:div.col-md-6 (:operator-email interface)]]]

       [:div.col-md-6 {:style {:padding-left "20px"}}
        [:div.row [:h3 "Rajapinta kuuluu palveluun:"]]
        [:div.row
         [:div.col-md-4 (stylefy/use-style style-admin/modal-data-label) "Nimi: "]
         [:div.col-md-6 [:a {:href "#"
                             :on-click #(do
                                          (.preventDefault %)
                                          (e! (fp/->ChangePage :edit-service {:id (:service-id interface)})))}
                         (:service-name interface)]]]
        [:div.row
         [:div.col-md-4 (stylefy/use-style style-admin/modal-data-label) "Puhelin: "]
         [:div.col-md-6 (:service-phone interface)]]
        [:div.row
         [:div.col-md-4 (stylefy/use-style style-admin/modal-data-label) "Sähköposti: "]
         [:div.col-md-6 (:service-email interface)]]]])))

(defn- gtfs-viewer-link [url format]
  (when format
    (let [format (str/lower-case format)]
      (when (or (= "gtfs" format) (= "kalkati.net" format))
        (common-ui/linkify
          (str "#/routes/view-gtfs?url=" (.encodeURIComponent js/window url)
               (when (= "kalkati.net" format)
                 "&type=kalkati"))
          (tr [:service-search :view-routes])
          {:target "_blank"})))))

(def interface-formats [:GTFS :Kalkati.net :ALL])

(defn interface-table-row [e! interface-id data-content operator-name format
                           import-error url date-0 imported db-error interface first? list-count selected-interface-id]
  [ui/table-row {:key (str interface)
                 :selectable false :style (merge
                                            (when first?
                                              {:background-color colors/gray100})
                                            {})}

   (cond
     (and first? (= selected-interface-id interface-id))
     [ui/table-row-column {:style {:width "2%" :padding "0px 0px 0px 5px"}}
      [:a {:style {:text-decoration "none" :font-size 20}
           :href "#"
           :on-click #(do (.preventDefault %)
                          (e! (admin-controller/->CloseInterfaceList)))}
       [:div {:dangerouslySetInnerHTML {:__html "&#8743;"}}]]]
     (and first? (not= selected-interface-id interface-id) (> list-count 1))
     [ui/table-row-column {:style {:width "2%" :padding "0px 0px 0px 5px"}}
      [:a {:style {:text-decoration "none" :font-size 20}
           :href "#"
           :on-click #(do (.preventDefault %)
                          (e! (admin-controller/->OpenInterfaceList interface-id)))}
       [:div {:dangerouslySetInnerHTML {:__html "&or;"}}]]]
     :else
     [ui/table-row-column {:style {:width "2%" :padding "0"}} " "])
   [ui/table-row-column {:style {:width "17%" :padding "0px 5px 0px 5px"}}
    [:a {:href "#"
         :on-click #(do (.preventDefault %)
                        (e! (admin-controller/->OpenOperatorModal interface)))}
     operator-name]]
   [ui/table-row-column {:style {:width "14%" :padding "0px 5px 0px 5px"}} (admin-controller/format-interface-content-values data-content)]
   [ui/table-row-column {:style {:width "8%" :padding "0px 5px 0px 5px"}} (first format)]
   [ui/table-row-column {:style {:width "35%" :padding "0px 5px 0px 5px"}} (if (= "Rajapinta puuttuu" import-error)
                                                                             url
                                                                             [common-ui/linkify url url {:target "_blank"}])]
   [ui/table-row-column {:style {:width "14%" :padding "0px 5px 0px 5px"}}
    (if (= date-0 imported)
      ""
      (time/format-timestamp-for-ui imported))]
   [ui/table-row-column {:style {:width "10%" :padding "0px 5px 0px 5px"}}
    (when (and import-error (not= "Rajapinta puuttuu" import-error))
      [:a {:style {:color "red"}
           :href "#"
           :on-click #(do (.preventDefault %)
                          (e! (admin-controller/->OpenInterfaceErrorModal interface)))}
       " Latausvirhe "])
    (when (and db-error (not= "no-db" db-error))
      [:a {:style {:color "red"}
           :href "#"
           :on-click #(do (.preventDefault %)
                          (e! (admin-controller/->OpenInterfaceErrorModal interface)))}
       " Käsittelyvirhe "])

    (when (and import-error (= "Rajapinta puuttuu" import-error))
      [:span " Rajapinta puuttuu kokonaan "])

    (when (and (nil? import-error) (nil? db-error)) [gtfs-viewer-link url (first format)])]])

(defn interface-list [e! app]
  (let [{:keys [loading? results filters]}
        (get-in app [:admin :interface-list])
        date-0 (js/Date. 0)
        grouped-results (group-by :interface-id results)
        ;; Take first from every vector
        first-from-grouped-results (map
                                     (fn [list]
                                       (merge {:first? true :list-count (count list)} (first list)))
                                     (map #(second %) grouped-results))
        selected-interface-id (get-in app [:admin :interface-list :selected-interface-id])
        selected-interfaces (rest                           ;; First is already in first-from-grouped-results array
                              (filter
                                (fn [x]
                                  (= selected-interface-id (:interface-id x)))
                                results))
        rows (doall
               (mapcat (fn [{:keys [interface-id operator-name format data-content url imported import-error db-error created first? list-count]
                             :as interface}]
                         [^{:key (str "tbl-row-" interface created)}
                          (interface-table-row e! interface-id data-content operator-name format import-error url
                                               date-0 imported db-error interface first? list-count selected-interface-id)])
                       (sort-by :interface-id (concat
                                                first-from-grouped-results selected-interfaces))))]
    [:div.row
     [:div.row.col-md-12 {:style {:padding-top "20px"}}
      [form/form {:update! #(e! (admin-controller/->UpdateInterfaceFilters %))}
       [(form/group
          {:label "Etsi rajapintoja"
           :columns 3
           :layout :row}
          {:type :string
           :name :operator-name
           :label "Palveluntuottaja"
           :hint-text "Palveluntuottajan nimi tai sen osa"
           :container-class "col-xs-12 col-sm-6 col-md-4"}
          {:type :string
           :name :service-name
           :label "Palvelu"
           :hint-text "Palvelun nimi tai sen osa"
           :container-class "col-xs-12 col-sm-6 col-md-4"}
          {:type :string
           :name :interface-url
           :label "Rajapinnan osoite"
           :container-class "col-xs-12 col-sm-6 col-md-4"}
          {:name :interface-format
           :type :selection
           :options interface-formats
           :label "Tyyppi"
           :hint-text "Palvelun nimi tai sen osa"
           :show-option (tr-key [:admin-page :interface-formats])
           :update! #(e! (admin-controller/->UpdatePublishedFilter %))}
          {:name :radio-group
           :type :component
           :width "100%"
           :container-class "col-xs-12 col-sm-10 col-md-10"
           :full-width? true
           :component (fn [_]
                        [:div {:style {:display "flex"
                                       :flex-direction "row"}}
                         [ui/radio-button-group {:name (str "admin-interface-type-selection")
                                                 :value-selected :all
                                                 :style {:display "flex"
                                                         :flex-direction "row"}}
                          [ui/radio-button {:style {:width "200px"}
                                            :label "Kaikki rajapinnat"
                                            :id "radio-interface-all"
                                            :value :all
                                            :on-click #(e! (admin-controller/->UpdateInterfaceRadioFilter :all))}]
                          [ui/radio-button {:style {:width "200px"}
                                            :label "Käsittelyvirhe"
                                            :id "radio-interface-db-error"
                                            :value :db-error
                                            :on-click #(e! (admin-controller/->UpdateInterfaceRadioFilter :db-error))}]
                          [ui/radio-button {:style {:width "200px"}
                                            :label "Latausvirhe"
                                            :id "radio-interface-download-error"
                                            :value :import-error
                                            :on-click #(e! (admin-controller/->UpdateInterfaceRadioFilter :import-error))}]
                          [ui/radio-button {:style {:width "200px"}
                                            :label "Palvelut, joilla ei rajapintaa"
                                            :id "radio-no-interface"
                                            :value :no-interface
                                            :on-click #(e! (admin-controller/->UpdateInterfaceRadioFilter :no-interface))}]]])})]
       filters]]

     [buttons/save
      {:on-click #(e! (admin-controller/->SearchInterfaces))
       :disabled (or (str/blank? filter) loading?)}
      "Hae rajapintoja"]

     [:div
      (when loading?
        [:p "Ladataan rajapintoja..."])

      (when results
        [:div
         [:div {:style {:margin "1rem 0 1rem 0"
                        }}
          [:p
           (str " Hakuehdoilla löytyi " (count first-from-grouped-results) " rajapintaa.")]
          [:div
           [buttons/save
            {:on-click #(e! (admin-controller/->DownloadInterfacesCSV))}
            "Lataa rajapinnat CSV:nä"]]]

         [ui/table {:selectable false}
          [ui/table-header {:class "table-header-wrap"
                            :adjust-for-checkbox false
                            :display-select-all false}
           [ui/table-row
            [ui/table-header-column
             {:class "table-header-wrap" :style {:width "2%"}}
             "#"]
            [ui/table-header-column
             {:class "table-header-wrap" :style {:width "17%"}}
             "Palveluntuottaja"]
            [ui/table-header-column
             {:class "table-header-wrap" :style {:width "14%"}}
             "Sisältö"]
            [ui/table-header-column
             {:class "table-header-wrap" :style {:width "8%"}}
             "Tyyppi"]
            [ui/table-header-column
             {:class "table-header-wrap" :style {:width "35%"}}
             "Rajapinta"]
            [ui/table-header-column
             {:class "table-header-wrap" :style {:width "14%"}}
             "Viimeisin käsittely"]
            [ui/table-header-column
             {:class "table-header-wrap" :style {:width "10%"}}
             "Katso"]]]
          [ui/table-body {:display-row-checkbox false}
           rows]]])

      (error-modal e! app)
      (operator-modal e! app)]]))
