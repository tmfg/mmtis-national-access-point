(ns ote.db.tx
  "Helper for transactions"
  (:require [clojure.java.jdbc :as jdbc]
            [jeesql.core :refer [defqueries]]))

(defqueries "ote/db/transaction_lock.sql")

(defmacro with-transaction
  "Start or join current db transaction.
  Runs `body` with `db-sym` bound to a connection that is in transaction."
  [db-sym & body]
  `(jdbc/with-db-transaction [~db-sym ~db-sym]
                             ~@body))

(defmacro with-xact-advisory-lock
  "Try to get transaction level advisory lock.
  Runs `body` with advisory lock. This should be ran inside transaction."
  [db-sym & body]
  `(when (try-advisory-xact-lock! ~db-sym {:id 1})
     ~@body))
