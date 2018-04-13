(ns ote.db.service-search
  "Transport service search facet tables"
  (:require [clojure.spec.alpha :as s]
            #?(:clj [ote.db.specql-db :refer [define-tables]])
            [specql.rel :as rel]
            [specql.transform :as xf]
            [specql.impl.registry]
            [ote.db.common]
            [specql.data-types]
            [ote.time]
            [ote.db.modification])
  #?(:cljs
     (:require-macros [ote.db.specql-db :refer [define-tables]])))


(define-tables
  ["operation-area-facet" ::operation-area-facet])
