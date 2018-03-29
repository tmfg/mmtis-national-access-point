(ns ote.gtfs.parse
  "Parse GTFS text files into Clojure data and back"
  (:require #?(:cljs [testdouble.cljs.csv :as csv]
               :clj [clojure.data.csv :as csv])
            [ote.gtfs.spec :as gtfs-spec]
            [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [ote.time :as time]
            #?(:cljs [goog.string :as gstr])
            [taoensso.timbre :as log]))

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

(defmethod gtfs->clj 'nat-int? [_ value]
  (#?(:cljs js/parseInt
      :clj Integer/parseInt) value))

(defmethod gtfs->clj 'double? [_ value]
  (#?(:cljs js/parseFloat
      :clj Double/parseDouble) value))

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

(defn- csv->string [rows]
  #?(:cljs (csv/write-csv rows)
     :clj (with-out-str
            (csv/write-csv *out* rows))))

(def file-info
  {:gtfs/agency-txt {:header gtfs-spec/agency-txt-header
                     :fields gtfs-spec/agency-txt-fields}
   :gtfs/stops-txt {:header gtfs-spec/stops-txt-header
                    :fields gtfs-spec/stops-txt-fields}
   :gtfs/routes-txt {:header gtfs-spec/routes-txt-header
                     :fields gtfs-spec/routes-txt-fields}
   :gtfs/trips-txt {:header gtfs-spec/trips-txt-header
                    :fields gtfs-spec/trips-txt-fields}
   :gtfs/stop-times-txt {:header gtfs-spec/stop-times-txt-header
                         :fields gtfs-spec/stop-times-txt-fields}
   :gtfs/calendar-txt {:header gtfs-spec/calendar-txt-header
                       :fields gtfs-spec/calendar-txt-fields}
   :gtfs/calendar-dates-txt {:header gtfs-spec/calendar-dates-txt-header
                             :fields gtfs-spec/calendar-dates-txt-fields}
   :gtfs/shapes-txt {:header gtfs-spec/shapes-txt-header
                     :fields gtfs-spec/shapes-txt-fields}})

(defn parse-gtfs-file [gtfs-file-type content]
  (let [[header & rows] (csv/read-csv content)
        {fields :fields} (file-info gtfs-file-type)
        allowed-fields (into #{} fields)
        content-fields (into []
                             (map #(keyword "gtfs"
                                            (-> %
                                                (str/replace #"\uFEFF" "")
                                                (str/replace #"_" "-"))))
                             header)]
    (when-let [unknown-fields (seq (filter (complement allowed-fields) content-fields))]
      (log/warn "GTFS file " gtfs-file-type " contains unknown fields: " unknown-fields))
    (mapv
     (fn [line]
       (into {}
             (remove nil?
                     (map (fn [field value]
                             (when-not (str/blank? value)
                               [field (gtfs->clj (field-spec-description field) value)]))
                           content-fields line))))
     (rest rows))))


(defn unparse-gtfs-file [gtfs-file-type content]
  (let [{:keys [header fields]} (file-info gtfs-file-type)]
    (try
      (str header "\n"
           (csv->string
             (mapv (fn [row]
                     (mapv #(clj->gtfs (field-spec-description %) (get row %))
                           fields))
                   content)))
      (catch #?(:cljs js/Object :clj Exception) e
        (log/warn "Error generating GTFS - unparse-gtfs-file error " e)
        (log/warn "Error generating GTFS - unparse-gtfs-file file type" gtfs-file-type)
        (log/warn "Error generating GTFS - unparse-gtfs-file content" content)))))
