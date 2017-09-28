(ns ote.shapefile.reader
  "Read ESRI Shapefile with GeoTools"
  (:import (org.geotools.data Query)
           (org.geotools.filter.text.cql2 CQL)
           (org.geotools.data.shapefile ShapefileDataStore))
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))


(defn shapefile-datastore [url]
  (ShapefileDataStore. url))

(defn read-shapefile [file]
  (-> file io/as-url shapefile-datastore))

(defn- to-iterator [feature-iterator]
  (let [closed? (volatile! false)]
    (reify java.util.Iterator
      (hasNext [_]
        (if @closed?
          false
          (let [more? (.hasNext feature-iterator)]
            (when-not more?
              (.close feature-iterator)
              (vreset! closed? true))
            more?)))
      (next [_]
        (when @closed?
          (throw (IllegalStateException. "Feature iterator has already been closed")))
        (.next feature-iterator)))))

(defn ->filter
  "Create a Filter instance from a CQL query string that can be used to filter shapefile features."
  [cql-query-string]
  (CQL/toFilter cql-query-string))

(defn features
  "Returns a lazy sequence of all features in the given shapefile. Optionally filter the features
  by applying the given `filter`."
  ([shp] (features shp nil))
  ([shp filter]
   (let [feature-source (-> shp .getFeatureSource)
         feature-collection (if filter
                              (.getFeatures feature-source filter)
                              (.getFeatures feature-source))]
     (-> feature-collection .features to-iterator iterator-seq))))

(defn feature-properties
  "Returns the properties of the given `feature` as a Clojure map."
  [feature]
  (into {}
        (map (juxt #(-> % .getName .getLocalPart str/lower-case keyword) #(.getValue %)))
        (.getProperties feature)))

(defn query [shp cql]
  (let [q (Query.)] ))
