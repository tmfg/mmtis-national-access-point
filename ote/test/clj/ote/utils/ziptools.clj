(ns ote.utils.ziptools
  "ZIP creation utilities, meant mainly for dealing with GTFS zips in tests."
  (:require [clojure.java.io :as io])
  (:import [java.io File]
           [java.util.zip ZipEntry ZipOutputStream]
           ))

(defmacro ^:private with-entry
  [zip entry-name & body]
  `(let [^ZipOutputStream zip# ~zip]
     (.putNextEntry zip# (ZipEntry. ~entry-name))
     ~@body
     (flush)
     (.closeEntry zip#)))

(defn create
  "Repackage given set of file paths into a temporary zip file which is deleted on JVM exit.

   - `output-name` suffixing name of the resulting zip file
   - `files` map of target->source file names; target being name of file within the zip, source a simple resource lookup"
  [output-name files]
  (let [target (File/createTempFile "ote_gtfs_" (str "_" output-name))
        _      (.deleteOnExit target)]
    (with-open [output (ZipOutputStream. (io/output-stream target))]
      (for [f files]
        (with-open [input (io/input-stream f)]
          (with-entry output f (io/copy input output)))))
    target))
