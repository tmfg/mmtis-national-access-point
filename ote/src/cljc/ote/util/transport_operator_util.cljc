(ns ote.util.transport-operator-util
  "Util functions for transport operator"
  (:require
    [clojure.string :as string]
    [ote.db.transport-operator :as t-operator]))

(defn gtfs-file-name [operator]
  (let [operator-name (::t-operator/name operator)
        filename (string/lower-case (str
                                      (string/replace operator-name " " "_")
                                      "_gtfs.zip"))]
    filename))

(def every-postal-code-regex #"(?i)^[a-z0-9][a-z0-9\- ]{0,9}$")
(defn validate-every-postal-codes [code]
  (let [valid (re-matches every-postal-code-regex code)]
    (or valid false)))
