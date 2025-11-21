(ns ote.services.rdf.data
  "Data layer for RDF service - database queries with no RDF concerns."
  (:require [specql.core :as specql]
            [jeesql.core :refer [defqueries]]
            [ote.db.transport-service :as t-service]
            [ote.db.transport-operator :as t-operator]))

(defqueries "ote/integration/export/geojson.sql")
(defqueries "ote/services/service_search.sql")
(defqueries "ote/tasks/gtfs.sql")

(defn fetch-latest-published-service
  "Fetch the latest published service timestamp.
  Returns map with :published key or nil."
  [db]
  (first (latest-published-service db)))

(defn fetch-dataset-raw-data
  "Fetch raw dataset data from database (service record).
  Returns simple map with no RDF concerns."
  [db service-id]
  (first (specql/fetch db ::t-service/transport-service
                      (conj (specql/columns ::t-service/transport-service)
                            [::t-service/external-interfaces
                             (specql/columns ::t-service/external-interface-description)])
                      {::t-service/id service-id})))

(defn fetch-operator-data
  "Fetch raw operator data from database.
  Returns simple map with no RDF concerns."
  [db operator-id]
  (first (specql/fetch db ::t-operator/transport-operator
                      (specql/columns ::t-operator/transport-operator)
                      {::t-operator/id operator-id})))

(defn fetch-operation-areas-data
  "Fetch operation areas for a service from database.
  Returns sequence of maps with geojson, primary?, and feature-id."
  [db service-id]
  (seq (fetch-operation-area-for-service db {:transport-service-id service-id})))

(defn fetch-gtfs-validation-data
  "Fetch latest GTFS validation status for an interface.
  Returns map with validation status or nil."
  [db service-id interface-id]
  (when interface-id
    (first (fetch-latest-gtfs-vaco-status db {:service-id service-id
                                              :interface-id interface-id}))))

(defn fetch-service-data
  "Fetch all data needed for RDF generation for a given service.
   Returns a map with :service, :operation-areas, :operator, :validation-data, and :latest-publication."
  [db service-id]
  (let [service (fetch-dataset-raw-data db service-id)
        operation-areas (fetch-operation-areas-data db service-id)
        operator-id (:ote.db.transport-service/transport-operator-id service)
        operator (fetch-operator-data db operator-id)
        external-interfaces (:ote.db.transport-service/external-interfaces service)
        validation-data (into {} (for [interface external-interfaces]
                                   (let [interface-id (:ote.db.transport-service/id interface)]
                                     [interface-id (fetch-gtfs-validation-data db service-id interface-id)])))
        latest-publication (:published (fetch-latest-published-service db))]
    {:service service
     :operation-areas operation-areas
     :operator operator
     :validation-data validation-data
     :latest-publication latest-publication}))
