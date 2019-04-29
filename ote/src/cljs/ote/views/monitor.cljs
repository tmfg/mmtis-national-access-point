(ns ote.views.monitor
  (:require [reagent.core :as r]
            [ote.ui.common :as ui-common]
            [ote.localization :refer [tr tr-key]]
            [ote.app.controller.monitor :as controller]
            [cljsjs.chartjs]))

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

(defn monitor-main [e! {:keys [page monitor-data] :as app}]
  (e! (controller/->QueryMonitorReport))
  (if monitor-data
    (let [translate-typekw (fn [type]
                             (tr [:enums :ote.db.transport-service/sub-type (keyword type)]))
          companies-by-month-data {:labels (mapv :month (:monthly-companies monitor-data))
                                   :datasets [{:label "Liikkumispalveluiden tarjoajien lukumäärä"
                                               :data (mapv :sum (:monthly-companies monitor-data))
                                               :backgroundColor "rgb(0, 170, 187)"}]}
          companies-by-tertiili-data {:labels (mapv :tertiili (:tertiili-companies monitor-data))
                                      :datasets [{:label "Liikkumispalveluiden tarjoajien lukumäärä"
                                                  :data (mapv :sum (:tertiili-companies monitor-data))
                                                  :backgroundColor "rgb(0, 170, 187)"}]}
          provider-share-by-type-data {:labels (mapv translate-typekw
                                                     (mapv :sub-type (:companies-by-service-type monitor-data)))
                                       :datasets [{:data (mapv :count (:companies-by-service-type monitor-data))
                                                   :backgroundColor ["rgb(0,170,187)"
                                                                     "rgb(102,214,184)"
                                                                     "rgb(102,204,102)"
                                                                     "rgb(221,204,0)"
                                                                     "rgb(255,136,0)"
                                                                     "rgb(255,102,153)"
                                                                     "rgb(235,102,204)"]
                                                   :label " Palvelut tyypeittäin "}]}
          monthly-types (:monthly-types monitor-data)
          tertiili-types (:tertiili-types monitor-data)]
      (fn []
        [:div
         [:h1 " Valvontanäkymä "]
         [:p "Valvontanäkymässä voit tarkastella liikkumispalveluiden tarjoajien kokonaismäärän
              kehitystä kokonaisuutena, sekä jaoteltuna liikkumispalvelutyyppien mukaan."]
         [:p "Termi "
          [:strong "Liikkumispalveluiden"] " tarjoajat tarkoittaa sekä NAPiin rekisteröityneitä "
          [:strong " liikkumispalveluiden tuottajia"] ", että sellaisia yrityksiä, joista on maininta NAP:ssa
          julkaistuiden palveluiden tiedoissa (ns. " [:strong "palveluiden tuottamiseen osallistuvat yritykset"] ").
          Esimerkki palvelun tuottamiseen osallistuvasta yrityksestä on yksityinen elinkeinon harjoittaja,
          joka ajaa ainoastaan taksivälityskeskuksen kyytejä."
          ]
         [:h2 " Liikkumispalveluiden tarjoajien lukumäärän kehitys kuukausittain "]
         [ui-common/linkify " /admin/reports/monitor/csv/monthly-companies " " Lataa kuvaajan tiedot CSV:nä "]
         [:div {:style {:width "75%"}}
          [barchart-inner " bar-companies-by-month " nil companies-by-month-data]]

         [:h2 " Liikkumispalveluiden tarjoajien lukumäärän kehitys tertiileittäin "]
         [ui-common/linkify " /admin/reports/monitor/csv/tertiili-companies " " Lataa kuvaajan tiedot CSV:nä "]
         [:div {:style {:width "75%"}}
          [barchart-inner " bar-companies-by-tertiili " nil companies-by-tertiili-data]]

         [:h2 " Liikkumispalveluiden tarjoajien lukumäärä jaoteltuna liikkumispalvelutyypin mukaan kuukausittain"]
         [:p "Yhdellä liikkumispalveluita tarjovalla palveluntuottajalla voi olla monta erilaista palvelua. Tästä syystä
         palvelutyypeittäin jaottelu tuottaa eri lukumäärän palveluntuottajia."]
         [ui-common/linkify " /admin/reports/monitor/csv/monthly-companies-by-service-type " " Lataa kuvaajan tiedot CSV:nä "]
         [:div {:style {:width "75%"}}
          [barchart-inner " bar-type-by-month " "right" monthly-types]]

         [:h2 " Liikkumispalveluiden tarjoajien lukumäärä jaoteltuna liikkumispalvelutyypin mukaan tertiileittäin"]
         [:p "Yhdellä liikkumispalveluita tarjovalla palveluntuottajalla voi olla monta erilaista palvelua. Tästä syystä
         palvelutyypeittäin jaottelu tuottaa eri lukumäärän palveluntuottajia."]
         [ui-common/linkify " /admin/reports/monitor/csv/tertiili-companies-by-service-type " " Lataa kuvaajan tiedot CSV:nä "]
         [:div {:style {:width "75%"}}
          [barchart-inner " bar-type-by-tertiili " "right" tertiili-types]]

         [:h2 " Liikkumispalveluiden tarjoajien tämänhetkinen lukumäärä liikkumispalvelutyypeittäin "]
         [:p "Yhdellä liikkumispalveluita tarjovalla palveluntuottajalla voi olla monta erilaista palvelua. Tästä syystä
         palvelutyypeittäin jaottelu tuottaa eri lukumäärän palveluntuottajia."]
         [ui-common/linkify " /admin/reports/monitor/csv/company-service-types " " Lataa kuvaajan tiedot CSV:nä "]
         [:div {:style {:width "75%"}}
          [doughnut-inner " donughnut-share-by-type " "right" provider-share-by-type-data]]]))
    [ui-common/loading-spinner]))
