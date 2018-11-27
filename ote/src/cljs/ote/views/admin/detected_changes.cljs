(ns ote.views.admin.detected-changes
  "Helper methods to help test and configure automatic traffic changes detection"
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
            [ote.style.base :as style-base]
            [ote.style.admin :as style-admin]
            [cljs-time.core :as t]
            [ote.ui.form :as form]))

(defn configure-detected-changes [e! app-state]
  [:div
   [:h2 "Käytössä olevat toiminnot"]
   [:div (stylefy/use-style (style-base/flex-container "column"))
    [ui/raised-button {:id       "force-detect-transit-changes"
                       :label    "Pakota kaikkien muutosten tunnistus"
                       :on-click #(do
                                    (.preventDefault %)
                                    (e! (admin-controller/->ForceDetectTransitChanges)))
                       :primary  true
                       :icon     (ic/content-filter-list)}]
    [:br]
    [ui/raised-button {:id       "force-import"
                       :label    "Pakota yhden lataamattoman pakettin lataus ulkoisesta osoitteesta"
                       :on-click #(do
                                    (.preventDefault %)
                                    (e! (admin-controller/->ForceInterfaceImport)))
                       :primary  true
                       :icon     (ic/content-filter-list)}]]
   [:div
    [:h4 "Laske valitulle palvelulle päivähäshit tauluun gtfs-date-hash"]
    [form/form
     {:update!   #(e! (admin-controller/->UpdateHashCalculationValues %))
      :footer-fn (fn [data]
                   [:span
                    [ui/raised-button {:primary  true
                                       :on-click #(e! (admin-controller/->ForceHashCalculationForService))
                                       :label    "Laske päivä hashit"}]])}
     [(form/group
        {:label   "Päivä hashien uudelleen laskenta"
         :columns 3
         :layout  :raw
         :card?   false}
        {:name      :service-id
         :type      :string
         :label     "Palvelun id"
         :hint-text "Palvelun id"
         :required? true}
        {:name      :package-count
         :type      :string
         :label     "Pakettien määrä"
         :hint-text "5"
         :required? true})]
     (get-in app-state [:admin :transit-changes :daily-hash-values])]]

   [:div
    [:h4 "Laske valitulle palvelulle route-hash-id tauluun detection-routes"]
    [form/form
     {:update!   #(e! (admin-controller/->UpdateRouteHashCalculationValues %))
      :footer-fn (fn [data]
                   [:span
                    [ui/raised-button {:primary  true
                                       :on-click #(e! (admin-controller/->ForceRouteHashCalculationForService))
                                       :label    "Laske route hash id:t"}]])}
     [(form/group
        {:label   "Route hash id:n hashien uudelleen laskenta"
         :columns 3
         :layout  :raw
         :card?   false}
        {:name      :service-id
         :type      :string
         :label     "Palvelun id"
         :hint-text "Palvelun id"
         :required? true}
        {:name      :package-count
         :type      :string
         :label     "Pakettien määrä"
         :hint-text "5"
         :required? true}
        {:name        :route-id-type
         :type        :selection
         :options     ["short-long" "short-long-headsign" "route-id"]
         :show-option (fn [x] x)
         :required?   true})]
     (get-in app-state [:admin :transit-changes :route-hash-values])]]

   [:div
    [linkify "/transit-changes/force-calculate-route-hash-id/2/100/short-long" "Laske route hash id palvelulle 2 short-long tunnistuksella"]
    [:div "Tyypit: " "short-long" "short-long-headsign"]]])

