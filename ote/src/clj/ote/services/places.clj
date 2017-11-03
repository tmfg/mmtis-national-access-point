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


(defn place-completions
  "Return a list of completions that match the given search term."
  [db term]
  (into []
        (specql/fetch db ::places/places
                      #{::places/namefin ::places/id ::places/type}
                      {::places/namefin (op/ilike (str "%" term "%"))}
                      {::specql/order-by ::places/namefin})))

(defn place-by-id [db id]
  (first
   (specql/fetch db ::places/places
                 #{::places/namefin ::places/id ::places/type ::places/location}
                 {::places/id id})))

(defn link-places-to-transport-service!
  "Clear old place links and insert new links for the given transport service.
  Should be called within a transaction."
  [db transport-service-id places]
  (clear-transport-service-places! db {:transport-service-id transport-service-id})
  (doseq [{::places/keys [id namefin type] :as place} places]
    (if (= type "drawn")
      (insert-geojson-for-transport-service! db {:transport-service-id transport-service-id
                                                 :name namefin
                                                 :geojson (:geojson place)})
      (link-transport-service-place! db {:transport-service-id transport-service-id
                                         :place-id id
                                         :name namefin}))))

(defrecord Places [sources]
  component/Lifecycle
  (start [{db :db http :http :as this}]
    (assoc this ::stop
           (http/publish!
            http
            (routes
             (GET "/place-completions/:term" [term]
                  (http/transit-response
                   (place-completions db term)))
             (GET "/place/:id" [id]
                  {:status 200
                   :headers {"Content-Type" "application/json"}
                   :body (fetch-place-geojson-by-id db {:id id})})))))

  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
