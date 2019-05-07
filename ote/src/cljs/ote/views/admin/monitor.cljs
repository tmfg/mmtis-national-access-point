(ns ote.views.admin.monitor
  (:require [reagent.core :as r]
            [ote.ui.common :as ui-common]
            [ote.ui.buttons :as btn]
            [ote.localization :refer [tr tr-key]]
            [ote.app.controller.monitor :as monitor-controller]
            [cljsjs.chartjs]
            [ote.ui.form-fields :as form-fields]
            [ote.theme.colors :as colors]
            [stylefy.core :as stylefy]
            [ote.style.buttons :as style-buttons]
            [ote.style.base :as style-base]
            [cljs-react-material-ui.icons :as ic]
            [ote.time :as time]))

;; Patterned after the advice at
;; https://github.com/Day8/re-frame/blob/master/docs/Using-Stateful-JS-Components.md

;; todo: use time type x axis

(def legend {:legend {:display true
                      :fullWidth true
                      :labels {:boxWidth 5
                               :fontSize 14
                               :usePointStyle true
                               :pointStyle "circle"}}})

(defn barchart-inner [dom-name legend-position data]
  (let [chart (atom nil)
        update (fn [comp]
                 #_(println "barchart update fn called"))
        stacked-chart-options (merge
                                (if legend-position
                                  (assoc-in legend [:legend :position] legend-position)
                                  (assoc-in legend [:legend :display] false))
                                {:scales {:yAxes [{:stacked true}]
                                          :xAxes [{:stacked true}]}})]
    (r/create-class {:reagent-render (fn []
                                       [:div
                                        [:canvas {:id dom-name :width 400 :height 150}]])
                     :component-did-mount (fn [comp]

                                            (let [canvas (.getElementById js/document dom-name)
                                                  ;; datasets [{:label "stufs"
                                                  ;;            :data (first (:datasets data))}]                                                  
                                                  config {:type "bar"
                                                          :options stacked-chart-options
                                                          :data {:labels (:labels data)
                                                                 :datasets (:datasets data)}}
                                                  new-chart (js/Chart. canvas (clj->js config))]
                                              (reset! chart {:chart new-chart :config config})
                                              (update comp)))
                     :component-did-update update})))

(defn doughnut-inner [dom-name legend-position data]
  (let [chart (atom nil)
        update (fn [comp]
                 #_(println "doughnut update fn called"))]
    (r/create-class {:reagent-render (fn []
                                       [:div
                                        [:canvas {:id dom-name :width 400 :height 150}]])
                     :component-did-mount (fn [comp]
                                            (let [canvas (.getElementById js/document dom-name)
                                                  config {:type "doughnut"
                                                          :options (merge
                                                                     (assoc-in legend [:legend :position] legend-position)
                                                                     {:responsive true})
                                                          :data {:labels (:labels data)
                                                                 :datasets (:datasets data)}}
                                                  new-chart (js/Chart. canvas (clj->js config))]
                                              (reset! chart {:chart new-chart :config config})
                                              (update comp)))
                     :component-did-update update})))

(defn monitor-main [e! app]
  (if-let [monitor-data (get-in app [:admin :monitor :monitor-data])]
    (let [translate-typekw (fn [type]
                              (tr [:enums :ote.db.transport-service/sub-type (keyword type)]))
           companies-by-month-data {:labels (mapv :month (:monthly-companies monitor-data))
                                    :datasets [{:label "Palveluntuottajat"
                                                :data (mapv :sum-providing (:monthly-companies monitor-data))
                                                :backgroundColor "rgb(0,136,160)"}
                                               {:label "Palveluiden tuottamiseen osallistuvat yritykset"
                                                :data (mapv :sum-participating (:monthly-companies monitor-data))
                                                :backgroundColor "rgb(102,204,214)"}]}
           companies-by-tertile-data {:labels (mapv :tertile (:tertile-companies monitor-data))
                                      :datasets [{:label "Palveluntuottajat"
                                                  :data (mapv :sum-providing (:tertile-companies monitor-data))
                                                  :backgroundColor "rgb(0,136,160)"}
                                                 {:label "Palveluiden tuottamiseen osallistuvat yritykset"
                                                  :data (mapv :sum-participating (:tertile-companies monitor-data))
                                                  :backgroundColor "rgb(102,204,214)"}]}
           provider-share-by-type-data {:labels (mapv translate-typekw
                                                      (mapv :sub-type (:companies-by-service-type monitor-data)))
                                        :datasets [{:data (mapv :count (:companies-by-service-type monitor-data))
                                                    :backgroundColor [colors/monitor-taxi-color
                                                                      colors/monitor-request-color
                                                                      colors/monitor-schedule-color
                                                                      colors/monitor-terminal-color
                                                                      colors/monitor-rental-color
                                                                      colors/monitor-parking-color
                                                                      colors/monitor-brokerage-color]
                                                    :label " Palvelut tyypeittäin "}]}
           monthly-types (:monthly-types monitor-data)
           tertile-types (:tertile-types monitor-data)
           chart-type (get-in app [:admin :monitor :report-type])]
    [:div
     [:h1 "Valvontanäkymä"]
     [:p "Valvontanäkymässä voit tarkastella liikkumispalveluiden tarjoajien kokonaismäärän
              kehitystä kokonaisuutena, sekä jaoteltuna liikkumispalvelutyyppien mukaan."]
     [:p "Termi "
      [:strong "Liikkumispalveluiden tarjoajat"] " tarkoittaa NAPiin rekisteröityneitä "
      [:strong " liikkumispalveluiden tuottajia"] ", sekä yrityksiä, jotka vain " [:strong "osallistuvat palveluiden tuottamiseen. "]
      "Palvelun tuottamiseen osallistuva yritys on esimerkiksi yksityinen elinkeinon harjoittaja, joka ajaa ainoastaan taksivälityskeskusken kyytejä."]

     [:div {:style {:padding-top "2rem"}}
      [:div
       [btn/big-icon-button-with-label
        {:id "btn-all-companies-csv"
         :on-click #(e! (monitor-controller/->DownloadCsv "/admin/reports/monitor/csv/all-companies" (str "yritykset-" (time/format-date-iso-8601 (time/now)) ".csv")))
         :style {:padding "1rem"}}
        [ic/action-description {:style {:width 30
                                          :height 30
                                          :margin-right "0.5rem"
                                          :color colors/primary}}]
        "Lataa liikkumispalveluiden tarjoajien tiedot CSV:nä"]]
      [:div
       [form-fields/field
        {:label "Kaavioiden aikayksikkö"
         :type :selection
         :update! #(e! (monitor-controller/->ChangeReportType %))
         :show-option (tr-key [:admin-page :report-types])
         :options [:tertile :month]}
        (get-in app [:admin :monitor :report-type])]]]

     (if (= chart-type :month)
       [:div {:id "month-charts" :key "month-charts"}
        [:div {:id "container-bar-companies-by-month"
               :key "container-bar-companies-by-month"
               :style {:margin-bottom "4rem"}}
         [:h2 " Liikkumispalveluiden tarjoajien lukumäärän kehitys kuukausittain "]
         [ui-common/linkify "/admin/reports/monitor/csv/monthly-companies" "Lataa kuvaajan tiedot CSV:nä"]
         [:div {:style {:width "100%"}}
          [barchart-inner "bar-companies-by-month" "right" companies-by-month-data]]]
        [:div {:id "container-bar-sub-types-by-month"
               :key "container-bar-sub-types-by-month"
               :style {:margin-bottom "4rem"}}
         [:h2 " Liikkumispalveluiden tarjoajien lukumäärä jaoteltuna liikkumispalvelutyypin mukaan kuukausittain"]
         [:p "Yksittäinen palveluntuottaja voi tarjota useita erilaisia liikkumispalveluita. Tästä syystä alla olevan
          kuvaajan yhteenlaskettu lukumäärä on suurempi, kuin NAP:issa ilmoitettu liikkumispalveluiden tarjoajien kokonaismäärä."]
         [ui-common/linkify " /admin/reports/monitor/csv/monthly-companies-by-service-type " " Lataa kuvaajan tiedot CSV:nä "]
         [:div {:style {:width "100%"}}
          [barchart-inner "bar-type-by-month" "right" monthly-types]]]]
       [:div {:id "tertile-charts" :key "tertile-charts"}
        [:div {:id "container-bar-companies-by-tertile"
               :key "container-bar-companies-by-tertile"
               :style {:margin-bottom "4rem"}}
         [:h2 " Liikkumispalveluiden tarjoajien lukumäärän kehitys tertiileittäin "]
         [ui-common/linkify "/admin/reports/monitor/csv/tertile-companies" "Lataa kuvaajan tiedot CSV:nä"]
         [:div {:style {:width "100%"}}
          [barchart-inner "bar-companies-by-tertile" "right" companies-by-tertile-data]]]
        [:div {:id "container-bar-sub-types-by-tertile"
               :key "container-bar-sub-types-by-tertile"
               :style {:margin-bottom "4rem"}}
         [:h2 " Liikkumispalveluiden tarjoajien lukumäärä jaoteltuna liikkumispalvelutyypin mukaan tertiileittäin"]
         [:p "Yksittäinen palveluntuottaja voi tarjota useita erilaisia liikkumispalveluita. Tästä syystä alla olevan
          kuvaajan yhteenlaskettu lukumäärä on suurempi, kuin NAP:issa ilmoitettu liikkumispalveluiden tarjoajien kokonaismäärä."]
         [ui-common/linkify "/admin/reports/monitor/csv/tertile-companies-by-service-type" "Lataa kuvaajan tiedot CSV:nä"]
         [:div {:style {:width "100%"}}
          [barchart-inner "bar-type-by-tertile" "right" tertile-types]]]])

     [:div {:id "container-company-service-types"
            :key "container-company-service-types"
            :style {:margin-bottom "4rem"}}
      [:h2 " Liikkumispalveluiden tarjoajien tämänhetkinen lukumäärä liikkumispalvelutyypeittäin "]
      [ui-common/linkify " /admin/reports/monitor/csv/company-service-types " " Lataa kuvaajan tiedot CSV:nä "]
      [:div {:style {:width "100%"}}
       [doughnut-inner "donughnut-share-by-type" "right" provider-share-by-type-data]]]])
    [:div
     [ui-common/loading-spinner]]))
