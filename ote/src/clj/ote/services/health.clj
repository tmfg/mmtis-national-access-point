(ns ote.services.health
  (:require [compojure.core :refer [GET]]
            [jeesql.core :refer [defqueries]]
            [ote.components.service :refer [define-service-component]]
            [taoensso.timbre :as log]))

(defqueries "ote/services/health.sql")

(defn ^:private db-simple-check
  [db]
  (let [now (System/currentTimeMillis)]
    (if (= now (ping-database db {:now now}))
      nil
      (do
        (log/warn "Ping database failed!")
        {:status 500
         :body   "Database unavailable"}))))

(defn ^:private db-data-check
  [db]
  (let [result (simple-data-check db)]
    (if (some? result)
      nil
      (do
        (log/warn "Simple data check failed!")
        {:status 500
         :body   "Database unavailable"}))))

(defn health
  "Perform various health checks when called, return 200 OK if all succeeded."
  [db config]
  ; implementation note: Health checks rely on nil punning. Return non-nil map of {:status, :body} to indicate failure.
  (try
    (if-let [{:keys [status body]} (or (db-simple-check db) (db-data-check db))]
      {:status  status
       :headers {"Content-Type" "text/plain"}
       :body    (str body)}
      {:status  200
       :headers {"Content-Type" "text/plain"}
       :body    "ok"})
    (catch Exception e
      (log/warn "Health check execution failure" e)
      {:status 500
       :headers {"Content-Type" "text/plain"}
       :body   "Health check execution failure"}
      )))

(define-service-component Health {:fields [config]}
  ^:unauthenticated
  (GET "/health" []
    (health db config)))

