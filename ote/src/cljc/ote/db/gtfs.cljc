(ns ote.db.gtfs
  "Datamodel for gtfs related tables"
  (:require [clojure.spec.alpha :as s]
            [ote.gtfs.spec]
            [ote.time :as time]
            [ote.gtfs.parse :as gtfs-parse]
    #?(:clj [ote.db.specql-db :refer [define-tables]])
    #?(:clj [specql.postgis])
            [specql.impl.registry]
            [specql.data-types])
  #?(:cljs
     (:require-macros [ote.db.specql-db :refer [define-tables]])))

(define-tables
  ["gtfs_package" :gtfs/package]
  ["gtfs-agency" :gtfs/agency]
  ["gtfs-route" :gtfs/route]
  ["gtfs-calendar" :gtfs/calendar]
  ["gtfs-calendar-date" :gtfs/calendar-date]
  ["gtfs-shape" :gtfs/shape]
  ["gtfs-stop-time" :gtfs/stop-time]
  ["gtfs-stop" :gtfs/stop]
  ["gtfs-transfer" :gtfs/transfer]
  ["gtfs-trip" :gtfs/trip])

(defn date? [dt]
  (satisfies? time/DateFields dt))

(s/def :gtfs/start-date date?)
(s/def :gtfs/end-date date?)
(s/def :gtfs/date date?)
