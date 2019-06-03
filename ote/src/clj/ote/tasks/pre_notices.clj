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

(defonce timezone (DateTimeZone/forID "Europe/Helsinki"))

(defn datetime-string [dt timezone]
  (when dt
    (format/unparse (format/with-zone (format/formatter "dd.MM.yyyy HH:mm") timezone) dt)))

(def notification-html-subject
  (str "Uudet 60 päivän muutosilmoitukset NAP:ssa " (datetime-string (t/now) timezone)))

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
          (str (email-template/html-header)
               (html (email-template/notification-html notices detected-changes notification-html-subject))))
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
                                                            :subject notification-html-subject
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
