(ns ote.util.file
  "Utilities for working with files and streams"
  (:require
    [clojure.java.io :refer [copy input-stream]]))

(defn slurp-bytes
  "takes `arg` and returns a bytearray read from the path it points to. Throws an exception if target is missing."
  [arg]
  (with-open [out (java.io.ByteArrayOutputStream.)]
    (copy (input-stream arg) out)
    (.toByteArray out)))
