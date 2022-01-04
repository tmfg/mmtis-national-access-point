(ns ote.services.taxiui
  (:require [com.stuartsierra.component :as component]
            [jeesql.core :refer [defqueries]]
            [ote.components.service :refer [define-service-component]]
            [ote.components.http :as http]))

(defqueries "ote/services/places.sql")

(defn priceinfo-for-service
  [db service]
  (vec (fetch-priceinfo-for-service db {:service (str service)})))

(defrecord Places [sources]
  component/Lifecycle
  (start [{db :db http :http :as this}]
    (assoc this ::stop
                (http/publish!
                  http
                  (routes
                    (GET "/taxiui/price-info/:service" [service]
                         (http/transit-response
                           (priceinfo-for-service db service)))))))

  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))

