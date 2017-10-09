(ns ote.db.places
  (:require [clojure.spec.alpha :as s]
            #?(:clj [ote.tietokanta.specql-db :refer [define-tables]])
            [specql.impl.registry]
            [specql.data-types])
  #?(:cljs (:require-macros [ote.tietokanta.specql-db :refer [define-tables]])))

(define-tables
  ["finnish_municipalities" ::finnish-municipalities])
