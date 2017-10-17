(ns ote.transit
  "Application specific extensions to transit"
  (:require [cognitect.transit :as t]
            [ote.time :as time]))

(def write-options
  {:handlers {ote.time.Time (t/write-handler (constantly "time")
                                             time/format-time)}})

(def read-options
  {:handlers {"time" #?(:clj (t/read-handler time/parse-time)
                        :cljs time/parse-time)}})

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
