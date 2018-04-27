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
            [ote.db.transit :as transit]
            [ote.email :refer [send-email]]
            [ote.db.tx :as tx]
            [ote.localization :refer [tr] :as localization]
            [ote.time :as time]
            [ote.nap.users :as nap-users])
  (:import (org.joda.time DateTimeZone)))

(defqueries "ote/tasks/pre_notices.sql")

(defonce timezone (DateTimeZone/forID "Europe/Helsinki"))
(defonce daily-notify-time (t/from-time-zone (t/today-at 8 15) timezone))

(defn PgArray->seqable [arr]
  (if arr
    (.getArray arr)
    []))

(defn datetime-string [dt timezone]
  (when dt
    (format/unparse (format/with-zone (format/formatter "dd.MM.yyyy HH:mm") timezone) dt)))

(defn date-string [dt timezone]
  (when dt
    (time/format-date (t/to-time-zone dt timezone))))

(defn hours->seconds [hours]
  (int (* 3600 hours)))

(defn pre-notice-row [{:keys [id regions operator-name pre-notice-type route-description
                              effective-dates-asc]}]
  (let [effective-date-str (date-string (coerce/from-sql-date
                                          (first (PgArray->seqable effective-dates-asc)))
                                        timezone)]
    [:tr
     [:td [:b id]]
     [:td (str/join ", " (PgArray->seqable regions))]
     [:td operator-name]
     [:td (str/join ", " (mapv #(tr [:enums ::transit/pre-notice-type (keyword %)])
                               (PgArray->seqable pre-notice-type)))]
     [:td effective-date-str]
     [:td [:a {:href (str "https://finap.fi/#/authority-pre-notices/" id)} route-description]]]))

(defn notification-template [pre-notices]
  [:html
   [:head
    [:style
     "table { border-collapse: collapse; font-size: 10px; }
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
     [:tbody
      [:tr
       [:th [:b "#"]]
       [:th [:b "Alue"]]
       [:th [:b "Palveluntuottajan nimi"]]
       [:th [:b "Muutoksen tyyppi"]]
       [:th [:b "Muutoksen ensimmäinen voimaantulopäivä"]]
       [:th [:b "Reitin nimi"]]]
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


(defn notification-html [db]
  (try
    (when-let [notices (fetch-pre-notices-by-interval db {:interval "1 day"})]
      (if-not (empty? notices)
        (do
          (log/info "Found" (count notices) "new pre-notices from 24 hours.")
          (html (notification-template notices)))
        (log/info "No new pre-notices found.")))

    (catch Exception e
      (log/warn "Error while generating notification html:" e))))

(defn send-notification! [db {server-opts :server msg-opts :msg :as email-opts}]
  (log/info "Starting pre-notices notification task...")

  (localization/with-language
    "fi"
    (tx/with-transaction db
      (tx/with-xact-advisory-lock db
        (let [users (nap-users/list-users db {:transit-authority? true :email nil :name nil})
              emails (mapv #(:email %) users)
              notification (notification-html db)]

          (try
            (when (and (not (empty? emails)) notification)
              (log/info "Trying to send a pre-notice email to: " (pr-str emails))

              (send-email
                server-opts
                {:bcc emails
                 :from (or (:from msg-opts) "NAP")
                 :subject (str "Uudet 60 päivän muutosilmoitukset NAP:ssa "
                               (datetime-string (t/now) timezone))
                 :body [{:type "text/html;charset=utf-8" :content notification}]}))

            ;; Sleep for 5 seconds to ensure that no other nodes are trying to send email at the same mail.
            (Thread/sleep 5000)
            (catch Exception e
              (log/warn "Error while sending a notification" e))))))))


(defrecord PreNoticesTasks [at email-config interval]
  component/Lifecycle
  (start [{db :db :as this}]
    (assoc this
      ::stop-tasks [(chime-at (drop 1 (periodic-seq at (t/seconds
                                                         (hours->seconds (or interval
                                                                             24)))))
                              (fn [_]
                                (#'send-notification! db email-config)))]))
  (stop [{stop-tasks ::stop-tasks :as this}]
    (doseq [stop stop-tasks]
      (stop))
    (dissoc this ::stop-tasks)))

(defn pre-notices-tasks
  ([email-config interval]
   (pre-notices-tasks daily-notify-time email-config interval))
  ([at email-config interval]
   (->PreNoticesTasks at email-config interval)))