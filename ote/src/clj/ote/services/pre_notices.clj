(ns ote.services.pre-notices
  "Backend services related to 60 days pre notices."
  (:require [ote.components.http :as http]
            [com.stuartsierra.component :as component]
            [compojure.core :refer [routes POST GET]]
            [ote.services.pre-notices.attachments :as pre-notices-attachments]
            [ote.services.pre-notices.operator :as pre-notices-operator]
            [ote.services.pre-notices.authority :as pre-notices-authority]))


(defrecord PreNotices [config]
  component/Lifecycle
  (start [{db   :db
           http :http
           :as this}]
    (assoc this ::stop-published-routes
           [(or (some->> (pre-notices-attachments/attachment-routes db config)
                         (http/publish! http)) :not-published)
            (http/publish! http (pre-notices-operator/operator-pre-notices-routes db (:pre-notices config)))
            (http/publish! http (pre-notices-authority/authority-pre-notices-routes db))]))

  (stop [{stop-published-routes ::stop-published-routes :as this}]
    (doseq [stop-route stop-published-routes]
      (stop-route))
    (dissoc this ::stop-published-routes)))
