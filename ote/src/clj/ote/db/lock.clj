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

(def ^:dynamic *exclusive-task-wait-ms* 5000)

(defmacro with-exclusive-lock
  "Run body with an exclusive lock on 1 node only. This is meant for tasks that are timed
  to run at a specific time. Waits `*exclusive-task-wait-ms*` milliseconds to make sure
  that all nodes have tried the lock.

  The parameters are the same `try-with-lock`."
  [db lock-name timelimit & body]
  `(try-with-lock
    ~db ~lock-name ~timelimit
    (try
      ~@body
      (finally
        ;; Sleep to ensure that no other nodes are able to run this task at the same.
        ;;
        ;; This is needed if the task is "too fast" and there is some clock skew on the nodes
        ;; so that they might fire the task at slightly different times. That would make it
        ;; possible for the first runner to release the lock before the next one tries to
        ;; acquire it. This sleep with the lock held prevents that.
        (Thread/sleep *exclusive-task-wait-ms*)))))
