(ns ote.services.routes
  "Routes api."
  (:require [com.stuartsierra.component :as component]
            [jeesql.core :refer [defqueries]]
            [ote.util.fn :refer [flip]]
            [ote.components.http :as http]
            [ote.services.transport :as transport]
            [ote.services.transport-operator :as transport-operator]
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
            [jeesql.core :refer [defqueries]]
            [ote.environment :as environment]
            [ote.db.feature]
            [clojure.string :as str])                               ; specql table definitions
  (:import (org.postgis PGgeometry Point Geometry)))

(defqueries "ote/services/routes.sql")

(defn- service-state-response [db user]
  (when (and (not (:admin? user))
             (:ote.db.feature/value
               (first
                 (fetch db
                        :ote.db.feature/feature-variation
                        (specql/columns :ote.db.feature/feature-variation)
                        {:ote.db.feature/feature :maintenance-break-sea-route}))))
    (http/no-cache-transit-response "Under maintenance" 503)))

(defn- interface-url
  "Create interface url for route."
  [operator-id]
  (str (environment/base-url) "export/gtfs/" operator-id))

(def route-list-columns  #{::transit/route-id
                           ::transit/transport-operator-id
                           ::transit/name
                           ::transit/published?
                           ::transit/available-from ::transit/available-to
                           ::transit/departure-point-name ::transit/destination-point-name
                           ::modification/created ::modification/modified})

(defn- route-used-in-services [db operator-id]
  (fetch-services-with-route db {:operator-id operator-id
                                  :url (interface-url operator-id)}))

(defn get-user-routes [db groups user]
  (let [operators (keep #(transport-operator/get-transport-operator db {::t-operator/ckan-group-id (:id %)}) groups)
        routes (fetch db
                      ::transit/route
                      route-list-columns
                      {::transit/transport-operator-id (op/in (map ::t-operator/id operators))})]

    (map (fn [{operator-id ::t-operator/id :as operator}]
           (let [routes (into []
                              (filter #(= operator-id (::transit/transport-operator-id %)))
                              routes)]
             {:transport-operator operator
              :routes routes
              :route-used-in-services (route-used-in-services db operator-id)}))
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

(defn save-operator-homepage
  "This is not usually needed. Operator homepage is updated via onBlur, but it is possible, that
  user manages to save route form before onBlur event fires. So here we save operator homepage to db and clean up route form data."
  [route db]
  (when (:operator-homepage route)
    (specql/update! db ::t-operator/transport-operator
                    {::t-operator/homepage (:operator-homepage route)}
                    {::t-operator/id (::transit/transport-operator-id route)}))
  (dissoc route :operator-homepage))

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

(defn- stop-time->pginterval [stop-time]
  (cond-> stop-time
          (some? (::transit/departure-time stop-time)) (update ::transit/departure-time #(ote.time/->PGInterval %))
          (some? (::transit/arrival-time stop-time)) (update ::transit/arrival-time #(ote.time/->PGInterval %))))

(defn- sort-tripsv [trips]
  (vec (sort-by
         (juxt
           (comp ote.time/minutes-from-midnight ote.time/pginterval->interval ::transit/departure-time first ::transit/stop-times)
           (comp ote.time/minutes-from-midnight ote.time/pginterval->interval ::transit/arrival-time last ::transit/stop-times))
         trips)))

(defn sort-stop-times-of-tripsv [trips]
  (mapv (fn [trip]
          (update trip ::transit/stop-times #(vec (sort-by ::transit/stop-idx %))))
        trips))

(defn fetch-sea-trips [db route-id]
  (-> (fetch db
             ::transit/trip
             (conj (specql/columns ::transit/trip)
                   [::transit/stop-times  ; Nests linked stop-time table records under each trip record
                    (specql/columns ::transit/stop-time)])
             {:transit-trip/route-id route-id})
      ; Trips must be in same order as stops because of front-end design so sorted just in case.
      sort-stop-times-of-tripsv
      sort-tripsv))

(defn get-route
  "Get single route by id"
  [db user route-id]
  (authorization/with-transport-operator-check
    db user
    (::transit/transport-operator-id (first
                                       (fetch db ::transit/route
                                              #{::transit/transport-operator-id}
                                              {::transit/route-id route-id})))
    (fn []
      (tx/with-transaction
        db
        (let [trips (fetch-sea-trips db route-id)
              route (->
                      ;(fetch db ::transit/route TODO: make this work or remove
                      ;   (conj (specql/columns ::transit/route)
                      ;         [::transit/trips                       ; Nests transit_route_trip for front-end model
                      ;          (conj (specql/columns ::transit/trip)
                      ;                [::transit/stop-times           ; Nests transit_route_stop_time for front-end model
                      ;                 (specql/columns ::transit/stop-time)])])
                      (fetch db
                             ::transit/route
                             (specql/columns ::transit/route)
                             {::transit/route-id route-id})
                      first
                      ; Trips assoc'd because couldn't get specql nested join working on 3 levels properly
                      (assoc ::transit/trips trips)
                      (update ::transit/service-calendars (flip mapv) transit/service-calendar-date-fields))]
          route)))))

(defn- set-for-keys [c k]
  "Takes collection `c` and takes values of key `k` of each item, returns a set of values with nils filtered out."
  (set (remove nil? (map c k))))

(defn- update-stop-times!
  "Takes `stop-times`, sets `trip-id` to them, saves to db and deletes stop-time record not part of `stop-times` set.
  Returns collection of updated stop-time records"
  [db stop-times trip-id]
  {:pre [(clojure.test/is (not (neg-int? trip-id)))]}       ; `is` used to print the value of a failed precondition
  (let [stop-times-saved (mapv #(upsert! db ::transit/stop-time %)
                               ;; New trip primary key set to stop-times because stop-times refer to trip
                               (map #(assoc % :transit-stop-time/trip-id trip-id)
                                    stop-times))]
    (delete! db                                             ; Orphaned stop-time records must be removed
             ::transit/stop-time
             (op/and {:transit-stop-time/trip-id trip-id}
                     {:transit-stop-time/stop-time-id (op/not
                                                        (op/in
                                                          (set-for-keys :transit-stop-time/stop-time-id
                                                                        stop-times-saved)))}))
    stop-times-saved))

(defn- update-trips-and-stops!
  "Takes `trips` and updates them to db as well as stop-time records which refer to them.
  Returns a collection of saved trips with updated primary key."
  [db trips]
  (mapv (fn [trip]
          (let [stop-times (mapv stop-time->pginterval (::transit/stop-times trip))
                ;; Set updated trip primary keys to stop time records so stop-times are not orphaned
                trip-saved (upsert! db
                                    ::transit/trip
                                    (dissoc trip ::transit/stop-times)) ; Nested stop time not part of trip table
                trip-id (:transit-trip/trip-id trip-saved)]
            (assoc trip-saved
              ::transit/stop-times
              (update-stop-times! db stop-times trip-id))))
        trips))

(defn- update-trip-model!
  "Takes a collection of `trips` for `route-id` and saves the trips and deletes trips not part of route anymore"
  [db trips route-id]
  {:pre [(clojure.test/is (not (neg-int? route-id)))]}            ; `is` used to print the value of a failed precondition
  (let [trips-saved (update-trips-and-stops! db trips)
        trip-ids-saved (set-for-keys :transit-trip/trip-id trips-saved)
        trip-records-deleted (delete! db                    ; Orphaned trips must be removed
                                      ::transit/trip
                                      (op/and {:transit-trip/route-id route-id}
                                              {:transit-trip/trip-id (op/not (op/in trip-ids-saved))}))]
    ;; ::transit/stop-time records referencing the deleted ::transit/trip records are not deleted explicitly here,
    ;; because design relies to implicit deletion as a result of the ON DELETE CASCADE constraint
    trips-saved))

(defn update-route-model!
  "Takes a `route` updates it to db,  updates or deletes child entities like trips and its children.
  This has to be done because they are unfortunately managed as one resource un-RESTfully."
  [db user route]
  {:pre [(clojure.test/is (some? route))]}                  ; `is` used to print the value of a failed precondition
  (authorization/with-transport-operator-check
    db user (::transit/transport-operator-id route)
    (fn []
      (tx/with-transaction
        db
        (let [r (-> route
                    (save-operator-homepage db)
                    (save-custom-stops db user)
                    (modification/with-modification-fields ::transit/route-id user)
                    (update ::transit/stops #(mapv stop-location-geometry %))
                    (update ::transit/service-calendars #(mapv service-calendar-dates->db %)))
              route-saved (upsert! db ::transit/route (dissoc r ::transit/trips))
              route-id (::transit/route-id route-saved)]
          (update-trip-model! db
                              ;; Trips of a new route are missing parent route id, so set it here and save trips to db
                              (mapv #(assoc % :transit-trip/route-id route-id)
                                    (::transit/trips r))
                              route-id)
          (get-route db user (::transit/route-id route-saved))))))) ; Return updated recordset in case clients need it

(defn delete-route!
  "Delete route and db entities referring to it by `route-id`"
  [db user route-id]
  (log/debug  "Deleting route: route-id = " route-id)
  (let [{::transit/keys [transport-operator-id]}
        (first (specql/fetch db ::transit/route
                             #{::transit/transport-operator-id}
                             {::transit/route-id route-id}))]
    (authorization/with-transport-operator-check
      db user transport-operator-id
        #(do
           (delete! db ::transit/route {::transit/route-id route-id})
           route-id))))

(defn link-interface
  "This is a helper function for users. Links created route to given service.
  Users can add interface by hand but this is much more convenient for them."
  [db user  {is-linked? :is-linked?
             service-id :service-id
             operator-id :operator-id :as form-data} lang]
  (let [interface-url (interface-url operator-id)]
    (authorization/with-transport-operator-check
      db user operator-id
      #(do
         (if (boolean is-linked?)
           ;delete
           (specql/delete! db ::t-service/external-interface-description
                           {::t-service/transport-service-id service-id
                            ::t-service/external-interface {::t-service/url interface-url}})
           (specql/insert! db ::t-service/external-interface-description
                           {::t-service/external-interface {::t-service/description {}
                                                            ::t-service/url interface-url}
                            ::t-service/data-content #{:route-and-schedule}
                            ::t-service/format #{"GTFS"}
                            ::t-service/license "CC BY 4.0"
                            ::t-service/transport-service-id service-id}))
         ;; return all services with operator to update front end app-state
         {:services (transport/get-transport-services db #{operator-id} lang)
          :routes (get-user-routes db (:groups user) (:user user) lang)}))))

(defn- update-operator-homepage! [db user {:keys [operator-id homepage] :as form-data}]
  (authorization/with-transport-operator-check
    db user operator-id
    #(do
       (update! db ::t-operator/transport-operator
                {::t-operator/homepage homepage}
                {::t-operator/id operator-id})
       homepage)))

(defn- get-route-response [db user id]
  (let [route (get-route db user (Long/parseLong id))]
    (cond
      (not route) {:status 404}
      (= "Forbidden" (:body route)) route
      :else (http/no-cache-transit-response route))))

(defn- routes-auth
  "Routes that require authentication"
  [db nap-config]
  (routes
    (GET "/routes/routes" {user :user}
      (or (service-state-response db (:user user))
          (http/no-cache-transit-response
            (get-user-routes db (:groups user) (:user user)))))

    (POST "/routes/new" {form-data :body
                         user      :user}
      (or (service-state-response db (:user user))
          (http/transit-response
            (update-route-model! db user (http/transit-request form-data)))))

    (POST "/routes/update-operator-homepage" {form-data :body
                                              user :user}
      (or (service-state-response db (:user user))
          (http/transit-response
            (update-operator-homepage! db user (http/transit-request form-data)))))

    (GET "/routes/:id" [id :as {user :user}]
      (or (service-state-response db (:user user))
          (get-route-response db user id)))

    (POST "/routes/delete" {form-data :body
                            user      :user}
      (or (service-state-response db (:user user))
          (http/transit-response
            (delete-route! db user
                           (:id (http/transit-request form-data))))))

    (POST "/routes/link-interface" {body :body
                                    user :user
                                    cookies :cookies}
      (let [lang (str/upper-case (get-in cookies ["finap_lang" :value]))
            form-data (http/transit-request body)]
        (or (service-state-response db (:user user))
            (http/transit-response
              (link-interface db user form-data lang)))))))

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
   (GET "/transit/stops.json" {user :user}
     (or (service-state-response db (:user user))
         {:status 200
          :headers {"Content-Type" "application/vnd.geo+json"}
          :body (stops-geojson db)}))))

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
