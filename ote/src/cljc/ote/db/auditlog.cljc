(ns ote.db.auditlog
  "Database configurations for Auditlog"
  (:require [clojure.spec.alpha :as s]
    #?(:clj [ote.db.specql-db :refer [define-tables]])
            [specql.data-types]
    #?(:clj [specql.impl.composite :as specql-composite]))
    #?(:cljs
        (:require-macros [ote.db.specql-db :refer [define-tables]])))


(define-tables
  ;; Define ENUMs
  ["auditlog_event_type" ::auditlog-event-type (specql.transform/transform (specql.transform/to-keyword))]

  ;; UDT tyypit
  ["auditlog_event_attribute" ::auditlog-event-attribute]

  ;; Tables
  ["auditlog" ::auditlog])

;; Create order for auditlog_event_type
(def auditlog-event-types [:delete-service :modify-service :add-service :delete-operator
                           :modify-operator :add-operator :delete-user :modify-user :add-user
                           :add-member-to-operator :add-user-to-operator :remove-member-from-operator])
