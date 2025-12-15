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

(defn export-rdf-to-s3 [{{:keys [bucket]} :rdf-export :as config} db]
  (try 
    (with-open [out (java.io.ByteArrayOutputStream.)]
      (log/info "Starting export-rdf-to-s3")
      (rdf-service/create-rdf config db out)
      (let [bytes (.toByteArray out)
            len (count bytes)]
        
        (s3/put-object bucket 
                       "rdf"
                       (ByteArrayInputStream. bytes)
                       {:content-length len})
        (log/info "exported rdf into s3")))
    (catch Throwable t
      (log/error (str (pr-str t) " export-rdf-to-s3 failed"))
      (throw t))))

;; (export-rdf-to-s3 ote.main/_config
;;                   (:db ote.main/ote))

(defrecord RdfTasks [config]
  component/Lifecycle
  (start [{db :db :as this}]
    (let [{:keys [testing-env? dev-mode?]} config]
      (if (and (not dev-mode?)
               testing-env?
               (feature/feature-enabled? :rdf-export))
        (do
          (log/info "Config allows us to start export-rdf-to-s3")
          (#'export-rdf-to-s3 config db))
        (log/infof "dev-mode? %s; feature-enabled? %s" (pr-str dev-mode?) (pr-str (feature/feature-enabled? :rdf-export))))
      
      (assoc this
             ::stop-tasks
             (if (and (not dev-mode?)
                      (feature/feature-enabled? :rdf-export))
               [(chime-at
                 (tasks-util/daily-at 16 30)
                 (fn [_]
                   (#'export-rdf-to-s3 config db)))]
               (do
                 (log/debug "RDF EXPORT IS NOT ENABLED!")
                 nil)))))
  (stop [{stop-tasks ::stop-tasks :as this}]
    (for [stop-task stop-tasks]
      (stop-task))
    (dissoc this ::stop-tasks)))

(defn rdf-tasks
  ([config]
   (->RdfTasks config)))
