(ns ote.db.lock
  (:require [jeesql.core :refer [defqueries]])
  (:import (java.util UUID)))

(defqueries "ote/db/lock.sql")

(defmacro try-with-lock
  "Run body with lock, if it is not already locked."
  [db lock-name timelimit & body]
  `(let [db# ~db
         lock-name# ~lock-name
         timelimit# ~timelimit
         acquire# (acquire-lock db# {:id lock-name#
                                     :lock (str (UUID/randomUUID))
                                     :timelimit timelimit#})]
     (when acquire#
         (try
           ~@body
           (finally
             (release-lock db# {:id lock-name#}))))))
