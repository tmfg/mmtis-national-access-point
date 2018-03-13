(ns ote.gtfs.transform
  "Transform ote.db.transit datamodel maps to GTFS data"
  (:require [ote.db.transit :as transit]
            [ote.gtfs.spec :as gtfs-spec]
            [ote.gtfs.parse :as gtfs-parse]
            [ote.db.transport-operator :as t-operator]))

(defn- agency-txt [{::t-operator/keys [id name homepage phone email] :as transport-operator}]
  [{:gtfs/agency-id id ;; always just one agency
    :gtfs/agency-name name
    :gtfs/agency-url homepage
    :gtfs/agency-timezone "Europe/Helsinki"
    :gtfs/agency-lang "FI"
    :gtfs/agency-phone phone
    :gtfs/agency-email email}])

(defn routes-gtfs
  "Generate all supported GTFS files form given transport operator and route list"
  [transport-operator routes]
  [{:name "agency.txt"
    :data (gtfs-parse/unparse-gtfs-file :gtfs/agency-txt (agency-txt transport-operator))}])
