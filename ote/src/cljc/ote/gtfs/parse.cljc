(ns ote.gtfs.parse
  "Parse GTFS text files into Clojure data and back"
  (:require #?(:cljs [testdouble.cljs.csv :as csv]
               :clj [clojure.data.csv :as csv])
            [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [ote.time :as time]
            #?(:cljs [goog.string :as gstr])))

(defmulti gtfs->clj (fn [spec-description value]
                      spec-description))

(defmulti clj->gtfs (fn [spec-description value]
                      spec-description))

(defmethod gtfs->clj :default [_ value] value)
(defmethod clj->gtfs :default [_ value] value)

(defmethod gtfs->clj 'boolean? [_ value]
  (= "1" value))

(defmethod clj->gtfs 'boolean? [_ value]
  (case value
    true "1"
    false "0"))

(defmethod gtfs->clj 'date? [_ value]
  (let [[_ y m d] (re-matches #"(\d{4})(\d{2})(\d{2})" value)]
    #?(:cljs (goog.date.Date. (js/parseInt y) (dec (js/parseInt m)) (js/parseInt d))
       :clj (java.time.LocalDate/of (Integer/parseInt y) (Integer/parseInt m) (Integer/parseInt d)))))

(defmethod clj->gtfs 'date? [_ value]
  (let [{::time/keys [year month date]} (time/date-fields value)]
    (#?(:cljs gstr/format :clj format) "%04d%02d%02d" year month date)))

(defmethod gtfs->clj 'time? [_ value]
  (time/parse-time value))

(defmethod clj->gtfs 'time? [_ value]
  (time/format-time-full value))

(def ^{:private true
       :doc "Memoized function for looking up a GTFS field spec (which may not exist).
This is only called with GTFS field names and cannot grow unbounded."}
  field-spec-description
  (memoize
   (fn [field]
     (try
       (s/describe field)
       (catch #?(:cljs js/Error
                 :clj Exception) e
         field)))))

(defn parse-gtfs-file [fields content]
  (mapv
   (fn [line]
     (into {}
           (remove nil?)
           (map (fn [field value]
                  (when-not (str/blank? value)
                    [field (gtfs->clj (field-spec-description field) value)]))
                fields line)))
   (csv/read-csv content)))

(defn- csv->string [rows]
  #?(:cljs (csv/write-csv rows)
     :clj (with-out-str
            (csv/write-csv *out* rows))))

(defn unparse-gtfs-file [fields content]
  (csv->string
   (mapv (fn [row]
           (mapv #(clj->gtfs (field-spec-description %) (get row %))
                 fields))
         content)))

;; (unparse-gtfs-file  ote.gtfs.spec/agency-txt-fields (parse-gtfs-file ote.gtfs.spec/agency-txt-fields "1,foo,urli,Europe/Helsinki,FI,123123,,tatu@emxapl.com"))

#_(unparse-gtfs-file  ote.gtfs.spec/calendar-txt-fields (parse-gtfs-file ote.gtfs.spec/calendar-txt-fields "1,1,0,1,0,0,1,1,20180101,20180228"))
