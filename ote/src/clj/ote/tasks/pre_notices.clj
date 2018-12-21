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
            [clojure.string :as string])
  (:import (org.joda.time DateTimeZone)))

(defqueries "ote/tasks/pre_notices.sql")

(defonce timezone (DateTimeZone/forID "Europe/Helsinki"))

(defn datetime-string [dt timezone]
  (when dt
    (format/unparse (format/with-zone (format/formatter "dd.MM.yyyy HH:mm") timezone) dt)))

(defn pre-notice-row [{:keys [id regions operator-name pre-notice-type route-description
                              first-effective-date description]}]
  [[:b id]
   [:a {:href (str (environment/base-url) "#/authority-pre-notices/" id)} (escape-html route-description)]
   (str/join ",<br />" (db-util/PgArray->seqable regions))
   (escape-html operator-name)
   (str/join ",<br />" (mapv #(tr [:enums ::transit/pre-notice-type (keyword %)])
                             (db-util/PgArray->seqable pre-notice-type)))
   first-effective-date
   (escape-html description)])

(defn- table [headers rows]
  [:table
   [:colgroup
    (for [{width :width} headers]
      [:col {:style (str "width: " width)}])]
   [:tbody
    [:tr
     (for [{label :label} headers]
       [:th [:b label]])]
    (for [row rows]
      [:tr
       (for [cell row]
         [:td cell])])]])

(defn detected-change-row [{:keys [service-name operator-name change-date days-until-change
                                   added-routes removed-routes changed-routes no-traffic-routes regions
                                   date transport-service-id]}]
  [operator-name
   (str "<a href=\"" (environment/base-url) "#/transit-visualization/"
        transport-service-id "/" date "\">" (escape-html service-name) "</a>")
   (str/join ", " (db-util/PgArray->vec regions))
   (str days-until-change " (" change-date ")")
   (str/join ", "
             (remove nil?
                     [(when (and added-routes (> added-routes 0))
                        (str added-routes " uutta reittiä"))
                      (when (and removed-routes (> removed-routes 0))
                        (str removed-routes " päättyvää reittiä"))
                      (when (and changed-routes (> changed-routes 0))
                        (str changed-routes " muuttunutta reittiä"))
                      (when (and no-traffic-routes (> no-traffic-routes 0))
                        (str no-traffic-routes " reitillä tauko liikennöinnissä"))]))])

(defn notification-template [pre-notices detected-changes]
  [:html
   [:head
    [:style
     "table { border-collapse: collapse; font-size: 10px; table-layout: fixed;}
      table,td,tr,th { border: 1px solid black; }
      td,tr,th { padding: 5px; }"]]
   [:body
    (when (seq pre-notices)
      [:span
       [:p [:b "Uudet 60 päivän muutosilmoitukset NAP:ssa"]]
       [:ul
        [:li
         "Muutosilmoitukset on listattu voimaantulopäivämäärän mukaisesti."]
        [:li
         "Pääset tarkastelemaan muutosilmoitusta NAP:ssa klikkaamalla reitin nimeä taulukossa."]]
       (table
        [{:width "5%" :label "#"}
         {:width "10%" :label "Reitin nimi"}
         {:width "10%" :label "Alue"}
         {:width "10%" :label "Palveluntuottajan nimi"}
         {:width "20%" :label "Muutoksen tyyppi"}
         {:width "15%" :label "Muutoksen ensimmäinen voimaantulopäivä"}
         {:width "30%" :label "Muutoksen tarkemmat tiedot"}]
        (for [n pre-notices]
          (pre-notice-row n)))
       [:br]])

    (when (seq detected-changes)
      [:span
       [:p [:b "Uudet tunnistetut liikennöintimuutokset NAP:ssa"]]
       (table
        [{:width "20%" :label "Palveluntuottaja"}
         {:width "20%" :label "Palvelu"}
         {:width "20%" :label "Alue"}
         {:width "20%" :label "Aikaa 1. muutokseen"}
         {:width "20%" :label "Muutokset"}]
        (for [chg  detected-changes]
          (detected-change-row chg)))
       [:br]])

    [:p "Tämän viestin lähetti NAP."]
    [:p "Ongelmia? Ota yhteys NAP-Helpdeskiin," [:br]
     ;; TODO: Trafi
     [:a {:href "mailto:joukkoliikenne@traficom.fi"} "joukkoliikenne@traficom.fi"]
     [:span " tai 029 534 5454 (arkisin 10-16)"]]]])


(defn user-notification-html
  "Every user can have their own set of notifications. Return notification html based on regions."
  [db user detected-changes-recipients]
  (try
    (let [notices (fetch-pre-notices-by-interval-and-regions db {:interval "1 day" :regions (:finnish-regions user)})
          detected-changes (when (detected-changes-recipients (:email user))
                             (fetch-current-detected-changes-by-regions db {:regions (:finnish-regions user)}))]
      (if (or (seq notices) (seq detected-changes))
        (do
          (log/info "For user " (:email user) " we found "
                    (count notices) " new pre-notices and "
                    (count detected-changes) " new detected changes "
                    " from 24 hours for regions " (:finnish-regions user))
          (html (notification-template notices detected-changes)))
        (log/info "No new pre-notices or detected changes found.")))

    (catch Exception e
      (log/warn "Error while generating notification html for regions: " (:finnish-regions user) " ERROR: " e ))))

(defn send-notification! [db email detected-changes-recipients]
  (log/info "Starting pre-notices notification task...")

  (lock/with-exclusive-lock
    db "pre-notice-email" 300
    (localization/with-language
      "fi"
      (tx/with-transaction db
        (let [authority-users (nap-users/list-authority-users db)] ;; Authority users
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
                       {:to      (:email u)
                        :subject (str "Uudet 60 päivän muutosilmoitukset NAP:ssa "
                                      (datetime-string (t/now) timezone))
                        :body    [{:type "text/html;charset=utf-8" :content notification}]})
                      (catch Exception e
                        (log/warn "Error while sending a notification" e)))))
                (log/info "Could not find notification for user with email: " (pr-str (:email u)))))))))))


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
