(ns ote.db.transit
  "Datamodel for pre-notices (advance notifications of transit route changes).
  Formerly also contained the Meri RAE route editor datamodel, removed in DPO-3689."
  (:require [clojure.spec.alpha :as s]
            #?@(:clj [[ote.db.specql-db :refer [define-tables]]
                      [specql.postgis]]
                :cljs [])

            [specql.rel :as rel]
            [specql.transform :as xf]
            [specql.impl.registry]
            [specql.data-types]
            [ote.db.common]
            [ote.db.modification]
            [ote.db.transport-service]
            [ote.db.transport-operator]
            [ote.db.user]
            [taoensso.timbre :as log])
  #?(:cljs
     (:require-macros [ote.db.specql-db :refer [define-tables]])))

(define-tables
  ["localized_text" :ote.db.transport-service/localized_text]
  ["pre_notice_type" ::pre_notice_type (specql.transform/transform (specql.transform/to-keyword))]
  ["pre_notice_state" ::pre_notice_state (specql.transform/transform (specql.transform/to-keyword))]
  ["notice_effective_date" ::notice-effective-date]
  ["pre_notice_comment" ::pre-notice-comment
   ote.db.modification/modification-fields
   {::author (specql.rel/has-one :ote.db.modification/created-by
                                 :ote.db.user/user
                                 :ote.db.user/id)}]
  ["pre_notice" ::pre-notice
   ote.db.modification/modification-fields
   {"transport-operator-id" :ote.db.transport-operator/id
    ::attachments (specql.rel/has-many ::id
                                       ::pre-notice-attachment
                                       ::pre-notice-id)
    ::comments (specql.rel/has-many ::id
                                    ::pre-notice-comment
                                    ::pre-notice-id)
    :ote.db.transport-operator/transport-operator (specql.rel/has-one :ote.db.transport-operator/id
                                                                      :ote.db.transport-operator/transport-operator
                                                                      :ote.db.transport-operator/id)}]
  ["pre_notice_attachment" ::pre-notice-attachment
   ote.db.modification/modification-fields])
