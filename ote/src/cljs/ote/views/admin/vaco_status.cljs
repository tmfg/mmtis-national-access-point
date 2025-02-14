(ns ote.views.admin.vaco-status
  "List all interfaces and their vaco status. This is one of the Admin panel views. Note this has a limited set of users and is not
  currently localized, all UI text is in Finnish."
  (:require [cljs-react-material-ui.reagent :as ui]
            [ote.app.controller.admin :as admin-controller]
            [clojure.string :as str]
            [ote.app.controller.flags :as flags]
            [ote.localization :refer [tr tr-key]]
            [ote.ui.common :as common-ui]
            [ote.time :as time]
            [ote.ui.form :as form]))

(def interface-formats
  (if (flags/enabled? :new-transit-data-formats)
    [:GTFS :GTFS-RT :GBFS :Kalkati :SIRI :NeTEx :GeoJSON :JSON :CSV :ALL]
    [:GTFS :Kalkati.net :ALL]))

(defn vaco-status-page-controls [e! app]
  [:div.row {:style {:padding-top "20px"}}
   [form/form {:update! #(e! (admin-controller/->UpdateVacoStatusFilters %))}
    [(form/group
       {:label "Etsi viimeisimmät VACO konvertoinnit"
        :columns 3
        :layout :row}
       {:name :help-interface
        :type :info-toggle
        :label "Ohjeet VACO statuksen hakemiseen"
        :body [:div
               [:p "Status voidaan hakea palveluntuottajan, palvelun tai rajapinnan osoitteen tiedoilla. Annettujen tietojen ei tarvitse olla täydellisiä. Pieni osa hakuehdosta riittää. "]
               [:p "Hakua voi tarkentaa rajapinnan tyypillä.."]
               [:p "Tuloksiin listataan vain yksi rivi per integraatio."]]
        :default-state false
        :container-class "col-xs-12 col-sm-12 col-md-12"}
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
        :update! #(e! (admin-controller/->UpdatePublishedFilter %))})]
    (get-in app [:admin :vaco-status :filters])]

   [ui/raised-button {:primary true
                      :disabled (str/blank? filter)
                      :on-click #(e! (admin-controller/->SearchVacoStatus))
                      :label "Hae Vacon konvertointitiedot"}]])

(defn interface-table-row [e! operator-name service-name format url tis-success tis-complete tis-magic-link created]
  [ui/table-row {:key (gensym) :selectable false }
   [ui/table-row-column {:style {:width "15%" :padding "0px 5px 0px 5px"}} operator-name]
   [ui/table-row-column {:style {:width "15%" :padding "0px 5px 0px 5px"}} service-name]
   [ui/table-row-column {:style {:width "10%" :padding "0px 5px 0px 5px"}} (first format)]
   [ui/table-row-column {:style {:width "10%" :padding "0px 5px 0px 5px"}} [common-ui/linkify url "Katso" {:target "_blank"}]]
   [ui/table-row-column {:style {:width "10%" :padding "0px 5px 0px 5px"}} (if tis-success "Onnistui" "Virhe!")]
   [ui/table-row-column {:style {:width "10%" :padding "0px 5px 0px 5px"}} (if tis-complete "Valmistui" "Virhe!")]
   [ui/table-row-column {:style {:width "20%" :padding "0px 5px 0px 5px"}} [common-ui/linkify tis-magic-link "Katso" {:target "_blank"}]]
   [ui/table-row-column {:style {:width "10%" :padding "0px 5px 0px 5px"}} (time/format-timestamp-for-ui created)]])

(defn interface-list [e! app]
  (let [{:keys [loading? results filters]} (get-in app [:admin :vaco-status])
        rows (doall
               (mapcat (fn [{:keys [operator-name service-name format url tis-success tis-complete tis-magic-link created]
                             :as interface}]
                         [^{:key (str "tbl-row-" (hash (str interface created)))}
                          (interface-table-row e! operator-name service-name format url tis-success tis-complete tis-magic-link created)])
                       results))]

    [:div.row
     [:div
      (when loading?
        [:p "Ladataan vaco integraation tuloksia..."])

      (when results
        [:div
         [:h2 "Viimeisimmät vaco integraation tulokset"]
         [:div {:style {:margin "1rem 0 1rem 0"}}
          [:p
           (str " Hakuehdoilla löytyi " (count results) " pakettia.")]]

         [ui/table {:selectable false}
          [ui/table-header {:class "table-header-wrap"
                            :adjust-for-checkbox false
                            :display-select-all false}
           [ui/table-row
            [ui/table-header-column {:class "table-header-wrap" :style {:width "15%"}} "Palveluntuottaja"]
            [ui/table-header-column {:class "table-header-wrap" :style {:width "15%"}} "Palvelu"]
            [ui/table-header-column {:class "table-header-wrap" :style {:width "10%"}} "Tyyppi"]
            [ui/table-header-column {:class "table-header-wrap" :style {:width "10%"}} "Rajapinta"]
            [ui/table-header-column {:class "table-header-wrap" :style {:width "10%"}} "Status"]
            [ui/table-header-column {:class "table-header-wrap" :style {:width "10%"}} "Valmistui"]
            [ui/table-header-column {:class "table-header-wrap" :style {:width "20%"}} "Vaco Linkki"]
            [ui/table-header-column {:class "table-header-wrap" :style {:width "10%"}} "Valmistui"]]]
          [ui/table-body {:display-row-checkbox false}
           rows]]])]]))
