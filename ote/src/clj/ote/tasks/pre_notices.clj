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
            [ote.email :refer [send-email]]
            [ote.db.tx :as tx]
            [ote.db.lock :as lock]
            [ote.localization :refer [tr] :as localization]
            [ote.time :as time]
            [ote.nap.users :as nap-users]
            [ote.tasks.util :refer [daily-at]]
            [ote.util.db :as db-util])
  (:import (org.joda.time DateTimeZone)))

(defqueries "ote/tasks/pre_notices.sql")

(defonce timezone (DateTimeZone/forID "Europe/Helsinki"))

(defn datetime-string [dt timezone]
  (when dt
    (format/unparse (format/with-zone (format/formatter "dd.MM.yyyy HH:mm") timezone) dt)))

(defn date-string [dt timezone]
  (when dt
    (time/format-date (t/to-time-zone dt timezone))))

(defn hours->seconds [hours]
  (int (* 3600 hours)))

(defn pre-notice-row [{:keys [id regions operator-name pre-notice-type route-description
                              effective-dates-asc description]}]
  (let [effective-date-str (date-string (coerce/from-sql-date
                                          (first (db-util/PgArray->seqable effective-dates-asc)))
                                        timezone)]
    [:tr
     [:td [:b id]]
     [:td [:a {:href (str "https://finap.fi/#/authority-pre-notices/" id)} (escape-html route-description)]]
     [:td (str/join ",<br />" (db-util/PgArray->seqable regions))]
     [:td (escape-html operator-name)]
     [:td (str/join ",<br />" (mapv #(tr [:enums ::transit/pre-notice-type (keyword %)])
                               (db-util/PgArray->seqable pre-notice-type)))]
     [:td effective-date-str]
     [:td (escape-html description)]]))

(defn notification-template [pre-notices]
  [:html
   [:head
    [:style
     "table { border-collapse: collapse; font-size: 10px; table-layout: fixed;}
      table,td,tr,th { border: 1px solid black; }
      td,tr,th { padding: 5px; }"]]
   [:body
    [:p [:b "Uudet 60 päivän muutosilmoitukset NAP:ssa"]]
    [:ul
     [:li
      "Muutosilmoitukset on listattu voimaantulopäivämäärän mukaisesti."]
     [:li
      "Pääset tarkastelemaan muutosilmoitusta NAP:ssa klikkaamalla reitin nimeä taulukossa."]]
    [:table
     [:colgroup
      [:col {:style "width: 5%"}]
      [:col {:style "width: 10%"}]
      [:col {:style "width: 10%"}]
      [:col {:style "width: 10%"}]
      [:col {:style "width: 20%"}]
      [:col {:style "width: 15%"}]
      [:col {:style "width: 30%"}]]
     [:tbody
      [:tr
       [:th [:b "#"]]
       [:th [:b "Reitin nimi"]]
       [:th [:b "Alue"]]
       [:th [:b "Palveluntuottajan nimi"]]
       [:th [:b "Muutoksen tyyppi"]]
       [:th [:b "Muutoksen ensimmäinen voimaantulopäivä"]]
       [:th [:b "Muutoksen tarkemmat tiedot"]]]
      (doall
        (for [n pre-notices]
          (pre-notice-row n)))]]
    [:br]
    [:p "Tämän viestin lähetti NAP."]
    [:p "Ongelmia? Ota yhteys NAP-Helpdeskiin," [:br]
     [:a
      {:href "mailto:joukkoliikenne@liikennevirasto.fi"}
      "joukkoliikenne@liikennevirasto.fi"]
     [:span " tai 0295 34 3434 (arkisin 10-16)"]]]])


(defn user-notification-html
  "Every user can have their own set of notifications. Return notification html based on regions."
  [db regions]
  (try
    (when-let [notices (fetch-pre-notices-by-interval-and-regions db {:interval "1 day" :regions regions})]
      (if-not (empty? notices)
        (do
          (log/info "Found" (count notices) "new pre-notices from 24 hours for regions " regions)
          (html (notification-template notices)))
        (log/info "No new pre-notices found.")))

    (catch Exception e
      (log/warn "Error while generating notification html for regions: " regions " ERROR: " e ))))

(defn send-notification! [db {server-opts :server msg-opts :msg :as email-opts}]
  (log/info "Starting pre-notices notification task...")

  (lock/try-with-lock
    db "pre-notice-email" 300
    (localization/with-language
      "fi"
      (tx/with-transaction db
         (let [authority-users (nap-users/list-authority-users db)] ;; Authority users
           (try
             (doseq [u authority-users]
               (let [notification (user-notification-html db (:finnish-regions u))]
                 (if notification
                   (do
                     (log/info "Trying to send a pre-notice email to: " (pr-str (:email u)))
                     (send-email
                       server-opts
                       {:bcc     (:email u)
                        :from    (or (:from msg-opts) "NAP")
                        :subject (str "Uudet 60 päivän muutosilmoitukset NAP:ssa "
                                      (datetime-string (t/now) timezone))
                        :body    [{:type "text/html;charset=utf-8" :content notification}]}))
                   (log/info "Could not find notification for user with email: " (pr-str (:email u))))))

             ;; Sleep for 5 seconds to ensure that no other nodes are trying to send email at the same mail.
             (Thread/sleep 5000)
             (catch Exception e
               (log/warn "Error while sending a notification" e))))))))


(defrecord PreNoticesTasks [email-config]
  component/Lifecycle
  (start [{db :db :as this}]
    (assoc this
           ::stop-tasks [(chime-at (daily-at 8 15)
                                   (fn [_]
                                     (#'send-notification! db email-config)))]))
  (stop [{stop-tasks ::stop-tasks :as this}]
    (doseq [stop stop-tasks]
      (stop))
    (dissoc this ::stop-tasks)))

(defn pre-notices-tasks
  [email-config]
  (->PreNoticesTasks email-config))
