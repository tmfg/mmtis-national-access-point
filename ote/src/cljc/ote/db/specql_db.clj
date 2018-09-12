(ns ote.db.specql-db
  "Specql DB määritys ajonaikaisia määrittelyjä varten."
  (:require [specql.core :as specql]
            [specql.transform :as xf]
            [specql.rel :as rel]
            [ote.db.modification]))

(def db {:connection-uri "jdbc:postgresql://localhost/napotetest_template?user=napotetest"})

(defmacro define-tables [& tables]
  `(specql/define-tables
     {:connection-uri "jdbc:postgresql://localhost/napotetest_template?user=napotetest"}
     ~@tables))
