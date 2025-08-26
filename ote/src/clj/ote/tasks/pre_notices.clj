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
            [specql.op :as op]
            [ote.util.email-template :as email-template])
  (:import (org.joda.time DateTimeZone)))

(defqueries "ote/tasks/pre_notices.sql")

(declare fetch-pre-notices-by-interval-and-regions fetch-unsent-changes-by-regions)

(defn datetime-string [dt timezone]
  (when dt
    (format/unparse (format/with-zone (format/formatter "dd.MM.yyyy HH:mm") timezone) dt)))

(defn- log-java-time-objs []                                ; This shall be removed once implementation is verified
  (log/warn (str "log-different-date-formations: java.time.LocalDateTime/now = " (java.time.LocalDateTime/now)))
  (log/warn (str "log-different-date-formations: java.time.ZoneId/of \"Europe/Helsinki\" = " (java.time.ZoneId/of "Europe/Helsinki")))
  (log/warn (str "log-different-date-formations: java.time.ZonedDateTime/of = " (java.time.ZonedDateTime/of
                                                                                  (java.time.LocalDateTime/now)
                                                                                  (java.time.ZoneId/of "Europe/Helsinki"))))
  (log/warn (str "log-different-date-formations:  java format DateTimeFormatter = "
                 (.format
                   (java.time.format.DateTimeFormatter/ofPattern "dd.MM.yyyy HH:mm")
                   (java.time.ZonedDateTime/of
                     (java.time.LocalDateTime/now)
                     (java.time.ZoneId/of "Europe/Helsinki"))))))

(defn notification-html-subject []
  (str "Uudet 60 päivän muutosilmoitukset NAP:ssa " (datetime-string (t/now) (DateTimeZone/forID "Europe/Helsinki"))))

(defn user-notification-html
  "Creates a user-specific html email html based on user preference of regions.
  Returns a collection  {:email-to-send coll1 :history-ids coll2} where `:email-to-send` is the html email as string
  and `:history-id`s is a collection of change history ids included in the email"
  [db user detected-changes-recipients]
  (try
    (let [notices (fetch-pre-notices-by-interval-and-regions db {:interval "1 day" :regions (:finnish-regions user)})
          detected-changes (when (detected-changes-recipients (:email user))
                             (fetch-unsent-changes-by-regions db {:regions (:finnish-regions user)}))
          history-ids (set (map :history-id detected-changes))]

      (if (or (seq notices) (seq detected-changes))
        (do
          (log/info "For user: " (:email user) ", new pre-notices: " (count notices)
                    ",  new detected changes: " (count detected-changes)
                    ", for regions: " (:finnish-regions user))

          {:history-ids history-ids
           :email-to-send (str email-template/html-header
                               (html (email-template/notification-html notices detected-changes (notification-html-subject))))})
        {:history-ids history-ids}))                        ; Return empty set of ids because no notification for user

    (catch Exception e
      (log/warn "Error while generating notification html for regions: " (:finnish-regions user) " ERROR: " e))))

(defn compose-and-send-pre-notice-to-user! [db u email detected-changes-recipients]
  (let [res (user-notification-html db u detected-changes-recipients)
        notification (:email-to-send res)
        history-ids (:history-ids res)]
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
                               :subject (notification-html-subject)
                               :body [{:type "text/html;charset=utf-8" :content notification}]})
                            history-ids                     ; Return ids included in email when no exception
                            (catch Exception e
                              (log/error "Error while sending a notification" e)
                              nil))))
      (do
        (log/info "No notification for user with email: " (pr-str (:email u)))
        #{}))))                                             ; Return empty because no history ids sent in notification

(defn send-pre-notice-emails! [db email detected-changes-recipients]
  (log/info "Starting composing and sending of pre-notice emails...")
  (lock/with-exclusive-lock
    db "pre-notice-email" 300
    (localization/with-language
      "fi"
      (tx/with-transaction
        db
        (log/info "pre-notice-email lock acquired and transaction opened, continuing...")

        ;; Returned collection contains those detected-change-history ids, which were included in sent notifications.
        ;; Does not contain those, which no user was interested about.
        ;; Not used at the moment. If used, put into set and remove nil to get unique ids.
        (doall
          (mapcat #(compose-and-send-pre-notice-to-user!
                     db % email detected-changes-recipients)
                  (nap-users/list-authority-users db)))

        ;; Mark all detected-change-history records as sent, because for now it does not make sense to
        ;; include them again in next email if sending some failed.
        ;; In future failed ids could be returned by compose-and-send-pre-notice-to-user!
        (specql/update! db :gtfs/detected-change-history
                        {:gtfs/email-sent (java.util.Date.)}
                        {:gtfs/id (op/in
                                    (into #{}
                                          (map :history-id
                                               (fetch-unsent-changes-by-regions db {:regions nil}))))})))))

(defn pre-notice-recipient-emails [config]
  (or (some-> config :detected-changes-recipients
              (string/split #",")
              set)
      identity))

(defrecord PreNoticesTasks [detected-changes-recipients]
  component/Lifecycle
  (start [{db :db email :email :as this}]
    (log/info "PreNoticesTasks: starting task, recipient emails = " detected-changes-recipients)
    (assoc this
      ::stop-tasks [(chime-at (daily-at 8 15)
                              (fn [_]
                                (#'send-pre-notice-emails! db email detected-changes-recipients)))]))
  (stop [{stop-tasks ::stop-tasks :as this}]
    (doseq [stop stop-tasks]
      (stop))
    (dissoc this ::stop-tasks)))

(defn pre-notices-tasks [config]
  (->PreNoticesTasks (pre-notice-recipient-emails config)))
