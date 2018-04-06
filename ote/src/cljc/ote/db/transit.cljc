(ns ote.db.transit
  "Datamodel for route based transit"
  (:require [clojure.spec.alpha :as s]
            #?(:clj [ote.tietokanta.specql-db :refer [define-tables]])
            #?(:clj [specql.postgis])
            [specql.rel :as rel]
            [specql.transform :as xf]
            [specql.impl.registry]
            [specql.data-types]
            [ote.db.common]
            [ote.db.modification]
            [ote.time :as time])
  #?(:cljs
     (:require-macros [ote.tietokanta.specql-db :refer [define-tables]])))

(define-tables
  ["transit_agency" ::agency]
  ["transit_stop_type" ::stop-type-enum (specql.transform/transform (specql.transform/to-keyword))]
  ["transit_route_type" ::route-type-enum (specql.transform/transform (specql.transform/to-keyword))]
  ["transit_stop" ::stop]
  ["transit_service_rule" ::service-rule]
  ["transit_service_calendar" ::service-calendar]
  ["transit_stopping_type" ::stopping-type (specql.transform/transform (specql.transform/to-keyword))]
  ["transit_stop_time" ::stop-time]
  ["transit_trip" ::trip]
  ["transit_route" ::route
   ote.db.modification/modification-fields]

  ["finnish_ports" ::finnish-ports
   ote.db.modification/modification-fields])

(def rule-week-days [::monday ::tuesday ::wednesday ::thursday
                     ::friday ::saturday ::sunday])

(defn rule-dates
  "Evaluate a recurring schedule rule. Returns a sequence of dates."
  [{::keys [from-date to-date] :as rule}]
  (let [week-days (into #{}
                        (keep #(when (get rule (keyword "ote.db.transit" (name %))) %))
                        time/week-days)]
    (when (and from-date to-date (not (empty? week-days)))
      (for [d (time/date-range (time/native->date-time from-date)
                               (time/native->date-time to-date))
            :when (week-days (time/day-of-week d))]
        (select-keys
         (time/date-fields d)
         #{::time/year ::time/month ::time/date})))))

(defn time-to-24h
  "Convert a time entry to stay within 24h (stop times may be greater)."
  [time]
  (update time :hours mod 24))
