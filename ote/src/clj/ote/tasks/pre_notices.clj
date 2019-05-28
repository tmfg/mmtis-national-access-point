(ns ote.tasks.pre-notices
  (:require [chime :refer [chime-at]]
            [jeesql.core :refer [defqueries]]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as log]
            [clj-time.core :as t]
            [clj-time.format :as format]
            [clj-time.coerce :as coerce]
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

(defqueries "ote/tasks/pre_notices.sql")

(defonce timezone (DateTimeZone/forID "Europe/Helsinki"))

(defn datetime-string [dt timezone]
  (when dt
    (format/unparse (format/with-zone (format/formatter "dd.MM.yyyy HH:mm") timezone) dt)))

(defn pre-notice-row [{:keys [id regions operator-name pre-notice-type route-description
                              first-effective-date description]}]
  [[:a {:href (str (environment/base-url) "#/authority-pre-notices/" id)} (escape-html route-description)]
   (escape-html operator-name)
   (str/join ",<br />" (db-util/PgArray->seqable regions))
   (str/join ",<br />" (mapv #(tr [:enums ::transit/pre-notice-type (keyword %)])
                             (db-util/PgArray->seqable pre-notice-type)))
   first-effective-date
   (escape-html description)])

(defn- table [headers rows]
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

(defn- sort-by-first-different-date [element]
  "Works with vector which is formatted like this:
  [<id> {:foo value :bar value :different-week-date 2019-02-01}
   <id> {:foo value :bar value :different-week-date 2019-03-01}]"
  (:different-week-date (first (second element))))

(defn detected-change-row [unsent-changes]
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
            different-week-date (:different-week-date (first change-list))
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
         (str days-until-change " pv (" different-week-date ")")
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

(defn html-divider-border [_]
  [:div
   [:table {:border "0", :width "80%", :cellpadding "0", :cellspacing "0"}
    [:tbody
     [:tr
      [:td {:style "background:none; border-bottom: 1px solid #d7dfe3; height:1px; width:100%; margin:0px 0px 0px 0px;"} "&nbsp;"]]]]
   [:br]])

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

(defn notification-html [pre-notices detected-changes]
  (println "pre-notices" (pr-str pre-notices))
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
    [:title "NAP:ssa on uutta tietoa markkinaehtoisen liikenteen tulevista muutoksista."]
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

         (table
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
          (table
            [{:class "tg-lusz" :width "20%" :label "Palveluntuottaja"}
             {:class "tg-lusz" :width "15%" :label "Palvelu"}
             {:class "tg-lusz" :width "25%" :label "Alue"}
             {:class "tg-lusz" :width "15%" :label "Aikaa 1. muutokseen"}
             {:class "tg-lusz" :width "25%" :label "Muutokset"}]
            (detected-change-row detected-changes))
          [:br]]
         (blue-button (str (environment/base-url) "#/transit-changes") "Siirry NAP:iin tarkastelemaan tunnistettuja muutoksia")])
      (html-divider-border nil)]

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

(defn user-notification-html
  "Every user can have their own set of notifications. Return notification html based on regions."
  [db user detected-changes-recipients]
  (try
    (let [notices (fetch-pre-notices-by-interval-and-regions db {:interval "1 day" :regions (:finnish-regions user)})
          detected-changes (when (detected-changes-recipients (:email user))
                             (fetch-unsent-changes-by-regions db {:regions (:finnish-regions user)}))]

      (if (or (seq notices) (seq detected-changes))
        (do
          (log/info "For user " (:email user) " we found "
                    (count notices) " new pre-notices and "
                    (count detected-changes) " new detected changes "
                    " from 24 hours for regions " (:finnish-regions user))

          ;; Add doctype which can't be addid using hiccup template
          (str "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
               (html (notification-html notices detected-changes))))
        (log/info "No new pre-notices or detected changes found.")))

    (catch Exception e
      (log/warn "Error while generating notification html for regions: " (:finnish-regions user) " ERROR: " e))))

(defn send-notification! [db email detected-changes-recipients]
  (log/info "Starting pre-notices notification task...")

  (lock/with-exclusive-lock
    db "pre-notice-email" 300
    (localization/with-language
      "fi"
      (tx/with-transaction db
                           (let [authority-users (nap-users/list-authority-users db) ;; Authority users
                                 unsent-detected-changes (fetch-unsent-changes-by-regions db {:regions nil})]
                             (log/info "Authority users: " (pr-str (map :email authority-users)))
                             (doseq [u authority-users]
                               (let [notification (user-notification-html db u detected-changes-recipients)]
                                 (if notification
                                   (do
                                     (log/info "Trying to send a pre-notice email to: " (pr-str (:email u)))
                                     ;; SES have limit of 14/email per second. We can send multiple emails from prod and dev at the
                                     ;; same time. Using sleep, we can't exceed that limit.
                                     (with-throttle-ms 200
                                                       (try
                                                         (email/send!
                                                           email
                                                           {:to (:email u)
                                                            :subject (str "Uudet 60 päivän muutosilmoitukset NAP:ssa "
                                                                          (datetime-string (t/now) timezone))
                                                            :body [{:type "text/html;charset=utf-8" :content notification}]})
                                                         (catch Exception e
                                                           (log/warn "Error while sending a notification" e)))))
                                   (log/info "Could not find notification for user with email: " (pr-str (:email u))))))

                             ;; Mark changes in detected-change-history as sent
                             (specql/update! db :gtfs/detected-change-history
                                             {:gtfs/email-sent (java.util.Date.)}
                                             {:gtfs/id (op/in (into #{} (map :history-id unsent-detected-changes)))}))))))


(defrecord PreNoticesTasks [detected-changes-recipients]
  component/Lifecycle
  (start [{db :db email :email :as this}]
    (assoc this
      ::stop-tasks [(chime-at (daily-at 8 15)
                              (fn [_]
                                (#'send-notification! db email detected-changes-recipients)))]))
  (stop [{stop-tasks ::stop-tasks :as this}]
    (doseq [stop stop-tasks]
      (stop))
    (dissoc this ::stop-tasks)))

(defn pre-notices-tasks [config]
  (let [detected-changes-recipients (or (some-> config :detected-changes-recipients
                                                (string/split #",")
                                                set)
                                        identity)]
    (->PreNoticesTasks detected-changes-recipients)))
