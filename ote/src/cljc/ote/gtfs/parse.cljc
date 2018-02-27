(ns ote.gtfs.parse
  "Parse GTFS text files into Clojure data and back"
  (:require #?(:cljs [testdouble.cljs.csv :as csv]
               :clj [clojure.data.csv :as csv])
            [clojure.string :as str]
            [clojure.spec.alpha :as s]))

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

;; (parse-gtfs-file ote.gtfs.spec/agency-txt-fields "1,foo,urli,Europe/Helsinki,FI,123123,,tatu@emxapl.com")
