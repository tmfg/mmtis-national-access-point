(ns ote.tasks.rdf
  "Scheduled tasks to generate rdf-dump to s3, where user can fetch it by calling /rdf"
  (:require [ote.util.feature :as feature]
            [amazonica.aws.s3 :as s3]
            [ote.services.rdf :as rdf-service]
            [taoensso.timbre :as log]
            [com.stuartsierra.component :as component]
            [ote.time :as time]
            [ote.tasks.util :as tasks-util]
            [clj-time.core :as t]
            [clj-time.periodic :refer [periodic-seq]]
            [chime :refer [chime-at]])
  (:import (org.joda.time DateTime DateTimeZone)
           (java.io ByteArrayInputStream)))

;; run only once a day
(def allowed-hours #{16})

(def daily-update-time-dev (t/from-time-zone (t/today-at 11 5)
                                         (DateTimeZone/forID "Europe/Helsinki")))

(def daily-update-time-prod (t/from-time-zone (t/today-at 18 5)
                                              (DateTimeZone/forID "Europe/Helsinki")))

(defn export-rdf-to-s3 [db]
  (let [rdf (rdf-service/create-rdf db)
        bytes (.getBytes rdf)
        len (count bytes)]
    
    (s3/put-object "finap-rdf-cache"
                   "rdf"
                   (ByteArrayInputStream. bytes)
                   {:content-length len})
    (log/info "exported rdf into s3")))
                   

(defn allowed-time? [dt]
  (-> dt (t/to-time-zone tasks-util/timezone) time/date-fields ::time/hours allowed-hours boolean))

(defrecord RdfTasks [at config]
  component/Lifecycle
  (start [{db :db :as this}]
    (assoc this
      ::stop-tasks
      (if (feature/feature-enabled? :rdf-export)
        [(chime-at
           (filter allowed-time?
                   (drop 1 (periodic-seq (t/now) (t/days 1))))
           (fn [_]
             (#'export-rdf-to-s3 db)))]
        (do
          (log/debug "RDF EXPORT IS NOT ENABLED!")
          nil))))
  (stop [{stop-tasks ::stop-tasks :as this}]
    (for [stop-task stop-tasks]
      (stop-task))
    (dissoc this ::stop-tasks)))

(defn rdf-tasks
  ([config] (rdf-tasks (if (:dev-mode? config) daily-update-time-dev daily-update-time-prod ) config))
  ([at config]
   (println (pr-str {:rdf-config config}))
   (->RdfTasks at config)))
