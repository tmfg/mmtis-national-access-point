(ns ote.db.gtfs
  "Datamodel for gtfs related tables"
  (:require [clojure.spec.alpha :as s]
            [ote.db.transport-operator :as transport-operator]
            [ote.db.transport-service :as transport-service]
            [ote.gtfs.spec]
            [ote.time :as time]
            [ote.gtfs.parse :as gtfs-parse]
            #?(:clj [ote.db.specql-db :refer [define-tables]])
            #?(:clj [specql.postgis])
            [specql.impl.registry]
            #?(:clj [specql.impl.composite :as composite])
            [specql.data-types]
            [clojure.string :as str])
  #?(:cljs
     (:require-macros [ote.db.specql-db :refer [define-tables]])))

(s/def :specql.data-types/int4range (s/keys))

(define-tables
  ["gtfs_package" :gtfs/package]
  ["gtfs-agency" :gtfs/agency]
  ["gtfs-route" :gtfs/route]
  ["gtfs-calendar" :gtfs/calendar]
  ["gtfs-calendar-date" :gtfs/calendar-date]
  ["route_shape" :gtfs/shape-info]
  ["gtfs-shape" :gtfs/shape]
  ["gtfs-stop-time-info" :gtfs/stop-time-info]
  ["gtfs-stop" :gtfs/stop]
  ["gtfs-transfer" :gtfs/transfer]
  ["gtfs-trip-info" :gtfs/trip-info]
  ["gtfs-trip" :gtfs/trip]

  ["gtfs-route-change-type" :gtfs/route-change-type (specql.transform/transform (specql.transform/to-keyword))] ;; ENUM
  ["gtfs-transit-changes" :gtfs/transit-changes]
  ["detected-route-change" :gtfs/detected-route-change]
  ["gtfs-route-hash" :gtfs/route-hash] ;; ENUM
  ["gtfs-date-hash" :gtfs/date-hash]
  ["gtfs_stoptime_display" :gtfs/stoptime-display] ;; ENUM
  ["detection-route" :gtfs/detection-route]
  ["detection-service-route-type" :gtfs/detection-service-route-type]
  ["hash-recalculation" :gtfs/hash-recalculation
   {"id" :gtfs/recalculation-id}]
  ["detected-change-history" :gtfs/detected-change-history]
  ["detection-holidays" :gtfs/detection-holidays]

  ["gtfs_import_report" :gtfs-import/report {:gtfs-import/package_id (specql.rel/has-one :gtfs-import/package_id :gtfs/package :gtfs/id)}])

#?(:clj
   (def ^:const int4range-pattern
     #"^(\(|\[)([^,]*),(.*)(\)|\])$"))

#?(:clj
   ;; As of 2018-08-30 specql doesn't support int4range
   (do
     (defmethod composite/parse-value "int4range" [_ string]
       (let [[m lower-type lower upper upper-type]
             (re-matches int4range-pattern string)]
         (when m
           (merge
            {:lower-inclusive? (= "[" lower-type)
             :upper-inclusive? (= "]" upper-type)}
            (when-not (str/blank? lower)
              {:lower (Integer/parseInt lower)})
            (when-not (str/blank? upper)
              {:upper (Integer/parseInt upper)})))))

     (defmethod composite/stringify-value "int4range" [_ {:keys [lower lower-inclusive?
                                                                 upper upper-inclusive?]}]
       (str (if lower-inclusive? "[" "(")
            lower ","
            upper
            (if upper-inclusive? "]" ")")))


     ;; bytea
     (defmethod composite/parse-value "bytea" [_ string]
         string)))


(defn date? [dt]
  (satisfies? time/DateFields dt))

(s/def :gtfs/start-date date?)
(s/def :gtfs/end-date date?)
(s/def :gtfs/date date?)
