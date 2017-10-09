(ns ote.geojson.passenger-transportation
  "Generate GeoJSON for passenger transportation service."
  (:require [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]
            [cheshire.core :refer [encode]]))

(defn- transport-operator-object
  "Return JSON object data for transport operator info."
  [{::t-operator/keys [name business-id phone homepage email id] :as transport-operator}]
  {:transport-operator transport-operator})

(defn- transport-service-object
  "Return JSON object data fro transport service info."
  [{::t-service/keys [type] :as service}]
  (assert (= type :passenger-transportation)
          (str "Trying to encode other service type as passenger-transportation, got: " type))
  {:transport-service service})

(defn- transport-service-feature [transport-service]
  {:properties
   (merge (transport-operator-object (::t-service/transport-operator transport-service))
          (transport-service-object transport-service))})

(defn to-geojson [transport-service]
  (encode 
   (merge
    {:type "FeatureCollection"
     :features [(transport-service-feature transport-service )]})
   {:key-fn name}))
