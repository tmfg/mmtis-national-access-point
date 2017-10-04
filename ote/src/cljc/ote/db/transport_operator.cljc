(ns ote.db.transport-operator
  "Database configurations for Transport Operators"
  (:require [clojure.spec.alpha :as s]
    #?(:clj [ote.tietokanta.specql-db :refer [define-tables]])
            [ote.db.common :as common]
            [specql.rel :as rel]
            [specql.transform :as xf]
            [specql.impl.registry]
            [specql.data-types])
  #?(:cljs
     (:require-macros [ote.tietokanta.specql-db :refer [define-tables]])))

(define-tables
  ;; Tables
  ["transport-operator" ::transport-operator])

