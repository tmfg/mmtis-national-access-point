(ns ote.db.modification
  "Creation and modification metadata"
  (:require [ote.time :as time]))

(def ^{:doc "Creation/Modification metadata mapping that can be used for all tables."}
  modification-fields
  {"created" ::created
   "created-by" ::created-by
   "modified" ::modified
   "modified-by" ::modified-by})

(def modification-field-keys #{::created ::created-by ::modified ::modified-by})

#?(:clj
   (defn with-modification-fields [data id-field user]
     (let [now (java.sql.Timestamp. (System/currentTimeMillis))
           user-id (get-in user [:user :id])]
       (if (get data id-field)
         ;; data has id, update modification fields
         (assoc data
                ::modified now
                ::modified-by user-id)
         ;; data has no id, update creation fields
         (assoc data
                ::created now
                ::created-by user-id)))))
