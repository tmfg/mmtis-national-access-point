(ns ote.views.transit-visualization.transit-changes
  "Transit changes view. Shows when regular route based traffic schedules
  change with links to detailed view (transit visualization)"
  (:require [reagent.core :as r]
            [cljs-react-material-ui.core :refer [color]]
            [ote.ui.tabs :as tabs]
            [ote.ui.icons :as ui-icons]
            [ote.app.controller.transit-changes :as tc]
            [ote.ui.common :as common]
            [ote.ui.table :as table]
            [clojure.string :as str]
            [ote.time :as time]
            [cljs-time.core :as t]
            [ote.localization :refer [tr]]
            [cljs-react-material-ui.reagent :as ui]
            [ote.app.controller.front-page :as fp]
            [ote.views.pre-notices.authority-listing :as pre-notices-authority-listing]
            [cljs-react-material-ui.icons :as ic]
            [ote.style.transit-changes :as style]
            [stylefy.core :refer [use-style]]
            [stylefy.core :as stylefy]
            [ote.db.places :as places]
            [ote.ui.form-fields :as form-fields]
            [ote.ui.icon_labeled :as icon-l]
            [ote.ui.page :as page]
            [ote.style.base :as style-base]
            [ote.ui.info :as info]
            [ote.theme.colors :as colors]))

(defn week-day-short [week-day]
  (tr [:enums :ote.db.transport-service/day :short
       (case week-day
         :monday :MON
         :tuesday :TUE
         :wednesday :WED
         :thursday :THU
         :friday :FRI
         :saturday :SAT
         :sunday :SUN)]))

(defn transit-changes-legend []
  [:div.transit-changes-legend (use-style style/transit-changes-legend-container)
   [:div
    [:b "Taulukon ikonien selitteet"]]
   [:div (stylefy/use-style style/transit-changes-icon-legend-row-container)
    (doall
      (for [[icon color label] [[ic/content-add-circle-outline {:color colors/add-color} " Uusia reittejä"]
                                [ic/content-remove-circle-outline {:color colors/remove-color} " Mahdollisesti päättyviä reittejä"]
                                [ui-icons/outline-ballot {:color colors/remove-color} " Muuttunutta reittiä"]
                                [ic/av-not-interested {:color colors/remove-color} " Reittejä, joissa tauko liikenteessä"]]]
        ^{:key (str "transit-changes-legend-" label)}
        [icon-l/icon-labeled style/transit-changes-icon [icon color] label]))
    [:div {:style {:display "flex"
                   :align-items "center"}}
     [:div (stylefy/use-style style/new-change-legend-icon)
      [:div (stylefy/use-style style/new-change-indicator)]]
     [:span {:style {:margin-left "0.3rem"}} " Viimeisimmät havaitut muutokset"]]]])

(def change-keys #{:added-routes :removed-routes :changed-routes :no-traffic-routes :changes?
                   :interfaces-has-errors? :no-interfaces? :no-interfaces-imported?})

(defn cap-number [n]
  [:div (use-style style/change-icon-value)
   (cond
     (> n 500) "500+"
     (= n 0) [:span {:style {:color colors/icon-disabled}} n]
     :else (str n))])

(defn change-icons [{:keys [added-routes removed-routes changed-routes no-traffic-routes]}]
  [:div.transit-change-icons (stylefy/use-style style/transit-changes-icon-row-container)
   [:div {:style {:width "25%"}}
    [ic/content-add-circle-outline {:color (if (= 0 added-routes)
                                             colors/icon-disabled
                                             colors/add-color)}]
    (cap-number added-routes)]
   [:div {:style {:width "25%"}}
    [ic/content-remove-circle-outline {:color (if (= 0 removed-routes)
                                                colors/icon-disabled
                                                colors/remove-color)}]
    (cap-number removed-routes)]

   [:div {:style {:width "25%"}}
    (if (= 0 changed-routes)
      [ui-icons/outline-ballot-disabled]
      [ui-icons/outline-ballot])
    (cap-number changed-routes)]

   [:div {:style {:width "25%"}}
    [ic/av-not-interested {:color (if (= 0 no-traffic-routes)
                                    colors/icon-disabled
                                    colors/remove-color)}]
    (cap-number no-traffic-routes)]])


(defn transit-change-filters [e! {:keys [selected-finnish-regions finnish-regions show-errors show-contract-traffic]}]
  [:div
   [:h3 "Rajaa taulukkoa"]
   [:div.row
    [:div.col-md-12
     [form-fields/field {:label "Maakunta"
                         :type :chip-input
                         :full-width? true
                         :list-style {:max-height "400px" :overflow "auto"}
                         :suggestions (mapv (fn [{name ::places/nimi :as r}]
                                              {:text name :value r}) finnish-regions)
                         :suggestions-config {:text :text :value :value}
                         :max-results (count finnish-regions)
                         :auto-select? true
                         :open-on-focus? true
                         :allow-duplicates? false
                         :show-option #(str (::places/numero %) " " (::places/nimi %))
                         :show-option-short ::places/numero
                         :update! #(e! (tc/->SetRegionFilter %))}
      selected-finnish-regions]]
    [:div.col-md-12 {:style {:margin-top "10px"}}
     [form-fields/field {:label "Näytä myös palvelut, joiden rajapinta on virheellinen tai rajapinta puuttuu"
                         :type :checkbox
                         :update! #(e! (tc/->ToggleShowAllChanges))}
      show-errors]]
    [:div.col-md-12 {:style {:margin-top "10px"}}
     [form-fields/field {:label "Näytä myös sopimusliikenne"
                         :type :checkbox
                         :update! #(e! (tc/->ToggleShowContractTraffic))}
      show-contract-traffic]]]])

(defn- change-description [{:keys [changes? interfaces-has-errors? no-interfaces? no-interfaces-imported? next-different-week
                                   added-routes changed-routes removed-routes no-traffic-routes] :as row}]
  (let [{:keys [current-week-traffic different-week-traffic]} next-different-week]
    [:span
     (cond
       (and (false? no-interfaces?) interfaces-has-errors?)
       [:div
        [ic/alert-error {:style {:color "CC0000"}}]
        [:div (use-style style/change-icon-value)
         "Virheitä rajapinnoissa"]]
       no-interfaces?
       [:div
        [ic/alert-warning {:style {:color "CCCC00"}}]
        [:div (use-style style/change-icon-value)
         "Ei rajapintoja"]]
       no-interfaces-imported?
       [:div
        [ic/action-info]
        [:div (use-style style/change-icon-value)
         "Rajapintoja ei vielä käsitelty"]]
       (= 0 (+ added-routes changed-routes removed-routes no-traffic-routes))
       [:div
        [ic/navigation-check]
        [:div (use-style style/change-icon-value)
         "Ei muutoksia"]]

       ;; FIXME: lisää mahdollinen liikenteen päättyminen / alkaminen

       #_(and (not (empty? (:days-with-traffic current-week-traffic)))
              (empty? (:days-with-traffic different-week-traffic)))
       #_[:div
          [ic/communication-business {:color colors/remove-color}]
          [:div (use-style style/change-icon-value)
           "Mahdollinen liikenteen päättyminen"]]

       #_(and (empty? (:days-with-traffic current-week-traffic))
              (not (empty? (:days-with-traffic different-week-traffic))))
       #_[:div
          [ic/communication-business {:color colors/add-color}] ;; FIXME: Seems that there is no domain_disabled icon available in our MUI version
          [:div (use-style style/change-icon-value)
           "Mahdollinen liikenteen alkaminen"]]
       :default
       [change-icons row])]))

(defn detected-transit-changes-page-controls [e! {:keys [loading? changes selected-finnish-regions] :as transit-changes}]
  [:div.transit-changes
   [:h3 "Tunnistetut muutokset"]
   [info/info-toggle
    "Ohjeet"
    [:span
     "Taulukossa on listattu säännöllisen aikataulun mukaisen liikenteen palveluissa havaittuja muutoksia "
     [:b "tulevan 30 viikon ajalta."]
     "Voit tarkastella yksittäisen palvelun liikennöinnissä tapahtuvia muutoksia yksityiskohtaisemmin napsauttamalla taulukon riviä. Yksityiskohtaiset tiedot avautuvat erilliseen näkymään."]
    {:default-open? false}]

   [transit-change-filters e! transit-changes]])

(defn- link-to-transit-visualization [row e! link-type]
  (let [date (:date row)
        formatted-date (when date
                         (time/format-date-iso-8601 date))
        transport-service-id (:transport-service-id row)
        operator-name (:transport-operator-name row)
        service-name (:transport-service-name row)
        link-text (if (= :operator link-type)
                    [:span operator-name]
                    [:span service-name])]
    (if date
      [:a {:style {:text-decoration "none" :color colors/gray800}
           :href (str "/transit-visualization/" transport-service-id "/" formatted-date)
           :on-click #(do
                        (.preventDefault %)
                        (e! (tc/->ShowChangesForService transport-service-id date)))}
       link-text]
      link-text)))

(defn detected-transit-changes [e! {:keys [loading? changes selected-finnish-regions show-errors show-contract-traffic]
                                    :as transit-changes}]
  (let [change-list (if show-errors
                      changes
                      ;; Filter errors out
                      (filter
                        (fn [change]
                          (and
                            (not (:interfaces-has-errors? change))
                            (not (:no-interfaces-imported? change))
                            (not (:no-interfaces? change))))
                        changes))
        change-list (if show-contract-traffic
                      change-list
                      ;; Filter contract traffic out
                      (filter
                        (fn [change]
                          (true? (:commercial? change)))
                        change-list))

        filter-missing-different-week-date (filter #(nil? (:different-week-date %)) change-list)
        filter-different-week-date (filter #(not (nil? (:different-week-date %))) change-list)
        sorted-change-list (sort-by :different-week-date < filter-different-week-date)
        change-list (concat sorted-change-list filter-missing-different-week-date)]
    [:div.transit-changes
     [detected-transit-changes-page-controls e! transit-changes]
     [transit-changes-legend]
     [table/table {:table-name "tbl-transit-changes"
                   :no-rows-message (if loading?
                                      "Ladataan muutoksia, odota hetki..."
                                      "Ei löydettyjä muutoksia")
                   :name->label str
                   :label-style (merge style-base/table-col-style-wrap {:font-weight "bold"})
                   :stripedRows true
                   :row-style {:cursor "pointer"}
                   :show-row-hover? true
                   :on-select (fn [evt]
                                (let [{:keys [transport-service-id date]} (first evt)]
                                  (when date
                                    (e! (tc/->ShowChangesForService transport-service-id date)))))}
      [{:name ""
        :read identity
        :format (fn [{:keys [recent-change? date]}]
                  (when recent-change?
                    [:div (merge (stylefy/use-style style/new-change-container)
                                 {:title (str "Muutos tunnistettu: " (time/format-timestamp->date-for-ui date))})
                     [:div (stylefy/use-style style/new-change-indicator)]]))
        :col-style style-base/table-col-style-wrap
        :width "2%"}
       {:name "Palveluntuottaja"
        :read identity
        :format #(link-to-transit-visualization % e! :operator)
        :col-style style-base/table-col-style-wrap
        :width "20%"}
       {:name "Palvelu"
        :read identity
        :format #(link-to-transit-visualization % e! :service)
        :col-style style-base/table-col-style-wrap
        :width "20%"}
       {:name "Aikaa 1. muutokseen" :width "15%"
        :read identity
        :col-style style-base/table-col-style-wrap
        :format (fn [{:keys [different-week-date days-until-change]}]
                  (if (and different-week-date (not (nil? different-week-date)))
                    [:span
                     (str days-until-change " " (tr [:common-texts :time-days-abbr]))
                     [:span (stylefy/use-style {:margin-left "5px"
                                                :color "gray"})
                      (str "(" (time/format-timestamp->date-for-ui different-week-date) ")")]]
                    "\u2015"))}
       {:name "Tiedot saatavilla (asti)"
        :read (comp time/format-timestamp->date-for-ui :max-date)
        :col-style style-base/table-col-style-wrap
        :width "13%"}
       {:name "Muutokset"
        :width "30%"
        :tooltip "Palvelun kaikkien reittien tulevien muutosten yhteenlaskettu lukumäärä."
        :tooltip-len "min-medium"
        :read #(select-keys % change-keys)
        :format change-description
        :col-style style-base/table-col-style-wrap}]

      (let [region-numbers (when (seq selected-finnish-regions)
                             (into #{} (map (comp :numero :value)) selected-finnish-regions))
            region-matches? (if region-numbers
                              (fn [{regions :finnish-regions}]
                                (some region-numbers regions))
                              (constantly true))]
        (filter region-matches? change-list))]]))

(defn transit-changes [e! {:keys [page transit-changes] :as app}]
  (let [tabs [{:label "Lomakeilmoitukset" :value "authority-pre-notices"}
              {:label "Tunnistetut muutokset (testi)" :value "transit-changes"}]
        selected-tab (or (get-in app [:transit-changes :selected-tab])
                         (when page
                           (name page)))]
    [:div
     [page/page-controls "" "Markkinaehtoisen liikenteen muutokset"
      [:div
       [tabs/tabs tabs {:update-fn #(e! (tc/->ChangeTab %))
                        :selected-tab selected-tab}]]]
     [:div.container
      (case selected-tab
        "authority-pre-notices" [pre-notices-authority-listing/pre-notices e! app]
        "transit-changes" [detected-transit-changes e! transit-changes]
        ;;default
        [pre-notices-authority-listing/pre-notices e! app])]]))
