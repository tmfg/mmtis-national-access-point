(ns ote.gtfs.transform
  "Transform ote.db.transit datamodel maps to GTFS data"
  (:require [ote.db.transit :as transit]
            [ote.gtfs.spec :as gtfs-spec]
            [ote.gtfs.parse :as gtfs-parse]
            [ote.db.transport-operator :as t-operator]
            [taoensso.timbre :as log]))

(defn- agency-txt [{::t-operator/keys [id name homepage phone email] :as transport-operator}]
  [{:gtfs/agency-id id
    :gtfs/agency-name name
    :gtfs/agency-url homepage
    :gtfs/agency-timezone "Europe/Helsinki"
    :gtfs/agency-lang "FI"
    :gtfs/agency-phone phone
    :gtfs/agency-email email}])

(defn- stops-txt [stops]
  (for [[id {::transit/keys [name location stop-type]}] stops]
    {:gtfs/stop-id id
     :gtfs/stop-name name
     :gtfs/stop-lat (.-x (.getGeometry location))
     :gtfs/stop-lon (.-y (.getGeometry location))}))

(defn- routes-txt [transport-operator-id routes]
  (for [{::transit/keys [id name route-type]} routes]
    {:gtfs/route-id id
     :gtfs/route-short-name name
     :gtfs/route-type (case route-type
                        :light-rail "0"
                        :subway "1"
                        :rail "2"
                        :bus "3"
                        :ferry "4"
                        :cable-car "5"
                        :gondola "6"
                        :funicular "7")
     :gtfs/agency-id transport-operator-id}))

(defn routes-gtfs
  "Generate all supported GTFS files form given transport operator and route list"
  [transport-operator routes]
  (let [stops-by-code (into {}
                            (comp (mapcat ::transit/stops)
                                  (map (juxt ::transit/code identity)))
                            routes)]
    (try
      [{:name "agency.txt"
        :data (gtfs-parse/unparse-gtfs-file :gtfs/agency-txt (agency-txt transport-operator))}
       {:name "stops.txt"
        :data (gtfs-parse/unparse-gtfs-file :gtfs/stops-txt (stops-txt stops-by-code))}

       {:name "routes.txt"
        :data (gtfs-parse/unparse-gtfs-file
               :gtfs/routes-txt
               (routes-txt (::t-operator/id transport-operator) routes))}
       ]
      (catch #?(:cljs js/Object :clj Exception) e
        (log/warn "Error generating GTFS file content" e)))))
