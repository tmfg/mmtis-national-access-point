(ns ote.db.stats
  "Database configurations for common Types"
  (:require [clojure.spec.alpha :as s]
            #?(:clj [ote.db.specql-db :refer [define-tables]])
            [specql.rel :as rel]
            [specql.transform :as xf]
            [specql.impl.registry]
            [specql.data-types])
  #?(:cljs
     (:require-macros [ote.db.specql-db :refer [define-tables]])))

(define-tables
  ;; Enums
  ["stat_type" ::stat_type (specql.transform/transform (specql.transform/to-keyword))]

  ;; Tables
  ["stats-service" ::stats-service])
