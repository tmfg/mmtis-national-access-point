(ns ote.geo
  "Utilities for working with geographic information (and GeoTools in particular).
  Defines clojure.specs for Clojure data representation of geographic data,
  provides utilities to work with geometric data and on the backend provides
  protocols to convert between GeoTools/PostGIS/Clojure data."

  (:require [clojure.spec.alpha :as s])
  #?(:clj
     (:import (org.postgis PGgeometry))))


;;; clojure.spec definitions for geometry data

(s/def ::single-coordinate (s/every number? :min-count 2 :max-count 2))
(s/def ::multiple-coordinates (s/every ::single-coordinate))
(s/def ::coordinates
  (s/or :single-coordinate ::single-coordinate
        :multiple-coordinates ::multiple-coordinates))
(s/def ::points (s/every ::single-coordinate))

(defmulti geometry-spec
  "Define spec by :type keyword."
  :type)

(s/def ::geometry
  (s/multi-spec geometry-spec :type))

(defmethod geometry-spec :point [_]
  (s/keys :req-un [::coordinates]))

(defmethod geometry-spec :geometry-collection [_]
  (s/keys :req-un [::geometries]))
(s/def ::geometries (s/every ::geometry))

(defmethod geometry-spec :multipolygon [_]
  (s/keys :req-un [::polygons]))
(s/def ::polygons (s/coll-of ::polygon))

(defmethod geometry-spec :polygon [_]
  ::polygon)
(s/def ::polygon (s/keys :req-un [::coordinates]))

(defmethod geometry-spec :multipoint [_]
  (s/keys :req-un [::coordinates]))

(defmethod geometry-spec :line [_]
  ::line)
(s/def ::line (s/keys :req-un [::coordinates]))

(defmethod geometry-spec :multiline [_]
  (s/keys :req-un [::lines]))

(s/def ::lines (s/every ::line))

(defmethod geometry-spec :circle [_]
  (s/keys :req-un [::coordinates ::radius]))


;;; Protocol for converting data to Clojure format

(defprotocol GeometryToClojure
  (to-clj [this] "Convert this geometry Java instance to equivalent Clojure data."))

;;; Conversion from GeoTools JTS to Clojure data

#?(:clj
   (defn- geometry-collection-seq
     ([gc] (geometry-collection-seq gc (.getNumGeometries gc) 0))
     ([gc len idx]
      (when (< idx len)
        (lazy-seq (cons (.getGeometryN gc idx)
                        (geometry-collection-seq gc len (inc idx))))))))


#?(:clj
   (defn point-coordinates [p]
     [(.x p) (.y p)]))

#?(:clj
   ;; Extend to-clj to work with Postgis geometry types

   (extend-protocol
       GeometryToClojure

     PGgeometry
     (to-clj [^PGgeometry g]
       (to-clj (.getGeometry g)))

     org.postgis.GeometryCollection
     (to-clj [^org.postgis.GeometryCollection gc]
       {:type :geometry-collection
        :geometries (into []
                          (map to-clj)
                          (.getGeometries gc))})

     org.postgis.MultiPolygon
     (to-clj [^org.postgis.MultiPolygon mp]
       {:type :multipolygon
        :polygons (mapv to-clj (seq (.getPolygons mp)))})

     org.postgis.Polygon
     (to-clj [^org.postgis.Polygon p]
       {:type :polygon
        :coordinates (mapv point-coordinates
                           (loop [acc []
                                  i 0]
                             (if (= i (.numPoints p))
                               acc
                               (recur (conj acc (.getPoint p i))
                                      (inc i)))))})

     org.postgis.Point
     (to-clj [^Point p]
       {:type :point
        :coordinates (point-coordinates p)})

     org.postgis.MultiPoint
     (to-clj [^org.postgis.MultiPoint mp]
       {:type :multipoint
        :coordinates (mapv to-clj (.getPoints mp))})


     org.postgis.LineString
     (to-clj [^org.postgis.LineString line]
       {:type :line
        :points (mapv point-coordinates (.getPoints line))})

     org.postgis.MultiLineString
     (to-clj [^org.postgis.MultiLineString mls]
       {:type :multiline
        :lines (mapv to-clj (.getLines mls))})


     ;; NULL database geometry is nil in Clojure
     nil
     (to-clj [_] nil)))



;; Conversion from KKJ to WGS84 (needed for Kalkati station positions)

#?(:clj
   ;; We need to define our own KKJ CRS because the one defined in geotools
   ;; has the wrong bursa wolf parameters (the TOWGS84 line below) that resulted
   ;; in wrong coordinates for kalkati stations (~10 - 15 meters off).
   ;; http://www.epsg-registry.org/
   (def kkj
          (org.geotools.referencing.CRS/parseWKT "PROJCS[\"KKJ / Finland Uniform Coordinate System\",
GEOGCS[\"KKJ\",DATUM[\"Kartastokoordinaattijarjestelma_1966\",
SPHEROID[\"International 1924\",6378388,297,AUTHORITY[\"EPSG\",\"7022\"]],
TOWGS84[-90.7, -106.1, -119.2, 4.09, 0.218, -1.05, 1.37],
AUTHORITY[\"EPSG\",\"6123\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4123\"]],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",27],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",3500000],PARAMETER[\"false_northing\",0],AUTHORITY[\"EPSG\",\"2393\"],AXIS[\"Y\",EAST],AXIS[\"X\",NORTH]]")))
#?(:clj (def wgs84 (org.geotools.referencing.CRS/decode "EPSG:4326")))
#?(:clj
   (def kkj->wgs84-transform
     (org.geotools.referencing.CRS/findMathTransform kkj wgs84 false)))


#?(:clj
   (defn kkj->wgs84
     "Transform a coordinate FROM KKJ to WGS84 (GPS).
  Y coordinate is North (P).
  X coordinate is East (I)"
     [{:keys [x y]}]
     (let [kkj-pos (org.geotools.geometry.DirectPosition2D. kkj x y)
           wgs84-pos (org.geotools.geometry.DirectPosition2D. wgs84)
           transformed (.transform kkj->wgs84-transform kkj-pos wgs84-pos)]
       transformed

       {:x (.-x transformed)
        :y (.-y transformed)})))
