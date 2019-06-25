(ns ote.services.pre-notices.authority
  "Pre notice services for transport authority users"
  (:require [ote.components.http :as http]
            [compojure.core :refer [routes POST GET]]
            [specql.core :as specql]
            [ote.db.transit :as transit]
            [ote.db.modification :as modification]
            [ote.db.user :as user]
            [ote.authorization :as authorization]
            [ote.db.transport-operator :as t-operator]
            [clojure.string :as str]
            [ote.db.places :as places]
            [specql.op :as op]
            [ote.nap.users :as users]))


(defn list-published-notices [db user]
  (specql/fetch db ::transit/pre-notice
                #{::transit/id
                  ::transit/sent
                  ::modification/created
                  ::transit/regions
                  ::transit/route-description
                  ::transit/pre-notice-type
                  [::t-operator/transport-operator #{::t-operator/name}]}
                {::transit/pre-notice-state :sent}
                {:specql.core/order-by ::transit/sent
                 :specql.core/order-direction :desc}))

(def comment-columns  (conj (specql/columns ::transit/pre-notice-comment)
                            [::transit/author #{::user/fullname ::user/email}]))

(defn fetch-notice [db id]
  (let [notice (first
                (specql/fetch db ::transit/pre-notice
                              (conj (specql/columns ::transit/pre-notice)
                                    [::transit/comments comment-columns]
                                    [::t-operator/transport-operator #{::t-operator/name
                                                                       ::t-operator/email
                                                                       ::t-operator/phone
                                                                       ::t-operator/gsm}])
                              {::transit/id id}))]
    (assoc notice
           ::transit/attachments
           (specql/fetch db ::transit/pre-notice-attachment
                                  (specql/columns ::transit/pre-notice-attachment)
                                  {::transit/pre-notice-id id})

           :region-names
           (str/join ", "
                     (map ::places/nimi
                          (specql/fetch db ::places/finnish-regions
                                        #{::places/nimi}
                                        {::places/numero (op/in (::transit/regions notice))}))))))

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
             (add-comment db user (http/transit-request form-data)))))
   (GET "/pre-notices/authority-group-id" {}
    (http/transit-response
       {:ckan-group-id (users/transit-authority-group-id db)} 200))))
