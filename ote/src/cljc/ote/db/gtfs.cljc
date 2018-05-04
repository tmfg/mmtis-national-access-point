(ns ote.db.gtfs
  "Datamodel for gtfs related tables"
  (:require [clojure.spec.alpha :as s]
    #?(:clj [ote.db.specql-db :refer [define-tables]])
    #?(:clj [specql.postgis])
            [specql.impl.registry]
            [specql.data-types])
  #?(:cljs
     (:require-macros [ote.db.specql-db :refer [define-tables]])))

(define-tables
  ["gtfs_package" ::package])
