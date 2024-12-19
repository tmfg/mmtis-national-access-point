(ns ote.test-tools
  (:require [ote.test :refer :all]))

(defn get-transport-opertor-by-name [operator-name]
  (first (sql-query
           (format "SELECT id, name, \"business-id\", email FROM \"transport-operator\" WHERE name = '%s'" operator-name))))
