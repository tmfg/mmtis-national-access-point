(ns ote.tasks.pre-notices
  (:require [chime :refer [chime-at]]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as log]
            [ote.email :refer [send-email]]
            [clj-time.core :as t]
            [clj-time.periodic :refer [periodic-seq]])
  (:import (org.joda.time DateTimeZone)))


(def daily-notify-time (t/from-time-zone (t/today-at 8 15)
                                         (DateTimeZone/forID "Europe/Helsinki")))

(defn send-notification! []
  #_(try
    (send-email {:to "test.testerson@notanymail.com"
                 :from "nope@localhost"
                 :subject "testing"
                 :body "Testing testing"})
    (catch Exception e
      (log/warn "Error while sending a notification" e))))


(defrecord PreNoticesTasks [at]
  component/Lifecycle
  (start [this]
    (assoc this
      ::stop-tasks [(chime-at (drop 1 (periodic-seq at (t/days 1)))
                              (fn [_]
                                (#'send-notification!)))]))
  (stop [{stop-tasks ::stop-tasks :as this}]
    (doseq [stop stop-tasks]
      (stop))
    (dissoc this ::stop-tasks)))

(defn pre-notices-tasks
  ([] (pre-notices-tasks daily-notify-time))
  ([at]
   (->PreNoticesTasks at)))