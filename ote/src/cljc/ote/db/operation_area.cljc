(ns ote.db.operation-area
  "Operation area defines the area (primary and secondary) where the service
  operates and is available in."
    (:require [clojure.spec.alpha :as s]
              #?(:clj [ote.tietokanta.specql-db :refer [define-tables]])
              [specql.rel :as rel]
              [specql.transform :as xf]
              [specql.impl.registry]
              [ote.db.common]
              [ote.db.transport-service]
              [specql.data-types]
              [ote.time])
    #?(:cljs (:require-macros [ote.tietokanta.specql-db :refer [define-tables]])))


(define-tables
  ["localized_text" :ote.db.transport-service/localized_text]
  ["operation_area" ::operation-area])
