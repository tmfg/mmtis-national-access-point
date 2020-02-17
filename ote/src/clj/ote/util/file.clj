(ns ote.util.file
  "Utilities for working with files and streams"
  (:require
    [clojure.java.io :refer [copy input-stream]]
    [ote.util.encrypt :as encrypt]
    [clojure.string :as str]))

(defn slurp-bytes
  "takes `arg` and returns a bytearray read from the path it points to. Throws an exception if target is missing."
  [arg]
  (with-open [out (java.io.ByteArrayOutputStream.)]
    (copy (input-stream arg) out)
    (.toByteArray out)))

(defn generate-s3-csv-key
  "We store these files to database temporarily and move them to more permanent place if or when service is saved.
  To make this process more robust and avoid all concurrency problems we generate random file-key that is based on
  the original filename."
  [filename]
  (let [filename (as-> filename f
                      (str/split f #"\s")
                       (str/join "" f))
        random-string (encrypt/random-string 8)
        file-key (str random-string "_" filename)]
    file-key))
