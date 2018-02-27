(ns ote.gtfs.spec
  "Define clojure.spec for GTFS data. All GTFS keys are under :gtfs namespace."
  (:require [clojure.spec.alpha :as s]))

(s/def :gtfs/gtfs (s/keys :req [:gtfs/agency-txt
                                :gtfs/stops-txt
                                :gtfs/routes-txt
                                :gtfs/trips-txt
                                :gtfs/stop-times-txt
                                :gtfs/calendar-txt]
                          :opt [:gtfs/calendar-dates-txt
                                :gtfs/fare-attributes-txt
                                :gtfs/fare-rules-txt
                                :gtfs/shapes-txt
                                :gtfs/frequencies-txt
                                :gtfs/transfers-txt
                                :gtfs/feed-info-txt]))

;;;;;;;;;;;;;;;;;;;;;;;
;; Spec for agency.txt

(s/def :gtfs/agency-txt
  (s/coll-of :gtfs/agency))

(s/def :gtfs/agency
  (s/keys :req [:gtfs/agency-name
                :gtfs/agency-url
                :gtfs/agency-timezone]
          :opt [:gtfs/agency-id
                :gtfs/agency-lang
                :gtfs/agency-phone
                :gtfs/agency-fare-url
                :gtfs/agency-email]))

(def ^{:doc "Defines the order of the CSV fields in an agency.txt file"}
  agency-txt-fields
  [:gtfs/agency-id :gtfs/agency-name :gtfs/agency-url :gtfs/agency-timezone
   :gtfs/agency-lang :gtfs/agency-phone :gtfs/agency-fare-url :gtfs/agency-email])

;; TODO: specs for individual agency fields

;;;;;;;;;;;;;;;;;;;;;;
;; Spec for stops.txt

(s/def :gtfs/stops-txt
  (s/coll-of :gtfs/stop))

(s/def :gtfs/stop
  (s/keys :req [:gtfs/stop-id
                :gtfs/stop-name
                :gtfs/stop-lat
                :gtfs/stop-lon]
          :opt [:gtfs/stop-code
                :gtfs/stop-desc
                :gtfs/zone-id
                :gtfs/stop-url
                :gtfs/location-type
                :gtfs/parent-station
                :gtfs/stop-timezone
                :gtfs/wheelchair-boarding]))

(def ^{:doc "Defines the order of the CSV fields in a stops.txt file"}
  stops-txt-fields
  [:gtfs/stop-id :gtfs/stop-code :gtfs/stop-name :gtfs/stop-desc
   :gtfs/stop-lat :gtfs/stop-lon :gtfs/zone-id :gtfs/stop-url :gtfs/location-type
   :gtfs/parent-station :gtfs/stop-timezone :gtfs/wheelchair-boarding])

(s/def :gtfs/wheelchair-boarding #{"0" "1" "2"})

;;;;;;;;;;;;;;;;;;;;;;;
;; Spec for routes.txt

(s/def :gtfs/routes-txt
  (s/coll-of :gtfs/route))

(s/def :gtfs/route
  (s/keys :req [:gtfs/route-id
                :gtfs/route-short-name
                :gtfs/route-long-name
                :gtfs/route-type]
          :opt [:gtfs/agency-id
                :gtfs/route-desc
                :gtfs/route-url
                :gtfs/route-color
                :gtfs/route-text-color
                :gtfs/route-sort-order]))

(def ^{:doc "Defines the order of the CSV fields in a routes.txt file"}
  routes-txt-fields
  [:gtfs/route-id :gtfs/agency-id :gtfs/route-short-name :gtfs/route-long-name
   :gtfs/route-desc :gtfs/route-type :gtfs/route-url :gtfs/route-color
   :gtfs/route-text-color :gtfs/route-sort-order])


;;;;;;;;;;;;;;;;;;;;;;
;; Spec for trips.txt

(s/def :gtfs/trips-txt
  (s/coll-of :gtfs/trip))

(s/def :gtfs/trip
  (s/keys :req [:gtfs/route-id
                :gtfs/service-id
                :gtfs/trip-id]
          :opt [:gtfs/trip-headsign
                :gtfs/trip-short-name
                :gtfs/direction-id
                :gtfs/block-id
                :gtfs/shape-id
                :gtfs/wheelchair-accessible
                :gtfs/bikes-allowed]))

(def ^{:doc "Defines the order of the CSV fields in a trips.txt file"}
  trips-txt-fields
  [:gtfs/route-id
   :gtfs/service-id
   :gtfs/trip-id
   :gtfs/trip-headsign
   :gtfs/trip-short-name
   :gtfs/direction-id
   :gtfs/block-id
   :gtfs/shape-id
   :gtfs/wheelchair-accessible
   :gtfs/bikes-allowed])

;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Spec for stop_times.txt

(s/def :gtfs/stop-times-txt
  (s/coll-of :gtfs/stop-time))

(s/def :gtfs/stop-time
  (s/keys :req  [:gtfs/trip-id
                 :gtfs/arrival-time
                 :gtfs/departure-time
                 :gtfs/stop-id
                 :gtfs/stop-sequence]
          :opt  [:gtfs/stop-headsign
                 :gtfs/pickup-type
                 :gtfs/drop-off-type
                 :gtfs/shape-dist-traveled
                 :gtfs/timepoint]))

(def ^{:doc "Defines the order of the CSV fields in a stop_times.txt file"}
  stop-times-txt-fields
  [:gtfs/trip-id
   :gtfs/arrival-time
   :gtfs/departure-time
   :gtfs/stop-id
   :gtfs/stop-sequence
   :gtfs/stop-headsign
   :gtfs/pickup-type
   :gtfs/drop-off-type
   :gtfs/shape-dist-traveled
   :gtfs/timepoint])


;;;;;;;;;;;;;;;;;;;;;;;;;
;; Spec for calendar.txt

(s/def :gtfs/calendar-txt
  (s/coll-of :gtfs/calendar))

(s/def :gtfs/calendar
  (s/keys :req [:gtfs/service-id
                :gtfs/monday
                :gtfs/tuesday
                :gtfs/wednesday
                :gtfs/thursday
                :gtfs/friday
                :gtfs/saturday
                :gtfs/sunday
                :gtfs/start-date
                :gtfs/end-date]))

(def ^{:doc "Defines the order of the CSV fields in a calendar.txt fiel"}
  calendar-txt-fields
  [:gtfs/service-id
   :gtfs/monday
   :gtfs/tuesday
   :gtfs/wednesday
   :gtfs/thursday
   :gtfs/friday
   :gtfs/saturday
   :gtfs/sunday
   :gtfs/start-date
   :gtfs/end-date])

(s/def :gtfs/monday boolean?)
(s/def :gtfs/tuesday boolean?)
(s/def :gtfs/wednesday boolean?)
(s/def :gtfs/thursday boolean?)
(s/def :gtfs/friday boolean?)
(s/def :gtfs/saturday boolean?)
(s/def :gtfs/sunday boolean?)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; FIXME: support optional files as well
