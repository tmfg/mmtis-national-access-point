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
  ["group" ::group
   {"name" ::group-name
    "id" ::group-id}]

  ["transport-operator" ::transport-operator
   {::ckan-group (rel/has-one ::ckan-group-id ::group ::group-id)}])
