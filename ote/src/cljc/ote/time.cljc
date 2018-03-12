(ns ote.time
  "Common utilities for working with date and time information."
  (:require
   #?@(:clj [[specql.impl.composite :as specql-composite]
             [clj-time.format :as format]
             [clj-time.coerce :as coerce]
             [cheshire.generate :as cheshire-generate]
             [clj-time.core :as t]]
       :cljs [[goog.string :as gstr]
              [goog.date.Date]
              [goog.date.DateTime]
              [cljs-time.core :as t]
              [cljs-time.format :as format]
              [cljs-time.local :as local]
              [cljs-time.coerce :as coerce]])
   [specql.data-types :as specql-data-types]
   [clojure.spec.alpha :as s]
   [clojure.string :as str]))

;; Record for wall clock time (hours, minutes and seconds)
(defrecord Time [hours minutes seconds])

(s/def ::specql-data-types/time #(instance? Time %))

#?(:cljs
(defn format-timestamp-for-ui [time]
  (if  (nil? time)
    " " ;: if nil - print empty string
    (->> time
         t/to-default-time-zone
         (format/unparse (format/formatter "dd.MM.yyyy HH:mm"))))))

#?(:cljs
   (defn format-js-time [time]
     (if  (nil? time)
       "" ;: if nil - print empty string
       (->> time
            t/to-default-time-zone
            (format/unparse (format/formatter "HH:mm:ss"))))))

#?(:cljs
   (defn to-js-time [db-time]
     (let [hours (get db-time :hours)
           minutes (get db-time :minutes)
           seconds (get db-time :seconds)]
       (js/Date.
         (coerce/to-long (local/to-local-date-time
                           (t/today-at hours minutes seconds)))))))


(defn time? [x]
  (instance? Time x))

(defn empty-time?
  "Check if time is empty. Requires both hours and minutes to be set."
  [{:keys [hours minutes]}]
  (or (nil? hours)
      (nil? minutes)))

(def valid-time? (complement empty-time?))

(defn format-time-full [{:keys [hours minutes seconds]}]
  (#?(:clj format
      :cljs gstr/format)
   "%02d:%02d:%02d" hours minutes (or seconds 0)))

(defn format-time [{:keys [hours minutes seconds] :as time}]
  (if (and seconds (not= 0 seconds))
    (format-time-full time)
    (#?(:clj format
        :cljs gstr/format)
     "%02d:%02d" hours minutes)))

(defn parse-time [string]
  (let [[h m s] (map #(#?(:clj Integer/parseInt
                          :cljs js/parseInt) %)
                     (str/split string #":"))]
    (->Time h m s)))

(defprotocol DateFields
  (date-fields [this] "Return date fields as a map of data."))

(extend-protocol DateFields
   #?(:cljs js/Date :clj java.util.Date)
   (date-fields [this]
     {::date (.getDate this)
      ::month (inc (.getMonth this))
      ::year (+ 1900 (.getYear this))
      ::hours (.getHours this)
      ::minutes (.getMinutes this)
      ::seconds (.getSeconds this)}))

#?(:cljs
   (extend-protocol DateFields
     goog.date.Date
     (date-fields [this]
       {::date (.getDate this)
        ::month (inc (.getMonth this))
        ::year (.getYear this)}))
   :clj
   (extend-protocol DateFields
     java.time.LocalDate
     (date-fields [this]
       {::date (.getDayOfMonth this)
        ::month (.getMonthValue this)
        ::year (.getYear this)})))

(defn date-fields->date-time [{::keys [year date month hours minutes seconds]}]
  (t/date-time year month date hours minutes seconds))

(defn date-fields->date [{::keys [year date month]}]
  #?(:clj (java.time.LocalDate/of year month date)
     :cljs (goog.date.Date. year month date)))

(defn format-date
  "Format given date in human readable format."
  [date]
  (let [{::keys [date month year]} (date-fields date)]
    (#?(:cljs gstr/format :clj format)
     "%02d.%02d.%d" date month year)))

(defn format-date-iso-8601
  "Format given date in ISO-8601 format."
  [date]
  (let [{::keys [date month year]} (date-fields date)]
    (#?(:cljs gstr/format :clj format)
     "%d-%02d-%02d" year month date)))


;; Hook into specql to allow us to read/write the time
#?(:clj
   (defmethod specql-composite/parse-value "time" [_ string]
     (parse-time string)))

#?(:clj
   (defmethod specql-composite/stringify-value "time" [_ time]
     (when time
       (format-time time))))

;; Define a record that models a Postgres interval type on the frontend
;; Units less than second are expressed as fractional seconds (as in postgres)

(defrecord Interval [years months days hours minutes seconds])

(def empty-interval {:years 0
                     :months 0
                     :days 0
                     :hours 0
                     :minutes 0
                     :seconds 0.0})

(defn interval
  "Returns an interval of the given amount and unit.
  Example:
  `(interval 2 :hours)` returns an Interval record with
  hours set to 2 and all other fields set to zero."
  [amount unit]
  (map->Interval (merge empty-interval
                        {unit amount})))

#?(:clj
   (defn ->PGInterval [interval]
     (if (instance? org.postgresql.util.PGInterval interval)
       interval
       (let [{:keys [years months days hours minutes seconds]} interval]
         (org.postgresql.util.PGInterval.
          (int years) (int months) (int days) (int hours)
          (int minutes) (double seconds))))))

#?(:clj
   (defn pginterval->interval [^org.postgresql.util.PGInterval pg-interval]
     (map->Interval {:years (.getYears pg-interval)
                     :months (.getMonths pg-interval)
                     :days (.getDays pg-interval)
                     :hours (.getHours pg-interval)
                     :minutes (.getMinutes pg-interval)
                     :seconds (.getSeconds pg-interval)})))

(s/def :specql.data-types/interval
   (partial instance? #?(:clj org.postgresql.util.PGInterval
                         :cljs Interval)))

#?(:clj (def interval-fields-regex #"(\d+(\.\d+)?) (\w+)"))
#?(:clj
   (defmethod specql-composite/parse-value "interval" [_ string]
     (->PGInterval
      (map->Interval
       (merge
        {:years 0 :months 0 :days 0 :hours 0 :minutes 0 :seconds 0.0}
        (when-let [field-matches (re-seq interval-fields-regex string)]
          (reduce (fn [interval [_ amount _ unit]]
                    (assoc interval
                           (case unit
                             ("year" "years") :years
                             ("mon" "mons") :months
                             ("day" "days") :days
                             "hours" :hours
                             "mins" :minutes
                             "secs" :seconds)
                           (if (= unit "secs")
                             (Double/parseDouble amount)
                             (Integer/parseInt amount))))
                  {} field-matches))
        (when-let [match (re-matches #"(.* )?(\d+):(\d+):(\d+)" string)]
          (let [[_ _ h m s] match]
            {:hours (Integer/parseInt h)
             :minutes (Integer/parseInt m)
             :seconds (Double/parseDouble s)})))))))

(def iso-8601-period-pattern #"^P(\d+Y)?(\d+M)?(\d+D)?(T(\d+H)?(\d+M)?(\d+(\.\d+)?S)?)?$")

(defn interval->iso-8601-period [{:keys [years months days
                                         hours minutes seconds]}]
  (let [defined? #(and % (not (zero? %)))]
    (str "P"
         (when (defined? years) (str years "Y"))
         (when (defined? months) (str months "Y"))
         (when (defined? days) (str days "D"))
         (when (some defined? [hours minutes seconds])
           (str "T"
                (when (defined? hours) (str hours "H"))
                (when (defined? minutes) (str minutes "M"))
                (when (defined? seconds) (str seconds "M")))))))

(defn iso-8601-period->interval [period]
  (let [[_ years months days _ hours minutes seconds] (re-matches iso-8601-period-pattern period)]
    (map->Interval
     (merge empty-interval
            (into {}
                  (remove nil?)
                  (for [[value key] [[years :years] [months :months] [days :days]
                                     [hours :hours] [minutes :minutes] [seconds :seconds]]
                        :let [value (when value
                                      (subs value 0 (dec (count value))))]]
                    (when value
                      [key (if (= key :seconds)
                             (#?(:clj Double/parseDouble :cljs js/parseFloat) value)
                             (#?(:clj Integer/parseInt :cljs js/parseInt) value))])))))))

#?(:clj
   (defn pgtimestamp->ckan-timestring
     "CKAN uses unstandard format of ISO8601 called CKAN8601. It is in format 'yyyy-MM-ddTHH:mm:ss'.
     This format is required for e.g. last_updated or created fields in CKAN api payloads."
     [timestamp]
     (format/unparse (format/formatters :date-hour-minute-second)
                     (coerce/from-sql-time timestamp))))

#?(:clj
   (cheshire-generate/add-encoder
    org.postgresql.util.PGInterval
    (fn [interval json-generator]
      (cheshire-generate/encode-map (pginterval->interval interval) json-generator))))

(defn minutes-from-midnight [{:keys [minutes hours] :as time}]
  (+ (* 60 hours) minutes))

(defn minutes-from-midnight->time [minutes]
  (let [hours (int (/ minutes 60))
        minutes (- minutes (* 60 hours))]
    (->Time hours minutes 0)))

(defn minutes-elapsed [t1 t2]
  (- (minutes-from-midnight t2)
     (minutes-from-midnight t1)))

(defn date-range [start end]
  (lazy-seq
   (cons start
         (when (t/before? start end)
           (date-range (t/plus start (t/days 1)) end)))))

(def week-days [:monday :tuesday :wednesday :thursday :friday :saturday :sunday])

(defn day-of-week [dt]
  (case (t/day-of-week dt)
    1 :monday
    2 :tuesday
    3 :wednesday
    4 :thursday
    5 :friday
    6 :saturday
    7 :sunday))

#?(:cljs
   (defn js->date-time
     "Convert a JS Date object to cljs-time. Takes into account that
the JS date objects for timezones added to UTC (like Finnish UTC+2/+3) the date
part is the previous day.

For example: midnight 27 Feb 2018 => 26 Feb 2018 22:00"
     [js]
     (date-fields->date-time (date-fields js))))
