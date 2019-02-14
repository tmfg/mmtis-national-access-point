(ns ote.services.transit-visualization
  (:require [compojure.core :refer [GET]]
            [jeesql.core :refer [defqueries]]
            [ote.time :as time]
            [cheshire.core :as cheshire]
            [ote.components.http :as http]
            [ote.components.service :refer [define-service-component]]
            [specql.core :as specql]
            [clojure.string :as str]
            [taoensso.timbre :as log]
            [specql.impl.composite :as composite]
            [specql.impl.registry :as specql-registry]
            [ote.util.fn :refer [flip]]
            [ote.transit-changes.detection :as detection]
            [clojure.set :as set]))

(defqueries "ote/services/transit_visualization.sql")

(defn- parse-stops [stops]
  (mapv (fn [stop]
          (let [[lat lon stop-name] (str/split stop #",")]
            {:lat (Double/parseDouble lat)
             :lon (Double/parseDouble lon)
             :name stop-name}))
        (str/split stops #"\|\|")))

(defn route-line-features [trips]
  (mapcat (fn [{:keys [route-line departures stops] :as foo}]
            (let [all-stops (parse-stops stops)
                  first-stop (first all-stops)
                  last-stop (last all-stops)]
              (vec (into
                    #{{:type "Feature"
                       :properties {:departures (mapv time/format-interval-as-time (.getArray departures))
                                    :routename (str (:name first-stop) " \u2192 " (:name last-stop))}
                       :geometry (cheshire/decode route-line keyword)}}
                    (map (fn [stop]
                           (let [[lon lat name] (str/split stop #",")]
                             {:type "Point"
                              :coordinates [(Double/parseDouble lon)
                                            (Double/parseDouble lat)]
                              :properties {"name" name
                                           "trip-name" (str (:name first-stop) " \u2192 " (:name last-stop))}})))
                    (when-not (str/blank? stops)
                      (str/split stops #"\|\|"))))))
          trips))

(defn service-changes-for-date [db service-id date]
  (first
   (specql/fetch db :gtfs/transit-changes
                 (specql/columns :gtfs/transit-changes)
                 {:gtfs/transport-service-id service-id
                  :gtfs/date date})))

(defn service-calendar-for-route [db service-id route-hash-id]
  (into {}
        (map (juxt :date :hash))
        (fetch-date-hashes-for-route-with-route-hash-id db {:service-id service-id
                                                            :route-hash-id route-hash-id})))

(defn parse-gtfs-stoptimes [pg-array]
  (let [string (str pg-array)]
    (if (str/blank? string)
      nil
      (composite/parse @specql-registry/table-info-registry
                       {:category "A"
                        :element-type :gtfs/stoptime-display}
                       string))))

(defn trip-differences-for-dates [db service-id date1 date2 route-hash-id]
  (let [date1-trips (detection/route-trips-for-date db service-id route-hash-id (time/parse-date-iso-8601 date1))
        date2-trips (detection/route-trips-for-date db service-id route-hash-id (time/parse-date-iso-8601 date2))
        result (detection/compare-selected-trips date1-trips date2-trips
                                                   (time/parse-date-iso-8601 date1)
                                                   (time/parse-date-iso-8601 date2))
        stop-changes (reduce detection/update-min-max-range nil
                             (map :stop-time-changes (:trip-changes result)))
        sequence-changes (reduce detection/update-min-max-range nil
                                 (map :stop-seq-changes (:trip-changes result)))]

    (-> result
        (assoc :trip-stop-sequence-changes-lower (:lower sequence-changes))
        (assoc :trip-stop-sequence-changes-upper (:upper sequence-changes))
        (assoc :trip-stop-time-changes-lower (:lower stop-changes))
        (assoc :trip-stop-time-changes-upper (:upper stop-changes))
        (dissoc :starting-week-date :different-week-date :trip-changes)
        (set/rename-keys {:added-trips :gtfs/added-trips :removed-trips :gtfs/removed-trips}))))

(define-service-component TransitVisualization {}

  ;; Get transit changes, service info and package info for given date and service
  ^{:unauthenticated true :format :transit}
  (GET "/transit-visualization/:service-id/:date{[0-9\\-]+}"
       {{:keys [service-id date]} :params}
    (let [service-id (Long/parseLong service-id)]
      {:service-info (first (fetch-service-info db {:service-id service-id}))
       :changes (first (detected-changes-for-date db
                                                  {:service-id service-id
                                                   :date (-> date
                                                             time/parse-date-iso-8601
                                                             java.sql.Date/valueOf)}))
       :route-changes (detected-route-changes-for-date db
                                                       {:service-id service-id
                                                        :date (-> date
                                                                  time/parse-date-iso-8601
                                                                  java.sql.Date/valueOf)})
       :route-hash-id-type (first (specql/fetch db :gtfs/detection-service-route-type
                                                #{:gtfs/route-hash-id-type}
                                                {:gtfs/transport-service-id service-id}))
       :gtfs-package-info (fetch-gtfs-packages-for-service db {:service-id service-id})}))

  ^{:unauthenticated true :format :transit}
  (GET "/transit-visualization/:service-id/route"
       {{:keys [service-id]} :params
        {:strs [route-hash-id]} :query-params}
      {:calendar (service-calendar-for-route db (Long/parseLong service-id) route-hash-id)})


  ^:unauthenticated
  (GET "/transit-visualization/:service-id/route-lines-for-date"
       {{service-id :service-id} :params
        {:strs [date short-name long-name headsign route-hash-id]} :query-params}
       (http/geojson-response
        (cheshire/encode
         {:type "FeatureCollection"
          :features (route-line-features
                     (fetch-route-trips-by-hash-and-date
                      db
                      {:service-id (Long/parseLong service-id)
                       :date (time/parse-date-iso-8601 date)
                       :route-hash-id route-hash-id}))}
         {:key-fn name})))

  ^{:unauthenticated true :format :transit}
  (GET "/transit-visualization/:service-id/route-trips-for-date"
       {{service-id :service-id} :params
        {:strs [date short-name long-name headsign route-hash-id]} :query-params}
      (into []
            (map #(update % :stoptimes parse-gtfs-stoptimes))
            (fetch-route-trip-info-by-name-and-date
              db
              {:service-id       (Long/parseLong service-id)
               :date             (time/parse-date-iso-8601 date)
               :route-hash-id route-hash-id})))

  ^{:unauthenticated true :format :transit}
  (GET "/transit-visualization/:service-id/route-differences"
       {{service-id :service-id} :params
        {:strs [date1 date2 short-name long-name headsign route-hash-id]} :query-params}
    (trip-differences-for-dates db service-id date1 date2 route-hash-id)))
