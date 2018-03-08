(ns ote.util.zip
  "Generate and read zip files."
  #?(:cljs
     (:require [cljsjs.jszip]
               [cljsjs.filesaverjs]))
  #?(:clj
     (:import (java.util.zip ZipOutputStream))))

(defn write-zip
  "Write a zip file. Content is a sequence of file descriptors.
  Each file descriptor is a map containing the name and data.
  To is a (platform dependent) target. In the browser it is a file name
  to download to. On the JVM it is an output stream."
  [content to]
  #?(:cljs (let [zip (js/JSZip.)]
             (doseq [{:keys [name data]} content]
               (.file zip name data))
             (-> zip
                 (.generateAsync #js {:type "blob"})
                 (.then #(js/saveAs % to))))
     :clj (doseq [{:keys [name data]} content]
            (println "saving " name))))
