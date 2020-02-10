(ns ote.views.admin.reports
  "Admin panel views. Note this has a limited set of users and is not
  currently localized, all UI text is in Finnish."
  (:require [stylefy.core :as stylefy]
            [ote.ui.common :refer [linkify]]
            [ote.style.base :as style-base]))

(defn reports [e!]
  [:div
   [:h2 "Palveluntuottajaraportit"]
   [:div (stylefy/use-style (style-base/flex-container "column"))
    [linkify "/admin/reports/transport-operator/all-emails" "Käytössä olevien käyttäjien ja palveluiden sähköpostit"]
    [linkify "/admin/reports/transport-operator/no-services" "Ei palveluita"]
    [linkify "/admin/reports/transport-operator/unpublished-services" "Julkaisemattomia palveluita"]
    [linkify "/admin/reports/transport-operator/brokerage" "Välityspalvelut"]
    [linkify "/admin/reports/transport-operator/taxi-operators" "Taksipalveluita tuottavat yritykset ja aliyritykset"]
    [linkify "/admin/reports/transport-operator/request-operators" "Tilausliikennettä tuottavat yritykset ja aliyritykset"]
    [linkify "/admin/reports/transport-operator/schedule-operators" "Säännöllistä aikataulun mukaista liikennettä tuottavat yritykset ja aliyritykset"]
    [linkify "/admin/reports/transport-operator/payment-interfaces" "Lippu- ja Maksujärjestelmän avanneet yritykset"]
    [linkify "/admin/reports/port" "Satama-aineisto csv"]
    [linkify "/admin/reports/tvv" "Toimivaltaiset viranomaiset"]
    [linkify "/admin/reports/netex-interfaces" "Gtfs/Kalkati.net rajapinnat, jotka on käännetty Netex muotoon"]
    [linkify "/admin/reports/associated-companies" "Liittyneet yritykset"]]])

