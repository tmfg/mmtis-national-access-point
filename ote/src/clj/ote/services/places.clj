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
            [specql.op :as op])
  (:import (java.net URLEncoder)))

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


(defrecord Places [sources]
  component/Lifecycle
  (start [{db :db http :http :as this}]
    (assoc this ::stop
           (http/publish!
            http
            (routes
             (GET "/places/:name" [name]
                  (http/transit-response
                   (search db name)))))))

  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
