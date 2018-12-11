(ns ote.views.monitor
  (:require [reagent.core :as r]
            [cljsjs.chartjs]))

;; Patterned after the advice at
;; https://github.com/Day8/re-frame/blob/master/docs/Using-Stateful-JS-Components.md


;; todo: use time type x axis

(defn barchart-inner [dom-name data]
  (let [chart (atom nil)
        update (fn [comp]
                 (println "barchart update fn called"))
        stacked-chart-options {:scales {:yAxes [{:stacked true}]
                                        :xAxes [{:stacked true}]}}]
    (r/create-class {:reagent-render (fn []
                                       (println "using dom name" dom-name)
                                       [:div "tää ois niinku se kuvaaja"
                                        [:canvas {:id dom-name :width 400 :height 400}]])
                     :component-did-mount (fn [comp]
                                            (println "did-mount: saaatiin comp" (pr-str comp))
                                            (let [canvas (.getElementById js/document dom-name)
                                                  ;; datasets [{:label "stufs"
                                                  ;;            :data (first (:datasets data))}]                                                  
                                                  config {:type "bar"
                                                          :options stacked-chart-options
                                                          :data {:labels (:labels data)
                                                                 :datasets (:datasets data)}}
                                                  new-chart (js/Chart. canvas (clj->js config))]
                                              (println "using options:" (pr-str (:options config)))
                                              (reset! chart {:chart new-chart :config config})
                                              (update comp)))
                     :component-did-update update})))

(defn doughnut-inner [dom-name data]
  (let [chart (atom nil)
        update (fn [comp]
                 (println "doughnut update fn called"))]
    (r/create-class {:reagent-render (fn []
                                       (println "using dom name" dom-name)
                                       [:div "tää ois niinku se kuvaaja"
                                        [:canvas {:id dom-name :width 400 :height 400}]])
                     :component-did-mount (fn [comp]
                                            (println "did-mount: saaatiin comp" (pr-str comp))
                                            (let [canvas (.getElementById js/document dom-name)
                                                  config {:type "doughnut"
                                                          :options {:responsive true
                                                                    :legend {:position "bottom"}}
                                                          :data {:labels (:labels data)
                                                                 :datasets (:datasets data)}}
                                                  new-chart (js/Chart. canvas (clj->js config))]
                                              (println "using options:" (pr-str (:options config)))
                                              (reset! chart {:chart new-chart :config config})
                                              (update comp)))
                     :component-did-update update})))




(defn monitor-main [e! {:keys [page monitor-data] :as app}]
  (let [companies-by-month-data {:labels ["11/2018" "12/2018" "1/2019"]
                                 :datasets [{:label "juttuja" :data [1742 3121 4311]}]}
        type-by-month-data {:labels ["11/2018" "12/2018" "1/2019"]
                            :datasets [{:label "hommia" :data  [1742 3121 4311] :backgroundColor "red"}
                                       {:label "jutskia" :data [111 222 333] :backgroundColor "blue"}]}
        provider-count-by-type-data {:labels ["Taksiliikenne" "Tilausliikenne ja muu kutsuun perustuva liikenne" "Säännöllinen aikataulun mukainen liikenne"]
                                     :datasets [{:data [500 200 50]
                                                 :backgroundColor ["red" "orange" "blue"]
                                                 :label "Tuottajat tyypeittäin"}]}]
    (fn []
      [:div {:style {:width "50%"}}
       [barchart-inner "chart-companies-by-month" companies-by-month-data]
       [doughnut-inner "chart-count-by-type" provider-count-by-type-data]
       [barchart-inner "chart-type-by-month" type-by-month-data]])))
