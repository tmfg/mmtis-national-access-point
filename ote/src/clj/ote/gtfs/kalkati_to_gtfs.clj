(ns ote.gtfs.kalkati-to-gtfs
  "Convert a Kalkati.net XML transit description to GTFS package"
  (:require [clojure.xml :as xml]
            [clojure.zip :refer [xml-zip]]
            [clojure.data.zip.xml :as z]
            [clojure.java.io :as io]
            [ote.geo :as geo]
            [ote.time :as time]
            [clj-time.coerce :as time-coerce]
            [ote.util.zip :as zip-file]
            [ring.util.io :as ring-io]
            [ote.gtfs.parse :as gtfs-parse]
            [ote.gtfs.spec :as gtfs-spec]
            [taoensso.timbre :as log]
            [ote.util.collections :refer [map-by]]))

(defn kalkati-zipper [input]
  (xml-zip
   (xml/parse input)))

(def
  ^{:doc
    "Convert a Kalkat.net transport mode to GTFS mode.
GTFS does not support all the same modes, so not all modes
can be mapped to GTFS.

Kalkati Transport modes
 1 - air
 2 - train
 21 - long/mid distance train
 22 - local train
 23 - rapid transit
 3 - metro
 4 - tramway
 5 - bus, coach
 6 - ferry
 7 - waterborne
 8 - private vehicle
 9 - walk
 10 - other

 GTFS Transport Modes
 0 - Tram, Streetcar, Light rail.
 1 - Subway, Metro.
 2 - Rail.
 3 - Bus.
 4 - Ferry.
 5 - Cable car.
 6 - Gondola, Suspended cable car.
 7 - Funicular."}
  kalkati-mode->gtfs-mode
  {"2" "2"
   "21" "2"
   "22" "0"
   "23" "2"
   "3" "1"
   "4" "0"
   "5" "3"
   "6" "4"
   "7" "4"})

(defn- double-attr [loc name]
  (Double/parseDouble (z/attr loc name)))

(defn- int-attr [loc name]
  (Integer/parseInt (z/attr loc name)))

(defn delivery
  "Return <Delivery> data as map. Mainly used as firstdate in calendar when footnote don't contain firstdate element."
  [kz]
  (let [del
        (first (z/xml-> kz :Delivery
                        (fn [d]
                          {:first-day (z/attr d :Firstday)
                           :last-day (z/attr d :Lastday)
                           :company-id (z/attr d :CompanyId)
                           :version (z/attr d :Version)})))]
    del))

(defn trans-modes-by-id [kz]
  (into {}
        (map (juxt :id identity))
        (z/xml-> kz :Trnsmode
                 (fn [m]
                   {:id (z/attr m :TrnsmodeId)
                    :name (z/attr m :Name)
                    :type (z/attr m :ModeType)}))))

(defn stations-by-id
  "Extract Kalkati stations and map them by id"
  [root]
  (map-by :id
          (z/xml-> root :Station
                   (fn [station]
                     {:id (z/xml1-> station (z/attr :StationId))
                      :name (z/xml1-> station (z/attr :Name))
                      :coordinate (geo/kkj->wgs84
                                   {:x (double-attr station :X)
                                    :y (double-attr station :Y)})}))))

(defn routes
  "Return all routes defined by the Kalkati file"
  [root calendars]
  (z/xml-> root :Timetbls :Service
           (fn [service]
             (let [;; Kalkati.net Timetbls Service can contain multiple footnotes. All of which are not valid.
                   ;; We take first and last stop from those footnotes
                   valid-footnote-list (z/xml-> service :ServiceValidity
                                                   (fn [servicevalidity]
                                                     {:footnote-id (int-attr servicevalidity :FootnoteId)
                                                      :first-stop (when (z/attr servicevalidity :Firststop)
                                                                    (Integer/parseInt (z/attr servicevalidity :Firststop)))
                                                      :last-stop (when (z/attr servicevalidity :Laststop)
                                                                   (Integer/parseInt (z/attr servicevalidity :Laststop)))}))
                   ;; Filter out all footnotes that are not in calendars list
                   valid-footnote-list (filter
                                            (fn [fid]
                                              (some
                                                (fn [c]
                                                  (= (:footnote-id fid) (Integer/parseInt c)))
                                                (map #(:id (second %)) calendars)))
                                            valid-footnote-list)]
                (map (fn [valid-footnote]
                       {:service-id (z/attr service :ServiceId)
                        :company-id (z/xml1-> service :ServiceNbr (z/attr :CompanyId))
                        :number (z/xml1-> service :ServiceNbr (z/attr :ServiceNbr))
                        :name (z/xml1-> service :ServiceNbr (z/attr :Name))
                        :variant (z/xml1-> service :ServiceNbr (z/attr :Variant))
                        :mode-id (z/xml1-> service :ServiceTrnsmode (z/attr :TrnsmodeId))
                        :validity-footnote-id (:footnote-id valid-footnote)
                        :footnote-variant-for-trip (str (:first-stop valid-footnote) "_" (:last-stop valid-footnote))
                        :stop-sequence (z/xml-> service :Stop
                                                (fn [stop]
                                                  (when (or
                                                          ;; no first or last stop restrictions
                                                          (and
                                                            (nil? (:first-stop valid-footnote))
                                                            (nil? (:last-stop valid-footnote)))
                                                          ;; First stop is given, so ensure that stop index is same or greater
                                                          (and
                                                            (not (nil? (:first-stop valid-footnote)))
                                                            (>= (int-attr stop :Ix) (:first-stop valid-footnote))
                                                            (nil? (:last-stop valid-footnote)))
                                                          ;; First and last stop are given, so ensure that stop index is same or greater than first stop
                                                          ;; and lesser or same as last stop
                                                          (and
                                                            (not (nil? (:first-stop valid-footnote)))
                                                            (not (nil? (:last-stop valid-footnote)))
                                                            (>= (int-attr stop :Ix) (:first-stop valid-footnote))
                                                            (<= (int-attr stop :Ix) (:last-stop valid-footnote)))
                                                          ;; only last stop is given, so ensure that stop index is same or lesser than last stop
                                                          (and
                                                            (nil? (:first-stop valid-footnote))
                                                            (not (nil? (:last-stop valid-footnote)))
                                                            (<= (int-attr stop :Ix) (:last-stop valid-footnote))))
                                                    {:stop-sequence (int-attr stop :Ix)
                                                     :station-id (z/attr stop :StationId)
                                                     :departure (z/attr stop :Departure)})))})
             valid-footnote-list)))))

(defn calendars
  "Return all calendars mapped by footnote id.
  For some reason Kalkati service calendars are called 'footnotes'."
  [root delivery]
  (map-by
   :id
   (z/xml-> root :Footnote
            (fn [c]
              (let [first-date (z/xml1-> c (z/attr :Firstdate) time/parse-date-iso-8601)
                    first-date (if first-date
                                 first-date
                                 ;; If first-date is nil we need to use delivery firstday
                                 (time/format-date-iso-8601 (time-coerce/to-date-time (:first-day delivery))))
                    date-vector (z/attr c :Vector)]
                ;; Kalkati.net format allows FootNote (basically the dates when there is traffic) to contain vectors
                ;; (= vector is list of 0 and 1 which indicates does the date contain traffic or not starting from first-date)
                ;; with only one value which is 0 (= no traffic) and without first-date.
                ;; These kind of calendar dates are not compatible with gtfs format.
                ;; So we need to filter out footnotes that doesn't give any traffic dates (vector=0)
                (when (not (and
                             (= 1 (count (vec date-vector))) ;; only one value in vector
                             (= \0 (first (vec date-vector))))) ;; First and only value is 0
                  (merge
                    {:id (z/attr c :FootnoteId)
                     :first-date first-date
                     :vector date-vector}
                    (when (and first-date date-vector)
                      {:dates (into #{}
                                    (remove
                                      nil?
                                      (map (fn [i valid?]
                                             ;; valid? is a character (\1 for valid, \0 for not valid)
                                             (when (= \1 valid?)
                                               (.plusDays first-date i)))
                                           (range) date-vector)))}))))))))

(defn companies-by-id [kz]
  (map-by
   :id
   (z/xml-> kz :Company
            (fn [c]
              {:id (z/attr c :CompanyId)
               :name (z/attr c :Name)}))))

(defn agency-txt [companies]
  (map (fn [{:keys [id name]}]
         {:gtfs/agency-id id
          :gtfs/agency-name name
          :gtfs/agency-url "http://example.com"
          :gtfs/agency-timezone "Europe/Helsinki"})
       (vals companies)))

(defn routes-txt [trans-modes routes]
  (map
   (fn [{:keys [id name mode-id company-id]}]
     (let [mode (trans-modes mode-id)]
       {:gtfs/route-id id
        :gtfs/agency-id company-id
        :gtfs/route-short-name ""
        :gtfs/route-long-name (str name
                                   (when (not= "N/A" (:name mode))
                                     (str " (" (:name mode) ")")))
        :gtfs/route-type (or (kalkati-mode->gtfs-mode (:type mode)) 3)}))
   routes))

(defn trips-txt [routes-with-trips]
  (mapcat
   (fn [{:keys [id trips]}]
     (mapcat
      (fn [[footnote-id trips]]
        (for [trip trips]
          {:gtfs/route-id id
           :gtfs/service-id footnote-id
           ;; Trip id is "t_<service-id>_<validity-footnote-id>_<footnote-variat>
           :gtfs/trip-id (str "t_" (:service-id trip) "_" footnote-id "_" (:footnote-variant-for-trip trip))}))
      (group-by :validity-footnote-id trips)))
   routes-with-trips))

(defn stops-txt [stations]
  (map (fn [{:keys [id name coordinate]}]
         {:gtfs/stop-id id
          :gtfs/stop-name name
          :gtfs/stop-lat (:y coordinate)
          :gtfs/stop-lon (:x coordinate)}) (vals stations)))

(defn routes-with-trips
  "Takes a list of kalkati routes and returns routes with ids and trips."
  [routes]
  (map
   (fn [i trips]
     (merge (select-keys (first trips) #{:company-id :number :name :mode-id})
            {:id (str "r_" i)
             :trips trips}))

   (drop 1 (range)) ; increasing id
   (vals (group-by (juxt :name :mode-id) routes))))

(defn calendar-dates-txt [calendars]
  (mapcat
   (fn [{:keys [id dates]}]
     (for [d dates]
       {:gtfs/service-id id
        :gtfs/date d
        :gtfs/exception-type "1"}))
   (vals calendars)))

(defn- gtfs-time [kalkati-time]
  (str (subs kalkati-time 0 2) ":" (subs kalkati-time 2) ":00"))

(defn stop-times-txt [routes-with-trips]
  (mapcat
   (fn [{:keys [id trips]}]
     (mapcat
      (fn [[footnote-id trips]]
        (mapcat
         (fn [{:keys [service-id stop-sequence] :as trip}]
           (for [{:keys [stop-sequence arrival departure station-id] :as stop} stop-sequence
                 :let [arr (when arrival (gtfs-time arrival))
                       dep (when departure (gtfs-time departure))]]
             {:gtfs/trip-id (str "t_" (:service-id trip) "_" footnote-id "_" (:footnote-variant-for-trip trip))
              :gtfs/arrival-time (or arr dep)
              :gtfs/departure-time (or dep arr)
              :gtfs/stop-sequence stop-sequence
              :gtfs/stop-id station-id}))
         trips))
      (group-by :validity-footnote-id trips)))
   routes-with-trips))

(defn kalkati->gtfs
  "Takes a parsed zipper of Kalkati.net XML and returns the equivalent
  contents as GTFS CSV-files. Returns a sequence of GTFS files as maps
  containing :name and :data keys."
  [kz]
  (let [trans-modes (trans-modes-by-id kz)
        delivery (delivery kz)
        calendars (calendars kz delivery)
        routes (routes-with-trips (routes kz calendars))
        stations (stations-by-id kz)]
    [{:name "agency.txt"
      :data (gtfs-parse/unparse-gtfs-file :gtfs/agency-txt (agency-txt (companies-by-id kz)))}
     {:name "routes.txt"
      :data (gtfs-parse/unparse-gtfs-file :gtfs/routes-txt (routes-txt trans-modes routes))}
     {:name "stops.txt"
      :data (gtfs-parse/unparse-gtfs-file :gtfs/stops-txt (stops-txt stations))}

     {:name "trips.txt"
      :data (gtfs-parse/unparse-gtfs-file :gtfs/trips-txt (trips-txt routes))}

     {:name "stop_times.txt"
      :data (gtfs-parse/unparse-gtfs-file :gtfs/stop-times-txt (stop-times-txt routes))}
     {:name "calendar_dates.txt"
      :data (gtfs-parse/unparse-gtfs-file :gtfs/calendar-dates-txt (calendar-dates-txt calendars))}]))

(defn kalkati->gtfs-zip-file
  "Takes a parsed zipper of Kalkati.net XML and an output stream.
  Writes the equivalent GTFS zip file to the output stream."
  [kz output]
  (try
    (zip-file/write-zip (kalkati->gtfs kz) output)
    (catch Exception e
      (log/error e "Kalkati.net to GTFS conversion failed."))))

(defn convert
  "Convert Kalkati.net zipped XML to GTFS zip file.
  Takes an input stream where the Kalkati.net zip can be read.
  Returns a piped input stream where the GTFS zip file can be read."
  [kalkati-zip-input]
  (let [kalkati-input-files (zip-file/read-zip kalkati-zip-input)
        lvm-xml (some #(when (= (:name %) "LVM.xml") %) kalkati-input-files)]
    (when-not lvm-xml
      (throw (ex-info "Unable to find LVM.xml file in zip input"
                      {:file-names (mapv :name kalkati-input-files)})))

    ;; Load the XML into a zip tree for easy parsing
    (let [kz (kalkati-zipper (java.io.ByteArrayInputStream. (.getBytes (:data lvm-xml) "UTF-8")))]
      (ring-io/piped-input-stream
       #(kalkati->gtfs-zip-file kz %)))))

(defn convert-bytes
  "Convert a byte array containing a Kalkati.net zipped XML to a GTFS zip file byte array."
  [byte-array]
  (let [out (java.io.ByteArrayOutputStream.)]
    (io/copy (convert (java.io.ByteArrayInputStream. byte-array))
             out)
    (.toByteArray out)))
