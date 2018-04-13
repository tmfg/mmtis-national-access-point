(ns ote.services.pre-notices.authority
  "Pre notice services for transport authority users"
  (:require [ote.components.http :as http]
            [compojure.core :refer [routes POST GET]]
            [specql.core :as specql]
            [ote.db.transit :as transit]
            [ote.db.modification :as modification]
            [ote.db.user :as user]
            [ote.authorization :as authorization]
            [ote.db.transport-operator :as t-operator]))


(defn list-published-notices [db user]
  (specql/fetch db ::transit/pre-notice
                #{::transit/id
                  ::modification/created
                  ::transit/route-description
                  ::transit/pre-notice-type
                  [::t-operator/transport-operator #{::t-operator/name}]}
                {}
                {:specql.core/order-by ::modification/created
                 :specql.core/order-direction :desc}))

(def comment-columns  (conj (specql/columns ::transit/pre-notice-comment)
                            [::transit/author #{::user/fullname ::user/email}]))

(defn fetch-notice [db id]
  (first
   (specql/fetch db ::transit/pre-notice
                 (conj (specql/columns ::transit/pre-notice)
                       [::transit/comments comment-columns])
                 {::transit/id id})))

(defn add-comment [db user {:keys [id comment] :as form-data}]
  (let [inserted (specql/insert! db ::transit/pre-notice-comment
                                 (modification/with-modification-fields
                                   {::transit/comment comment
                                    ::transit/pre-notice-id id}
                                   ::transit/id user))]

    ;; Re-fetch the inserted comment so that we get the joined info as well
    (first
     (specql/fetch db ::transit/pre-notice-comment
                   comment-columns
                   {::transit/id (::transit/id inserted)}))))

(defn authority-pre-notices-routes [db]
  (routes
   (GET "/pre-notices/show/:id" {params :params user :user}
        (authorization/with-transit-authority-check
          db user
          #(http/no-cache-transit-response
            (fetch-notice db (Long/parseLong (:id params))))))
   (GET "/pre-notices/authority-list" {user :user :as req}
        (authorization/with-transit-authority-check
          db user
          #(http/no-cache-transit-response (list-published-notices db user))))
   (POST "/pre-notices/comment" {form-data :body user :user}
         (authorization/with-transit-authority-check
           db user
           #(http/transit-response
             (add-comment db user (http/transit-request form-data)))))))
