(ns ote.db.lock
  (:require [jeesql.core :refer [defqueries]])
  (:import (java.util UUID)))

(defqueries "ote/db/lock.sql")

(defmacro try-with-lock
  "Run body with lock, if it is not already locked.
  If lock is already locked, body will not run and nil is returned.

  The lock-name is a short human readable name for the lock.

  Timelimit is the amount of seconds after which the lock is considered stale.
  If a lock is in a locked state but it was locked more than timelimit seconds ago,
  it will be acquired. This prevents a deadlock where one process crashes while
  holding the lock."
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
