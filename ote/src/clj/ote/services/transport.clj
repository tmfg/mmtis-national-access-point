(ns ote.services.transport
  "Services for getting transport data from database"
  (:require [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [specql.core :refer [fetch update! insert! upsert! delete!] :as specql]
            [specql.op :as op]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]
            [ote.db.auditlog :as auditlog]
            [ote.db.user :as user]
            [ote.util.db :as util]
            [ote.util.email-template :as email-template]
            [ote.config.email-config :as email-config]
            [ote.db.user :as user]
            [compojure.core :refer [routes GET POST DELETE]]
            [taoensso.timbre :as log]
            [clojure.java.jdbc :as jdbc]
            [ote.services.places :as places]
            [ote.services.operators :as operators]
            [ote.services.external :as external]
            [ote.authorization :as authorization]
            [jeesql.core :refer [defqueries]]
            [ote.db.tx :as tx]
            [ote.db.modification :as modification]
            [clojure.set :as set]
            [ote.email :as email]
            [hiccup.core :refer [html]]
            [ote.time :as time])
  (:import (java.util UUID)))

; TODO: split file to transport-service and transport-operator
(defqueries "ote/services/places.sql")
(defqueries "ote/services/transport.sql")
(defqueries "ote/services/operators.sql")
(defqueries "ote/services/associations.sql")
(defqueries "ote/nap/users.sql")

(def transport-operator-columns
  #{::t-operator/id ::t-operator/business-id ::t-operator/email
    ::t-operator/name})

(def transport-service-personal-columns
  #{::t-service/contact-phone
    ::t-service/contact-gsm
    ::t-service/contact-email})

(defn get-transport-operator
  [db where-parameter]
  (let [where  (merge  {::t-operator/deleted? false} where-parameter)
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

(defn authenticated-get-operator-with-id [db user transport-operator-id]
  (authorization/with-transport-operator-check
    db user transport-operator-id
    #(let [ckan-group-id (first (fetch db ::t-operator/transport-operator
                                       #{::t-operator/ckan-group-id}
                                       {::t-operator/id transport-operator-id}
                                       {::specql/limit 1}))]
       (when ckan-group-id
         (get-transport-operator db ckan-group-id)))))

(def transport-services-column-keys
  {:id ::t-service/id
    :transport-operator-id ::t-service/transport-operator-id
    :name ::t-service/name
    :transport-type ::t-service/transport-type
    :interface-types ::t-service/interface-types
    :type ::t-service/type
    :sub-type ::t-service/sub-type
    :published ::t-service/published
    :created ::modification/created
    :modified ::modification/modified})

(defn add-error-data
  [service]
  (let [sub-type (::t-service/sub-type service)
        interface-types (::t-service/interface-types service)]
    (assoc service :has-errors?
      (and
        (= sub-type :schedule)
        (not ((set interface-types) :route-and-schedule))))))

(defn get-transport-services
  "Return Vector of transport-services"
  [db operators]
  (let [services (fetch-transport-services db {:operator-ids operators})
        ;; Add namespace for non namespaced keywords because sql query returns values without namespace
        modified-services (mapv (fn [x] (set/rename-keys x transport-services-column-keys)) services)]
    ;; For some reason type must be a keyword and query returns it as a string so make it keyword.
    (mapv (fn [service]
            (-> service
                (update ::t-service/type keyword)
                (update ::t-service/sub-type keyword)
                (update ::t-service/interface-types #(mapv keyword (util/PgArray->vec %)))
                (update ::t-service/transport-type #(mapv keyword (util/PgArray->vec %)))
                add-error-data))
          modified-services)))

(defn all-data-transport-service
  "Get single transport service by id"
  [db id]
  (let [ts (first (fetch db ::t-service/transport-service
                         (conj (specql/columns ::t-service/transport-service)
                               ;; join external interfaces
                               [::t-service/external-interfaces
                                (specql/columns ::t-service/external-interface-description)])
                         {::t-service/id id}))]
    (if ts
      (http/transit-response (assoc ts ::t-service/operation-area
                                       (places/fetch-transport-service-operation-area db id)))
      {:status 404})))

(defn delete-transport-service!
  "Delete single transport service by id"
  [nap-config db user id]

  (let [{::t-service/keys [transport-operator-id published]}
        (first (specql/fetch db ::t-service/transport-service
                             #{::t-service/transport-operator-id
                               ::t-service/published}
                             {::t-service/id id}))]
    (authorization/with-transport-operator-check
      db user transport-operator-id
      #(do
         (delete! db ::t-service/transport-service {::t-service/id id})
         id))))

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

(defn get-user-transport-operators-with-services [db groups user]
  (let [operators (keep #(get-transport-operator db {::t-operator/ckan-group-id (:id %)}) groups)
        operator-ids (into #{} (map ::t-operator/id) operators)
        operator-services (get-transport-services db operator-ids)]
    {:user (dissoc user :apikey :id)
     :transport-operators
     (map (fn [{id ::t-operator/id :as operator}]
            {:transport-operator operator
             :transport-service-vector (into []
                                             (filter #(= id (::t-service/transport-operator-id %)))
                                             operator-services)})
          operators)}))

(defn get-transport-operator-data [db {:keys [title id] :as ckan-group} user]
  (let [transport-operator (get-transport-operator db {::t-operator/ckan-group-id (:id ckan-group)})
        transport-services-vector (get-transport-services db transport-operator)
        ;; Clean up user data
        cleaned-user (dissoc user
                             :apikey
                             :email
                             :id)]
    {:transport-operator transport-operator
     :transport-service-vector transport-services-vector
     :user cleaned-user}))

(defn create-member! [db user-id group-id]
  (specql/insert! db ::user/member
                  {::user/id         (str (UUID/randomUUID))
                   ::user/table_id   user-id
                   ::user/group_id   group-id
                   ::user/table_name "user"
                   ::user/capacity   "admin"
                   ::user/state      "active"}))

(defn- give-permissions!
  "Takes `op` operator and `user` and pairs user to organization in db using the member table. Sets role (Capacity) to 'admin'"
  [db op user]
  {:pre [(some? op) (some? (::t-operator/name op))]}
  (let [user-id (get-in user [:user :id])
        group (specql/insert! db ::t-operator/group
                              {::t-operator/group-id        (str (UUID/randomUUID))
                               ::t-operator/group-name      (str "transport-operator-" (::t-operator/id op))
                               ::t-operator/title           (::t-operator/name op)
                               ::t-operator/description     (or (::t-operator/ckan-description op) "")
                               ::t-operator/created         (java.util.Date.)
                               ::t-operator/state           "active"
                               ::t-operator/type            "organization"
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
                       {::t-operator/title       (::t-operator/name op)
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

(defn ensure-bigdec [value]
  (when-not (nil? value ) (bigdec value)))

(defn- fix-price-classes
  "Frontend sends price classes prices as floating points. Convert them to bigdecimals before db insert."
  ([service data-path]
   (fix-price-classes service data-path [::t-service/price-per-unit]))
  ([service data-path price-per-unit-path]
   (update-in service data-path
              (fn [price-classes-float]
                (mapv #(update-in % price-per-unit-path ensure-bigdec) price-classes-float)))))

(defn- update-rental-price-classes [service]
  (update-in service [::t-service/rentals ::t-service/vehicle-classes]
             (fn [vehicles]
               (mapv #(let [before %
                            after (fix-price-classes % [::t-service/price-classes])]
                        after)
                     vehicles))))

(defn- floats-to-bigdec
  "Frontend sends some values as floating points. Convert them to bigdecimals before db insert."
  [service]
  (case (::t-service/type service)
    :passenger-transportation
    (fix-price-classes service [::t-service/passenger-transportation ::t-service/price-classes])
    :parking
    (fix-price-classes service [::t-service/parking ::t-service/price-classes])
    :rentals
    (-> service
        (fix-price-classes [::t-service/rentals ::t-service/rental-additional-services]
                           [::t-service/additional-service-price ::t-service/price-per-unit])
        update-rental-price-classes)
    service))

(defn mark-package-as-deleted
  "When external interface is deleted (when it is delted or service is delted) we don't want to
  remove all gtfs data that we have aquired. So we only mark gtfs_packages.deleted = TRUE for those packages and
  remove the interface url."
  [db external-interface-description-id]

  ;; set all found packages as deleted
  (specql/update! db :gtfs/package
                  {:gtfs/deleted? true}
                  {:gtfs/external-interface-description-id external-interface-description-id}))

(defn- save-external-interfaces
  "Save external interfaces for a transport service"
  [db transport-service-id external-interfaces removed-resources]

  ;; Delete removed services from OTE db
  (doseq [{id ::t-service/id} removed-resources]
    ;; Mark possible gtfs_packages to removed and then remove interface
    (mark-package-as-deleted db id)

    (specql/delete! db ::t-service/external-interface-description
                    {::t-service/id id}))

  (doseq [{id ::t-service/id :as ext-if} external-interfaces]
    (if id
      (specql/update! db ::t-service/external-interface-description
                      ext-if
                      {::t-service/transport-service-id transport-service-id
                       ::t-service/id id})
      (specql/insert! db ::t-service/external-interface-description
                      (assoc ext-if ::t-service/transport-service-id transport-service-id)))))

(defn- removable-resources
  [from-db from-client]
  (let [in-db (into #{} (map ::t-service/id) from-db)
        from-ui (into #{} (map ::t-service/id) from-client)
        to-delete (map #(select-keys % #{::t-service/id})
                       (filter (comp (complement from-ui) ::t-service/id) from-db))]
    to-delete))

(defn- delete-external-companies
  "User might remove url from service, so we delete all service-companies from db"
  [db transport-service]
    (specql/delete! db ::t-service/service-company {::t-service/transport-service-id (::t-service/id transport-service)}))

(defn save-external-companies
  "Service can contain an url that contains company names and business-id. Sevice can also contain an imported csv file
  with company names and business-ids."
  [db transport-service]
  (let [current-data (first (fetch db ::t-service/service-company (specql/columns ::t-service/service-company)
                                   {::t-service/transport-service-id (::t-service/id transport-service)}))
        companies (vec (:companies (external/check-csv {:url (::t-service/companies-csv-url transport-service)})))
        new-data (if (empty? current-data)
                   {::t-service/companies            companies
                    ::t-service/transport-service-id (::t-service/id transport-service)
                    ::t-service/source               "URL"}
                   (assoc current-data ::t-service/companies companies))]

    (external/save-companies db new-data)))

(defn- maybe-clear-companies
  "Companies can be added from url, csv or by hand in form. Clean up url if some other option is selected"
  [transport-service]
  (let [source (get transport-service ::t-service/company-source)]
  (cond
    (= :none source) (assoc transport-service ::t-service/companies-csv-url nil
                                              ::t-service/companies {})
    (= :form source) (assoc transport-service ::t-service/companies-csv-url nil)
    (= :csv-file source) (assoc transport-service ::t-service/companies-csv-url nil)
    (= :csv-url source) (assoc transport-service ::t-service/companies {})
    :else transport-service)))

(defn- fetch-transport-service-external-interfaces [db id]
  (when id
    (fetch db ::t-service/external-interface-description
           #{::t-service/external-interface ::t-service/data-content ::t-service/format
             ::t-service/license ::t-service/id}
           {::t-service/transport-service-id id})))

(defn- set-publish-time
  [data db]
  (let [publish? (::t-service/published? data)
        service-id (::t-service/id data)
        existing-publish-date (when service-id
                                (::t-service/published (first (specql/fetch db ::t-service/transport-service
                                                                            #{::t-service/published}
                                                                            {::t-service/id service-id}))))
        publish-date (or existing-publish-date (java.util.Date.))
        data (cond-> data
                     publish? (assoc ::t-service/published publish-date)
                     true (dissoc ::t-service/published?))]
    data))

(defn- save-transport-service
  "UPSERT! given data to database. And convert possible float point values to bigdecimal"
  [nap-config db user {places ::t-service/operation-area
                       external-interfaces ::t-service/external-interfaces
                       service-company ::t-service/service-company
                       :as data}]
  ;(println "DATA: " (pr-str data))
  (let [service-info (-> data
                         (modification/with-modification-fields ::t-service/id user)
                         (dissoc ::t-service/operation-area)
                         floats-to-bigdec
                         (set-publish-time db)
                         (dissoc ::t-service/external-interfaces
                                 ::t-service/service-company)
                         (maybe-clear-companies))

        resources-from-db (fetch-transport-service-external-interfaces db (::t-service/id data))
        removed-resources (removable-resources resources-from-db external-interfaces)

        ;; Store to OTE database
        transport-service
        (jdbc/with-db-transaction [db db]
          (let [transport-service (upsert! db ::t-service/transport-service service-info)
                transport-service-id (::t-service/id transport-service)]

            ;; Save possible external interfaces
            (save-external-interfaces db transport-service-id external-interfaces removed-resources)

            ;; Save operation areas
            (places/save-transport-service-operation-area! db transport-service-id places)

            ;; Save companies
            (if (::t-service/companies-csv-url transport-service)
              ;; Update companies
              (save-external-companies db transport-service)
              ;; If url is empty, delete remaining data
              (delete-external-companies db transport-service))
            transport-service))]

    ;; Return the stored transport-service
    transport-service))

(defn- save-transport-service-handler
  "Process transport service save POST request. Checks that the transport operator id
  in the service to be stored is in the set of allowed operators for the user.
  If authorization check succeeds, the transport service is saved to the database and optionally
  published to CKAN."
  [nap-config db user request]
    (authorization/with-transport-operator-check
      db user (::t-service/transport-operator-id request)
      #(http/transit-response
        (save-transport-service nap-config db user request))))

(defn- save-associated-operator
  "Save association between transport service and transport operator"
  [db user-id service-id operator-id]
  (try
    (http/transit-response
      (specql/insert! db ::t-service/associated-service-operators
                      {::t-service/service-id service-id
                       ::t-service/operator-id operator-id
                       ::t-service/user-id user-id}))
    (catch Exception e
      (log/info (str "Association save failed with service: " service-id " and user: " user-id))
      {:status 409 :body "Forbidden"})))

(defn- delete-associated-operator
  "remove association between trasport service and transport operator"
  [db service-id operator-id]
  (try
    (http/transit-response (specql/delete! db ::t-service/associated-service-operators
                                           {::t-service/service-id service-id
                                            ::t-service/operator-id operator-id}))
    (catch Exception e
      (log/info (str "Delete association failed with service: " service-id " and operator: " operator-id))
      {:status 404 :body "Association not found"})))

(defn public-data-transport-service
  "Get single transport service by id"
  [db id]
  (let [ts (first (fetch db ::t-service/transport-service
                         (apply disj
                                (conj (specql/columns ::t-service/transport-service)
                                    ;; join external interfaces
                                    [::t-service/external-interfaces
                                     (specql/columns ::t-service/external-interface-description)])
                                transport-service-personal-columns)
                         {::t-service/id id}))]
    (if ts
      (http/transit-response
        (assoc ts ::t-service/operation-area
                  (places/fetch-transport-service-operation-area db id)))
      {:status 404})))


(defn private-data-transport-operator
  "Get single transport service by id"
  [db id]
  (let [to (first (fetch db ::t-operator/transport-operator
                         (specql/columns ::t-operator/transport-operator)
                         {::t-operator/id id}))]
    (if to
      (http/transit-response to)
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
      (http/transit-response to)
      {:status 404})))

(defn transport-operator-users
  [db operator-id]
  (http/transit-response (fetch-operator-users db {:operator-id operator-id})))

(defn add-user-to-operator [email db new-member requester operator]
  (let [member (create-member! db (:user_id new-member) (::t-operator/ckan-group-id operator))
        ;; Ensure that we do not send email to test user
        updated-member (if (= (:e2e-test-email (email-config/config)) (:user_email new-member))
               (assoc new-member :user_email (:e2e-test-amazon-simulator-email (email-config/config)))
               new-member)
        title (str "Sinut on kutsuttu " (:name operator) " -nimisen palveluntuottajan jäseneksi")
        auditlog {::auditlog/event-type :add-member-to-operator
                  ::auditlog/event-attributes
                  [{::auditlog/name "transport-operator-id", ::auditlog/value (str (::t-operator/id operator))},
                   {::auditlog/name "transport-operator-name", ::auditlog/value (str (::t-operator/name operator))}
                   {::auditlog/name "new-member-id", ::auditlog/value (str (:user_id updated-member))}
                   {::auditlog/name "new-member-email", ::auditlog/value (str (:user_email updated-member))}]
                  ::auditlog/event-timestamp (java.sql.Timestamp. (System/currentTimeMillis))
                  ::auditlog/created-by (get-in requester [:user :id])}]

    (do
      ;; Send email to user to inform about the new membership.
      (try
        (email/send!
          email
          {:to (:user_email updated-member)
           :subject title
           :body [{:type "text/html;charset=utf-8"
                   :content (str email-template/html-header
                                 (html (email-template/notify-user-new-member updated-member requester operator title)))}]})
        (catch Exception e
          (log/warn "Error while sending a email to new member" e)))

      ;; Create auditlog
      (specql/insert! db ::auditlog/auditlog auditlog)

      ;; Return added new-member data
      {:id (:user_id new-member)
       :username (:user_username new-member)
       :name (:user_name new-member)
       :email (:user_email new-member)})))

(defn invite-new-user [email-config db requester operator user-email]
  (let [expiration-date (time/sql-date (.plusDays (java.time.LocalDate/now) 1))
        token (UUID/randomUUID)
        inserted-token (specql/insert! db ::user/user-tokens
                                       {::user/user-email user-email
                                        ::user/token (str token)
                                        ::user/operator-id (::t-operator/id operator)
                                        ::user/expiration expiration-date
                                        ::user/requester-id (get-in requester [:user :id])})
        title (str "Sinut on kutsuttu " (:name operator) " -nimisen palveluntuottajan jäseneksi")]
    (try
      (email/send!
        email-config
        {:to user-email
         :subject title
         :body [{:type "text/html;charset=utf-8"
                 :content (html (email-template/new-user-invite requester operator title (::user/token inserted-token)))}]})
      (catch Exception e
        (log/warn (str "Error while inviting " user-email " ") e)))))

(defn manage-adding-users-to-operator [email db requester operator-id form-data]
  (let [new-member (first (fetch-user-by-email db {:email (:email form-data)}))
        operator (first (specql/fetch db ::t-operator/transport-operator
                                      #{::t-operator/id ::t-operator/name ::t-operator/ckan-group-id}
                                      {::t-operator/id operator-id}))
        operator-users (fetch-operator-users db {:operator-id operator-id})
        new-member-is-operator-member (some #(= (:user_id new-member) (:id %)) operator-users)
        ;; If member exists, add it to the organization if not, invite
        response (cond
                   ;; Existing new-member, existing operator
                   (and (not new-member-is-operator-member) (not (empty? new-member)) (not (empty? operator)))
                   (add-user-to-operator email db new-member requester operator)

                   ;; new new-member, existing operator -> invite new-member
                   (and (empty? new-member) (not (empty? operator)))
                   (invite-new-user email db requester operator (:email form-data))

                   ;; new-member is already a member
                   (and (not (empty? new-member)) new-member-is-operator-member)
                   {:error :already-member}

                   ;; Something wrong is happening
                   :else
                   {:error :something-went-wrong})]
    (if (not (:error response))
      (http/transit-response response 200)
      (http/transit-response response 400))))

(defn remove-member-from-operator [email db user operator-id member-email]
  (let [member (first (fetch-user-by-email db {:email member-email}))
        operator (first (specql/fetch db ::t-operator/transport-operator
                                      #{::t-operator/id ::t-operator/name ::t-operator/ckan-group-id}
                                      {::t-operator/id operator-id}))]
    (specql/delete! db ::user/member
                    {::user/table_id   (:user_id member)
                     ::user/group_id   (::t-operator/ckan-group-id operator)
                     ::user/table_name "user"})
    (http/transit-response "OK")))

(defn- transport-routes-auth
  "Routes that require authentication"
  [db config email]
  (let [nap-config (:nap config)]
    (routes

      (GET "/transport-operator/ensure-unique-business-id/:business-id" [business-id :as {user :user}]
        (http/transit-response
          (business-id-exists db business-id)))

      (POST "/transport-service/:service-id/associated-operators"
            {{:keys [service-id]} :params
             user :user
             data :body}
        (let [operator-id (:operator-id (http/transit-request data))
              user-id (get-in user [:user :id])
              service-id (Long/parseLong service-id)]
          (authorization/with-transport-operator-check db user operator-id
                                                       #(save-associated-operator db user-id service-id operator-id))))

      (DELETE "/transport-service/:service-id/associated-operators/:operator-id"
              {{:keys [service-id operator-id]}
               :params
               user :user}
        (let [service-id (Long/parseLong service-id)
              operator-id (Long/parseLong operator-id)]
          (authorization/with-transport-operator-check db user operator-id
                                                       #(delete-associated-operator db service-id operator-id))))

      (GET "/transport-operator/:operator-id/users"
           {{:keys [operator-id]}
            :params
            user :user}
        (let [op-id (Long/parseLong operator-id)]
          (authorization/with-transport-operator-check db user op-id
                                                       #(transport-operator-users db op-id))))

      (POST "/transport-operator/:operator-id/users"
            {{:keys [operator-id]}
             :params
             user :user
             form-data :body}
            (authorization/with-transport-operator-check
              db user (Long/parseLong operator-id)
              #(manage-adding-users-to-operator email db user (Long/parseLong operator-id)
                                                (http/transit-request form-data))))

      ;; This is not ready, only implemented to help add-member implementation
      (DELETE "/transport-operator/:operator-id/users/:member-email"
            {{:keys [operator-id member-email]}
             :params
             user :user}
            (authorization/with-transport-operator-check
              db user (Long/parseLong operator-id)
              #(remove-member-from-operator email db user (Long/parseLong operator-id) member-email)))

      (POST "/transport-operator/group" {user :user}
        (http/transit-response
          (get-transport-operator db {::t-operator/ckan-group-id (get (-> user :groups first) :id)})))

      (POST "/transport-operator/data" {user :user}
        (http/transit-response
          (get-user-transport-operators-with-services db (:groups user) (:user user))))

      (POST "/transport-operator" {form-data :body
                                   user      :user}
        (http/transit-response
          (save-transport-operator config db user
                                   (http/transit-request form-data))))

      (POST "/transport-service" {form-data :body
                                  user      :user}
        (save-transport-service-handler nap-config db user (http/transit-request form-data)))

      (POST "/transport-service/delete" {form-data :body
                                         user      :user}
        (http/transit-response
          (delete-transport-service! nap-config db user
                                     (:id (http/transit-request form-data)))))

      (POST "/transport-operator/delete" {form-data :body
                                          user      :user}
        (http/transit-response
          (delete-transport-operator! nap-config db user
                                      (:id (http/transit-request form-data))))))))

(defn- transport-routes
  "Unauthenticated routes"
  [db config]
  (routes
    (GET "/transport-operator/:ckan-group-id" [ckan-group-id]
      (http/transit-response
        (get-transport-operator db {::t-operator/ckan-group-id ckan-group-id})))

    (GET "/transport-service/:id"
      {{:keys [id]}
       :params
       user :user}
      (let [id (Long/parseLong id)
            operator-id (::t-service/transport-operator-id (first (specql/fetch db ::t-service/transport-service
                                              #{::t-service/transport-operator-id}
                                              {::t-service/id id})))]
        (if (or (authorization/admin? user) (authorization/is-author? db user operator-id))
          (all-data-transport-service db id)
          (public-data-transport-service db id))))

    (GET "/t-operator/:id"
         {{:keys [id]}
          :params
          user :user}
      (let [id (Long/parseLong id)]
        (if (or (authorization/admin? user) (authorization/is-author? db user id))
          (private-data-transport-operator db id)
          (public-data-transport-operator db id))))))

(defrecord Transport [config]
  component/Lifecycle
  (start [{:keys [db http email] :as this}]
    (assoc
      this ::stop
      [(http/publish! http (transport-routes-auth db config email))
       (http/publish! http {:authenticated? false} (transport-routes db config))]))
  (stop [{stop ::stop :as this}]
    (doseq [s stop]
      (s))
    (dissoc this ::stop)))
