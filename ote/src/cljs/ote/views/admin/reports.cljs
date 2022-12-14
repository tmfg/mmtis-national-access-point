(ns ote.views.admin.reports
  "Admin panel views. Note this has a limited set of users and is not
  currently localized, all UI text is in Finnish."
  (:require [stylefy.core :as stylefy]
            [ote.ui.common :refer [linkify]]
            [ote.style.base :as style-base]))

(defn reports
  [e!]
  [:div
   [:h2 "Palveluntuottajaraportit"]
   (into [:div (stylefy/use-style (style-base/flex-container "column"))]
    (mapv
      (fn [[link label]] [linkify link label {:analytics-tag "palveluntuottajaraportit"}])
      [["/admin/reports/transport-operator/all-emails" "Käytössä olevien käyttäjien ja palveluiden sähköpostit"]
       ["/admin/reports/transport-operator/no-services" "Ei palveluita"]
       ["/admin/reports/transport-operator/unpublished-services" "Julkaisemattomia palveluita"]
       ["/admin/reports/transport-operator/brokerage" "Välityspalvelut"]
       ["/admin/reports/transport-operator/taxi-operators" "Taksipalveluita tuottavat yritykset ja aliyritykset"]
       ["/admin/reports/transport-operator/request-operators" "Tilausliikennettä tuottavat yritykset ja aliyritykset"]
       ["/admin/reports/transport-operator/schedule-operators" "Säännöllistä aikataulun mukaista liikennettä tuottavat yritykset ja aliyritykset"]
       ["/admin/reports/transport-operator/payment-interfaces" "Lippu- ja Maksujärjestelmän avanneet yritykset"]
       ["/admin/reports/port" "Satama-aineisto csv"]
       ["/admin/reports/tvv" "Toimivaltaiset viranomaiset"]
       ["/admin/reports/netex-interfaces" "Gtfs/Kalkati.net rajapinnat, jotka on käännetty Netex muotoon"]
       ["/admin/reports/netex-interfaces-with-max-date" "Gtfs/Kalkati.net rajapinnat ja ajopäivät, jotka on käännetty Netex muotoon"]
       ["/admin/reports/associated-companies" "Liittyneet yritykset"]
       ["/admin/reports/reported-taxi-prices" "Taksiyritysten ilmoittamat hinnat"]]))])

