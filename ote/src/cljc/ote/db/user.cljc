(ns ote.db.user
  "Datamodel for user table"
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
            [ote.time :as time]
            [ote.util.fn :refer [flip]]
            [ote.db.transport-operator]
            [clojure.string :as str])
  #?(:cljs
     (:require-macros [ote.db.specql-db :refer [define-tables]])))

(define-tables
  ["user" ::user])


(defn password-valid? [password]
  (boolean (and (string? password)
                (<= 6 (count password) 32)
                (re-find #"[^\d]" password)
                (re-find #"\d" password))))

(defn email-valid? [email]
  (boolean (and (string? email)
                (>= (count email) 5)
                (re-find #"@" email)
                (re-find #"\." email))))
