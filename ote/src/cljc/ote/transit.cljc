(ns ote.transit
  "Application specific extensions to transit"
  (:require [cognitect.transit :as t]
            [ote.time :as time]
            [ote.geo :as geo]
            #?(:cljs [goog.date]))
  #?(:clj
     (:import (java.time.format DateTimeFormatter)
              (java.time LocalDate))))

(def write-options
  {:handlers
   {ote.time.Time (t/write-handler (constantly "time")
                                   time/format-time)
    ote.time.Interval (t/write-handler (constantly "interval")
                                       (partial into {}))
    #?@(:clj
        [org.postgresql.util.PGInterval
         (t/write-handler (constantly "interval")
                          time/pginterval->interval)

         org.postgis.PGgeometry
         (t/write-handler (constantly "geo")
                          geo/to-clj)

         java.time.LocalDate
         (t/write-handler (constantly "localdate")
                          (fn [^java.time.LocalDate d]
                            (.format DateTimeFormatter/ISO_LOCAL_DATE d)))])}})

(def read-options
  {:handlers {"time"
              #?(:clj (t/read-handler time/parse-time)
                 :cljs time/parse-time)

              "interval"
              #?(:clj (t/read-handler time/->PGInterval)
                 :cljs (comp time/map->Interval #(aget % 1)))

              ;; Transit "f" tag is an arbitrary precision decimal number that has no native
              ;; JS equivalent, for now we simply map it to parseFloat in JS as we are not doing
              ;; calculations with money
              ;; XXX: January 14 2022 update: We are now handling calculations with money, but this cannot be changed
              ;;      anymore since it would break a lot of the existing UI :)
              "f" #?(:clj (t/read-handler #(BigDecimal. %))
                     :cljs js/parseFloat)
              "geo" #?(:clj (t/read-handler identity)
                       :cljs identity)

              "localdate" #?(:clj (t/read-handler #(LocalDate/from
                                                    (.parse DateTimeFormatter/ISO_LOCAL_DATE %)))
                             :cljs goog.date/fromIsoString)}})

(defn clj->transit
  "Convert given Clojure `data` to transit+json string."
  [data]
  #?(:clj
     (with-open [out (java.io.ByteArrayOutputStream.)]
       (t/write (t/writer out :json write-options) data)
       (str out))

     :cljs
     (t/write (t/writer :json write-options) data)))

(defn transit->clj
  "Parse transit+json `in` to Clojure data."
  [in]
  #?(:clj
     (with-open [in (if (string? in)
                      (java.io.ByteArrayInputStream. (.getBytes in "UTF-8"))
                      in)]
       (t/read (t/reader in :json read-options)))

     :cljs
     (t/read (t/reader :json read-options) in)))
