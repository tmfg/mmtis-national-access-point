(ns ote.db.feature
  "Data model for dynamic feature flags"
  (:require
            #?@(:clj [[ote.db.specql-db :refer [define-tables]]
                      [specql.postgis]
                      [clj-time.coerce :as tc]]
                :cljs [[cljs-time.coerce :as tc]])
            [specql.impl.registry]
            [specql.data-types]
            [ote.db.common]
            [ote.db.user])

  #?(:cljs
     (:require-macros [ote.db.specql-db :refer [define-tables]])))

(define-tables
  ["feature_type" ::feature-type-enum (specql.transform/transform (specql.transform/to-keyword))]
  ["feature_variation" ::feature-variation])
