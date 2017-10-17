(ns ote.db.tx
  "Helper for transactions"
  (:require [clojure.java.jdbc :as jdbc]))

;; Bound to `true` when in a transaction.
(def ^:dynamic *in-transaction?* false)

(defmacro with-transaction
  "Start or join current db transaction. If no db transaction is in progress,
  starts a new one and runs `body` with `db-sym` bound to a connection.
  If a transaction is already in progress, runs `body` with same `db-sym`."
  [db-sym & body]
  `(if ote.db.tx/*in-transaction?*
     (do ~@body)
     (binding [ote.db.tx/*in-transaction?* true]
       (jdbc/with-db-transaction [~db-sym ~db-sym]
         ~@body))))
