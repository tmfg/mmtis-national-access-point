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
            [ote.util.functor :refer [fmap]])
  (:import (java.net URLEncoder)))

(defprotocol PlaceSource
  (initialize [this]
    "Initialize this source. Called before first search is done.
    Returns updated version of the source.")
  (search-places-by-name [this name-prefix]
    "Search for places by a given name prefix."))

(defrecord ShapefilePlaceSource [url name-field shp]
  PlaceSource
  (initialize [this]
    ;; PENDING: we could do a search here to force loading
    (assoc this :shp (shp/shapefile-datastore (java.net.URL. url))))
  
  (search-places-by-name [{shp :shp} name-prefix]
    (let [;; FIXME: no way to bind parameters? we need to escape
          ;; or rather include only safe characters (alpha)
          cql (str name-field " LIKE '" name-prefix "%'")
          cql-filter (shp/->filter cql)]
      (into []
            (comp
             (map shp/feature-properties)
             (map #(select-keys % #{:the_geom :namefin :nameswe}))
             (map #(assoc % :geometry (geo/to-clj (geo/euref->wgs84 (:the_geom %)))))
             (map #(dissoc % :the_geom)))
            (shp/features shp cql-filter)))))

(defmulti create-source "Create place source by :type keyword" :type)

(defmethod create-source :shapefile [config]
  (map->ShapefilePlaceSource config))

(defn search [sources name-prefix]
  (fmap #(search-places-by-name % name-prefix) sources))


(defrecord Places [sources]
  component/Lifecycle
  (start [{http :http :as this}]
    ;; Create and initialize all place sources
    (let [sources (fmap (comp initialize create-source) sources)]
      (println "SOURCES: " (pr-str sources))
      (assoc this ::stop
             (http/publish!
              http
              (routes
               (GET "/places/:name" [name]
                    (http/transit-response
                     (search sources name))))))))
  
  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
