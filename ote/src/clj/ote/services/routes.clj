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
  (-> cal
      (update ::transit/service-rules (flip mapv) #(-> %
                                                       (update ::transit/from-date service-date->inst)
                                                       (update ::transit/to-date service-date->inst)))
      (update ::transit/service-removed-dates (flip mapv) service-date->inst)
      (update ::transit/service-added-dates (flip mapv) service-date->inst)))

(defn- next-stop-code [db]
  (str "OTE" (next-stop-sequence-number db)))

(defn save-custom-stops [route db user]
  (let [custom-stops
        (into {}
              (map (fn [{:keys [id geojson name]}]
                     [id (specql/insert!
                          db ::transit/finnish-ports
                          (modification/with-modification-fields
                            {::transit/code (next-stop-code db)
                             ::transit/name name
                             ::transit/location (point-geometry (get-in geojson ["geometry" "coordinates"]))}
                            ::transit/id user))]))
              (:custom-stops route))]
    (-> route
        (dissoc :custom-stops)
        (update ::transit/stops (flip mapv)
                (fn [{::transit/keys [code] :as stop}]
                  (or (custom-stops code) stop))))))

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
                    (update ::transit/trips (flip mapv) transit/trip-stop-times-to-24h))]
          (log/debug "Save route: " r)
          (upsert! db ::transit/route r))))))

(defn get-route
  "Get single route by id"
  [db user route-id]
  (authorization/with-transport-operator-check
    db user
    (::transit/transport-operator-id (first
                                       (fetch db ::transit/route
                                              #{::transit/transport-operator-id}
                                              {::transit/id route-id})))
    (fn []
      (tx/with-transaction
        db
        (let [route (-> (fetch db ::transit/route
                               (specql/columns ::transit/route)
                               {::transit/id route-id})
                        first
                        (update ::transit/service-calendars (flip mapv) transit/service-calendar-date-fields)
                        (update ::transit/trips (flip mapv) transit/trip-stop-times-from-24h))]
          route)))))

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

(defn- update-operator-homepage! [db user {:keys [operator-id homepage] :as form-data}]
  (authorization/with-transport-operator-check
    db user operator-id
    #(do
       (update! db ::t-operator/transport-operator
                {::t-operator/homepage homepage}
                {::t-operator/id operator-id})
       homepage)))

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

    (POST "/routes/update-operator-homepage" {form-data :body
                                              user :user}
      (http/transit-response
        (update-operator-homepage! db user (http/transit-request form-data))))

    (GET "/routes/:id" [id :as {user :user}]
      (let [route (get-route db user (Long/parseLong id))]
        (cond
          (not route) {:status 404}
          (= "Forbidden" (:body route)) route
          :else (http/no-cache-transit-response route))))

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
