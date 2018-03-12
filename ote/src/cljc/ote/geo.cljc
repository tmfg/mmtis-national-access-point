(ns ote.geo
  "Utilities for working with geographic information (and GeoTools in particular).
  Defines clojure.specs for Clojure data representation of geographic data,
  provides utilities to work with geometric data and on the backend provides
  protocols to convert between GeoTools/PostGIS/Clojure data."

  (:require [clojure.spec.alpha :as s])
  #?(:clj
     (:import (com.vividsolutions.jts.geom MultiPolygon Polygon Point LineString)
              (org.postgis
               PGgeometry))))


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

   ;; Extend to-clj to JTS geometry types
   (extend-protocol
       GeometryToClojure

     com.vividsolutions.jts.geom.Coordinate
     (to-clj [^com.vividsolutions.jts.geom.Coordinate c]
       [(.-x c) (.-y c)])

     com.vividsolutions.jts.geom.Point
     (to-clj [^com.vividsolutions.jts.geom.Point p]
       {:type :point :coordinates (to-clj (.getCoordinate p))})

     com.vividsolutions.jts.geom.Polygon
     (to-clj [^com.vividsolutions.jts.geom.Polygon p]
       {:type :polygon
        :coordinates (mapv to-clj (.getCoordinates p))})

     com.vividsolutions.jts.geom.LineString
     (to-clj [^com.vividsolutions.jts.geom.LineString p]
       {:type :line
        :coordinates (mapv to-clj (.getCoordinates p))})

     com.vividsolutions.jts.geom.MultiLineString
     (to-clj [^com.vividsolutions.jts.geom.MultiLineString p]
       {:type :multiline
        :lines (mapv to-clj (geometry-collection-seq p))})


     com.vividsolutions.jts.geom.MultiPolygon
     (to-clj [^com.vividsolutions.jts.geom.MultiPolygon mp]
       {:type :multipolygon
        :polygons (mapv to-clj (geometry-collection-seq mp))})))


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



;;; Conversion between WGS84 and Finnish EUREF-FIN systems

#?(:clj
   (def euref-wkt "PROJCS[\"EUREF_FIN_TM35FIN\", \n  GEOGCS[\"GCS_EUREF_FIN\", \n    DATUM[\"D_ETRS_1989\", \n      SPHEROID[\"GRS_1980\", 6378137.0, 298.257222101]], \n    PRIMEM[\"Greenwich\", 0.0], \n    UNIT[\"degree\", 0.017453292519943295], \n    AXIS[\"Longitude\", EAST], \n    AXIS[\"Latitude\", NORTH]], \n  PROJECTION[\"Transverse_Mercator\"], \n  PARAMETER[\"central_meridian\", 27.0], \n  PARAMETER[\"latitude_of_origin\", 0.0], \n  PARAMETER[\"scale_factor\", 0.9996], \n  PARAMETER[\"false_easting\", 500000.0], \n  PARAMETER[\"false_northing\", 0.0], \n  UNIT[\"m\", 1.0], \n  AXIS[\"x\", EAST], \n  AXIS[\"y\", NORTH]]"))

#?(:clj (def wgs84 org.geotools.referencing.crs.DefaultGeographicCRS/WGS84))
#?(:clj (def euref (org.geotools.referencing.CRS/parseWKT euref-wkt)))
#?(:clj (def euref->wgs84-transform (org.geotools.referencing.CRS/findMathTransform euref wgs84 true)))
#?(:clj (def swap-coordinates
          (reify com.vividsolutions.jts.geom.CoordinateFilter
            (filter [_ c]
              (let [x (.-x c)
                    y (.-y c)]
                (set! (.-x c) y)
                (set! (.-y c) x))))))

#?(:clj
   (defn euref->wgs84
     "Transform a coordinate from EUREF-FIN to WGS84 (GPS)."
     [geometry]
     (let [new-geometry
           (org.geotools.geometry.jts.JTS/transform geometry euref->wgs84-transform)]
       (.apply new-geometry swap-coordinates)
       new-geometry)))

(def ETRS-TM35FIN "EPSG:3067")
(def WGS84 "EPSG:4326")
