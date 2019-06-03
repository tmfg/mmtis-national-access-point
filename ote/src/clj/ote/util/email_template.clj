(ns ote.util.email-template
  (:require [chime :refer [chime-at]]
            [jeesql.core :refer [defqueries]]
            [clj-time.periodic :refer [periodic-seq]]
            [clojure.string :as str]
            [hiccup.core :refer [html]]
            [hiccup.util :refer [escape-html]]
            [ote.db.transit :as transit]
            [ote.db.tx :as tx]
            [ote.db.lock :as lock]
            [ote.localization :refer [tr] :as localization]
            [ote.time :as time]
            [ote.nap.users :as nap-users]
            [ote.tasks.util :refer [daily-at]]
            [ote.util.db :as db-util]
            [ote.email :as email]
            [ote.util.throttle :refer [with-throttle-ms]]
            [ote.environment :as environment]
            [clojure.string :as string]
            [specql.core :as specql]
            [specql.op :as op])
  (:import (org.joda.time DateTimeZone)))

(def html-header
  "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">")

(defn- html-table [headers rows]
  [:table {:class "tg" :cellpadding "0" :cellspacing "0"}
   [:thead
    [:tr
     (for [{width :width class :class label :label} headers]
       [:th {:class class
             :style (str "width: " width)}
        label])]]
   [:tbody
    (map-indexed (fn [index row]
                   [:tr {:class (if (even? index) "even-row" "odd-row")}
                    (for [cell row]
                      [:td cell])])
                 rows)]])

(defn- blue-button [link text]
  [:table {:style "background-color: #fff;" :cellpadding "16"}
   [:tr
    [:td {:align "center"
          :valign "middle"
          :class "mcnButtonContent"
          :style "font-family: Roboto, &quot;Helvetica Neue&quot;, Helvetica, Arial, sans-serif; font-size: 16px; padding: 16px;"}
     [:a.mcnButton {:title text
                    :href link
                    :target "_blank"
                    :style "font-family:Roboto,helvetica neue,arial,sans-serif;
                            font-size: 16px;
                            font-weight: normal;
                            letter-spacing: normal;
                            line-height: 25px;
                            text-align: center;
                            text-decoration: none;
                            display: inline-block;
                            color: #FFFFFF !important;"}
      text]]]])

(defn- html-divider-border [_]
  [:div
   [:table {:border "0", :width "80%", :cellpadding "0", :cellspacing "0"}
    [:tbody
     [:tr
      [:td {:style "background:none; border-bottom: 1px solid #d7dfe3; height:1px; width:100%; margin:0px 0px 0px 0px;"} "&nbsp;"]]]]
   [:br]])

(defn- sort-by-first-different-date [element]
  "Works with vector which is formatted like this:
  [<id> {:foo value :bar value :different-week-date 2019-02-01}
   <id> {:foo value :bar value :different-week-date 2019-03-01}]"
  (:different-week-date (first (second element))))

(defn- detected-change-row [unsent-changes]
  (let [grouped-changes (group-by :transport-service-id unsent-changes)
        sorted-changes (sort-by #(sort-by-first-different-date %) grouped-changes)]
    (for [grouped-change sorted-changes]
      (let [change-list (second grouped-change)
            transport-service-id (:transport-service-id (first change-list))
            operator-name (:operator-name (first change-list))
            service-name (:service-name (first change-list))
            date (:date (first change-list))
            regions (:regions (first change-list))
            days-until-change (:days-until-change (first change-list))
            different-week-date (:different-week-date (first (sort-by :different-week-date change-list)))
            added-routes (count (filter #(= "added" (:change-type %)) change-list))
            removed-routes (count (filter #(= "removed" (:change-type %)) change-list))
            changed-routes (filter #(= "changed" (:change-type %)) change-list)
            grouped-changed-routes (group-by :route-hash-id changed-routes)
            changed-routes-count (count grouped-changed-routes)
            no-traffic-routes (count (filter #(= "no-traffic" (:change-type %)) change-list))]

        [operator-name
         (str "<a href=\"" (environment/base-url) "#/transit-visualization/"
              transport-service-id "/" date "/new\">" (escape-html service-name) "</a>")
         (str/join ", " (db-util/PgArray->vec regions))
         (str days-until-change " pv (" (time/format-date different-week-date) ")")
         (str/join ", "
                   (remove nil?
                           [(when (and added-routes (> added-routes 0))
                              (str added-routes " uutta reittiä"))
                            (when (and removed-routes (> removed-routes 0))
                              (str removed-routes " päättyvää reittiä"))
                            (when (and changed-routes (> changed-routes-count 0))
                              (str changed-routes-count " muuttuvaa reittiä"))
                            (when (and no-traffic-routes (> no-traffic-routes 0))
                              (str no-traffic-routes " reitillä tauko liikennöinnissä"))]))]))))

(defn- pre-notice-row [{:keys [id regions operator-name pre-notice-type route-description
                               first-effective-date description]}]
  [[:a {:href (str (environment/base-url) "#/authority-pre-notices/" id)} (escape-html route-description)]
   (escape-html operator-name)
   (str/join ",<br />" (db-util/PgArray->seqable regions))
   (str/join ",<br />" (mapv #(tr [:enums ::transit/pre-notice-type (keyword %)])
                             (db-util/PgArray->seqable pre-notice-type)))
   first-effective-date
   (escape-html description)])

(defn html-template [title body]
  [:html {:xmlns "http://www.w3.org/1999/xhtml"
          :xmlns:v "urn:schemas-microsoft-com:vml"
          :xmlns:o "urn:schemas-microsoft-com:office:office"}
   [:head "<!-- NAME: 1 COLUMN - FULL WIDTH -->" "<!--
                    [if gte mso 9]>
                    <xml><o:OfficeDocumentSettings><o:AllowPNG/><o:PixelsPerInch>96</o:PixelsPerInch></o:OfficeDocumentSettings></xml>
                    <![endif]-->"
    [:meta {:http-equiv "Content-Type", :content "text/html; charset=utf-8"}]
    [:meta {:http-equiv "X-UA-Compatible", :content "IE=edge"}]
    [:meta {:name "viewport", :content "width=device-width, initial-scale=1.0"}]
    [:meta {:name "format-detection", :content "telephone=no"}]
    [:title title]
    "<!--[if !mso]><!-->
    <link href=\"https://fonts.googleapis.com/css?family=Roboto:300,400,600,700,800\" rel=\"stylesheet\" />
    <!--<![endif]-->"
    [:style {:type "text/css"}
     "body { margin: 0; padding: 0; -webkit-text-size-adjust: 100% !important; -ms-text-size-adjust: 100% !important; -webkit-font-smoothing: antialiased !important;font-family:Roboto,helvetica neue,arial,sans-serif;}
     img { border: 0 !important; outline: none !important;}
     p { margin: 0px; }
     table { border-collapse: collapse; mso-table-lspace: 0px; mso-table-rspace: 0px;}
     td, a, span { border-collapse: collapse; mso-line-height-rule: exactly;}
     .headerText1 {font-family:Roboto,helvetica neue,arial,sans-serif; font-size:2rem; font-weight:700;}
     .headerText2 {font-family:Roboto,helvetica neue,arial,sans-serif; font-size:1.5rem; font-weight:700;}
     .whiteBackground {background-color:#FFFFFF}
     .grayBackground {background-color:#EFEFEF}
     .mcnButtonContent {background-color:#0066CC;padding:15px;}
     .footer {font-family:Roboto,helvetica neue,arial,sans-serif;font-size:0.75rem;}
     a.mcnButton{font-family:Roboto,helvetica neue,arial,sans-serif;font-size: 16px;font-weight: normal;letter-spacing: normal;line-height: 25px;text-align: center;text-decoration: none; display: inline-block; color: #FFF !important;}
     .even-row {background-color:#EFEFEF;}
     .odd-row {background-color:#FFFFFF;}
     .tg  {border-collapse:collapse;border-spacing:0;}
     .tg td{font-family:Roboto,helvetica neue,arial,sans-serif;font-size:14px;padding:10px 5px;overflow:hidden;word-break:normal;}
     .tg th{font-family:Roboto,helvetica neue,arial,sans-serif;font-size:16px;font-weight:700;padding:10px 5px;overflow:hidden;word-break:normal;}
     .tg .tg-oe15{background-color:#ffffff;text-align:left;vertical-align:top}
     .tg .tg-lusz{background-color:#656565;color:#ffffff;text-align:left;vertical-align:top}
     .tg .tg-vnjh{text-decoration:underline;background-color:#ffffff;color:#0066cc;text-align:left;vertical-align:top}
     .tg .tg-m03x{text-decoration:underline;background-color:#efefef;color:#0066cc;text-align:left;vertical-align:top}
     .tg .tg-fkgn{background-color:#efefef;border-color:#efefef;text-align:left;vertical-align:top}
     .spacing-left-right{padding-left:0;padding-right:0;}
     @media screen and (min-width:699px) {
      .headerText1 {font-size:2rem !important;}
      .headerText2 {font-size:1.5rem !important;}
      .spacing-left-right{padding-left:20px;padding-right:20px;}
     }"]]
   [:body
    [:center
     [:div.whiteBackground.spacing-left-right
      [:a {:href (str (environment/base-url))}
       [:img {:src (str (environment/base-url) "img/icons/NAP-logo-blue.png")
              :widht "150" :height "100" :title "NAP Logo" :alt "NAP Logo"}]]

      body]

     [:div.grayBackground.footer
      [:br]
      [:br]
      [:p {:style "font-family:Roboto,helvetica neue,arial,sans-serif;font-size:0.75rem;"}
       "Tämän viestin lähetti NAP."]
      [:br]
      [:span [:strong {:style "font-family:Roboto,helvetica neue,arial,sans-serif;font-size:0.75rem;"}
              "NAP-yhteystiedot:"]]
      [:p
       [:a {:style "font-family:Roboto,helvetica neue,arial,sans-serif;font-size:0.75rem;"
            :href "mailto:nap@traficom.fi"} "nap@traficom.fi"]
       [:span {:style "font-family:Roboto,helvetica neue,arial,sans-serif;font-size:0.75rem;"}
        " tai 029 534 5454 (arkisin 09-15)"]]
      [:br]
      [:p {:style "font-family:Roboto,helvetica neue,arial,sans-serif;font-size:0.75rem;"}
       "Haluatko muuttaa sähköpostiasetuksiasi?"
       [:br]
       [:a
        {:style "font-family:Roboto,helvetica neue,arial,sans-serif;font-size:0.75rem;"
         :href (str (environment/base-url) "#/email-settings") :target "_blank"} "Avaa NAPin sähköposti-ilmoitusten asetukset -sivu"]]
      [:br]]]]])

(defn notification-html [pre-notices detected-changes title]
  (html-template title
                 [:div
                  [:br]
                  [:h1 {:class "headerText1"
                        :style "font-family:Roboto,helvetica neue,arial,sans-serif; font-size:1.5rem; font-weight:700;"}
                   "NAP:ssa on uutta tietoa markkinaehtoisen liikenteen tulevista muutoksista."]

                  (when (seq pre-notices)
                    [:div {:style "background-color:#FFFFFF"}
                     (html-divider-border nil)
                     [:p {:style "margin-bottom:  20px;"}
                      [:h2 {:class "headerText2"
                            :style "font-family:Roboto,helvetica neue,arial,sans-serif; font-size:1.2rem; font-weight:700;margin:0;"}
                       "Liikennöitsijöiden lähettämät lomakeilmoitukset"]]

                     (html-table
                       [{:class "tg-lusz" :width "10%" :label "Reitin nimi"}
                        {:class "tg-lusz" :width "10%" :label "Palveluntuottajan nimi"}
                        {:class "tg-lusz" :width "20%" :label "Alue"}
                        {:class "tg-lusz" :width "15%" :label "Muutoksen tyyppi"}
                        {:class "tg-lusz" :width "15%" :label "Muutoksen ensimmäinen voimaantulopäivä"}
                        {:class "tg-lusz" :width "30%" :label "Lisätiedot muutoksesta"}]
                       (for [n pre-notices]
                         (pre-notice-row n)))
                     [:br]
                     (blue-button (str (environment/base-url) "#/authority-pre-notices") "Siirry NAP:iin tarkastelemaan lomakeilmoituksia")])

                  (when (seq detected-changes)
                    [:div {:style "background-color:#FFFFFF"}
                     (html-divider-border nil)
                     [:p
                      [:h2 {:class "headerText2"
                            :style "font-family:Roboto,helvetica neue,arial,sans-serif; font-size:1.2rem; font-weight:700;margin:0;"}
                       "Rajapinnoista tunnistetut muutokset"]
                      [:h2
                       {:class "headerText2"
                        :style "font-family:Roboto,helvetica neue,arial,sans-serif; font-size:1.2rem; font-weight:700;margin-top:0; margin-bottom:  20px;"}
                       "Tunnistusajankohta " (time/format-date (time/now))]
                      (html-table
                        [{:class "tg-lusz" :width "20%" :label "Palveluntuottaja"}
                         {:class "tg-lusz" :width "15%" :label "Palvelu"}
                         {:class "tg-lusz" :width "25%" :label "Alue"}
                         {:class "tg-lusz" :width "15%" :label "Aikaa 1. muutokseen"}
                         {:class "tg-lusz" :width "25%" :label "Muutokset"}]
                        (detected-change-row detected-changes))
                      [:br]]
                     (blue-button (str (environment/base-url) "#/transit-changes") "Siirry NAP:iin tarkastelemaan tunnistettuja muutoksia")])
                  (html-divider-border nil)]))

(defn notify-user-new-member [new-member requester operator title]
  (html-template title
                 [:div
                  [:br]
                  [:h1 {:class "headerText1"
                        :style "font-family:Roboto,helvetica neue,arial,sans-serif; font-size:1.5rem; font-weight:700;"}
                   (str "Sinut on kutsuttu " (::t-operator/name operator) "-nimisen palveluntuottajan jäseneksi.")]

                  (html-divider-border nil)
                  [:p
                   [:strong {:style "font-family:Roboto,helvetica neue,arial,sans-serif;font-size:0.75rem;"} (:user_email requester)]
                   "on kutsunut sinut NAP-palveluun "
                   [:strong (::t-operator/name operator)]
                   (str " -nimisen palveluntuottajan jäseneksi. Voit nyt muokata " (::t-operator/name operator) " -nimisen palvelutuottajan ja sen alla julkaistujen palveluiden tietoja.")
                   ]
                  [:p "Mikäli olet saanut kutsun vahingossa, tai et halua olla palveluntuottajan jäsen, sinua ei tarvitse tehdä mitään."]
                  [:br]
                  (blue-button (str (environment/base-url) "#/") "Siirry NAP-palveluun")

                  (html-divider-border nil)]))