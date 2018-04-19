(ns ote.tasks.pre-notices
  (:require [chime :refer [chime-at]]
            [jeesql.core :refer [defqueries]]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as log]
            [clj-time.core :as t]
            [clj-time.periodic :refer [periodic-seq]]
            [clojure.string :as str]
            [hiccup.core :refer [html]]
            [ote.db.transit :as transit]
            [ote.email :refer [send-email]]
            [ote.db.tx :as tx]
            [ote.localization :refer [tr]]
            [ote.localization :as localization])
  (:import (org.joda.time DateTimeZone)))

(defqueries "ote/nap/users.sql")
(defqueries "ote/tasks/pre_notices.sql")

(def daily-notify-time (t/from-time-zone (t/today-at 8 15)
                                         (DateTimeZone/forID "Europe/Helsinki")))

(defn PgArray->seqable [arr]
  (if arr
    (.getArray arr)
    []))

(defn pre-notice-row [{:keys [id regions operator-name pre-notice-type route-description created modified :as pre-notice]}]
  [:tr
   [:td [:b id]]
   [:td (str/join ", " (PgArray->seqable regions))]
   [:td operator-name]
   [:td (str/join ", " (mapv #(tr [:enums ::transit/pre-notice-type (keyword %)])
                             (PgArray->seqable pre-notice-type)))]
   [:td "Muutoksen ensimmäinen voimaantulopäivä??? (Tämä vielä epäselvä)"]
   [:td route-description]])

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


(defn notificiation-html [db]
  (try
    (when-let [notices (fetch-pre-notices-by-interval db {:interval "1 day"})]
      (html (notification-template notices)))
    (catch Exception e
      (log/warn "Error while generating notification html:" e))))

(defn send-notification! [db]
  (localization/with-language
    "fi"
    (tx/with-transaction
      db
      (let [users (list-users db {:transit-authority? true :email nil :name nil})
            emails (mapv #(:email %) users)
            notification (notificiation-html db)]
        (try
          #_(println "#### Trying to send mail to: " (pr-str emails))
          (send-email {:bcc emails
                       :from "NAP"
                       :subject "Uudet 60 päivän muutosilmoitukset NAP:ssa xx.xx.xxxx"
                       :body [{:type "text/html" :content notification}]})
          (catch Exception e
            (log/warn "Error while sending a notification" e)))))))


(defrecord PreNoticesTasks [at]
  component/Lifecycle
  (start [{db :db :as this}]
    (assoc this
      ::stop-tasks [(chime-at (drop 1 (periodic-seq at (t/seconds 60)))
                              (fn [_]
                                (#'send-notification! db)))]))
  (stop [{stop-tasks ::stop-tasks :as this}]
    (doseq [stop stop-tasks]
      (stop))
    (dissoc this ::stop-tasks)))

(defn pre-notices-tasks
  ([] (pre-notices-tasks daily-notify-time))
  ([at]
   (->PreNoticesTasks at)))