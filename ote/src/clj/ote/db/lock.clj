(ns ote.db.lock
  (:require [jeesql.core :refer [defqueries]]
            [taoensso.timbre :as log])
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

;; non-macro version of the above
(defn run-function [db lock-name do-fn]
  (try
    (do-fn)
    (catch Exception e
      (throw e))
    (finally
      (log/info "run-function with name: " lock-name))))

(defn try-with-lock-non-macro
  "Yritä ajaa annettu funktio lukon kanssa. Jos lukko on lukittuna, ei toimintoa ajeta.
  Palauttaa true jos toiminto ajettiin, false muuten.
  Huom! Vanhenemisaika täytyy aina antaa, jotta lukko ei jää virhetilanteessa ikuisesti kiinni."
  ([db lock-name do-fn] (try-with-lock-non-macro db lock-name *exclusive-task-wait-ms* do-fn))
  ([db lock-name timelimit do-fn]
   (let [_ (log/info "try-with-lock-non-macro :: lock-name" (pr-str lock-name) "timelimit:" timelimit ) al (acquire-lock db {:id lock-name
                              :lock (str (UUID/randomUUID))
                              :timelimit timelimit})]

     (if al
       (do
         (log/info (format "Lock: %s is not set. Run function." lock-name))
         (run-function db lock-name do-fn)
         true)
       (do
         (log/info (format "Lock: %s is set. Cannot run function." lock-name))
         false)))))