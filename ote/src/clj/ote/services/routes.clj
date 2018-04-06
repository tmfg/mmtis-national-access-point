(ns ote.services.routes
  "Routes api."
  (:require [com.stuartsierra.component :as component]
            [jeesql.core :refer [defqueries]]
            [ote.util.fn :refer [flip]]
            [ote.components.http :as http]
            [ote.services.transport :as transport]
            [specql.core :refer [fetch update! insert! upsert! delete!] :as specql]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]
            [ote.db.transit :as transit]
            [ote.db.modification :as modification]
            [compojure.core :refer [routes GET POST DELETE]]
            [ote.geo :as geo]
            [ote.time :as time]
            [taoensso.timbre :as log]
            [specql.op :as op]
            [ote.authorization :as authorization]
            [cheshire.core :as cheshire]
            [ote.db.tx :as tx]
            [jeesql.core :refer [defqueries]])
  (:import (org.postgis PGgeometry Point Geometry)))

(defqueries "ote/services/routes.sql")

(def route-list-columns  #{::transit/id
                           ::transit/transport-operator-id
                           ::transit/name
                           ::transit/published?
                           ::transit/available-from ::transit/available-to
                           ::transit/departure-point-name ::transit/destination-point-name
                           ::modification/created ::modification/modified})

(defn get-user-routes [db groups user]
  (let [operators (keep #(transport/get-transport-operator db {::t-operator/ckan-group-id (:id %)}) groups)
        routes (fetch db ::transit/route route-list-columns
                      {::transit/transport-operator-id (op/in (map ::t-operator/id operators))})]

    (map (fn [{id ::t-operator/id :as operator}]
           {:transport-operator operator
            :routes             (into []
                                      (filter #(= id (::transit/transport-operator-id %)))
                                      routes)})
         operators)))

(defn- service-date->inst [date]
  (-> date
      time/date-fields->date
      (.atStartOfDay (java.time.ZoneId/of "Europe/Helsinki"))
      .toInstant
      java.util.Date/from))

(defn- point-geometry [geom]
  (if (instance? PGgeometry geom)
    geom
    (let [[lat lng] geom]
      (PGgeometry. (Point. lat lng)))))

(defn- stop-location-geometry [{loc ::transit/location :as stop}]
  (assoc stop
         ::transit/location (point-geometry loc)))

(defn- service-calendar-dates->db [{::transit/keys [service-removed-dates service-added-dates] :as cal}]
  (assoc cal
    ::transit/service-removed-dates (map service-date->inst service-removed-dates)
    ::transit/service-added-dates (map service-date->inst service-added-dates)))

(defn- next-stop-code [db]
  (str "OTE" (next-stop-sequence-number db)))

(defn save-custom-stops [route db user]
  (let [custom-stops
        (into {}
              (map (fn [{:keys [id geojson]}]
                     [id (specql/insert!
                          db ::transit/finnish-ports
                          (modification/with-modification-fields
                            {::transit/code (next-stop-code db)
                             ::transit/name (get-in geojson ["properties" "name"])
                             ::transit/location (point-geometry (get-in geojson ["geometry" "coordinates"]))}
                            ::transit/id user))]))
              (:custom-stops route))]
    (-> route
        (dissoc :custom-stops)
        (update ::transit/stops (flip mapv)
                (fn [{::transit/keys [code] :as stop}]
                  (or (custom-stops code) stop))))))

(comment
  {:ote.db.transit/name "asdasda", :ote.db.transit/service-calendars [{:ote.db.transit/service-rules [{:ote.db.transit/from-date #inst "2018-04-03T21:00:00.000-00:00", :ote.db.transit/to-date #inst "2018-04-29T21:00:00.000-00:00", :ote.db.transit/monday true, :ote.db.transit/tuesday true}], :rule-dates #{{:ote.time/date 17, :ote.time/year 2018, :ote.time/month 4} {:ote.time/date 23, :ote.time/year 2018, :ote.time/month 4} {:ote.time/date 16, :ote.time/year 2018, :ote.time/month 4} {:ote.time/date 24, :ote.time/year 2018, :ote.time/month 4} {:ote.time/date 30, :ote.time/year 2018, :ote.time/month 4} {:ote.time/date 10, :ote.time/year 2018, :ote.time/month 4} {:ote.time/date 9, :ote.time/year 2018, :ote.time/month 4}}, :ote.db.transit/service-removed-dates (), :ote.db.transit/service-added-dates ()}],
   :ote.db.transit/trips [{:ote.db.transit/stop-times
                           [{:ote.db.transit/stop-idx 0, :ote.db.transit/drop-off-type :regular, :ote.db.transit/pickup-type :regular, :ote.db.transit/arrival-time nil, :ote.db.transit/departure-time #ote.time.Time{:hours 20, :minutes 0, :seconds nil}}
                            {:ote.db.transit/stop-idx 1, :ote.db.transit/drop-off-type :regular, :ote.db.transit/pickup-type :regular, :ote.db.transit/arrival-time #ote.time.Time{:hours 23, :minutes 0, :seconds nil}, :ote.db.transit/departure-time nil}
                            ],
                           :ote.db.transit/service-calendar-idx 0}
                          {:ote.db.transit/stop-times [{:ote.db.transit/stop-idx 0, :ote.db.transit/drop-off-type :regular, :ote.db.transit/pickup-type :regular, :ote.db.transit/arrival-time nil, :ote.db.transit/departure-time #ote.time.Time{:hours 23, :minutes 30, :seconds nil}} {:ote.db.transit/stop-idx 1, :ote.db.transit/drop-off-type :regular, :ote.db.transit/pickup-type :regular, :ote.db.transit/arrival-time #ote.time.Time{:hours 26, :minutes 30, :seconds nil}, :ote.db.transit/departure-time nil}], :ote.db.transit/service-calendar-idx 0}], :ote.db.transit/published? false, :ote.db.transit/route-type :ferry, :ote.db.transit/transport-operator-id 1, :ote.db.transit/stops [{:ote.db.transit/code "FIKJO-C", :ote.db.transit/name "KALAJOKI: Itäkenttä", :ote.db.transit/location #object[org.postgis.PGgeometry 0x67908422 "POINT(23.710541798098134 64.22124510154973)"]} {:ote.db.transit/code "FIRAA-RR", :ote.db.transit/name "RAAHE: Rautaruukki", :ote.db.transit/location #object[org.postgis.PGgeometry 0x24eb70dd "POINT(24.407513182926373 64.65296589847007)"]}], :ote.db.modification/created #inst "2018-04-06T09:38:57.251000000-00:00", :ote.db.modification/created-by "401139db-8f3e-4371-8233-5d51d4c4c8b6"})

(defn- trip-stop-times-to-24h [trip]
  (update trip ::transit/stop-times (flip mapv)
          (fn [st]
            (-> st
                (update ::transit/arrival-time #(when % (transit/time-to-24h %)))
                (update ::transit/departure-time #(when % (transit/time-to-24h %)))))))

(defn save-route [nap-config db user route]
  (authorization/with-transport-operator-check
    db user (::transit/transport-operator-id route)
    (fn []
      (tx/with-transaction db
        (let [r (-> route
                    (save-custom-stops db user)
                    (modification/with-modification-fields ::transit/id user)
                    (update ::transit/stops #(mapv stop-location-geometry %))
                    (update ::transit/service-calendars #(mapv service-calendar-dates->db %))
                    (update ::transit/trips (flip mapv) trip-stop-times-to-24h))]
          (log/debug "Save route: " r)
          (upsert! db ::transit/route r))))))

(defn get-route
  "Get single route by id"
  [db id]
  (let [route (first (fetch db ::transit/route
                            (specql/columns ::transit/route)
                            {::transit/id id}))]
    (log/debug  "**************** route" (pr-str route))
    route))

(defn delete-route!
  "Delete single route by id"
  [db user id]
  (log/debug  "***************** deleting route id " id)
  (let [{::transit/keys [transport-operator-id]}
        (first (specql/fetch db ::transit/route
                             #{::transit/transport-operator-id}
                             {::transit/id id}))]
    (authorization/with-transport-operator-check
      db user transport-operator-id
        #(do
           (delete! db ::transit/route {::transit/id id})
           id))))

(defn- routes-auth
  "Routes that require authentication"
  [db nap-config]
  (routes
    (POST "/routes/routes" {user :user}
      (http/transit-response
        (get-user-routes db (:groups user) (:user user))))

    (POST "/routes/new" {form-data :body
                         user      :user}
      (http/transit-response
        (save-route nap-config db user (http/transit-request form-data))))

    (GET "/routes/:id" [id]
      (let [route (get-route db (Long/parseLong id))]
        (if-not route
          {:status 404}
          (http/no-cache-transit-response route))))

    (POST "/routes/delete" {form-data :body
                            user      :user}
      (http/transit-response
        (delete-route! db user
                       (:id (http/transit-request form-data)))))))

(defn- stops-geojson [db]
  (cheshire/encode
   {:type "FeatureCollection"
    :features (for [{::transit/keys [code name location]}
                    (fetch db ::transit/finnish-ports
                           #{::transit/code ::transit/name ::transit/location}
                           {})]
                {:type "Feature"
                 :geometry {:type "Point"
                            :coordinates [(.-x (.getGeometry location))
                                          (.-y (.getGeometry location))]}
                 :properties {:code code :name name}})}))

(defn- public-routes
  "Routes that are public (don't require authentication)"
  [db]
  (routes
   (GET "/transit/stops.json" _
        {:status 200
         :headers {"Content-Type" "application/vnd.geo+json"}
         :body (stops-geojson db)})))

(defrecord Routes [nap-config]
  component/Lifecycle
  (start [{:keys [db http] :as this}]
    (assoc
      this ::stop
      [(http/publish! http (routes-auth db nap-config))
       (http/publish! http {:authenticated? false} (public-routes db))]))
  (stop [{stop ::stop :as this}]
    (doseq [s stop]
      (s))
    (dissoc this ::stop)))
