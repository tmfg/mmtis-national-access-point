(ns ote.services.openstreetmap
  "Provides a service for the frontend to do OSM Overpass API
  queries. Takes a Hiccup-style XML description and turns it into
  an Overpass API call. Returns data as transit."
  (:require [ote.components.http :as http]
            [com.stuartsierra.component :as component]
            [compojure.core :refer [routes GET]]
            [org.httpkit.client :as http-client]
            [clojure.zip :refer [xml-zip]]
            [clojure.data.zip.xml :as z]
            [clojure.xml :as xml]
            [clojure.string :as str]
            [taoensso.timbre :as log])
  (:import (java.net URLEncoder)))

(def place-types #{"city" "county" "town" "hamlet" "suburb" "village" "municipality"})

(defn- osm-query [api-endpoint osm-query]
  (with-open [in (-> api-endpoint
                     (str (URLEncoder/encode osm-query))
                     (http-client/get {:as :stream})
                     deref :body)]
    (xml-zip (xml/parse in))))

(defn- parse-osm-node [node]
  {:location {:lat (z/xml1-> node (z/attr :lat) #(Double/parseDouble %))
              :lon (z/xml1-> node (z/attr :lon) #(Double/parseDouble %))}
   :tags (into {}
               (zipmap (z/xml-> node :tag (z/attr :k) keyword)
                       (z/xml-> node :tag (z/attr :v))))})

(defn- parse-osm-response [root]
  (z/xml-> root :node parse-osm-node))

(defn search-places [api-endpoint name-prefix]
  (let [name (str "[name~\"" (str/escape name-prefix {\" "\\\""}) ".*\"]")
        query (str "node" name "[is_in=\"Finland\"]" ";out;")]
    (log/info "Running OpenStreetMap Overpass QL: " query)
    (parse-osm-response
     (osm-query api-endpoint query))))


(defrecord OpenStreetMap [api-endpoint]
  component/Lifecycle
  (start [{http :http :as this}]
    (assoc this ::stop
           (http/publish!
            http
            (routes
             (GET "/openstreetmap-places/:name" [name]
                  (http/transit-response
                   (search-places api-endpoint name)))))))
  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
