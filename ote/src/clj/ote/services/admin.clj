(ns ote.services.admin
  "Backend services for admin functionality."
  (:require [ote.components.http :as http]
            [com.stuartsierra.component :as component]
            [specql.core :refer [fetch update! insert! upsert! delete!] :as specql]
            [specql.op :as op]
            [taoensso.timbre :as log]
            [compojure.core :refer [routes GET POST DELETE]]
            [ote.nap.users :as nap-users]
            [specql.core :as specql]
            [ote.db.auditlog :as auditlog]
            [ote.db.transport-service :as t-service]
            [ote.db.modification :as modification]
            [ote.services.transport :as transport]))


(def service-search-result-columns
  #{::t-service/contact-email
    ::t-service/sub-type
    ::t-service/id
    ::t-service/contact-gsm
    ::t-service/contact-address
    ::t-service/name
    ::t-service/type
    ::modification/created
    ::t-service/published?
    ::t-service/transport-operator-id
    ::t-service/contact-phone

    ;; Information JOINed from other tables
    ::t-service/operator-name})

(defn- require-admin-user [route user]
  (when (not (:admin? user))
    (throw (SecurityException. "admin only"))))

(defn- admin-service [route {user :user
                             form-data :body :as req} db handler]
  (require-admin-user route (:user user))
  (http/transit-response
   (handler db user (http/transit-request form-data))))

(defn- list-users [db user query]
  (nap-users/list-users db {:email (str "%" query "%")
                            :name  (str "%" query "%")}))
(defn- published-search-param [query]
  (case (:published-type query)
    nil? nil
    :ALL nil
    :YES {::t-service/published? true}
    :NO {::t-service/published? false}
    nil))

(defn- list-services
  "Returns list of transport-services. Query parameters aren't mandatory, but it can be used to filter results."
  [db user query]
  (let [q (if (nil? (:query query))
            nil
            {::t-service/name (op/ilike (str "%" (:query query) "%"))})
        search-params (merge q (published-search-param query))]
    (fetch db ::t-service/transport-service-search-result
         service-search-result-columns
         search-params
         {:specql.core/order-by ::t-service/name})))

(defn- list-services-by-operator [db user query]
  (let [q (if (nil? (:query query))
            nil
            {::t-service/operator-name (op/ilike (str "%" (:query query) "%"))})
        search-params (merge q (published-search-param query))]
    (fetch db ::t-service/transport-service-search-result
         service-search-result-columns
         search-params
         {:specql.core/order-by ::t-service/operator-name})))

(defn- admin-delete-transport-service!
  "Allow admin to delete single transport service by id"
  [nap-config db user {id :id}]
  (let [deleted-service (transport/get-transport-service db id)
        return (transport/delete-transport-service! nap-config db user id)
        auditlog {::auditlog/event-type :delete-service
                  ::auditlog/event-attributes
                  [{::auditlog/name "transport-service-id", ::auditlog/value (str id)},
                   {::auditlog/name "transport-service-name", ::auditlog/value (::t-service/name deleted-service)}]
                  ::auditlog/event-timestamp (java.sql.Timestamp. (System/currentTimeMillis))
                  ::auditlog/created-by (get-in user [:user :id])}]
    (upsert! db ::auditlog/auditlog auditlog)
    return))

(defn- admin-routes [db http nap-config]
  (routes
    (POST "/admin/users" req (admin-service "users" req db #'list-users))

    (POST "/admin/transport-services" req (admin-service "services" req db #'list-services))

    (POST "/admin/transport-services-by-operator" req (admin-service "services" req db #'list-services-by-operator))

    (POST "/admin/transport-service/delete" req
      (admin-service "transport-service/delete" req db
                     (partial admin-delete-transport-service! nap-config)))))

(defrecord Admin [nap-config]
  component/Lifecycle
  (start [{db :db http :http :as this}]
    (assoc this ::stop
           (http/publish! http (admin-routes db http nap-config))))

  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
