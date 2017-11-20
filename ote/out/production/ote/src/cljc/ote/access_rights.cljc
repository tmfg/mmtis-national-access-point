(ns ote.access-rights
  "Utility functions to check if user has access for requested action")

(defn user-has-rights-to-save-service? [operator service]
  "Check if user has access rights to save service for the selected operator"
  (if (= (get operator :ote.db.transport-operator/id) (get service :ote.db.transport-service/transport-operator-id)) true false))