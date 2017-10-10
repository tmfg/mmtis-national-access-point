(ns ote.services.places
  "Provides a service for the frontend to search for geographical places.
  Returns data as transit in as Clojure data (see `ote.geo` namespace for
  a definition of the geographical data)."
  (:require [ote.components.http :as http]
            [com.stuartsierra.component :as component]
            [compojure.core :refer [routes GET]]
            [clojure.string :as str]
            [taoensso.timbre :as log]
            [ote.shapefile.reader :as shp]
            [ote.geo :as geo]
            [ote.util.functor :refer [fmap]]
            [specql.core :as specql]
            [ote.db.places :as places]
            [specql.op :as op]
            [jeesql.core :refer [defqueries]])
  (:import (java.net URLEncoder)))

(defqueries "ote/services/places.sql")


(defn search [db name-prefix]
  (into []
        (comp
         ;; PENDING: we don't have other sources for now.
         ;; When we do, create a VIEW in the database at makes them
         ;; look the same.
         (map #(-> %
                   (assoc ::places/name (::places/namefin %)
                          ::places/id (::places/natcode %))
                   (dissoc ::places/namefin ::places/natcode)))
         (map #(update % ::places/location geo/to-clj)))
        (specql/fetch db ::places/finnish-municipalities
                      #{::places/namefin ::places/location ::places/natcode}
                      {::places/namefin (op/ilike (str name-prefix "%"))})))

(defn list-places [db]
  (into []
        (map #(-> %
                  (assoc ::places/name (::places/namefin %)
                         ::places/id (::places/natcode %))
                  (dissoc :places/namefin ::places/natcode)))
        (specql/fetch db ::places/finnish-municipalities
                      #{::places/namefin ::places/natcode}
                      {}
                      {::specql/order-by ::places/namefin})))

(defn link-places-to-transport-service!
  "Clear old place links and insert new links for the given transport service.
  Should be called within a transaction."
  [db transport-service-id place-references]
  (clear-transport-service-places! db {:transport-service-id transport-service-id})
  (doseq [{::places/keys [id name]} place-references]
    (link-transport-service-place! db {:transport-service-id transport-service-id
                                       :place-id id
                                       :name name})))

(defrecord Places [sources]
  component/Lifecycle
  (start [{db :db http :http :as this}]
    (assoc this ::stop
           (http/publish!
            http
            (routes
             (GET "/place-list" []
                  (http/transit-response
                   (list-places db)))
             (GET "/places/:name" [name]
                  {:status 200
                   :headers {"Content-Type" "application/json"}
                   :body (fetch-place-geojson-by-name db {:name name})})))))

  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
