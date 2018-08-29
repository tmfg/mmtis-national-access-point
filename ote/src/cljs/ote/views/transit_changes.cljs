(ns ote.views.transit-changes
  "Transit changes view. Shows when regular route based traffic schedules
  change with links to detailed view (transit visualization)"
  (:require [reagent.core :as r]
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
            [ote.ui.form-fields :as form-fields]))

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
  [:div.transit-changes-legend (use-style style/transit-changes-legend)
   [:div [:b "Taulukon ikonien selitteet"]]
   (for [[icon label] [[ic/content-add-circle-outline " Uusia reittejä"]
                       [ic/content-remove-circle-outline " Päättyviä reittejä"]
                       [ic/editor-format-list-bulleted " Reittimuutoksia"]]]
     ^{:key label}
     [:div (use-style style/transit-changes-legend-icon)
      [icon]
      [:div (use-style style/change-icon-value) label]])])

(def change-keys #{:added-routes :removed-routes :changed-routes :changes?})

(defn cap-number [n]
  [:div (use-style style/change-icon-value)
   (if (> n 500)
     "500+"
     (str n))])

(defn change-icons [{:keys [added-routes removed-routes changed-routes]}]
  [:div.transit-change-icons
   [:div (use-style style/transit-changes-legend-icon)
    [ic/content-add-circle-outline {:color (if (= 0 added-routes)
                                             style/no-change-color
                                             style/add-color)}] (cap-number added-routes)]
   [:div (use-style style/transit-changes-legend-icon)
    [ic/content-remove-circle-outline {:color (if (= 0 removed-routes)
                                                style/no-change-color
                                                style/remove-color)}] (cap-number removed-routes)]

   [:div (use-style style/transit-changes-legend-icon)
    [ic/editor-format-list-bulleted] (cap-number changed-routes)]])


(defn transit-change-filters [e! {:keys [selected-finnish-regions finnish-regions]}]
  [:div
   [form-fields/field {:label "Maakunta"
                       :type :chip-input
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
    selected-finnish-regions]])

(defn- change-description [{:keys [changes? next-different-week] :as row}]
  (let [{:keys [current-week-traffic different-week-traffic]} next-different-week]
    [:span
     (cond
       (not changes?)
       [:div
        [ic/navigation-check]
        [:div (use-style style/change-icon-value)
         "Ei muutoksia"]]

       ;; FIXME: lisää mahdollinen liikenteen päättyminen / alkaminen

       #_(and (not (empty? (:days-with-traffic current-week-traffic)))
              (empty? (:days-with-traffic different-week-traffic)))
       #_[:div
          [ic/communication-business {:color style/remove-color}]
          [:div (use-style style/change-icon-value)
           "Mahdollinen liikenteen päättyminen"]]

       #_(and (empty? (:days-with-traffic current-week-traffic))
              (not (empty? (:days-with-traffic different-week-traffic))))
       #_[:div
          [ic/communication-business {:color style/add-color}] ;; FIXME: Seems that there is no domain_disabled icon available in our MUI version
          [:div (use-style style/change-icon-value)
           "Mahdollinen liikenteen alkaminen"]]
       :default
       [change-icons row])]))

(defn detected-transit-changes [e! {:keys [loading? changes selected-finnish-regions] :as transit-changes}]
  [:div.transit-changes
   [:h3 "Säännöllisen markkinaehtoisen reittiliikenteen tulevat muutokset"]
   [:p
    "Taulukossa on listattu " [:b "säännöllisen aikataulun mukaisen liikenteen"]
    " palveluista havaittuja muutoksia. "
    "Voit tarkastella yksittäisessä palvelussa tapahtuvia muutoksia yksityiskohtaisemmin napsauttamalla taulukon riviä. "
    "Yksyityiskohtaiset tiedot avautuvat erilliseen näkymään."]
   [transit-change-filters e! transit-changes]
   [transit-changes-legend]
   [table/table {:no-rows-message (if loading?
                                    "Ladataan muutoksia, odota hetki..."
                                    "Ei löydettyjä muutoksia")
                 :name->label str
                 :label-style {:font-weight "bold"}
                 :stripedRows    true
                 :row-style {:cursor "pointer"}
                 :show-row-hover? true
                 :on-select (fn [evt]
                              (let [change (first evt)
                                    {date1 :date1 date2 :date2} (:first-diff-dates change)]
                                (e! (tc/->ShowChangesForOperator (:transport-operator-id change)
                                                                 (time/format-date-opt date1) (time/format-date-opt date2)))))}
    [{:name "Palveluntuottaja" :read :transport-operator-name :width "25%"}
     {:name "Palvelu" :read :transport-service-name :width "25%"}
     {:name "Aikaa 1:seen muutokseen" :width "25%"
      :read (juxt :change-date :days-until-change)
      :format (fn [[change-date days-until-change]]
                (if change-date
                  [:span
                   (str days-until-change " pv")
                   [:span (stylefy/use-style {:margin-left "5px"
                                              :color "gray"})
                    (str  "(" (time/format-timestamp->date-for-ui change-date) ")")]]
                  "\u2015"))}
     {:name "Muutokset" :width "50%"
      :tooltip "Kaikkien reittien 1:sten muutosten yhteenlaskettu lukumäärä palveluntuottajakohtaisesti."
      :tooltip-len "min-medium"
      :read #(select-keys % change-keys)
      :format change-description
      :col-style {:white-space "pre-wrap"}}]

    (let [region-numbers (when (seq selected-finnish-regions)
                           (into #{} (map (comp :numero :value)) selected-finnish-regions))
          region-matches? (if region-numbers
                            (fn [{regions :finnish-regions}]
                              (some region-numbers regions))
                            (constantly true))]
      (filter region-matches? changes))]])

(defn transit-changes [e! {:keys [page transit-changes] :as app}]
  [ui/tabs {:value (name page)
            :on-change #(e! (fp/->ChangePage (keyword %) nil))}
   [ui/tab {:label "Lomakeilmoitukset" :value "authority-pre-notices"}
    [pre-notices-authority-listing/pre-notices e! app]]
   [ui/tab {:label "Tunnistetut muutokset" :value "transit-changes"}
    [detected-transit-changes e! transit-changes]]])
