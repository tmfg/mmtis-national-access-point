(ns ote.services.pre-notices.operator
  "Transport operator's routes for managing 60 day pre notices"
  (:require [ote.services.pre-notices.attachments :as attachment]
            [ote.components.http :as http]
            [ote.authorization :as authorization]
            [ote.db.transit :as transit]
            [ote.db.transport-operator :as t-operator]
            [specql.core :as specql]
            [compojure.core :refer [routes GET POST]]
            [specql.op :as op]
            [clojure.string :as str]
            [ote.db.tx :as tx]
            [ote.db.modification :as modification]
            [taoensso.timbre :as log]
            [ote.db.transport-operator :as t-operator]
            [jeesql.core :refer [defqueries]]))

(defqueries "ote/services/pre_notices/regions.sql")

(def pre-notice-columns #{::transit/id ::t-operator/id ::transit/regions
                          ::transit/pre-notice-type ::transit/pre-notice-state
                          ::transit/other-type-description ::transit/effective-dates
                          ::transit/route-description ::transit/url
                          [::transit/attachments #{::transit/attachment-file-name}]})

(defn get-operator-pre-notice [db user id]
  "Get singular operator pre-notice by id"
  (http/no-cache-transit-response
    (let [pre-notice (first (specql/fetch
             db ::transit/pre-notice
             pre-notice-columns
             {::t-operator/id (op/in (authorization/user-transport-operators db user))
              ::transit/id id}))
          pre-notice (assoc pre-notice :attachments (into [] (specql/fetch db ::transit/pre-notice-attachment
                                                                  (specql/columns ::transit/pre-notice-attachment)
                                                                  {::transit/pre-notice-id (::transit/id pre-notice)}
                                                                  {::specql/order-by ::transit/id
                                                                   :specql.core/order-direction :asc})))]
      pre-notice)))

(defn list-operator-notices [db user]
  (http/no-cache-transit-response
   (specql/fetch db ::transit/pre-notice
                 (specql/columns ::transit/pre-notice)
                 {::t-operator/id (op/in (authorization/user-transport-operators db user))})))

(defn- list-attachments-in-db [db user]
  (specql/fetch db ::transit/pre-notice-attachment
                (specql/columns ::transit/pre-notice-attachment)
                {::modification/created-by (authorization/user-id user)}))

(defn- delete-attachments [db config attachments]
  (doseq [{id ::transit/id} attachments]
    (specql/delete! db ::transit/pre-notice-attachment {::transit/id id}))
  (attachment/delete-from-s3 db config attachments))

(defn with-sent-field [notice]
  (if (= (::transit/pre-notice-state notice) :sent)
    (assoc notice ::transit/sent (java.sql.Timestamp. (System/currentTimeMillis)))
    notice))

(defn save-pre-notice [db user notice config]
  (let [attachments-from-db (list-attachments-in-db db user)
        attachments-ids-from-client (set (map ::transit/id (:attachments notice)))
        to-be-removed (filter #(not (contains? attachments-ids-from-client (::transit/id %)))
                              attachments-from-db)]
    (authorization/with-transport-operator-check
      db user (::t-operator/id notice)
      (fn []
        (tx/with-transaction db
          (let [form-attachments (:attachments notice)
                n (-> notice
                      (dissoc :attachments)
                      (dissoc ::transit/attachments)
                      (modification/with-modification-fields ::transit/id user)
                      (with-sent-field))
                saved-notice (specql/upsert! db ::transit/pre-notice n)
                notice-id (::transit/id saved-notice)]
            (specql/update! db ::transit/pre-notice-attachment
                            {::transit/pre-notice-id notice-id}
                            (op/and
                             {::transit/id              (op/in (mapv ::transit/id (:attachments notice)))
                              ::modification/created-by (authorization/user-id user)}))
            (delete-attachments db config to-be-removed)
            saved-notice))))))

(defn operator-pre-notices-routes
  "Routes for listing and creating pre notices for transport operators"
  [db config]
  (routes
   (GET "/pre-notices/list" {user :user}
        (list-operator-notices db user))
   (GET ["/pre-notices/:id" :id #"\d+"] {{id :id} :params
                             user :user}
     (get-operator-pre-notice db user (Long/parseLong id)))
   (POST "/pre-notice" {form-data :body
                        user :user}
         (http/transit-response
          (save-pre-notice db user (http/transit-request form-data) config)))
   (GET "/pre-notices/regions" {}
        (http/transit-response (fetch-regions db)))
   (GET "/pre-notices/region/:id" [id]
        {:status 200
         :headers {"Content-Type" "application/json"}
         :body (fetch-region-geometry db {:id id})})))
