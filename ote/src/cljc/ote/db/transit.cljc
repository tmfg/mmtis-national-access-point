(ns ote.db.transit
  "Datamodel for route based transit"
  (:require [clojure.spec.alpha :as s]
            #?(:clj [ote.tietokanta.specql-db :refer [define-tables]])
            [specql.rel :as rel]
            [specql.transform :as xf]
            [specql.impl.registry]
            [specql.data-types]
            [ote.db.common]
            [ote.db.modification])
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
   ote.db.modification/modification-fields])
