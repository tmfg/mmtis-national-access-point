(ns ote.util.transport-operator-util
  "Util functions for transport operator"
  (:require
    [clojure.string :as string]
    [ote.db.transport-operator :as t-operator]))

(defn- export-file-name
  [operator suffix]
  (-> (::t-operator/name operator)
      (string/replace " " "_")
      (str suffix)
      string/lower-case))

(defn gtfs-file-name
  [operator]
  (export-file-name operator "_gtfs.zip"))

(defn gtfs-flex-file-name
  [operator]
  (export-file-name operator "_gtfs_flex.zip"))

(def every-postal-code-regex #"(?i)^(?=.{1,10}$)[a-z0-9][a-z0-9\- ]*[a-zA-Z0-9]$")
(defn validate-every-postal-codes [code]
  (let [valid (re-matches every-postal-code-regex code)]
    (or valid false)))
