(ns ote.services.transit-visualization
  (:require [compojure.core :refer [GET]]
            [jeesql.core :refer [defqueries]]
            [ote.time :as time]
            [cheshire.core :as cheshire]
            [ote.components.http :as http]
            [ote.components.service :refer [define-service-component]]))

(defqueries "ote/services/transit_visualization.sql")

(define-service-component TransitVisualization {}
  ^{:unauthenticated true :format :transit}
  (GET "/transit-visualization/dates/:operator" [operator]
       (fetch-operator-date-hashes db {:operator-id (Long/parseLong operator)}))

  ^{:unauthenticated true :format :transit}
  (GET "/transit-visualization/routes-for-dates/:operator"
       {{operator :operator} :params
        {:strs [date1 date2]} :query-params}
       (fetch-routes-for-dates db {:operator-id (Long/parseLong operator)
                                   :date1 (time/parse-date-eu date1)
                                   :date2 (time/parse-date-eu date2)}))

  ^:unauthenticated
  (GET "/transit-visualization/route-lines-for-date/:operator"
       {{operator :operator} :params
        {:strs [date short long]} :query-params}
       (http/geojson-response
        (cheshire/encode
         {:type "FeatureCollection"
          :features (for [{:keys [route-line departures]}
                          (fetch-route-trips-by-name-and-date
                           db
                           {:operator-id (Long/parseLong operator)
                            :date (time/parse-date-eu date)
                            :route-short-name short
                            :route-long-name long})]
                      {:type "Feature"
                       :properties {:departures (mapv time/format-interval-as-time (.getArray departures))}
                       :geometry (cheshire/decode route-line keyword)})}
         {:key-fn name})))

  ^{:unauthenticated true :format :transit}
  (GET "/transit-visualization/route-trips-for-date/:operator"
       {{operator :operator} :params
        {:strs [date short long]} :query-params}
       (fetch-trip-stops-for-route-by-name-and-date db {:operator-id (Long/parseLong operator)
                                                        :date (time/parse-date-eu date)
                                                        :route-short-name short
                                                        :route-long-name long})))
