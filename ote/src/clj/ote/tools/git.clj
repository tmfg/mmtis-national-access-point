(ns ote.tools.git
  "Tools for Git in build process"
  (:require [clojure.java.shell :as sh]
            [clojure.string :as str]))

(defmacro current-revision-sha []
  {:current-revision-sha (str/trim (:out (sh/sh "git" "rev-parse" "HEAD")))})
