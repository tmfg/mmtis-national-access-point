(ns ote.shapefile.reader
  "Read ESRI Shapefile with GeoTools"
  (:import (org.geotools.data Query)
           (org.geotools.data.crs ReprojectFeatureResults)
           (org.geotools.filter.text.cql2 CQL)
           (org.geotools.data.shapefile ShapefileDataStore)
           (java.io OutputStream)
           (org.opengis.feature FeatureVisitor)
           (org.opengis.util ProgressListener))
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [ote.geo :as geo]))


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

(def null-progress-listener
  (reify ProgressListener
    (complete [_])
    (dispose [_])
    (exceptionOccurred [_ t])
    (getDescription [_] "null-progress-listener")
    (getTask [_] nil)
    (isCanceled [_] false)
    (progress [_ pct])
    (setCanceled [_ c])
    (setDescription [_ d])
    (setTask [_ t])
    (started [_])
    (warningOccurred [_ source location warning])))

(defn feature-visitor [feature-fn]
  (reify org.opengis.feature.FeatureVisitor
    (visit [_ feature] (feature-fn feature))))

(defn features-to-geojson [^ShapefileDataStore shp
                           ^String cql-query-string
                           feature-fn
                           ^OutputStream to]
  (let [f (->filter cql-query-string)
        feature-source (.getFeatureSource shp)
        feature-collection (.getFeatures feature-source f)
        feature-collection-wgs84 (ReprojectFeatureResults.
                                  feature-collection geo/wgs84)
        json (org.geotools.geojson.feature.FeatureJSON.)]
    ;;(.accept feature-collection (feature-visitor feature-fn))

    #_(println "TÄÄLLÄ")
    #_(.writeFeatureCollection json feature-collection to)

    (.toString json feature-collection-wgs84)))
