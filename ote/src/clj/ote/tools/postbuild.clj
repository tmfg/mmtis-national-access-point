(ns ote.tools.postbuild
  "Hooks to run after build (before packaging)."
  (:require [ote.tools.git :refer [current-revision-sha]]
            [clojure.java.io :as io]))

(defn rename-ote-js []
  (.copy (io/file "resources/public/js/ote.js")
             (io/file (str "resources/public/js/ote-"
                           (:current-revision-sha
                            (current-revision-sha))
                           ".js"))))

(defn -main []
  (rename-ote-js))
