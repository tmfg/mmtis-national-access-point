(ns ote.tietokanta.specql-db
  "Specql DB määritys ajonaikaisia määrittelyjä varten."
  (:require [specql.core :as specql]
            [specql.transform :as xf]
            [specql.rel :as rel]))


(defmacro define-tables [& tables]
  `(specql/define-tables
     {:connection-uri "jdbc:postgresql://localhost/napotetest_template?user=postgres"}
     ~@tables))
