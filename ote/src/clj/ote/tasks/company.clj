(ns ote.tasks.company
  "Scheduled tasks to update company CSVs and stats."
  (:require [chime :refer [chime-at]]
            [clj-time.core :as t]
            [clj-time.periodic :refer [periodic-seq]]
            [com.stuartsierra.component :as component]
            [clojure.java.jdbc :as jdbc])
  (:import (org.joda.time DateTimeZone)))

(def daily-update-time (t/from-time-zone (t/today-at 0 5)
                                         (DateTimeZone/forID "Europe/Helsinki")))

(defrecord CompanyTasks [at]
  component/Lifecycle
  (start [{db :db :as this}]
    (assoc this
           ::stop (chime-at (drop 1 (periodic-seq at (t/days 1)))
                            #(jdbc/query db ["SELECT store_daily_company_stats();"]))))
  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))

(defn company-tasks
  ([] (company-tasks daily-update-time))
  ([at]
   (->CompanyTasks at)))
