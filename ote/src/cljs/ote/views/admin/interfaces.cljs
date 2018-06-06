(ns ote.views.admin.interfaces
  "Admin panel views. Note this has a limited set of users and is not
  currently localized, all UI text is in Finnish."
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
            [ote.style.admin :as style-admin]
            [ote.app.controller.front-page :as fp]))

(defn error-modal [e! interface]
  (when (:show-error-modal? interface)
    [ui/dialog
     {:open                     true
      :modal                    false
      :auto-scroll-body-content true
      :on-request-close         #(e! (admin-controller/->CloseInterfaceErrorModal (:interface-id interface)))
      :title                    "Rajapinnan käsittelyssä tapahtunut virhe"
      :actions                  [(r/as-element
                                   [ui/flat-button
                                    {:label     (tr [:buttons :close])
                                     :secondary true
                                     :primary   true
                                     :on-click  #(e! (admin-controller/->CloseInterfaceErrorModal (:interface-id interface)))}])]}
     [:div.col-md-8
      [:div.row
       [:div.col-md-6 (stylefy/use-style style-admin/modal-data-label) "Rajapinnan osoite: "]
       [:div.col-md-6 (:url interface)]]
      [:div.row
       [:div.col-md-6 (stylefy/use-style style-admin/modal-data-label) "Rajapinnan tyyppi: "]
       [:div.col-md-6 (str/join ", " (:format interface))]]
      [:div.row
       [:div.col-md-6 (stylefy/use-style style-admin/modal-data-label) "Virhe: "]
       [:div.col-md-6 (:import-error interface) (:db-error interface)]]]]))

(defn operator-modal [e! interface]
  (when (:show-operator-modal? interface)
    [ui/dialog
     {:open                     true
      :modal                    false
      :auto-scroll-body-content true
      :on-request-close         #(e! (admin-controller/->CloseOperatorModal (:interface-id interface)))
      :title                    "Palvelun ja palveluntuottajan tiedot"
      :actions                  [(r/as-element
                                   [ui/flat-button
                                    {:label     (tr [:buttons :close])
                                     :secondary true
                                     :primary   true
                                     :on-click  #(e! (admin-controller/->CloseOperatorModal (:interface-id interface)))}])]}
     [:div.col-md-6 {:style {:border-right "1px solid gray"}}
      [:div.row [:h2 "Rajapinnan tuottaa"]]
      [:div.row
       [:div.col-md-4 (stylefy/use-style style-admin/modal-data-label) "Nimi: "]
       [:div.col-md-6 [:a {:href     "#"
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
      [:div.row [:h2 "Rajapinta kuuluu palveluun:"]]
      [:div.row
       [:div.col-md-4 (stylefy/use-style style-admin/modal-data-label) "Nimi: "]
       [:div.col-md-6 [:a {:href     "#"
                           :on-click #(do
                                        (.preventDefault %)
                                        (e! (fp/->ChangePage :edit-service {:id (:service-id interface)})))}
                       (:service-name interface)]]]
      [:div.row
       [:div.col-md-4 (stylefy/use-style style-admin/modal-data-label) "Puhelin: "]
       [:div.col-md-6 (:service-phone interface)]]
      [:div.row
       [:div.col-md-4 (stylefy/use-style style-admin/modal-data-label) "Sähköposti: "]
       [:div.col-md-6 (:service-email interface)]]]]))

(defn parse-content-value [value-array]
  (let [data-content-value #(tr [:enums ::t-service/interface-data-content %])
        value-str (str/join ", " (map #(data-content-value (keyword %)) value-array))
        return-value (common-ui/maybe-shorten-text-to 45 value-str)]
    return-value))

(defn- gtfs-viewer-link [url format]
  (when format
    (let [format (str/lower-case format)]
      (when (or (= "gtfs" format) (= "kalkati.net" format))
        (common-ui/linkify
          (str "#/routes/view-gtfs?url=" url
               (when (= "kalkati.net" format)
                 "&type=kalkati"))
          (tr [:service-search :view-routes])
          {:target "_blank"})))))

(defn interface-list [e! app]
  (let [{:keys [loading? results interface-operator-filter interface-service-filter]}
        (get-in app [:admin :interface-list])]
    [:div.row
     [:div.row.col-md-6
      [form-fields/field {:type    :string
                          :label   "Hae palveluntuottajan nimellä tai sen osalla"
                          :update! #(e! (admin-controller/->UpdateInterfaceOperatorFilter %))}
       interface-operator-filter]

      [ui/raised-button {:primary  true
                         :disabled (str/blank? filter)
                         :on-click #(e! (admin-controller/->SearchInterfacesByOperator))
                         :label    "Hae rajapintoja"}]]
     [:div.row.col-md-6
      [form-fields/field {:type    :string
                          :label   "Hae palvelun nimellä tai sen osalla"
                          :update! #(e! (admin-controller/->UpdateInterfaceServiceFilter %))}
       interface-service-filter]

      [ui/raised-button {:primary  true
                         :disabled (str/blank? filter)
                         :on-click #(e! (admin-controller/->SearchInterfacesByService))
                         :label    "Hae rajapintoja"}]]
     [:div.row
      (when loading?
        [:span "Ladataan rajapintoja..."])

      (when results
        [:span
         [:div "Hakuehdoilla löytyi " (count results) " rajapintaa."]
         [ui/table {:selectable false}
          [ui/table-header {:adjust-for-checkbox false
                            :display-select-all  false}
           [ui/table-row
            [ui/table-header-column {:style {:width "15%"}} "Palveluntuottaja"]
            [ui/table-header-column {:style {:width "20%"}} "Sisältö"]
            [ui/table-header-column {:width "10%"} "Tyyppi"]
            [ui/table-header-column {:width "15%"} "Viimeisin käsittely"]
            [ui/table-header-column {:width "10%"} "Latausvirhe"]
            [ui/table-header-column {:width "10%"} "Käsittelyvirhe"]
            [ui/table-header-column {:width "10%"} "Katso"]]]
          [ui/table-body {:display-row-checkbox false}
           (doall
             (for [{:keys [interface-id operator-name format data-content url imported import-error db-error] :as interface} results]
               ^{:key (str "link_" interface-id)}
               [ui/table-row {:selectable false}
                [ui/table-row-column {:style {:width "15%"}} [:a {:href     "#"
                                                                  :on-click #(do (.preventDefault %)
                                                                                 (e! (admin-controller/->OpenOperatorModal interface-id)))}
                                                              operator-name]]
                [ui/table-row-column {:style {:width "20%"}} (parse-content-value data-content)]
                [ui/table-row-column {:style {:width "10%"}} (first format)]
                [ui/table-row-column {:style {:width "15%"}} (time/format-timestamp-for-ui imported)]
                [ui/table-row-column {:style {:width "10%"}} (when import-error
                                                               [:a {:href     "#"
                                                                    :on-click #(do (.preventDefault %)
                                                                                   (e! (admin-controller/->OpenInterfaceErrorModal interface-id)))}
                                                                "Ks. Virhe"])]
                [ui/table-row-column {:style {:width "10%"}} (when db-error
                                                               [:a {:href     "#"
                                                                    :on-click #(do (.preventDefault %)
                                                                                   (e! (admin-controller/->OpenInterfaceErrorModal interface-id)))}
                                                                "Ks. Virhe"])]
                [ui/table-row-column {:style {:width "10%"}} (when (and (nil? import-error) (nil? db-error)) [gtfs-viewer-link url (first format)])]
                (error-modal e! interface)
                (operator-modal e! interface)]))]]])]]))
