(ns ote.services.pre-notices.operator
  "Transport operator's routes for managing 60 day pre notices"
  (:require [ote.components.http :as http]
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

(defn- parse-attachments
  [attachments db user]
  (let [saved-attachments (mapv
                            #(first (specql/fetch db ::transit/pre-notice-attachment
                                           (specql/columns ::transit/pre-notice-attachment)
                                                  (op/and
                                                  {::transit/attachment-file-name (::transit/attachment-file-name %)
                                                   ::modification/created-by (get-in user [:user :id])})))
                            attachments)]
    saved-attachments))

(defn get-operator-pre-notice [db user id]
  "Get singular operator pre-notice by id"
  (http/no-cache-transit-response
    (let [pre-notice (first (specql/fetch
             db ::transit/pre-notice
             (specql/columns ::transit/pre-notice)
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

(defn save-pre-notice [db user notice]
  (authorization/with-transport-operator-check
    db user (::t-operator/id notice)
    (fn []
      (tx/with-transaction db
         (let [attachment-names (:attachments notice)
               n (-> notice
                     (dissoc :attachments)
                     (modification/with-modification-fields ::transit/id user))
               saved-notice (specql/upsert! db ::transit/pre-notice n)
               notice-id (::transit/id saved-notice)
               attachments (parse-attachments attachment-names db user)
               ments (mapv #(assoc % ::transit/pre-notice-id notice-id) attachments)]
           (log/debug "Save notice: " saved-notice)
           (log/debug "Attachments: " attachments)
           (doall
             (mapv #(specql/upsert! db ::transit/pre-notice-attachment %)
                   ments)))))))

(defn operator-pre-notices-routes
  "Routes for listing and creating pre notices for transport operators"
  [db]
  (routes
   (GET "/pre-notices/list" {user :user}
        (list-operator-notices db user))
   (GET ["/pre-notices/:id" :id #"\d+"] {{id :id} :params
                             user :user}
     (get-operator-pre-notice db user (Long/parseLong id)))
   (POST "/pre-notice" {form-data :body
                        user :user}
         (http/transit-response
          (save-pre-notice db user (http/transit-request form-data))))
   (GET "/pre-notices/regions" {}
        (http/transit-response (fetch-regions db)))
   (GET "/pre-notices/region/:id" [id]
        {:status 200
         :headers {"Content-Type" "application/json"}
         :body (fetch-region-geometry db {:id id})})))
