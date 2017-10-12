(ns ote.authorization
  "Common helpers for authorization. Does database checks based on user info."
  (:require [specql.core :as specql]
            [ote.db.transport-operator :as t-operator]
            [specql.op :as op]))

(defn user-transport-operators
  "Returns set of transport-operators the user belongs to (based on CKAN group membership)."
  [db {groups :groups :as user}]
  (into #{}
        (map ::t-operator/id)
        (specql/fetch db ::t-operator/transport-operator #{::t-operator/id}
                      {::t-operator/ckan-group-id (op/in (map :id groups))})))
