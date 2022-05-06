(ns ote.views.admin.taxi-prices
  (:require [cljs-react-material-ui.reagent :as ui]
            [clojure.string :as str]
            [stylefy.core :as stylefy]
            [ote.localization :refer [tr tr-key]]
            [ote.time :as time]
            [ote.style.base :as style-base]
            [ote.ui.buttons :as buttons]
            [ote.ui.common :refer [linkify]]
            [ote.app.controller.admin-validation :as admin-validation]
            [ote.app.controller.taxi-prices :as taxi-prices-controller]
            [reagent.core :as r]
            [ote.style.dialog :as style-dialog]
            [cljs-react-material-ui.icons :as ic]
            [taxiui.views.components.formatters :as formatters]
            [ote.ui.form-fields :as form-fields]))

; XXX: tuck events refused to work in Material UI's table#on-row-selection; `(e! event params)` call would make the
;      whole page fail so instead this page works on internal atom and state management which bypasses the entire app
; This atom contains the selected _indices_ (=rows)
(defonce selected-prices (atom []))

(defn page-controls [e! app]
  (fn [e! app]
    [:div.row {:style {:padding-top "20px"}}
     [ui/raised-button {:primary true
                        :disabled (str/blank? filter)
                        :on-click (fn [e]
                                    (let [prices (get-in app [:admin :taxi-prices :results])]
                                      (e! (taxi-prices-controller/->ApproveByIds (->> (map #(get prices %) @selected-prices)
                                                                                      (mapv :id))))
                                      ; reset the atom immediately just in case
                                      (reset! selected-prices [])))
                      :label "Hyväksy valitut"}]]))

(defn taxi-prices [e! app]
  (fn [e! app]
    (let [services (get-in app [:admin :taxi-prices :results])]
      [:div.row
       (when services
         [:span
          [ui/table {:multi-selectable true
                     :on-row-selection (fn [maybe-keys]
                                         (let [items #(count (get-in app [:admin :taxi-prices :results]))
                                               selected (cond
                                                          (= "all" maybe-keys) (vec (range 0 (items)))
                                                          (= "none" maybe-keys) []
                                                          :else (vec (map int maybe-keys)))]
                                           (reset! selected-prices selected))
                                         maybe-keys)}
           [ui/table-header {:class               "table-header-wrap"
                             :adjust-for-checkbox true
                             :display-select-all  true}
            [ui/table-row
             [ui/table-header-column {:class "table-header-wrap" :style {:width "12%"}} "Nimi"]
             [ui/table-header-column {:class "table-header-wrap" :style {:width "8%"}} "Aloitus (arkisin)"]
             [ui/table-header-column {:class "table-header-wrap" :style {:width "8%"}} "Aloitus (öisin)"]
             [ui/table-header-column {:class "table-header-wrap" :style {:width "8%"}} "Aloitus (vklp.)"]
             [ui/table-header-column {:class "table-header-wrap" :style {:width "10%"}} "Hinta/min"]
             [ui/table-header-column {:class "table-header-wrap" :style {:width "10%"}} "Hinta/km"]
             [ui/table-header-column {:class "table-header-wrap" :style {:width "10%"}} "Porrasveto"]
             [ui/table-header-column {:class "table-header-wrap" :style {:width "10%"}} "Paariasennus"]
             [ui/table-header-column {:class "table-header-wrap" :style {:width "10%"}} "Avustuksen kertalisä"]
             [ui/table-header-column {:class "table-header-wrap" :style {:width "10%"}} "Toiminta-alueet"]
             [ui/table-header-column {:class "table-header-wrap" :style {:width "10%"}} "Lisätty"]]]
           [ui/table-body {:deselect-on-clickaway false}
            (doall
              (for [{:keys [id service-id name start-price-daytime start-price-nighttime start-price-weekend price-per-minute price-per-kilometer timestamp accessibility-service-stairs accessibility-service-stretchers accessibility-service-fare operating-areas] :as result} services]
                ^{:key (str "prices_" service-id)}
                [ui/table-row {:selectable true}
                 [ui/table-row-column (merge (stylefy/use-style style-base/table-col-style-wrap) {:width "12%"})
                  [:a {:href     (str "/edit-service/" service-id)
                       :on-click #(do
                                    (.preventDefault %)
                                    (e! (admin-validation/->EditService service-id)))} name]]
                 [ui/table-row-column (merge (stylefy/use-style style-base/table-col-style-wrap) {:width "8%"})
                  (formatters/currency start-price-daytime)]
                 [ui/table-row-column (merge (stylefy/use-style style-base/table-col-style-wrap) {:width "8%"})
                  (formatters/currency start-price-nighttime)]
                 [ui/table-row-column (merge (stylefy/use-style style-base/table-col-style-wrap) {:width "8%"})
                  (formatters/currency start-price-weekend)]
                 [ui/table-row-column (merge (stylefy/use-style style-base/table-col-style-wrap) {:width "10%"})
                  (formatters/currency price-per-minute)]
                 [ui/table-row-column (merge (stylefy/use-style style-base/table-col-style-wrap) {:width "10%"})
                  (formatters/currency price-per-kilometer)]
                 [ui/table-row-column (merge (stylefy/use-style style-base/table-col-style-wrap) {:width "10%"})
                  (formatters/currency accessibility-service-stairs)]
                 [ui/table-row-column (merge (stylefy/use-style style-base/table-col-style-wrap) {:width "10%"})
                  (formatters/currency accessibility-service-stretchers)]
                 [ui/table-row-column (merge (stylefy/use-style style-base/table-col-style-wrap) {:width "10%"})
                  (formatters/currency accessibility-service-fare)]
                 [ui/table-row-column (merge (stylefy/use-style style-base/table-col-style-wrap) {:width "10%"})
                  (doall
                    (for [area operating-areas]
                      ^{:key area}
                      [:div (str area)]))]
                 [ui/table-row-column (merge (stylefy/use-style style-base/table-col-style-wrap) {:width "10%"})
                  (time/format-timestamp-for-ui timestamp)]
                 ]))]]])])))
