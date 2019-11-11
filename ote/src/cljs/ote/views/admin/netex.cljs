(ns ote.views.admin.netex
  "Netex conversion status."
  (:require [cljs-react-material-ui.reagent :as ui]
            [clojure.string :as str]
            [cljs-time.core :as t]
            [ote.time :as time]
            [ote.localization :refer [tr tr-key selected-language]]
            [ote.db.transport-service :as t-service]
            [ote.db.transport-operator :as t-operator]
            [ote.db.netex :as netex]
            [stylefy.core :as stylefy]
            [ote.ui.common :refer [linkify]]
            [ote.ui.form-fields :as form-fields]
            [ote.ui.circular_progress :as circular]
            [ote.style.base :as style-base]
            [ote.app.controller.admin :as admin-controller]
            [ote.theme.colors :as colors]))

(defn netex-page-controls [e! app]
  [:div.row {:style {:padding-top "20px"}}
   [form-fields/field {:update! #(e! (admin-controller/->UpdateNetexFilters %))
                       :on-enter #(e! (admin-controller/->SearchNetexConversions))
                       :name :operator-name
                       :label "Palveluntuottaja"
                       :type :string
                       :style {:margin-right "1rem"}
                       :hint-text "Palveluntuottajan nimi tai sen osa"
                       :container-class "col-xs-12 col-sm-4 col-md-4"}
    (get-in app [:admin :netex :filters])]

   [ui/raised-button {:primary true
                      :disabled (str/blank? filter)
                      :on-click #(e! (admin-controller/->SearchNetexConversions))
                      :label "Hae Netex konversiot"}]])

(defn netex [app]
  (let [{:keys [loading? results]}
        (get-in app [:admin :netex])]
    [:div.row {:style {:padding-top "40px"}}
     (when loading?
       [circular/circular-progress
        [:span "Ladataan konversioita..."]])

     (when results
       [:div
        [:div "Hakuehdoilla löytyi " (count results) " Netex konversiota."]
        [ui/table {:selectable false
                   :style style-base/basic-table}
         [ui/table-header {:class "table-header-wrap"
                           :adjust-for-checkbox false
                           :display-select-all false
                           :selectable false
                           :style {:border-bottom (str "1px solid" colors/gray650)}}
          [ui/table-row
           [ui/table-header-column
            {:class "table-header-wrap" :style {:width "25%"}}
            "Palveluntuottaja"]
           [ui/table-header-column
            {:class "table-header-wrap" :style {:width "25%"}}
            "Palvelu"]
           [ui/table-header-column
            {:class "table-header-wrap" :style {:width "20%"}}
            "NeTEx luotu"]
           [ui/table-header-column
            {:class "table-header-wrap" :style {:width "10%"}}
            "NeTEx status"]
           [ui/table-header-column
            {:class "table-header-wrap" :style {:width "20%"}}
            "NeTEx paketti"]]]
         [ui/table-body {:display-row-checkbox false}
          (doall
            (for [row results]
              ^{:key (str "link_" row)}
              [ui/table-row {:selectable false}
               [ui/table-row-column
                (merge (stylefy/use-style style-base/table-col-style-wrap)
                       {:width "25%"})
                (::t-operator/name row)]
               [ui/table-row-column
                (merge (stylefy/use-style style-base/table-col-style-wrap)
                       {:width "25%"})
                (::t-service/name row)]
               [ui/table-row-column
                (merge (stylefy/use-style style-base/table-col-style-wrap)
                       {:width "20%"})
                (time/format-timestamp-for-ui (::netex/modified row))]
               [ui/table-row-column
                (merge (stylefy/use-style style-base/table-col-style-wrap)
                       {:width "10%"})
                (::netex/status row)]
               [ui/table-row-column
                (merge (stylefy/use-style style-base/table-col-style-wrap)
                       {:width "20%"})
                [linkify (str (:url row) "?origin=ui") (:url row) {:target "_blank"}]]]))]]])]))
