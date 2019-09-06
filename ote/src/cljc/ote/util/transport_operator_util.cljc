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