(ns ote.tasks.pre-notices
  (:require [chime :refer [chime-at]]
            [jeesql.core :refer [defqueries]]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as log]
            [ote.email :refer [send-email]]
            [ote.db.tx :as tx]
            [clj-time.core :as t]
            [clj-time.periodic :refer [periodic-seq]])
  (:import (org.joda.time DateTimeZone)))

(defqueries "ote/nap/users.sql")
(defqueries "ote/tasks/pre_notices.sql")

(def daily-notify-time (t/from-time-zone (t/today-at 8 15)
                                         (DateTimeZone/forID "Europe/Helsinki")))

(defn generate-notification [db]
  (try
    (when-let [notices (fetch-pre-notices-by-interval db {:interval "1 day"})]
      (println notices))
    (catch Exception e
      (log/warn "Error while fetching pre-notices:" e))))

(defn send-notification! [db]
  (tx/with-transaction
    db
    (let [users (list-users db {:transit-authority? true :email nil :name nil})
          notification (generate-notification db)]
      (println users)
      #_(try
          (send-email {:to "test.testerson@notanymail.com"
                       :from "nope@localhost"
                       :subject "testing"
                       :body "Testing testing"})
          (catch Exception e
            (log/warn "Error while sending a notification" e))))))


(defrecord PreNoticesTasks [at]
  component/Lifecycle
  (start [{db :db :as this}]
    (assoc this
      ::stop-tasks [(chime-at (drop 1 (periodic-seq at (t/seconds 10)))
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