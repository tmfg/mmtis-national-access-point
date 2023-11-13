(ns ote.tasks.tis
  (:require [cheshire.core :as cheshire]
            [chime :as chime]
            [clj-time.core :as t]
            [clj-time.periodic :as periodic]
            [com.stuartsierra.component :as component]
            [jeesql.core :refer [defqueries]]
            [ote.integration.tis-vaco :as tis-vaco]
            [ote.util.feature :as feature]
            [taoensso.timbre :as log]))

(defqueries "ote/tasks/tis.sql")

(defn poll-incomplete-entry-results!
  "Polls incomplete TIS entries and complete them by storing the result links to database."
  [config db]
  (when (and (feature/feature-enabled? config :tis-vaco-integration)
             (feature/feature-enabled? config :netex-conversion-automated))
    (->> (select-packages-without-finished-results db)
         (mapv
           (fn [package]
             (log/debug (str "Polling package " (select-keys package [:id :tis-entry-public-id]) " for results"))
             (let [entry (tis-vaco/api-fetch-entry (:tis-vaco config) (:tis-entry-public-id package))
                   links (get entry "links")
                   result (get links "gtfs2netex.perille.v1_0_0")]
               (when result
                 (log/info (str "Results found for package " (select-keys package [:id :tis-entry-public-id]) ", storing links to database"))
                 (update-tis-results! db {:tis-entry-public-id (get-in entry ["data" "publicId"])
                                          :tis-result-links    (cheshire/generate-string result)}))))))))

(defrecord TisTasks [config]
  component/Lifecycle
  (start [{db :db :as this}]
    (assoc this
      ; 5 minute delay is more than enough, we do not want to overload the VACO API with redundant calls
      ::tis-tasks [(chime/chime-at (drop 1 (periodic/periodic-seq (t/now) (t/minutes 5)))
                              (fn [_]
                                (#'poll-incomplete-entry-results! config db)))]))
  (stop [{stop-tasks ::stop-tasks :as this}]
    (doseq [stop stop-tasks]
      (stop))
    (dissoc this ::stop-tasks)))

(defn tis-tasks [config]
  (->TisTasks config))