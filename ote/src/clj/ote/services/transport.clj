(ns ote.services.transport
  "Services for getting transport data from database"
  (:require [amazonica.aws.s3 :as s3]
            [clojure.java.io :as io]
            [ring.util.io :as ring-io]
            [com.stuartsierra.component :as component]
            [compojure.core :refer [routes GET POST DELETE]]
            [taoensso.timbre :as log]
            [clojure.java.jdbc :as jdbc]
            [clojure.set :as set]
            [jeesql.core :refer [defqueries]]
            [specql.core :refer [fetch upsert! delete!] :as specql]
            [specql.op :as op]
            [ote.components.http :as http]
            [ote.util.feature :as feature]
            [ote.db.modification :as modification]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]
            [ote.db.rental-booking-service :as rental-booking]
            [ote.db.stats :as stats]
            [ote.util.db :as util]
            [ote.authorization :as authorization]
            [ote.netex.netex_util :as netex-util]
            [ote.services.places :as places]
            [ote.services.external :as external]
            [ote.db.tx :as tx]
            ;[ote.util.file :as file] - CSV s3 copy is not used currently
            ))

(defqueries "ote/services/places.sql")
(defqueries "ote/services/transport.sql")
(defqueries "ote/services/operators.sql")
(defqueries "ote/services/associations.sql")
(defqueries "ote/nap/users.sql")

(def transport-service-personal-columns
  #{::t-service/contact-phone
    ::t-service/contact-gsm
    ::t-service/contact-email})

(def transport-services-column-keys
  {:id ::t-service/id
   :transport-operator-id ::t-service/transport-operator-id
   :name ::t-service/name
   :transport-type ::t-service/transport-type
   :interface-types ::t-service/interface-types
   :type ::t-service/type
   :sub-type ::t-service/sub-type
   :parent-id ::t-service/parent-id
   :has-child? ::t-service/has-child?
   :published ::t-service/published
   :validate ::t-service/validate
   :re-edit ::t-service/re-edit
   :created ::modification/created
   :modified ::modification/modified})

(defn- maybe-delete-company-csv-from-s3
  "When e.g. service is deleted files tend to stay at s3 for nothing. So delete them if they exists."
  [db bucket service-id]
  ;; Company csv s3 copy is stopped for now
  false
  #_ (let [db-csv-row (first (specql/fetch db ::t-service/transport-service-company-csv
                                        #{::t-service/id ::t-service/csv-file-name ::t-service/file-key}
                                        {::t-service/transport-service-id service-id}))]
    (when db-csv-row
      (specql/delete! db ::t-service/transport-service-company-csv
                      {::t-service/transport-service-id service-id})
      (s3/delete-object bucket (::t-service/file-key db-csv-row)))))

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

(defn all-service-data [db id]
  (let [ts (first (fetch db ::t-service/transport-service
                         (conj (specql/columns ::t-service/transport-service)
                               ;; join external interfaces
                               [::t-service/external-interfaces
                                (specql/columns ::t-service/external-interface-description)])
                         {::t-service/id id}))

        company-csv-file (first (fetch db ::t-service/transport-service-company-csv
                                       (specql/columns ::t-service/transport-service-company-csv)
                                       {::t-service/transport-service-id (::t-service/id ts)}))
        ts (if company-csv-file
             (assoc ts :db-file-key (::t-service/file-key company-csv-file)
                       :csv-imported? true
                       :csv-valid? (or (= (::t-service/failed-companies-count company-csv-file) 0) false)
                       :csv-failed-companies-count (::t-service/failed-companies-count company-csv-file)
                       :csv-valid-companies-count (::t-service/valid-companies-count company-csv-file))
             ts)]
    ts))

(defn- public-service-data [db id]
  (first (fetch db ::t-service/transport-service
                (apply disj
                       (conj (specql/columns ::t-service/transport-service)
                             ;; join external interfaces
                             [::t-service/external-interfaces
                              (specql/columns ::t-service/external-interface-description)])
                       transport-service-personal-columns)
                {::t-service/id id})))

(defn fetch-or-create-rental-booking-info
  [db service]
  (let [{:keys [application-link phone-countrycode phone-number]} (first (fetch-rental-booking-info db {:service-id (::t-service/id service)}))]
    {::rental-booking/application-link  application-link
     ::rental-booking/phone-countrycode phone-countrycode
     ::rental-booking/phone-number      phone-number}))

(defn type-specific-booking-data
  [services config db]
  (mapv
    (fn [service]
      (if (and (= :passenger-transportation (::t-service/type service))
               (contains? #{:taxi :rental} (::t-service/sub-type service)))
        (assoc-in service
                  [::t-service/passenger-transportation ::rental-booking/rental-booking-info]
                  (fetch-or-create-rental-booking-info db service))
        service))
    services))



(defn all-data-transport-service
  "Get single transport service by id"
  [config db id]
  (let [ts (all-service-data db id)]
    (if ts
      (http/no-cache-transit-response
        (-> (assoc ts ::t-service/operation-area
                      (places/fetch-transport-service-operation-area db id))
            vector
            (netex-util/append-ote-netex-urls config db ::t-service/external-interfaces)
            (type-specific-booking-data config db)
            first)
        200)
      {:status 404})))

(defn delete-transport-service!
  "Delete child and parent transport services by id"
  [db config user service-id]
  (let [bucket (get-in config [:csv :bucket])
        service (first (specql/fetch db ::t-service/transport-service
                                     #{::t-service/transport-operator-id ::t-service/parent-id}
                                     {::t-service/id service-id}))
        transport-operator-id (::t-service/transport-operator-id service)
        parent-id (::t-service/parent-id service)]
    (authorization/with-transport-operator-check
      db user transport-operator-id
      #(do
         (when parent-id
           ;; Delete parent company csv files
           (maybe-delete-company-csv-from-s3 db bucket parent-id)

           ;; Delete parent
           (delete! db ::t-service/transport-service {::t-service/id parent-id}))
         ;; Delete service company csv files
         (maybe-delete-company-csv-from-s3 db bucket service-id)

         ;; All stats-service rows must also be deleted when service is deleted. We do not want to leave anything behind
         (delete! db ::stats/stats-service {::stats/transport-service-id service-id})

         ;; Delete service
         (delete! db ::t-service/transport-service {::t-service/id service-id})
         service-id))))

(defn ensure-bigdec [value]
  (when-not (nil? value) (bigdec value)))

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
               (mapv #(fix-price-classes % [::t-service/price-classes])
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

(defn- save-external-interfaces
  "Save external interfaces for a transport service"
  [db transport-service-id external-interfaces removable-interfaces]
  (let [external-interfaces (mapv #(dissoc % :url-ote-netex) ; Remove because not part of db data model
                                  external-interfaces)]

    ;; Delete removed services from OTE db
    (doseq [{id ::t-service/id} removable-interfaces]
      ;; Delete from external-interface-description
      ;; We do not delete packages that might be downloaded from this interface
      ;; We also do not delete interface download history. It should be available in transit visualization
      (specql/delete! db ::t-service/external-interface-description
                      {::t-service/id id}))

    (doseq [{id ::t-service/id :as ext-if} external-interfaces]
      (if id
        (do
          (specql/update! db ::t-service/external-interface-description
                          ext-if
                          {::t-service/transport-service-id transport-service-id
                           ::t-service/id id}))
        (do
          (specql/insert! db ::t-service/external-interface-description
                          (assoc ext-if ::t-service/transport-service-id transport-service-id)))))))

(defn- removable-resources
  [from-db from-client]
  (let [from-ui (into #{} (map ::t-service/id) from-client)
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
                   {::t-service/companies companies
                    ::t-service/transport-service-id (::t-service/id transport-service)
                    ::t-service/source "URL"}
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

(defn- update-child-parent-download-status [db parent-id child-id]
  (let [child-interface-id-list (fetch-child-service-interfaces db {:service-id child-id})
       ;; Update download-history interface-id:s to new ids.
        id-list (doall
                  (mapv
                    (fn [row]
                      (let [url (:url row)
                            orig-interface-id (:original-interface-id row)
                            ;; Update download history
                            _ (specql/update! db ::t-service/external-interface-download-status
                                              {::t-service/external-interface-description-id (:id row)}
                                              {::t-service/transport-service-id parent-id
                                               ::t-service/url url})
                            ;; Update packages
                            packages (specql/fetch db :gtfs/package (specql/columns :gtfs/package)
                                                   {:gtfs/transport-service-id parent-id
                                                    :gtfs/external-interface-description-id orig-interface-id})
                            _ (specql/update! db :gtfs/package
                                              {:gtfs/external-interface-description-id (:id row)}
                                              {:gtfs/transport-service-id parent-id
                                               :gtfs/external-interface-description-id orig-interface-id})]
                        (:id row)))
                    child-interface-id-list))
        ids (mapv #(:id %) child-interface-id-list)
        ;; When updating old interfaces, this will change interface-id's correctly tot package_gtfs table
        ;; However when interfaces are deleted and created again (users can "edit" their interfaces by deleting and creating them as a new row
        ;; This won't update interface-id:s correctly.
        _ (update-old-package-interface-ids! db {:new-interface-id (first id-list) ;; It doesnt matter to which interface those old packages point, because the history is stored in history table and the
                                                 ;; interface-id is critical for change detection. Change detection gets unique packages based on interface id. If we don't rewrite those we get corrupted change detection results
                                                 :service-id parent-id
                                                 :ids ids})
        ;; So we need to ensure that those deleted interfaces are marked as deleted so that
        ;; change-detection can skip packages that are donwloaded from deleted interface
        _ (update-packages-with-deleted-interface-true! db {:service-id parent-id})]))

(defn replace-parent-service-with-child [db bucket child-id publish?]
  (let [;; Get child service data
        service (first (specql/fetch db ::t-service/transport-service
                                     (specql/columns ::t-service/transport-service)
                                     {::t-service/id child-id}))
        parent-id (::t-service/parent-id service)
        ;; Replace service id with parent-id and update data -> original published service will be overwritten
        service (-> service
                    (assoc ::t-service/id parent-id)
                    (assoc ::t-service/validate nil)
                    (dissoc ::t-service/parent-id)
                    (dissoc ::t-service/validate?)
                    (assoc ::t-service/published (when publish?
                                                   (java.util.Date.)))
                    (assoc ::t-service/re-edit nil))]
    (tx/with-transaction
      db
      ;; Update service data in database
      (update-child-parent-download-status db parent-id child-id)
      (specql/upsert! db ::t-service/transport-service service)
      (update-child-parent-interfaces db {:parent-id parent-id
                                          :child-id child-id})

      ;; Delete parents operation areas
      (specql/delete! db ::t-service/operation_area {::t-service/transport-service-id parent-id})

      ;; Convert child's operation areas to parents
      (specql/update! db ::t-service/operation_area
                      {::t-service/transport-service-id parent-id}
                      {::t-service/transport-service-id child-id})

      ;; Maybe delete company-csv from S3
      (maybe-delete-company-csv-from-s3 db bucket child-id)

      ;; Delete child - and its external-interfaces and places
      (specql/delete! db ::t-service/transport-service {::t-service/id child-id}))))

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
                     (not publish?) (assoc ::t-service/published nil)
                     true (dissoc ::t-service/published?)
                     ;; Ensure that state is publish in every case after saving
                     true (assoc ::t-service/validate nil)
                     true (assoc ::t-service/re-edit nil))]
    data))

(defn set-validate-time
  "Add validate datetime when it is moved to validation. If not, remove validate? key."
  [data]
  (cond
    ;; Saving and moving service to validation
    (::t-service/validate? data)
    (let [;; If service already published, remove id, set old id as parent-id and let the save duplicate service data
          service-id (::t-service/id data)
          data (if (::t-service/published data)
                 (-> data
                     (dissoc ::t-service/id)                ;; When creating a copy id must be removed
                     (dissoc ::t-service/published)         ;; When creating a copy the copy is not published
                     (assoc ::t-service/parent-id service-id))
                 data)]
      ;; Move to validation
      (-> data
          (dissoc ::t-service/validate?)
          (assoc ::t-service/validate (java.util.Date.)
                 ::t-service/re-edit nil)))

    ;; Save as draft
    :else
    (-> data
        (dissoc ::t-service/validate?)
        (assoc ::t-service/published nil)
        (assoc ::t-service/re-edit nil))))

(defn service-has-child?
  "When child service has parent-id pointing to given service it has child.
  Ensure that service has id before calling this fn."
  [db transport-service]
  (or
    (first (specql/fetch db ::t-service/transport-service
                         #{::t-service/id}
                         {::t-service/parent-id (::t-service/id transport-service)}))
    nil))

(defn- maybe-copy-service-company-csv [db bucket transport-service-id original-service-id]
  ; XXX: This function is a no-op since the file-key started conflicting effectively preventing upload of new versions
  ;      of the CSV. As this feature is quite complex to remove properly, it is left as is.
  ;      For the history books, this CSV file-key tracking is originally for the functionality which copies the CSV to
  ;      S3 bucket.
  nil
  #_(let [original-csv-from-db (first (specql/fetch db ::t-service/transport-service-company-csv
                                                  (specql/columns ::t-service/transport-service-company-csv)
                                                  {::t-service/transport-service-id original-service-id}))
        ;; original-csv-from-s3 (when original-csv-from-db (s3/get-object bucket (::t-service/file-key original-csv-from-db))) - Stop copying csv to s3
        ;; new-file-key (when original-csv-from-db (file/generate-s3-csv-key (::t-service/csv-file-name original-csv-from-db))) - Stop copying csv to s3
        new-csv-from-db (when original-csv-from-db
                          (specql/insert! db ::t-service/transport-service-company-csv
                                          (-> original-csv-from-db
                                              (dissoc ::t-service/id)
                                              (assoc ::t-service/transport-service-id transport-service-id
                                                     ;::t-service/file-key new-file-key  - Stop copying csv to s3
                                                     ))))
        ;;temp-file (when original-csv-from-s3 (java.io.File/createTempFile "csv-tmp" ".csv"))
        ;;_ (when original-csv-from-s3 (io/copy (:input-stream original-csv-from-s3) temp-file))
        ;;new-to-s3 (when new-csv-from-db (s3/put-object bucket new-file-key temp-file))
        ]
    nil))

(defn- save-service-company-csv
  "New Service company csv's are stored in temp table. And if they are they need to stored to more permanent place."
  [db transport-service-id filename bucket]
  (let [temp-csv-row (first (specql/fetch db ::t-service/transport-service-company-csv-temp
                                          (specql/columns ::t-service/transport-service-company-csv-temp)
                                          {::t-service/transport-service-id transport-service-id}))
        temp-csv-row (if (or (nil? temp-csv-row) (empty? temp-csv-row))
                       ;; When service is first saved transport-service-company-csv-temp table doesn't contain
                       ;; service-id, because there were non when it was added to the temp table.
                       ;; So find the latest ro with the same file name and assume that it is correct.
                       (last (specql/fetch db ::t-service/transport-service-company-csv-temp
                                           (specql/columns ::t-service/transport-service-company-csv-temp)
                                           {::t-service/csv-file-name filename}))
                       ;; Return it if it exists
                       temp-csv-row)
        ;; Service may have company csv in permanent table. If so, delete it
        permanent-csv (first (specql/fetch db ::t-service/transport-service-company-csv
                                           (specql/columns ::t-service/transport-service-company-csv)
                                           {::t-service/transport-service-id transport-service-id}))
        _ (when permanent-csv
            (maybe-delete-company-csv-from-s3 db bucket transport-service-id))]
    (when temp-csv-row
      ;; Save to permanent table
      (specql/insert! db ::t-service/transport-service-company-csv (dissoc temp-csv-row ::t-service/id))
      ;; Delete from temp table
      (specql/delete! db ::t-service/transport-service-company-csv-temp
                      {::t-service/file-key (::t-service/file-key temp-csv-row)}))))

(defn- ensure-created-time
  "If service is a child then its created timestamp must be always parents created time. No exceptions here."
  [service db]
  (let [created (if (::t-service/parent-id service)
                  (::modification/created (public-service-data db (::t-service/parent-id service)))
                  (::modification/created service))]
    (assoc service ::modification/created created)))

(defn- save-rental-booking-info
  [db transport-service-id rental-booking]
  (let [{::rental-booking/keys [application-link phone-countrycode phone-number]} rental-booking]
    (save-rental-booking-info! db {:transport-service-id transport-service-id
                                   :application-link     application-link
                                   :phone-countrycode    phone-countrycode
                                   :phone-number         phone-number})))

(defn- save-transport-service
  "UPSERT! given data to database. And convert possible float point values to bigdecimal"
  [config db user {places              ::t-service/operation-area
                   external-interfaces ::t-service/external-interfaces
                   :as data}]
  ;; When validation flag is enabled we cannot let users to update service data if it has child service in validation.
  ;; So we need to check if validation flag is enabled and if parent-id is set for some other child service
  (let [user-id (get-in user [:user :id])
        now (java.sql.Timestamp. (System/currentTimeMillis))
        has-child? (if (and
                         (feature/feature-enabled? config :service-validation)
                         (::t-service/id data))
                     (service-has-child? db data)
                     false)]

    (if has-child?
      data
      (let [;; Figure out if service is a child. Because if child is saved as a draft the parent will also get changed
            is-child? (boolean (::t-service/parent-id data))
            original-service-id (::t-service/id data)
            ;; Validation is enabled via flag. Publish services if validation is not enabled
            data (if (feature/feature-enabled? config :service-validation)
                   (set-validate-time data)
                   (set-publish-time data db))
            ;; Force modification timestamp for child - even when admin modifies data, user-id will be saved
            data (if (::t-service/parent-id data)
                   (assoc data
                     ::modification/modified now
                     ::modification/modified-by user-id)
                   data)
            service-info (-> data
                             ;; Modification timestamp is set only if service is changed as a draft. When creating other changes
                             ;; validation process prevents modified timestamp to be set
                             (modification/with-modification-fields ::t-service/id user)
                             (dissoc ::t-service/operation-area)
                             floats-to-bigdec
                             (dissoc ::t-service/external-interfaces
                                     ::t-service/service-company)
                             (maybe-clear-companies)
                             (ensure-created-time db))
            interfaces-from-db (fetch-transport-service-external-interfaces db original-service-id)
            removable-interfaces (removable-resources interfaces-from-db external-interfaces)
            csv-file-key (:db-file-key service-info)        ;; Take csv id from temp table
            service-info (dissoc service-info :db-file-key) ;; Remove csv id to make save to work
            ;; Store to OTE database
            transport-service
            (jdbc/with-db-transaction
              [db db]
              (let [transport-service (upsert! db ::t-service/transport-service service-info)
                    transport-service-id (::t-service/id transport-service)
                    rental-booking       (some-> transport-service ::t-service/passenger-transportation ::rental-booking/rental-booking-info)
                    ;; Update transport-service-id only when saving service for the first time
                    _ (when (and csv-file-key (nil? original-service-id))
                        (specql/update! db ::t-service/transport-service-company-csv-temp
                                        {::t-service/transport-service-id transport-service-id}
                                        {::t-service/file-key csv-file-key}))]
                ;; Copy possible company csv file to child service if needed
                (if (and (not (nil? original-service-id))
                         (not= original-service-id transport-service-id)) ;; child id is given
                  (do
                    (save-service-company-csv db original-service-id (::t-service/company-csv-filename service-info) (get-in config [:csv :bucket]))
                    ; XXX: see comment in the function to see why this is commented out
                    #_(maybe-copy-service-company-csv db (get-in config [:csv :bucket]) transport-service-id original-service-id))
                  (save-service-company-csv db transport-service-id (::t-service/company-csv-filename service-info) (get-in config [:csv :bucket])))
                ;; Save possible external interfaces
                (if (and (not (nil? original-service-id))
                         (not= original-service-id transport-service-id))
                  ;; Copy existing interfaces to new service
                  (save-external-interfaces
                    db transport-service-id (map
                                              (fn [interface]
                                                (-> interface
                                                    (assoc ::t-service/transport-service-id transport-service-id)
                                                    ;; Save original id
                                                    (assoc ::t-service/original-interface-id (::t-service/id interface))
                                                    ;; Remove id so it can be saved to new service with different id
                                                    (dissoc ::t-service/id)))
                                              external-interfaces)
                    nil)
                  (save-external-interfaces db transport-service-id external-interfaces removable-interfaces))

                ;; Save operation areas for duplicates and original services
                (if (and (not (nil? original-service-id))
                         (not= original-service-id transport-service-id))
                  ;; We need to duplicate those original places and add new ones if user has decided to do changes to them
                  (places/duplicate-operation-area db original-service-id transport-service-id places)
                  ;; Save given places
                  (places/save-transport-service-operation-area! db transport-service-id places true))

                ;; Save companies
                (if (::t-service/companies-csv-url transport-service)
                  ;; Update companies
                  (save-external-companies db transport-service)
                  ;; If url is empty, delete remaining data
                  (delete-external-companies db transport-service))

                ;; save rental service booking data if present
                (when (not (nil? rental-booking))
                  (save-rental-booking-info db transport-service-id rental-booking))
                transport-service))]

        ;; if service is a child and if it is saved as a draft then copy data to parent
        (when (and
                is-child?
                (nil? (::t-service/validate data)))
          (replace-parent-service-with-child db (get-in config [:csv :bucket]) (::t-service/id data) false))

        ;; Return the stored transport-service
        transport-service))))

(defn- re-edit-service
  "Services can be changed to re-edit state when they are in validation but admin haven't yet
  validated them."
  [db user service-id]
  (let [service-operator-id (::t-service/transport-operator-id (first (specql/fetch db
                                                                                    ::t-service/transport-service
                                                                                    #{::t-service/transport-operator-id}
                                                                                    {::t-service/id service-id})))
        current-timestamp (java.sql.Timestamp. (System/currentTimeMillis))]
    (authorization/with-transport-operator-check
      db user service-operator-id
      #(do
         (specql/update! db ::t-service/transport-service
                         {::t-service/re-edit current-timestamp
                          ::t-service/validate nil}
                         {::t-service/id service-id})
         current-timestamp))))

(defn service-operator-id [db service-id]
  (::t-service/transport-operator-id (first (specql/fetch db
                                                          ::t-service/transport-service
                                                          #{::t-service/transport-operator-id}
                                                          {::t-service/id service-id}))))

(defn back-to-validation
  "User has possibility to take service back to editing state when it is changed to re-edit state. Also
  if user doesn't make any changes in 24 hours service will be changed back to validation to ensure
  that services that are edited will be published and not leave as drafts."
  [db service-id]
  (let [current-timestamp (java.sql.Timestamp. (System/currentTimeMillis))]
    (do
      (specql/update! db ::t-service/transport-service
                      {::t-service/re-edit nil
                       ::t-service/validate current-timestamp}
                      {::t-service/id service-id})
      current-timestamp)))

(defn- save-transport-service-handler
  "Process transport service save POST request. Checks that the transport operator id
  in the service to be stored is in the set of allowed operators for the user.
  If authorization check succeeds, the transport service is saved to the database."
  [config db user request]
  (authorization/with-transport-operator-check
    db user (::t-service/transport-operator-id request)
    #(http/transit-response
       (save-transport-service config db user request))))

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
  [config db id]
  (let [ts (public-service-data db id)]
    (if ts
      (http/no-cache-transit-response
        (-> (assoc ts ::t-service/operation-area
                      (places/fetch-transport-service-operation-area db id))
            vector
            (netex-util/append-ote-netex-urls config db ::t-service/external-interfaces)
            (type-specific-booking-data config db)
            first))
      {:status 404})))

(defn return-company-csv-by-id
  "CSV file can be returned to the user even if service is not saved to database. So we cannot rely on service id to check
  if user has authorization to download it. Check the operator id and authorization if possible and use created-by if not."
  [db config user file-key]
  (let [bucket (get-in config [:csv :bucket])
        csv-row (first (specql/fetch db ::t-service/transport-service-company-csv-temp
                                     (specql/columns ::t-service/transport-service-company-csv-temp)
                                     {::t-service/file-key file-key}))
        csv-row (if csv-row
                  csv-row
                  (first (specql/fetch db ::t-service/transport-service-company-csv
                                       (specql/columns ::t-service/transport-service-company-csv)
                                       {::t-service/file-key file-key})))
        transport-operator-id (when (not (nil? (::t-service/transport-service-id csv-row)))
                                (::t-service/transport-operator-id (first (specql/fetch db ::t-service/transport-service
                                                                                        #{::t-service/transport-operator-id}
                                                                                        {::t-service/id (::t-service/transport-service-id csv-row)}))))
        filename (::t-service/csv-file-name csv-row)
        s3-file (when (or
                        (= (:id (:user user)) (::t-service/created-by csv-row))
                        (authorization/is-author? db user transport-operator-id))
                  (s3/get-object bucket (::t-service/file-key csv-row)))]

    ;; Change default headers and put file as a stream to body
    ;; to enable downloading a file and not html plain text
    (if-not s3-file
      {:status 404
       :body "No such file."}
      {:status 200
       :headers {"Content-Type" "text/csv; charset=UTF-8"
                 "Content-Disposition" (str "attachment;" (when filename (str " filename=" filename)))}
       :body (ring-io/piped-input-stream
               (fn [out]
                 (io/copy (:input-stream s3-file) out)))})))

(defn- delete-company-csv
  "Company csv can be in temp table and if it is we can only check for authentication if the user is the same as the
  temp table has. If csv is already in permanent table, then we can check authentication normally."
  [config db user form-data]
  (let [file-key (:file-key form-data)
        bucket (get-in config [:csv :bucket])
        temp-csv (first (specql/fetch db ::t-service/transport-service-company-csv-temp
                                      (specql/columns ::t-service/transport-service-company-csv-temp)
                                      {::t-service/file-key file-key}))
        permanent-csv (first (specql/fetch db ::t-service/transport-service-company-csv
                                           (specql/columns ::t-service/transport-service-company-csv)
                                           {::t-service/file-key file-key}))
        operator-id (when permanent-csv
                      (::t-service/transport-operator-id
                        (first (specql/fetch db ::t-service/transport-service
                                             #{::t-service/transport-operator-id}
                                             {::t-service/id (::t-service/transport-service-id permanent-csv)}))))]
    (if temp-csv
      ;; Authenticate and delete temp-csv
      (when (= (:id (:user user)) (::modification/created-by temp-csv))
        ;; Same user, we can delete temp-csv
        ;; S3 bucket usage is not in use currently
        ;;(s3/delete-object bucket (::t-service/file-key temp-csv))
        (specql/delete! db ::t-service/transport-service-company-csv-temp
                        {::t-service/file-key file-key})
        ;; Return
        file-key)
      (when (and permanent-csv operator-id)
        (authorization/with-transport-operator-check
          db user operator-id
          #(do
             ;; S3 bucket usage is not in use currently
             ;;(s3/delete-object bucket (::t-service/file-key permanent-csv))
             (specql/delete! db ::t-service/transport-service-company-csv
                             {::t-service/file-key file-key})
             ;; Return
             file-key))))))

(defn ckan-group-id->group
  [db ckan-group-id]
  (first (specql/fetch db ::t-operator/group
                       (specql/columns ::t-operator/group)
                       {::t-operator/group-id ckan-group-id})))

(defn- transport-service-routes-auth
  "Routes that require authentication"
  [db config]
  (routes

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

    (POST "/transport-service" {form-data :body
                                user :user}
      (save-transport-service-handler config db user (http/transit-request form-data)))

    ^{:format :csv}
    (GET "/transport-service/company-csv/:file-key"
         {{:keys [file-key]}
          :params
          user :user}
      (return-company-csv-by-id db config user file-key))

    (DELETE "/transport-service/delete-csv"
            {{:keys [file-key]}
             :params
             user :user
             form-data :body}
      (let [form-data (http/transit-request form-data)]
        (http/transit-response
          (delete-company-csv config db user form-data))))

    (POST "/transport-service/delete" {form-data :body
                                       user :user}
      (http/transit-response
        (delete-transport-service! db config user (:id (http/transit-request form-data)))))

    (POST "/transport-service/:service-id/re-edit-service" {{:keys [service-id]} :params
                                                            user :user}
      (let [service-id (Long/parseLong service-id)]
        (http/transit-response (re-edit-service db user service-id))))

    (POST "/transport-service/:service-id/back-to-validation" {{:keys [service-id]} :params
                                                               user :user}
      (let [service-id (Long/parseLong service-id)
            operator-id (service-operator-id db service-id)]
        (http/transit-response
          (authorization/with-transport-operator-check
            db user operator-id
            #(back-to-validation db service-id)))))))

(defn- transport-service-routes
  "Unauthenticated routes"
  [db config]
  (routes
    (GET "/transport-service/:id"
         {{:keys [id]}
          :params
          user :user
          cookies :cookies}
      (let [id (Long/parseLong id)
            operator-id (::t-service/transport-operator-id (first (specql/fetch db ::t-service/transport-service
                                                                                #{::t-service/transport-operator-id}
                                                                                {::t-service/id id})))]
        (if (or (authorization/admin? user) (authorization/is-author? db user operator-id))
          (all-data-transport-service config db id)
          (public-data-transport-service config db id))))))

(defrecord TransportService [config]
  component/Lifecycle
  (start [{:keys [db http] :as this}]
    (assoc
      this ::stop
           [(http/publish! http (transport-service-routes-auth db config))
            (http/publish! http {:authenticated? false} (transport-service-routes db config))]))
  (stop [{stop ::stop :as this}]
    (doseq [s stop]
      (s))
    (dissoc this ::stop)))
