(ns ote.services.transit-visualization
  (:require [compojure.core :refer [GET]]
            [jeesql.core :refer [defqueries]]
            [ote.time :as time]
            [cheshire.core :as cheshire]
            [ote.components.http :as http]
            [ote.components.service :refer [define-service-component]]
            [ote.db.transport-operator :as t-operator]
            [specql.core :as specql]
            [specql.op :as op]
            [clojure.string :as str]
            [taoensso.timbre :as log]))

(defqueries "ote/services/transit_visualization.sql")

(defn route-line-features [rows]
  (mapcat (fn [{:keys [route-line departures stops] :as foo}]
            (vec (into
                  #{{:type "Feature"
                     :properties {:departures (mapv time/format-interval-as-time (.getArray departures))}
                     :geometry (cheshire/decode route-line keyword)}}
                  (map (fn [stop]
                         (let [[lon lat name] (str/split stop #",")]
                           {:type "Point"
                            :coordinates [(Double/parseDouble lon)
                                          (Double/parseDouble lat)]
                            :properties {"name" name}})))
                  (when (not (str/blank? stops))
                    (str/split stops #"\|\|")))))
          rows))

(defn service-changes-for-date [db service-id date]
  (first
   (specql/fetch db :gtfs/transit-changes
                 (specql/columns :gtfs/transit-changes)
                 {:gtfs/transport-service-id service-id
                  :gtfs/date date})))

(defn service-calendar-for-route [db service-id route-short-name route-long-name trip-headsign]
  (into {}
        (map (juxt :date :hash))
        (fetch-date-hashes-for-route db {:service-id service-id
                                         :route-short-name route-short-name
                                         :route-long-name route-long-name
                                         :trip-headsign trip-headsign})))

(define-service-component TransitVisualization {}

  ^{:unauthenticated true :format :transit}
  (GET "/transit-visualization/:service-id/:date{[0-9\\-]+}"
       {{:keys [service-id date]} :params}
       (let [service-id (Long/parseLong service-id)]
         {:service-info (first (fetch-service-info db {:service-id service-id}))
          :changes (service-changes-for-date db
                                             service-id
                                             (-> date
                                                 time/parse-date-iso-8601
                                                 java.sql.Date/valueOf))}))

  ^{:unauthenticated true :format :transit}
  (GET "/transit-visualization/:service-id/route/:short-name/:long-name/:headsign"
       {{:keys [service-id short-name long-name headsign]} :params}
       {:calendar (service-calendar-for-route db (Long/parseLong service-id)
                                              short-name long-name headsign)})


    ^:unauthenticated
  (GET "/transit-visualization/:service-id/route-lines-for-date"
       {{service-id :service-id} :params
        {:strs [date short long headsign]} :query-params}
       (http/geojson-response
        (cheshire/encode
         {:type "FeatureCollection"
          :features (route-line-features
                     (fetch-route-trips-by-name-and-date
                      db
                      {:service-id (Long/parseLong service-id)
                       :date (time/parse-date-iso-8601 date)
                       :route-short-name short
                       :route-long-name long
                       :trip-headsign headsign}))}
         {:key-fn name})))

  ;;; FIXME: poista vanhat
  ^{:unauthenticated true :format :transit}
  (GET "/transit-visualization/info/:operator"
       {{operator :operator} :params}
    (first
      (specql/fetch db
                    ::t-operator/transport-operator
                    #{::t-operator/id ::t-operator/name}
                    {::t-operator/id (Long/parseLong operator)})))

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



  ^{:unauthenticated true :format :transit}
  (GET "/transit-visualization/route-trips-for-date/:operator"
       {{operator :operator} :params
        {:strs [date short long headsign]} :query-params}
       (fetch-trip-stops-for-route-by-name-and-date db {:operator-id (Long/parseLong operator)
                                                        :date (time/parse-date-eu date)
                                                        :route-short-name short
                                                        :route-long-name long
                                                        :headsign headsign})))
