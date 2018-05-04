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


(comment
  (require '[specql.core :as specql])
  (require '[ote.gtfs.parse :as gtfs-parse])
  (require '[clojure.spec.alpha :as s])
  (def db (:db ote.main/ote))

  (def reitit (gtfs-parse/parse-gtfs-file :gtfs/routes-txt (slurp "/Users/markusva/Downloads/oulu_gtfs/routes.txt")))
  (doseq [r reitit] (specql/insert! db :gtfs/route (assoc r :gtfs/package-id 1)))

  (def agentit (gtfs-parse/parse-gtfs-file :gtfs/agency-txt (slurp "/Users/markusva/Downloads/oulu_gtfs/agency.txt")))
  (doseq [a agentit
          :when (seq a)]
    (specql/insert! db :gtfs/agency (assoc a :gtfs/package-id 1)))


  (def kalenterit (gtfs-parse/parse-gtfs-file :gtfs/calendar-txt (slurp "/Users/markusva/Downloads/oulu_gtfs/calendar.txt")))
  (doseq [k kalenterit] (specql/insert! db :gtfs/calendar (assoc k :gtfs/package-id 1)))

  (def kalenteri_paivat (gtfs-parse/parse-gtfs-file :gtfs/calendar-dates-txt (slurp "/Users/markusva/Downloads/oulu_gtfs/calendar_dates.txt")))
  (doseq [kp kalenteri_paivat] (specql/insert! db :gtfs/calendar-date (assoc kp :gtfs/package-id 1)))

  (def muodot (gtfs-parse/parse-gtfs-file :gtfs/shapes-txt (slurp "/Users/markusva/Downloads/oulu_gtfs/shapes.txt")))
  (doseq [m muodot] (specql/insert! db :gtfs/shape (assoc m :gtfs/package-id 1)))

  (def stoppi-ajat (gtfs-parse/parse-gtfs-file :gtfs/stop-times-txt (slurp "/Users/markusva/Downloads/oulu_gtfs/stop_times.txt")))
  (doseq [sa stoppi-ajat] (specql/insert! db :gtfs/stop-time (assoc sa :gtfs/package-id 1)))

  (def stoppit (gtfs-parse/parse-gtfs-file :gtfs/stops-txt (slurp "/Users/markusva/Downloads/oulu_gtfs/stops.txt")))
  (def stoppit (gtfs-parse/parse-gtfs-file :gtfs/stops-txt (slurp "/Users/markusva/Downloads/gtfs_8/stops.txt")))
  (doseq [s stoppit] (specql/insert! db :gtfs/stop (assoc s :gtfs/package-id 1)))
  )