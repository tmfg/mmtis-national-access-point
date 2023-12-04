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
  (log/info "Polling for finished entries from TIS/VACO API")
  (when (and (feature/feature-enabled? config :tis-vaco-integration)
             (feature/feature-enabled? config :netex-conversion-automated))
    (let [packages (select-packages-without-finished-results db)]
      (log/info (str (count packages) " TIS packages to update"))
      (mapv
        (fn [package]
          (let [{package-id :id entry-public-id :tis-entry-public-id} (select-keys package [:id :tis-entry-public-id])]
            (log/info (str "Polling package " package-id "/" entry-public-id " for results"))
            (let [entry     (tis-vaco/api-fetch-entry (:tis-vaco config) entry-public-id)
                  complete? (every? (fn [t] (not (some? (get t "completed")))) (get-in entry ["data" "tasks"]))
                  links     (get entry "links")
                  result    (some-> links (get "gtfs2netex.fintraffic.v1_0_0"))]
              (if result
                (do
                  (log/info (str "Results found for package " package-id "/" entry-public-id ", storing links to database"))
                  (update-tis-results! db {:tis-entry-public-id entry-public-id
                                           :tis-complete        true
                                           :tis-result-links    (cheshire/generate-string result)}))
                (if complete?
                  (do
                    (log/info (str "No results found for package " package-id "/" entry-public-id " but the entry is complete -> no result available"))
                    (update-tis-results! db {:tis-entry-public-id entry-public-id
                                             :tis-complete        true
                                             :tis-result-links    nil}))
                  (log/info (str "Package " package-id "/" entry-public-id " processing is not yet complete on TIS side."))))))

          )
        packages))))

(defrecord TisTasks [config]
  component/Lifecycle
  (start [{db :db :as this}]
    (assoc this
      ; 5 minute delay is more than enough, we do not want to overload the VACO API with redundant calls
      ::tis-tasks [(chime/chime-at (drop 1 (periodic/periodic-seq (t/now) (t/minutes 15)))
                              (fn [_]
                                (#'poll-incomplete-entry-results! config db)))]))
  (stop [{stop-tasks ::stop-tasks :as this}]
    (doseq [stop stop-tasks]
      (stop))
    (dissoc this ::stop-tasks)))

(defn tis-tasks [config]
  (->TisTasks config))