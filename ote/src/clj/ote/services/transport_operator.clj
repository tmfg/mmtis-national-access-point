(ns ote.services.transport-operator
  "Services for getting transport-operator data from database"
  (:require [clojure.string :as str]
            [clojure.set :refer [rename-keys]]
            [clj-time.core :as t]
            [clj-time.coerce :as tc]
            [taoensso.timbre :as log]
            [compojure.core :refer [routes GET POST DELETE]]
            [hiccup.core :refer [html]]
            [specql.core :refer [fetch update! insert! upsert! delete!] :as specql]
            [specql.op :as op]
            [jeesql.core :refer [defqueries]]
            [com.stuartsierra.component :as component]
            [ote.config.email-config :as email-config]
            [ote.time :as time]
            [ote.components.http :as http]
            [ote.util.email-template :as email-template]
            [ote.authorization :as authorization]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]
            [ote.db.auditlog :as auditlog]
            [ote.db.common :as common]
            [ote.db.user :as user]
            [ote.db.tx :as tx]
            [ote.email :as email]
            [ote.services.operators :as operators]
            [ote.services.transport :as transport-service])
  (:import (java.util UUID)))

(defqueries "ote/services/transport.sql")
(defqueries "ote/services/operators.sql")
(defqueries "ote/services/associations.sql")
(defqueries "ote/nap/users.sql")

(def transport-operator-columns
  #{::t-operator/id ::t-operator/business-id ::t-operator/email
    ::t-operator/name})

(defn get-transport-operator
  [db where-parameter]
  (let [where (merge {::t-operator/deleted? false} where-parameter)
        operator (first (fetch db ::t-operator/transport-operator
                               (specql/columns ::t-operator/transport-operator)
                               where {::specql/limit 1}))
        associated-to-services-others (services-associated-to-operator db {:business-id (::t-operator/business-id operator)})
        own-associations (fetch-operators-associated-services db {:operator-id (::t-operator/id operator)})]
    (when operator
      (-> operator
          (assoc ::t-operator/ckan-description (or (fetch-transport-operator-ckan-description
                                                     db {:id (::t-operator/ckan-group-id operator)})
                                                   ""))
          (assoc ::t-operator/associated-services associated-to-services-others)
          (assoc ::t-operator/own-associations own-associations)))))

(defn delete-transport-operator!
  "Delete transport operator by id"
  [nap-config db user id]
  (let [operator-services (specql/fetch db
                                        ::t-service/transport-service
                                        #{::t-service/id}
                                        {::t-service/transport-operator-id id})]
    ;; delete only if operator-services = nil
    (if (empty? operator-services)
      (authorization/with-transport-operator-check
        db user id
        #(do
           (operators/delete-transport-operator db {:operator-group-name (str "transport-operator-" id)})
           id))
      {:status 403
       :body "Operator has services and it cannot be removed."})))

(defn create-member! [db user-id group-id]
  (specql/insert! db ::user/member
                  {::user/id (str (UUID/randomUUID))
                   ::user/table_id user-id
                   ::user/group_id group-id
                   ::user/table_name "user"
                   ::user/capacity "admin"
                   ::user/state "active"}))

(defn- give-permissions!
  "Takes `op` operator and `user` and pairs user to organization in db using the member table. Sets role (Capacity) to 'admin'"
  [db op user]
  {:pre [(some? op) (some? (::t-operator/name op))]}
  (let [user-id (get-in user [:user :id])
        group (specql/insert! db ::t-operator/group
                              {::t-operator/group-id (str (UUID/randomUUID))
                               ::t-operator/group-name (str "transport-operator-" (::t-operator/id op))
                               ::t-operator/title (::t-operator/name op)
                               ::t-operator/description (or (::t-operator/ckan-description op) "")
                               ::t-operator/created (java.util.Date.)
                               ::t-operator/state "active"
                               ::t-operator/type "organization"
                               ::t-operator/approval_status "approved"
                               ::t-operator/is_organization true})
        member (create-member! db user-id (:ote.db.transport-operator/group-id group))
        ;; Ensure that other users get permissions to new operator as well
        ;; Get other possible users that need permissions
        other-users (fetch-users-within-same-business-id-family db {:business-id (::t-operator/business-id op)
                                                                    :user-id user-id})]
    (when other-users
      (doall
        (for [u other-users]
          (create-member! db (:user-id u) (:ote.db.transport-operator/group-id group)))))
    group))

(defn- update-group!
  "Takes `op` operator and updates the group table for matching row. Returns number of affected rows."
  [db op]
  {:pre [(coll? op)
         (and (some? (::t-operator/ckan-group-id op)) (string? (::t-operator/ckan-group-id op)))]}
  (let [count (update! db ::t-operator/group
                       {::t-operator/title (::t-operator/name op)
                        ::t-operator/description (or (::t-operator/ckan-description op) "")}
                       {::t-operator/group-id (::t-operator/ckan-group-id op)})]
    (when (not= 1 count) (log/error (prn-str "update-group!: updating groups, expected 1 but got number of records=" count)))
    count))

(defn- create-transport-operator-nap
  "Takes `db`, `user` and operator `data`. Creates a new transport-operator and a group (organization) for it.
   Links the transport-operator to group via member table"
  [db user data]
  {:pre [(some? data)]}
  (tx/with-transaction db
                       (let [op (insert! db ::t-operator/transport-operator
                                         (dissoc data
                                                 ::t-operator/id
                                                 ::t-operator/ckan-description
                                                 ::t-operator/ckan-group-id))
                             group (give-permissions! db op user)]

                         (update! db ::t-operator/transport-operator
                                  {::t-operator/ckan-group-id (::t-operator/group-id group)}
                                  {::t-operator/id (::t-operator/id op)})
                         op)))

(defn- select-op-keys-to-update [op]
  (select-keys op
               [::t-operator/name
                ::t-operator/billing-address
                ::t-operator/visiting-address
                ::t-operator/phone
                ::t-operator/gsm
                ::t-operator/email
                ::t-operator/homepage]))

;; Takes db and transport-operator. Resolves optional data for updating operator and returns a transport-operator map.
(defn resolve-update-operator-data [db op]
  (let [ckan-id (group-id-for-op db {:id (::t-operator/id op)})]
    ;; cond used just in case there are mode fields to handle in the future
    (cond-> op
            (some? ckan-id) (assoc ::t-operator/ckan-group-id (:ckan-group-id (first ckan-id))))))

(defn- update-transport-operator-nap [db user {id ::t-operator/id :as data}]
  ;; Edit transport operator
  {:pre [(coll? data) (number? (::t-operator/id data))]}
  (authorization/with-transport-operator-check
    db user id
    #(tx/with-transaction
       db
       (update! db ::t-operator/transport-operator
                (select-op-keys-to-update data)
                {::t-operator/id (::t-operator/id data)})
       (update-group! db (resolve-update-operator-data db data)))))

(defn- upsert-transport-operator
  "Creates or updates a transport operator for each company name. Operators will have the same details, except the name"
  [nap-config db user data]
  {:pre [(some? data)]}
  (let [operator data]
    (if (::t-operator/id operator)
      (update-transport-operator-nap db user operator)
      (create-transport-operator-nap db user operator))))

(defn- save-transport-operator [config db user data]
  {:pre [(some? data)]}
  (log/debug (prn-str "save-transport-operator " data))

  (upsert-transport-operator (:nap config) db user data))

(defn- business-id-exists [db business-id]
  (if (empty? (does-business-id-exists db {:business-id business-id}))
    {:business-id-exists false}
    {:business-id-exists true}))

(defn private-data-transport-operator
  "Get single transport service by id"
  [db id]
  (let [to (first (fetch db ::t-operator/transport-operator
                         (specql/columns ::t-operator/transport-operator)
                         {::t-operator/id id}))
        to (-> to
               (dissoc :ote.db.transport-operator/deleted?)
               (assoc ::t-operator/ckan-description (or (fetch-transport-operator-ckan-description
                                                          db {:id (::t-operator/ckan-group-id to)})
                                                        "")))]
    (if to
      (http/no-cache-transit-response to)
      {:status 404})))

(defn public-data-transport-operator
  "Get single transport service by id"
  [db id]
  (let [to (first (fetch db ::t-operator/transport-operator
                         #{::t-operator/business-id
                           ::t-operator/name
                           ::t-operator/id
                           ::t-operator/homepage}
                         {::t-operator/id id}))]
    (if to
      (http/no-cache-transit-response to)
      {:status 404})))

(defn operator-users&invites
  [db ckan-group-id]
  (let [users (fetch-operator-users db {:ckan-group-id ckan-group-id})
        invites (fetch db ::user/user-token
                       #{::user/user-email ::user/token}
                       {::user/ckan-group-id ckan-group-id
                        ::user/expiration (op/>= (tc/to-sql-date (t/now)))})
        invites (map
                  (fn [invite]
                    (-> invite
                        (assoc :pending? true)
                        (rename-keys {::user/user-email :email ::user/token :token})))
                  invites)]
    (concat users invites)))

(defn- delete-old-tokens
  [db ckan-group-id]
  (specql/delete! db ::user/user-token
                  {::user/ckan-group-id ckan-group-id
                   ::user/expiration (op/< (tc/to-sql-date (t/now)))}))

(defn operator-users-response
  [db ckan-group-id]
  (let [users&invites (operator-users&invites db ckan-group-id)
        _ (delete-old-tokens db ckan-group-id)]
    (http/transit-response users&invites 200)))

(defn add-user-to-operator [email db new-member requester operator authority?]
  (let [member (create-member! db (:user_id new-member) (::t-operator/group-id operator))
        ;; Ensure that we do not send email to test user
        updated-member (if (= (:e2e-test-email (email-config/config)) (:user_email new-member))
                         (assoc new-member :user_email (:e2e-test-amazon-simulator-email (email-config/config)))
                         new-member)
        title (if authority?
                (str "Olet saanut kutsun liittyä tarkastelemaan markkinaehtoisen henkilöliikenteen muutosilmoituksia NAP:ssa")
                (str "Sinut on kutsuttu " (::t-operator/title operator) " -nimisen palveluntuottajan jäseneksi"))
        auditlog {::auditlog/event-type :add-member-to-operator
                  ::auditlog/event-attributes
                  [{::auditlog/name "transport-group-id", ::auditlog/value (str (::t-operator/group-id operator))},
                   {::auditlog/name "transport-group-title", ::auditlog/value (str (::t-operator/title operator))}
                   {::auditlog/name "new-member-id", ::auditlog/value (str (:user_id updated-member))}
                   {::auditlog/name "new-member-email", ::auditlog/value (str (:user_email updated-member))}]
                  ::auditlog/event-timestamp (java.sql.Timestamp. (System/currentTimeMillis))
                  ::auditlog/created-by (get-in requester [:user :id])}]
    ;; Send email to user to inform about the new membership.
    (try
      (email/send!
        email
        {:to (:user_email updated-member)
         :subject title
         :body [{:type "text/html;charset=utf-8"
                 :content (str email-template/html-header
                               (html (if authority?
                                       (email-template/notify-authority-new-member updated-member requester operator title)
                                       (email-template/notify-user-new-member updated-member requester operator title))))}]})
      (catch Exception e
        (log/warn "Error while sending a email to new member" e)))

    ;; Create auditlog
    (specql/insert! db ::auditlog/auditlog auditlog)

    ;; Return added new-member data
    {:id (:user_id new-member)
     :name (:user_username new-member)
     :fullname (:user_name new-member)
     :email (:user_email new-member)}))

(defn invite-new-user [email-config db requester operator user-email authority?]
  (let [expiration-date (time/sql-date (.plusDays (java.time.LocalDate/now) 62)) ;; Expiration date increased from 1 to 62 to allow users to join even if invite is send on their summer holiday
        token (UUID/randomUUID)
        inserted-token (specql/insert! db ::user/user-token
                                       {::user/user-email user-email
                                        ::user/token (str token)
                                        ::user/ckan-group-id (::t-operator/group-id operator)
                                        ::user/expiration expiration-date
                                        ::user/requester-id (get-in requester [:user :id])})
        title (if authority?
                (str "Olet saanut kutsun liittyä NAP:iin")
                (str "Sinut on kutsuttu " (::t-operator/title operator) " -nimisen palveluntuottajan jäseneksi"))
        auditlog {::auditlog/event-type :invite-new-user
                  ::auditlog/event-attributes
                  [{::auditlog/name "transport-group-id", ::auditlog/value (str (::t-operator/group-id operator))},
                   {::auditlog/name "transport-group-title", ::auditlog/value (str (::t-operator/title operator))}
                   {::auditlog/name "new-member-email", ::auditlog/value (str user-email)}]
                  ::auditlog/event-timestamp (java.sql.Timestamp. (System/currentTimeMillis))
                  ::auditlog/created-by (get-in requester [:user :id])}]
    (try
      (email/send!
        email-config
        {:to user-email
         :subject title
         :body [{:type "text/html;charset=utf-8"
                 :content (str email-template/html-header
                               (html (if authority?
                                       (email-template/new-authority-invite requester operator title (::user/token inserted-token))
                                       (email-template/new-user-invite requester operator title (::user/token inserted-token)))))}]})

      (specql/insert! db ::auditlog/auditlog auditlog)

      {:pending? true
       :token (::user/token inserted-token)
       :email (::user/user-email inserted-token)}
      (catch Exception e
        (log/warn (str "Error while inviting " user-email " ") e)))))

(defn manage-adding-users-to-operator [email db requester operator form-data]
  (let [transit-authority?            (= (::t-operator/group-id operator) (transit-authority-group-id db))
        allowed-to-manage?            (or (authorization/admin? requester)
                                          (if transit-authority?
                                            (authorization/member-of-group? requester (authority-group-admin-id db))
                                            true))
        new-member                    (first (fetch-user-by-email db {:email (:email form-data)}))
        ckan-group-id                 (::t-operator/group-id operator)
        operator-users                (fetch-operator-users db {:ckan-group-id ckan-group-id})
        not-invited?                  (empty?
                                        (specql/fetch db ::user/user-token
                                                      #{::user/user-email}
                                                      {::user/ckan-group-id ckan-group-id
                                                       ::user/user-email    (:email form-data)
                                                       ::user/expiration    (op/>= (tc/to-sql-date (t/now)))}))
        new-member-is-operator-member (some #(= (:user_id new-member) (:id %)) operator-users)

        ;; If member exists, add it to the organization if not, invite
        response (cond
                   ;; Existing new-member, existing operator
                   (and allowed-to-manage?
                        (not new-member-is-operator-member)
                        (not (empty? new-member))
                        (not (empty? operator)))
                   (add-user-to-operator email db new-member requester operator transit-authority?)

                   ;; new new-member, existing operator -> invite new-member
                   (and allowed-to-manage?
                        (empty? new-member)
                        (not (empty? operator))
                        not-invited?)
                   (invite-new-user email db requester operator (:email form-data) transit-authority?)

                   ;; new-member is already a member
                   (and (not (empty? new-member))
                        new-member-is-operator-member)
                   {:error :already-member}

                   (not not-invited?)
                   {:error :already-invited}

                   ;; Something wrong is happening
                   :else
                   {:error :something-went-wrong})]
    (if (not (:error response))
      (http/transit-response response 200)
      (http/transit-response response 400))))


(defn remove-token-from-operator
  [db user ckan-group-id form-data operator]
  (let [transit-authority? (= ckan-group-id (transit-authority-group-id db))
        allowed-to-manage? (and (authorization/admin? user)
                                (if transit-authority?
                                  (authorization/member-of-group? user (authority-group-admin-id db))
                                  true))
        delete-count (if allowed-to-manage?
                       (specql/delete! db ::user/user-token
                                       {::user/token (:token form-data)
                                        ::user/ckan-group-id ckan-group-id})
                       0)]

    (log/info "Token deleted by " (get-in user [:user :email]) ". From operator " (::t-operator/title operator))
    (if (= 0 delete-count)
      (http/transit-response "Delete failed" 400)
      (http/transit-response "Deleted successfully" 200))))

(defn ckan-group-id->group
  [db ckan-group-id]
  (first (specql/fetch db ::t-operator/group
                       (specql/columns ::t-operator/group)
                       {::t-operator/group-id ckan-group-id})))

(defn- as-authorization-groups
  "Converts operator base data into authorization group lookup by group id.

  This is a sort-of-a-hack around the fact that CKAN groups are used directly as both operators and authorization groups.
  The resulting data structure is a lookup by id which is meant to be easily consumed on the frontend.

  ```clojure
  {groupid -> {extra? truth}}
  ```"
  [user groups]
  (assoc
    user
    :groups
    (reduce
      (fn [all group]
        (assoc all (:id group) (dissoc group :id)))
      {}
      groups)))

(defn get-user-transport-operators-with-services [db groups user]
  (let [operators (keep #(get-transport-operator db {::t-operator/ckan-group-id (:id %)}) groups)
        operator-ids (into #{} (map ::t-operator/id) operators)
        operator-services (transport-service/get-transport-services db operator-ids)]
    {:user (-> user (dissoc :apikey :id) (as-authorization-groups groups))
     :authority-group-id (authority-group-admin-id db)
     :transport-operators
     (map (fn [{id ::t-operator/id :as operator}]
            {:transport-operator operator
             :transport-service-vector (into []
                                             (filter #(= id (::t-service/transport-operator-id %)))
                                             operator-services)})
          operators)}))

(defn remove-member-from-operator
  [db user operator form-data]
  (let [transit-authority? (= (::t-operator/group-id operator) (transit-authority-group-id db))
        allowed-to-manage? (and (authorization/admin? user)
                                (if transit-authority?
                                  (authorization/member-of-group? user (authority-group-admin-id db))
                                  true))
        ckan-group-id      (::t-operator/group-id operator)
        auditlog           {::auditlog/event-type :remove-member-from-operator
                            ::auditlog/event-attributes
                            [{::auditlog/name "transport-group-id", ::auditlog/value (str (::t-operator/group-id operator))},
                             {::auditlog/name "transport-group-title", ::auditlog/value (str (::t-operator/title operator))}
                             {::auditlog/name "removed-user-id", ::auditlog/value (str (:id form-data))}]
                            ::auditlog/event-timestamp (java.sql.Timestamp. (System/currentTimeMillis))
                            ::auditlog/created-by (get-in user [:user :id])}

        user-count         (count (specql/fetch db ::user/member
                                                #{::user/group_id}
                                                {::user/group_id ckan-group-id}))
        delete-clauses     (filterv
                             (complement nil?)
                             [(when (nil? (:id form-data)) :no-member-email-available)
                              (when (= user-count 1) :only-one-member)
                              (when (not allowed-to-manage?) :not-an-admin)])
        delete-count       (if-not (empty? delete-clauses)
                             0
                             (specql/delete! db ::user/member
                                             {::user/table_id (:id form-data)
                                              ::user/group_id ckan-group-id}))]

    (if (= 0 delete-count)
      (do
        (log/warn (str "Member removal by " (get-in usert [:user :email]) " (" (get-in usert [:user :id]) ") failed for operator: " (or (::t-operator/name operator) (::t-operator/group-name operator) (::t-operator/title operator)) " with user: " (:email form-data) ", reasons: " delete-clauses))
        (http/transit-response "Removal unsuccessful" 400))
      (do
        (specql/insert! db ::auditlog/auditlog auditlog)
        (http/transit-response "Member removed successfully" 200)))))

(defn- transport-operator-routes-auth
  "Routes that require authentication"
  [db config email]
  (let [nap-config (:nap config)]
    (routes

      (GET "/transport-operator/ensure-unique-business-id/:business-id" [business-id :as {user :user}]
        (http/transit-response
          (business-id-exists db business-id)))

      (GET "/transport-operator/:ckan-group-id/users"
           {{:keys [ckan-group-id]}
            :params
            user :user}
        (authorization/with-group-check db user ckan-group-id
                                        #(operator-users-response db ckan-group-id)))

      (POST "/transport-operator/:ckan-group-id/users"
            {{:keys [ckan-group-id]}
             :params
             user :user
             form-data :body}
        (let [operator (ckan-group-id->group db ckan-group-id)
              form-data (http/transit-request form-data)]
          (authorization/with-group-check
            db user ckan-group-id
            #(manage-adding-users-to-operator email db user operator
                                              form-data))))

      ;; This is not ready, only implemented to help add-member implementation
      (DELETE "/transport-operator/:ckan-group-id/token"
              {{:keys [ckan-group-id]}
               :params
               user :user
               form-data :body}
        (let [operator (ckan-group-id->group db ckan-group-id)
              form-data (http/transit-request form-data)]
          (authorization/with-group-check
            db user ckan-group-id
            #(remove-token-from-operator db user ckan-group-id form-data operator))))

      (DELETE "/transport-operator/:ckan-group-id/users"
              {{:keys [ckan-group-id]}
               :params
               user :user
               form-data :body}
        (let [group (ckan-group-id->group db ckan-group-id)
              form-data (http/transit-request form-data)]
          (authorization/with-group-check
            db user ckan-group-id
            #(remove-member-from-operator db user group form-data))))

      (POST "/transport-operator/group" {user :user cookies :cookies}
        (http/transit-response
          (get-transport-operator db
                                  {::t-operator/ckan-group-id (get (-> user :groups first) :id)})))

      (POST "/transport-operator/data"
            {user :user
             cookies :cookies}
        (http/transit-response
          (get-user-transport-operators-with-services db (:groups user) (:user user))))

      (POST "/transport-operator" {form-data :body
                                   user :user}
        (let [form-data (http/transit-request form-data)
              form-data (-> form-data
                            (update ::t-operator/foreign-business-id? true?)
                            (update ::t-operator/business-id str/trim))]
          (http/transit-response
            (save-transport-operator config db user form-data))))

      (POST "/transport-operator/delete" {form-data :body
                                          user :user}
        (http/transit-response
          (delete-transport-operator! nap-config db user
                                      (:id (http/transit-request form-data))))))))

(defn- transport-operator-routes
  "Unauthenticated routes"
  [db config]
  (routes
    (GET "/transport-operator/:ckan-group-id" [ckan-group-id]
      (http/transit-response
        (get-transport-operator db {::t-operator/ckan-group-id ckan-group-id})))

    (GET "/t-operator/:id"
         {{:keys [id]}
          :params
          user :user}
      (let [id (Long/parseLong id)]
        (if (or (authorization/admin? user) (authorization/is-author? db user id))
          (private-data-transport-operator db id)
          (public-data-transport-operator db id))))))

(defrecord TransportOperator [config]
  component/Lifecycle
  (start [{:keys [db http email] :as this}]
    (assoc
      this ::stop
           [(http/publish! http (transport-operator-routes-auth db config email))
            (http/publish! http {:authenticated? false} (transport-operator-routes db config))]))
  (stop [{stop ::stop :as this}]
    (doseq [s stop]
      (s))
    (dissoc this ::stop)))
