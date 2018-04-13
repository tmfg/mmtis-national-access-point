(ns ote.db.places
  (:require [clojure.spec.alpha :as s]
            #?(:clj [ote.db.specql-db :refer [define-tables]])
            [specql.impl.registry]
            [specql.data-types])
  #?(:cljs (:require-macros [ote.db.specql-db :refer [define-tables]])))

(define-tables
  ["places" ::places])
