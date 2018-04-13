(ns ote.db.user
  "Datamodel for user table"
  (:require [clojure.spec.alpha :as s]
            #?(:clj [ote.tietokanta.specql-db :refer [define-tables]])
            #?(:clj [specql.postgis])
            [specql.rel :as rel]
            [specql.transform :as xf]
            [specql.impl.registry]
            [specql.data-types]
            [ote.db.common]
            [ote.db.modification]
            [ote.db.transport-service]
            [ote.time :as time]
            [ote.util.fn :refer [flip]]
            [ote.db.transport-operator])
  #?(:cljs
     (:require-macros [ote.tietokanta.specql-db :refer [define-tables]])))

(define-tables
  ["user" ::user])
