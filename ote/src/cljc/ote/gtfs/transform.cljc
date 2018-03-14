(ns ote.gtfs.transform
  "Transform ote.db.transit datamodel maps to GTFS data"
  (:require [ote.db.transit :as transit]
            [ote.gtfs.spec :as gtfs-spec]
            [ote.gtfs.parse :as gtfs-parse]
            [ote.db.transport-operator :as t-operator]
            [taoensso.timbre :as log]
            [ote.time :as time]))

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

(defn- index-key
  "Add a key to all items of collection based on index position."
  [key val-fn coll]
  (map-indexed
   (fn [i item]
     (assoc item key (val-fn i)))
   coll))

(defn- index-of [pred vec]
  (first (keep-indexed
          (fn [i item]
            (when (pred item)
              i))
          vec)))

(defn- services-with-removed-dates
  "Returns GTFS services with removed dates"
  [services dates]
  (reduce
   (fn [services date]
     (let [df (select-keys (time/date-fields date) #{::time/year ::time/month ::time/date})
           service-idx (index-of #((:rule-dates %) df) services)])


     )


   ))
(defn route-services
  "Generate service ids from service calendars."
  [{::transit/keys [service-calendars id]}]
  (vec
   (for [{::transit/keys [service-rules service-added-dates service-removed-dates]
          service-calendar-id :service-calendar-id}
         (index-key :service-calendar-id str service-calendars)]
     ;; GTFS has 1 rule per service id, we need to create as many
     ;; services as there are rules, and distribute added/removed dates between them
     (vec
      (for [{::transit/keys [monday tuesday wednesday thursday
                             friday saturday sunday]
             :as service} (index-key :gtfs/service-id
                                     #(str id "_" service-calendar-id "_" %)
                                     service-rules)]
        {:rule-dates (into #{} (transit/rule-dates service))
         :gtfs/service-id (:gtfs/service-id service)
         :gtfs/monday (boolean monday)
         :gtfs/tuesday (boolean tuesday)
         :gtfs/wednesday (boolean wednesday)
         :gtfs/thursday (boolean thursday)
         :gtfs/friday (boolean friday)
         :gtfs/saturday (boolean saturday)
         :gtfs/sunday (boolean sunday)
         :gtfs/start-date (::transit/from-date service)
         :gtgs/end-date (::transit/to-date service)})))))

(defn- trips-txt [routes]
  (mapcat
   (fn [{::transit/keys [id trips service-calendars]}]
     (map-indexed
      (fn [i {::transit/keys [service-calendar-idx]}]
        (let [service-calendar (nth service-calendars service-calendar-idx)]
          )
        ))
     (fn [i time]
       {:gtfs/route-id 1
        :gtfs/service-id 1 ;; only 1 calendar
        :gtfs/trip-id i})))
  times)

(defn routes-gtfs
  "Generate all supported GTFS files form given transport operator and route list"
  [transport-operator routes]
  (doseq [r routes]
    (println "ROUTE-SERVICES: " (pr-str (route-services r))))
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
       #_{:name "trips.txt"
        :data (gtfs-parse/unparse-gtfs-file
               :gtfs/trips-txt
               (trips-txt routes))}
       ]
      (catch #?(:cljs js/Object :clj Exception) e
        (log/warn "Error generating GTFS file content" e)))))
