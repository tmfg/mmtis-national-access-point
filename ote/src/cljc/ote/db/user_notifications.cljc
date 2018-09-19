(ns ote.db.user-notifications
  "Datamodel for user notification regions table"
  (:require [clojure.spec.alpha :as s]
    #?(:clj [ote.db.specql-db :refer [define-tables]])
    #?(:clj [specql.postgis])
            [specql.rel :as rel]
            [specql.transform :as xf]
            [specql.impl.registry]
            [specql.data-types]
            [ote.db.common]
            [ote.db.modification]
            [ote.db.transport-service]
            [ote.util.fn :refer [flip]]
            [ote.db.transport-operator])
  #?(:cljs
     (:require-macros [ote.db.specql-db :refer [define-tables]])))

(define-tables
  ["user_notifications" ::user-notifications])
