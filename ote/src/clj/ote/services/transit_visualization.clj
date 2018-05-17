(ns ote.services.transit-visualization
  (:require [ote.components.http :as http]
            [compojure.core :refer [GET routes]]
            [com.stuartsierra.component :as component]
            [jeesql.core :refer [defqueries]]))

(defqueries "ote/services/transit_visualization.sql")


(defrecord TransitVisualization []
  component/Lifecycle
  (start [{http :http
           db :db :as this}]
    (assoc this ::stop
           (http/publish!
            http {:authenticated? false}
            (routes
             (GET "/transit-visualization/dates/:operator" [operator]
                  (http/transit-response (fetch-operator-date-hashes db {:operator-id (Long/parseLong operator)})))))))
  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
