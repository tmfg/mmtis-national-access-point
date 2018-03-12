(ns ote.services.routes
  "Routes api."
  (:require [com.stuartsierra.component :as component]
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
            [taoensso.timbre :as log])
  (:import (org.postgis PGgeometry Point Geometry)))

(defn get-user-routes [db groups user]
  (let [operators (keep #(transport/get-transport-operator db {::t-operator/ckan-group-id (:id %)}) groups)
        routes
        [
         {:id                               1
          ::t-service/transport-operator-id 1
          :name                             "Oulu - Hailuoto"
          :available-from                   "2018-01-01"
          :available-to                     "2018-10-01"
          :first-stop                       "Oulu"
          :last-stop                        "Hailuoto"
          :modified                         "2018-01-01"
          :created                          "2018-01-01"}
         {:id                               2
          ::t-service/transport-operator-id 1
          :name                             "Oulu - Liminka"
          :available-from                   "2018-01-01"
          :available-to                     "2018-10-01"
          :first-stop                       "Oulu"
          :last-stop                        "Liminka"
          :modified                         "2018-01-01"
          :created                          "2018-01-01"}
         {:id                               3
          ::t-service/transport-operator-id 1
          :name                             "Liminka - Kokkola"
          :available-from                   "2018-01-01"
          :available-to                     "2018-10-01"
          :first-stop                       "Liminka"
          :last-stop                        "Kokkola"
          :modified                         "2018-01-01"
          :created                          "2018-01-01"}
         {:id                               4
          ::t-service/transport-operator-id 3
          :name                             "Haukipudas - Vantaa"
          :available-from                   "2018-01-01"
          :available-to                     "2018-10-01"
          :first-stop                       "Haukipudas"
          :last-stop                        "Vantaa"
          :modified                         "2018-01-01"
          :created                          "2018-01-01"}]]
    (map (fn [{id ::t-operator/id :as operator}]
           {:transport-operator operator
            :routes             (into []
                                      (filter #(= id (::t-service/transport-operator-id %)))
                                      routes)})
         operators)))

(defn- service-date->inst [date]
  (-> date
      time/date-fields->date
      (.atStartOfDay (java.time.ZoneId/of "Europe/Helsinki"))
      .toInstant
      java.util.Date/from))

(defn- stop-location-geometry [{[lat lng] ::transit/location :as stop}]
  (assoc stop
         ::transit/location (PGgeometry. (Point. lat lng))))

(defn- service-calendar-dates [{::transit/keys [service-removed-dates service-added-dates] :as cal}]
  (assoc cal
         ::transit/service-removed-dates (map service-date->inst service-removed-dates)
         ::transit/service-added-dates (map service-date->inst service-added-dates)))

(defn save-route [nap-config db user route]
  (let [r (-> route
              (modification/with-modification-fields ::transit/id user)
              (update ::transit/stops #(mapv stop-location-geometry %))
              (update ::transit/service-calendars #(mapv service-calendar-dates %)))]
    (log/debug "Save route: " r)
    (upsert! db ::transit/route r)))

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
       (save-route nap-config db user (http/transit-request form-data))))))

(defrecord Routes [nap-config]
  component/Lifecycle
  (start [{:keys [db http] :as this}]
    (assoc
      this ::stop
           [(http/publish! http (routes-auth db nap-config))]))
  (stop [{stop ::stop :as this}]
    (doseq [s stop]
      (s))
    (dissoc this ::stop)))
